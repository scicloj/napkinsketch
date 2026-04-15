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
 v5_l67
 (def trace-data {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :b]}))


(def
 v7_l78
 (def trace-sk (-> trace-data (sk/lay-point :x :y {:color :g}))))


(def v9_l94 (sketch-impl/sketch? trace-sk))


(deftest t10_l96 (is (true? v9_l94)))


(def v12_l100 (count (:views trace-sk)))


(deftest t13_l102 (is ((fn [n] (= 1 n)) v12_l100)))


(def v14_l104 (:views trace-sk))


(deftest
 t15_l106
 (is
  ((fn
    [views]
    (let
     [v (first views)]
     (and
      (= :x (get-in v [:mapping :x]))
      (= :y (get-in v [:mapping :y]))
      (= 1 (count (:layers v))))))
   v14_l104)))


(def v17_l115 (get-in (:views trace-sk) [0 :layers 0 :method]))


(deftest t18_l117 (is ((fn [m] (= :point m)) v17_l115)))


(def v20_l125 (def trace-draft (sk/draft trace-sk)))


(def v21_l128 (count trace-draft))


(deftest t22_l130 (is ((fn [n] (= 1 n)) v21_l128)))


(def v23_l132 (select-keys (first trace-draft) [:x :y :mark :color]))


(deftest
 t24_l134
 (is
  ((fn
    [m]
    (and
     (= :x (:x m))
     (= :y (:y m))
     (= :point (:mark m))
     (= :g (:color m))))
   v23_l132)))


(def v26_l145 (def trace-plan (plan-impl/draft->plan trace-draft {})))


(def v27_l148 trace-plan)


(deftest
 t28_l150
 (is ((fn [v] (and (map? v) (contains? v :panels))) v27_l148)))


(def v30_l154 (ss/valid? trace-plan))


(deftest t31_l156 (is (true? v30_l154)))


(def v33_l163 (def trace-membrane (sk/plan->membrane trace-plan)))


(def v34_l165 trace-membrane)


(deftest
 t35_l167
 (is ((fn [v] (and (vector? v) (pos? (count v)))) v34_l165)))


(def
 v37_l173
 (def
  trace-figure
  (sk/membrane->figure
   trace-membrane
   :svg
   {:total-width (:total-width trace-plan),
    :total-height (:total-height trace-plan)})))


(def v38_l178 (kind/pprint trace-figure))


(deftest
 t39_l180
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v38_l178)))


(def v41_l184 (kind/hiccup trace-figure))


(deftest
 t42_l186
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v41_l184)))


(def v44_l196 (def shortcut-plan (sk/plan trace-sk)))


(def v45_l198 (ss/valid? shortcut-plan))


(deftest t46_l200 (is (true? v45_l198)))


(def
 v48_l218
 (kind/mermaid
  "\ngraph LR\n  subgraph WHAT [\"WHAT -- data + semantics\"]\n    B[\"sketch\"]\n    D[\"Draft\"]\n    ST[\"Statistics\"]\n    D[\"Domains\"]\n    C[\"Colors\"]\n  end\n  subgraph HOW [\"HOW -- pixels + rendering\"]\n    SC[\"Scales (wadogo)\"]\n    CO[\"Coord transforms\"]\n    MS[\"Membrane tree\"]\n    SV[\"SVG conversion\"]\n  end\n  WHAT -->|plan| HOW\n  style WHAT fill:#e8f5e9\n  style HOW fill:#e3f2fd\n"))


(def
 v50_l263
 (def
  multi-sk
  (->
   (rdatasets/datasets-iris)
   (sk/view :petal-length :petal-width {:color :species})
   sk/lay-point
   sk/lay-lm)))


(def v52_l271 (count (:views multi-sk)))


(deftest t53_l273 (is ((fn [n] (= 1 n)) v52_l271)))


(def v54_l275 (mapv :method (:layers multi-sk)))


(deftest
 t55_l277
 (is ((fn [v] (and (= :point (first v)) (= :lm (second v)))) v54_l275)))


(def v57_l283 (def multi-draft (sk/draft multi-sk)))


(def v58_l285 (count multi-draft))


(deftest t59_l287 (is ((fn [n] (= 2 n)) v58_l285)))


(def v60_l289 (mapv :mark multi-draft))


(deftest
 t61_l291
 (is
  ((fn [v] (and (= :point (first v)) (= :line (second v)))) v60_l289)))


(def
 v63_l296
 (def
  multi-plan
  (sk/plan multi-sk {:title "Iris Petals with Regression"})))


(def
 v64_l299
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels multi-plan)))))


(deftest
 t65_l304
 (is
  ((fn
    [v]
    (and
     (= :point (:mark (first v)))
     (= :line (:mark (second v)))
     (= 3 (:n-groups (first v)))))
   v64_l299)))


(def v67_l310 multi-plan)


(deftest
 t68_l312
 (is
  ((fn
    [m]
    (and
     (= "Iris Petals with Regression" (:title m))
     (= 3 (count (get-in m [:legend :entries])))))
   v67_l310)))


(def
 v70_l317
 (->
  (rdatasets/datasets-iris)
  (sk/view :petal-length :petal-width {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Iris Petals with Regression"})))


(deftest
 t71_l323
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v70_l317)))


(def
 v73_l329
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> SK[\"impl/sketch.clj\"]\n  API --> RES[\"impl/resolve.clj\"]\n  API --> PL[\"impl/plan.clj\"]\n  SK --> RES\n  SK --> PL\n  PL --> RES\n  PL --> STAT[\"impl/stat.clj\"]\n  PL --> SCALE[\"impl/scale.clj\"]\n  PL --> DEFAULTS[\"impl/defaults.clj\"]\n  PL --> SS[\"impl/sketch_schema.clj\"]\n  SK --> RENDER[\"impl/render.clj\"]\n  RENDER --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  style API fill:#c8e6c9\n  style SK fill:#d1c4e9\n  style PL fill:#d1c4e9\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n"))
