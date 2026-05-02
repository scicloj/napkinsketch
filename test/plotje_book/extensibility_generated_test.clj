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
 v3_l37
 (kind/mermaid
  "\ngraph LR\n  B[\"Pose\"] -->|pj/pose->draft| D[\"Draft\"]\n  D -->|pj/draft->plan| P[\"Plan\"]\n  P -->|pj/plan->membrane| M[\"Membrane\"]\n  M -->|pj/membrane->plot| F[\"Plot\"]\n  P -.->|pj/plan->plot| F\n  style B fill:#d1c4e9\n  style D fill:#e8f5e9\n  style P fill:#fff3e0\n  style M fill:#e3f2fd\n  style F fill:#fce4ec\n"))


(def
 v5_l70
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


(deftest t6_l81 (is ((fn [t] (= 11 (count (:row-maps t)))) v5_l70)))


(def v8_l87 (layer-type/lookup :histogram))


(deftest t9_l89 (is ((fn [m] (= :bin (:stat m))) v8_l87)))


(def v11_l93 (layer-type/lookup :bar))


(deftest t12_l95 (is ((fn [m] (= :count (:stat m))) v11_l93)))


(def v14_l99 (layer-type/lookup :point))


(deftest t15_l101 (is ((fn [m] (= :identity (:stat m))) v14_l99)))


(def
 v17_l134
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


(deftest t18_l145 (is ((fn [t] (= 18 (count (:row-maps t)))) v17_l134)))


(def
 v20_l150
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t21_l153
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v20_l150)))


(def
 v23_l157
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
 t24_l163
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v23_l157)))


(def
 v26_l174
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


(deftest t27_l185 (is ((fn [t] (= 18 (count (:row-maps t)))) v26_l174)))


(def
 v29_l271
 (def
  my-plan
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   pj/plan)))


(def v30_l276 (first (pj/plan->plot my-plan :svg {})))


(deftest t31_l278 (is ((fn [v] (= :svg v)) v30_l276)))


(def v33_l282 (def my-figure (pj/plan->plot my-plan :svg {})))


(def v34_l284 (vector? my-figure))


(deftest t35_l286 (is ((fn [v] (true? v)) v34_l284)))


(def v37_l333 (def my-membrane (pj/plan->membrane my-plan)))


(def v38_l335 (vector? my-membrane))


(deftest t39_l337 (is ((fn [v] (true? v)) v38_l335)))


(def v40_l339 (:total-width (meta my-membrane)))


(deftest t41_l341 (is ((fn [v] (number? v)) v40_l339)))


(def v42_l343 (first (pj/membrane->plot my-membrane :svg {})))


(deftest t43_l345 (is ((fn [v] (= :svg v)) v42_l343)))


(def
 v45_l351
 (def
  shortcut-membrane
  (pj/membrane
   (->
    (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})))))


(def v46_l356 (vector? shortcut-membrane))


(deftest t47_l358 (is ((fn [v] (true? v)) v46_l356)))


(def
 v49_l394
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


(deftest t50_l404 (is ((fn [t] (= 3 (count (:row-maps t)))) v49_l394)))


(def
 v52_l415
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


(deftest t53_l426 (is ((fn [t] (= 4 (count (:row-maps t)))) v52_l415)))


(def
 v55_l433
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t56_l437
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v55_l433)))


(def
 v58_l453
 (defmethod
  stat/compute-stat
  :quantile
  [draft-layer]
  {:points [], :x-domain [0 1], :y-domain [0 1]}))


(def
 v59_l456
 (defmethod
  stat/compute-stat
  [:quantile :doc]
  [_]
  "Quantile regression bands"))


(def v61_l461 (pj/stat-doc :quantile))


(deftest
 t62_l463
 (is ((fn [v] (= "Quantile regression bands" v)) v61_l461)))


(def v64_l471 (remove-method stat/compute-stat [:quantile :doc]))


(def v65_l473 (pj/stat-doc :quantile))


(deftest t66_l475 (is ((fn [v] (= "(no description)" v)) v65_l473)))


(def v68_l481 (remove-method stat/compute-stat :quantile))


(def
 v69_l483
 (count
  (remove
   #{:default}
   (filter keyword? (keys (methods stat/compute-stat))))))


(deftest t70_l485 (is ((fn [v] (= 11 v)) v69_l483)))
