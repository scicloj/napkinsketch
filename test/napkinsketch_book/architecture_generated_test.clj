(ns
 napkinsketch-book.architecture-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.impl.sketch-schema :as ss]
  [clojure.test :refer [deftest is]]))


(def
 v3_l20
 (kind/mermaid
  "\ngraph LR\n  V[\"Views<br/>(API)\"] -->|resolve| S[\"PlanSchema<br/>(data-space)\"]\n  S -->|scales + coords| M[\"Membrane<br/>(pixel-space)\"]\n  M -->|tree walk| F[\"Figure<br/>(output)\"]\n  style V fill:#e8f5e9\n  style S fill:#fff3e0\n  style M fill:#e3f2fd\n  style F fill:#fce4ec\n"))


(def
 v5_l49
 (def trace-data {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :b]}))


(def
 v7_l59
 (def trace-views (-> trace-data (sk/lay-point :x :y {:color :g}))))


(def v8_l63 (kind/pprint trace-views))


(deftest
 t9_l65
 (is
  ((fn
    [v]
    (and (sk/sketch? v) (= :point (:mark (first (sk/views-of v))))))
   v8_l63)))


(def v11_l75 (def trace-plan (sk/plan trace-views)))


(def v12_l77 trace-plan)


(deftest
 t13_l79
 (is ((fn [v] (and (map? v) (contains? v :panels))) v12_l77)))


(def v15_l83 (ss/valid? trace-plan))


(deftest t16_l85 (is (true? v15_l83)))


(def v18_l98 (def trace-membrane (sk/plan->membrane trace-plan)))


(def v19_l100 trace-membrane)


(deftest
 t20_l102
 (is ((fn [v] (and (vector? v) (pos? (count v)))) v19_l100)))


(def
 v22_l109
 (def
  trace-figure
  (sk/membrane->figure
   trace-membrane
   :svg
   {:total-width (:total-width trace-plan),
    :total-height (:total-height trace-plan)})))


(def v23_l114 (kind/pprint trace-figure))


(deftest
 t24_l116
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v23_l114)))


(def v26_l120 (kind/hiccup trace-figure))


(deftest
 t27_l122
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v26_l120)))


(def
 v29_l140
 (kind/mermaid
  "\ngraph LR\n  subgraph WHAT [\"WHAT — data + semantics\"]\n    V[\"Views\"]\n    ST[\"Statistics\"]\n    D[\"Domains\"]\n    C[\"Colors\"]\n  end\n  subgraph HOW [\"HOW — pixels + rendering\"]\n    SC[\"Scales (wadogo)\"]\n    CO[\"Coord transforms\"]\n    MS[\"Membrane tree\"]\n    SV[\"SVG conversion\"]\n  end\n  WHAT -->|plan| HOW\n  style WHAT fill:#e8f5e9\n  style HOW fill:#e3f2fd\n"))


(def
 v31_l182
 (def
  multi-views
  (->
   data/iris
   (sk/view :petal_length :petal_width {:color :species})
   sk/lay-point
   sk/lay-lm)))


(def
 v32_l188
 (def
  multi-plan
  (sk/plan multi-views {:title "Iris Petals with Regression"})))


(def
 v34_l192
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels multi-plan)))))


(deftest
 t35_l197
 (is
  ((fn
    [v]
    (and
     (= :point (:mark (first v)))
     (= :line (:mark (second v)))
     (= 3 (:n-groups (first v)))))
   v34_l192)))


(def v37_l203 multi-plan)


(deftest
 t38_l205
 (is
  ((fn
    [m]
    (and
     (= "Iris Petals with Regression" (:title m))
     (= 3 (count (get-in m [:legend :entries])))))
   v37_l203)))


(def
 v40_l210
 (-> multi-views (sk/options {:title "Iris Petals with Regression"})))


(deftest
 t41_l212
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v40_l210)))


(def
 v43_l218
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> VIEW[\"impl/view.clj\"]\n  API --> PLOT[\"impl/plot.clj\"]\n  API --> PLAN[\"impl/sketch.clj\"]\n  PLAN --> VIEW\n  PLAN --> STAT[\"impl/stat.clj\"]\n  PLAN --> SCALE[\"impl/scale.clj\"]\n  PLAN --> DEFAULTS[\"impl/defaults.clj\"]\n  PLOT --> PLAN\n  PLOT --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  style API fill:#c8e6c9\n  style PLAN fill:#ffe0b2\n  style PLOT fill:#bbdefb\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n"))
