(ns scicloj.napkinsketch.frame-polymorphism-test
  "Specification for Phase 6's polymorphic sk/frame.

   These tests describe the target behavior after the Sketch record
   and :views adapter are retired. sk/frame dispatches on its first
   argument: raw data creates a leaf; an existing frame may be
   extended, promoted to a composite, or appended-to. Empty :mapping
   and :opts are never present in output (strict elision, Reading A
   per Phase 6 design discussion 2026-04-24).

   Design reference: memory/project_phase_6_design.md and
   dev-notes/frame-rules-draft.md. Case labels (C1, E2, P3, ...) match
   the test-first outline in the design doc."
  (:require [clojure.test :refer [deftest testing is]]
            [tablecloth.api :as tc]
            [scicloj.napkinsketch.api :as sk]
            [scicloj.napkinsketch.impl.frame :as frame]))

;; ============================================================
;; Shared fixtures
;; ============================================================

(def iris
  "Synthetic stand-in for rdatasets iris, using :a :b :c :d :e :f as
   placeholder column names to match the design-doc notation."
  (tc/dataset {:a       [1.0 2.0 3.0]
               :b       [0.5 1.0 1.5]
               :c       [10.0 20.0 30.0]
               :d       [100.0 200.0 300.0]
               :e       [1000.0 2000.0 3000.0]
               :f       [10000.0 20000.0 30000.0]
               :species ["setosa" "setosa" "versicolor"]}))

(defn- all-nodes
  "Seq of every frame and layer in the tree -- for property checks."
  [fr]
  (concat [fr]
          (:layers fr)
          (mapcat all-nodes (:frames fr))))

(defn- no-empty-map-output? [fr]
  (every? (fn [node]
            (not (or (and (contains? node :mapping) (empty? (:mapping node)))
                     (and (contains? node :opts) (empty? (:opts node))))))
          (all-nodes fr)))

;; ============================================================
;; Creation -- raw data input (C1-C6)
;; ============================================================

(deftest creation-raw-data-test
  (testing "C1: (sk/frame) -- arity-0 empty leaf, mapping elided"
    (let [f (sk/frame)]
      (is (= [] (:layers f)))
      (is (not (contains? f :mapping)))
      (is (not (contains? f :data)))
      (is (not (contains? f :frames)))))

  (testing "C2: (sk/frame iris) -- data only, mapping elided"
    (let [f (sk/frame iris)]
      (is (tc/dataset? (:data f)))
      (is (= [] (:layers f)))
      (is (not (contains? f :mapping)))
      (is (not (contains? f :frames)))))

  (testing "C3: (sk/frame iris {:color :species}) -- aesthetic-only mapping"
    (let [f (sk/frame iris {:color :species})]
      (is (= {:color :species} (:mapping f)))
      (is (= [] (:layers f)))
      (is (not (contains? f :frames)))))

  (testing "C4: (sk/frame iris :a) -- positional x only"
    (let [f (sk/frame iris :a)]
      (is (= {:x :a} (:mapping f)))
      (is (= [] (:layers f)))))

  (testing "C5: (sk/frame iris :a :b) -- positional x and y"
    (let [f (sk/frame iris :a :b)]
      (is (= {:x :a :y :b} (:mapping f)))
      (is (= [] (:layers f)))))

  (testing "C6: (sk/frame iris :a :b {:color :species}) -- position + opts"
    (let [f (sk/frame iris :a :b {:color :species})]
      (is (= {:x :a :y :b :color :species} (:mapping f)))
      (is (= [] (:layers f))))))

;; ============================================================
;; Extend -- existing leaf with room to grow (E1-E2)
;; ============================================================
;;
;; When a leaf has no position-bearing state (no :x/:y in its own
;; :mapping, no position-carrying layers), a subsequent sk/frame call
;; extends the leaf's mapping rather than promoting to composite.

(deftest extend-leaf-test
  (testing "E1: empty leaf, then position -- extends mapping"
    (let [f (-> iris sk/frame (sk/frame :a :b))]
      (is (= {:x :a :y :b} (:mapping f)))
      (is (= [] (:layers f)))
      (is (not (contains? f :frames)))))

  (testing "E2: aesthetic-only leaf, then position -- extends mapping"
    (let [f (-> iris (sk/frame {:color :species}) (sk/frame :a :b))]
      (is (= {:x :a :y :b :color :species} (:mapping f)))
      (is (= [] (:layers f)))
      (is (not (contains? f :frames))))))

