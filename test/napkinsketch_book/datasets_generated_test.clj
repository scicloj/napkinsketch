(ns
 napkinsketch-book.datasets-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def
 v3_l36
 (->
  [{:month "Jan", :temperature 5}
   {:month "Feb", :temperature 7}
   {:month "Mar", :temperature 12}
   {:month "Apr", :temperature 16}]
  (sk/lay-line :month :temperature)
  sk/lay-point))


(deftest
 t4_l43
 (is ((fn [v] (= 4 (:points (sk/svg-summary v)))) v3_l36)))


(def v6_l64 (tc/dataset {:x [1 2 3 4 5], :y [10 20 15 30 25]}))


(deftest t7_l67 (is ((fn [ds] (= 5 (tc/row-count ds))) v6_l64)))


(def
 v9_l71
 (tc/dataset
  [{:name "Alice", :score 92}
   {:name "Bob", :score 85}
   {:name "Carol", :score 97}]))


(deftest t10_l75 (is ((fn [ds] (= 3 (tc/row-count ds))) v9_l71)))


(def
 v12_l79
 (tc/dataset
  [["Alice" 92] ["Bob" 85] ["Carol" 97]]
  {:column-names [:name :score]}))


(deftest t13_l84 (is ((fn [ds] (= 3 (tc/row-count ds))) v12_l79)))


(def
 v15_l88
 (tc/dataset
  "https://vincentarelbundock.github.io/Rdatasets/csv/datasets/iris.csv"
  {:key-fn keyword}))


(deftest t16_l91 (is ((fn [ds] (= 150 (tc/row-count ds))) v15_l88)))


(def v18_l119 (rdatasets/datasets-iris))


(deftest
 t19_l121
 (is
  ((fn [ds] (and (tc/dataset? ds) (= 150 (tc/row-count ds))))
   v18_l119)))


(def v21_l145 (tc/head (rdatasets/datasets-iris) 3))


(deftest t22_l147 (is ((fn [ds] (= 3 (tc/row-count ds))) v21_l145)))


(def
 v24_l151
 (->
  (rdatasets/datasets-iris)
  (tc/select-rows
   (fn* [p1__87418#] (= "setosa" (:species p1__87418#))))))


(deftest t25_l154 (is ((fn [ds] (= 50 (tc/row-count ds))) v24_l151)))


(def
 v27_l158
 (->
  (rdatasets/datasets-iris)
  (tc/group-by [:species])
  (tc/aggregate
   {:mean-sl
    (fn [ds] (/ (reduce + (ds :sepal-length)) (tc/row-count ds)))})))


(deftest t28_l163 (is ((fn [ds] (= 3 (tc/row-count ds))) v27_l158)))


(def
 v30_l167
 (->
  (rdatasets/datasets-mtcars)
  (tc/order-by [:mpg] :desc)
  (tc/head 3)))


(deftest t31_l171 (is ((fn [ds] (= 3 (tc/row-count ds))) v30_l167)))


(def v33_l175 (tc/column-names (rdatasets/datasets-iris)))


(deftest t34_l177 (is ((fn [cols] (= 6 (count cols))) v33_l175)))


(def v36_l181 (tc/row-count (rdatasets/ggplot2-diamonds)))


(deftest t37_l183 (is ((fn [n] (= 53940 n)) v36_l181)))


(def v39_l193 (-> {:x [1 2 3], :y [4 5 6]} (sk/lay-point :x :y)))


(deftest
 t40_l196
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v39_l193)))


(def
 v42_l200
 (-> (tc/dataset {:x [1 2 3], :y [4 5 6]}) (sk/lay-point :x :y)))


(deftest
 t43_l203
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v42_l200)))
