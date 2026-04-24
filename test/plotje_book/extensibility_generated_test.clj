(ns
 plotje-book.extensibility-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [scicloj.plotje.layer-type :as layer-type]
  [scicloj.plotje.impl.stat :as stat]
  [scicloj.plotje.impl.extract :as extract]
  [scicloj.plotje.render.mark :as mark]
  [scicloj.plotje.render.svg :as svg]
  [scicloj.plotje.impl.render :as render]
  [clojure.test :refer [deftest is]]))


(def
 v3_l72
 (kind/table
  {:column-names ["Dispatch value" "What it does"],
   :row-maps
   (->>
    (methods stat/compute-stat)
    keys
    (filter keyword?)
    (remove #{:default})
    sort
    (mapv
     (fn
      [k]
      {"Dispatch value" (kind/code (pr-str k)),
       "What it does" (pj/stat-doc k)})))}))


(deftest t4_l83 (is ((fn [t] (= 11 (count (:row-maps t)))) v3_l72)))


(def v6_l91 (layer-type/lookup :histogram))


(deftest t7_l93 (is ((fn [m] (= :bin (:stat m))) v6_l91)))


(def v9_l97 (layer-type/lookup :bar))


(deftest t10_l99 (is ((fn [m] (= :count (:stat m))) v9_l97)))


(def v12_l103 (layer-type/lookup :point))


(deftest t13_l105 (is ((fn [m] (= :identity (:stat m))) v12_l103)))


(def
 v15_l136
 (kind/table
  {:column-names ["Dispatch value" "Output"],
   :row-maps
   (->>
    (methods extract/extract-layer)
    keys
    (filter keyword?)
    (remove #{:default})
    sort
    (mapv
     (fn
      [k]
      {"Dispatch value" (kind/code (pr-str k)),
       "Output" (pj/mark-doc k)})))}))


(deftest t16_l147 (is ((fn [t] (= 17 (count (:row-maps t)))) v15_l136)))


(def
 v18_l153
 (let
  [s
   (->
    (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    pj/plan)
   layer
   (first (:layers (first (:panels s))))]
  layer))


(deftest
 t19_l159
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v18_l153)))


(def
 v21_l168
 (kind/table
  {:column-names ["Dispatch value" "Membrane output"],
   :row-maps
   (->>
    (methods mark/layer->membrane)
    keys
    (filter keyword?)
    (remove #{:default})
    sort
    (mapv
     (fn
      [k]
      {"Dispatch value" (kind/code (pr-str k)),
       "Membrane output" (pj/membrane-mark-doc k)})))}))


(deftest t22_l179 (is ((fn [t] (= 17 (count (:row-maps t)))) v21_l168)))


(def
 v24_l248
 (def
  my-plan
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   pj/plan)))


(def v25_l253 (first (pj/plan->plot my-plan :svg {})))


(deftest t26_l255 (is ((fn [v] (= :svg v)) v25_l253)))


(def v28_l259 (def my-figure (pj/plan->plot my-plan :svg {})))


(def v29_l261 (vector? my-figure))


(deftest t30_l263 (is ((fn [v] (true? v)) v29_l261)))


(def v32_l306 (def my-membrane (pj/plan->membrane my-plan)))


(def v33_l308 (vector? my-membrane))


(deftest t34_l310 (is ((fn [v] (true? v)) v33_l308)))


(def
 v35_l312
 (first
  (pj/membrane->plot
   my-membrane
   :svg
   {:total-width (:total-width my-plan),
    :total-height (:total-height my-plan)})))


(deftest t36_l316 (is ((fn [v] (= :svg v)) v35_l312)))


(def
 v38_l346
 (kind/table
  {:column-names ["Dispatch value" "Scale type"],
   :row-maps
   (->>
    (methods scicloj.plotje.impl.scale/make-scale)
    keys
    (filter keyword?)
    sort
    (mapv
     (fn
      [k]
      {"Dispatch value" (kind/code (pr-str k)),
       "Scale type" (pj/scale-doc k)})))}))


(deftest t39_l356 (is ((fn [t] (= 3 (count (:row-maps t)))) v38_l346)))


(def
 v41_l367
 (kind/table
  {:column-names ["Dispatch value" "Behavior"],
   :row-maps
   (->>
    (methods scicloj.plotje.impl.coord/make-coord)
    keys
    (filter keyword?)
    (remove #{:default})
    sort
    (mapv
     (fn
      [k]
      {"Dispatch value" (kind/code (pr-str k)),
       "Behavior" (pj/coord-doc k)})))}))


(deftest t42_l378 (is ((fn [t] (= 4 (count (:row-maps t)))) v41_l367)))


(def
 v44_l385
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t45_l389
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v44_l385)))


(def
 v47_l405
 (defmethod
  stat/compute-stat
  :quantile
  [view]
  {:points [], :x-domain [0 1], :y-domain [0 1]}))


(def
 v48_l408
 (defmethod
  stat/compute-stat
  [:quantile :doc]
  [_]
  "Quantile regression bands"))


(def v50_l413 (pj/stat-doc :quantile))


(deftest
 t51_l415
 (is ((fn [v] (= "Quantile regression bands" v)) v50_l413)))


(def v53_l423 (remove-method stat/compute-stat [:quantile :doc]))


(def v54_l425 (pj/stat-doc :quantile))


(deftest t55_l427 (is ((fn [v] (= "(no description)" v)) v54_l425)))


(def v57_l433 (remove-method stat/compute-stat :quantile))


(def
 v58_l435
 (count
  (remove
   #{:default}
   (filter keyword? (keys (methods stat/compute-stat))))))


(deftest t59_l437 (is ((fn [v] (= 11 v)) v58_l435)))