;; ============================================================
;; Promote -- leaf-with-position plus a new position (P1-P3)
;; ============================================================
;;
;; When the receiver leaf already carries position (:x or :y in its
;; :mapping), another position-bearing sk/frame call promotes to a
;; composite. Aesthetic parts split to the composite's root; position
;; parts stay on the per-panel sub-frames.

(deftest promote-via-position-test
  (testing "P1 (guiding): identical positions -- two identical panels"
    (let [f (-> iris (sk/frame :a :b) (sk/frame :a :b))]
      (is (= 2 (count (:frames f))))
      (is (= {:x :a :y :b} (-> f :frames (nth 0) :mapping)))
      (is (= {:x :a :y :b} (-> f :frames (nth 1) :mapping)))
      (is (not (contains? f :mapping)))
      (is (not (contains? f :layers)))))

  (testing "P2: distinct positions -- two distinct panels"
    (let [f (-> iris (sk/frame :a :b) (sk/frame :c :d))]
      (is (= 2 (count (:frames f))))
      (is (= {:x :a :y :b} (-> f :frames (nth 0) :mapping)))
      (is (= {:x :c :y :d} (-> f :frames (nth 1) :mapping)))
      (is (not (contains? f :mapping)))))

  (testing "P3: aesthetic in first call splits to root; positions to panels"
    (let [f (-> iris (sk/frame :a :b {:color :species}) (sk/frame :c :d))]
      (is (= {:color :species} (:mapping f)))
      (is (= 2 (count (:frames f))))
      (is (= {:x :a :y :b} (-> f :frames (nth 0) :mapping)))
      (is (= {:x :c :y :d} (-> f :frames (nth 1) :mapping))))))

;; ============================================================
;; Promote -- aesthetic-only call on leaf-with-position (A1-A2)
;; ============================================================
;;
;; Aesthetic-only calls do not add a panel; they promote the leaf so
;; the aesthetic lives at composite root (flowing to every descendant
;; panel). Subsequent position calls append panels as usual.

(deftest promote-via-aesthetic-test
  (testing "A1: aesthetic-only on leaf-with-position promotes, no new panel"
    (let [f (-> iris (sk/frame :a :b) (sk/frame {:color :species}))]
      (is (= {:color :species} (:mapping f)))
      (is (= 1 (count (:frames f))))
      (is (= {:x :a :y :b} (-> f :frames (nth 0) :mapping)))))

  (testing "A2: aesthetic-then-position gives root aesthetic + two panels"
    (let [f (-> iris
                (sk/frame :a :b)
                (sk/frame {:color :species})
                (sk/frame :c :d))]
      (is (= {:color :species} (:mapping f)))
      (is (= 2 (count (:frames f))))
      (is (= {:x :a :y :b} (-> f :frames (nth 0) :mapping)))
      (is (= {:x :c :y :d} (-> f :frames (nth 1) :mapping))))))

;; ============================================================
;; Layer partitioning during promotion (L1-L4)
;; ============================================================
;;
;; Partition rule is single and self-describing: inspect each layer's
;; own :mapping. Layers carrying :x or :y ("panel-origin") stay with
;; sub-frame 1. Layers without :x/:y ("root-origin") move to the new
;; composite's root :layers -- they distribute to every panel via
;; resolve-tree.

