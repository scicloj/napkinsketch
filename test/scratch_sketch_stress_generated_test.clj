(ns
 scratch-sketch-stress-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l12
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v3_l15
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v5_l22
 (let
  [sk
   (->
    iris
    (sk/view :sepal_length :sepal_width {:color :species})
    (sk/view :petal_length :petal_width {:alpha 0.4}))]
  [(:shared sk) (count (:entries sk))]))


(deftest
 t6_l27
 (is
  ((fn
    [[shared n]]
    (and (= :species (:color shared)) (= 0.4 (:alpha shared)) (= 2 n)))
   v5_l22)))


(def
 v8_l34
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  (sk/view :petal_length :petal_width {:alpha 0.4})
  sk/lay-point))


(deftest
 t9_l39
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v8_l34)))


(def
 v11_l47
 (let
  [sk
   (->
    iris
    (sk/view :sepal_length :sepal_width {:color :species})
    (sk/view :petal_length :petal_width {:color :petal_length}))]
  (:color (:shared sk))))


(deftest t12_l52 (is ((fn [v] (= :petal_length v)) v11_l47)))


(def
 v14_l58
 (let
  [sk
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}))]
  [(:shared sk)
   (:color (first (:methods (first (:entries sk)))))]))


(deftest
 t15_l62
 (is
  ((fn
    [[shared method-color]]
    (and (empty? shared) (= :species method-color)))
   v14_l58)))


(def
 v17_l68
 (let
  [sk
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    sk/lay-lm)]
  [(:color (first (:methods (first (:entries sk)))))
   (:color (first (:methods sk)))]))


(deftest
 t18_l74
 (is
  ((fn
    [[entry-color global-color]]
    (and (= :species entry-color) (nil? global-color)))
   v17_l68)))


(def
 v20_l82
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})
  (sk/facet :species)))


(deftest
 t21_l88
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v20_l82)))


(def
 v23_l99
 (sk/lay-histogram
  (sk/sketch iris {:color :species})
  [:sepal_length :sepal_width :petal_length]))


(deftest
 t24_l102
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v23_l99)))


(def
 v26_l111
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  (sk/view
   {:x :sepal_length,
    :y :sepal_width,
    :methods [{:mark :line, :stat :lm, :color nil}]})))


(deftest
 t27_l117
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)) (= 1 (:lines s)))))
   v26_l111)))


(def
 v29_l128
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  (sk/view
   {:x :sepal_length,
    :y :sepal_width,
    :methods [{:mark :line, :stat :lm}]})))


(deftest
 t30_l134
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)) (= 3 (:lines s)))))
   v29_l128)))


(def
 v32_l143
 (let
  [sk (-> {:x [1 2 3], :y [4 5 6]} (sk/view))]
  [(count (:entries sk)) (:shared sk)]))


(deftest
 t33_l147
 (is ((fn [[n shared]] (and (= 1 n) (empty? shared))) v32_l143)))


(def
 v35_l153
 (->
  (sk/sketch
   {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :a]}
   {:color :g})
  (sk/view)
  sk/lay-point))


(deftest
 t36_l158
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v35_l153)))


(def v38_l162 (def cols [:sepal_length :sepal_width :petal_length]))


(def
 v40_l167
 (->
  (sk/sketch iris {:color :species})
  (sk/view (sk/cross cols cols))
  sk/lay-point))


(deftest
 t41_l171
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 9 (:panels s)) (= (* 9 150) (:points s)))))
   v40_l167)))


(def
 v43_l180
 (let
  [sk
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width)
    (sk/lay-histogram :petal_length))]
  [(count (:entries sk))
   (count (:methods sk))
   (mapv
    (fn* [p1__396999#] (count (:methods p1__396999#)))
    (:entries sk))]))


(deftest
 t44_l186
 (is
  ((fn
    [[entries global-methods entry-method-counts]]
    (and
     (= 2 entries)
     (= 0 global-methods)
     (= [1 1] entry-method-counts)))
   v43_l180)))


(def
 v46_l195
 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t47_l198
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v46_l195)))


(def
 v49_l202
 (-> {"x" [1 2 3 4 5], "y" [2 4 3 5 4]} (sk/lay-point "x" "y")))


(deftest
 t50_l205
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v49_l202)))


(def
 v52_l209
 (def
  recipe
  (->
   (sk/sketch)
   (sk/view :sepal_length :sepal_width)
   sk/lay-point
   sk/lay-lm)))


(def v53_l214 (-> recipe (sk/with-data iris)))


(deftest
 t54_l216
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v53_l214)))


(def
 v56_l225
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  (sk/annotate (sk/rule-h 3.0) (sk/band-v 5.5 6.5 {:alpha 0.3}))))


(deftest
 t57_l230
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v56_l225)))


(def
 v59_l238
 (let
  [sk
   (->
    iris
    (sk/view :sepal_length :sepal_width {:color :species})
    (sk/lay-point)
    (sk/lay-lm {:color nil}))]
  [(:color (:shared sk))
   (:color (first (:entries sk)))
   (:color (second (:methods sk)))]))


(deftest
 t60_l246
 (is
  ((fn
    [[shared-c entry-c method-c]]
    (and (= :species shared-c) (nil? entry-c) (nil? method-c)))
   v59_l238)))


(def
 v62_l253
 (let
  [sk
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width)
    (sk/options {:title "My Plot", :width 800}))]
  [(:title (:opts sk))
   (:width (:opts sk))
   (:shared sk)]))


(deftest
 t63_l258
 (is
  ((fn
    [[title width shared]]
    (and (= "My Plot" title) (= 800 width) (empty? shared)))
   v62_l253)))


(def
 v65_l265
 (let
  [sk
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width)
    (sk/scale :y :log)
    (sk/coord :flip))]
  [(-> sk :entries first :y-scale)
   (-> sk :entries first :coord)]))


(deftest
 t66_l272
 (is
  ((fn [[yscale coord]] (and (= {:type :log} yscale) (= :flip coord)))
   v65_l265)))
