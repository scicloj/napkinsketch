(ns scicloj.napkinsketch.frame-api-test
  "Tests for sk/frame public constructor and its equivalence to the
   legacy sk/sketch + sk/view combination. Phase 3a of the pre-alpha
   refactor: sk/frame produces a leaf-frame plain map; rendering
   routes through a leaf-frame adapter."
  (:require [clojure.test :refer [deftest testing is]]
            [tablecloth.api :as tc]
            [scicloj.napkinsketch.api :as sk]))

(def tiny-ds
  (tc/dataset {:x [1.0 2.0 3.0 4.0 5.0]
               :y [2.0 4.0 1.0 5.0 3.0]
               :g ["a" "a" "b" "b" "b"]}))

;; ============================================================
;; sk/frame constructor shape
;; ============================================================

(deftest frame-constructor-test
  (testing "0-arity makes an empty leaf frame"
    (let [f (sk/frame)]
      (is (sk/frame? f))
      (is (not (sk/sketch? f)))
      (is (nil? (:data f)))
      (is (= {} (:mapping f)))
      (is (= [] (:layers f)))))

  (testing "1-arity coerces data, no mapping"
    (let [f (sk/frame {:x [1 2] :y [3 4]})]
      (is (sk/frame? f))
      (is (tc/dataset? (:data f)))
      (is (= {} (:mapping f)))))

  (testing "2-arity with a map arg is an aesthetic mapping"
    (let [f (sk/frame tiny-ds {:color :g})]
      (is (= {:color :g} (:mapping f)))))

  (testing "2-arity with a keyword arg sets :x only"
    (let [f (sk/frame tiny-ds :x)]
      (is (= {:x :x} (:mapping f)))))

  (testing "3-arity sets both :x and :y"
    (let [f (sk/frame tiny-ds :x :y)]
      (is (= {:x :x :y :y} (:mapping f))))))

(deftest frame-predicate-test
  (testing "frame? rejects sketches"
    (is (not (sk/frame? (sk/sketch))))
    (is (not (sk/frame? (sk/sketch tiny-ds)))))

  (testing "sketch? rejects frames"
    (is (not (sk/sketch? (sk/frame))))
    (is (not (sk/sketch? (sk/frame tiny-ds :x :y))))))

;; ============================================================
;; Equivalence to the legacy path
;; ============================================================
;;
;; The phase-3a adapter promises that a leaf frame renders to the
;; same SVG as the equivalent (sketch + view + lay-*) construction.
;; These tests compare plans (structural) rather than SVG strings
;; (which carry nondeterministic ids).

(defn- same-plan-shape? [p1 p2]
  (and (= (:grid p1) (:grid p2))
       (= (count (:panels p1)) (count (:panels p2)))
       (= (mapv :layers (:panels p1))
          (mapv :layers (:panels p2)))))

(deftest equivalence-bivariate-test
  (testing "sk/frame :x :y -> lay-point matches sk/view :x :y -> sk/lay-point"
    (let [via-frame  (-> (sk/frame tiny-ds :x :y) sk/lay-point sk/plan)
          via-view   (-> tiny-ds (sk/view :x :y) sk/lay-point sk/plan)]
      (is (same-plan-shape? via-frame via-view)))))

(deftest equivalence-aesthetic-mapping-test
  (testing "sk/frame with aesthetic mapping matches sk/sketch"
    (let [via-frame  (-> (sk/frame tiny-ds {:color :g})
                         (sk/lay-point :x :y) sk/plan)
          via-sketch (-> (sk/sketch tiny-ds {:color :g})
                         (sk/lay-point :x :y) sk/plan)]
      (is (same-plan-shape? via-frame via-sketch)))))

(deftest equivalence-layers-before-view-test
  (testing "lay-point with explicit columns on a frame is the one-shot form"
    (let [via-frame  (-> tiny-ds
                         sk/frame
                         (sk/lay-point :x :y)
                         sk/plan)
          via-legacy (-> tiny-ds
                         (sk/lay-point :x :y)
                         sk/plan)]
      (is (same-plan-shape? via-frame via-legacy)))))

;; ============================================================
;; Composite frames are not yet supported (Phase 4)
;; ============================================================

(deftest composite-rejects-test
  (testing "sk/plot on a composite frame throws a clear Phase-4 error"
    (let [composite {:frames [{:layers []} {:layers []}]}]
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Composite frames"
                            (sk/plot composite))))))
