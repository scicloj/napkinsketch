(ns
 napkinsketch-book.architecture-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.impl.xkcd7-sketch :as xkcd7-sketch]
  [scicloj.napkinsketch.impl.theold-sketch :as sketch-impl]
  [scicloj.napkinsketch.impl.render :as render-impl]
  [scicloj.napkinsketch.impl.sketch-schema :as ss]
  [clojure.test :refer [deftest is]]))


(def
 v3_l30
 (kind/mermaid
  "\ngraph LR\n  B[\"xkcd7-sketch<br/>(composable API)\"] -->|resolve| V[\"Views<br/>(flat maps)\"]\n  V -->|xkcd7-views->plan| P[\"Plan<br/>(data-space)\"]\n  P -->|scales + coords| M[\"Membrane<br/>(pixel-space)\"]\n  M -->|tree walk| F[\"Figure<br/>(output)\"]\n  style B fill:#d1c4e9\n  style V fill:#e8f5e9\n  style P fill:#fff3e0\n  style M fill:#e3f2fd\n  style F fill:#fce4ec\n"))


(def
 v5_l70
 (def trace-data {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :b]}))


(def
 v7_l81
 (def
  trace-xkcd7-sk
  (-> trace-data (sk/xkcd7-lay-point :x :y {:color :g}))))


(def v9_l97 (xkcd7-sketch/xkcd7-sketch? trace-xkcd7-sk))


(deftest t10_l99 (is (true? v9_l97)))


(def v12_l103 (count (:entries trace-xkcd7-sk)))


(deftest t13_l105 (is ((fn [n] (= 1 n)) v12_l103)))


(def v14_l107 (:entries trace-xkcd7-sk))


(deftest
 t15_l109
 (is
  ((fn
    [entries]
    (let
     [e (first entries)]
     (and (= :x (:x e)) (= :y (:y e)) (= 1 (count (:methods e))))))
   v14_l107)))


(def v17_l118 (get-in (:entries trace-xkcd7-sk) [0 :methods 0 :mark]))


(deftest t18_l120 (is ((fn [m] (= :point m)) v17_l118)))


(def
 v20_l128
 (def trace-views (xkcd7-sketch/xkcd7-resolve-sketch trace-xkcd7-sk)))


(def v21_l131 (count trace-views))


(deftest t22_l133 (is ((fn [n] (= 1 n)) v21_l131)))


(def v23_l135 (select-keys (first trace-views) [:x :y :mark :color]))


(deftest
 t24_l137
 (is
  ((fn
    [m]
    (and
     (= :x (:x m))
     (= :y (:y m))
     (= :point (:mark m))
     (= :g (:color m))))
   v23_l135)))


(def
 v26_l148
 (def trace-plan (sketch-impl/xkcd7-views->plan trace-views {})))


(def v27_l151 trace-plan)


(deftest
 t28_l153
 (is ((fn [v] (and (map? v) (contains? v :panels))) v27_l151)))


(def v30_l157 (ss/valid? trace-plan))


(deftest t31_l159 (is (true? v30_l157)))


(def v33_l166 (def trace-membrane (sk/plan->membrane trace-plan)))


(def v34_l168 trace-membrane)


(deftest
 t35_l170
 (is ((fn [v] (and (vector? v) (pos? (count v)))) v34_l168)))


(def
 v37_l176
 (def
  trace-figure
  (sk/membrane->figure
   trace-membrane
   :svg
   {:total-width (:total-width trace-plan),
    :total-height (:total-height trace-plan)})))


(def v38_l181 (kind/pprint trace-figure))


(deftest
 t39_l183
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v38_l181)))


(def v41_l187 (kind/hiccup trace-figure))


(deftest
 t42_l189
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v41_l187)))


(def v44_l199 (def shortcut-plan (sk/xkcd7-plan trace-xkcd7-sk)))


(def v45_l201 (ss/valid? shortcut-plan))


(deftest t46_l203 (is (true? v45_l201)))


(def
 v48_l221
 (kind/mermaid
  "\ngraph LR\n  subgraph WHAT [\"WHAT — data + semantics\"]\n    B[\"xkcd7-sketch\"]\n    V[\"Views\"]\n    ST[\"Statistics\"]\n    D[\"Domains\"]\n    C[\"Colors\"]\n  end\n  subgraph HOW [\"HOW — pixels + rendering\"]\n    SC[\"Scales (wadogo)\"]\n    CO[\"Coord transforms\"]\n    MS[\"Membrane tree\"]\n    SV[\"SVG conversion\"]\n  end\n  WHAT -->|plan| HOW\n  style WHAT fill:#e8f5e9\n  style HOW fill:#e3f2fd\n"))


(def
 v50_l266
 (def
  multi-xkcd7-sk
  (->
   data/iris
   (sk/xkcd7-view :petal_length :petal_width {:color :species})
   sk/xkcd7-lay-point
   sk/xkcd7-lay-lm)))


(def v52_l274 (count (:entries multi-xkcd7-sk)))


(deftest t53_l276 (is ((fn [n] (= 1 n)) v52_l274)))


(def v54_l278 (mapv :mark (:methods multi-xkcd7-sk)))


(deftest
 t55_l280
 (is
  ((fn [v] (and (= :point (first v)) (= :line (second v)))) v54_l278)))


(def
 v57_l286
 (def multi-views (xkcd7-sketch/xkcd7-resolve-sketch multi-xkcd7-sk)))


(def v58_l288 (count multi-views))


(deftest t59_l290 (is ((fn [n] (= 2 n)) v58_l288)))


(def v60_l292 (mapv :mark multi-views))


(deftest
 t61_l294
 (is
  ((fn [v] (and (= :point (first v)) (= :line (second v)))) v60_l292)))


(def
 v63_l299
 (def
  multi-plan
  (sk/xkcd7-plan
   multi-xkcd7-sk
   {:title "Iris Petals with Regression"})))


(def
 v64_l302
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels multi-plan)))))


(deftest
 t65_l307
 (is
  ((fn
    [v]
    (and
     (= :point (:mark (first v)))
     (= :line (:mark (second v)))
     (= 3 (:n-groups (first v)))))
   v64_l302)))


(def v67_l313 multi-plan)


(deftest
 t68_l315
 (is
  ((fn
    [m]
    (and
     (= "Iris Petals with Regression" (:title m))
     (= 3 (count (get-in m [:legend :entries])))))
   v67_l313)))


(def
 v70_l320
 (->
  data/iris
  (sk/xkcd7-view :petal_length :petal_width {:color :species})
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm
  (sk/xkcd7-options {:title "Iris Petals with Regression"})))


(deftest
 t71_l326
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v70_l320)))


(def
 v73_l332
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> XKCD7SK[\"impl/xkcd7_sketch.clj\"]\n  API --> VIEW[\"impl/view.clj\"]\n  API --> PLOT[\"impl/plot.clj\"]\n  API --> PLAN[\"impl/sketch.clj\"]\n  XKCD7SK --> VIEW\n  XKCD7SK --> PLAN\n  XKCD7SK --> RENDER[\"impl/render.clj\"]\n  PLAN --> VIEW\n  PLAN --> STAT[\"impl/stat.clj\"]\n  PLAN --> SCALE[\"impl/scale.clj\"]\n  PLAN --> DEFAULTS[\"impl/defaults.clj\"]\n  PLOT --> PLAN\n  PLOT --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  style API fill:#c8e6c9\n  style XKCD7SK fill:#d1c4e9\n  style PLAN fill:#ffe0b2\n  style PLOT fill:#bbdefb\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n"))
