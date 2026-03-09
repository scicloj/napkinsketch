(ns
 napkinsketch-book.architecture-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as ns]
  [scicloj.napkinsketch.impl.sketch-schema :as ss]
  [clojure.test :refer [deftest is]]))


(def
 v3_l95
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v5_l102
 (def
  views
  [(ns/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(def v7_l107 (dissoc (first views) :data))


(deftest t8_l109 (is (fn v7_l107 [v] (= :point (:mark v)))))


(def v10_l116 (def sk (ns/sketch views)))


(def
 v12_l120
 (let
  [panel
   (first (:panels sk))
   layer
   (first (:layers panel))
   group
   (first (:groups layer))]
  {:mark (:mark layer),
   :x-domain (:x-domain panel),
   :y-domain (:y-domain panel),
   :n-groups (count (:groups layer)),
   :first-group-n-points (count (:xs group)),
   :first-group-color (:color group)}))


(deftest
 t13_l130
 (is
  (fn
   v12_l120
   [m]
   (and
    (= :point (:mark m))
    (= 3 (:n-groups m))
    (pos? (:first-group-n-points m))))))


(def v15_l136 (ss/valid? sk))


(deftest t16_l138 (is (true? v15_l136)))


(def v18_l142 (= sk (read-string (pr-str sk))))


(deftest t19_l144 (is (true? v18_l142)))


(def v21_l152 (ns/plot views))


(def
 v23_l190
 (def
  multi-views
  [(ns/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (ns/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(def
 v24_l194
 (def
  multi-sk
  (ns/sketch multi-views {:title "Iris Petals with Regression"})))


(def v26_l198 (count (:layers (first (:panels multi-sk)))))


(deftest t27_l200 (is (fn v26_l198 [n] (= 2 n))))


(def
 v29_l204
 (let
  [layer (first (:layers (first (:panels multi-sk))))]
  {:mark (:mark layer), :n-groups (count (:groups layer))}))


(deftest
 t30_l208
 (is (fn v29_l204 [m] (and (= :point (:mark m)) (= 3 (:n-groups m))))))


(def
 v32_l212
 (let
  [layer (second (:layers (first (:panels multi-sk))))]
  {:mark (:mark layer),
   :stat-origin (:stat-origin layer),
   :n-groups (count (:groups layer)),
   :first-group-keys (set (keys (first (:groups layer))))}))


(deftest
 t33_l218
 (is
  (fn
   v32_l212
   [m]
   (and
    (= :line (:mark m))
    (= :lm (:stat-origin m))
    (= 3 (:n-groups m))
    (contains? (:first-group-keys m) :x1)))))


(def v35_l225 (:title multi-sk))


(deftest
 t36_l227
 (is (fn v35_l225 [t] (= "Iris Petals with Regression" t))))


(def v37_l229 (count (:entries (:legend multi-sk))))


(deftest t38_l231 (is (fn v37_l229 [n] (= 3 n))))


(def
 v40_l235
 (ns/plot multi-views {:title "Iris Petals with Regression"}))
