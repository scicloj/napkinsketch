(ns scicloj.napkinsketch.impl.frame-draft-test
  "Specification for Phase 6 slice-2's frame-native leaf->draft.

   The old pipeline routes a leaf frame through leaf-frame->sketch ->
   sketch->draft. That detour is replaced by frame/leaf->draft, which
   reads the leaf directly and produces the same draft shape.

   These tests assert equivalence with sketch->draft on the inputs the
   adapter handles, and pin the documented bug (edge case B) that the
   adapter path cannot cover: a leaf with no position in its own
   :mapping but layers that carry :x/:y. Old path: empty draft. New
   path: one draft entry per layer, :x/:y sourced from the layer."
  (:require [clojure.test :refer [deftest testing is]]
            [tablecloth.api :as tc]
            [scicloj.napkinsketch.impl.frame :as frame]
            [scicloj.napkinsketch.impl.sketch :as sketch]))

(def iris
  (tc/dataset {:a [1.0 2.0 3.0]
               :b [0.5 1.0 1.5]
               :species ["setosa" "setosa" "versicolor"]}))

(defn- scrub-adapter-keys
  "Drop keys the adapter stamps that the frame-native path does not
   (or does differently). Compared shape is the data-bearing merged
   draft body."
  [m]
  (dissoc m :__entry-idx :__sketch-scope))

(defn- scrub-draft [draft]
  (mapv scrub-adapter-keys draft))

(defn- drafts-equivalent?
  "sketch->draft and frame/leaf->draft produce the same draft modulo
   the adapter-only bookkeeping keys."
  [leaf]
  (let [old-draft (scrub-draft (sketch/sketch->draft (sketch/leaf-frame->sketch leaf)))
        new-draft (scrub-draft (frame/leaf->draft leaf))]
    (= old-draft new-draft)))

;; ============================================================
;; Equivalence with sketch->draft on adapter-supported shapes
;; ============================================================

(deftest equivalence-leaf-with-position-single-layer-test
  (testing "leaf :x/:y + one bare layer"
    (is (drafts-equivalent?
         {:data iris
          :mapping {:x :a :y :b}
          :layers [{:layer-type :point :mapping {}}]}))))

(deftest equivalence-leaf-with-position-and-aesthetic-test
  (testing "frame-level :color flows into the layer"
    (is (drafts-equivalent?
         {:data iris
          :mapping {:x :a :y :b :color :species}
          :layers [{:layer-type :point :mapping {}}]}))))

(deftest equivalence-layer-overrides-frame-test
  (testing "layer mapping overrides frame mapping on shared keys"
    (is (drafts-equivalent?
         {:data iris
          :mapping {:x :a :y :b :color :species}
          :layers [{:layer-type :point :mapping {:color :a}}]}))))

(deftest equivalence-multiple-layers-test
  (testing "each layer becomes one draft entry"
    (is (drafts-equivalent?
         {:data iris
          :mapping {:x :a :y :b}
          :layers [{:layer-type :point :mapping {}}
                   {:layer-type :line  :mapping {}}]}))))

(deftest equivalence-layer-structural-keys-test
  (testing ":stat/:position/:mark siblings on a layer carry through"
    (is (drafts-equivalent?
         {:data iris
          :mapping {:x :a :y :b}
          :layers [{:layer-type :smooth :mapping {} :stat :linear-model}]}))
    (is (drafts-equivalent?
         {:data iris
          :mapping {:x :a :y :b}
          :layers [{:layer-type :bar :mapping {} :position :dodge}]}))
    (is (drafts-equivalent?
         {:data iris
          :mapping {:x :a :y :b}
          :layers [{:layer-type :point :mapping {} :mark :line}]}))))

(deftest equivalence-empty-layers-test
  (testing "no layers -> a single {:mark :infer} draft entry"
    (is (drafts-equivalent?
         {:data iris
          :mapping {:x :a :y :b}
          :layers []}))))

(deftest equivalence-opts-plot-level-test
  (testing ":x-scale/:y-scale/:coord on leaf opts stamp on each draft entry"
    (is (drafts-equivalent?
         {:data iris
          :mapping {:x :a :y :b}
          :layers [{:layer-type :point :mapping {}}]
          :opts   {:x-scale {:type :log}
                   :y-scale {:type :linear}
                   :coord   :flip}}))))

(deftest equivalence-aesthetic-only-frame-annotation-layer-test
  (testing "frame with no :x/:y + an annotation layer (rule-h) renders one panel"
    ;; Old adapter path synthesizes an empty view for annotation-only
    ;; sketches. Frame-native path just emits the annotation directly
    ;; with :__entry-idx 0. plan.clj treats both as an annotation-only
    ;; group and synthesizes a domain.
    (is (drafts-equivalent?
         {:data iris
          :layers [{:layer-type :rule-h :mapping {:y-intercept 1.0}}]}))))

;; ============================================================
;; Edge case B: layers carry position, frame :mapping does not
;; ============================================================
;;
;; sketch/leaf-frame->sketch treats this as a sketch with empty mapping
;; and sketch-level layers, and sketch->draft emits no drafts because
;; there are no views. Frame-native leaf->draft produces one entry per
;; layer with :x/:y sourced from the layer, matching the behavior a
;; user would expect of the frame model. Pinned here so the reroute
;; keeps this in the spec.

(deftest edge-case-b-layer-carries-position-test
  (testing "leaf has no :x/:y; layer has :x/:y -- one draft entry per layer"
    (let [leaf {:data iris
                :layers [{:layer-type :point :mapping {:x :a :y :b}}]}
          draft (frame/leaf->draft leaf)]
      (is (= 1 (count draft)))
      (is (= :a (:x (first draft))))
      (is (= :b (:y (first draft))))
      (is (= :point (:mark (first draft)))))))

(deftest edge-case-b-multiple-layers-test
  (testing "two layers both carrying position -- two draft entries"
    (let [leaf {:data iris
                :layers [{:layer-type :point :mapping {:x :a :y :b}}
                         {:layer-type :line  :mapping {:x :a :y :b}}]}
          draft (frame/leaf->draft leaf)]
      (is (= 2 (count draft)))
      (is (every? #(= :a (:x %)) draft))
      (is (every? #(= :b (:y %)) draft)))))
