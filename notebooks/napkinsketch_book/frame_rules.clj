;; # Frame Rules
;;
;; This chapter is the specification for how `sk/frame`, `sk/lay-*`,
;; `sk/arrange`, `sk/options`, `sk/scale`, `sk/coord`, `sk/facet`,
;; and `sk/cross` compose in the frame world. Twenty-eight rules
;; across eight sections; each rule is demonstrated with a rendered
;; frame (or plan), a printed structure, and a verified assertion.
;;
;; Read [Frame Model](./napkinsketch_book.frame_model.html) first --
;; this chapter is the proof layer, not a teaching chapter. Every
;; rule is tested on every run. The parallel legacy reference is
;; [Sketch Rules](./napkinsketch_book.sketch_rules.html), which
;; documents the adapter path still available as `sk/sketch` +
;; `sk/view`.

(ns napkinsketch-book.frame-rules
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Tablecloth -- dataset operations
   [tablecloth.api :as tc]
   ;; RDatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## Setup

(def iris (rdatasets/datasets-iris))

;; A helper to inspect frame structure without `:data` -- the dataset
;; is heavy and not what we are checking. We strip `:data` from the
;; frame and every nested sub-frame and layer.

(defn strip-data [fr]
  (cond-> (dissoc fr :data)
    (:layers fr) (update :layers (partial mapv #(dissoc % :data)))
    (:frames fr) (update :frames (partial mapv strip-data))))

(defn fr-summary
  "Print frame structure without :data (for readability)."
  [fr]
  (kind/pprint (strip-data fr)))

;; ## Overview
;;
;; A **frame** is a plain Clojure map with a documented set of keys:
;;
;; | Key | On | Purpose |
;; |:----|:---|:--------|
;; | `:data` | leaf or any ancestor | dataset (tablecloth) |
;; | `:mapping` | frame or layer | column-to-aesthetic bindings |
;; | `:layers` | frame | per-scope layers |
;; | `:frames` | composite only | sub-frames |
;; | `:layout` | composite | direction + weights |
;; | `:opts` | root | plot-level options |
;; | `:share-scales` | composite | axes shared across sub-frames |
;;
;; A **leaf frame** has `:data`, `:mapping`, `:layers`; no `:frames`.
;; A **composite frame** has `:frames`; sub-frames can be leaves or
;; further composites. A **layer** is a map with `:layer-type` and an
;; optional `:mapping`, plus sibling keys `:stat`, `:position`,
;; `:mark` when the user provides them.
;;
;; The rules below assume some familiarity with these shapes. If this
;; is new, [Frame Model](./napkinsketch_book.frame_model.html) shows
;; them in use before we formalize them here.

;; ---
;; ## Construction
;;
;; How frames and composites come into existence. Eight rules
;; covering every `sk/frame` call shape plus `sk/arrange`.

;; ### Rule C1: `sk/frame` on raw data creates a leaf
;;
;; Called with a dataset as first argument, `sk/frame` returns a
;; leaf frame. The arity decides what's in `:mapping`: a keyword is
;; `:x`; two keywords are `:x` and `:y`; an options map contributes
;; aesthetic keys.

(-> iris
    (sk/frame :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

(-> iris
    (sk/frame :sepal-length :sepal-width)
    fr-summary)

(kind/test-last
 [(fn [fr]
    (and (= {:x :sepal-length :y :sepal-width} (:mapping fr))
         (= [] (:layers fr))
         (not (contains? fr :frames))))])

;; With only an aesthetic mapping, position is omitted -- the frame
;; is a leaf with no position yet. Inference at render time will
;; handle picking an axis if a layer is added without position.

(-> iris
    (sk/frame {:color :species})
    fr-summary)

(kind/test-last
 [(fn [fr]
    (and (= {:color :species} (:mapping fr))
         (not (contains? fr :frames))))])

;; ### Rule C2: `sk/frame` on an unpositioned leaf extends its mapping
;;
;; A leaf is **unpositioned** if neither its own `:mapping` nor any
;; of its layers' mappings carries `:x` or `:y`. Calling `sk/frame`
;; again on such a leaf merges the new mapping into the leaf's own;
;; the leaf remains a leaf. No composite is created.

(-> iris
    sk/frame
    (sk/frame :sepal-length :sepal-width))

(kind/test-last
 [(fn [fr]
    (and (= {:x :sepal-length :y :sepal-width} (:mapping fr))
         (not (contains? fr :frames))))])

;; And an aesthetic-on-aesthetic extension merges with later-wins:

(-> iris
    (sk/frame {:color :species})
    (sk/frame :sepal-length :sepal-width))

(kind/test-last
 [(fn [fr]
    (= {:x :sepal-length :y :sepal-width :color :species} (:mapping fr)))])

;; **Property P-C2 -- construction commutativity.** A chained
;; unpositioned extension yields a frame structurally equal to the
;; same content expressed as one call.

(-> iris
    sk/frame
    (sk/frame {:color :species})
    (sk/frame :sepal-length :sepal-width))

(kind/test-last
 [(fn [fr]
    (= fr (sk/frame iris :sepal-length :sepal-width {:color :species})))])

;; ### Rule C3: `sk/frame` with position on a positioned leaf promotes to a composite
;;
;; When `sk/frame` is called with position (`:x`/`:y`) on a leaf
;; that already has position, the leaf becomes sub-frame 1 of a new
;; composite and the call becomes sub-frame 2. If the leaf carried
;; aesthetic alongside position, the aesthetic moves to the new
;; composite's **root** `:mapping` and flows to every sub-frame; the
;; position stays with sub-frame 1.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/frame :petal-length :petal-width))

(kind/test-last
 [(fn [fr]
    (and (= 2 (count (:frames fr)))
         (= {:x :sepal-length :y :sepal-width}
            (:mapping (first (:frames fr))))
         (= {:x :petal-length :y :petal-width}
            (:mapping (second (:frames fr))))))])

;; When the leaf carried aesthetic + position, promotion splits
;; them -- aesthetic goes to root (flows to both panels), position
;; stays with sub-frame 1:

(-> iris
    (sk/frame :sepal-length :sepal-width {:color :species})
    (sk/frame :petal-length :petal-width)
    fr-summary)

(kind/test-last
 [(fn [fr]
    (and (= {:color :species} (:mapping fr))
         (= {:x :sepal-length :y :sepal-width}
            (:mapping (first (:frames fr))))
         (= {:x :petal-length :y :petal-width}
            (:mapping (second (:frames fr))))))])

;; **Property P-C3b -- plot-level options stay at root on promotion.**
;; A `:title` set via `sk/options` before promotion does not demote
;; into sub-frame 1; it lives on the composite root's `:opts`.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/options {:title "Iris"})
    (sk/frame :petal-length :petal-width))

(kind/test-last
 [(fn [fr]
    (and (= "Iris" (get-in fr [:opts :title]))
         (not (contains? (first (:frames fr)) :opts))))])

;; ### Rule C4: aesthetic-only `sk/frame` on a positioned leaf promotes without adding a panel
;;
;; An aesthetic-only call (no `:x`/`:y`) on a positioned leaf
;; wraps the leaf as sub-frame 1 of a new composite and routes the
;; aesthetic to the composite's root `:mapping`. The composite ends
;; up with exactly **one** sub-frame. The purpose is to position the
;; aesthetic at plot scope ahead of any subsequent panel.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/frame {:color :species}))

