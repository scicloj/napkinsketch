(ns scicloj.plotje.impl.pose-schema-test
  "Tests for the Pose Malli schema."
  (:require [clojure.test :refer [deftest testing is]]
            [scicloj.plotje.impl.pose-schema :as fs]))

(deftest leaf-pose-validity-test
  (testing "leaf with layers but no data/mapping is valid"
    (is (fs/valid? {:layers [{:layer-type :point}]})))

  (testing "leaf with full context is valid"
    (is (fs/valid? {:data {:x [1 2 3] :y [4 5 6]}
                    :mapping {:x :x :y :y}
                    :layers [{:layer-type :point
                              :mapping {:color :species}}]
                    :opts {:title "t"}})))

  (testing "layer with :mark / :stat overrides is valid"
    (is (fs/valid? {:layers [{:layer-type :smooth
                              :stat :linear-model
                              :mark :line}]}))))

(deftest composite-pose-validity-test
  (testing "composite with poses and layout is valid"
    (is (fs/valid? {:poses [{:layers [{:layer-type :point}]}
                             {:layers [{:layer-type :line}]}]
                    :layout {:direction :horizontal :weights [1 1]}})))

  (testing "nested composites are valid"
    (is (fs/valid? {:poses [{:poses [{:layers [{:layer-type :point}]}]}]})))

  (testing "composite with :share-scales is valid"
    (is (fs/valid? {:share-scales #{:x :y}
                    :poses [{:layers [{:layer-type :point}]}
                             {:layers [{:layer-type :point}]}]}))))

(deftest rejection-test
  (testing "non-map rejected"
    (is (not (fs/valid? "string"))))

  (testing "bad :layout :direction rejected"
    (is (not (fs/valid? {:poses []
                         :layout {:direction :diagonal}}))))

  (testing "non-positive :weights rejected"
    (is (not (fs/valid? {:poses []
                         :layout {:weights [1 -1]}}))))

  (testing ":share-scales with unknown axis rejected"
    (is (not (fs/valid? {:share-scales #{:z}
                         :poses []}))))

  (testing ":mapping keys must be keywords"
    (is (not (fs/valid? {:layers [{:mapping {"x" :foo}}]}))))

  (testing ":layers must be a vector of maps, :layer-type must be a keyword"
    (is (not (fs/valid? {:layers "not a vector"})))
    (is (not (fs/valid? {:layers [{:layer-type "string"}]})))))

(deftest extras-pass-through-test
  (testing "non-structural keys are allowed (for facet/mosaic metadata)"
    (is (fs/valid? {:layers [{:layer-type :point}]
                    :panel-label "row=a, col=b"
                    :facet-row :a
                    :facet-col :b}))))
