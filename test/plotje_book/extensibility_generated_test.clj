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
  [membrane.ui]
  [clojure.test :refer [deftest is]]))


(def
 v3_l39
 (kind/mermaid
  "\ngraph LR\n  B[\"Pose\"] -->|pj/pose->draft| D[\"Draft\"]\n  D -->|pj/draft->plan| P[\"Plan\"]\n  P -->|pj/plan->membrane| M[\"Membrane\"]\n  M -->|pj/membrane->plot| F[\"Plot\"]\n  P -.->|pj/plan->plot| F\n  style B fill:#d1c4e9\n  style D fill:#e8f5e9\n  style P fill:#fff3e0\n  style M fill:#e3f2fd\n  style F fill:#fce4ec\n"))


(def
 v5_l72
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


(deftest t6_l83 (is ((fn [t] (= 11 (count (:row-maps t)))) v5_l72)))


(def v8_l89 (layer-type/lookup :histogram))


(deftest t9_l91 (is ((fn [m] (= :bin (:stat m))) v8_l89)))


(def v11_l95 (layer-type/lookup :bar))


(deftest t12_l97 (is ((fn [m] (= :count (:stat m))) v11_l95)))


(def v14_l101 (layer-type/lookup :point))


(deftest t15_l103 (is ((fn [m] (= :identity (:stat m))) v14_l101)))


(def
 v17_l136
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


(deftest t18_l147 (is ((fn [t] (= 18 (count (:row-maps t)))) v17_l136)))


(def
 v20_l152
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t21_l155
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v20_l152)))


(def
 v23_l159
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
 t24_l165
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v23_l159)))


(def
 v26_l176
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


(deftest t27_l187 (is ((fn [t] (= 18 (count (:row-maps t)))) v26_l176)))


(def
 v29_l273
 (def
  my-plan
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   pj/plan)))


(def v30_l278 (first (pj/plan->plot my-plan :svg {})))


(deftest t31_l280 (is ((fn [v] (= :svg v)) v30_l278)))


(def v33_l284 (def my-figure (pj/plan->plot my-plan :svg {})))


(def v34_l286 (vector? my-figure))


(deftest t35_l288 (is ((fn [v] (true? v)) v34_l286)))


(def v37_l338 (def my-membrane (pj/plan->membrane my-plan)))


(def v38_l340 (pj/membrane? my-membrane))


(deftest t39_l342 (is ((fn [v] (true? v)) v38_l340)))


(def v40_l344 (membrane.ui/width my-membrane))


(deftest t41_l346 (is ((fn [v] (number? v)) v40_l344)))


(def v42_l348 (first (pj/membrane->plot my-membrane :svg {})))


(deftest t43_l350 (is ((fn [v] (= :svg v)) v42_l348)))


(def
 v45_l356
 (def
  shortcut-membrane
  (pj/membrane
   (->
    (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})))))


(def v46_l361 (pj/membrane? shortcut-membrane))


(deftest t47_l363 (is ((fn [v] (true? v)) v46_l361)))


(def
 v49_l402
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


(deftest t50_l412 (is ((fn [t] (= 3 (count (:row-maps t)))) v49_l402)))


(def
 v52_l423
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


(deftest t53_l434 (is ((fn [t] (= 4 (count (:row-maps t)))) v52_l423)))


(def
 v55_l441
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t56_l445
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v55_l441)))


(def
 v58_l461
 (defmethod
  stat/compute-stat
  :quantile
  [draft-layer]
  {:points [], :x-domain [0 1], :y-domain [0 1]}))


(def
 v59_l464
 (defmethod
  stat/compute-stat
  [:quantile :doc]
  [_]
  "Quantile regression bands"))


(def v61_l469 (pj/stat-doc :quantile))


(deftest
 t62_l471
 (is ((fn [v] (= "Quantile regression bands" v)) v61_l469)))


(def v64_l479 (remove-method stat/compute-stat [:quantile :doc]))


(def v65_l481 (pj/stat-doc :quantile))


(deftest t66_l483 (is ((fn [v] (= "(no description)" v)) v65_l481)))


(def v68_l489 (remove-method stat/compute-stat :quantile))


(def
 v69_l491
 (count
  (remove
   #{:default}
   (filter keyword? (keys (methods stat/compute-stat))))))


(deftest t70_l493 (is ((fn [v] (= 11 v)) v69_l491)))