(kind/test-last
 [(fn [fr]
    (and (= 1 (count (:frames fr)))
         (= {:color :species} (:mapping fr))
         (= {:x :sepal-length :y :sepal-width}
            (:mapping (first (:frames fr))))))])

;; **Property P-C4 -- aesthetic-then-panel equivalence.**
;; Aesthetic-only promotion followed by a position call equals
;; bundling the aesthetic on the initial leaf and then promoting
;; (C3's mapping-split path). Users can switch between the two
;; forms without changing the result.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/frame {:color :species})
    (sk/frame :petal-length :petal-width))

(kind/test-last
 [(fn [fr]
    (= fr
       (-> iris
           (sk/frame :sepal-length :sepal-width {:color :species})
           (sk/frame :petal-length :petal-width))))])

;; ### Rule C5: layer partitioning at promotion splits layers by position presence
;;
;; When a positioned leaf is promoted (via C3 or C4), each layer
;; is partitioned by a single test: a layer whose own `:mapping`
;; contains `:x` or `:y` is **panel-origin** and stays with
;; sub-frame 1; otherwise it is **root-origin** and moves to the
;; composite's root `:layers`, flowing to every sub-frame via
;; `resolve-tree`. No whitelist; the layer's own mapping is
;; self-describing.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    sk/lay-point
    (sk/frame :petal-length :petal-width))

(kind/test-last
 [(fn [fr]
    (and (= 1 (count (:layers fr)))
         (= :point (:layer-type (first (:layers fr))))
         (= 2 (count (:frames fr)))
         (= [] (:layers (first (:frames fr))))
         (= [] (:layers (second (:frames fr))))))])

