(ns
 scratch-blueprint-spec-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l60
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v5_l65
 (defn
  bp-summary
  "Show the Blueprint fields that matter for understanding the rules."
  [bp]
  {:shared (:shared bp),
   :entries
   (mapv (fn* [p1__11149#] (dissoc p1__11149# :data)) (:entries bp)),
   :methods (:methods bp),
   :opts (:opts bp)}))


(def
 v7_l78
 (let
  [bp
   (->
    iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)]
  (kind/pprint (bp-summary bp))))


(deftest
 t8_l84
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:shared :color]))
     (= 1 (count (:entries m)))
     (nil? (:color (first (:entries m))))
     (= 2 (count (:methods m)))))
   v7_l78)))


(def
 v9_l90
 (->
  iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t10_l95
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v9_l90)))


(def
 v12_l104
 (let
  [bp
   (->
    iris
    (sk/xkcd7-view :sepal_length :sepal_width)
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)]
  (kind/pprint (bp-summary bp))))


(deftest
 t13_l110
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:methods m)))
     (= :point (:mark (first (:methods m))))
     (nil? (:methods (first (:entries m))))))
   v12_l104)))


(def
 v14_l115
 (->
  iris
  (sk/xkcd7-view :sepal_length :sepal_width)
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t15_l120
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v14_l115)))


(def
 v17_l128
 (let
  [bp
   (->
    iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))]
  (kind/pprint (bp-summary bp))))


(deftest
 t18_l132
 (is
  ((fn
    [m]
    (and
     (= 0 (count (:methods m)))
     (= 1 (count (:entries m)))
     (= 1 (count (:methods (first (:entries m)))))
     (= :species (:color (first (:methods (first (:entries m))))))))
   v17_l128)))


(def
 v19_l138
 (->
  iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t20_l141
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v19_l138)))


(def
 v22_l148
 (let
  [bp
   (->
    iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-lm)]
  (kind/pprint (bp-summary bp))))


(deftest
 t23_l153
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:methods m)))
     (= 1 (count (:methods (first (:entries m)))))
     (= :point (:mark (first (:methods (first (:entries m))))))
     (= :line (:mark (first (:methods m))))))
   v22_l148)))


(def
 v24_l159
 (->
  iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-lm))


(deftest
 t25_l163
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v24_l159)))


(def
 v27_l172
 (let
  [bp
   (->
    iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-lay-lm :sepal_length :sepal_width))]
  (kind/pprint (bp-summary bp))))


(deftest
 t28_l177
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:entries m)))
     (= 0 (count (:methods m)))
     (= 2 (count (:methods (first (:entries m)))))))
   v27_l172)))


(def
 v29_l182
 (->
  iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-lay-lm :sepal_length :sepal_width)))


(deftest
 t30_l186
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v29_l182)))


(def
 v32_l196
 (let
  [bp
   (->
    iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width)
    (sk/xkcd7-lay-histogram :petal_length))]
  (kind/pprint (bp-summary bp))))


(deftest
 t33_l201
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:entries m)))
     (= 0 (count (:methods m)))
     (=
      [1 1]
      (mapv
       (fn* [p1__11150#] (count (:methods p1__11150#)))
       (:entries m)))
     (= :sepal_length (:x (first (:entries m))))
     (= :petal_length (:x (second (:entries m))))))
   v32_l196)))


(def
 v34_l208
 (->
  iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width)
  (sk/xkcd7-lay-histogram :petal_length)))


(deftest
 t35_l212
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:polygons s)))))
   v34_l208)))


(def
 v37_l221
 (let
  [bp
   (->
    (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)]
  (kind/pprint (bp-summary bp))))


(deftest
 t38_l227
 (is ((fn [m] (= :species (get-in m [:shared :color]))) v37_l221)))


(def
 v39_l230
 (->
  (sk/xkcd7-sketch iris {:color :species})
  (sk/xkcd7-view :sepal_length :sepal_width)
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t40_l235
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v39_l230)))