(deftest layer-partitioning-test
  (testing "L1 (Pattern D): bare lay-point routes to root, flows to both"
    (let [f (-> iris (sk/frame :a :b) sk/lay-point (sk/frame :c :d))]
      (is (= 1 (count (:layers f))))
      (is (= :point (-> f :layers (nth 0) :layer-type)))
      (is (not (contains? (-> f :layers (nth 0)) :mapping)))
      (is (= 2 (count (:frames f))))
      (is (= [] (-> f :frames (nth 0) :layers)))
      (is (= [] (-> f :frames (nth 1) :layers)))))

  (testing "L2: position-matching lay-point stays in sub-frame 1"
    (let [f (-> iris
                (sk/frame :a :b)
                (sk/lay-point :a :b)
                (sk/frame :c :d))]
      (is (or (not (contains? f :layers))
              (= [] (:layers f))))
      (is (= 1 (count (-> f :frames (nth 0) :layers))))
      (is (= :point (-> f :frames (nth 0) :layers (nth 0) :layer-type)))
      (is (= [] (-> f :frames (nth 1) :layers)))))

  (testing "L3: mixed bare + position layers partition correctly"
    (let [f (-> iris
                (sk/frame :a :b)
                sk/lay-point
                (sk/lay-line :a :b)
                (sk/frame :c :d))]
      (is (= 1 (count (:layers f))))
      (is (= :point (-> f :layers (nth 0) :layer-type)))
      (is (= 1 (count (-> f :frames (nth 0) :layers))))
      (is (= :line (-> f :frames (nth 0) :layers (nth 0) :layer-type)))
      (is (= [] (-> f :frames (nth 1) :layers)))))

  (testing "L4: aesthetic-only on layer (no :x/:y) routes to root"
    (let [f (-> iris
                (sk/frame :a :b)
                (sk/lay-point {:color :species})
                (sk/frame :c :d))]
      (is (= 1 (count (:layers f))))
      (is (= {:color :species} (-> f :layers (nth 0) :mapping)))
      (is (= [] (-> f :frames (nth 0) :layers)))
      (is (= [] (-> f :frames (nth 1) :layers))))))

;; ============================================================
;; No-op on empty call (N1-N2)
;; ============================================================
;;
;; A 1-arity sk/frame on an existing leaf or composite returns the
;; input unchanged. (Distinct from (sk/frame data) where `data` is raw
;; -- that creates a new leaf; see C2.)

(deftest no-op-empty-call-test
  (testing "N1: (sk/frame leaf) returns leaf unchanged"
    (let [leaf (sk/frame iris :a :b)
          again (sk/frame leaf)]
      (is (= leaf again))))

  (testing "N2: (sk/frame composite) returns composite unchanged"
    (let [composite (-> iris (sk/frame :a :b) (sk/frame :c :d))
          again (sk/frame composite)]
      (is (= composite again)))))

;; ============================================================
;; Composite append -- third panel and beyond (K1)
;; ============================================================

(deftest composite-append-test
  (testing "K1: three sk/frame position calls in a row give three panels"
    (let [f (-> iris
                (sk/frame :a :b)
                (sk/frame :c :d)
                (sk/frame :e :f))]
      (is (= 3 (count (:frames f))))
      (is (= {:x :a :y :b} (-> f :frames (nth 0) :mapping)))
      (is (= {:x :c :y :d} (-> f :frames (nth 1) :mapping)))
      (is (= {:x :e :y :f} (-> f :frames (nth 2) :mapping))))))

;; ============================================================
;; Multi-pair sk/frame -- broadcast N panels in one call (M1-M6)
;; ============================================================
;;
;; (sk/frame fr [[:a :b] [:c :d]]) and (sk/frame fr [:a :b :c]) expand
;; to an iterated sequence of (sk/frame fr ...) calls, one per pair or
;; column. The result is equivalent to threading sk/frame N times.
;; This restores the panel-broadcast half of the old sk/view that was
;; dropped in slice 1. The canonical use is SPLOM:
;;
;;   (-> data (sk/frame {:color :species})
;;            sk/lay-point
;;            (sk/frame (sk/cross cols cols)))

(deftest multi-pair-raw-data-bivariate-test
  (testing "M1: (sk/frame data [[:a :b] [:c :d]]) -- vector of pairs"
    (let [f (sk/frame iris [[:a :b] [:c :d]])]
      (is (= 2 (count (:frames f))))
      (is (= {:x :a :y :b} (-> f :frames (nth 0) :mapping)))
      (is (= {:x :c :y :d} (-> f :frames (nth 1) :mapping))))))

(deftest multi-pair-raw-data-univariate-test
  (testing "M2: (sk/frame data [:a :b :c]) -- vector of columns"
    (let [f (sk/frame iris [:a :b :c])]
      (is (= 3 (count (:frames f))))
      (is (= {:x :a} (-> f :frames (nth 0) :mapping)))
      (is (= {:x :b} (-> f :frames (nth 1) :mapping)))
      (is (= {:x :c} (-> f :frames (nth 2) :mapping))))))

