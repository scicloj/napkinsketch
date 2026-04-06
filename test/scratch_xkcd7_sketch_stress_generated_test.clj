(ns
 scratch-xkcd7-sketch-stress-generated-test
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
   [xkcd7-sk
    (->
     iris
     (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
     (sk/xkcd7-view :petal_length :petal_width {:alpha 0.4}))]
    [(:shared xkcd7-sk) (count (:entries xkcd7-sk))]))

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
   (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
   (sk/xkcd7-view :petal_length :petal_width {:alpha 0.4})
   sk/xkcd7-lay-point))

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
   [xkcd7-sk
    (->
     iris
     (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
     (sk/xkcd7-view :petal_length :petal_width {:color :petal_length}))]
    (:color (:shared xkcd7-sk))))

(deftest t12_l52 (is ((fn [v] (= :petal_length v)) v11_l47)))

(def
  v14_l58
  (let
   [xkcd7-sk
    (->
     iris
     (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))]
    [(:shared xkcd7-sk) (:color (first (:methods (first (:entries xkcd7-sk)))))]))

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
   [xkcd7-sk
    (->
     iris
     (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
     sk/xkcd7-lay-lm)]
    [(:color (first (:methods (first (:entries xkcd7-sk)))))
     (:color (first (:methods xkcd7-sk)))]))

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
   (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
   sk/xkcd7-lay-point
   (sk/xkcd7-lay-lm {:color nil})
   (sk/xkcd7-facet :species)))

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
  (sk/xkcd7-lay-histogram (sk/xkcd7-sketch iris {:color :species})
                          [:sepal_length :sepal_width :petal_length]))

(deftest
  t24_l103
  (is
   ((fn
      [v]
      (let
       [s (sk/svg-summary v)]
        (and (= 3 (:panels s)) (pos? (:polygons s)))))
    v23_l99)))

(def
  v26_l112
  (->
   iris
   (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
   sk/xkcd7-lay-point
   (sk/xkcd7-view
    {:x :sepal_length,
     :y :sepal_width,
     :methods [{:mark :line, :stat :lm, :color nil}]})))

(deftest
  t27_l118
  (is
   ((fn
      [v]
      (let
       [s (sk/svg-summary v)]
        (and (= 2 (:panels s)) (= 300 (:points s)) (= 1 (:lines s)))))
    v26_l112)))

(def
  v29_l129
  (->
   iris
   (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
   sk/xkcd7-lay-point
   (sk/xkcd7-view
    {:x :sepal_length,
     :y :sepal_width,
     :methods [{:mark :line, :stat :lm}]})))

(deftest
  t30_l135
  (is
   ((fn
      [v]
      (let
       [s (sk/svg-summary v)]
        (and (= 2 (:panels s)) (= 300 (:points s)) (= 3 (:lines s)))))
    v29_l129)))

(def
  v32_l144
  (let
   [xkcd7-sk (-> {:x [1 2 3], :y [4 5 6]} (sk/xkcd7-view))]
    [(count (:entries xkcd7-sk)) (:shared xkcd7-sk)]))

(deftest
  t33_l148
  (is ((fn [[n shared]] (and (= 1 n) (empty? shared))) v32_l144)))

(def
  v35_l154
  (->
   (sk/xkcd7-sketch
    {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :a]}
    {:color :g})
   (sk/xkcd7-view)
   sk/xkcd7-lay-point))

(deftest
  t36_l159
  (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v35_l154)))

(def v38_l163 (def cols [:sepal_length :sepal_width :petal_length]))

(def
  v40_l168
  (->
   (sk/xkcd7-sketch iris {:color :species})
   (sk/xkcd7-view (sk/cross cols cols))
   sk/xkcd7-lay-point))

(deftest
  t41_l172
  (is
   ((fn
      [v]
      (let
       [s (sk/svg-summary v)]
        (and (= 9 (:panels s)) (= (* 9 150) (:points s)))))
    v40_l168)))

(def
  v43_l181
  (let
   [xkcd7-sk
    (->
     iris
     (sk/xkcd7-lay-point :sepal_length :sepal_width)
     (sk/xkcd7-lay-histogram :petal_length))]
    [(count (:entries xkcd7-sk))
     (count (:methods xkcd7-sk))
     (mapv
      (fn* [p1__11149#] (count (:methods p1__11149#)))
      (:entries xkcd7-sk))]))

(deftest
  t44_l187
  (is
   ((fn
      [[entries global-methods entry-method-counts]]
      (and
       (= 2 entries)
       (= 0 global-methods)
       (= [1 1] entry-method-counts)))
    v43_l181)))

(def
  v46_l196
  (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/xkcd7-lay-point :x :y)))

(deftest
  t47_l199
  (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v46_l196)))

(def
  v49_l203
  (-> {"x" [1 2 3 4 5], "y" [2 4 3 5 4]} (sk/xkcd7-lay-point "x" "y")))

(deftest
  t50_l206
  (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v49_l203)))

(def
  v52_l210
  (def
    recipe
    (->
     (sk/xkcd7-sketch)
     (sk/xkcd7-view :sepal_length :sepal_width)
     sk/xkcd7-lay-point
     sk/xkcd7-lay-lm)))

(def v53_l215 (-> recipe (sk/xkcd7-with-data iris)))

(deftest
  t54_l217
  (is
   ((fn
      [v]
      (let
       [s (sk/svg-summary v)]
        (and (= 150 (:points s)) (= 1 (:lines s)))))
    v53_l215)))

(def
  v56_l226
  (->
   iris
   (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
   sk/xkcd7-lay-point
   (sk/xkcd7-annotate (sk/rule-h 3.0) (sk/band-v 5.5 6.5 {:alpha 0.3}))))

(deftest
  t57_l231
  (is
   ((fn
      [v]
      (let
       [s (sk/svg-summary v)]
        (and (= 150 (:points s)) (= 1 (:lines s)))))
    v56_l226)))

(def
  v59_l239
  (let
   [xkcd7-sk
    (->
     iris
     (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
     (sk/xkcd7-lay-point)
     (sk/xkcd7-lay-lm {:color nil}))]
    [(:color (:shared xkcd7-sk))
     (:color (first (:entries xkcd7-sk)))
     (:color (second (:methods xkcd7-sk)))]))

(deftest
  t60_l247
  (is
   ((fn
      [[shared-c entry-c method-c]]
      (and (= :species shared-c) (nil? entry-c) (nil? method-c)))
    v59_l239)))

(def
  v62_l254
  (let
   [xkcd7-sk
    (->
     iris
     (sk/xkcd7-lay-point :sepal_length :sepal_width)
     (sk/xkcd7-options {:title "My Plot", :width 800}))]
    [(:title (:opts xkcd7-sk)) (:width (:opts xkcd7-sk)) (:shared xkcd7-sk)]))

(deftest
  t63_l259
  (is
   ((fn
      [[title width shared]]
      (and (= "My Plot" title) (= 800 width) (empty? shared)))
    v62_l254)))

(def
  v65_l266
  (let
   [xkcd7-sk
    (->
     iris
     (sk/xkcd7-lay-point :sepal_length :sepal_width)
     (sk/xkcd7-scale :y :log)
     (sk/xkcd7-coord :flip))]
    [(-> xkcd7-sk :entries first :y-scale) (-> xkcd7-sk :entries first :coord)]))

(deftest
  t66_l273
  (is
   ((fn [[yscale coord]] (and (= {:type :log} yscale) (= :flip coord)))
    v65_l266)))
