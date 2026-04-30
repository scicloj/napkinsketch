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
 v3_l74
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


(deftest t4_l85 (is ((fn [t] (= 11 (count (:row-maps t)))) v3_l74)))


(def v6_l91 (layer-type/lookup :histogram))


(deftest t7_l93 (is ((fn [m] (= :bin (:stat m))) v6_l91)))


(def v9_l97 (layer-type/lookup :bar))


(deftest t10_l99 (is ((fn [m] (= :count (:stat m))) v9_l97)))


(def v12_l103 (layer-type/lookup :point))


(deftest t13_l105 (is ((fn [m] (= :identity (:stat m))) v12_l103)))


(def
 v15_l138
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


(deftest t16_l149 (is ((fn [t] (= 18 (count (:row-maps t)))) v15_l138)))


(def
 v18_l154
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t19_l157
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v18_l154)))


(def
 v21_l161
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
 t22_l167
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v21_l161)))


(def
 v24_l178
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


(deftest t25_l189 (is ((fn [t] (= 18 (count (:row-maps t)))) v24_l178)))


(def
 v27_l275
 (def
  my-plan
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   pj/plan)))


(def v28_l280 (first (pj/plan->plot my-plan :svg {})))


(deftest t29_l282 (is ((fn [v] (= :svg v)) v28_l280)))


(def v31_l286 (def my-figure (pj/plan->plot my-plan :svg {})))


(def v32_l288 (vector? my-figure))


(deftest t33_l290 (is ((fn [v] (true? v)) v32_l288)))


(def v35_l334 (def my-membrane (pj/plan->membrane my-plan)))


(def v36_l336 (vector? my-membrane))


(deftest t37_l338 (is ((fn [v] (true? v)) v36_l336)))


(def
 v38_l340
 (first
  (pj/membrane->plot
   my-membrane
   :svg
   {:total-width (:total-width my-plan),
    :total-height (:total-height my-plan)})))


(deftest t39_l344 (is ((fn [v] (= :svg v)) v38_l340)))


(def
 v41_l374
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


(deftest t42_l384 (is ((fn [t] (= 3 (count (:row-maps t)))) v41_l374)))


(def
 v44_l395
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


(deftest t45_l406 (is ((fn [t] (= 4 (count (:row-maps t)))) v44_l395)))


(def
 v47_l413
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t48_l417
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v47_l413)))


(def
 v50_l433
 (defmethod
  stat/compute-stat
  :quantile
  [draft-layer]
  {:points [], :x-domain [0 1], :y-domain [0 1]}))


(def
 v51_l436
 (defmethod
  stat/compute-stat
  [:quantile :doc]
  [_]
  "Quantile regression bands"))


(def v53_l441 (pj/stat-doc :quantile))


(deftest
 t54_l443
 (is ((fn [v] (= "Quantile regression bands" v)) v53_l441)))


(def v56_l451 (remove-method stat/compute-stat [:quantile :doc]))


(def v57_l453 (pj/stat-doc :quantile))


(deftest t58_l455 (is ((fn [v] (= "(no description)" v)) v57_l453)))


(def v60_l461 (remove-method stat/compute-stat :quantile))


(def
 v61_l463
 (count
  (remove
   #{:default}
   (filter keyword? (keys (methods stat/compute-stat))))))


(deftest t62_l465 (is ((fn [v] (= 11 v)) v61_l463)))
