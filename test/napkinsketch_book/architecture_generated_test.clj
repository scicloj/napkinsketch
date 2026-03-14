(ns
 napkinsketch-book.architecture-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.impl.sketch-schema :as ss]
  [clojure.test :refer [deftest is]]))


(def
 v3_l16
 (kind/mermaid
  "\ngraph LR\n  V[\"Views<br/>(API)\"] -->|resolve| S[\"Sketch<br/>(data-space)\"]\n  S -->|scales + coords| M[\"Membrane<br/>(pixel-space)\"]\n  M -->|tree walk| F[\"Figure<br/>(output)\"]\n  style V fill:#e8f5e9\n  style S fill:#fff3e0\n  style M fill:#e3f2fd\n  style F fill:#fce4ec\n"))


(def
 v5_l44
 (def
  trace-data
  (tc/dataset {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :b]})))


(def
 v7_l54
 (def
  trace-views
  (-> trace-data (sk/view [[:x :y]]) (sk/lay (sk/point {:color :g})))))


(def v8_l59 (kind/pprint trace-views))


(deftest
 t9_l61
 (is ((fn [v] (and (vector? v) (= :point (:mark (first v))))) v8_l59)))


(def v11_l70 (def trace-sketch (sk/sketch trace-views)))


(def v12_l72 (kind/pprint trace-sketch))


(deftest
 t13_l74
 (is ((fn [v] (and (map? v) (contains? v :panels))) v12_l72)))


(def v15_l78 (ss/valid? trace-sketch))


(deftest t16_l80 (is (true? v15_l78)))


(def v18_l84 (= trace-sketch (read-string (pr-str trace-sketch))))


(deftest t19_l86 (is (true? v18_l84)))


(def v21_l95 (def trace-membrane (sk/sketch->membrane trace-sketch)))


(def v22_l97 (kind/pprint trace-membrane))


(deftest
 t23_l99
 (is ((fn [v] (and (vector? v) (pos? (count v)))) v22_l97)))


(def
 v25_l106
 (def
  trace-figure
  (sk/membrane->figure
   trace-membrane
   :svg
   {:total-width (:total-width trace-sketch),
    :total-height (:total-height trace-sketch)})))


(def v26_l111 (kind/pprint trace-figure))


(deftest
 t27_l113
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v26_l111)))


(def v29_l117 (kind/hiccup trace-figure))


(deftest
 t30_l119
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v29_l117)))


(def
 v32_l137
 (kind/mermaid
  "\ngraph LR\n  subgraph WHAT [\"WHAT — data + semantics\"]\n    V[\"Views\"]\n    ST[\"Statistics\"]\n    D[\"Domains\"]\n    C[\"Colors\"]\n  end\n  subgraph HOW [\"HOW — pixels + rendering\"]\n    SC[\"Scales (wadogo)\"]\n    CO[\"Coord transforms\"]\n    MS[\"Membrane tree\"]\n    SV[\"SVG conversion\"]\n  end\n  WHAT -->|sketch| HOW\n  style WHAT fill:#e8f5e9\n  style HOW fill:#e3f2fd\n"))


(def
 v34_l181
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v35_l184
 (def
  multi-views
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(def
 v36_l188
 (def
  multi-sketch
  (sk/sketch multi-views {:title "Iris Petals with Regression"})))


(def
 v38_l192
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels multi-sketch)))))


(deftest
 t39_l197
 (is
  ((fn
    [v]
    (and
     (= :point (:mark (first v)))
     (= :line (:mark (second v)))
     (= 3 (:n-groups (first v)))))
   v38_l192)))


(def v41_l203 (select-keys multi-sketch [:title :legend]))


(deftest
 t42_l205
 (is
  ((fn
    [m]
    (and
     (= "Iris Petals with Regression" (:title m))
     (= 3 (count (get-in m [:legend :entries])))))
   v41_l203)))


(def
 v44_l210
 (sk/plot multi-views {:title "Iris Petals with Regression"}))


(deftest
 t45_l212
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v44_l210)))


(def
 v47_l218
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> VIEW[\"impl/view.clj\"]\n  API --> PLOT[\"impl/plot.clj\"]\n  API --> SKETCH[\"impl/sketch.clj\"]\n  SKETCH --> VIEW\n  SKETCH --> STAT[\"impl/stat.clj\"]\n  SKETCH --> SCALE[\"impl/scale.clj\"]\n  SKETCH --> DEFAULTS[\"impl/defaults.clj\"]\n  PLOT --> SKETCH\n  PLOT --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  style API fill:#c8e6c9\n  style SKETCH fill:#ffe0b2\n  style PLOT fill:#bbdefb\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n"))


(def
 v49_l251
 (kind/mermaid
  "\ngraph TD\n  VIEWS[\"views + opts\"]\n  VIEWS --> INFER[\"infer-layout\"]\n  VIEWS --> COLORS[\"collect-colors<br/>(resolve-view × N)\"]\n  VIEWS --> ANNOTS[\"annotations\"]\n\n  INFER --> GRID[\"compute-grid\"]\n  INFER --> DIMS[\"compute-panel-dims\"]\n  INFER --> GROUP[\"group-panels\"]\n\n  COLORS --> GROUP\n  GROUP --> RPV[\"resolve-panel-views<br/>(compute-stat + extract-layer)\"]\n  COLORS --> RPV\n\n  RPV --> BUILD[\"build-panels<br/>(domains, ticks)\"]\n  GRID --> BUILD\n  DIMS --> BUILD\n\n  BUILD --> LABELS[\"resolve-labels\"]\n  COLORS --> LEGEND[\"build-legend\"]\n  LABELS --> LAYOUT[\"compute-layout-dims\"]\n  LEGEND --> LAYOUT\n  DIMS --> LAYOUT\n\n  BUILD --> SKETCH[\"sketch\"]\n  LABELS --> SKETCH\n  LEGEND --> SKETCH\n  LAYOUT --> SKETCH\n\n  style VIEWS fill:#e8f5e9\n  style SKETCH fill:#fff3e0\n  style RPV fill:#e3f2fd\n  style BUILD fill:#e3f2fd\n"))
