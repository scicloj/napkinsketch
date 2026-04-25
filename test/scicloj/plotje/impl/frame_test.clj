(ns scicloj.plotje.impl.frame-test
  "Unit tests for impl.frame. The substrate is not wired into the
   public API yet (Phase 3 does that); these tests exercise the tree
   operations in isolation."
  (:require [clojure.test :refer [deftest testing is]]
            [tablecloth.api :as tc]
            [scicloj.plotje.impl.frame :as frame]))

;; ============================================================
;; Structural predicates
;; ============================================================

(deftest predicates-test
  (testing "frame? requires :layers or :frames"
    (is (frame/frame? {:layers []}))
    (is (frame/frame? {:layers [{:layer-type :point}]}))
    (is (frame/frame? {:frames []}))
    (is (frame/frame? {:frames [{:layers []}]}))
    (is (not (frame/frame? {})))
    (is (not (frame/frame? {:data {:x [1]}})))
    (is (not (frame/frame? "not a map"))))

  (testing "leaf? = no non-empty :frames"
    (is (frame/leaf? {:layers [{:layer-type :point}]}))
    (is (frame/leaf? {:frames []})
        "An empty :frames vector is effectively a leaf")
    (is (not (frame/leaf? {:frames [{:layers []}]}))))

  (testing "composite? is the inverse of leaf?"
    (is (frame/composite? {:frames [{:layers []}]}))
    (is (not (frame/composite? {:layers [{:layer-type :point}]})))))

;; ============================================================
;; resolve-tree
;; ============================================================

(deftest resolve-tree-leaf-test
  (testing "a single leaf resolves to itself with :path []"
    (let [leaf {:layers [{:layer-type :point}]}
          [r] (frame/resolve-tree leaf)]
      (is (= [] (:path r)))
      (is (= [{:layer-type :point}] (:layers r)))
      (is (= {} (:mapping r)))
      (is (nil? (:data r)))
      (is (= {} (:opts r))))))

(deftest resolve-tree-context-inheritance-test
  (testing ":data, :mapping, :layers, :opts inherit top-down"
    (let [ds {:x [1 2 3]}
          tree {:data    ds
                :mapping {:color :species}
                :opts    {:title "outer"}
                :layers  [{:layer-type :lay-a}]
                :frames  [{:layers [{:layer-type :lay-b}]}
                          {:data    {:x [10 20]}
                           :mapping {:color :override
                                     :size  :other}
                           :opts    {:title "inner"}
                           :layers  [{:layer-type :lay-c}]}]}
          leaves (frame/resolve-tree tree)]
      (is (= 2 (count leaves)))

      (testing "first leaf inherits everything"
        (let [l1 (first leaves)]
          (is (= [0] (:path l1)))
          (is (= ds (:data l1)))
          (is (= {:color :species} (:mapping l1)))
          (is (= {:title "outer"} (:opts l1)))
          (is (= [{:layer-type :lay-a} {:layer-type :lay-b}] (:layers l1)))))

      (testing "second leaf overrides data/mapping/opts and accumulates layers"
        (let [l2 (second leaves)]
          (is (= [1] (:path l2)))
          (is (= {:x [10 20]} (:data l2)))
          (is (= {:color :override :size :other} (:mapping l2)))
          (is (= {:title "inner"} (:opts l2)))
          (is (= [{:layer-type :lay-a} {:layer-type :lay-c}] (:layers l2))))))))

(deftest resolve-tree-preserves-extras-test
  (testing "non-structural keys on a leaf pass through to the resolved leaf"
    (let [tree {:frames [{:layers [{:layer-type :point}]
                          :panel-label "row=a, col=b"}]}
          [l] (frame/resolve-tree tree)]
      (is (= "row=a, col=b" (:panel-label l))))))

(deftest resolve-tree-deep-nesting-test
  (testing "paths accumulate through arbitrary depth"
    (let [tree {:frames [{:frames [{:frames [{:layers [{:layer-type :point}]}]}]}]}
          [l] (frame/resolve-tree tree)]
      (is (= [0 0 0] (:path l))))))

;; ============================================================
;; compute-layout
;; ============================================================

(deftest compute-layout-leaf-test
  (testing "a leaf gets the bounding rectangle verbatim"
    (let [leaf {:layers []}]
      (is (= {[] [0.0 0.0 100.0 50.0]}
             (frame/compute-layout leaf [0 0 100 50]))))))