;; The bare `sk/lay-point` call is root-origin: at render time it
;; reaches both panels through the resolve-tree walk. Had we passed
;; position -- `(sk/lay-point :sepal-length :sepal-width)` -- the
;; layer would stay with sub-frame 1.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/frame :petal-length :petal-width))

(kind/test-last
 [(fn [fr]
    (and (or (not (contains? fr :layers))
             (= [] (:layers fr)))
         (= 1 (count (:layers (first (:frames fr)))))
         (= [] (:layers (second (:frames fr))))))])

;; ### Rule C6: `sk/frame` on a composite dispatches by call shape
;;
;; Already a composite? The dispatch is:
;;
;; - **Position-carrying call** -- append a new sub-frame to `:frames`.
;; - **Aesthetic-only call** -- merge the aesthetic into the root
;;   `:mapping` (no new sub-frame).
;; - **Empty call** -- no-op (see C7).

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/frame :petal-length :petal-width)
    (sk/frame :sepal-length :petal-length))

(kind/test-last
 [(fn [fr]
    (and (= 3 (count (:frames fr)))
         (= [{:x :sepal-length :y :sepal-width}
             {:x :petal-length :y :petal-width}
             {:x :sepal-length :y :petal-length}]
            (mapv :mapping (:frames fr)))))])

;; An aesthetic-only call on a composite merges into root, leaving
;; sub-frames alone:

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/frame :petal-length :petal-width)
    (sk/frame {:color :species})
    fr-summary)

(kind/test-last
 [(fn [fr]
    (and (= 2 (count (:frames fr)))
         (= {:color :species} (:mapping fr))
         (= {:x :sepal-length :y :sepal-width}
            (:mapping (first (:frames fr))))))])

;; ### Rule C7: empty `sk/frame` on an existing frame is a no-op
;;
;; `(sk/frame fr)` where `fr` is a leaf or a composite returns `fr`
;; unchanged. This makes the 1-arity `sk/frame` safe as a syntactic
;; nullity.

(let [fr (-> iris (sk/frame :sepal-length :sepal-width))]
  (= fr (sk/frame fr)))

(kind/test-last [true?])

(let [fr (-> iris
             (sk/frame :sepal-length :sepal-width)
             (sk/frame :petal-length :petal-width))]
  (= fr (sk/frame fr)))

(kind/test-last [true?])

;; ### Rule C8: `sk/arrange` composes frames into a composite
;;
;; `sk/arrange` takes a sequence of frames (leaves in alpha) plus
;; optional layout opts and returns a composite. The inputs become
;; the composite's `:frames`, wrapped in a 2-level row-and-column
;; layout.

(sk/arrange
 [(-> iris (sk/frame :sepal-length :sepal-width) sk/lay-point)
  (-> iris (sk/frame :petal-length :petal-width) sk/lay-point)])

(kind/test-last
 [(fn [fr]
    (and (contains? fr :frames)
         (= :vertical (get-in fr [:layout :direction]))
         ;; arrange wraps in rows-of-columns: outer :frames is
         ;; rows, each row's :frames is cells
         (= 1 (count (:frames fr)))
         (= 2 (count (:frames (first (:frames fr)))))))])

;; Opts (`:cols`, `:title`, `:width`, `:height`, `:share-scales`)
;; route into the composite's `:opts` / `:layout`:

