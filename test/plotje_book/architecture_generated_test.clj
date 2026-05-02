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
  "\ngraph LR\n  X[\"Raw data\"] -->|pj/->pose| B[\"Pose\"]\n  B -->|pj/options pj/lay-* ...| B\n  B -->|pj/pose->draft| D[\"Draft\"]\n  D -->|pj/draft->plan| P[\"Plan\"]\n  P -->|pj/plan->membrane| M[\"Membrane\"]\n  M -->|pj/membrane->plot| F[\"Plot\"]\n  style X fill:#eee,stroke-dasharray:3 3\n  style B fill:#d1c4e9\n  style D fill:#e8f5e9\n  style P fill:#fff3e0\n  style M fill:#e3f2fd\n  style F fill:#fce4ec\n"))


(def
 v5_l146
 (def
  trace-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose :petal-length :petal-width {:color :species})
   pj/lay-point
   (pj/lay-smooth {:stat :linear-model}))))


(def v7_l154 trace-pose)


(deftest
 t8_l156
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v7_l154)))


(def v10_l164 (kind/pprint trace-pose))


(deftest
 t11_l166
 (is
  ((fn
    [v]
    (and
     (pj/pose? v)
     (=
      [:petal-length :petal-width :species]
      [(:x (:mapping v)) (:y (:mapping v)) (:color (:mapping v))])
     (= 2 (count (:layers v)))
     (= [:point :smooth] (mapv :layer-type (:layers v)))))
   v10_l164)))


(def v13_l190 (def trace-draft (pj/pose->draft trace-pose)))


(def v14_l193 (kind/pprint trace-draft))


(deftest
 t15_l195
 (is
  ((fn
    [d]
    (and
     (pj/leaf-draft? d)
     (= 2 (count (:layers d)))
     (let
      [layers (:layers d)]
      (and
       (= [:point :line] (mapv :mark layers))
       (every?
        (fn* [p1__91165#] (= :petal-length (:x p1__91165#)))
        layers)
       (every?
        (fn* [p1__91166#] (= :petal-width (:y p1__91166#)))
        layers)
       (every?
        (fn* [p1__91167#] (= :species (:color p1__91167#)))
        layers)))
     (= {} (:opts d))))
   v14_l193)))


(def v17_l212 (def trace-plan (pj/draft->plan trace-draft)))


(def v19_l219 (kind/pprint trace-plan))


(deftest
 t20_l221
 (is
  ((fn
    [v]
    (and
     (pj/leaf-plan? v)
     (= 1 (count (:panels v)))
     (some? (:total-width v))
     (some? (:total-height v))
     (= 3 (count (get-in v [:legend :entries])))
     (let
      [layers (:layers (first (:panels v)))]
      (and
       (= [:point :line] (mapv :mark layers))
       (= 3 (count (:groups (first layers))))
       (= 3 (count (:groups (second layers))))))))
   v19_l219)))


(def v22_l233 (ss/valid? trace-plan))


(deftest t23_l235 (is (true? v22_l233)))


(def v25_l242 (def trace-membrane (pj/plan->membrane trace-plan)))


(def v27_l247 (kind/pprint trace-membrane))


(deftest
 t28_l249
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (pos? (count v))
     (every?
      (fn*
       [p1__91168#]
       (.startsWith (.getName (class p1__91168#)) "membrane.ui."))
      v)))
   v27_l247)))


(def
 v30_l263
 (def trace-plot (pj/membrane->plot trace-membrane :svg {})))


(def v31_l266 (kind/pprint trace-plot))


(deftest
 t32_l268
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v31_l266)))


(def v34_l272 (kind/hiccup trace-plot))


(deftest
 t35_l274
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v34_l272)))


(def
 v37_l378
 (let
  [pose-with-opts
   (->
    trace-pose
    (pj/options
     {:title "Iris Petals", :x-label "Petal length", :width 700}))
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
 t38_l394
 (is
  ((fn
    [m]
    (and
     (:title-match m)
     (:x-label-match m)
     (:width-match m)
     (= "Iris Petals" (:title m))
     (= "Petal length" (:x-label m))
     (= 700 (:width m))))
   v37_l378)))


(def v40_l422 (pj/pose trace-data))


(deftest
 t41_l424
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
         (fn* [p1__91169#] (.startsWith p1__91169# "rgb"))
         (:colors s)))))))
   v40_l422)))


(def
 v43_l472
 (def
  composite-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose
    [[:petal-length :petal-width] [:sepal-length :sepal-width]]
    {:color :species})
   pj/lay-point)))


(def v44_l479 composite-pose)


(deftest
 t45_l481
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v44_l479)))


(def v47_l488 (-> composite-pose pj/draft kind/pprint))


(deftest
 t48_l490
 (is
  ((fn [d] (and (pj/composite-draft? d) (= 2 (count (:sub-drafts d)))))
   v47_l488)))


(def v50_l495 (pj/plan composite-pose))


(deftest
 t51_l497
 (is
  ((fn [p] (and (pj/composite-plan? p) (= 2 (count (:sub-plots p)))))
   v50_l495)))


(def v53_l505 (pj/membrane composite-pose))


(deftest
 t54_l507
 (is
  ((fn
    [m]
    (and
     (vector? m)
     (pos? (count m))
     (let
      [{:keys [total-width total-height]} (meta m)]
      (and (number? total-width) (number? total-height)))))
   v53_l505)))


(def v56_l517 (kind/pprint (pj/plot composite-pose)))


(deftest
 t57_l519
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v56_l517)))


(def
 v59_l545
 (kind/mermaid
  "\ngraph LR\n  A[\"Pose + draft\"] -->|plan| P[\"Plan\"]\n  P --> R[\"membrane + plot\"]\n  style A fill:#e8f5e9\n  style P fill:#fff3e0\n  style R fill:#e3f2fd\n"))


(def
 v61_l624
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> POSE[\"impl/pose.clj\"]\n  API --> RES[\"impl/resolve.clj\"]\n  API --> PL[\"impl/plan.clj\"]\n  API --> COMP[\"impl/compositor.clj\"]\n  POSE --> RES\n  COMP --> POSE\n  COMP --> PL\n  PL --> RES\n  PL --> STAT[\"impl/stat.clj\"]\n  PL --> SCALE[\"impl/scale.clj\"]\n  PL --> DEFAULTS[\"impl/defaults.clj\"]\n  PL --> PS[\"impl/plan_schema.clj\"]\n  API --> RENDER[\"impl/render.clj\"]\n  RENDER --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  API --> RC[\"render/composite.clj\"]\n  RC --> MEMBRANE\n  style API fill:#c8e6c9\n  style COMP fill:#d1c4e9\n  style PL fill:#d1c4e9\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n  style RC fill:#f8bbd0\n"))
