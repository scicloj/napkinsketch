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
 v5_l42
 (kind/mermaid
  "\ngraph TB\n  subgraph WHAT [\"What to draw\"]\n    direction TB\n    A1[\"api.clj\"]\n    A2[\"impl/view.clj\"]\n    A3[\"impl/stat.clj\"]\n    A4[\"impl/sketch.clj\"]\n  end\n  subgraph HOW [\"How to draw it\"]\n    direction TB\n    B1[\"impl/scale.clj\"]\n    B2[\"render/mark.clj\"]\n    B3[\"render/panel.clj\"]\n    B4[\"impl/plot.clj\"]\n    B5[\"render/membrane.clj\"]\n    B6[\"render/svg.clj\"]\n  end\n  WHAT -->|sketch| HOW\n  style WHAT fill:#e8f5e9\n  style HOW fill:#e3f2fd\n"))


(def
 v7_l101
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> VIEW[\"impl/view.clj\"]\n  API --> PLOT[\"impl/plot.clj\"]\n  API --> SKETCH[\"impl/sketch.clj\"]\n  SKETCH --> VIEW\n  SKETCH --> STAT[\"impl/stat.clj\"]\n  SKETCH --> SCALE[\"impl/scale.clj\"]\n  SKETCH --> DEFAULTS[\"impl/defaults.clj\"]\n  PLOT --> SKETCH\n  PLOT --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  style API fill:#c8e6c9\n  style SKETCH fill:#ffe0b2\n  style PLOT fill:#bbdefb\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n"))


(def
 v9_l128
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v11_l135
 (def
  views
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(def v13_l140 (dissoc (first views) :data))


(deftest t14_l142 (is ((fn [v] (= :point (:mark v))) v13_l140)))


(def v16_l149 (def sk (sk/sketch views)))


(def
 v18_l153
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
 t19_l163
 (is
  ((fn
    [m]
    (and
     (= :point (:mark m))
     (= 3 (:n-groups m))
     (pos? (:first-group-n-points m))))
   v18_l153)))


(def v21_l169 (ss/valid? sk))


(deftest t22_l171 (is (true? v21_l169)))


(def v24_l175 (= sk (read-string (pr-str sk))))


(deftest t25_l177 (is (true? v24_l175)))


(def v27_l185 (sk/plot views))


(deftest
 t28_l187
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v27_l185)))


(def
 v30_l197
 (kind/mermaid
  "\ngraph LR\n  subgraph WHAT [\"WHAT — data + semantics\"]\n    V[\"Views\"]\n    ST[\"Statistics\"]\n    D[\"Domains\"]\n    C[\"Colors\"]\n  end\n  subgraph HOW [\"HOW — pixels + rendering\"]\n    SC[\"Scales (wadogo)\"]\n    CO[\"Coord transforms\"]\n    MS[\"Membrane tree\"]\n    SV[\"SVG conversion\"]\n  end\n  WHAT -->|sketch| HOW\n  style WHAT fill:#e8f5e9\n  style HOW fill:#e3f2fd\n"))


(def
 v32_l232
 (def
  multi-views
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(def
 v33_l236
 (def
  multi-sk
  (sk/sketch multi-views {:title "Iris Petals with Regression"})))


(def v35_l240 (count (:layers (first (:panels multi-sk)))))


(deftest t36_l242 (is ((fn [n] (= 2 n)) v35_l240)))


(def
 v38_l246
 (let
  [layer (first (:layers (first (:panels multi-sk))))]
  {:mark (:mark layer), :n-groups (count (:groups layer))}))


(deftest
 t39_l250
 (is
  ((fn [m] (and (= :point (:mark m)) (= 3 (:n-groups m)))) v38_l246)))


(def
 v41_l254
 (let
  [layer (second (:layers (first (:panels multi-sk))))]
  {:mark (:mark layer),
   :n-groups (count (:groups layer)),
   :first-group-keys (set (keys (first (:groups layer))))}))


(deftest
 t42_l259
 (is
  ((fn
    [m]
    (and
     (= :line (:mark m))
     (= 3 (:n-groups m))
     (contains? (:first-group-keys m) :x1)))
   v41_l254)))


(def v44_l265 (:title multi-sk))


(deftest
 t45_l267
 (is ((fn [t] (= "Iris Petals with Regression" t)) v44_l265)))


(def v46_l269 (count (:entries (:legend multi-sk))))


(deftest t47_l271 (is ((fn [n] (= 3 n)) v46_l269)))


(def
 v49_l275
 (sk/plot multi-views {:title "Iris Petals with Regression"}))


(deftest
 t50_l277
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v49_l275)))
