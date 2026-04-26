(ns scicloj.plotje.facet-scales-test
  "Tests for facet scale coordination (the :scales option).

   Default behavior is :shared -- all facet panels carry the same
   x-domain and y-domain so panels are visually comparable. The
   :scales opt overrides per axis: :free-y (x shared, y per-panel),
   :free-x (y shared, x per-panel), :free (both per-panel).

   Coordination only fires for :facet-grid layouts. Multi-variable
   composite layouts keep per-panel domains because aggregating across
   different x/y columns is meaningless."
  (:require [clojure.test :refer [deftest testing is]]
            [scicloj.plotje.api :as pj]))

(def small
  ;; Three groups with deliberately different x and y ranges.
  ;; Group A: x [1 3], y [10 30]
  ;; Group B: x [4 6], y [40 60]
  ;; Group C: x [7 9], y [70 90]
  {:x [1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0]
   :y [10.0 20.0 30.0 40.0 50.0 60.0 70.0 80.0 90.0]
   :g ["A" "A" "A" "B" "B" "B" "C" "C" "C"]})

(defn- domains [pose]
  (let [pl (pj/plan pose)
        ps (:panels pl)]
    {:x (mapv :x-domain ps)
     :y (mapv :y-domain ps)}))

(deftest default-scales-share-both-axes
  (testing "default :scales is :shared -- all panels carry one x-domain and one y-domain"
    (let [pose (-> small (pj/lay-point :x :y) (pj/facet :g))
          {:keys [x y]} (domains pose)]
      (is (= 3 (count x)))
      (is (apply = x) "x-domains identical across panels")
      (is (apply = y) "y-domains identical across panels")
      ;; Aggregate covers full data range.
      (let [[x-lo x-hi] (first x)
            [y-lo y-hi] (first y)]
        (is (<= x-lo 1.0))
        (is (>= x-hi 9.0))
        (is (<= y-lo 10.0))
        (is (>= y-hi 90.0))))))

(deftest scales-free-keeps-per-panel-domains
  (testing ":scales :free disables coordination on both axes"
    (let [pose (-> small
                   (pj/lay-point :x :y)
                   (pj/facet :g)
                   (pj/options {:scales :free}))
          {:keys [x y]} (domains pose)]
      (is (= 3 (count x)))
      (is (= 3 (count (distinct x))) "x-domains differ per panel")
      (is (= 3 (count (distinct y))) "y-domains differ per panel"))))

(deftest scales-free-x-y-shared
  (testing ":scales :free-x lets x vary, y stays shared"
    (let [pose (-> small
                   (pj/lay-point :x :y)
                   (pj/facet :g)
                   (pj/options {:scales :free-x}))
          {:keys [x y]} (domains pose)]
      (is (= 3 (count (distinct x))) "x differs per panel")
      (is (apply = y) "y identical across panels"))))

(deftest scales-free-y-x-shared
  (testing ":scales :free-y lets y vary, x stays shared"
    (let [pose (-> small
                   (pj/lay-point :x :y)
                   (pj/facet :g)
                   (pj/options {:scales :free-y}))
          {:keys [x y]} (domains pose)]
      (is (apply = x) "x identical across panels")
      (is (= 3 (count (distinct y))) "y differs per panel"))))

(deftest scales-shared-explicit-equals-default
  (testing "explicit :scales :shared matches the default"
    (let [base   (-> small (pj/lay-point :x :y) (pj/facet :g))
          d-def  (domains base)
          d-shar (domains (pj/options base {:scales :shared}))]
      (is (= d-def d-shar)))))

(deftest user-scale-domain-wins-over-coordination
  (testing "explicit (pj/scale :x {:domain ...}) overrides coordination"
    (let [pose (-> small
                   (pj/lay-point :x :y)
                   (pj/facet :g)
                   (pj/scale :x {:domain [0 100]}))
          {:keys [x]} (domains pose)]
      (is (apply = x) "still shared")
      (is (= [0 100] (first x)) "user-set domain wins"))))

(deftest multi-pair-composite-not-coordinated
  (testing "multi-pair poses become composites; coordination does not apply"
    ;; Two-pair pose: panel 1 uses :x-A,:y-A; panel 2 uses :x-B,:y-B.
    ;; This goes through the compositor (a separate path from
    ;; faceting) and is unaffected by the :scales opt or by
    ;; coordinate-facet-domains.
    (let [data {:x-A [1.0 2.0 3.0]
                :y-A [10.0 20.0 30.0]
                :x-B [100.0 200.0 300.0]
                :y-B [1000.0 2000.0 3000.0]}
          pose (-> (pj/pose data [[:x-A :y-A] [:x-B :y-B]])
                   pj/lay-point)
          pl   (pj/plan pose)]
      (is (:composite? pl) "multi-pair becomes a composite plan")
      (let [sub-domains (mapv #(get-in % [:plan :panels 0 :x-domain])
                              (:sub-plots pl))]
        (is (= 2 (count sub-domains)))
        (is (not= (first sub-domains) (second sub-domains))
            "each sub-plot keeps its own column-derived x-domain")))))

(deftest single-panel-coordination-noop
  (testing "non-faceted single panel is unaffected"
    (let [pose (-> small (pj/lay-point :x :y))
          pl   (pj/plan pose)
          ps   (:panels pl)]
      (is (= 1 (count ps))))))