(deftest multi-pair-extend-existing-test
  (testing "M3: threaded onto a leaf-with-position -- promote + append"
    (let [f (-> iris (sk/frame :a :b) (sk/frame [[:c :d] [:e :f]]))]
      (is (= 3 (count (:frames f))))
      (is (= {:x :a :y :b} (-> f :frames (nth 0) :mapping)))
      (is (= {:x :c :y :d} (-> f :frames (nth 1) :mapping)))
      (is (= {:x :e :y :f} (-> f :frames (nth 2) :mapping))))))

(deftest multi-pair-root-layer-flows-test
  (testing "M4: root layer flows to every panel via resolve-tree"
    (let [f (-> iris sk/frame sk/lay-point (sk/frame [[:a :b] [:c :d]]))]
      (is (= 2 (count (:frames f))))
      (is (= 1 (count (:layers f))))
      (is (= :point (-> f :layers (nth 0) :layer-type))))))

(deftest multi-pair-root-aesthetic-splom-pattern-test
  (testing "M5: full SPLOM pattern -- root aesthetic + root layer + N panels"
    (let [f (-> iris
                (sk/frame {:color :species})
                sk/lay-point
                (sk/frame [[:a :b] [:c :d]]))]
      (is (= {:color :species} (:mapping f)))
      (is (= 2 (count (:frames f))))
      (is (= 1 (count (:layers f))))
      (is (= {:x :a :y :b} (-> f :frames (nth 0) :mapping)))
      (is (= {:x :c :y :d} (-> f :frames (nth 1) :mapping))))))

