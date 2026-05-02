(ns
 plotje-book.architecture-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.plotje.api :as pj]
  [scicloj.plotje.impl.plan-schema :as ss]
  [clojure.test :refer [deftest is]]))


(def
 v3_l30
 (kind/mermaid
  "\ngraph LR\n  B[\"Pose\"] -->|pj/pose->draft| D[\"Draft\"]\n  D -->|pj/draft->plan| P[\"Plan\"]\n  P -->|pj/plan->membrane| M[\"Membrane\"]\n  M -->|pj/membrane->plot| F[\"Plot\"]\n  style B fill:#d1c4e9\n  style D fill:#e8f5e9\n  style P fill:#fff3e0\n  style M fill:#e3f2fd\n  style F fill:#fce4ec\n"))


(def
 v5_l116
 (def trace-data {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :b]}))


(def
 v7_l128
 (def
  trace-pose
  (-> trace-data pj/->pose (pj/lay-point :x :y {:color :g}))))


(def v9_l135 trace-pose)


(deftest
 t10_l137
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v9_l135)))


(def v12_l143 (kind/pprint trace-pose))


(deftest
 t13_l145
 (is
  ((fn
    [v]
    (and
     (pj/pose? v)
     (= [:x :y] [(:x (:mapping v)) (:y (:mapping v))])
     (= 1 (count (:layers v)))
     (= :point (:layer-type (first (:layers v))))
     (= :g (:color (:mapping (first (:layers v)))))))
   v12_l143)))


(def v15_l158 (def trace-draft (pj/pose->draft trace-pose)))


(def v16_l161 (kind/pprint trace-draft))


(deftest
 t17_l163
 (is
  ((fn
    [d]
    (and
     (pj/leaf-draft? d)
     (= 1 (count (:layers d)))
     (let
      [l (first (:layers d))]
      (and
       (= :x (:x l))
       (= :y (:y l))
       (= :point (:mark l))
       (= :g (:color l))))
     (= {} (:opts d))))
   v16_l161)))


(def v19_l177 (def trace-plan (pj/draft->plan trace-draft)))


(def v21_l184 (kind/pprint trace-plan))


(deftest
 t22_l186
 (is
  ((fn
    [v]
    (and
     (pj/leaf-plan? v)
     (= 1 (count (:panels v)))
     (some? (:total-width v))
     (some? (:total-height v))
     (some? (:legend v))))
   v21_l184)))


(def v24_l194 (ss/valid? trace-plan))


(deftest t25_l196 (is (true? v24_l194)))


(def v27_l203 (def trace-membrane (pj/plan->membrane trace-plan)))


(def v29_l208 (kind/pprint trace-membrane))


(deftest
 t30_l210
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (pos? (count v))
     (every?
      (fn*
       [p1__82274#]
       (.startsWith (.getName (class p1__82274#)) "membrane.ui."))
      v)))
   v29_l208)))


(def
 v32_l221
 (def
  trace-plot
  (pj/membrane->plot
   trace-membrane
   :svg
   {:total-width (:total-width trace-plan),
    :total-height (:total-height trace-plan)})))


(def v33_l226 (kind/pprint trace-plot))


(deftest
 t34_l228
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v33_l226)))


(def v36_l232 (kind/hiccup trace-plot))


(deftest
 t37_l234
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v36_l232)))


(def
 v39_l288
 (let
  [pose-with-opts
   (->
    trace-data
    (pj/lay-point :x :y {:color :g})
    (pj/options {:title "trace", :x-label "X", :width 700}))
   via-plan
   (pj/plan pose-with-opts)
   via-arrows
   (-> pose-with-opts pj/pose->draft pj/draft->plan)]
  {:title-match (= (:title via-plan) (:title via-arrows)),
   :x-label-match (= (:x-label via-plan) (:x-label via-arrows)),
   :width-match (= (:width via-plan) (:width via-arrows)),
   :title (:title via-plan),
   :x-label (:x-label via-plan),
   :width (:width via-plan)}))


