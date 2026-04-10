(ns
 napkinsketch-book.datasets-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def
 v3_l40
 (->
  [{:month "Jan", :temperature 5}
   {:month "Feb", :temperature 7}
   {:month "Mar", :temperature 12}
   {:month "Apr", :temperature 16}]
  (sk/lay-line :month :temperature)
  sk/lay-point))


(deftest
 t4_l47
 (is ((fn [v] (= 4 (:points (sk/svg-summary v)))) v3_l40)))


(def v6_l68 (tc/dataset {:x [1 2 3 4 5], :y [10 20 15 30 25]}))


(deftest t7_l71 (is ((fn [ds] (= 5 (tc/row-count ds))) v6_l68)))


(def
 v9_l75
 (tc/dataset
  [{:name "Alice", :score 92}
   {:name "Bob", :score 85}
   {:name "Carol", :score 97}]))


(deftest t10_l79 (is ((fn [ds] (= 3 (tc/row-count ds))) v9_l75)))


(def
 v12_l83
 (tc/dataset
  [["Alice" 92] ["Bob" 85] ["Carol" 97]]
  {:column-names [:name :score]}))


(deftest t13_l88 (is ((fn [ds] (= 3 (tc/row-count ds))) v12_l83)))


(def
 v15_l92
 (tc/dataset
  "https://vincentarelbundock.github.io/Rdatasets/csv/datasets/iris.csv"
  {:key-fn keyword}))


(deftest t16_l95 (is ((fn [ds] (= 150 (tc/row-count ds))) v15_l92)))


(def v18_l123 (rdatasets/datasets-iris))


(deftest
 t19_l125
 (is
  ((fn [ds] (and (tc/dataset? ds) (= 150 (tc/row-count ds))))
   v18_l123)))


(def v21_l149 (tc/head (rdatasets/datasets-iris) 3))


(deftest t22_l151 (is ((fn [ds] (= 3 (tc/row-count ds))) v21_l149)))


(def
 v24_l155
 (->
  (rdatasets/datasets-iris)
  (tc/select-rows
   (fn* [p1__1959825#] (= "setosa" (:species p1__1959825#))))))


(deftest t25_l158 (is ((fn [ds] (= 50 (tc/row-count ds))) v24_l155)))


(def
 v27_l162
 (->
  (rdatasets/datasets-iris)
  (tc/group-by [:species])
  (tc/aggregate
   {:mean-sl
    (fn [ds] (/ (reduce + (ds :sepal-length)) (tc/row-count ds)))})))


(deftest t28_l167 (is ((fn [ds] (= 3 (tc/row-count ds))) v27_l162)))


(def
 v30_l171
 (->
  (rdatasets/datasets-mtcars)
  (tc/order-by [:mpg] :desc)
  (tc/head 3)))


(deftest t31_l175 (is ((fn [ds] (= 3 (tc/row-count ds))) v30_l171)))


(def v33_l179 (tc/column-names (rdatasets/datasets-iris)))


(deftest t34_l181 (is ((fn [cols] (= 6 (count cols))) v33_l179)))


(def v36_l185 (tc/row-count (rdatasets/ggplot2-diamonds)))


(deftest t37_l187 (is ((fn [n] (= 53940 n)) v36_l185)))


(def v39_l197 (-> {:x [1 2 3], :y [4 5 6]} (sk/lay-point :x :y)))


(deftest
 t40_l200
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v39_l197)))


(def
 v42_l204
 (-> (tc/dataset {:x [1 2 3], :y [4 5 6]}) (sk/lay-point :x :y)))


(deftest
 t43_l207
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v42_l204)))