(deftest compute-layout-horizontal-equal-weights-test
  (testing "two children, horizontal, default equal weights"
    (let [tree {:frames [{:layers []} {:layers []}]}
          layout (frame/compute-layout tree [0 0 100 50])]
      (is (= [0.0 0.0 50.0 50.0] (layout [0])))
      (is (= [50.0 0.0 50.0 50.0] (layout [1]))))))

(deftest compute-layout-vertical-weighted-test
  (testing "two children, vertical, 3:1 weights"
    (let [tree {:layout {:direction :vertical :weights [3 1]}
                :frames [{:layers []} {:layers []}]}
          layout (frame/compute-layout tree [0 0 100 400])]
      (is (= [0.0 0.0 100.0 300.0] (layout [0])))
      (is (= [0.0 300.0 100.0 100.0] (layout [1]))))))

(deftest compute-layout-rejects-nonpositive-weights-test
  (testing "weights summing to zero throw"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"must sum to a positive number"
                          (frame/compute-layout {:layout {:weights [0 0]}
                                                 :frames [{:layers []} {:layers []}]}
                                                [0 0 100 100])))))

(deftest compute-layout-nested-test
  (testing "nested composite: outer vertical, inner row horizontal"
    (let [tree {:layout {:direction :vertical :weights [1 3]}
                :frames [{:layers []} ; 25% top
                         {:layout {:direction :horizontal :weights [3 1]}
                          :frames [{:layers []} {:layers []}]}]}
          layout (frame/compute-layout tree [0 0 400 400])]
      ;; top band: full width, top quarter
      (is (= [0.0 0.0 400.0 100.0] (layout [0])))
      ;; bottom-left: 75% width × 75% height offset
      (is (= [0.0 100.0 300.0 300.0] (layout [1 0])))
      ;; bottom-right
      (is (= [300.0 100.0 100.0 300.0] (layout [1 1]))))))

;; ============================================================
;; Tree utilities
;; ============================================================

(deftest last-leaf-path-test
  (testing "root leaf has empty path"
    (is (= [] (frame/last-leaf-path {:layers []}))))

  (testing "composites walk to the rightmost leaf"
    (is (= [1] (frame/last-leaf-path
                {:frames [{:layers []} {:layers []}]})))
    (is (= [1 0] (frame/last-leaf-path
                  {:frames [{:layers []}
                            {:frames [{:layers []}]}]}))))

  (testing "empty-frames vector counts as a leaf (path = [])"
    (is (= [] (frame/last-leaf-path {:frames []})))))

(deftest leaf-at-test
  (let [tree {:frames [{:layers [{:layer-type :point}]}
                       {:frames [{:layers [{:layer-type :line}]}]}]}]
    (testing "path lands on a leaf"
      (is (= :point (-> (frame/leaf-at tree [0]) :layers first :layer-type)))
      (is (= :line (-> (frame/leaf-at tree [1 0]) :layers first :layer-type))))

    (testing "path lands on a composite -> nil"
      (is (nil? (frame/leaf-at tree [1]))))))

(deftest path->update-in-path-test
  (testing "empty path translates to empty"
    (is (= [] (frame/path->update-in-path []))))
  (testing "single-level path interleaves :frames"
    (is (= [:frames 0] (frame/path->update-in-path [0]))))
  (testing "deep paths interleave :frames at every level"
    (is (= [:frames 2 :frames 1 :frames 0]
           (frame/path->update-in-path [2 1 0])))))

(deftest last-matching-leaf-path-test
  (testing "two leaves with identical :x/:y match -- picks the last (Rule 6)"
    (is (= [1]
           (frame/last-matching-leaf-path
            {:frames [{:layers [] :mapping {:x :a :y :b}}
                      {:layers [] :mapping {:x :a :y :b}}]}
            {:x :a :y :b}))))

  (testing "no matching leaf returns nil"
    (is (nil? (frame/last-matching-leaf-path
               {:frames [{:layers [] :mapping {:x :c :y :d}}]}
               {:x :a :y :b}))))

  (testing "keyword/string tolerance -- :x matches \"x\" on either side"
    (is (= [0] (frame/last-matching-leaf-path
                {:frames [{:layers [] :mapping {:x "a" :y "b"}}]}
                {:x :a :y :b})))
    (is (= [0] (frame/last-matching-leaf-path
                {:frames [{:layers [] :mapping {:x :a :y :b}}]}
                {:x "a" :y "b"}))))

  (testing "ancestor :mapping inherits into a bare leaf (resolve-tree merge)"
    (is (= [0] (frame/last-matching-leaf-path
                {:mapping {:x :a :y :b}
                 :frames  [{:layers []}]}
                {:x :a :y :b}))))

  (testing "DFS order across different depths"
    (is (= [1 0] (frame/last-matching-leaf-path
                  {:frames [{:layers [] :mapping {:x :a :y :b}}
                            {:frames [{:layers [] :mapping {:x :a :y :b}}]}]}
                  {:x :a :y :b}))))

  (testing "nil position components match bare leaves (no :x/:y mapping)"
    (is (= [0] (frame/last-matching-leaf-path
                {:frames [{:layers []}]}
                {:x nil :y nil})))))

