(ns
 napkinsketch-book.sketch-rules-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l73
 (defn
  sk-summary
  "Show the sketch fields that matter for understanding the rules."
  [sk]
  {:shared (:shared sk),
   :entries
   (mapv
    (fn* [p1__1906739#] (dissoc p1__1906739# :data))
    (:entries sk)),
   :methods (:methods sk),
   :opts (:opts sk)}))


(def
 v5_l86
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm
  sk-summary
  kind/pprint))


(deftest
 t6_l92
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:shared :color]))
     (= 1 (count (:entries m)))
     (nil? (:color (first (:entries m))))
     (= 2 (count (:methods m)))))
   v5_l86)))


(def
 v7_l98
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t8_l103
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v7_l98)))


(def
 v10_l112
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm
  sk-summary
  kind/pprint))


(deftest
 t11_l118
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:methods m)))
     (= :point (:mark (first (:methods m))))
     (nil? (:methods (first (:entries m))))))
   v10_l112)))


(def
 v12_l123
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t13_l128
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v12_l123)))


(def
 v15_l136
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk-summary
  kind/pprint))


(deftest
 t16_l140
 (is
  ((fn
    [m]
    (and
     (= 0 (count (:methods m)))
     (= 1 (count (:entries m)))
     (= 1 (count (:methods (first (:entries m)))))
     (= :species (:color (first (:methods (first (:entries m))))))))
   v15_l136)))


(def
 v17_l146
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t18_l149
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v17_l146)))


(def
 v20_l156
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm
  sk-summary
  kind/pprint))


(deftest
 t21_l161
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:methods m)))
     (= 1 (count (:methods (first (:entries m)))))
     (= :point (:mark (first (:methods (first (:entries m))))))
     (= :line (:mark (first (:methods m))))))
   v20_l156)))


(def
 v22_l167
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm))


(deftest
 t23_l171
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v22_l167)))


(def
 v25_l180
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)
  sk-summary
  kind/pprint))


(deftest
 t26_l185
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:entries m)))
     (= 0 (count (:methods m)))
     (= 2 (count (:methods (first (:entries m)))))))
   v25_l180)))


(def
 v27_l190
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)))


(deftest
 t28_l194
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v27_l190)))


(def
 v30_l204
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  sk-summary
  kind/pprint))


(deftest
 t31_l209
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:entries m)))
     (= 0 (count (:methods m)))
     (=
      [1 1]
      (mapv
       (fn* [p1__1906740#] (count (:methods p1__1906740#)))
       (:entries m)))
     (= :sepal-length (:x (first (:entries m))))
     (= :petal-length (:x (second (:entries m))))))
   v30_l204)))


(def
 v32_l216
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t33_l220
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:polygons s)))))
   v32_l216)))


(def
 v35_l229
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm
  sk-summary
  kind/pprint))


(deftest
 t36_l235
 (is ((fn [m] (= :species (get-in m [:shared :color]))) v35_l229)))


(def
 v37_l238
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t38_l243
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v37_l238)))


(def
 v40_l251
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})
  sk-summary
  kind/pprint))


(deftest
 t41_l257
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:shared :color]))
     (nil? (:color (second (:methods m))))))
   v40_l251)))


(def
 v42_l261
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})))


(deftest
 t43_l266
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v42_l261)))


(def
 v45_l274
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary
  kind/pprint))


(deftest
 t46_l280
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:shared :color]))
     (= 2 (count (:entries m)))
     (= 1 (count (:methods m)))))
   v45_l274)))


(def
 v47_l285
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t48_l290
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v47_l285)))


(def
 v50_l298
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0))
  sk-summary
  kind/pprint))


(deftest
 t51_l303
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:entries m)))
     (= :rule-h (:mark (second (:entries m))))))
   v50_l298)))


(def
 v52_l307
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0))))


(deftest
 t53_l311
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v52_l307)))


(def
 v55_l319
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/overlay :sepal-length :petal-width :lm)
  sk-summary
  kind/pprint))


(deftest
 t56_l324
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:entries m)))
     (= :sepal-width (:y (first (:entries m))))
     (= :petal-width (:y (second (:entries m))))
     (= :line (:mark (first (:methods (second (:entries m))))))))
   v55_l319)))


(def
 v58_l335
 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point sk-summary kind/pprint))


(deftest
 t59_l339
 (is
  ((fn
    [m]
    (and (= 1 (count (:entries m))) (= :x (:x (first (:entries m))))))
   v58_l335)))


(def v60_l343 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point))


(deftest
 t61_l346
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v60_l343)))


(def
 v63_l363
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  sk-summary
  kind/pprint))


(deftest
 t64_l368
 (is
  ((fn [m] (and (= 2 (count (:entries m))) (= 0 (count (:methods m)))))
   v63_l363)))


(def
 v65_l372
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t66_l376
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v65_l372)))


(def
 v68_l388
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :sepal-length)))


(deftest
 t69_l392
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v68_l388)))


(def
 v71_l405
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)))


(deftest
 t72_l409
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v71_l405)))


(def
 v74_l419
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)
  sk/lay-lm))


(deftest
 t75_l424
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:panels s)) (pos? (:points s)) (pos? (:lines s)))))
   v74_l419)))


(def
 v77_l434
 (def splom-cols [:sepal-length :sepal-width :petal-length]))


(def
 v78_l436
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view (sk/cross splom-cols splom-cols))))


(deftest
 t79_l439
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 9 (:panels s))
      (= (* 6 150) (:points s))
      (pos? (:polygons s)))))
   v78_l436)))


(def
 v81_l453
 (def
  scale-plan
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width)
   sk/lay-point
   (sk/scale :x :log)
   (sk/scale :y {:domain [0 6]})
   (sk/coord :flip)
   sk/plan)))


(def
 v82_l462
 (let
  [panel (first (:panels scale-plan))]
  {:coord (:coord panel),
   :x-scale (:x-scale panel),
   :y-scale (:y-scale panel),
   :x-domain (:x-domain panel)}))


(deftest
 t83_l468
 (is
  ((fn
    [m]
    (and
     (= :flip (:coord m))
     (= [0 6] (:x-domain m))
     (= {:type :linear, :domain [0 6]} (:x-scale m))
     (= {:type :log} (:y-scale m))))
   v82_l462)))


(def v85_l480 (select-keys scale-plan [:x-label :y-label]))


(deftest
 t86_l482
 (is
  ((fn
    [m]
    (and
     (= "sepal width" (:x-label m))
     (= "sepal length" (:y-label m))))
   v85_l480)))
