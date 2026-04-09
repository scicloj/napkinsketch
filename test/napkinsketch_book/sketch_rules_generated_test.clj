(ns
 napkinsketch-book.sketch-rules-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l77
 (defn
  sk-summary
  "Show the sketch fields that matter for understanding the rules."
  [sk]
  {:mapping (:mapping sk),
   :views
   (mapv (fn* [p1__1928659#] (dissoc p1__1928659# :data)) (:views sk)),
   :layers (:layers sk),
   :opts (:opts sk)}))


(def
 v5_l90
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm
  sk-summary
  kind/pprint))


(deftest
 t6_l96
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :mapping :color]))
     (= 1 (count (:views m)))
     (= {} (:mapping m))
     (= 2 (count (:layers m)))))
   v5_l90)))


(def
 v7_l102
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t8_l107
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v7_l102)))


(def
 v10_l116
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm
  sk-summary
  kind/pprint))


(deftest
 t11_l122
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:layers m)))
     (= :point (:method (first (:layers m))))
     (nil? (:layers (first (:views m))))))
   v10_l116)))


(def
 v12_l127
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t13_l132
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v12_l127)))


(def
 v15_l140
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk-summary
  kind/pprint))


(deftest
 t16_l144
 (is
  ((fn
    [m]
    (and
     (= 0 (count (:layers m)))
     (= 1 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))))
   v15_l140)))


(def
 v17_l150
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t18_l153
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v17_l150)))


(def
 v20_l160
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm
  sk-summary
  kind/pprint))


(deftest
 t21_l165
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:layers m)))
     (= 1 (count (:layers (first (:views m)))))
     (= :point (:method (first (:layers (first (:views m))))))
     (= :lm (:method (first (:layers m))))))
   v20_l160)))


(def
 v22_l171
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm))


(deftest
 t23_l175
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v22_l171)))


(def
 v25_l184
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)
  sk-summary
  kind/pprint))


(deftest
 t26_l189
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= 0 (count (:layers m)))
     (= 2 (count (:layers (first (:views m)))))))
   v25_l184)))


(def
 v27_l194
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)))


(deftest
 t28_l198
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v27_l194)))


(def
 v30_l208
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  sk-summary
  kind/pprint))


(deftest
 t31_l213
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:views m)))
     (= 0 (count (:layers m)))
     (=
      [1 1]
      (mapv
       (fn* [p1__1928660#] (count (:layers p1__1928660#)))
       (:views m)))
     (= :sepal-length (get-in m [:views 0 :mapping :x]))
     (= :petal-length (get-in m [:views 1 :mapping :x]))))
   v30_l208)))


(def
 v32_l220
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t33_l224
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:polygons s)))))
   v32_l220)))


(def
 v35_l233
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm
  sk-summary
  kind/pprint))


(deftest
 t36_l239
 (is ((fn [m] (= :species (get-in m [:mapping :color]))) v35_l233)))


(def
 v37_l242
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t38_l247
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v37_l242)))


(def
 v40_l256
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})
  sk-summary
  kind/pprint))


(deftest
 t41_l262
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :mapping :color]))
     (nil? (get-in m [:layers 1 :mapping :color]))))
   v40_l256)))


(def
 v42_l266
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})))


(deftest
 t43_l271
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v42_l266)))


(def
 v45_l279
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary
  kind/pprint))


(deftest
 t46_l285
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:mapping :color]))
     (= 2 (count (:views m)))
     (= 1 (count (:layers m)))))
   v45_l279)))


(def
 v47_l290
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t48_l295
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v47_l290)))


(def
 v50_l303
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0))
  sk-summary
  kind/pprint))


(deftest
 t51_l308
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= :rule-h (:mark (first (get-in m [:opts :annotations]))))))
   v50_l303)))


(def
 v52_l312
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0))))


(deftest
 t53_l316
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v52_l312)))


(def
 v55_l324
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/overlay :sepal-length :petal-width :lm)
  sk-summary
  kind/pprint))


(deftest
 t56_l329
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:views m)))
     (= :sepal-width (get-in m [:views 0 :mapping :y]))
     (= :petal-width (get-in m [:views 1 :mapping :y]))
     (= :lm (:method (first (:layers (second (:views m))))))))
   v55_l324)))


(def
 v58_l340
 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point sk-summary kind/pprint))


(deftest
 t59_l344
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= :x (get-in m [:views 0 :mapping :x]))))
   v58_l340)))


(def v60_l348 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point))


(deftest
 t61_l351
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v60_l348)))


(def
 v63_l368
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  sk-summary
  kind/pprint))


(deftest
 t64_l373
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 0 (count (:layers m)))))
   v63_l368)))


(def
 v65_l377
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t66_l381
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v65_l377)))


(def
 v68_l393
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :sepal-length)))


(deftest
 t69_l397
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v68_l393)))


(def
 v71_l410
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)))


(deftest
 t72_l414
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v71_l410)))


(def
 v74_l424
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)
  sk/lay-lm))


(deftest
 t75_l429
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:panels s)) (pos? (:points s)) (pos? (:lines s)))))
   v74_l424)))


(def
 v77_l439
 (def splom-cols [:sepal-length :sepal-width :petal-length]))


(def
 v78_l441
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view (sk/cross splom-cols splom-cols))))


(deftest
 t79_l444
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 9 (:panels s))
      (= (* 6 150) (:points s))
      (pos? (:polygons s)))))
   v78_l441)))


(def
 v81_l458
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
 v82_l467
 (let
  [panel (first (:panels scale-plan))]
  {:coord (:coord panel),
   :x-scale (:x-scale panel),
   :y-scale (:y-scale panel),
   :x-domain (:x-domain panel)}))


(deftest
 t83_l473
 (is
  ((fn
    [m]
    (and
     (= :flip (:coord m))
     (= [0 6] (:x-domain m))
     (= {:type :linear, :domain [0 6]} (:x-scale m))
     (= {:type :log} (:y-scale m))))
   v82_l467)))


(def v85_l485 (select-keys scale-plan [:x-label :y-label]))


(deftest
 t86_l487
 (is
  ((fn
    [m]
    (and
     (= "sepal width" (:x-label m))
     (= "sepal length" (:y-label m))))
   v85_l485)))