(sk/arrange
 [(sk/frame iris :sepal-length :sepal-width)
  (sk/frame iris :petal-length :petal-width)]
 {:title "Arranged"
  :share-scales #{:y}})

(kind/test-last
 [(fn [fr]
    (and (= "Arranged" (get-in fr [:opts :title]))
         (= #{:y} (:share-scales fr))))])

;; ---
;; ## Layer Placement
;;
;; How `lay-*` calls decide where the layer lands in the frame tree.
;; Four rules covering the (bare vs position) x (leaf vs composite)
;; matrix plus the raw-data convenience case.
;;
;; **Position storage (ratified 2026-04-23):** when a `lay-*` call
;; carries position, the position lives on the **layer's own
;; `:mapping`**. The leaf being attached to (or created for) also
;; carries position in its own `:mapping` where appropriate -- both
;; resolve to the same effective `:x`/`:y` via scope merge. The
;; layer's own `:mapping` is the authoritative record of what the
;; user typed and is what C5 inspects at promotion.

;; ### Rule LP1: bare `lay-*` attaches at the current frame's root
;;
;; A `lay-*` call without position arguments attaches the layer to
;; the current frame's top-level `:layers`. On a leaf, that is the
;; leaf's own `:layers`. On a composite, it is the root `:layers`,
;; and the layer flows into every descendant leaf via
;; `resolve-tree`.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    sk/lay-point)

(kind/test-last
 [(fn [fr]
    (and (= 1 (count (:layers fr)))
         (= :point (:layer-type (first (:layers fr))))
         (empty? (or (:mapping (first (:layers fr))) {}))))])

;; On a composite, the same call attaches at root and reaches every
;; panel through resolve-tree:

(-> (sk/arrange
     [(sk/frame iris :sepal-length :sepal-width)
      (sk/frame iris :petal-length :petal-width)])
    sk/lay-point
    fr-summary)

(kind/test-last
 [(fn [fr]
    (and (contains? fr :frames)
         (= 1 (count (:layers fr)))
         (= :point (:layer-type (first (:layers fr))))))])

;; **Property P-LP1 -- bare layers flow downward.** After adding one
;; bare layer to a composite, the composite's root `:layers` holds
;; that single entry; `resolve-tree` walks each leaf with it
;; prepended, so every sub-plot renders the layer on top of its
;; inferred or explicit leaf layers.

(let [before (sk/arrange
              [(sk/frame iris :sepal-length :sepal-width)
               (sk/frame iris :petal-length :petal-width)])
      after  (-> (sk/arrange
                  [(sk/frame iris :sepal-length :sepal-width)
                   (sk/frame iris :petal-length :petal-width)])
                 sk/lay-point)]
  [(count (or (:layers before) []))
   (count (or (:layers after)  []))])

(kind/test-last
 [(fn [counts] (= [0 1] counts))])

;; ### Rule LP2: position-carrying `lay-*` attaches to the DFS-last matching leaf
;;
;; When `lay-*` carries `:x`/`:y` and at least one leaf has matching
;; effective `:x`/`:y` (after ancestor merge), the layer attaches to
;; the **last such leaf in left-to-right depth-first order**.
;; Matching is keyword/string tolerant. The layer's own `:mapping`
;; carries the call's position.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/frame :petal-length :petal-width)
    (sk/lay-point :sepal-length :sepal-width))

(kind/test-last
 [(fn [fr]
    (and (= 2 (count (:frames fr)))
         (= 1 (count (:layers (first (:frames fr)))))
         (= 0 (count (:layers (second (:frames fr)))))
         (= :point
            (:layer-type (first (:layers (first (:frames fr))))))))])

;; Keyword/string tolerance -- the string form matches a keyword
;; leaf (LP2e):

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/lay-point "sepal-length" "sepal-width"))

(kind/test-last
 [(fn [fr]
    (and (not (contains? fr :frames))
         (= 1 (count (:layers fr)))))])

;; **Note on leaf-input with non-matching position (overlay).** When
;; the receiver is a single leaf that carries position and the
;; `lay-*` call carries a **different** position, the call does
;; *not* promote to a composite. Instead, the layer's own `:mapping`
;; carries the new position; at render, the layer's position
;; overrides the leaf's via scope merge -- an overlay on the same
;; panel. Adding a new panel in the frame world requires an
;; explicit `sk/frame` call.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/lay-point :petal-length :petal-width))

(kind/test-last
 [(fn [fr]
    (and (not (contains? fr :frames))
         (= {:x :sepal-length :y :sepal-width} (:mapping fr))
         (= 1 (count (:layers fr)))
         (= {:x :petal-length :y :petal-width}
            (:mapping (first (:layers fr))))))])

;; ### Rule LP3: on a composite, position-carrying `lay-*` misses append a new leaf at root
;;
;; When `lay-*` carries `:x`/`:y` and **no** descendant leaf has
;; matching effective `:x`/`:y`, a new leaf is appended at the
;; composite's root `:frames`. Its `:mapping` carries the call's
;; position; a single layer with matching position attaches to it.
;; (Leaf-input with non-matching position is a separate case --
;; overlay, per LP2 above.)

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/frame :petal-length :petal-width)
    (sk/lay-point :sepal-length :petal-length))

(kind/test-last
 [(fn [fr]
    (and (= 3 (count (:frames fr)))
         (= {:x :sepal-length :y :petal-length}
            (:mapping (nth (:frames fr) 2)))
         (= 1 (count (:layers (nth (:frames fr) 2))))))])

;; ### Rule LP4: `lay-*` on raw data coerces via the legacy adapter
;;
;; `lay-*` called with a dataset as its first argument routes
;; through the legacy sketch adapter. The result is a Sketch record
;; with one view (or none, for bare calls on small datasets where
;; auto-inference from structure supplies columns), not a frame.
;; This keeps the convenience one-liner
;; `(-> data (sk/lay-point :x :y))` working during the alpha
;; transition; frame-native idiom is `(-> data (sk/frame :x :y)
;; sk/lay-point)`.

