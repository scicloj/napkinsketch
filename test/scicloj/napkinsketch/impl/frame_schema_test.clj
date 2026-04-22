(ns scicloj.napkinsketch.impl.frame-schema-test
  "Tests for the Frame Malli schema."
  (:require [clojure.test :refer [deftest testing is]]
            [scicloj.napkinsketch.impl.frame-schema :as fs]))

(deftest leaf-frame-validity-test
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

(deftest composite-frame-validity-test
  (testing "composite with frames and layout is valid"
    (is (fs/valid? {:frames [{:layers [{:layer-type :point}]}
                             {:layers [{:layer-type :line}]}]
                    :layout {:direction :horizontal :weights [1 1]}})))

  (testing "nested composites are valid"
    (is (fs/valid? {:frames [{:frames [{:layers [{:layer-type :point}]}]}]})))

  (testing "composite with :share-scales is valid"
    (is (fs/valid? {:share-scales #{:x :y}
                    :frames [{:layers [{:layer-type :point}]}
                             {:layers [{:layer-type :point}]}]}))))

(deftest rejection-test
  (testing "non-map rejected"
    (is (not (fs/valid? "string"))))

  (testing "bad :layout :direction rejected"
    (is (not (fs/valid? {:frames []
                         :layout {:direction :diagonal}}))))

  (testing "non-positive :weights rejected"
    (is (not (fs/valid? {:frames []
                         :layout {:weights [1 -1]}}))))

  (testing ":share-scales with unknown axis rejected"
    (is (not (fs/valid? {:share-scales #{:z}
                         :frames []}))))

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
