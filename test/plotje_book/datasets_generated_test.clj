(ns
 plotje-book.datasets-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.string :as str]
  [clojure.test :refer [deftest is]]))


(def
 v3_l49
 (->
  [{:month "Jan", :temperature 5}
   {:month "Feb", :temperature 7}
   {:month "Mar", :temperature 12}
   {:month "Apr", :temperature 16}]
  (pj/lay-line :month :temperature)
  pj/lay-point))


(deftest
 t4_l56
 (is ((fn [v] (= 4 (:points (pj/svg-summary v)))) v3_l49)))


(def v6_l77 (tc/dataset {:x [1 2 3 4 5], :y [10 20 15 30 25]}))


(deftest t7_l80 (is ((fn [ds] (= 5 (tc/row-count ds))) v6_l77)))


(def
 v9_l84
 (tc/dataset
  [{:name "Alice", :score 92}
   {:name "Bob", :score 85}
   {:name "Carol", :score 97}]))


(deftest t10_l88 (is ((fn [ds] (= 3 (tc/row-count ds))) v9_l84)))


(def
 v12_l92
 (tc/dataset
  [["Alice" 92] ["Bob" 85] ["Carol" 97]]
  {:column-names [:name :score]}))


(deftest t13_l97 (is ((fn [ds] (= 3 (tc/row-count ds))) v12_l92)))


(def
 v15_l101
 (tc/dataset
  "https://vincentarelbundock.github.io/Rdatasets/csv/datasets/iris.csv"
  {:key-fn keyword}))


(deftest t16_l104 (is ((fn [ds] (= 150 (tc/row-count ds))) v15_l101)))


(def v18_l129 (rdatasets/datasets-iris))


(deftest
 t19_l131
 (is
  ((fn [ds] (and (tc/dataset? ds) (= 150 (tc/row-count ds))))
   v18_l129)))


(def
 v21_l139
 (->
  {:var
   [#'rdatasets/datasets-iris
    #'rdatasets/reshape2-tips
    #'rdatasets/ggplot2-mpg
    #'rdatasets/ggplot2-diamonds
    #'rdatasets/gapminder-gapminder
    #'rdatasets/datasets-mtcars]}
  tc/dataset
  (tc/map-columns
   :function
   :var
   (fn* [p1__86355#] (-> p1__86355# meta :name)))
  (tc/map-columns :dataset :var (fn* [p1__86356#] (p1__86356#)))
  (tc/map-columns :rows :dataset tc/row-count)
  (tc/map-columns
   :description
   :var
   (fn*
    [p1__86357#]
    (->
     p1__86357#
     meta
     :doc-link
     slurp
     str/split-lines
     first
     (str/replace "<!DOCTYPE html><html><head><title>R: " "")
     (str/replace "</title>" ""))))
  (tc/select-columns [:function :rows :description])))


(def v23_l171 (tc/head (rdatasets/datasets-iris) 3))


(deftest t24_l173 (is ((fn [ds] (= 3 (tc/row-count ds))) v23_l171)))


(def
 v26_l177
 (->
  (rdatasets/datasets-iris)
  (tc/select-rows
   (fn* [p1__86358#] (= "setosa" (:species p1__86358#))))))


(deftest t27_l180 (is ((fn [ds] (= 50 (tc/row-count ds))) v26_l177)))


(def
 v29_l184
 (->
  (rdatasets/datasets-iris)
  (tc/group-by [:species])
  (tc/aggregate
   {:mean-sl
    (fn [ds] (/ (reduce + (ds :sepal-length)) (tc/row-count ds)))})))


(deftest t30_l189 (is ((fn [ds] (= 3 (tc/row-count ds))) v29_l184)))


(def
 v32_l193
 (->
  (rdatasets/datasets-mtcars)
  (tc/order-by [:mpg] :desc)
  (tc/head 3)))


(deftest t33_l197 (is ((fn [ds] (= 3 (tc/row-count ds))) v32_l193)))


(def v35_l201 (tc/column-names (rdatasets/datasets-iris)))


(deftest t36_l203 (is ((fn [cols] (= 6 (count cols))) v35_l201)))


(def v38_l207 (tc/row-count (rdatasets/ggplot2-diamonds)))


(deftest t39_l209 (is ((fn [n] (= 53940 n)) v38_l207)))


(def v41_l219 (-> {:x [1 2 3], :y [4 5 6]} (pj/lay-point :x :y)))


(deftest
 t42_l222
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v41_l219)))


(def
 v44_l226
 (-> (tc/dataset {:x [1 2 3], :y [4 5 6]}) (pj/lay-point :x :y)))


(deftest
 t45_l229
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v44_l226)))
