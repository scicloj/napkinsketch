(ns
 napkinsketch-book.architecture-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.impl.sketch :as sketch-impl]
  [scicloj.napkinsketch.impl.sketch-schema :as ss]
  [clojure.test :refer [deftest is]]))


(def
 v3_l26
 (kind/mermaid
  "\ngraph LR\n  B[\"sketch<br/>(composable API)\"] -->|resolve| V[\"Views<br/>(flat maps)\"]\n  V -->|views->plan| P[\"Plan<br/>(data-space)\"]\n  P -->|scales + coords| M[\"Membrane<br/>(pixel-space)\"]\n  M -->|tree walk| F[\"Figure<br/>(output)\"]\n  style B fill:#d1c4e9\n  style V fill:#e8f5e9\n  style P fill:#fff3e0\n  style M fill:#e3f2fd\n  style F fill:#fce4ec\n"))


(def
 v5_l66
 (def trace-data {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :b]}))


(def
 v7_l77
 (def trace-sk (-> trace-data (sk/lay-point :x :y {:color :g}))))


(def v9_l93 (sketch-impl/sketch? trace-sk))


(deftest t10_l95 (is (true? v9_l93)))


(def v12_l99 (count (:entries trace-sk)))


(deftest t13_l101 (is ((fn [n] (= 1 n)) v12_l99)))


(def v14_l103 (:entries trace-sk))


(deftest
 t15_l105
 (is
  ((fn
    [entries]
    (let
     [e (first entries)]
     (and (= :x (:x e)) (= :y (:y e)) (= 1 (count (:methods e))))))
   v14_l103)))


(def v17_l114 (get-in (:entries trace-sk) [0 :methods 0 :mark]))


(deftest t18_l116 (is ((fn [m] (= :point m)) v17_l114)))


(def v20_l124 (def trace-views (sketch-impl/resolve-sketch trace-sk)))


(def v21_l127 (count trace-views))


(deftest t22_l129 (is ((fn [n] (= 1 n)) v21_l127)))


(def v23_l131 (select-keys (first trace-views) [:x :y :mark :color]))


(deftest
 t24_l133
 (is
  ((fn
    [m]
    (and
     (= :x (:x m))
     (= :y (:y m))
     (= :point (:mark m))
     (= :g (:color m))))
   v23_l131)))


(def v26_l144 (def trace-plan (sketch-impl/views->plan trace-views {})))


(def v27_l147 trace-plan)


(deftest
 t28_l149
 (is ((fn [v] (and (map? v) (contains? v :panels))) v27_l147)))


(def v30_l153 (ss/valid? trace-plan))


(deftest t31_l155 (is (true? v30_l153)))


(def v33_l162 (def trace-membrane (sk/plan->membrane trace-plan)))


(def v34_l164 trace-membrane)


(deftest
 t35_l166
 (is ((fn [v] (and (vector? v) (pos? (count v)))) v34_l164)))


(def
 v37_l172
 (def
  trace-figure
  (sk/membrane->figure
   trace-membrane
   :svg
   {:total-width (:total-width trace-plan),
    :total-height (:total-height trace-plan)})))


(def v38_l177 (kind/pprint trace-figure))


(deftest
 t39_l179
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v38_l177)))


(def v41_l183 (kind/hiccup trace-figure))


(deftest
 t42_l185
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v41_l183)))


(def v44_l195 (def shortcut-plan (sk/plan trace-sk)))


(def v45_l197 (ss/valid? shortcut-plan))


(deftest t46_l199 (is (true? v45_l197)))


(def
 v48_l217
 (kind/mermaid
  "\ngraph LR\n  subgraph WHAT [\"WHAT — data + semantics\"]\n    B[\"sketch\"]\n    V[\"Views\"]\n    ST[\"Statistics\"]\n    D[\"Domains\"]\n    C[\"Colors\"]\n  end\n  subgraph HOW [\"HOW — pixels + rendering\"]\n    SC[\"Scales (wadogo)\"]\n    CO[\"Coord transforms\"]\n    MS[\"Membrane tree\"]\n    SV[\"SVG conversion\"]\n  end\n  WHAT -->|plan| HOW\n  style WHAT fill:#e8f5e9\n  style HOW fill:#e3f2fd\n"))


(def
 v50_l262
 (def
  multi-sk
  (->
   data/iris
   (sk/view :petal_length :petal_width {:color :species})
   sk/lay-point
   sk/lay-lm)))


(def v52_l270 (count (:entries multi-sk)))


(deftest t53_l272 (is ((fn [n] (= 1 n)) v52_l270)))


(def v54_l274 (mapv :mark (:methods multi-sk)))


(deftest
 t55_l276
 (is
  ((fn [v] (and (= :point (first v)) (= :line (second v)))) v54_l274)))


(def v57_l282 (def multi-views (sketch-impl/resolve-sketch multi-sk)))


(def v58_l284 (count multi-views))


(deftest t59_l286 (is ((fn [n] (= 2 n)) v58_l284)))


(def v60_l288 (mapv :mark multi-views))


(deftest
 t61_l290
 (is
  ((fn [v] (and (= :point (first v)) (= :line (second v)))) v60_l288)))


(def
 v63_l295
 (def
  multi-plan
  (sk/plan multi-sk {:title "Iris Petals with Regression"})))


(def
 v64_l298
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels multi-plan)))))


(deftest
 t65_l303
 (is
  ((fn
    [v]
    (and
     (= :point (:mark (first v)))
     (= :line (:mark (second v)))
     (= 3 (:n-groups (first v)))))
   v64_l298)))


(def v67_l309 multi-plan)


(deftest
 t68_l311
 (is
  ((fn
    [m]
    (and
     (= "Iris Petals with Regression" (:title m))
     (= 3 (count (get-in m [:legend :entries])))))
   v67_l309)))


(def
 v70_l316
 (->
  data/iris
  (sk/view :petal_length :petal_width {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Iris Petals with Regression"})))


(deftest
 t71_l322
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v70_l316)))


(def
 v73_l328
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> SK[\"impl/sketch.clj\"]\n  API --> VIEW[\"impl/view.clj\"]\n  SK --> VIEW\n  SK --> RENDER[\"impl/render.clj\"]\n  SK --> STAT[\"impl/stat.clj\"]\n  SK --> SCALE[\"impl/scale.clj\"]\n  SK --> DEFAULTS[\"impl/defaults.clj\"]\n  SK --> SS[\"impl/sketch_schema.clj\"]\n  RENDER --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  style API fill:#c8e6c9\n  style SK fill:#d1c4e9\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n"))