(def tiny
  {:a [1 2 3 4 5]
   :b [2 4 3 5 4]})

(-> tiny
    (sk/lay-point :a :b))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; The frame-native equivalent produces a clean frame:

(-> tiny
    (sk/frame :a :b)
    sk/lay-point
    fr-summary)

(kind/test-last
 [(fn [fr]
    (and (= {:x :a :y :b} (:mapping fr))
         (= 1 (count (:layers fr)))
         (not (contains? fr :frames))))])

;; ---
;; ## Leaf Identity
;;
;; How columns identify a leaf. Two rules -- one about inference
;; when the user omits column names, one about how column refs are
;; compared.

;; ### Rule LI1: few-column datasets auto-infer columns by position
;;
;; When `lay-*` or `sk/frame` is called on a dataset without
;; explicit column arguments, columns are inferred:
;;
;; | Columns | Inferred mapping |
;; |:--------|:-----------------|
;; | 1 | `{:x col0}` |
;; | 2 | `{:x col0 :y col1}` |
;; | 3 | `{:x col0 :y col1 :color col2}` |
;; | 4+ | error (pass explicit x and y) |

(-> {:height [1 2 3] :weight [4 5 6] :species ["a" "b" "a"]}
    sk/lay-point)

(kind/test-last
 [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; Four or more columns without explicit arguments throws:

(try
  (-> {:a [1 2] :b [3 4] :c [5 6] :d [7 8]}
      sk/lay-point)
  (catch Exception e
    (ex-message e)))

(kind/test-last
 [(fn [msg] (re-find #"Cannot auto-infer columns" msg))])

;; ### Rule LI2: column references compare tolerantly between keywords and strings
;;
;; When matching column refs -- whether a `lay-*` call's position
;; against a leaf's, or inside scope resolution -- `:x` and `"x"`
;; are treated as the same column. The stored form is preserved
;; as the user typed it; tolerance is a comparison property only.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/lay-point "sepal-length" "sepal-width"))

(kind/test-last
 [(fn [fr]
    (and (not (contains? fr :frames))
         (= 1 (count (:layers fr)))))])

;; Storage preserves the user's input -- if you type a string, the
;; frame holds a string:

(-> iris
    (sk/frame "sepal-length" "sepal-width")
    fr-summary)

(kind/test-last
 [(fn [fr] (= {:x "sepal-length" :y "sepal-width"} (:mapping fr)))])

;; ---
;; ## Scope
;;
;; How mappings and data flow through the frame tree. Four rules
;; generalizing the sketch-world hierarchy (sketch / view / layer)
;; to arbitrary-depth composites.

;; ### Rule S1: mapping scope is a tree-walk merge; narrower wins
;;
;; The effective `:mapping` for a rendered layer is computed by
;; merging, in order: root's `:mapping` -> each ancestor
;; composite's `:mapping` -> the leaf's own `:mapping` -> the
;; layer's own `:mapping`. Inner keys override outer. Any depth of
;; composite nesting works the same way.

;; Root-level aesthetic flows to every leaf. Using a two-panel
;; composite with `:color` declared at root:

(def s1-composite
  (sk/prepare-frame
   {:data iris
    :mapping {:color :species}
    :frames [{:mapping {:x :sepal-length :y :sepal-width}
              :layers [{:layer-type :point}]}
             {:mapping {:x :petal-length :y :petal-width}
              :layers [{:layer-type :point}]}]}))

s1-composite

(kind/test-last
 [(fn [fr]
    (let [pl (sk/plan fr)
          panels (mapv (comp :panels :plan) (:sub-plots pl))]
      ;; Both sub-plots render colored-by-species -- 3 groups per panel
      (every? (fn [pp]
                (= 3 (count (:groups (first (:layers (first pp)))))))
              panels)))])

;; **Property P-S1a -- sibling independence.** A sub-frame's own
;; mapping does not leak into its siblings.

(def s1-siblings
  (sk/prepare-frame
   {:data iris
    :frames [{:mapping {:x :sepal-length :y :sepal-width}
              :layers [{:layer-type :point}]}
             {:mapping {:x :petal-length :y :petal-width :color :species}
              :layers [{:layer-type :point}]}]}))

s1-siblings

(kind/test-last
 [(fn [fr]
    (let [sub-plots (:sub-plots (sk/plan fr))
          panel-groups (mapv (fn [sp]
                               (count (:groups (first (:layers
                                                       (first (-> sp :plan :panels)))))))
                             sub-plots)]
      ;; Sub-plot 0: no color -> 1 group. Sub-plot 1: :color :species -> 3.
      (= [1 3] panel-groups)))])

;; ### Rule S2: data scope -- nearest-ancestor-non-nil wins
;;
;; The effective `:data` for a rendered layer is the nearest
;; non-nil `:data` walking from the layer up through each ancestor
;; to the root. Layer `:data` > leaf `:data` > nearest ancestor
;; composite `:data` > root `:data`. Unlike mappings, data does not
;; merge -- it is picked, wholesale.

(def s2-tree
  (sk/prepare-frame
   {:data iris
    :frames [{:mapping {:x :sepal-length :y :sepal-width}
              :layers [{:layer-type :point}]}
             {:mapping {:x :a :y :b}
              :data (tc/dataset {:a [1 2 3] :b [3 5 4]})
              :layers [{:layer-type :point}]}]}))

s2-tree

(kind/test-last
 [(fn [fr]
    (let [sub-plots (:sub-plots (sk/plan fr))
          counts (mapv (fn [sp]
                         (-> sp :plan :panels first :layers first
                             :groups first :xs count))
                       sub-plots)]
      ;; Sub-plot 0 inherits root's iris (150 rows). Sub-plot 1 uses its own (3).
      (= [150 3] counts)))])

;; ### Rule S3: `nil` in a mapping cancels an inherited value
;;
;; Assigning `nil` to a mapping key at an inner scope cancels the
;; value inherited from outer scopes. The rendering path treats a
;; nil mapping value as equivalent to "no mapping for that
;; aesthetic."

(-> iris
    (sk/frame :sepal-length :sepal-width {:color :species})
    sk/lay-point
    (sk/lay-smooth {:color nil :stat :linear-model}))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            ;; lm produces one overall line (not three), because its
            ;; :color was canceled
            (and (= 150 (:points s))
                 (= 1 (:lines s)))))])

