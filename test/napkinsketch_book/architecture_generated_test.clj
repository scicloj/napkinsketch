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
 v3_l34
 (kind/mermaid
  "\ngraph LR\n  B[\"Frame<br/>(composable API)\"] -->|frame->draft| D[\"Draft<br/>(flat maps)\"]\n  D -->|draft->plan| P[\"Plan<br/>(data-space)\"]\n  P -->|scales + coords| M[\"Membrane<br/>(drawing primitives)\"]\n  M -->|tree walk| F[\"Plot<br/>(output)\"]\n  style B fill:#d1c4e9\n  style D fill:#e8f5e9\n  style P fill:#fff3e0\n  style M fill:#e3f2fd\n  style F fill:#fce4ec\n"))


(def
 v5_l75
 (def trace-data {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :b]}))


(def
 v7_l86
 (def trace-sk (-> trace-data (sk/lay-point :x :y {:color :g}))))


(def v9_l106 (sketch-impl/sketch? trace-sk))


(deftest t10_l108 (is (true? v9_l106)))


(def v12_l112 (count (:views trace-sk)))


(deftest t13_l114 (is ((fn [n] (= 1 n)) v12_l112)))


(def v14_l116 (:views trace-sk))


(deftest
 t15_l118
 (is
  ((fn
    [views]
    (let
     [v (first views)]
     (and
      (= :x (get-in v [:mapping :x]))
      (= :y (get-in v [:mapping :y]))
      (= 1 (count (:layers v))))))
   v14_l116)))


(def v17_l127 (get-in (:views trace-sk) [0 :layers 0 :layer-type]))


(deftest t18_l129 (is ((fn [m] (= :point m)) v17_l127)))


(def v20_l137 (def trace-draft (sk/draft trace-sk)))


(def v21_l140 (count trace-draft))


(deftest t22_l142 (is ((fn [n] (= 1 n)) v21_l140)))


(def v23_l144 (select-keys (first trace-draft) [:x :y :mark :color]))


(deftest
 t24_l146
 (is
  ((fn
    [m]
    (and
     (= :x (:x m))
     (= :y (:y m))
     (= :point (:mark m))
     (= :g (:color m))))
   v23_l144)))


(def v26_l157 (def trace-plan (plan-impl/draft->plan trace-draft {})))


(def v27_l160 trace-plan)


(deftest
 t28_l162
 (is ((fn [v] (and (map? v) (contains? v :panels))) v27_l160)))


(def v30_l166 (ss/valid? trace-plan))


(deftest t31_l168 (is (true? v30_l166)))


(def v33_l175 (def trace-membrane (sk/plan->membrane trace-plan)))


(def v34_l177 trace-membrane)


(deftest
 t35_l179
 (is ((fn [v] (and (vector? v) (pos? (count v)))) v34_l177)))


(def
 v37_l185
 (def
  trace-plot
  (sk/membrane->plot
   trace-membrane
   :svg
   {:total-width (:total-width trace-plan),
    :total-height (:total-height trace-plan)})))


(def v38_l190 (kind/pprint trace-plot))


(deftest
 t39_l192
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v38_l190)))


(def v41_l196 (kind/hiccup trace-plot))


(deftest
 t42_l198
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v41_l196)))


(def v44_l208 (def shortcut-plan (sk/plan trace-sk)))


(def v45_l210 (ss/valid? shortcut-plan))


(deftest t46_l212 (is (true? v45_l210)))


(def
 v48_l232
 (kind/mermaid
  "\ngraph LR\n  A[\"Frame + draft\"] -->|plan| P[\"Plan\"]\n  P --> R[\"membrane + plot\"]\n  style A fill:#e8f5e9\n  style P fill:#fff3e0\n  style R fill:#e3f2fd\n"))


(def
 v50_l261
 (def
  multi-sk
  (->
   (rdatasets/datasets-iris)
   (sk/frame :petal-length :petal-width {:color :species})
   sk/lay-point
   (sk/lay-smooth {:stat :linear-model}))))


(def v52_l269 (count (:views multi-sk)))


(deftest t53_l271 (is ((fn [n] (= 1 n)) v52_l269)))


(def v54_l273 (mapv :layer-type (:layers multi-sk)))


(deftest
 t55_l275
 (is
  ((fn [v] (and (= :point (first v)) (= :smooth (second v))))
   v54_l273)))


(def v57_l281 (def multi-draft (sk/draft multi-sk)))


(def v58_l283 (count multi-draft))


(deftest t59_l285 (is ((fn [n] (= 2 n)) v58_l283)))


(def v60_l287 (mapv :mark multi-draft))


(deftest
 t61_l289
 (is
  ((fn [v] (and (= :point (first v)) (= :line (second v)))) v60_l287)))


(def
 v63_l294
 (def
  multi-plan
  (sk/plan multi-sk {:title "Iris Petals with Regression"})))


(def
 v64_l297
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels multi-plan)))))


(deftest
 t65_l302
 (is
  ((fn
    [v]
    (and
     (= :point (:mark (first v)))
     (= :line (:mark (second v)))
     (= 3 (:n-groups (first v)))))
   v64_l297)))


(def v67_l308 multi-plan)


(deftest
 t68_l310
 (is
  ((fn
    [m]
    (and
     (= "Iris Petals with Regression" (:title m))
     (= 3 (count (get-in m [:legend :entries])))))
   v67_l308)))


(def
 v70_l315
 (->
  (rdatasets/datasets-iris)
  (sk/view :petal-length :petal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  (sk/options {:title "Iris Petals with Regression"})))


(deftest
 t71_l321
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v70_l315)))


(def
 v73_l327
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> SK[\"impl/sketch.clj\"]\n  API --> RES[\"impl/resolve.clj\"]\n  API --> PL[\"impl/plan.clj\"]\n  SK --> RES\n  SK --> PL\n  PL --> RES\n  PL --> STAT[\"impl/stat.clj\"]\n  PL --> SCALE[\"impl/scale.clj\"]\n  PL --> DEFAULTS[\"impl/defaults.clj\"]\n  PL --> SS[\"impl/sketch_schema.clj\"]\n  SK --> RENDER[\"impl/render.clj\"]\n  RENDER --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  style API fill:#c8e6c9\n  style SK fill:#d1c4e9\n  style PL fill:#d1c4e9\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n"))
