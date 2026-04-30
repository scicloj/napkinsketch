(ns scicloj.plotje.no-data-placeholder-test
  "The 'no data' placeholder is drawn over panels whose layers carry
   no rendered geometry (and that have no annotations). Different
   layer types use different data slots -- :groups for points/lines/
   bars/etc., :boxes for boxplot, :violins for violin, :ridges for
   ridgeline, :tiles for tile/density-2d, :levels for contour. The
   predicate must check all of these, not just :groups."
  (:require [clojure.test :refer [deftest testing is]]
            [scicloj.plotje.api :as pj]
            [scicloj.metamorph.ml.rdatasets :as rdatasets]))

(defn- has-no-data-text? [pose]
  (boolean (some #{"no data"} (:texts (pj/svg-summary pose)))))

(deftest valid-boxplot-does-not-show-no-data
  (testing "boxplot with valid :boxes is not flagged as empty"
    (let [pose (-> (rdatasets/datasets-iris)
                   (pj/lay-boxplot :species :sepal-width))]
      (is (not (has-no-data-text? pose))))))

(deftest valid-violin-does-not-show-no-data
  (testing "violin with valid :violins is not flagged as empty"
    (let [pose (-> (rdatasets/datasets-iris)
                   (pj/lay-violin :species :sepal-width))]
      (is (not (has-no-data-text? pose))))))

(deftest valid-ridgeline-does-not-show-no-data
  (testing "ridgeline with valid :ridges is not flagged as empty"
    (let [pose (-> (rdatasets/datasets-iris)
                   (pj/lay-ridgeline :species :sepal-width))]
      (is (not (has-no-data-text? pose))))))

(deftest valid-tile-does-not-show-no-data
  (testing "tile with valid :tiles is not flagged as empty"
    (let [pose (-> (rdatasets/datasets-iris)
                   (pj/lay-tile :sepal-length :sepal-width))]
      (is (not (has-no-data-text? pose))))))

(deftest valid-density-2d-does-not-show-no-data
  (testing "density-2d with valid :tiles is not flagged as empty"
    (let [pose (-> (rdatasets/datasets-iris)
                   (pj/lay-density-2d :sepal-length :sepal-width))]
      (is (not (has-no-data-text? pose))))))

(deftest valid-contour-does-not-show-no-data
  (testing "contour with valid :levels is not flagged as empty"
    (let [pose (-> (rdatasets/datasets-iris)
                   (pj/lay-contour :sepal-length :sepal-width))]
      (is (not (has-no-data-text? pose))))))

(deftest dashboard-with-boxplot-cell-does-not-show-no-data
  (testing "the composition.clj dashboard example renders without 'no data'"
    (let [iris (rdatasets/datasets-iris)
          dashboard (pj/arrange
                     [[(-> iris (pj/lay-histogram :sepal-length))
                       (-> iris (pj/lay-boxplot :species :sepal-width
                                                {:color :species}))]
                      [(-> iris (pj/lay-point :petal-length :petal-width
                                              {:color :species}))
                       (-> iris (pj/lay-density :petal-length
                                                {:color :species}))]])]
      (is (not (has-no-data-text? dashboard))))))

(deftest empty-data-still-shows-no-data
  (testing "the placeholder still fires when layers truly have no geometry"
    (let [pose (-> {:x [1.0 2.0 3.0] :y [##NaN ##NaN ##NaN]}
                   (pj/lay-point :x :y))]
      (is (has-no-data-text? pose)
          "all-NaN y collapses :groups to empty -> 'no data' should appear"))))