;; ============================================================
;; inject-shared-scales
;; ============================================================
;;
;; The high-value primitive. Three scenarios cover the behavior that
;; the nested-frames PoC validated:
;; - shared axis with the same column across leaves -> union domain
;; - shared axis with DIFFERENT columns across leaves -> independent buckets
;; - composite without :share-scales -> no injection

(def iris-like
  (tc/dataset {:sepal-length [4.0 5.0 6.0 7.0 8.0]
               :sepal-width  [2.0 3.0 3.5 4.0 4.5]
               :species      ["a" "a" "b" "b" "c"]}))

(deftest inject-shared-scales-no-share-test
  (testing "composite without :share-scales leaves leaves untouched"
    (let [tree {:data iris-like
                :frames [{:layers [{:layer-type :point
                                    :mapping {:x :sepal-length}}]}
                         {:layers [{:layer-type :point
                                    :mapping {:x :sepal-length}}]}]}
          injected (frame/inject-shared-scales tree)
          leaves (frame/resolve-tree injected)]
      (is (every? #(nil? (:x-scale-domain (:opts %))) leaves)))))

(deftest inject-shared-scales-same-column-test
  (testing "leaves sharing a column get a union x domain"
    (let [tree {:share-scales #{:x}
                :frames [{:data (tc/dataset {:sepal-length [1.0 2.0 3.0]})
                          :layers [{:layer-type :point
                                    :mapping {:x :sepal-length}}]}
                         {:data (tc/dataset {:sepal-length [10.0 20.0]})
                          :layers [{:layer-type :point
                                    :mapping {:x :sepal-length}}]}]}
          leaves (frame/resolve-tree (frame/inject-shared-scales tree))]
      (is (= 2 (count leaves)))
      (is (= [1.0 20.0] (:x-scale-domain (:opts (first leaves)))))
      (is (= [1.0 20.0] (:x-scale-domain (:opts (second leaves))))))))

(deftest inject-shared-scales-column-bucketed-test
  (testing "leaves using different columns for the same axis get different buckets"
    (let [tree {:data iris-like
                :share-scales #{:x}
                :frames [{:layers [{:layer-type :point
                                    :mapping {:x :sepal-length}}]}
                         {:layers [{:layer-type :point
                                    :mapping {:x :sepal-width}}]}]}
          [l1 l2] (frame/resolve-tree (frame/inject-shared-scales tree))]
      (is (= [4.0 8.0] (:x-scale-domain (:opts l1)))
          "first leaf gets the sepal-length domain")
      (is (= [2.0 4.5] (:x-scale-domain (:opts l2)))
          "second leaf gets the sepal-width domain (not unioned with sepal-length)"))))

(deftest inject-shared-scales-nested-test
  (testing "SPLOM-like: outer :x :y share, inner rows and columns each get their own buckets"
    (let [ds (tc/dataset {:a [1.0 2.0 3.0]
                          :b [10.0 20.0 30.0]
                          :c [100.0 200.0 300.0]})
          tree {:data ds
                :share-scales #{:x :y}
                :layout {:direction :vertical}
                :frames [{:layout {:direction :horizontal}
                          :frames [{:layers [{:layer-type :point :mapping {:x :a :y :a}}]}
                                   {:layers [{:layer-type :point :mapping {:x :b :y :a}}]}]}
                         {:layout {:direction :horizontal}
                          :frames [{:layers [{:layer-type :point :mapping {:x :a :y :b}}]}
                                   {:layers [{:layer-type :point :mapping {:x :b :y :b}}]}]}]}
          leaves (frame/resolve-tree (frame/inject-shared-scales tree))]
      (is (= 4 (count leaves)))
      (is (every? #(= [1.0 3.0] (:x-scale-domain (:opts %)))
                  (filter #(= :a (get-in % [:layers 0 :mapping :x])) leaves))
          "leaves with :x :a share the :a domain")
      (is (every? #(= [10.0 30.0] (:x-scale-domain (:opts %)))
                  (filter #(= :b (get-in % [:layers 0 :mapping :x])) leaves))
          "leaves with :x :b share the :b domain")
      (is (every? #(= [1.0 3.0] (:y-scale-domain (:opts %)))
                  (filter #(= :a (get-in % [:layers 0 :mapping :y])) leaves))
          "leaves with :y :a share the :a domain")
      (is (every? #(= [10.0 30.0] (:y-scale-domain (:opts %)))
                  (filter #(= :b (get-in % [:layers 0 :mapping :y])) leaves))
          "leaves with :y :b share the :b domain"))))

(deftest inject-shared-scales-stat-driven-y-test
  (testing "leaves whose y axis is stat-driven (count/density) skip the y-domain stamp"
    (let [ds (tc/dataset {:a [1.0 2.0 3.0]
                          :b [10.0 20.0 30.0]})
          ;; SPLOM where one leaf is an explicit histogram on the y=:b
          ;; column. The other y=:b leaf is a scatter -- it should
          ;; still get the shared :b domain. The histogram leaf
          ;; should NOT, since its rendered y-axis is a count axis.
          tree {:data ds
                :share-scales #{:y}
                :frames [{:layers [{:layer-type :point :mapping {:x :a :y :b}}]}
                         {:layers [{:layer-type :histogram :mapping {:x :b}}]
                          ;; Force the histogram leaf into the :y :b
                          ;; bucket via its frame mapping; the layer
                          ;; itself still stat-bins on x.
                          :mapping {:y :b}}]}
          [scatter hist] (frame/resolve-tree (frame/inject-shared-scales tree))]
      (is (= [10.0 30.0] (:y-scale-domain (:opts scatter)))
          "scatter leaf still gets the shared y-domain")
      (is (nil? (:y-scale-domain (:opts hist)))
          "histogram leaf does not get the shared y-domain (its y-axis is count, not data)"))))

(deftest inject-shared-scales-empty-layers-diagonal-test
  (testing "an empty-layers leaf whose diagonal mapping infers to :bin gets exempted"
    (let [ds (tc/dataset {:a [1.0 2.0 3.0]
                          :b [10.0 20.0 30.0]})
          ;; This mirrors the SPLOM diagonal cell shape produced by
          ;; (pj/cross cols cols): the leaf has :x = :y mapping and
          ;; empty layers. Per-cell inference picks {:mark :bar :stat :bin}.
          tree {:data ds
                :share-scales #{:y}
                :frames [{:mapping {:x :a :y :a} :layers []}      ;; diagonal: will infer :bin
                         {:mapping {:x :b :y :a}                  ;; off-diagonal: will infer scatter
                          :layers [{:layer-type :point}]}]}
          [diag off] (frame/resolve-tree (frame/inject-shared-scales tree))]
      (is (nil? (:y-scale-domain (:opts diag)))
          "diagonal cell (inferred to :bin) skips the y-stamp")
      (is (= [1.0 3.0] (:y-scale-domain (:opts off)))
          "off-diagonal scatter still gets the shared y-domain"))))

(deftest inject-shared-scales-bin2d-not-exempted-test
  (testing ":bin2d (heatmap) is NOT exempted -- its y is a data axis, count goes to fill"
    (let [ds (tc/dataset {:a [1.0 2.0 3.0]
                          :b [10.0 20.0 30.0]})
          tree {:data ds
                :share-scales #{:y}
                :frames [{:layers [{:layer-type :point :mapping {:x :a :y :b}}]}
                         {:layers [{:layer-type :tile
                                    :stat :bin2d
                                    :mapping {:x :a :y :b}}]}]}
          [scatter heatmap] (frame/resolve-tree (frame/inject-shared-scales tree))]
      (is (= [10.0 30.0] (:y-scale-domain (:opts scatter))))
      (is (= [10.0 30.0] (:y-scale-domain (:opts heatmap)))
          "heatmap participates in shared y-domain because its y is data, not count"))))

(deftest inject-shared-scales-explicit-density-stat-test
  (testing "explicit :stat :density triggers the exemption"
    (let [ds (tc/dataset {:a [1.0 2.0 3.0]
                          :b [10.0 20.0 30.0]})
          tree {:data ds
                :share-scales #{:y}
                :frames [{:layers [{:layer-type :point :mapping {:x :a :y :b}}]}
                         {:mapping {:y :b}
                          :layers [{:layer-type :line
                                    :stat :density
                                    :mapping {:x :a}}]}]}
          [scatter density] (frame/resolve-tree (frame/inject-shared-scales tree))]
      (is (= [10.0 30.0] (:y-scale-domain (:opts scatter))))
      (is (nil? (:y-scale-domain (:opts density)))
          ":density layer's y axis is density values, not data"))))
