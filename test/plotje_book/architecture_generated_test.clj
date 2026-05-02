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
 v5_l86
 (def trace-data {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :b]}))


(def
 v7_l98
 (def
  trace-pose
  (-> trace-data pj/->pose (pj/lay-point :x :y {:color :g}))))


(def v9_l105 trace-pose)


(deftest
 t10_l107
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v9_l105)))


(def v12_l113 (kind/pprint trace-pose))


(deftest
 t13_l115
 (is
  ((fn
    [v]
    (and
     (pj/pose? v)
     (= [:x :y] [(:x (:mapping v)) (:y (:mapping v))])
     (= 1 (count (:layers v)))
     (= :point (:layer-type (first (:layers v))))
     (= :g (:color (:mapping (first (:layers v)))))))
   v12_l113)))


(def v15_l128 (def trace-draft (pj/pose->draft trace-pose)))


(def v16_l131 (kind/pprint trace-draft))


(deftest
 t17_l133
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
   v16_l131)))


(def v19_l147 (def trace-plan (pj/draft->plan trace-draft)))


(def v20_l150 trace-plan)


(deftest
 t21_l152
 (is
  ((fn
    [v]
    (and
     (pj/leaf-plan? v)
     (= 1 (count (:panels v)))
     (some? (:total-width v))
     (some? (:total-height v))
     (some? (:legend v))))
   v20_l150)))


(def v23_l160 (ss/valid? trace-plan))


(deftest t24_l162 (is (true? v23_l160)))


(def v26_l169 (def trace-membrane (pj/plan->membrane trace-plan)))


(def v27_l171 trace-membrane)


(deftest
 t28_l173
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (pos? (count v))
     (every?
      (fn*
       [p1__82343#]
       (.startsWith (.getName (class p1__82343#)) "membrane.ui."))
      v)))
   v27_l171)))


(def
 v30_l184
 (def
  trace-plot
  (pj/membrane->plot
   trace-membrane
   :svg
   {:total-width (:total-width trace-plan),
    :total-height (:total-height trace-plan)})))


(def v31_l189 (kind/pprint trace-plot))


(deftest
 t32_l191
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v31_l189)))


(def v34_l195 (kind/hiccup trace-plot))


(deftest
 t35_l197
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v34_l195)))


(def
 v37_l234
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
 t38_l246
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
   v37_l234)))


(def
 v40_l272
 (def
  composite-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose
    [[:petal-length :petal-width] [:sepal-length :sepal-width]]
    {:color :species})
   pj/lay-point)))


(def v41_l279 composite-pose)


(deftest
 t42_l281
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v41_l279)))


(def v44_l288 (-> composite-pose pj/pose->draft kind/pprint))


(deftest
 t45_l290
 (is
  ((fn [d] (and (pj/composite-draft? d) (= 2 (count (:sub-drafts d)))))
   v44_l288)))


(def v47_l296 (-> composite-pose pj/pose->draft pj/draft->plan))


(deftest
 t48_l298
 (is
  ((fn [p] (and (pj/composite-plan? p) (= 2 (count (:sub-plots p)))))
   v47_l296)))


(def
 v50_l323
 (kind/mermaid
  "\ngraph LR\n  A[\"Pose + draft\"] -->|plan| P[\"Plan\"]\n  P --> R[\"membrane + plot\"]\n  style A fill:#e8f5e9\n  style P fill:#fff3e0\n  style R fill:#e3f2fd\n"))


(def
 v52_l354
 (def
  multi-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose :petal-length :petal-width {:color :species})
   pj/lay-point
   (pj/lay-smooth {:stat :linear-model}))))


(def v53_l360 multi-pose)


(deftest
 t54_l362
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v53_l360)))


(def v56_l371 (def multi-draft (pj/pose->draft multi-pose)))


(def v57_l373 (kind/pprint multi-draft))


(deftest
 t58_l375
 (is
  ((fn
    [d]
    (and
     (pj/leaf-draft? d)
     (= 2 (count (:layers d)))
     (= [:point :line] (mapv :mark (:layers d)))))
   v57_l373)))


(def
 v60_l383
 (def
  multi-plan
  (pj/plan multi-pose {:title "Iris Petals with Regression"})))


(def v61_l386 multi-plan)


(deftest
 t62_l388
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
   v61_l386)))


(def
 v64_l397
 (pj/options multi-pose {:title "Iris Petals with Regression"}))


(deftest
 t65_l399
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v64_l397)))


(def
 v67_l405
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> POSE[\"impl/pose.clj\"]\n  API --> RES[\"impl/resolve.clj\"]\n  API --> PL[\"impl/plan.clj\"]\n  API --> COMP[\"impl/compositor.clj\"]\n  POSE --> RES\n  COMP --> POSE\n  COMP --> PL\n  PL --> RES\n  PL --> STAT[\"impl/stat.clj\"]\n  PL --> SCALE[\"impl/scale.clj\"]\n  PL --> DEFAULTS[\"impl/defaults.clj\"]\n  PL --> PS[\"impl/plan_schema.clj\"]\n  API --> RENDER[\"impl/render.clj\"]\n  RENDER --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  API --> RC[\"render/composite.clj\"]\n  RC --> MEMBRANE\n  style API fill:#c8e6c9\n  style COMP fill:#d1c4e9\n  style PL fill:#d1c4e9\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n  style RC fill:#f8bbd0\n"))
