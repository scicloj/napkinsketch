(ns
 napkinsketch-book.extensibility-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [scicloj.napkinsketch.impl.stat :as stat]
  [scicloj.napkinsketch.impl.extract :as extract]
  [scicloj.napkinsketch.render.mark :as mark]
  [scicloj.napkinsketch.render.svg :as svg]
  [scicloj.napkinsketch.impl.render :as render]
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
    sort
    (mapv
     (fn
      [k]
      {"Dispatch value" (kind/code (pr-str k)),
       "What it does" (sk/stat-doc k)})))}))


(deftest t4_l82 (is ((fn [t] (= 11 (count (:row-maps t)))) v3_l72)))


(def v6_l90 (method/lookup :histogram))


(deftest t7_l92 (is ((fn [m] (= :bin (:stat m))) v6_l90)))


(def v9_l96 (method/lookup :bar))


(deftest t10_l98 (is ((fn [m] (= :count (:stat m))) v9_l96)))


(def v12_l102 (method/lookup :point))


(deftest t13_l104 (is ((fn [m] (= :identity (:stat m))) v12_l102)))


(def
 v15_l133
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
       "Output" (sk/mark-doc k)})))}))


(deftest t16_l144 (is ((fn [t] (= 17 (count (:row-maps t)))) v15_l133)))


(def
 v18_l150
 (let
  [s
   (->
    (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk/plan)
   layer
   (first (:layers (first (:panels s))))]
  layer))


(deftest
 t19_l156
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v18_l150)))


(def
 v21_l165
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
       "Membrane output" (sk/membrane-mark-doc k)})))}))


(deftest t22_l176 (is ((fn [t] (= 17 (count (:row-maps t)))) v21_l165)))


(def
 v24_l243
 (def
  my-plan
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   sk/plan)))


(def v25_l248 (first (sk/plan->figure my-plan :svg {})))


(deftest t26_l250 (is ((fn [v] (= :svg v)) v25_l248)))


(def v28_l254 (def my-figure (sk/plan->figure my-plan :svg {})))


(def v29_l256 (vector? my-figure))


(deftest t30_l258 (is ((fn [v] (true? v)) v29_l256)))


(def v32_l299 (def my-membrane (sk/plan->membrane my-plan)))


(def v33_l301 (vector? my-membrane))


(deftest t34_l303 (is ((fn [v] (true? v)) v33_l301)))


(def
 v35_l305
 (first
  (sk/membrane->figure
   my-membrane
   :svg
   {:total-width (:total-width my-plan),
    :total-height (:total-height my-plan)})))


(deftest t36_l309 (is ((fn [v] (= :svg v)) v35_l305)))


(def
 v38_l337
 (kind/table
  {:column-names ["Dispatch value" "Scale type"],
   :row-maps
   (->>
    (methods scicloj.napkinsketch.impl.scale/make-scale)
    keys
    (filter keyword?)
    sort
    (mapv
     (fn
      [k]
      {"Dispatch value" (kind/code (pr-str k)),
       "Scale type" (sk/scale-doc k)})))}))


(deftest t39_l347 (is ((fn [t] (= 3 (count (:row-maps t)))) v38_l337)))


(def
 v41_l358
 (kind/table
  {:column-names ["Dispatch value" "Behavior"],
   :row-maps
   (->>
    (methods scicloj.napkinsketch.impl.coord/make-coord)
    keys
    (filter keyword?)
    sort
    (mapv
     (fn
      [k]
      {"Dispatch value" (kind/code (pr-str k)),
       "Behavior" (sk/coord-doc k)})))}))


(deftest t42_l368 (is ((fn [t] (= 4 (count (:row-maps t)))) v41_l358)))


(def
 v44_l375
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t45_l379
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v44_l375)))


(def
 v47_l395
 (defmethod
  stat/compute-stat
  :quantile
  [view]
  {:points [], :x-domain [0 1], :y-domain [0 1]}))


(def
 v48_l398
 (defmethod
  stat/compute-stat
  [:quantile :doc]
  [_]
  "Quantile regression bands"))


(def v50_l403 (sk/stat-doc :quantile))


(deftest
 t51_l405
 (is ((fn [v] (= "Quantile regression bands" v)) v50_l403)))


(def v53_l413 (remove-method stat/compute-stat [:quantile :doc]))


(def v54_l415 (sk/stat-doc :quantile))


(deftest t55_l417 (is ((fn [v] (= "(no description)" v)) v54_l415)))


(def v57_l423 (remove-method stat/compute-stat :quantile))


(def
 v58_l425
 (count (filter keyword? (keys (methods stat/compute-stat)))))


(deftest t59_l427 (is ((fn [v] (= 11 v)) v58_l425)))
