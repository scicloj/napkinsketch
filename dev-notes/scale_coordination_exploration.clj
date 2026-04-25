;; # Scale-coordination bug -- diversity exploration
;;
;; A dev-notes scratch file used to enumerate the variants of the
;; SPLOM-diagonal-histogram scale-coordination bug while designing
;; the fix shipped 2026-04-25 (commit 9133a6b). Lives in dev-notes
;; rather than notebooks/ because it's an exploration / testing
;; tool, not part of the published book. Render via:
;;
;;   (require 'scicloj.clay.v2.api)
;;   (scicloj.clay.v2.api/make!
;;     {:format [:html]
;;      :base-source-path "dev-notes"
;;      :source-path "scale_coordination_exploration.clj"
;;      :base-target-path "/tmp/scale-explore-out"
;;      :show false})
;;
;; Three design questions the exploration was structured around:
;;
;; * Q1 -- where does the per-cell-scale exemption live, and which
;;   stats trigger it?
;; * Q2 -- what about leaves whose layers mix count-axis and
;;   data-axis stats?
;; * Q3 -- does the compositor's representative-leaf-for-legend
;;   pick a count-axis cell and produce a wrong legend?
;;
;; Background reading:
;; `dev-notes/regression-corpus/notes/diagonal-histogram-scale.md`.

(ns scale-coordination-exploration
  (:require [scicloj.plotje.api :as pj]
            [scicloj.plotje.impl.frame :as ifr]
            [scicloj.kindly.v4.kind :as kind]
            [tablecloth.api :as tc]))

;; ## The bug, in one paragraph
;;
;; A SPLOM is built as `(pj/frame data (pj/cross cols cols))`.
;; The composite root carries `:share-scales #{:x :y}`.
;; `frame/inject-shared-scales` computes one [lo hi] domain per
;; (axis, effective-column) bucket from data values across the
;; bucket's leaves, and stamps it onto each leaf as
;; `:x-scale-domain` / `:y-scale-domain`. Per-cell inference picks
;; `:bar :bin` (histogram) on the diagonal cells where x equals y.
;; The histogram's true y-axis is *count*, but the cell still gets
;; stamped with the data column's [lo hi] from the shared bucket.
;; Bars are drawn against the wrong y-domain -- usually clipped or
;; invisible.

;; ## Q1 -- which stats trigger it?
;;
;; The bug fires whenever a leaf's y-axis is *stat-driven* (count,
;; density) but the leaf's `:y` mapping points at a data column
;; that participates in shared scaling. Concretely:
;;
;; * `:bin`     histogram          -- numeric x leads to count y
;; * `:count`   categorical bar    -- categorical x leads to count y
;; * `:density` 1D KDE             -- numeric x leads to density y
;;
;; And it does NOT fire for stats whose y is data-driven:
;;
;; * `:identity`   scatter / line
;; * `:bin2d`      heatmap (count goes to fill, not y)
;; * `:density-2d` heatmap KDE
;; * `:smooth`     regression line (y is fitted data)
;; * `:summary`    point-range (y is summary statistic of y data)
;; * `:boxplot`    (y quartiles are over y data)

;; ### Variant 1a -- `:bin` on a numeric SPLOM diagonal (canonical)
;;
;; Three numeric columns sl/sw/pl with very different ranges
;; (sl in [4.6, 7.1], sw in [3.0, 3.6], pl in [1.3, 5.5]).
;; Diagonal histograms are bucketed with their column's data range,
;; and counts (max around 5) get squashed into [3.0, 3.6] etc.
;; Bars are visually clipped or invisible on every diagonal.

(def iris-toy
  (tc/dataset {:sl [5.1 4.9 4.7 4.6 5.0 6.0 6.4 6.9 7.1 6.5]
               :sw [3.5 3.0 3.2 3.1 3.6 3.4 3.2 3.1 3.0 3.0]
               :pl [1.4 1.4 1.3 1.5 1.4 4.5 4.5 4.9 5.1 5.5]
               :species ["a" "a" "a" "a" "a" "b" "b" "b" "c" "c"]}))

