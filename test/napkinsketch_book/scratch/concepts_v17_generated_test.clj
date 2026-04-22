(ns
 napkinsketch-book.scratch.concepts-v17-generated-test
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   [clojure.test :refer [deftest is]]))

(def v2_l20 (def iris (rdatasets/datasets-iris)))

(def
  v4_l33
  (kind/pprint
   (-> iris (sk/view :sepal-length :sepal-width) sk/lay-point)))

(deftest
  t5_l38
  (is
   ((fn
      [sk]
      (and
       (= 1 (count (:views sk)))
       (=
        {:x :sepal-length, :y :sepal-width}
        (:mapping (first (:views sk))))
       (= 1 (count (:layers sk)))))
    v4_l33)))

(def
  v7_l48
  (kind/pprint (-> iris (sk/lay-point :sepal-length :sepal-width))))

(deftest
  t8_l52
  (is
   ((fn
      [sk]
      (and
       (= 1 (count (:views sk)))
       (= 0 (count (:layers sk)))
       (= 1 (count (:layers (first (:views sk)))))))
    v7_l48)))

(def
  v10_l69
  (def
    view-scoped
    (->
     iris
     (sk/view :sepal-length :sepal-width {:color :species})
     (sk/view :petal-length :petal-width)
     sk/lay-point)))

(def v11_l75 (kind/pprint view-scoped))

(deftest
  t12_l77
  (is
   ((fn
      [sk]
      (and
       (= {} (:mapping sk))
       (= :species (:color (:mapping (first (:views sk)))))
       (nil? (:color (:mapping (second (:views sk)))))))
    v11_l75)))

(def v14_l86 view-scoped)

(deftest
  t15_l88
  (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v14_l86)))

(def
  v17_l93
  (def
    sketch-scoped
    (->
     (sk/sketch iris {:color :species})
     (sk/view :sepal-length :sepal-width)
     (sk/view :petal-length :petal-width)
     sk/lay-point)))

(def v18_l99 (kind/pprint sketch-scoped))

(deftest
  t19_l101
  (is
   ((fn
      [sk]
      (and
       (= :species (:color (:mapping sk)))
       (nil? (:color (:mapping (first (:views sk)))))
       (nil? (:color (:mapping (second (:views sk)))))))
    v18_l99)))

(def v20_l107 sketch-scoped)

(deftest
  t21_l109
  (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v20_l107)))

(def
  v23_l120
  (def
    multi-col
    (->
     iris
     (sk/lay-density [:sepal-length :sepal-width :petal-length]))))

(def v24_l124 (kind/pprint multi-col))

(deftest
  t25_l126
  (is
   ((fn
      [sk]
      (and
       (= 3 (count (:views sk)))
       (= 0 (count (:layers sk)))
       (every?
        (fn* [p1__81992#] (= 1 (count (:layers p1__81992#))))
        (:views sk))))
    v24_l124)))

(def v26_l132 multi-col)

(deftest
  t27_l134
  (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v26_l132)))

(def
  v29_l146
  (def
    log-plot
    (->
     iris
     (sk/lay-point :sepal-length :sepal-width)
     (sk/scale :x :log))))

(def v30_l151 (kind/pprint log-plot))

(deftest
  t31_l153
  (is
   ((fn
      [sk]
      (and
       (= {:type :log} (:x-scale (:opts sk)))
       (nil? (:x-scale (:mapping (first (:views sk)))))))
    v30_l151)))

(def v32_l158 log-plot)

(deftest
  t33_l160
  (is ((fn [v] (= 1 (:panels (sk/svg-summary v)))) v32_l158)))

(def
  v35_l170
  (def
    annotated
    (->
     iris
     (sk/lay-point :sepal-length :sepal-width {:color :species})
     (sk/lay-rule-h {:y-intercept 3.0})
     (sk/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1}))))

(def v36_l176 (kind/pprint annotated))

(deftest
  t37_l178
  (is
   ((fn [sk] (and (= 1 (count (:views sk))) (= 2 (count (:layers sk)))))
    v36_l176)))

(def v38_l183 annotated)

(deftest
  t39_l185
  (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v38_l183)))

(def
  v41_l198
  (-> iris (sk/view :sepal-length :sepal-width) sk/lay-point (sk/lay-smooth {:stat :linear-model})))

(deftest
  t42_l203
  (is
   ((fn
      [v]
      (let
       [s (sk/svg-summary v)]
        (and (= 150 (:points s)) (pos? (:lines s)))))
    v41_l198)))

(def
  v44_l211
  (->
   (sk/sketch iris {:color :species})
   (sk/view :sepal-length :sepal-width)
   sk/lay-point
   (sk/lay-smooth {:stat :linear-model})))

(deftest
  t45_l216
  (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v44_l211)))

(def
  v47_l219
  (->
   (sk/sketch iris {:color :species})
   (sk/view :sepal-length :sepal-width)
   (sk/lay-point {:color nil})
   (sk/lay-smooth {:stat :linear-model})))

(deftest
  t48_l224
  (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v47_l219)))

(def
  v50_l231
  (def
    targeted
    (->
     iris
     (sk/view :sepal-width)
     (sk/lay-histogram :sepal-width)
     (sk/view :sepal-width)
     (sk/lay-density :sepal-width))))

(def v51_l238 (kind/pprint targeted))

(deftest
  t52_l240
  (is
   ((fn
      [sk]
      (and
       (= 2 (count (:views sk)))
       (= :histogram (:layer-type (first (:layers (first (:views sk))))))
       (= :density (:layer-type (first (:layers (second (:views sk))))))))
    v51_l238)))

(def v53_l246 targeted)

(deftest
  t54_l248
  (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v53_l246)))

(def
  v56_l267
  (def
    my-sketch
    (->
     (sk/sketch iris {:color :species})
     (sk/view :sepal-length :sepal-width)
     sk/lay-point
     (sk/lay-smooth {:stat :linear-model})
     (sk/options {:title "Iris"}))))

(def v57_l274 (kind/pprint my-sketch))

(deftest
  t58_l276
  (is
   ((fn
      [sk]
      (and
       (tc/dataset? (:data sk))
       (= :species (:color (:mapping sk)))
       (= 1 (count (:views sk)))
       (= 2 (count (:layers sk)))
       (= "Iris" (:title (:opts sk)))))
    v57_l274)))

(def v59_l284 my-sketch)

(deftest
  t60_l286
  (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v59_l284)))
