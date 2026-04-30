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


(def v8_l86 trace-pose)


(deftest
 t9_l88
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v8_l86)))


(def v11_l103 (pj/pose? trace-pose))


(deftest t12_l105 (is (true? v11_l103)))


(def v14_l109 (pose-impl/leaf? trace-pose))


(deftest t15_l111 (is (true? v14_l109)))


(def v17_l118 (:mapping trace-pose))


(deftest
 t18_l120
 (is ((fn [m] (and (= :x (:x m)) (= :y (:y m)))) v17_l118)))


(def v19_l123 (get-in trace-pose [:layers 0 :layer-type]))


(deftest t20_l125 (is ((fn [m] (= :point m)) v19_l123)))


(def v22_l129 (get-in trace-pose [:layers 0 :mapping :color]))


(deftest t23_l131 (is ((fn [m] (= :g m)) v22_l129)))


(def v25_l139 (def trace-draft (pj/draft trace-pose)))


(def v26_l142 (count trace-draft))


(deftest t27_l144 (is ((fn [n] (= 1 n)) v26_l142)))


(def v28_l146 (select-keys (first trace-draft) [:x :y :mark :color]))


(deftest
 t29_l148
 (is
  ((fn
    [m]
    (and
     (= :x (:x m))
     (= :y (:y m))
     (= :point (:mark m))
     (= :g (:color m))))
   v28_l146)))


(def v31_l159 (def trace-plan (plan-impl/draft->plan trace-draft {})))


(def v32_l162 trace-plan)


(deftest
 t33_l164
 (is ((fn [v] (and (map? v) (contains? v :panels))) v32_l162)))


(def v35_l168 (ss/valid? trace-plan))


(deftest t36_l170 (is (true? v35_l168)))


(def v38_l177 (def trace-membrane (pj/plan->membrane trace-plan)))


(def v39_l179 trace-membrane)


(deftest
 t40_l181
 (is ((fn [v] (and (vector? v) (pos? (count v)))) v39_l179)))


(def
 v42_l187
 (def
  trace-plot
  (pj/membrane->plot
   trace-membrane
   :svg
   {:total-width (:total-width trace-plan),
    :total-height (:total-height trace-plan)})))


(def v43_l192 (kind/pprint trace-plot))


(deftest
 t44_l194
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v43_l192)))


(def v46_l198 (kind/hiccup trace-plot))


(deftest
 t47_l200
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v46_l198)))


(def v49_l210 (def shortcut-plan (pj/plan trace-pose)))


(def v50_l212 (ss/valid? shortcut-plan))


(deftest t51_l214 (is (true? v50_l212)))


(def
 v53_l234
 (kind/mermaid
  "\ngraph LR\n  A[\"Pose + draft\"] -->|plan| P[\"Plan\"]\n  P --> R[\"membrane + plot\"]\n  style A fill:#e8f5e9\n  style P fill:#fff3e0\n  style R fill:#e3f2fd\n"))


(def
 v55_l263
 (def
  multi-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose :petal-length :petal-width {:color :species})
   pj/lay-point
   (pj/lay-smooth {:stat :linear-model}))))


(def v57_l271 (count (:layers multi-pose)))


(deftest t58_l273 (is ((fn [n] (= 2 n)) v57_l271)))


(def v59_l275 (mapv :layer-type (:layers multi-pose)))


(deftest
 t60_l277
 (is
  ((fn [v] (and (= :point (first v)) (= :smooth (second v))))
   v59_l275)))


(def v62_l283 (def multi-draft (pj/draft multi-pose)))


(def v63_l285 (count multi-draft))


(deftest t64_l287 (is ((fn [n] (= 2 n)) v63_l285)))


(def v65_l289 (mapv :mark multi-draft))


(deftest
 t66_l291
 (is
  ((fn [v] (and (= :point (first v)) (= :line (second v)))) v65_l289)))


(def
 v68_l296
 (def
  multi-plan
  (pj/plan multi-pose {:title "Iris Petals with Regression"})))


(def
 v69_l299
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels multi-plan)))))


(deftest
 t70_l304
 (is
  ((fn
    [v]
    (and
     (= :point (:mark (first v)))
     (= :line (:mark (second v)))
     (= 3 (:n-groups (first v)))))
   v69_l299)))


(def v72_l310 multi-plan)


(deftest
 t73_l312
 (is
  ((fn
    [m]
    (and
     (= "Iris Petals with Regression" (:title m))
     (= 3 (count (get-in m [:legend :entries])))))
   v72_l310)))


(def
 v75_l317
 (->
  (rdatasets/datasets-iris)
  (pj/pose :petal-length :petal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/options {:title "Iris Petals with Regression"})))


(deftest
 t76_l323
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v75_l317)))


(def
 v78_l329
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> POSE[\"impl/pose.clj\"]\n  API --> RES[\"impl/resolve.clj\"]\n  API --> PL[\"impl/plan.clj\"]\n  API --> COMP[\"impl/compositor.clj\"]\n  POSE --> RES\n  COMP --> POSE\n  COMP --> PL\n  PL --> RES\n  PL --> STAT[\"impl/stat.clj\"]\n  PL --> SCALE[\"impl/scale.clj\"]\n  PL --> DEFAULTS[\"impl/defaults.clj\"]\n  PL --> PS[\"impl/plan_schema.clj\"]\n  API --> RENDER[\"impl/render.clj\"]\n  RENDER --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  style API fill:#c8e6c9\n  style FR fill:#d1c4e9\n  style COMP fill:#d1c4e9\n  style PL fill:#d1c4e9\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n"))
