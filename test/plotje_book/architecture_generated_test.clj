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


(def v8_l103 trace-pose)


(deftest
 t9_l105
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v8_l103)))


(def v11_l109 (sort (keys trace-pose)))


(deftest t12_l111 (is ((fn [ks] (every? (set ks) [:layers])) v11_l109)))


(def v14_l123 (pj/pose? trace-pose))


(deftest t15_l125 (is (true? v14_l123)))


(def v17_l132 (def trace-draft (pj/pose->draft trace-pose)))


(def v18_l135 (type trace-draft))


(deftest
 t19_l137
 (is
  ((fn [t] (= "scicloj.plotje.impl.resolve.LeafDraft" (.getName t)))
   v18_l135)))


(def v21_l142 (:layers trace-draft))


(deftest
 t22_l144
 (is ((fn [v] (and (vector? v) (= 1 (count v)))) v21_l142)))


(def
 v23_l146
 (select-keys (first (:layers trace-draft)) [:x :y :mark :color]))


(deftest
 t24_l148
 (is
  ((fn
    [m]
    (and
     (= :x (:x m))
     (= :y (:y m))
     (= :point (:mark m))
     (= :g (:color m))))
   v23_l146)))


(def v26_l156 (:opts trace-draft))


(deftest t27_l158 (is ((fn [m] (= {} m)) v26_l156)))


(def v29_l165 (def trace-plan (pj/draft->plan trace-draft)))


(def v30_l168 (type trace-plan))


(deftest
 t31_l170
 (is
  ((fn [t] (= "scicloj.plotje.impl.resolve.Plan" (.getName t)))
   v30_l168)))


(def v33_l174 (ss/valid? trace-plan))


(deftest t34_l176 (is (true? v33_l174)))


(def v36_l181 (sort (keys trace-plan)))


(deftest
 t37_l183
 (is
  ((fn
    [ks]
    (every? (set ks) [:panels :total-width :total-height :legend]))
   v36_l181)))


(def v39_l190 (def trace-membrane (pj/plan->membrane trace-plan)))


(def v40_l192 (count trace-membrane))


(deftest t41_l194 (is ((fn [n] (pos? n)) v40_l192)))


(def
 v43_l199
 (mapv (fn [el] (.getSimpleName (class el))) trace-membrane))


(deftest t44_l201 (is ((fn [v] (every? string? v)) v43_l199)))


(def
 v46_l208
 (def
  trace-plot
  (pj/membrane->plot
   trace-membrane
   :svg
   {:total-width (:total-width trace-plan),
    :total-height (:total-height trace-plan)})))


(def v47_l213 (kind/pprint trace-plot))


(deftest
 t48_l215
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v47_l213)))


(def v50_l219 (kind/hiccup trace-plot))


(deftest
 t51_l221
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v50_l219)))


(def
 v53_l258
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
 t54_l270
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
   v53_l258)))


(def v56_l289 (:layers (pj/pose->draft trace-pose)))


(deftest t57_l291 (is ((fn [v] (= 1 (count v))) v56_l289)))


(def
 v59_l294
 (count (:panels (pj/draft->plan (pj/pose->draft trace-pose)))))


(deftest t60_l296 (is ((fn [n] (= 1 n)) v59_l294)))


(def
 v62_l299
 (count
  (pj/plan->membrane (pj/draft->plan (pj/pose->draft trace-pose)))))


(deftest t63_l301 (is ((fn [n] (pos? n)) v62_l299)))


(def
 v65_l311
 (def
  composite-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose :petal-length :petal-width {:color :species})
   (pj/pose
    [[:petal-length :petal-width] [:sepal-length :sepal-width]])
   pj/lay-point)))


(def v67_l320 (type (pj/pose->draft composite-pose)))


(deftest
 t68_l322
 (is
  ((fn
    [t]
    (= "scicloj.plotje.impl.resolve.CompositeDraft" (.getName t)))
   v67_l320)))


