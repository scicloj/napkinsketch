(ns
 napkinsketch-book.sketch-rules-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l71
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v5_l76
 (defn
  sk-summary
  "Show the sketch fields that matter for understanding the rules."
  [sk]
  {:shared (:shared sk),
   :entries
   (mapv (fn* [p1__78055#] (dissoc p1__78055# :data)) (:entries sk)),
   :methods (:methods sk),
   :opts (:opts sk)}))


(def
 v7_l89
 (let
  [sk
   (->
    iris
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/lay-point
    sk/lay-lm)]
  (kind/pprint (sk-summary sk))))


(deftest
 t8_l95
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:shared :color]))
     (= 1 (count (:entries m)))
     (nil? (:color (first (:entries m))))
     (= 2 (count (:methods m)))))
   v7_l89)))


(def
 v9_l101
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t10_l106
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v9_l101)))


(def
 v12_l115
 (let
  [sk
   (->
    iris
    (sk/view :sepal_length :sepal_width)
    sk/lay-point
    sk/lay-lm)]
  (kind/pprint (sk-summary sk))))


(deftest
 t13_l121
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:methods m)))
     (= :point (:mark (first (:methods m))))
     (nil? (:methods (first (:entries m))))))
   v12_l115)))


(def
 v14_l126
 (-> iris (sk/view :sepal_length :sepal_width) sk/lay-point sk/lay-lm))


(deftest
 t15_l131
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v14_l126)))


(def
 v17_l139
 (let
  [sk
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}))]
  (kind/pprint (sk-summary sk))))


(deftest
 t18_l143
 (is
  ((fn
    [m]
    (and
     (= 0 (count (:methods m)))
     (= 1 (count (:entries m)))
     (= 1 (count (:methods (first (:entries m)))))
     (= :species (:color (first (:methods (first (:entries m))))))))
   v17_l139)))


(def
 v19_l149
 (-> iris (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t20_l152
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v19_l149)))


(def
 v22_l159
 (let
  [sk
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    sk/lay-lm)]
  (kind/pprint (sk-summary sk))))


(deftest
 t23_l164
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:methods m)))
     (= 1 (count (:methods (first (:entries m)))))
     (= :point (:mark (first (:methods (first (:entries m))))))
     (= :line (:mark (first (:methods m))))))
   v22_l159)))


(def
 v24_l170
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/lay-lm))


(deftest
 t25_l174
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v24_l170)))


(def
 v27_l183
 (let
  [sk
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/lay-lm :sepal_length :sepal_width))]
  (kind/pprint (sk-summary sk))))


(deftest
 t28_l188
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:entries m)))
     (= 0 (count (:methods m)))
     (= 2 (count (:methods (first (:entries m)))))))
   v27_l183)))


(def
 v29_l193
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/lay-lm :sepal_length :sepal_width)))


(deftest
 t30_l197
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v29_l193)))


(def
 v32_l207
 (let
  [sk
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width)
    (sk/lay-histogram :petal_length))]
  (kind/pprint (sk-summary sk))))


(deftest
 t33_l212
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:entries m)))
     (= 0 (count (:methods m)))
     (=
      [1 1]
      (mapv
       (fn* [p1__78056#] (count (:methods p1__78056#)))
       (:entries m)))
     (= :sepal_length (:x (first (:entries m))))
     (= :petal_length (:x (second (:entries m))))))
   v32_l207)))


(def
 v34_l219
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay-histogram :petal_length)))


(deftest
 t35_l223
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:polygons s)))))
   v34_l219)))


(def
 v37_l232
 (let
  [sk
   (->
    (sk/sketch iris {:color :species})
    (sk/view :sepal_length :sepal_width)
    sk/lay-point
    sk/lay-lm)]
  (kind/pprint (sk-summary sk))))


(deftest
 t38_l238
 (is ((fn [m] (= :species (get-in m [:shared :color]))) v37_l232)))


(def
 v39_l241
 (->
  (sk/sketch iris {:color :species})
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t40_l246
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v39_l241)))


(def
 v42_l254
 (let
  [sk
   (->
    iris
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/lay-point
    (sk/lay-lm {:color nil}))]
  (kind/pprint (sk-summary sk))))


(deftest
 t43_l260
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:shared :color]))
     (nil? (:color (second (:methods m))))))
   v42_l254)))


(def
 v44_l264
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})))