;; ### Rule S4: layer `:mapping` is the narrowest scope
;;
;; A mapping written in a layer's own `:mapping` (aesthetic opts
;; passed to `lay-*`) scopes to that layer only. Other layers --
;; even on the same leaf -- do not see it. This is the terminal
;; case of S1: the layer's mapping is innermost in the merge.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/lay-point {:color :species})
    (sk/lay-smooth {:stat :linear-model}))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            ;; Point layer sees :species (colored points); smooth
            ;; layer does not (one overall regression line).
            (and (= 150 (:points s))
                 (= 1 (:lines s)))))])

;; ---
;; ## Options
;;
;; Plot-level options and modifiers. Unlike mappings, layers, and
;; data (which live in the scope hierarchy), options configure the
;; whole rendered plot and attach to the root's `:opts`.

;; ### Rule O1: `sk/options` writes to the root's `:opts`
;;
;; `sk/options` merges its argument into the current frame's
;; `:opts`. On a leaf, that is the leaf's `:opts`. On a composite,
;; the root's. Options do not flow down like mappings -- they are
;; plot-level, not layer-level.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    sk/lay-point
    (sk/options {:title "Iris"}))

(kind/test-last
 [(fn [fr] (= "Iris" (get-in fr [:opts :title])))])

;; Repeated calls merge, later-wins on collisions:

(-> iris
    (sk/frame :sepal-length :sepal-width)
    sk/lay-point
    (sk/options {:title "One"})
    (sk/options {:title "Two" :subtitle "Sub"}))

(kind/test-last
 [(fn [fr]
    (and (= "Two" (get-in fr [:opts :title]))
         (= "Sub" (get-in fr [:opts :subtitle]))))])

;; ### Rule O2: `sk/scale` and `sk/coord` are plot-level options
;;
;; `sk/scale` and `sk/coord` write into `:opts` as `:x-scale` /
;; `:y-scale` and `:coord`. They apply to every leaf in the tree
;; uniformly. (Per-panel scale variation is an open design
;; question; today both are plot-wide.)

(-> iris
    (sk/frame :sepal-length :sepal-width)
    sk/lay-point
    (sk/scale :x :log)
    (sk/coord :flip))

(kind/test-last
 [(fn [fr]
    (and (= {:type :log} (get-in fr [:opts :x-scale]))
         (= :flip (get-in fr [:opts :coord]))))])

;; ### Rule O3: `sk/facet` writes the faceting column to `:opts`
;;
;; `sk/facet` and `sk/facet-grid` store facet columns in `:opts`
;; as `:facet-col` (and `:facet-row` for a grid). The layout
;; effect -- splitting each leaf's panel into a group of panels --
;; happens at render time.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    sk/lay-point
    (sk/facet :species))

(kind/test-last
 [(fn [fr] (= :species (get-in fr [:opts :facet-col])))])

;; A 2D grid uses both keys:

