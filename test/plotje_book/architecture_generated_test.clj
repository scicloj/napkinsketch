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
 v5_l119
 (def trace-data {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :b]}))


(def
 v7_l131
 (def
  trace-pose
  (-> trace-data pj/->pose (pj/lay-point :x :y {:color :g}))))


(def v9_l138 trace-pose)


(deftest
 t10_l140
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v9_l138)))


(def v12_l146 (kind/pprint trace-pose))


(deftest
 t13_l148
 (is
  ((fn
    [v]
    (and
     (pj/pose? v)
     (= [:x :y] [(:x (:mapping v)) (:y (:mapping v))])
     (= 1 (count (:layers v)))
     (= :point (:layer-type (first (:layers v))))
     (= :g (:color (:mapping (first (:layers v)))))))
   v12_l146)))


(def v15_l161 (def trace-draft (pj/pose->draft trace-pose)))


(def v16_l164 (kind/pprint trace-draft))


(deftest
 t17_l166
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
   v16_l164)))


(def v19_l180 (def trace-plan (pj/draft->plan trace-draft)))


(def v21_l187 (kind/pprint trace-plan))


(deftest
 t22_l189
 (is
  ((fn
    [v]
    (and
     (pj/leaf-plan? v)
     (= 1 (count (:panels v)))
     (some? (:total-width v))
     (some? (:total-height v))
     (some? (:legend v))))
   v21_l187)))


(def v24_l197 (ss/valid? trace-plan))


(deftest t25_l199 (is (true? v24_l197)))


(def v27_l206 (def trace-membrane (pj/plan->membrane trace-plan)))


(def v29_l211 (kind/pprint trace-membrane))


(deftest
 t30_l213
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (pos? (count v))
     (every?
      (fn*
       [p1__82068#]
       (.startsWith (.getName (class p1__82068#)) "membrane.ui."))
      v)))
   v29_l211)))


(def
 v32_l227
 (def trace-plot (pj/membrane->plot trace-membrane :svg {})))


(def v33_l230 (kind/pprint trace-plot))


(deftest
 t34_l232
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v33_l230)))


(def v36_l236 (kind/hiccup trace-plot))


(deftest
 t37_l238
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v36_l236)))


(def
 v39_l328
 (let
  [pose-with-opts
   (->
    trace-data
    (pj/lay-point :x :y {:color :g})
    (pj/options {:title "trace", :x-label "X", :width 700}))
   via-plan
   (pj/plan pose-with-opts)
   via-arrows
   (-> pose-with-opts pj/->pose pj/pose->draft pj/draft->plan)]
  {:title-match (= (:title via-plan) (:title via-arrows)),
   :x-label-match (= (:x-label via-plan) (:x-label via-arrows)),
   :width-match (= (:width via-plan) (:width via-arrows)),
   :title (:title via-plan),
   :x-label (:x-label via-plan),
   :width (:width via-plan)}))


(deftest
 t40_l343
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
   v39_l328)))


(def v42_l371 (pj/pose trace-data))


(deftest
 t43_l373
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
         (fn* [p1__82069#] (.startsWith p1__82069# "rgb"))
         (:colors s)))))))
   v42_l371)))


(def
 v45_l418
 (def
  composite-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose
    [[:petal-length :petal-width] [:sepal-length :sepal-width]]
    {:color :species})
   pj/lay-point)))


(def v46_l425 composite-pose)


(deftest
 t47_l427
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v46_l425)))


(def v49_l434 (-> composite-pose pj/draft kind/pprint))


(deftest
 t50_l436
 (is
  ((fn [d] (and (pj/composite-draft? d) (= 2 (count (:sub-drafts d)))))
   v49_l434)))


(def v52_l441 (pj/plan composite-pose))


(deftest
 t53_l443
 (is
  ((fn [p] (and (pj/composite-plan? p) (= 2 (count (:sub-plots p)))))
   v52_l441)))


(def v55_l451 (pj/membrane composite-pose))


(deftest
 t56_l453
 (is
  ((fn
    [m]
    (and
     (vector? m)
     (pos? (count m))
     (let
      [{:keys [total-width total-height]} (meta m)]
      (and (number? total-width) (number? total-height)))))
   v55_l451)))


(def v58_l463 (kind/pprint (pj/plot composite-pose)))


(deftest
 t59_l465
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v58_l463)))


(def
 v61_l481
 (def
  multi-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose :petal-length :petal-width {:color :species})
   pj/lay-point
   (pj/lay-smooth {:stat :linear-model}))))


(def v62_l487 multi-pose)


(deftest
 t63_l489
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v62_l487)))


(def v65_l498 (def multi-draft (pj/draft multi-pose)))


(def v66_l500 (kind/pprint multi-draft))


(deftest
 t67_l502
 (is
  ((fn
    [d]
    (and
     (pj/leaf-draft? d)
     (= 2 (count (:layers d)))
     (= [:point :line] (mapv :mark (:layers d)))))
   v66_l500)))


(def
 v69_l510
 (def
  multi-plan
  (pj/plan multi-pose {:title "Iris Petals with Regression"})))


(def v70_l513 multi-plan)


(deftest
 t71_l515
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
   v70_l513)))


(def
 v73_l527
 (def
  multi-membrane
  (pj/membrane multi-pose {:title "Iris Petals with Regression"})))


(def v74_l530 (kind/pprint multi-membrane))


(deftest
 t75_l532
 (is
  ((fn
    [m]
    (and
     (vector? m)
     (pos? (count m))
     (= "Iris Petals with Regression" (:title (meta m)))))
   v74_l530)))


(def
 v77_l540
 (kind/pprint
  (pj/plot multi-pose {:title "Iris Petals with Regression"})))


(deftest
 t78_l543
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v77_l540)))


(def
 v80_l565
 (kind/mermaid
  "\ngraph LR\n  A[\"Pose + draft\"] -->|plan| P[\"Plan\"]\n  P --> R[\"membrane + plot\"]\n  style A fill:#e8f5e9\n  style P fill:#fff3e0\n  style R fill:#e3f2fd\n"))


(def
 v82_l634
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> POSE[\"impl/pose.clj\"]\n  API --> RES[\"impl/resolve.clj\"]\n  API --> PL[\"impl/plan.clj\"]\n  API --> COMP[\"impl/compositor.clj\"]\n  POSE --> RES\n  COMP --> POSE\n  COMP --> PL\n  PL --> RES\n  PL --> STAT[\"impl/stat.clj\"]\n  PL --> SCALE[\"impl/scale.clj\"]\n  PL --> DEFAULTS[\"impl/defaults.clj\"]\n  PL --> PS[\"impl/plan_schema.clj\"]\n  API --> RENDER[\"impl/render.clj\"]\n  RENDER --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  API --> RC[\"render/composite.clj\"]\n  RC --> MEMBRANE\n  style API fill:#c8e6c9\n  style COMP fill:#d1c4e9\n  style PL fill:#d1c4e9\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n  style RC fill:#f8bbd0\n"))
