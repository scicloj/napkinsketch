(ns
 napkinsketch-book.xkcd7-extensibility-generated-test
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
 v3_l73
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


(deftest t4_l83 (is ((fn [t] (= 11 (count (:row-maps t)))) v3_l73)))


(def v6_l91 (method/lookup :histogram))


(deftest t7_l93 (is ((fn [m] (= :bin (:stat m))) v6_l91)))


(def v9_l97 (method/lookup :bar))


(deftest t10_l99 (is ((fn [m] (= :count (:stat m))) v9_l97)))


(def v12_l103 (method/lookup :point))


(deftest t13_l105 (is ((fn [m] (= :identity (:stat m))) v12_l103)))


(def
 v15_l134
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


(deftest t16_l145 (is ((fn [t] (= 17 (count (:row-maps t)))) v15_l134)))


(def
 v18_l151
 (let
  [s
   (->
    data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    sk/xkcd7-plan)
   layer
   (first (:layers (first (:panels s))))]
  layer))


(deftest
 t19_l157
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v18_l151)))


(def
 v21_l166
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


(deftest t22_l177 (is ((fn [t] (= 17 (count (:row-maps t)))) v21_l166)))


(def
 v24_l244
 (def
  my-plan
  (->
   data/iris
   (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
   sk/xkcd7-plan)))


(def v25_l249 (first (sk/plan->figure my-plan :svg {})))


(deftest t26_l251 (is ((fn [v] (= :svg v)) v25_l249)))


(def v28_l255 (def my-figure (sk/plan->figure my-plan :svg {})))


(def v29_l257 (vector? my-figure))


(deftest t30_l259 (is ((fn [v] (true? v)) v29_l257)))


(def v32_l300 (def my-membrane (sk/plan->membrane my-plan)))


(def v33_l302 (vector? my-membrane))


(deftest t34_l304 (is ((fn [v] (true? v)) v33_l302)))


(def
 v35_l306
 (first
  (sk/membrane->figure
   my-membrane
   :svg
   {:total-width (:total-width my-plan),
    :total-height (:total-height my-plan)})))


(deftest t36_l310 (is ((fn [v] (= :svg v)) v35_l306)))


(def
 v38_l338
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


(deftest t39_l348 (is ((fn [t] (= 3 (count (:row-maps t)))) v38_l338)))


(def
 v41_l359
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


(deftest t42_l369 (is ((fn [t] (= 4 (count (:row-maps t)))) v41_l359)))


(def
 v44_l376
 (-> data/iris (sk/xkcd7-lay-bar :species) (sk/xkcd7-coord :flip)))


(deftest
 t45_l380
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v44_l376)))


(def
 v47_l396
 (defmethod
  stat/compute-stat
  :quantile
  [view]
  {:points [], :x-domain [0 1], :y-domain [0 1]}))


(def
 v48_l399
 (defmethod
  stat/compute-stat
  [:quantile :doc]
  [_]
  "Quantile regression bands"))


(def v50_l404 (sk/stat-doc :quantile))


(deftest
 t51_l406
 (is ((fn [v] (= "Quantile regression bands" v)) v50_l404)))


(def v53_l414 (remove-method stat/compute-stat [:quantile :doc]))


(def v54_l416 (sk/stat-doc :quantile))


(deftest t55_l418 (is ((fn [v] (= "(no description)" v)) v54_l416)))


(def v57_l424 (remove-method stat/compute-stat :quantile))


(def
 v58_l426
 (count (filter keyword? (keys (methods stat/compute-stat)))))


(deftest t59_l428 (is ((fn [v] (= 11 v)) v58_l426)))