(-> iris
    (sk/frame :sepal-length :sepal-width)
    sk/lay-point
    (sk/facet-grid :species :species))

(kind/test-last
 [(fn [fr]
    (and (= :species (get-in fr [:opts :facet-col]))
         (= :species (get-in fr [:opts :facet-row]))))])

;; ### Rule O4: `sk/lay-rule-*` and `sk/lay-band-*` are layers (annotations)
;;
;; `sk/lay-rule-h`, `sk/lay-rule-v`, `sk/lay-band-h`, `sk/lay-band-v`
;; produce layers and scope like any other `lay-*`: bare call
;; attaches at root (flows to every panel); 4-arity with column
;; refs attaches to a matching leaf. Position rides as layer-type
;; keys (`:y-intercept`, `:x-intercept`, `:y-min`/`:y-max`,
;; `:x-min`/`:x-max`), not column refs.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/lay-point {:color :species})
    (sk/lay-rule-h {:y-intercept 3.0}))

(kind/test-last
 [(fn [fr]
    (let [layers (:layers fr)
          rule (some #(when (= :rule-h (:layer-type %)) %) layers)]
      (and (some? rule)
           (= 3.0 (get-in rule [:mapping :y-intercept])))))])

;; A view-scope annotation via the 4-arity attaches to a matching
;; leaf, not every panel:

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/frame :petal-length :petal-width)
    (sk/lay-rule-h :sepal-length :sepal-width {:y-intercept 3.0}))

(kind/test-last
 [(fn [fr]
    (and (= 2 (count (:frames fr)))
         (= 1 (count (:layers (first (:frames fr)))))
         (= 0 (count (:layers (second (:frames fr)))))
         (= :rule-h (:layer-type
                     (first (:layers (first (:frames fr))))))))])

;; ---
;; ## Assembly
;;
;; How the rules above combine to produce rendered layers. The
;; `sk/draft` pipeline stage is the observable output of assembly;
;; each entry corresponds to one rendered layer.

;; ### Rule A1: one rendered layer per applicable (leaf, layer) pair
;;
;; For each leaf in the resolved tree, the number of rendered
;; layers equals the number of layers applicable to that leaf --
;; the leaf's own plus all ancestor root-origin layers.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/frame :petal-length :petal-width)
    sk/lay-point                            ;; root-origin; reaches both
    (sk/lay-smooth :sepal-length :sepal-width
                   {:stat :linear-model}))  ;; panel-origin; sub-frame 1 only

(kind/test-last
 [(fn [fr]
    (let [pl (sk/plan fr)
          panel-layer-counts (mapv (fn [sp]
                                     (count (:layers (first (-> sp :plan :panels)))))
                                   (:sub-plots pl))]
      ;; sub-plot 0: point + smooth (2); sub-plot 1: point only (1)
      (= [2 1] panel-layer-counts)))])

;; ### Rule A2: each rendered layer carries fully merged scope
;;
;; A draft entry reflects the full scope merge: effective `:data`,
;; effective `:mapping` (covering both aesthetics and position),
;; and `:layer-type` (plus any `:stat`, `:position`, `:mark`
;; promoted to siblings). No scope level is dropped; no key is
;; unresolved.

(-> iris
    (sk/frame :sepal-length :sepal-width {:color :species})
    sk/lay-point
    sk/draft)

(kind/test-last
 [(fn [drafts]
    (and (= 1 (count drafts))
         (let [d (first drafts)]
           (and (= :sepal-length (:x d))
                (= :sepal-width (:y d))
                (= :species (:color d))
                (= :point (:mark d))
                (= 150 (tc/row-count (:data d)))))))])

;; ---
;; ## Layout
;;
;; How leaves become panels in the rendered plot. Four rules
;; covering single panels, overlays, faceting, and composite grids
;; with shared scales.

;; ### Rule L1: each leaf produces a panel block
;;
;; Each leaf in the resolved tree produces one **panel block** in
;; the rendered plot. Without faceting, the block contains one
;; panel. With `sk/facet` or `sk/facet-grid`, the block contains
;; one panel per facet value (or per (row, col) pair).

(-> iris
    (sk/frame :sepal-length :sepal-width)
    (sk/frame :petal-length :petal-width)
    sk/lay-point
    sk/plan)

(kind/test-last
 [(fn [pl]
    (and (:composite? pl)
         (= 2 (count (:sub-plots pl)))))])

;; ### Rule L2: layers within one leaf overlay within that leaf's panel block
;;
;; All layers applicable to a leaf (the leaf's own plus all
;; ancestor root-origin layers) draw on the same axis pair -- they
;; overlay within each panel of that leaf's block, not on separate
;; panels.

