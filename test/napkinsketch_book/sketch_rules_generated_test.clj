(ns
 napkinsketch-book.sketch-rules-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l90
 (defn
  sk-summary
  "Show the sketch fields that matter for understanding the rules."
  [sk]
  {:mapping (:mapping sk),
   :views
   (mapv (fn* [p1__85135#] (dissoc p1__85135# :data)) (:views sk)),
   :layers (:layers sk),
   :opts (:opts sk)}))


(def
 v5_l103
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm
  sk-summary
  kind/pprint))


(deftest
 t6_l109
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :mapping :color]))
     (= 1 (count (:views m)))
     (= {} (:mapping m))
     (= 2 (count (:layers m)))))
   v5_l103)))


(def
 v7_l115
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t8_l120
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v7_l115)))


(def
 v10_l129
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm
  sk-summary
  kind/pprint))


(deftest
 t11_l135
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:layers m)))
     (= :point (:method (first (:layers m))))
     (nil? (:layers (first (:views m))))))
   v10_l129)))


(def
 v12_l140
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t13_l145
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v12_l140)))


(def
 v15_l153
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk-summary
  kind/pprint))


(deftest
 t16_l157
 (is
  ((fn
    [m]
    (and
     (= 0 (count (:layers m)))
     (= 1 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))))
   v15_l153)))


(def
 v17_l163
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t18_l166
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v17_l163)))


(def
 v20_l173
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm
  sk-summary
  kind/pprint))


(deftest
 t21_l178
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:layers m)))
     (= 1 (count (:layers (first (:views m)))))
     (= :point (:method (first (:layers (first (:views m))))))
     (= :lm (:method (first (:layers m))))))
   v20_l173)))


(def
 v22_l184
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm))


(deftest
 t23_l188
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v22_l184)))


(def
 v25_l197
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)
  sk-summary
  kind/pprint))


(deftest
 t26_l202
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= 0 (count (:layers m)))
     (= 2 (count (:layers (first (:views m)))))))
   v25_l197)))


(def
 v27_l207
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)))


(deftest
 t28_l211
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v27_l207)))


(def
 v30_l221
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  sk-summary
  kind/pprint))


(deftest
 t31_l226
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:views m)))
     (= 0 (count (:layers m)))
     (=
      [1 1]
      (mapv
       (fn* [p1__85136#] (count (:layers p1__85136#)))
       (:views m)))
     (= :sepal-length (get-in m [:views 0 :mapping :x]))
     (= :petal-length (get-in m [:views 1 :mapping :x]))))
   v30_l221)))


(def
 v32_l233
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t33_l237
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:polygons s)))))
   v32_l233)))


(def
 v35_l246
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm
  sk-summary
  kind/pprint))


(deftest
 t36_l252
 (is ((fn [m] (= :species (get-in m [:mapping :color]))) v35_l246)))


(def
 v37_l255
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t38_l260
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v37_l255)))


(def
 v40_l269
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})
  sk-summary
  kind/pprint))


(deftest
 t41_l275
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :mapping :color]))
     (nil? (get-in m [:layers 1 :mapping :color]))))
   v40_l269)))


(def
 v42_l279
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})))


(deftest
 t43_l284
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v42_l279)))


(def
 v45_l292
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary
  kind/pprint))


(deftest
 t46_l298
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:mapping :color]))
     (= 2 (count (:views m)))
     (= 1 (count (:layers m)))))
   v45_l292)))


(def
 v47_l303
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t48_l308
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v47_l303)))


(def
 v50_l316
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0))
  sk-summary
  kind/pprint))


(deftest
 t51_l321
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= :rule-h (:mark (first (get-in m [:opts :annotations]))))))
   v50_l316)))


(def
 v52_l325
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0))))


(deftest
 t53_l329
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v52_l325)))


(def
 v55_l337
 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point sk-summary kind/pprint))


(deftest
 t56_l341
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= :x (get-in m [:views 0 :mapping :x]))))
   v55_l337)))


(def v57_l345 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point))


(deftest
 t58_l348
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v57_l345)))


(def
 v60_l365
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  sk-summary
  kind/pprint))


(deftest
 t61_l370
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 0 (count (:layers m)))))
   v60_l365)))


(def
 v62_l374
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t63_l378
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v62_l374)))


(def
 v65_l390
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :sepal-length)))


(deftest
 t66_l394
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v65_l390)))


(def
 v68_l407
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)))


(deftest
 t69_l411
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v68_l407)))


(def
 v71_l421
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)
  sk/lay-lm))


(deftest
 t72_l426
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:panels s)) (pos? (:points s)) (pos? (:lines s)))))
   v71_l421)))


(def
 v74_l436
 (def splom-cols [:sepal-length :sepal-width :petal-length]))


(def
 v75_l438
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view (sk/cross splom-cols splom-cols))))


(deftest
 t76_l441
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 9 (:panels s))
      (= (* 6 150) (:points s))
      (pos? (:polygons s)))))
   v75_l438)))


(def
 v78_l455
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
 v79_l464
 (let
  [panel (first (:panels scale-plan))]
  {:coord (:coord panel),
   :x-scale (:x-scale panel),
   :y-scale (:y-scale panel),
   :x-domain (:x-domain panel)}))


(deftest
 t80_l470
 (is
  ((fn
    [m]
    (and
     (= :flip (:coord m))
     (= [0 6] (:x-domain m))
     (= {:type :linear, :domain [0 6]} (:x-scale m))
     (= {:type :log} (:y-scale m))))
   v79_l464)))


(def v82_l482 (select-keys scale-plan [:x-label :y-label]))


(deftest
 t83_l484
 (is
  ((fn
    [m]
    (and
     (= "sepal width" (:x-label m))
     (= "sepal length" (:y-label m))))
   v82_l482)))
