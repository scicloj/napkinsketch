(ns
 napkinsketch-book.architecture-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as ns]
  [scicloj.napkinsketch.impl.sketch-schema :as ss]
  [clojure.test :refer [deftest is]]))


(def
 v3_l15
 (kind/mermaid
  "\ngraph LR\n  V[\"Views<br/>(API)\"] -->|resolve| S[\"Sketch<br/>(data-space)\"]\n  S -->|scales + coords| M[\"Membrane Scene<br/>(pixel-space)\"]\n  M -->|tree walk| SVG[\"SVG Hiccup<br/>(output)\"]\n  style V fill:#e8f5e9\n  style S fill:#fff3e0\n  style M fill:#e3f2fd\n  style SVG fill:#fce4ec\n"))


(def
 v5_l42
 (kind/mermaid
  "\ngraph TB\n  subgraph WHAT [\"What to draw\"]\n    direction TB\n    A1[\"api.clj\"]\n    A2[\"impl/view.clj\"]\n    A3[\"impl/stat.clj\"]\n    A4[\"impl/sketch.clj\"]\n  end\n  subgraph HOW [\"How to draw it\"]\n    direction TB\n    B1[\"impl/scale.clj\"]\n    B2[\"impl/mark.clj\"]\n    B3[\"impl/panel.clj\"]\n    B4[\"impl/plot.clj\"]\n    B5[\"render/svg.clj\"]\n  end\n  WHAT -->|sketch| HOW\n  style WHAT fill:#e8f5e9\n  style HOW fill:#e3f2fd\n"))


(def
 v7_l100
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> VIEW[\"impl/view.clj\"]\n  API --> PLOT[\"impl/plot.clj\"]\n  API --> SKETCH[\"impl/sketch.clj\"]\n  SKETCH --> VIEW\n  SKETCH --> STAT[\"impl/stat.clj\"]\n  SKETCH --> SCALE[\"impl/scale.clj\"]\n  SKETCH --> DEFAULTS[\"impl/defaults.clj\"]\n  PLOT --> SKETCH\n  PLOT --> PANEL[\"impl/panel.clj\"]\n  PLOT --> SVG[\"render/svg.clj\"]\n  PANEL --> MARK[\"impl/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  style API fill:#c8e6c9\n  style SKETCH fill:#ffe0b2\n  style PLOT fill:#bbdefb\n  style SVG fill:#f8bbd0\n"))


(def
 v9_l125
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v11_l132
 (def
  views
  [(ns/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(def v13_l137 (dissoc (first views) :data))


(deftest t14_l139 (is (fn v13_l137 [v] (= :point (:mark v)))))


(def v16_l146 (def sk (ns/sketch views)))


(def
 v18_l150
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
 t19_l160
 (is
  (fn
   v18_l150
   [m]
   (and
    (= :point (:mark m))
    (= 3 (:n-groups m))
    (pos? (:first-group-n-points m))))))


(def v21_l166 (ss/valid? sk))


(deftest t22_l168 (is (true? v21_l166)))


(def v24_l172 (= sk (read-string (pr-str sk))))


(deftest t25_l174 (is (true? v24_l172)))


(def v27_l182 (ns/plot views))


(def
 v29_l190
 (kind/mermaid
  "\ngraph LR\n  subgraph WHAT [\"WHAT — data + semantics\"]\n    V[\"Views\"]\n    ST[\"Statistics\"]\n    D[\"Domains\"]\n    C[\"Colors\"]\n  end\n  subgraph HOW [\"HOW — pixels + rendering\"]\n    SC[\"Scales (wadogo)\"]\n    CO[\"Coord transforms\"]\n    MS[\"Membrane scene\"]\n    SV[\"SVG conversion\"]\n  end\n  WHAT -->|sketch| HOW\n  style WHAT fill:#e8f5e9\n  style HOW fill:#e3f2fd\n"))


(def
 v31_l225
 (def
  multi-views
  [(ns/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (ns/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(def
 v32_l229
 (def
  multi-sk
  (ns/sketch multi-views {:title "Iris Petals with Regression"})))


(def v34_l233 (count (:layers (first (:panels multi-sk)))))


(deftest t35_l235 (is (fn v34_l233 [n] (= 2 n))))


(def
 v37_l239
 (let
  [layer (first (:layers (first (:panels multi-sk))))]
  {:mark (:mark layer), :n-groups (count (:groups layer))}))


(deftest
 t38_l243
 (is (fn v37_l239 [m] (and (= :point (:mark m)) (= 3 (:n-groups m))))))


(def
 v40_l247
 (let
  [layer (second (:layers (first (:panels multi-sk))))]
  {:mark (:mark layer),
   :stat-origin (:stat-origin layer),
   :n-groups (count (:groups layer)),
   :first-group-keys (set (keys (first (:groups layer))))}))


(deftest
 t41_l253
 (is
  (fn
   v40_l247
   [m]
   (and
    (= :line (:mark m))
    (= :lm (:stat-origin m))
    (= 3 (:n-groups m))
    (contains? (:first-group-keys m) :x1)))))


(def v43_l260 (:title multi-sk))


(deftest
 t44_l262
 (is (fn v43_l260 [t] (= "Iris Petals with Regression" t))))


(def v45_l264 (count (:entries (:legend multi-sk))))


(deftest t46_l266 (is (fn v45_l264 [n] (= 3 n))))


(def
 v48_l270
 (ns/plot multi-views {:title "Iris Petals with Regression"}))