(-> iris-toy
    (pj/frame {:color :species})
    (pj/frame (pj/cross [:sl :sw :pl] [:sl :sw :pl])))

;; ### Variant 1b -- `:count` on a categorical SPLOM diagonal
;;
;; Three categorical columns. Per-cell inference picks
;; `{:mark :rect :stat :count}` on the diagonal.
;;
;; Hypothesis: the bug does NOT fire here because
;; `numeric-domain` filters the bucket's values to numbers and
;; returns nil for string columns. Diagonal cells thus never get
;; a y-domain stamp and are free to use the count axis.

(def cat-data
  (tc/dataset {:size  ["S" "S" "M" "L" "M" "S" "L" "L" "M" "S"]
               :color ["red" "blue" "red" "red" "blue" "blue" "red" "blue" "red" "blue"]
               :grade ["A" "B" "A" "C" "B" "A" "B" "C" "A" "B"]}))

(-> cat-data
    (pj/frame (pj/cross [:size :color :grade] [:size :color :grade])))

;; The diagonal counts render correctly. The fix does not need to
;; exempt `:count` for correctness today, but should still exempt
;; it defensively -- a future numeric-categorical column type or
;; new sharing mechanism could expose the same shape of bug.

;; ### Variant 1c -- numeric SPLOM with very different column ranges
;;
;; Three numeric columns with disjoint ranges to make the
;; clipping more dramatic. Every diagonal is blank.

