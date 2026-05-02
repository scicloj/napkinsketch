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
 v5_l122
 (def trace-data {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :b]}))


(def
 v7_l134
 (def
  trace-pose
  (-> trace-data pj/->pose (pj/lay-point :x :y {:color :g}))))


(def v9_l141 trace-pose)


(deftest
 t10_l143
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v9_l141)))


(def v12_l149 (kind/pprint trace-pose))


(deftest
 t13_l151
 (is
  ((fn
    [v]
    (and
     (pj/pose? v)
     (= [:x :y] [(:x (:mapping v)) (:y (:mapping v))])
     (= 1 (count (:layers v)))
     (= :point (:layer-type (first (:layers v))))
     (= :g (:color (:mapping (first (:layers v)))))))
   v12_l149)))


(def v15_l164 (def trace-draft (pj/pose->draft trace-pose)))


(def v16_l167 (kind/pprint trace-draft))


(deftest
 t17_l169
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
   v16_l167)))


(def v19_l183 (def trace-plan (pj/draft->plan trace-draft)))


(def v21_l190 (kind/pprint trace-plan))


(deftest
 t22_l192
 (is
  ((fn
    [v]
    (and
     (pj/leaf-plan? v)
     (= 1 (count (:panels v)))
     (some? (:total-width v))
     (some? (:total-height v))
     (some? (:legend v))))
   v21_l190)))


(def v24_l200 (ss/valid? trace-plan))


(deftest t25_l202 (is (true? v24_l200)))


(def v27_l209 (def trace-membrane (pj/plan->membrane trace-plan)))


(def v29_l214 (kind/pprint trace-membrane))


(deftest
 t30_l216
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (pos? (count v))
     (every?
      (fn*
       [p1__89327#]
       (.startsWith (.getName (class p1__89327#)) "membrane.ui."))
      v)))
   v29_l214)))


(def
 v32_l230
 (def trace-plot (pj/membrane->plot trace-membrane :svg {})))


(def v33_l233 (kind/pprint trace-plot))


(deftest
 t34_l235
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v33_l233)))


(def v36_l239 (kind/hiccup trace-plot))


(deftest
 t37_l241
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v36_l239)))


(def
 v39_l344
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
 t40_l359
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
   v39_l344)))


(def v42_l387 (pj/pose trace-data))


(deftest
 t43_l389
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
         (fn* [p1__89328#] (.startsWith p1__89328# "rgb"))
         (:colors s)))))))
   v42_l387)))


(def
 v45_l434
 (def
  composite-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose
    [[:petal-length :petal-width] [:sepal-length :sepal-width]]
    {:color :species})
   pj/lay-point)))


(def v46_l441 composite-pose)


(deftest
 t47_l443
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v46_l441)))


(def v49_l450 (-> composite-pose pj/draft kind/pprint))


(deftest
 t50_l452
 (is
  ((fn [d] (and (pj/composite-draft? d) (= 2 (count (:sub-drafts d)))))
   v49_l450)))


(def v52_l457 (pj/plan composite-pose))


(deftest
 t53_l459
 (is
  ((fn [p] (and (pj/composite-plan? p) (= 2 (count (:sub-plots p)))))
   v52_l457)))


(def v55_l467 (pj/membrane composite-pose))


(deftest
 t56_l469
 (is
  ((fn
    [m]
    (and
     (vector? m)
     (pos? (count m))
     (let
      [{:keys [total-width total-height]} (meta m)]
      (and (number? total-width) (number? total-height)))))
   v55_l467)))


(def v58_l479 (kind/pprint (pj/plot composite-pose)))


(deftest
 t59_l481
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v58_l479)))


(def
 v61_l497
 (def
  multi-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose :petal-length :petal-width {:color :species})
   pj/lay-point
   (pj/lay-smooth {:stat :linear-model}))))


(def v62_l503 multi-pose)


(deftest
 t63_l505
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v62_l503)))


(def v65_l514 (def multi-draft (pj/draft multi-pose)))


(def v66_l516 (kind/pprint multi-draft))


(deftest
 t67_l518
 (is
  ((fn
    [d]
    (and
     (pj/leaf-draft? d)
     (= 2 (count (:layers d)))
     (= [:point :line] (mapv :mark (:layers d)))))
   v66_l516)))


(def
 v69_l526
 (def
  multi-plan
  (pj/plan multi-pose {:title "Iris Petals with Regression"})))


(def v70_l529 multi-plan)


(deftest
 t71_l531
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
   v70_l529)))


(def
 v73_l543
 (def
  multi-membrane
  (pj/membrane multi-pose {:title "Iris Petals with Regression"})))


(def v74_l546 (kind/pprint multi-membrane))


(deftest
 t75_l548
 (is
  ((fn
    [m]
    (and
     (vector? m)
     (pos? (count m))
     (= "Iris Petals with Regression" (:title (meta m)))))
   v74_l546)))


(def
 v77_l556
 (kind/pprint
  (pj/plot multi-pose {:title "Iris Petals with Regression"})))


(deftest
 t78_l559
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v77_l556)))


(def
 v80_l581
 (kind/mermaid
  "\ngraph LR\n  A[\"Pose + draft\"] -->|plan| P[\"Plan\"]\n  P --> R[\"membrane + plot\"]\n  style A fill:#e8f5e9\n  style P fill:#fff3e0\n  style R fill:#e3f2fd\n"))


(def
 v82_l650
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> POSE[\"impl/pose.clj\"]\n  API --> RES[\"impl/resolve.clj\"]\n  API --> PL[\"impl/plan.clj\"]\n  API --> COMP[\"impl/compositor.clj\"]\n  POSE --> RES\n  COMP --> POSE\n  COMP --> PL\n  PL --> RES\n  PL --> STAT[\"impl/stat.clj\"]\n  PL --> SCALE[\"impl/scale.clj\"]\n  PL --> DEFAULTS[\"impl/defaults.clj\"]\n  PL --> PS[\"impl/plan_schema.clj\"]\n  API --> RENDER[\"impl/render.clj\"]\n  RENDER --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  API --> RC[\"render/composite.clj\"]\n  RC --> MEMBRANE\n  style API fill:#c8e6c9\n  style COMP fill:#d1c4e9\n  style PL fill:#d1c4e9\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n  style RC fill:#f8bbd0\n"))
