(ns
 napkinsketch-book.extensibility-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [scicloj.napkinsketch.impl.stat :as stat]
  [scicloj.napkinsketch.impl.extract :as extract]
  [scicloj.napkinsketch.impl.sketch :as sketch]
  [scicloj.napkinsketch.render.mark :as mark]
  [scicloj.napkinsketch.render.svg :as svg]
  [scicloj.napkinsketch.impl.render :as render]
  [clojure.test :refer [deftest is]]))


(def
 v3_l66
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


(deftest t4_l76 (is ((fn [t] (= 11 (count (:row-maps t)))) v3_l66)))


(def v6_l84 (method/lookup :histogram))


(deftest t7_l86 (is ((fn [m] (= :bin (:stat m))) v6_l84)))


(def v9_l90 (method/lookup :bar))


(deftest t10_l92 (is ((fn [m] (= :count (:stat m))) v9_l90)))


(def v12_l96 (method/lookup :point))


(deftest t13_l98 (is ((fn [m] (= :identity (:stat m))) v12_l96)))


(def
 v15_l127
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


(deftest t16_l138 (is ((fn [t] (= 17 (count (:row-maps t)))) v15_l127)))


(def
 v18_l144
 (let
  [s
   (->
    data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    sk/plan)
   layer
   (first (:layers (first (:panels s))))]
  layer))


(deftest
 t19_l150
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v18_l144)))


(def
 v21_l159
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


(deftest t22_l170 (is ((fn [t] (= 17 (count (:row-maps t)))) v21_l159)))


(def
 v24_l233
 (def
  my-plan
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width {:color :species})
   sk/plan)))


(def v25_l238 (first (sk/plan->figure my-plan :svg {})))


(deftest t26_l240 (is ((fn [v] (= :svg v)) v25_l238)))


(def v28_l244 (def my-figure (sk/plan->figure my-plan :svg {})))


(def v29_l246 (vector? my-figure))


(deftest t30_l248 (is ((fn [v] (true? v)) v29_l246)))


(def v32_l289 (def my-membrane (sk/plan->membrane my-plan)))


(def v33_l291 (vector? my-membrane))


(deftest t34_l293 (is ((fn [v] (true? v)) v33_l291)))


(def
 v35_l295
 (first
  (sk/membrane->figure
   my-membrane
   :svg
   {:total-width (:total-width my-plan),
    :total-height (:total-height my-plan)})))


(deftest t36_l299 (is ((fn [v] (= :svg v)) v35_l295)))


(def
 v38_l327
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


(deftest t39_l337 (is ((fn [t] (= 3 (count (:row-maps t)))) v38_l327)))


(def
 v41_l348
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


(deftest t42_l358 (is ((fn [t] (= 4 (count (:row-maps t)))) v41_l348)))


(def v44_l365 (-> data/iris (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t45_l369
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v44_l365)))


(def
 v47_l385
 (defmethod
  stat/compute-stat
  :quantile
  [view]
  {:points [], :x-domain [0 1], :y-domain [0 1]}))


(def
 v48_l388
 (defmethod
  stat/compute-stat
  [:quantile :doc]
  [_]
  "Quantile regression bands"))


(def v50_l393 (sk/stat-doc :quantile))


(deftest
 t51_l395
 (is ((fn [v] (= "Quantile regression bands" v)) v50_l393)))


(def v53_l403 (remove-method stat/compute-stat [:quantile :doc]))


(def v54_l405 (sk/stat-doc :quantile))


(deftest t55_l407 (is ((fn [v] (= "(no description)" v)) v54_l405)))


(def v57_l413 (remove-method stat/compute-stat :quantile))


(def
 v58_l415
 (count (filter keyword? (keys (methods stat/compute-stat)))))


(deftest t59_l417 (is ((fn [v] (= 11 v)) v58_l415)))
