(ns
 napkinsketch-book.architecture-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.impl.sketch :as sketch-impl]
  [scicloj.napkinsketch.impl.plan :as plan-impl]
  [scicloj.napkinsketch.impl.sketch-schema :as ss]
  [clojure.test :refer [deftest is]]))


(def
 v3_l27
 (kind/mermaid
  "\ngraph LR\n  B[\"sketch<br/>(composable API)\"] -->|sketch->draft| D[\"Draft<br/>(flat maps)\"]\n  D -->|draft->plan| P[\"Plan<br/>(data-space)\"]\n  P -->|scales + coords| M[\"Membrane<br/>(pixel-space)\"]\n  M -->|tree walk| F[\"Figure<br/>(output)\"]\n  style B fill:#d1c4e9\n  style D fill:#e8f5e9\n  style P fill:#fff3e0\n  style M fill:#e3f2fd\n  style F fill:#fce4ec\n"))


(def
 v5_l68
 (def trace-data {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :b]}))


(def
 v7_l79
 (def trace-sk (-> trace-data (sk/lay-point :x :y {:color :g}))))


(def v9_l95 (sketch-impl/sketch? trace-sk))


(deftest t10_l97 (is (true? v9_l95)))


(def v12_l101 (count (:views trace-sk)))


(deftest t13_l103 (is ((fn [n] (= 1 n)) v12_l101)))


(def v14_l105 (:views trace-sk))


(deftest
 t15_l107
 (is
  ((fn
    [views]
    (let
     [v (first views)]
     (and
      (= :x (get-in v [:mapping :x]))
      (= :y (get-in v [:mapping :y]))
      (= 1 (count (:layers v))))))
   v14_l105)))


(def v17_l116 (get-in (:views trace-sk) [0 :layers 0 :method]))


(deftest t18_l118 (is ((fn [m] (= :point m)) v17_l116)))


(def v20_l126 (def trace-draft (sk/draft trace-sk)))


(def v21_l129 (count trace-draft))


(deftest t22_l131 (is ((fn [n] (= 1 n)) v21_l129)))


(def v23_l133 (select-keys (first trace-draft) [:x :y :mark :color]))


(deftest
 t24_l135
 (is
  ((fn
    [m]
    (and
     (= :x (:x m))
     (= :y (:y m))
     (= :point (:mark m))
     (= :g (:color m))))
   v23_l133)))


(def v26_l146 (def trace-plan (plan-impl/draft->plan trace-draft {})))


(def v27_l149 trace-plan)


(deftest
 t28_l151
 (is ((fn [v] (and (map? v) (contains? v :panels))) v27_l149)))


(def v30_l155 (ss/valid? trace-plan))


(deftest t31_l157 (is (true? v30_l155)))


(def v33_l164 (def trace-membrane (sk/plan->membrane trace-plan)))


(def v34_l166 trace-membrane)


(deftest
 t35_l168
 (is ((fn [v] (and (vector? v) (pos? (count v)))) v34_l166)))


(def
 v37_l174
 (def
  trace-figure
  (sk/membrane->figure
   trace-membrane
   :svg
   {:total-width (:total-width trace-plan),
    :total-height (:total-height trace-plan)})))


(def v38_l179 (kind/pprint trace-figure))


(deftest
 t39_l181
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v38_l179)))


(def v41_l185 (kind/hiccup trace-figure))


(deftest
 t42_l187
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v41_l185)))


(def v44_l197 (def shortcut-plan (sk/plan trace-sk)))


(def v45_l199 (ss/valid? shortcut-plan))


(deftest t46_l201 (is (true? v45_l199)))


(def
 v48_l219
 (kind/mermaid
  "\ngraph LR\n  subgraph WHAT [\"WHAT -- data + semantics\"]\n    B[\"sketch\"]\n    D[\"Draft\"]\n    ST[\"Statistics\"]\n    D[\"Domains\"]\n    C[\"Colors\"]\n  end\n  subgraph HOW [\"HOW -- pixels + rendering\"]\n    SC[\"Scales (wadogo)\"]\n    CO[\"Coord transforms\"]\n    MS[\"Membrane tree\"]\n    SV[\"SVG conversion\"]\n  end\n  WHAT -->|plan| HOW\n  style WHAT fill:#e8f5e9\n  style HOW fill:#e3f2fd\n"))


(def
 v50_l264
 (def
  multi-sk
  (->
   (rdatasets/datasets-iris)
   (sk/view :petal-length :petal-width {:color :species})
   sk/lay-point
   sk/lay-lm)))


(def v52_l272 (count (:views multi-sk)))


(deftest t53_l274 (is ((fn [n] (= 1 n)) v52_l272)))


(def v54_l276 (mapv :method (:layers multi-sk)))


(deftest
 t55_l278
 (is ((fn [v] (and (= :point (first v)) (= :lm (second v)))) v54_l276)))


(def v57_l284 (def multi-draft (sk/draft multi-sk)))


(def v58_l286 (count multi-draft))


(deftest t59_l288 (is ((fn [n] (= 2 n)) v58_l286)))


(def v60_l290 (mapv :mark multi-draft))


(deftest
 t61_l292
 (is
  ((fn [v] (and (= :point (first v)) (= :line (second v)))) v60_l290)))


(def
 v63_l297
 (def
  multi-plan
  (sk/plan multi-sk {:title "Iris Petals with Regression"})))


(def
 v64_l300
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels multi-plan)))))


(deftest
 t65_l305
 (is
  ((fn
    [v]
    (and
     (= :point (:mark (first v)))
     (= :line (:mark (second v)))
     (= 3 (:n-groups (first v)))))
   v64_l300)))


(def v67_l311 multi-plan)


(deftest
 t68_l313
 (is
  ((fn
    [m]
    (and
     (= "Iris Petals with Regression" (:title m))
     (= 3 (count (get-in m [:legend :entries])))))
   v67_l311)))


(def
 v70_l318
 (->
  (rdatasets/datasets-iris)
  (sk/view :petal-length :petal-width {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Iris Petals with Regression"})))


(deftest
 t71_l324
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v70_l318)))


(def
 v73_l330
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> SK[\"impl/sketch.clj\"]\n  API --> RES[\"impl/resolve.clj\"]\n  API --> PL[\"impl/plan.clj\"]\n  SK --> RES\n  SK --> PL\n  PL --> RES\n  PL --> STAT[\"impl/stat.clj\"]\n  PL --> SCALE[\"impl/scale.clj\"]\n  PL --> DEFAULTS[\"impl/defaults.clj\"]\n  PL --> SS[\"impl/sketch_schema.clj\"]\n  SK --> RENDER[\"impl/render.clj\"]\n  RENDER --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  style API fill:#c8e6c9\n  style SK fill:#d1c4e9\n  style PL fill:#d1c4e9\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n"))