(deftest
 t45_l269
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v44_l264)))


(def
 v47_l277
 (let
  [sk
   (->
    (sk/sketch iris {:color :species})
    (sk/view :sepal_length :sepal_width)
    (sk/view :petal_length :petal_width)
    sk/lay-point)]
  (kind/pprint (sk-summary sk))))


(deftest
 t48_l283
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:shared :color]))
     (= 2 (count (:entries m)))
     (= 1 (count (:methods m)))))
   v47_l277)))


(def
 v49_l288
 (->
  (sk/sketch iris {:color :species})
  (sk/view :sepal_length :sepal_width)
  (sk/view :petal_length :petal_width)
  sk/lay-point))


(deftest
 t50_l293
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v49_l288)))


(def
 v52_l301
 (let
  [sk
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/annotate (sk/rule-h 3.0)))]
  (kind/pprint (sk-summary sk))))


(deftest
 t53_l306
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:entries m)))
     (= :rule-h (:mark (second (:entries m))))))
   v52_l301)))


(def
 v54_l310
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/annotate (sk/rule-h 3.0))))


(deftest
 t55_l314
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v54_l310)))


(def
 v57_l322
 (let
  [sk
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width)
    (sk/overlay :sepal_length :petal_width :lm))]
  (kind/pprint (sk-summary sk))))


(deftest
 t58_l327
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:entries m)))
     (= :sepal_width (:y (first (:entries m))))
     (= :petal_width (:y (second (:entries m))))
     (= :line (:mark (first (:methods (second (:entries m))))))))
   v57_l322)))


(def
 v60_l338
 (let
  [sk (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point)]
  (kind/pprint (sk-summary sk))))


(deftest
 t61_l342
 (is
  ((fn
    [m]
    (and (= 1 (count (:entries m))) (= :x (:x (first (:entries m))))))
   v60_l338)))


(def v62_l346 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point))


(deftest
 t63_l349
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v62_l346)))


(def
 v65_l366
 (let
  [sk
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width)
    (sk/lay-histogram :petal_length))]
  (kind/pprint (sk-summary sk))))


(deftest
 t66_l371
 (is
  ((fn [m] (and (= 2 (count (:entries m))) (= 0 (count (:methods m)))))
   v65_l366)))


(def
 v67_l375
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay-histogram :petal_length)))


(deftest
 t68_l379
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v67_l375)))


(def
 v70_l391
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay-histogram :sepal_length)))


(deftest
 t71_l395
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v70_l391)))


(def
 v73_l408
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/lay-lm :sepal_length :sepal_width)))


(deftest
 t74_l412
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v73_l408)))


(def
 v76_l422
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay-point :petal_length :petal_width)
  sk/lay-lm))


(deftest
 t77_l427
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:panels s)) (pos? (:points s)) (pos? (:lines s)))))
   v76_l422)))


(def
 v79_l437
 (def splom-cols [:sepal_length :sepal_width :petal_length]))


(def
 v80_l439
 (->
  (sk/sketch iris {:color :species})
  (sk/view (sk/cross splom-cols splom-cols))))


(deftest
 t81_l442
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 9 (:panels s))
      (= (* 6 150) (:points s))
      (pos? (:polygons s)))))
   v80_l439)))


(def
 v83_l456
 (def
  scale-plan
  (->
   iris
   (sk/view :sepal_length :sepal_width)
   sk/lay-point
   (sk/scale :x :log)
   (sk/scale :y {:domain [0 6]})
   (sk/coord :flip)
   sk/plan)))


(def
 v84_l465
 (let
  [panel (first (:panels scale-plan))]
  {:coord (:coord panel),
   :x-scale (:x-scale panel),
   :y-scale (:y-scale panel),
   :x-domain (:x-domain panel)}))


(deftest
 t85_l471
 (is
  ((fn
    [m]
    (and
     (= :flip (:coord m))
     (= [0 6] (:x-domain m))
     (= {:type :linear, :domain [0 6]} (:x-scale m))
     (= {:type :log} (:y-scale m))))
   v84_l465)))


(def v87_l483 (select-keys scale-plan [:x-label :y-label]))


(deftest
 t88_l485
 (is
  ((fn
    [m]
    (and
     (= "sepal_width" (:x-label m))
     (= "sepal_length" (:y-label m))))
   v87_l483)))