(-> iris
    (sk/frame :sepal-length :sepal-width {:color :species})
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model}))

(kind/test-last
 [(fn [fr]
    (let [pl (sk/plan fr)
          panel (first (:panels pl))]
      (and (= 1 (count (:panels pl)))
           (= 2 (count (:layers panel))))))])

;; ### Rule L3: faceting splits each leaf into panels by category
;;
;; `sk/facet :col` produces one panel per unique value of `:col`;
;; `sk/facet-grid :row-col :col-col` produces one panel per (row,
;; col) pair.

(-> iris
    (sk/frame :sepal-length :sepal-width)
    sk/lay-point
    (sk/facet :species))

(kind/test-last
 [(fn [fr] (= 3 (count (:panels (sk/plan fr)))))])

;; ### Rule L4: composite layout is controlled by `:layout` and optional `:share-scales`
;;
;; A composite frame carries a `:layout` map (set by `sk/arrange`
;; options: `:cols`, `:width`, `:height`, `:title`) controlling
;; the grid of its sub-frames' panel blocks. An optional
;; `:share-scales` (subset of `#{:x :y}`) enables column-bucketed
;; shared-scale resolution across sub-frames.
;;
;; **Column-bucketing**: when `:x` is shared, sub-frames whose
;; effective `:x` column is the same share that scale's domain;
;; sub-frames with different `:x` columns get independent
;; x-domains. Same for `:y`. This is what enables SPLOM (aligning
;; columns down, rows across) and marginal plots (x shared between
;; scatter and top density; right density has its own y).

(def l4-shared
  (sk/arrange
   [(-> iris (sk/frame :sepal-length :sepal-width) sk/lay-point)
    (-> iris (sk/frame :sepal-length :petal-width) sk/lay-point)]
   {:share-scales #{:x}}))

l4-shared

(kind/test-last
 [(fn [fr]
    (let [sub-plots (:sub-plots (sk/plan fr))
          domains (mapv #(get-in % [:plan :panels 0 :x-scale :domain]) sub-plots)]
      ;; Both panels share :sepal-length as x -> same x-domain
      (and (= 2 (count domains))
           (= (first domains) (second domains)))))])

;; ### Rule L5: multi-pair `sk/frame` reshapes rectangular pairs into a 2D grid (SPLOM)
;;
;; When `sk/frame` receives a pair-sequence that forms a
;; rectangular M x N Cartesian product (like the output of
;; `sk/cross cols cols`), the result is a nested **rows-of-cols**
;; composite with `:share-scales #{:x :y}` -- the canonical SPLOM
;; layout. Each cell inherits the base's `:data`, root `:mapping`,
;; and root `:layers` via `resolve-tree`. The compositor applies
;; three renderer flags on cells:
;;
;; - `:suppress-legend true` on every cell (one shared legend is
;;   drawn at composite level).
;; - `:suppress-x-label true` on every non-bottom row (x-axis label
;;   shows only on the bottom row).
;; - `:suppress-y-label true` on every non-leftmost column (y-axis
;;   label shows only on the leftmost column).
;;
;; Idiomatic SPLOM usage therefore omits `sk/lay-point` -- each
;; cell infers its own layer type: scatter off-diagonal, histogram
;; on the diagonal (where x = y). Pair lists that are not
;; rectangular fall through to the flat one-panel-per-pair behaviour
;; (see Rules C3 / C6).

(-> iris
    (sk/frame {:color :species})
    (sk/frame (sk/cross [:sepal-length :sepal-width]
                        [:petal-length :petal-width])))

(kind/test-last
 [(fn [fr]
    (and (= :vertical (get-in fr [:layout :direction]))
         (= #{:x :y} (:share-scales fr))
         (= 2 (count (:frames fr)))
         (every? #(= 2 (count (:frames %))) (:frames fr))
         (= {:color :species} (:mapping fr))))])

;; ---
;; ## A note on `sk/cross`
;;
;; `sk/cross` is not a rule. It is a pure pair-generator --
;; `(for [x xs y ys] [x y])` -- returning `[x-col y-col]` pairs. It
;; has no plot-level behavior on its own; whatever consumes the
;; pairs (today's `sk/view`, tomorrow's `sk/arrange` + `sk/frame`
;; over the generated sequence) is where the layout behavior lives,
;; and those cases are already covered by the rules above. `sk/cross`
;; belongs in the cookbook as a SPLOM-construction ingredient, not
;; here.

(sk/cross [:a :b] [:c :d])

(kind/test-last
 [(fn [pairs] (= [[:a :c] [:a :d] [:b :c] [:b :d]] pairs))])
