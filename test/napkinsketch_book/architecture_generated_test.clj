(ns
 napkinsketch-book.architecture-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.impl.sketch-schema :as ss]
  [clojure.test :refer [deftest is]]))


(def
 v3_l15
 (kind/mermaid
  "\ngraph LR\n  V[\"Views<br/>(API)\"] -->|resolve| S[\"Sketch<br/>(data-space)\"]\n  S -->|scales + coords| M[\"Membrane Tree<br/>(pixel-space)\"]\n  M -->|tree walk| SVG[\"SVG Hiccup<br/>(output)\"]\n  style V fill:#e8f5e9\n  style S fill:#fff3e0\n  style M fill:#e3f2fd\n  style SVG fill:#fce4ec\n"))


(def
 v5_l43
 (kind/mermaid
  "\ngraph TB\n  subgraph WHAT [\"What to draw\"]\n    direction TB\n    A1[\"api.clj\"]\n    A2[\"impl/view.clj\"]\n    A3[\"impl/stat.clj\"]\n    A4[\"impl/sketch.clj\"]\n  end\n  subgraph HOW [\"How to draw it\"]\n    direction TB\n    B1[\"impl/scale.clj\"]\n    B2[\"render/mark.clj\"]\n    B3[\"render/panel.clj\"]\n    B4[\"impl/plot.clj\"]\n    B5[\"render/membrane.clj\"]\n    B6[\"render/svg.clj\"]\n  end\n  WHAT -->|sketch| HOW\n  style WHAT fill:#e8f5e9\n  style HOW fill:#e3f2fd\n"))


(def
 v7_l103
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> VIEW[\"impl/view.clj\"]\n  API --> PLOT[\"impl/plot.clj\"]\n  API --> SKETCH[\"impl/sketch.clj\"]\n  SKETCH --> VIEW\n  SKETCH --> STAT[\"impl/stat.clj\"]\n  SKETCH --> SCALE[\"impl/scale.clj\"]\n  SKETCH --> DEFAULTS[\"impl/defaults.clj\"]\n  PLOT --> SKETCH\n  PLOT --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  style API fill:#c8e6c9\n  style SKETCH fill:#ffe0b2\n  style PLOT fill:#bbdefb\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n"))


(def
 v9_l132
 (kind/mermaid
  "\ngraph TD\n  VIEWS[\"views + opts\"]\n  VIEWS --> INFER[\"infer-layout\"]\n  VIEWS --> COLORS[\"collect-colors<br/>(resolve-view × N)\"]\n  VIEWS --> ANNOTS[\"annotations\"]\n\n  INFER --> GRID[\"compute-grid\"]\n  INFER --> DIMS[\"compute-panel-dims\"]\n  INFER --> GROUP[\"group-panels\"]\n\n  COLORS --> GROUP\n  GROUP --> RPV[\"resolve-panel-views<br/>(compute-stat + extract-layer)\"]\n  COLORS --> RPV\n\n  RPV --> BUILD[\"build-panels<br/>(domains, ticks)\"]\n  GRID --> BUILD\n  DIMS --> BUILD\n\n  BUILD --> LABELS[\"resolve-labels\"]\n  COLORS --> LEGEND[\"build-legend\"]\n  LABELS --> LAYOUT[\"compute-layout-dims\"]\n  LEGEND --> LAYOUT\n  DIMS --> LAYOUT\n\n  BUILD --> SKETCH[\"sketch\"]\n  LABELS --> SKETCH\n  LEGEND --> SKETCH\n  LAYOUT --> SKETCH\n\n  style VIEWS fill:#e8f5e9\n  style SKETCH fill:#fff3e0\n  style RPV fill:#e3f2fd\n  style BUILD fill:#e3f2fd\n"))


(def
 v11_l173
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v13_l180
 (def
  views
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(def v15_l185 (dissoc (first views) :data))


(deftest t16_l187 (is ((fn [v] (= :point (:mark v))) v15_l185)))


(def v18_l194 (def sk (sk/sketch views)))


(def
 v20_l198
 (let
  [panel
   (first (:panels sk))
   layer
   (first (:layers panel))
   group
   (first (:groups layer))]
  {:mark (:mark layer),
   :x-domain (:x-domain panel),
   :y-domain (:y-domain panel),
   :n-groups (count (:groups layer)),
   :first-group-n-points (count (:xs group)),
   :first-group-color (:color group)}))


(deftest
 t21_l208
 (is
  ((fn
    [m]
    (and
     (= :point (:mark m))
     (= 3 (:n-groups m))
     (pos? (:first-group-n-points m))))
   v20_l198)))


(def v23_l214 (ss/valid? sk))


(deftest t24_l216 (is (true? v23_l214)))


(def v26_l220 (= sk (read-string (pr-str sk))))


(deftest t27_l222 (is (true? v26_l220)))


(def v29_l230 (sk/plot views))


(deftest
 t30_l232
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v29_l230)))


(def
 v32_l242
 (kind/mermaid
  "\ngraph LR\n  subgraph WHAT [\"WHAT — data + semantics\"]\n    V[\"Views\"]\n    ST[\"Statistics\"]\n    D[\"Domains\"]\n    C[\"Colors\"]\n  end\n  subgraph HOW [\"HOW — pixels + rendering\"]\n    SC[\"Scales (wadogo)\"]\n    CO[\"Coord transforms\"]\n    MS[\"Membrane tree\"]\n    SV[\"SVG conversion\"]\n  end\n  WHAT -->|sketch| HOW\n  style WHAT fill:#e8f5e9\n  style HOW fill:#e3f2fd\n"))


(def
 v34_l278
 (def
  multi-views
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(def
 v35_l282
 (def
  multi-sk
  (sk/sketch multi-views {:title "Iris Petals with Regression"})))


(def v37_l286 (count (:layers (first (:panels multi-sk)))))


(deftest t38_l288 (is ((fn [n] (= 2 n)) v37_l286)))


(def
 v40_l292
 (let
  [layer (first (:layers (first (:panels multi-sk))))]
  {:mark (:mark layer), :n-groups (count (:groups layer))}))


(deftest
 t41_l296
 (is
  ((fn [m] (and (= :point (:mark m)) (= 3 (:n-groups m)))) v40_l292)))


(def
 v43_l300
 (let
  [layer (second (:layers (first (:panels multi-sk))))]
  {:mark (:mark layer),
   :n-groups (count (:groups layer)),
   :first-group-keys (set (keys (first (:groups layer))))}))


(deftest
 t44_l305
 (is
  ((fn
    [m]
    (and
     (= :line (:mark m))
     (= 3 (:n-groups m))
     (contains? (:first-group-keys m) :x1)))
   v43_l300)))


(def v46_l311 (:title multi-sk))


(deftest
 t47_l313
 (is ((fn [t] (= "Iris Petals with Regression" t)) v46_l311)))


(def v48_l315 (count (:entries (:legend multi-sk))))


(deftest t49_l317 (is ((fn [n] (= 3 n)) v48_l315)))


(def
 v51_l321
 (sk/plot multi-views {:title "Iris Petals with Regression"}))


(deftest
 t52_l323
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v51_l321)))