(deftest multi-pair-cross-utility-test
  (testing "M6: (sk/frame data (sk/cross cols cols)) builds a SPLOM grid"
    ;; (sk/cross cols cols) is the canonical SPLOM input -- an MxM
    ;; Cartesian rectangle. See the G1-G5 tests below for the full
    ;; grid-shape contract; here we pin that it is NOT the flat
    ;; composite (which was the slice-1 behaviour).
    (let [cols [:a :b]
          f (sk/frame iris (sk/cross cols cols))]
      (is (= 2 (count (:frames f))) "2 rows, not 4 flat sub-frames")
      (is (= :vertical (get-in f [:layout :direction])))
      (is (= #{:x :y} (:share-scales f))))))

(deftest multi-pair-iteration-equivalence-test
  (testing "M7: (sk/frame fr vec-of-pairs) -- non-rectangular pair list threads per-pair"
    ;; 3 pairs that do NOT form a Cartesian rectangle -- keeps flat
    ;; behaviour; equivalent to threading sk/frame three times.
    (let [expected (-> iris
                       (sk/frame :a :b)
                       (sk/frame :c :d)
                       (sk/frame :e :f))
          actual (sk/frame iris [[:a :b] [:c :d] [:e :f]])]
      (is (= expected actual)))))

;; ============================================================
;; Multi-pair sk/frame -- rectangular grid reshape (G1-G5)
;; ============================================================
;;
;; When multi-pair sk/frame receives pairs that form an M x N
;; Cartesian rectangle (every combination of unique first-elements
;; with unique second-elements, in cross-order), the result is a
;; nested composite: outer :vertical of M rows; each row is
;; :horizontal of N cells. :share-scales is stamped as #{:x :y} so
;; axes align across rows and columns -- the canonical SPLOM shape.
;; Pair lists that are not rectangular keep the flat-reduce
;; behaviour asserted in M7.

(deftest multi-pair-grid-splom-shape-test
  (testing "G1: (sk/cross cols cols) produces M x N nested composite"
    (let [f (sk/frame iris (sk/cross [:a :b] [:c :d]))]
      (is (= :vertical (get-in f [:layout :direction])))
      (is (= #{:x :y} (:share-scales f)))
      (is (= 2 (count (:frames f))) "2 rows")
      (let [row-0 (first (:frames f))
            row-1 (second (:frames f))]
        (is (= :horizontal (get-in row-0 [:layout :direction])))
        (is (= 2 (count (:frames row-0))) "2 cells per row")
        (is (= 2 (count (:frames row-1))))
        ;; row 0: x = :a, y varies [:c :d]
        (is (= {:x :a :y :c} (:mapping (first (:frames row-0)))))
        (is (= {:x :a :y :d} (:mapping (second (:frames row-0)))))
        ;; row 1: x = :b, y varies [:c :d]
        (is (= {:x :b :y :c} (:mapping (first (:frames row-1)))))
        (is (= {:x :b :y :d} (:mapping (second (:frames row-1)))))))))

(deftest multi-pair-grid-3x3-test
  (testing "G2: 3 x 3 SPLOM via sk/cross"
    (let [cols [:a :b :c]
          f (sk/frame iris (sk/cross cols cols))]
      (is (= 3 (count (:frames f))) "3 rows")
      (is (every? #(= 3 (count (:frames %))) (:frames f)) "3 cells each"))))

(deftest multi-pair-grid-root-aesthetic-test
  (testing "G3: SPLOM pattern -- root aesthetic + root layer + grid"
    (let [f (-> iris
                (sk/frame {:color :species})
                sk/lay-point
                (sk/frame (sk/cross [:a :b] [:c :d])))]
      (is (= {:color :species} (:mapping f))
          "root aesthetic preserved")
      (is (= 1 (count (:layers f)))
          "root layer preserved")
      (is (= :point (-> f :layers first :layer-type)))
      (is (= 2 (count (:frames f))) "2 rows")
      (is (every? #(= 2 (count (:frames %))) (:frames f)) "2 cells each"))))

(deftest multi-pair-grid-non-rectangular-falls-through-test
  (testing "G4: non-rectangular pair list keeps flat composite"
    ;; 2 pairs: [:a :b] [:c :d] -- would need [:a :d] [:c :b] for a 2x2
    ;; rectangle, so falls through to flat reduce.
    (let [f (sk/frame iris [[:a :b] [:c :d]])]
      (is (= 2 (count (:frames f))))
      ;; Flat composite -- each sub-frame is a leaf with mapping, no
      ;; nested :frames
      (is (every? #(not (contains? % :frames)) (:frames f))))))

(deftest multi-pair-grid-1xN-falls-through-test
  (testing "G5: a 1xN shape does not grid-reshape"
    ;; (sk/cross [:a] [:b :c :d]) gives 3 pairs but only 1 unique x --
    ;; 1 row. We require at least 2 rows and 2 cols to reshape.
    (let [f (sk/frame iris (sk/cross [:a] [:b :c :d]))]
      ;; Falls through to flat -- 3 sub-frames at root
      (is (= 3 (count (:frames f))))
      (is (every? #(not (contains? % :frames)) (:frames f)))
      (is (not (contains? f :share-scales))))))

;; ============================================================
;; Integration with sk/lay-* and resolve-tree (I1-I3)
;; ============================================================

(deftest integration-lay-resolve-test
  (testing "I1: root-origin layer reaches both panels through resolve-tree"
    (let [f (-> iris (sk/frame :a :b) sk/lay-point (sk/frame :c :d))
          leaves (frame/resolve-tree f)]
      (is (= 2 (count leaves)))
      (is (every? (fn [leaf] (= 1 (count (:layers leaf)))) leaves)
          "each resolved leaf carries the root layer")))

  (testing "I2: panel-specific layer does not leak across panels"
    (let [f (-> iris
                (sk/frame :a :b)
                (sk/lay-point :a :b)
                (sk/frame :c :d))
          leaves (frame/resolve-tree f)
          layers-per-leaf (mapv #(count (:layers %)) leaves)]
      (is (= [1 0] layers-per-leaf)
          "leaf 1 has the layer; leaf 2 does not")))

  (testing "I3: root aesthetic merges into both resolved leaves' mappings"
    (let [f (-> iris
                (sk/frame :a :b)
                (sk/frame {:color :species})
                (sk/frame :c :d))
          leaves (frame/resolve-tree f)]
      (is (= 2 (count leaves)))
      (is (every? (fn [leaf] (= :species (get-in leaf [:mapping :color])))
                  leaves)))))

;; ============================================================
;; Property-style checks
;; ============================================================

(deftest no-empty-map-output-property-test
  (testing "No frame or layer in the resulting tree carries {} for mapping or opts"
    (is (no-empty-map-output? (sk/frame)))
    (is (no-empty-map-output? (sk/frame iris)))
    (is (no-empty-map-output? (sk/frame iris :a :b)))
    (is (no-empty-map-output? (-> iris (sk/frame :a :b) (sk/frame :c :d))))
    (is (no-empty-map-output? (-> iris (sk/frame :a :b) sk/lay-point)))
    (is (no-empty-map-output?
         (-> iris (sk/frame :a :b) sk/lay-point (sk/frame :c :d))))))

(deftest frames-preserve-call-order-test
  (testing "Sub-frames appear in left-to-right sk/frame invocation order"
    (let [f (-> iris
                (sk/frame :a :b)
                (sk/frame :c :d)
                (sk/frame :e :f))]
      (is (= [{:x :a :y :b} {:x :c :y :d} {:x :e :y :f}]
             (mapv :mapping (:frames f)))))))

(deftest promotion-shape-idempotence-test
  (testing "Threading sk/frame three times equals threading twice + once"
    (let [three-in-a-row (-> iris
                             (sk/frame :a :b)
                             (sk/frame :c :d)
                             (sk/frame :e :f))
          built-in-two   (let [two (-> iris
                                       (sk/frame :a :b)
                                       (sk/frame :c :d))]
                           (sk/frame two :e :f))]
      (is (= three-in-a-row built-in-two)))))

;; ============================================================
;; Layer position vs frame position -- overlay semantics
;; ============================================================
;;
;; When a leaf already carries position and `sk/lay-*` is called with
;; a non-matching position, the new position lives on the LAYER's
;; own `:mapping` rather than creating a new panel. At render, the
;; layer's position overrides the frame's via merge -- an overlay on
;; the same panel. This differs from the old Sketch-world behavior
;; (where a non-matching position would have created a new view /
;; panel); in the frame world, adding a panel is an explicit `sk/frame`
;; call. Pinned here so the next slice can't silently change it.

(deftest leaf-layer-non-matching-position-test
  (testing "leaf with position, lay-* with non-matching position -- overlay"
    (let [f (-> iris
                (sk/frame :a :b)
                (sk/lay-point :c :d))]
      (is (= {:x :a :y :b} (:mapping f))
          "leaf's own mapping is unchanged")
      (is (= 1 (count (:layers f)))
          "one layer added, no panel added")
      (is (= {:x :c :y :d} (-> f :layers (nth 0) :mapping))
          "the non-matching position lives on the layer's :mapping")
      (is (not (contains? f :frames))
          "no promotion to composite"))))

;; ============================================================
;; Layer structural keys -- :stat, :position, :mark (Decision 1)
;; ============================================================
;;
;; :layer-type, :stat, :position, and explicit :mark are first-class
;; sibling keys on the layer map. :mapping is reserved for
;; column-to-aesthetic bindings only.

(deftest layer-structural-keys-test
  (testing ":stat is a sibling key, not inside :mapping"
    (let [layer (-> (sk/frame iris :a :b)
                    (sk/lay-smooth {:stat :linear-model})
                    :layers
                    first)]
      (is (= :linear-model (:stat layer)))
      (is (not (contains? (or (:mapping layer) {}) :stat)))))

  (testing ":position is a sibling key, not inside :mapping"
    (let [layer (-> (sk/frame iris :a :b)
                    (sk/lay-bar {:position :dodge})
                    :layers
                    first)]
      (is (= :dodge (:position layer)))
      (is (not (contains? (or (:mapping layer) {}) :position)))))

  (testing ":mark (user override) is a sibling key, not inside :mapping"
    (let [layer (-> (sk/frame iris :a :b)
                    (sk/lay-point {:mark :line})
                    :layers
                    first)]
      (is (= :line (:mark layer)))
      (is (not (contains? (or (:mapping layer) {}) :mark)))))

  (testing "aesthetic keys stay in :mapping"
    (let [layer (-> (sk/frame iris :a :b)
                    (sk/lay-point {:color :species :alpha 0.5})
                    :layers
                    first)]
      (is (= {:color :species :alpha 0.5} (:mapping layer)))
      (is (not (contains? layer :color)))
      (is (not (contains? layer :alpha))))))