(def
 v42_l243
 (let
  [bp
   (->
    iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    (sk/xkcd7-lay-lm {:color nil}))]
  (kind/pprint (bp-summary bp))))


(deftest
 t43_l249
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:shared :color]))
     (nil? (:color (second (:methods m))))))
   v42_l243)))


(def
 v44_l253
 (->
  iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-point
  (sk/xkcd7-lay-lm {:color nil})))


(deftest
 t45_l258
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v44_l253)))


(def
 v47_l266
 (let
  [bp
   (->
    (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-view :petal_length :petal_width)
    sk/xkcd7-lay-point)]
  (kind/pprint (bp-summary bp))))


(deftest
 t48_l272
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:shared :color]))
     (= 2 (count (:entries m)))
     (= 1 (count (:methods m)))))
   v47_l266)))


(def
 v49_l277
 (->
  (sk/xkcd7-sketch iris {:color :species})
  (sk/xkcd7-view :sepal_length :sepal_width)
  (sk/xkcd7-view :petal_length :petal_width)
  sk/xkcd7-lay-point))


(deftest
 t50_l282
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v49_l277)))


(def
 v52_l290
 (let
  [bp
   (->
    iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-annotate (sk/rule-h 3.0)))]
  (kind/pprint (bp-summary bp))))


(deftest
 t53_l295
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:entries m)))
     (= :rule-h (:mark (second (:entries m))))))
   v52_l290)))


(def
 v54_l299
 (->
  iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-annotate (sk/rule-h 3.0))))


(deftest
 t55_l303
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v54_l299)))


(def
 v57_l311
 (let
  [bp
   (->
    iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width)
    (sk/xkcd7-overlay :sepal_length :petal_width :lm))]
  (kind/pprint (bp-summary bp))))


(deftest
 t58_l316
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:entries m)))
     (= :sepal_width (:y (first (:entries m))))
     (= :petal_width (:y (second (:entries m))))
     (= :line (:mark (first (:methods (second (:entries m))))))))
   v57_l311)))


(def
 v60_l327
 (let
  [bp (-> {:x [1 2 3], :y [4 5 6]} sk/xkcd7-lay-point)]
  (kind/pprint (bp-summary bp))))


(deftest
 t61_l331
 (is
  ((fn
    [m]
    (and (= 1 (count (:entries m))) (= :x (:x (first (:entries m))))))
   v60_l327)))


(def v62_l335 (-> {:x [1 2 3], :y [4 5 6]} sk/xkcd7-lay-point))


(deftest
 t63_l338
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v62_l335)))


(def
 v65_l355
 (let
  [bp
   (->
    iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width)
    (sk/xkcd7-lay-histogram :petal_length))]
  (kind/pprint (bp-summary bp))))


(deftest
 t66_l360
 (is
  ((fn [m] (and (= 2 (count (:entries m))) (= 0 (count (:methods m)))))
   v65_l355)))


(def
 v67_l364
 (->
  iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width)
  (sk/xkcd7-lay-histogram :petal_length)))


(deftest
 t68_l368
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v67_l364)))


(def
 v70_l380
 (->
  iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width)
  (sk/xkcd7-lay-histogram :sepal_length)))


(deftest
 t71_l384
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v70_l380)))


(def
 v73_l397
 (->
  iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-lay-lm :sepal_length :sepal_width)))


(deftest
 t74_l401
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v73_l397)))


(def
 v76_l411
 (->
  iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width)
  (sk/xkcd7-lay-point :petal_length :petal_width)
  sk/xkcd7-lay-lm))


(deftest
 t77_l416
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:panels s)) (pos? (:points s)) (pos? (:lines s)))))
   v76_l411)))


(def
 v79_l426
 (def splom-cols [:sepal_length :sepal_width :petal_length]))


(def
 v80_l428
 (->
  (sk/xkcd7-sketch iris {:color :species})
  (sk/xkcd7-view (sk/cross splom-cols splom-cols))
  sk/xkcd7-lay-point))


(deftest
 t81_l432
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 9 (:panels s)) (= (* 9 150) (:points s)))))
   v80_l428)))
