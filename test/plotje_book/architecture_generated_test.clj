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
 v5_l68
 (def trace-data {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :b]}))


(def
 v7_l79
 (def trace-pose (-> trace-data (pj/lay-point :x :y {:color :g}))))


(def v9_l96 (pj/pose? trace-pose))


(deftest t10_l98 (is (true? v9_l96)))


(def v12_l102 (pose-impl/leaf? trace-pose))


(deftest t13_l104 (is (true? v12_l102)))


(def v15_l111 (:mapping trace-pose))


(deftest
 t16_l113
 (is ((fn [m] (and (= :x (:x m)) (= :y (:y m)))) v15_l111)))


(def v17_l116 (get-in trace-pose [:layers 0 :layer-type]))


(deftest t18_l118 (is ((fn [m] (= :point m)) v17_l116)))


(def v20_l122 (get-in trace-pose [:layers 0 :mapping :color]))


(deftest t21_l124 (is ((fn [m] (= :g m)) v20_l122)))


(def v23_l132 (def trace-draft (pj/draft trace-pose)))


(def v24_l135 (count trace-draft))


(deftest t25_l137 (is ((fn [n] (= 1 n)) v24_l135)))


(def v26_l139 (select-keys (first trace-draft) [:x :y :mark :color]))


(deftest
 t27_l141
 (is
  ((fn
    [m]
    (and
     (= :x (:x m))
     (= :y (:y m))
     (= :point (:mark m))
     (= :g (:color m))))
   v26_l139)))


(def v29_l152 (def trace-plan (plan-impl/draft->plan trace-draft {})))


(def v30_l155 trace-plan)


(deftest
 t31_l157
 (is ((fn [v] (and (map? v) (contains? v :panels))) v30_l155)))


(def v33_l161 (ss/valid? trace-plan))


(deftest t34_l163 (is (true? v33_l161)))


(def v36_l170 (def trace-membrane (pj/plan->membrane trace-plan)))


(def v37_l172 trace-membrane)


(deftest
 t38_l174
 (is ((fn [v] (and (vector? v) (pos? (count v)))) v37_l172)))


(def
 v40_l180
 (def
  trace-plot
  (pj/membrane->plot
   trace-membrane
   :svg
   {:total-width (:total-width trace-plan),
    :total-height (:total-height trace-plan)})))


(def v41_l185 (kind/pprint trace-plot))


(deftest
 t42_l187
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v41_l185)))


(def v44_l191 (kind/hiccup trace-plot))


(deftest
 t45_l193
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v44_l191)))


(def v47_l203 (def shortcut-plan (pj/plan trace-pose)))


(def v48_l205 (ss/valid? shortcut-plan))


(deftest t49_l207 (is (true? v48_l205)))


(def
 v51_l227
 (kind/mermaid
  "\ngraph LR\n  A[\"Pose + draft\"] -->|plan| P[\"Plan\"]\n  P --> R[\"membrane + plot\"]\n  style A fill:#e8f5e9\n  style P fill:#fff3e0\n  style R fill:#e3f2fd\n"))


(def
 v53_l256
 (def
  multi-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose :petal-length :petal-width {:color :species})
   pj/lay-point
   (pj/lay-smooth {:stat :linear-model}))))


(def v55_l264 (count (:layers multi-pose)))


(deftest t56_l266 (is ((fn [n] (= 2 n)) v55_l264)))


(def v57_l268 (mapv :layer-type (:layers multi-pose)))


(deftest
 t58_l270
 (is
  ((fn [v] (and (= :point (first v)) (= :smooth (second v))))
   v57_l268)))


(def v60_l276 (def multi-draft (pj/draft multi-pose)))


(def v61_l278 (count multi-draft))


(deftest t62_l280 (is ((fn [n] (= 2 n)) v61_l278)))


(def v63_l282 (mapv :mark multi-draft))


(deftest
 t64_l284
 (is
  ((fn [v] (and (= :point (first v)) (= :line (second v)))) v63_l282)))


(def
 v66_l289
 (def
  multi-plan
  (pj/plan multi-pose {:title "Iris Petals with Regression"})))


(def
 v67_l292
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels multi-plan)))))


(deftest
 t68_l297
 (is
  ((fn
    [v]
    (and
     (= :point (:mark (first v)))
     (= :line (:mark (second v)))
     (= 3 (:n-groups (first v)))))
   v67_l292)))


(def v70_l303 multi-plan)


(deftest
 t71_l305
 (is
  ((fn
    [m]
    (and
     (= "Iris Petals with Regression" (:title m))
     (= 3 (count (get-in m [:legend :entries])))))
   v70_l303)))


(def
 v73_l310
 (->
  (rdatasets/datasets-iris)
  (pj/pose :petal-length :petal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/options {:title "Iris Petals with Regression"})))


(deftest
 t74_l316
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v73_l310)))


(def
 v76_l322
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> POSE[\"impl/pose.clj\"]\n  API --> RES[\"impl/resolve.clj\"]\n  API --> PL[\"impl/plan.clj\"]\n  API --> COMP[\"impl/compositor.clj\"]\n  POSE --> RES\n  COMP --> POSE\n  COMP --> PL\n  PL --> RES\n  PL --> STAT[\"impl/stat.clj\"]\n  PL --> SCALE[\"impl/scale.clj\"]\n  PL --> DEFAULTS[\"impl/defaults.clj\"]\n  PL --> PS[\"impl/plan_schema.clj\"]\n  API --> RENDER[\"impl/render.clj\"]\n  RENDER --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  style API fill:#c8e6c9\n  style FR fill:#d1c4e9\n  style COMP fill:#d1c4e9\n  style PL fill:#d1c4e9\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n"))
