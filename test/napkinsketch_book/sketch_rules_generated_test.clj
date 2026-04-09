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
  {:mapping (:mapping sk),
   :views
   (mapv (fn* [p1__80485#] (dissoc p1__80485# :data)) (:views sk)),
   :layers (:layers sk),
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
     (= :species (get-in m [:views 0 :mapping :color]))
     (= 1 (count (:views m)))
     (= {} (:mapping m))
     (= 2 (count (:layers m)))))
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
     (= 2 (count (:layers m)))
     (= :point (:method (first (:layers m))))
     (nil? (:layers (first (:views m))))))
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
     (= 0 (count (:layers m)))
     (= 1 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))))
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
     (= 1 (count (:layers m)))
     (= 1 (count (:layers (first (:views m)))))
     (= :point (:method (first (:layers (first (:views m))))))
     (= :lm (:method (first (:layers m))))))
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
     (= 1 (count (:views m)))
     (= 0 (count (:layers m)))
     (= 2 (count (:layers (first (:views m)))))))
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
     (= 2 (count (:views m)))
     (= 0 (count (:layers m)))
     (=
      [1 1]
      (mapv
       (fn* [p1__80486#] (count (:layers p1__80486#)))
       (:views m)))
     (= :sepal-length (get-in m [:views 0 :mapping :x]))
     (= :petal-length (get-in m [:views 1 :mapping :x]))))
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
 (is ((fn [m] (= :species (get-in m [:mapping :color]))) v35_l229)))


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
 v40_l252
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})
  sk-summary
  kind/pprint))


(deftest
 t41_l258
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :mapping :color]))
     (nil? (get-in m [:layers 1 :mapping :color]))))
   v40_l252)))


(def
 v42_l262
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})))


(deftest
 t43_l267
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v42_l262)))


(def
 v45_l275
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary
  kind/pprint))


(deftest
 t46_l281
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:mapping :color]))
     (= 2 (count (:views m)))
     (= 1 (count (:layers m)))))
   v45_l275)))


(def
 v47_l286
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t48_l291
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v47_l286)))


(def
 v50_l299
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0))
  sk-summary
  kind/pprint))


(deftest
 t51_l304
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= :rule-h (:mark (first (get-in m [:opts :annotations]))))))
   v50_l299)))


(def
 v52_l308
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0))))


(deftest
 t53_l312
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v52_l308)))


(def
 v55_l320
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/overlay :sepal-length :petal-width :lm)
  sk-summary
  kind/pprint))


(deftest
 t56_l325
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:views m)))
     (= :sepal-width (get-in m [:views 0 :mapping :y]))
     (= :petal-width (get-in m [:views 1 :mapping :y]))
     (= :lm (:method (first (:layers (second (:views m))))))))
   v55_l320)))


(def
 v58_l336
 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point sk-summary kind/pprint))


(deftest
 t59_l340
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= :x (get-in m [:views 0 :mapping :x]))))
   v58_l336)))


(def v60_l344 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point))


(deftest
 t61_l347
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v60_l344)))


(def
 v63_l364
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  sk-summary
  kind/pprint))


(deftest
 t64_l369
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 0 (count (:layers m)))))
   v63_l364)))


(def
 v65_l373
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t66_l377
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v65_l373)))


(def
 v68_l389
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :sepal-length)))


(deftest
 t69_l393
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v68_l389)))


(def
 v71_l406
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)))


(deftest
 t72_l410
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v71_l406)))


(def
 v74_l420
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)
  sk/lay-lm))


(deftest
 t75_l425
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:panels s)) (pos? (:points s)) (pos? (:lines s)))))
   v74_l420)))


(def
 v77_l435
 (def splom-cols [:sepal-length :sepal-width :petal-length]))


(def
 v78_l437
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view (sk/cross splom-cols splom-cols))))


(deftest
 t79_l440
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 9 (:panels s))
      (= (* 6 150) (:points s))
      (pos? (:polygons s)))))
   v78_l437)))


(def
 v81_l454
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
 v82_l463
 (let
  [panel (first (:panels scale-plan))]
  {:coord (:coord panel),
   :x-scale (:x-scale panel),
   :y-scale (:y-scale panel),
   :x-domain (:x-domain panel)}))


(deftest
 t83_l469
 (is
  ((fn
    [m]
    (and
     (= :flip (:coord m))
     (= [0 6] (:x-domain m))
     (= {:type :linear, :domain [0 6]} (:x-scale m))
     (= {:type :log} (:y-scale m))))
   v82_l463)))


(def v85_l481 (select-keys scale-plan [:x-label :y-label]))


(deftest
 t86_l483
 (is
  ((fn
    [m]
    (and
     (= "sepal width" (:x-label m))
     (= "sepal length" (:y-label m))))
   v85_l481)))
