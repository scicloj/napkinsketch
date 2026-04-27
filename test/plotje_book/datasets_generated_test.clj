(ns
 plotje-book.datasets-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def
 v3_l48
 (->
  [{:month "Jan", :temperature 5}
   {:month "Feb", :temperature 7}
   {:month "Mar", :temperature 12}
   {:month "Apr", :temperature 16}]
  (pj/lay-line :month :temperature)
  pj/lay-point))


(deftest
 t4_l55
 (is ((fn [v] (= 4 (:points (pj/svg-summary v)))) v3_l48)))


(def v6_l76 (tc/dataset {:x [1 2 3 4 5], :y [10 20 15 30 25]}))


(deftest t7_l79 (is ((fn [ds] (= 5 (tc/row-count ds))) v6_l76)))


(def
 v9_l83
 (tc/dataset
  [{:name "Alice", :score 92}
   {:name "Bob", :score 85}
   {:name "Carol", :score 97}]))


(deftest t10_l87 (is ((fn [ds] (= 3 (tc/row-count ds))) v9_l83)))


(def
 v12_l91
 (tc/dataset
  [["Alice" 92] ["Bob" 85] ["Carol" 97]]
  {:column-names [:name :score]}))


(deftest t13_l96 (is ((fn [ds] (= 3 (tc/row-count ds))) v12_l91)))


(def
 v15_l100
 (tc/dataset
  "https://vincentarelbundock.github.io/Rdatasets/csv/datasets/iris.csv"
  {:key-fn keyword}))


(deftest t16_l103 (is ((fn [ds] (= 150 (tc/row-count ds))) v15_l100)))


(def v18_l128 (rdatasets/datasets-iris))


(deftest
 t19_l130
 (is
  ((fn [ds] (and (tc/dataset? ds) (= 150 (tc/row-count ds))))
   v18_l128)))


(def
 v21_l138
 (kind/table
  {:column-names ["Function" "Rows" "Description"],
   :row-maps
   (let
    [mpg (rdatasets/ggplot2-mpg)]
    [{"Function" (kind/code "rdatasets/datasets-iris"),
      "Rows" (tc/row-count (rdatasets/datasets-iris)),
      "Description" "Iris flower measurements by species"}
     {"Function" (kind/code "rdatasets/reshape2-tips"),
      "Rows" (tc/row-count (rdatasets/reshape2-tips)),
      "Description" "Restaurant tips with bill, day, time, smoker"}
     {"Function" (kind/code "rdatasets/ggplot2-mpg"),
      "Rows" (tc/row-count mpg),
      "Description"
      (str
       "Fuel economy for "
       (count (distinct (mpg :model)))
       " car models")}
     {"Function" (kind/code "rdatasets/ggplot2-diamonds"),
      "Rows" (tc/row-count (rdatasets/ggplot2-diamonds)),
      "Description" "Diamond price, carat, cut, color, clarity"}
     {"Function" (kind/code "rdatasets/gapminder-gapminder"),
      "Rows" (tc/row-count (rdatasets/gapminder-gapminder)),
      "Description" "Country-level life expectancy and GDP"}
     {"Function" (kind/code "rdatasets/datasets-mtcars"),
      "Rows" (tc/row-count (rdatasets/datasets-mtcars)),
      "Description" "Motor Trend car road tests"}])}))


(def v23_l170 (tc/head (rdatasets/datasets-iris) 3))


(deftest t24_l172 (is ((fn [ds] (= 3 (tc/row-count ds))) v23_l170)))


(def
 v26_l176
 (->
  (rdatasets/datasets-iris)
  (tc/select-rows
   (fn* [p1__86877#] (= "setosa" (:species p1__86877#))))))


(deftest t27_l179 (is ((fn [ds] (= 50 (tc/row-count ds))) v26_l176)))


(def
 v29_l183
 (->
  (rdatasets/datasets-iris)
  (tc/group-by [:species])
  (tc/aggregate
   {:mean-sl
    (fn [ds] (/ (reduce + (ds :sepal-length)) (tc/row-count ds)))})))


(deftest t30_l188 (is ((fn [ds] (= 3 (tc/row-count ds))) v29_l183)))


(def
 v32_l192
 (->
  (rdatasets/datasets-mtcars)
  (tc/order-by [:mpg] :desc)
  (tc/head 3)))


(deftest t33_l196 (is ((fn [ds] (= 3 (tc/row-count ds))) v32_l192)))


(def v35_l200 (tc/column-names (rdatasets/datasets-iris)))


(deftest t36_l202 (is ((fn [cols] (= 6 (count cols))) v35_l200)))


(def v38_l206 (tc/row-count (rdatasets/ggplot2-diamonds)))


(deftest t39_l208 (is ((fn [n] (= 53940 n)) v38_l206)))


(def v41_l218 (-> {:x [1 2 3], :y [4 5 6]} (pj/lay-point :x :y)))


(deftest
 t42_l221
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v41_l218)))


(def
 v44_l225
 (-> (tc/dataset {:x [1 2 3], :y [4 5 6]}) (pj/lay-point :x :y)))


(deftest
 t45_l228
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v44_l225)))
