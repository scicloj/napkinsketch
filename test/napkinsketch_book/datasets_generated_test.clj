(ns
 napkinsketch-book.datasets-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def
 v3_l31
 (->
  [{:month "Jan", :temperature 5}
   {:month "Feb", :temperature 7}
   {:month "Mar", :temperature 12}
   {:month "Apr", :temperature 16}]
  (sk/lay-line :month :temperature)
  sk/lay-point))


(deftest
 t4_l38
 (is ((fn [v] (= 4 (:points (sk/svg-summary v)))) v3_l31)))


(def v6_l59 (tc/dataset {:x [1 2 3 4 5], :y [10 20 15 30 25]}))


(deftest t7_l62 (is ((fn [ds] (= 5 (tc/row-count ds))) v6_l59)))


(def
 v9_l66
 (tc/dataset
  [{:name "Alice", :score 92}
   {:name "Bob", :score 85}
   {:name "Carol", :score 97}]))


(deftest t10_l70 (is ((fn [ds] (= 3 (tc/row-count ds))) v9_l66)))


(def
 v12_l76
 (def
  iris-from-url
  (tc/dataset
   "https://vincentarelbundock.github.io/Rdatasets/csv/datasets/iris.csv"
   {:key-fn keyword})))


(def v13_l80 (tc/row-count iris-from-url))


(deftest t14_l82 (is ((fn [n] (= 150 n)) v13_l80)))


(def v16_l106 (rdatasets/datasets-iris))


(deftest
 t17_l108
 (is
  ((fn [ds] (and (tc/dataset? ds) (= 150 (tc/row-count ds))))
   v16_l106)))


(def v19_l132 (tc/head (rdatasets/datasets-iris) 3))


(deftest t20_l134 (is ((fn [ds] (= 3 (tc/row-count ds))) v19_l132)))


(def
 v22_l138
 (->
  (rdatasets/datasets-iris)
  (tc/select-rows
   (fn* [p1__1904461#] (= "setosa" (:species p1__1904461#))))
  tc/row-count))


(deftest t23_l142 (is ((fn [n] (= 50 n)) v22_l138)))


(def
 v25_l146
 (->
  (rdatasets/datasets-iris)
  (tc/group-by [:species])
  (tc/aggregate
   {:mean-sl
    (fn [ds] (/ (reduce + (ds :sepal-length)) (tc/row-count ds)))})))


(deftest t26_l151 (is ((fn [ds] (= 3 (tc/row-count ds))) v25_l146)))


(def
 v28_l155
 (->
  (rdatasets/datasets-mtcars)
  (tc/order-by [:mpg] :desc)
  (tc/head 3)))


(deftest t29_l159 (is ((fn [ds] (= 3 (tc/row-count ds))) v28_l155)))


(def v31_l163 (tc/column-names (rdatasets/datasets-iris)))


(deftest t32_l165 (is ((fn [cols] (= 6 (count cols))) v31_l163)))


(def v34_l169 (tc/row-count (rdatasets/ggplot2-diamonds)))


(deftest t35_l171 (is ((fn [n] (= 53940 n)) v34_l169)))


(def v37_l181 (-> {:x [1 2 3], :y [4 5 6]} (sk/lay-point :x :y)))


(deftest
 t38_l184
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v37_l181)))


(def
 v40_l188
 (-> (tc/dataset {:x [1 2 3], :y [4 5 6]}) (sk/lay-point :x :y)))


(deftest
 t41_l191
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v40_l188)))
