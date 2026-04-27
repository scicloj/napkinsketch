(ns
 plotje-book.architecture-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.plotje.api :as pj]
  [scicloj.plotje.impl.pose :as pose-impl]
  [scicloj.plotje.impl.plan :as plan-impl]
  [scicloj.plotje.impl.plan-schema :as ss]
  [clojure.test :refer [deftest is]]))


(def
 v3_l27
 (kind/mermaid
  "\ngraph LR\n  B[\"Pose<br/>(composable API)\"] -->|pose->draft| D[\"Draft<br/>(flat maps)\"]\n  D -->|draft->plan| P[\"Plan<br/>(data-space)\"]\n  P -->|scales + coords| M[\"Membrane<br/>(drawing primitives)\"]\n  M -->|tree walk| F[\"Plot<br/>(output)\"]\n  style B fill:#d1c4e9\n  style D fill:#e8f5e9\n  style P fill:#fff3e0\n  style M fill:#e3f2fd\n  style F fill:#fce4ec\n"))


(def
 v5_l71
 (def trace-data {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :b]}))


(def
 v7_l82
 (def trace-pose (-> trace-data (pj/lay-point :x :y {:color :g}))))


(def v9_l99 (pj/pose? trace-pose))


(deftest t10_l101 (is (true? v9_l99)))


(def v12_l105 (pose-impl/leaf? trace-pose))


(deftest t13_l107 (is (true? v12_l105)))


(def v15_l114 (:mapping trace-pose))


(deftest
 t16_l116
 (is ((fn [m] (and (= :x (:x m)) (= :y (:y m)))) v15_l114)))


(def v17_l119 (get-in trace-pose [:layers 0 :layer-type]))


(deftest t18_l121 (is ((fn [m] (= :point m)) v17_l119)))


(def v20_l125 (get-in trace-pose [:layers 0 :mapping :color]))


(deftest t21_l127 (is ((fn [m] (= :g m)) v20_l125)))


(def v23_l135 (def trace-draft (pj/draft trace-pose)))


(def v24_l138 (count trace-draft))


(deftest t25_l140 (is ((fn [n] (= 1 n)) v24_l138)))


(def v26_l142 (select-keys (first trace-draft) [:x :y :mark :color]))


(deftest
 t27_l144
 (is
  ((fn
    [m]
    (and
     (= :x (:x m))
     (= :y (:y m))
     (= :point (:mark m))
     (= :g (:color m))))
   v26_l142)))


(def v29_l155 (def trace-plan (plan-impl/draft->plan trace-draft {})))


(def v30_l158 trace-plan)


(deftest
 t31_l160
 (is ((fn [v] (and (map? v) (contains? v :panels))) v30_l158)))


(def v33_l164 (ss/valid? trace-plan))


(deftest t34_l166 (is (true? v33_l164)))


(def v36_l173 (def trace-membrane (pj/plan->membrane trace-plan)))


(def v37_l175 trace-membrane)


(deftest
 t38_l177
 (is ((fn [v] (and (vector? v) (pos? (count v)))) v37_l175)))


(def
 v40_l183
 (def
  trace-plot
  (pj/membrane->plot
   trace-membrane
   :svg
   {:total-width (:total-width trace-plan),
    :total-height (:total-height trace-plan)})))


(def v41_l188 (kind/pprint trace-plot))


(deftest
 t42_l190
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v41_l188)))


(def v44_l194 (kind/hiccup trace-plot))


(deftest
 t45_l196
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v44_l194)))


(def v47_l206 (def shortcut-plan (pj/plan trace-pose)))


(def v48_l208 (ss/valid? shortcut-plan))


(deftest t49_l210 (is (true? v48_l208)))


(def
 v51_l230
 (kind/mermaid
  "\ngraph LR\n  A[\"Pose + draft\"] -->|plan| P[\"Plan\"]\n  P --> R[\"membrane + plot\"]\n  style A fill:#e8f5e9\n  style P fill:#fff3e0\n  style R fill:#e3f2fd\n"))


(def
 v53_l259
 (def
  multi-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose :petal-length :petal-width {:color :species})
   pj/lay-point
   (pj/lay-smooth {:stat :linear-model}))))


(def v55_l267 (count (:layers multi-pose)))


(deftest t56_l269 (is ((fn [n] (= 2 n)) v55_l267)))


(def v57_l271 (mapv :layer-type (:layers multi-pose)))


(deftest
 t58_l273
 (is
  ((fn [v] (and (= :point (first v)) (= :smooth (second v))))
   v57_l271)))


(def v60_l279 (def multi-draft (pj/draft multi-pose)))


(def v61_l281 (count multi-draft))


(deftest t62_l283 (is ((fn [n] (= 2 n)) v61_l281)))


(def v63_l285 (mapv :mark multi-draft))


(deftest
 t64_l287
 (is
  ((fn [v] (and (= :point (first v)) (= :line (second v)))) v63_l285)))


(def
 v66_l292
 (def
  multi-plan
  (pj/plan multi-pose {:title "Iris Petals with Regression"})))


(def
 v67_l295
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels multi-plan)))))


(deftest
 t68_l300
 (is
  ((fn
    [v]
    (and
     (= :point (:mark (first v)))
     (= :line (:mark (second v)))
     (= 3 (:n-groups (first v)))))
   v67_l295)))


(def v70_l306 multi-plan)


(deftest
 t71_l308
 (is
  ((fn
    [m]
    (and
     (= "Iris Petals with Regression" (:title m))
     (= 3 (count (get-in m [:legend :entries])))))
   v70_l306)))


(def
 v73_l313
 (->
  (rdatasets/datasets-iris)
  (pj/pose :petal-length :petal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/options {:title "Iris Petals with Regression"})))


(deftest
 t74_l319
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v73_l313)))


(def
 v76_l325
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> POSE[\"impl/pose.clj\"]\n  API --> RES[\"impl/resolve.clj\"]\n  API --> PL[\"impl/plan.clj\"]\n  API --> COMP[\"impl/compositor.clj\"]\n  POSE --> RES\n  COMP --> POSE\n  COMP --> PL\n  PL --> RES\n  PL --> STAT[\"impl/stat.clj\"]\n  PL --> SCALE[\"impl/scale.clj\"]\n  PL --> DEFAULTS[\"impl/defaults.clj\"]\n  PL --> PS[\"impl/plan_schema.clj\"]\n  API --> RENDER[\"impl/render.clj\"]\n  RENDER --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  style API fill:#c8e6c9\n  style FR fill:#d1c4e9\n  style COMP fill:#d1c4e9\n  style PL fill:#d1c4e9\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n"))
