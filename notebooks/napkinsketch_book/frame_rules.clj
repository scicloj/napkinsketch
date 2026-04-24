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