(def numeric-wide
  (tc/dataset {:a (repeatedly 100 #(+ 50 (* 10 (rand))))
               :b (repeatedly 100 #(+ 200 (* 50 (rand))))
               :c (repeatedly 100 #(rand))}))

(-> numeric-wide
    (pj/frame (pj/cross [:a :b :c] [:a :b :c])))

;; ### Variant 1d -- `:bin2d` heatmap (the counterexample)
;;
;; A heatmap. x and y are BOTH data axes; count is the fill
;; aesthetic. If the cell were inside a composite with shared
;; y, that sharing SHOULD apply. The fix must NOT exempt `:bin2d`.

(def hm-data
  (tc/dataset {:x (repeatedly 200 #(* 10 (rand)))
               :y (repeatedly 200 #(* 5 (rand)))}))

(-> hm-data
    (pj/lay-tile :x :y {:stat :bin2d}))

;; ## Q2 -- mixed-layer leaves
;;
;; What if a single cell carries layers with mixed y-axis semantics
;; (one count-axis, one data-axis)? The proposed rule is "exempt
;; only if EVERY layer is count-axis." We need to know how common
;; mixed-layer cells are and whether the fix's granularity is right.

;; ### Variant 2a -- histogram + KDE on the same leaf
;;
;; Both layers are count/density-axis stats; the "every layer"
;; rule still exempts the leaf from shared y-scale. This is the
;; natural "histogram with smoothing curve" composition.

(def density-data
  (tc/dataset {:x (concat (repeatedly 100 #(+ 2 (rand)))
                          (repeatedly 100 #(+ 5 (rand))))}))

(-> density-data
    (pj/lay-histogram :x)
    (pj/lay-density :x))

;; The density curve is invisible above. The y-axis is the
;; histogram's count [0, ~50], and density values [0, ~0.3] get
;; squashed against zero. Both layers landed on a single leaf, but
;; the within-leaf y-domain is computed as the union of all layers'
;; y-data -- so the count axis dominates.
;;
;; The user-facing fix is to normalize the histogram to a density
;; scale. Then both layers measure density, share the same axis,
;; and read together:

(-> density-data
    (pj/lay-histogram :x {:normalize :density})
    (pj/lay-density :x))

;; This matches ggplot's behavior. The library does not auto-detect
;; the mismatch -- we follow the principle that a user who layers
;; two stat-driven geoms together is asking for the union of their
;; y-data, and we leave the choice of normalization to them.

;; ### Variant 2b -- histogram + scatter forced onto one leaf
;;
;; The hypothetical worst case: a count-axis bar layer plus a
;; data-axis scatter layer share a cell. Force them via an
;; explicit frame mapping so both inherit `:x` and `:y`.

(def scatter-hist-data
  (tc/dataset {:x (repeatedly 50 #(rand))
               :y (repeatedly 50 #(rand))}))

(-> scatter-hist-data
    (pj/frame {:x :x :y :y})
    (pj/lay-histogram)
    (pj/lay-point))

;; The bars dominate the cell; scatter points squash at the bottom.
;; This is the same within-leaf y-coordination issue as 2a, just
;; with a stat-driven layer (`:bin`) and a data-driven layer
;; (`:identity`) instead of two stat-driven layers.
;;
;; Same workaround applies: normalize the histogram so its y-axis
;; is on a comparable scale to the scatter's y data:

(-> scatter-hist-data
    (pj/frame {:x :x :y :y})
    (pj/lay-histogram {:normalize :density})
    (pj/lay-point))

;; The density bars peak around 1.7 and the scatter points span
;; [0, 1] -- they coexist on a shared y-axis [0, 1.7]. Not a
;; perfectly aligned visual, but both layers are now legible.
;; Real applications usually pick a normalization tuned to the data
;; (e.g. `(pj/options {:y-scale {:domain [0 1]}})` to clip).

;; ## Q3 -- representative leaf for shared legend
;;
;; `compositor/rep-leaf-plan` picks `(first leaves)` for legend
;; extraction. For a SPLOM, the first leaf is the (col-0, col-0)
;; diagonal cell -- a histogram. Does the legend break?

(def size-data
  (tc/dataset {:a (repeatedly 30 #(rand))
               :b (repeatedly 30 #(rand))
               :weight (repeatedly 30 #(* 10 (rand)))}))

(-> size-data
    (pj/frame {:size :weight})
    (pj/frame (pj/cross [:a :b] [:a :b])))

;; The size legend renders correctly even though the rep-leaf is
;; a histogram cell that doesn't visually use the size aesthetic.
;; The legend code reads from the leaf's *mapping* (which inherits
;; `:size :weight` from the root), not from rendered output. Q3 is
;; not a real bug today; no rep-leaf guard is needed.

;; ## Conclusions and shipped fix
;;
;; * **Q1** -- shipped. `frame/inject-shared-scales` now skips the
;;   `:y-scale-domain` stamp on any leaf whose every effective layer
;;   resolves to a stat in `#{:bin :count :density}`. The diagonal
;;   histogram cells of a SPLOM correctly render against a count
;;   axis, while off-diagonal scatters keep their shared-data domain.
;;
;; * **Q2** -- mixed-axis cells (count-axis layers plus data-axis
;;   layers on the same leaf, OR two count-axis layers with
;;   different value ranges like histogram + density) follow ggplot's
;;   convention: the y-axis is the union of all layers' y-data. The
;;   user-facing fix is `{:normalize :density}` on the histogram to
;;   put both layers on a comparable scale; both 2a and 2b above
;;   demonstrate it. The library does not auto-detect or auto-rescale
;;   here -- by design, layering stat-driven layers leaves the
;;   normalization choice with the user.
;;
;; * **Q3** -- not a real bug. The compositor's representative leaf
;;   for the shared legend reads from the leaf's mapping (which
;;   inherits from the root frame), not from rendered output. Even
;;   when the rep leaf is a histogram, the legend renders the right
;;   color/size/alpha entries.
;;
;; The exemption is bounded by predicted stat (the same precedence
;; `leaf->draft` and `resolve/resolve-draft-layer` use) so it
;; correctly handles four shapes:
;;
;; * explicit `:stat` on the layer
;; * explicit `:layer-type` whose registry entry carries a `:stat`
;; * explicit `:mark` with no `:stat` (defaults to `:identity`)
;; * empty layers + non-empty mapping (the SPLOM diagonal case);
;;   `infer-layer-type` runs on the merged mapping
;;
;; `:bin2d` and `:density-2d` (heatmaps) are deliberately excluded
;; from the exemption -- their count goes to the fill aesthetic, not
;; to y, so they participate in shared y-domain coordination
;; normally.
