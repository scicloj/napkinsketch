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
 v21_l170
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


(deftest t22_l181 (is ((fn [t] (= 18 (count (:row-maps t)))) v21_l170)))


(def
 v24_l267
 (def
  my-plan
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   pj/plan)))


(def v25_l272 (first (pj/plan->plot my-plan :svg {})))


(deftest t26_l274 (is ((fn [v] (= :svg v)) v25_l272)))


(def v28_l278 (def my-figure (pj/plan->plot my-plan :svg {})))


(def v29_l280 (vector? my-figure))


(deftest t30_l282 (is ((fn [v] (true? v)) v29_l280)))


(def v32_l326 (def my-membrane (pj/plan->membrane my-plan)))


(def v33_l328 (vector? my-membrane))


(deftest t34_l330 (is ((fn [v] (true? v)) v33_l328)))


(def
 v35_l332
 (first
  (pj/membrane->plot
   my-membrane
   :svg
   {:total-width (:total-width my-plan),
    :total-height (:total-height my-plan)})))


(deftest t36_l336 (is ((fn [v] (= :svg v)) v35_l332)))


(def
 v38_l366
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


(deftest t39_l376 (is ((fn [t] (= 3 (count (:row-maps t)))) v38_l366)))


(def
 v41_l387
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


(deftest t42_l398 (is ((fn [t] (= 4 (count (:row-maps t)))) v41_l387)))


(def
 v44_l405
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t45_l409
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v44_l405)))


(def
 v47_l425
 (defmethod
  stat/compute-stat
  :quantile
  [draft-layer]
  {:points [], :x-domain [0 1], :y-domain [0 1]}))


(def
 v48_l428
 (defmethod
  stat/compute-stat
  [:quantile :doc]
  [_]
  "Quantile regression bands"))


(def v50_l433 (pj/stat-doc :quantile))


(deftest
 t51_l435
 (is ((fn [v] (= "Quantile regression bands" v)) v50_l433)))


(def v53_l443 (remove-method stat/compute-stat [:quantile :doc]))


(def v54_l445 (pj/stat-doc :quantile))


(deftest t55_l447 (is ((fn [v] (= "(no description)" v)) v54_l445)))


(def v57_l453 (remove-method stat/compute-stat :quantile))


(def
 v58_l455
 (count
  (remove
   #{:default}
   (filter keyword? (keys (methods stat/compute-stat))))))


(deftest t59_l457 (is ((fn [v] (= 11 v)) v58_l455)))