(deftest
 t40_l300
 (is
  ((fn
    [m]
    (and
     (:title-match m)
     (:x-label-match m)
     (:width-match m)
     (= "trace" (:title m))
     (= "X" (:x-label m))
     (= 700 (:width m))))
   v39_l288)))


(def v42_l328 (pj/pose trace-data))


(deftest
 t43_l330
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 5 (:points s))
      (=
       2
       (count
        (filter
         (fn* [p1__82275#] (.startsWith p1__82275# "rgb"))
         (:colors s)))))))
   v42_l328)))


(def
 v45_l375
 (def
  composite-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose
    [[:petal-length :petal-width] [:sepal-length :sepal-width]]
    {:color :species})
   pj/lay-point)))


(def v46_l382 composite-pose)


(deftest
 t47_l384
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v46_l382)))


(def v49_l391 (-> composite-pose pj/pose->draft kind/pprint))


(deftest
 t50_l393
 (is
  ((fn [d] (and (pj/composite-draft? d) (= 2 (count (:sub-drafts d)))))
   v49_l391)))


(def v52_l399 (-> composite-pose pj/pose->draft pj/draft->plan))


(deftest
 t53_l401
 (is
  ((fn [p] (and (pj/composite-plan? p) (= 2 (count (:sub-plots p)))))
   v52_l399)))


(def
 v55_l426
 (kind/mermaid
  "\ngraph LR\n  A[\"Pose + draft\"] -->|plan| P[\"Plan\"]\n  P --> R[\"membrane + plot\"]\n  style A fill:#e8f5e9\n  style P fill:#fff3e0\n  style R fill:#e3f2fd\n"))


(def
 v57_l495
 (def
  multi-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose :petal-length :petal-width {:color :species})
   pj/lay-point
   (pj/lay-smooth {:stat :linear-model}))))


(def v58_l501 multi-pose)


(deftest
 t59_l503
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v58_l501)))


(def v61_l512 (def multi-draft (pj/pose->draft multi-pose)))


(def v62_l514 (kind/pprint multi-draft))


(deftest
 t63_l516
 (is
  ((fn
    [d]
    (and
     (pj/leaf-draft? d)
     (= 2 (count (:layers d)))
     (= [:point :line] (mapv :mark (:layers d)))))
   v62_l514)))


(def
 v65_l524
 (def
  multi-plan
  (pj/plan multi-pose {:title "Iris Petals with Regression"})))


(def v66_l527 multi-plan)


(deftest
 t67_l529
 (is
  ((fn
    [m]
    (and
     (= "Iris Petals with Regression" (:title m))
     (= 3 (count (get-in m [:legend :entries])))
     (let
      [layers (:layers (first (:panels m)))]
      (and
       (= [:point :line] (mapv :mark layers))
       (= 3 (count (:groups (first layers))))
       (= 3 (count (:groups (second layers))))))))
   v66_l527)))


(def
 v69_l538
 (pj/options multi-pose {:title "Iris Petals with Regression"}))


(deftest
 t70_l540
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v69_l538)))


(def
 v72_l546
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> POSE[\"impl/pose.clj\"]\n  API --> RES[\"impl/resolve.clj\"]\n  API --> PL[\"impl/plan.clj\"]\n  API --> COMP[\"impl/compositor.clj\"]\n  POSE --> RES\n  COMP --> POSE\n  COMP --> PL\n  PL --> RES\n  PL --> STAT[\"impl/stat.clj\"]\n  PL --> SCALE[\"impl/scale.clj\"]\n  PL --> DEFAULTS[\"impl/defaults.clj\"]\n  PL --> PS[\"impl/plan_schema.clj\"]\n  API --> RENDER[\"impl/render.clj\"]\n  RENDER --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  API --> RC[\"render/composite.clj\"]\n  RC --> MEMBRANE\n  style API fill:#c8e6c9\n  style COMP fill:#d1c4e9\n  style PL fill:#d1c4e9\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n  style RC fill:#f8bbd0\n"))
