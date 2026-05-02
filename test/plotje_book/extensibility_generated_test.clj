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


(def v35_l337 (def my-membrane (pj/plan->membrane my-plan)))


(def v36_l339 (vector? my-membrane))


(deftest t37_l341 (is ((fn [v] (true? v)) v36_l339)))


(def v38_l343 (:total-width (meta my-membrane)))


(deftest t39_l345 (is ((fn [v] (number? v)) v38_l343)))


(def v40_l347 (first (pj/membrane->plot my-membrane :svg {})))


(deftest t41_l349 (is ((fn [v] (= :svg v)) v40_l347)))


(def
 v43_l355
 (def
  shortcut-membrane
  (pj/membrane
   (->
    (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})))))


(def v44_l360 (vector? shortcut-membrane))


(deftest t45_l362 (is ((fn [v] (true? v)) v44_l360)))


(def
 v47_l398
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


(deftest t48_l408 (is ((fn [t] (= 3 (count (:row-maps t)))) v47_l398)))


(def
 v50_l419
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


(deftest t51_l430 (is ((fn [t] (= 4 (count (:row-maps t)))) v50_l419)))


(def
 v53_l437
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t54_l441
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v53_l437)))


(def
 v56_l457
 (defmethod
  stat/compute-stat
  :quantile
  [draft-layer]
  {:points [], :x-domain [0 1], :y-domain [0 1]}))


(def
 v57_l460
 (defmethod
  stat/compute-stat
  [:quantile :doc]
  [_]
  "Quantile regression bands"))


(def v59_l465 (pj/stat-doc :quantile))


(deftest
 t60_l467
 (is ((fn [v] (= "Quantile regression bands" v)) v59_l465)))


(def v62_l475 (remove-method stat/compute-stat [:quantile :doc]))


(def v63_l477 (pj/stat-doc :quantile))


(deftest t64_l479 (is ((fn [v] (= "(no description)" v)) v63_l477)))


(def v66_l485 (remove-method stat/compute-stat :quantile))


(def
 v67_l487
 (count
  (remove
   #{:default}
   (filter keyword? (keys (methods stat/compute-stat))))))


(deftest t68_l489 (is ((fn [v] (= 11 v)) v67_l487)))