(def v70_l327 (type (-> composite-pose pj/pose->draft pj/draft->plan)))


(deftest
 t71_l329
 (is
  ((fn
    [t]
    (= "scicloj.plotje.impl.resolve.CompositePlan" (.getName t)))
   v70_l327)))


(def
 v73_l353
 (kind/mermaid
  "\ngraph LR\n  A[\"Pose + draft\"] -->|plan| P[\"Plan\"]\n  P --> R[\"membrane + plot\"]\n  style A fill:#e8f5e9\n  style P fill:#fff3e0\n  style R fill:#e3f2fd\n"))


(def
 v75_l384
 (def
  multi-pose
  (->
   (rdatasets/datasets-iris)
   (pj/pose :petal-length :petal-width {:color :species})
   pj/lay-point
   (pj/lay-smooth {:stat :linear-model}))))


(def v77_l392 (count (:layers multi-pose)))


(deftest t78_l394 (is ((fn [n] (= 2 n)) v77_l392)))


(def v79_l396 (mapv :layer-type (:layers multi-pose)))


(deftest
 t80_l398
 (is
  ((fn [v] (and (= :point (first v)) (= :smooth (second v))))
   v79_l396)))


(def v82_l404 (def multi-draft (pj/pose->draft multi-pose)))


(def v83_l406 (count (:layers multi-draft)))


(deftest t84_l408 (is ((fn [n] (= 2 n)) v83_l406)))


(def v85_l410 (mapv :mark (:layers multi-draft)))


(deftest
 t86_l412
 (is
  ((fn [v] (and (= :point (first v)) (= :line (second v)))) v85_l410)))


(def
 v88_l417
 (def
  multi-plan
  (pj/plan multi-pose {:title "Iris Petals with Regression"})))


(def
 v89_l420
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels multi-plan)))))


(deftest
 t90_l425
 (is
  ((fn
    [v]
    (and
     (= :point (:mark (first v)))
     (= :line (:mark (second v)))
     (= 3 (:n-groups (first v)))))
   v89_l420)))


(deftest
 t92_l431
 (is
  ((fn
    [_]
    (and
     (= "Iris Petals with Regression" (:title multi-plan))
     (= 3 (count (get-in multi-plan [:legend :entries])))))
   v89_l420)))


(def
 v94_l436
 (->
  (rdatasets/datasets-iris)
  (pj/pose :petal-length :petal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/options {:title "Iris Petals with Regression"})))


(deftest
 t95_l442
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v94_l436)))


(def
 v97_l448
 (kind/mermaid
  "\ngraph TD\n  API[\"api.clj\"] --> POSE[\"impl/pose.clj\"]\n  API --> RES[\"impl/resolve.clj\"]\n  API --> PL[\"impl/plan.clj\"]\n  API --> COMP[\"impl/compositor.clj\"]\n  POSE --> RES\n  COMP --> POSE\n  COMP --> PL\n  PL --> RES\n  PL --> STAT[\"impl/stat.clj\"]\n  PL --> SCALE[\"impl/scale.clj\"]\n  PL --> DEFAULTS[\"impl/defaults.clj\"]\n  PL --> PS[\"impl/plan_schema.clj\"]\n  API --> RENDER[\"impl/render.clj\"]\n  RENDER --> SVG[\"render/svg.clj\"]\n  SVG --> MEMBRANE[\"render/membrane.clj\"]\n  MEMBRANE --> PANEL[\"render/panel.clj\"]\n  PANEL --> MARK[\"render/mark.clj\"]\n  PANEL --> SCALE\n  PANEL --> COORD[\"impl/coord.clj\"]\n  style API fill:#c8e6c9\n  style COMP fill:#d1c4e9\n  style PL fill:#d1c4e9\n  style SVG fill:#f8bbd0\n  style MEMBRANE fill:#f8bbd0\n"))
