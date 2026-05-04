(ns
 plotje-book.glossary-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [clojure2d.color :as c2d]
  [clojure.test :refer [deftest is]]))


(def
 v3_l31
 (def
  my-pose
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/options {:title "Iris"}))))


(def v4_l36 my-pose)


(deftest
 t5_l38
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v4_l36)))


(def v7_l44 (kind/pprint my-pose))


(deftest
 t8_l46
 (is
  ((fn
    [pose]
    (and
     (some? (:data pose))
     (= :sepal-length (get-in pose [:mapping :x]))
     (= :sepal-width (get-in pose [:mapping :y]))
     (= :species (get-in pose [:layers 0 :mapping :color]))
     (= "Iris" (get-in pose [:opts :title]))))
   v7_l44)))


(def v10_l107 (-> my-pose :layers first :layer-type))


(deftest t11_l109 (is ((fn [k] (= :point k)) v10_l107)))


(def
 v13_l143
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v14_l147
 (->
  tips
  (pj/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t15_l150
 (is
  ((fn
    [v]
    (let
     [s
      (pj/svg-summary v)
      dinner-bar
      (->
       tips
       (pj/lay-value-bar :day :count {:color :meal, :position :stack})
       pj/plan
       (get-in [:panels 0 :layers 0 :groups 1]))]
     (and (= 4 (:polygons s)) (every? pos? (:y0s dinner-bar)))))
   v14_l147)))


(def v17_l182 (-> my-pose pj/draft kind/pprint))


(deftest
 t18_l184
 (is
  ((fn
    [d]
    (and
     (pj/leaf-draft? d)
     (= 1 (count (:layers d)))
     (= :point (:mark (first (:layers d))))))
   v17_l182)))


(def v20_l198 (-> my-pose pj/draft :layers first kind/pprint))


(deftest
 t21_l200
 (is
  ((fn
    [d]
    (and
     (some? (:data d))
     (= :sepal-length (:x d))
     (= :sepal-width (:y d))
     (= :species (:color d))
     (= :point (:mark d))))
   v20_l198)))


(def
 v23_l259
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point
   :sepal-length
   :sepal-width
   {:color :species, :size :petal-length, :alpha 0.7})))


(deftest
 t24_l263
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= #{0.7} (:alphas s)))))
   v23_l259)))


(def
 v26_l278
 (->
  (rdatasets/datasets-iris)
  (pj/lay-line :sepal-length :sepal-width {:group :species})))


(deftest
 t27_l281
 (is
  ((fn
    [v]
    (let
     [groups
      (->
       (rdatasets/datasets-iris)
       (pj/lay-line :sepal-length :sepal-width {:group :species})
       pj/plan
       (get-in [:panels 0 :layers 0 :groups]))]
     (and
      (= 3 (:lines (pj/svg-summary v)))
      (= 3 (count groups))
      (= ["setosa" "versicolor" "virginica"] (mapv :label groups)))))
   v26_l278)))


(def
 v29_l302
 (-> {:x [1 2 3], :y [4 5 6]} (pj/lay-point :x :y {:nudge-x 0.5})))


(deftest
 t30_l305
 (is
  ((fn
    [v]
    (let
     [xs
      (->
       {:x [1 2 3], :y [4 5 6]}
       (pj/lay-point :x :y {:nudge-x 0.5})
       pj/plan
       (get-in [:panels 0 :layers 0 :groups 0 :xs]))]
     (and (= 3 (:points (pj/svg-summary v))) (= [1.5 2.5 3.5] xs))))
   v29_l302)))


(def v32_l326 (merge (pj/layer-type-lookup :point) {:jitter true}))


(deftest t33_l328 (is ((fn [m] (true? (:jitter m))) v32_l326)))


(def
 v35_l340
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point))


(deftest
 t36_l344
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v35_l340)))


(def v38_l358 (def my-plan (pj/plan my-pose)))


(def v39_l360 (kind/pprint my-plan))


(deftest
 t40_l362
 (is
  ((fn
    [plan]
    (and
     (vector? (:panels plan))
     (= 1 (count (:panels plan)))
     (= 600 (:width plan))
     (= 400 (:height plan))
     (some? (:legend plan))))
   v39_l360)))


(def v42_l377 (kind/pprint (first (:panels my-plan))))


(deftest
 t43_l379
 (is
  ((fn
    [p]
    (and
     (= :cartesian (:coord p))
     (= [4.12 8.08] (:x-domain p))
     (= 1 (count (:layers p)))))
   v42_l377)))


(def v45_l391 (kind/pprint (get-in my-plan [:panels 0 :layers 0])))


(deftest
 t46_l393
 (is
  ((fn
    [layer]
    (and
     (= :point (:mark layer))
     (= 3 (count (:groups layer)))
     (every? :xs (:groups layer))))
   v45_l391)))


(def
 v48_l465
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t49_l469
 (is
  ((fn
    [m]
    (and
     (= [4.12 8.08] (:x-domain m))
     (= 2 (count (:y-domain m)))
     (number? (first (:y-domain m)))))
   v48_l465)))


(def v51_l484 (-> my-plan :panels first :x-ticks))


(deftest
 t52_l486
 (is
  ((fn
    [m]
    (and
     (vector? (:values m))
     (vector? (:labels m))
     (= (count (:values m)) (count (:labels m)))
     (false? (:categorical? m))))
   v51_l484)))


(def
 v54_l525
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t55_l529
 (is
  ((fn
    [v]
    (and
     (= 3 (:polygons (pj/svg-summary v)))
     (=
      :flip
      (->
       (rdatasets/datasets-iris)
       (pj/lay-bar :species)
       (pj/coord :flip)
       pj/plan
       (get-in [:panels 0 :coord])))))
   v54_l525)))


(def
 v57_l552
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/facet :species)))


(deftest
 t58_l556
 (is
  ((fn
    [v]
    (let
     [s
      (pj/svg-summary v)
      n-panels
      (count
       (:panels
        (pj/plan
         (->
          (rdatasets/datasets-iris)
          (pj/lay-point :sepal-length :sepal-width)
          (pj/facet :species)))))]
     (and (= 3 (:panels s)) (= 3 n-panels))))
   v57_l552)))


(def
 v60_l592
 (def
  annotated
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width)
   (pj/lay-rule-h {:y-intercept 3.0}))))


(def v61_l597 annotated)


(def v62_l599 (kind/pprint (nth (:layers annotated) 1)))


(deftest
 t63_l601
 (is
  ((fn
    [layer]
    (and
     (= :rule-h (:layer-type layer))
     (= 3.0 (get-in layer [:mapping :y-intercept]))))
   v62_l599)))


(def v65_l613 (kind/pprint (:legend my-plan)))


(deftest
 t66_l615
 (is
  ((fn
    [leg]
    (and
     (map? leg)
     (= :species (:title leg))
     (= 3 (count (:entries leg)))
     (=
      ["setosa" "versicolor" "virginica"]
      (mapv :label (:entries leg)))))
   v65_l613)))


(def
 v68_l639
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})))


(deftest
 t69_l643
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v68_l639)))


(def v71_l666 (def my-membrane (pj/plan->membrane my-plan)))


(def v73_l672 (kind/pprint my-membrane))


(deftest
 t74_l674
 (is
  ((fn
    [m]
    (let
     [walk-text
      (fn
       walk
       [d]
       (cond
        (string? (:text d))
        (:text d)
        (:drawable d)
        (walk (:drawable d))
        (:drawables d)
        (some walk (:drawables d))))
      drawables
      (membrane.ui/children m)
      texts
      (mapv walk-text drawables)]
     (and
      (pj/membrane? m)
      (= 9 (count drawables))
      (=
       ["Iris" "sepal width" "sepal length" "species"]
       (vec (take 4 texts))))))
   v73_l672)))


(def v76_l698 (def my-plot (pj/plan->plot my-plan :svg {})))


(def v78_l705 (kind/hiccup my-plot))


(deftest
 t79_l707
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= :svg (first my-plot))
      (= 150 (:points s))
      (= 600.0 (double (:width s))))))
   v78_l705)))


(def
 v81_l723
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette :set2})))


(deftest
 t82_l727
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v81_l723)))


(def v84_l733 (count (c2d/find-palette #".*")))


(deftest t85_l735 (is ((fn [n] (<= 5000 n)) v84_l733)))


(def
 v87_l747
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (pj/lay-point :x :y {:color :c})
  (pj/options {:color-scale :inferno})))


(deftest
 t88_l751
 (is
  ((fn
    [v]
    (and
     (= 50 (:points (pj/svg-summary v)))
     (=
      :inferno
      (:color-scale
       (:legend
        (pj/plan
         (->
          {:x (range 50), :y (range 50), :c (range 50)}
          (pj/lay-point :x :y {:color :c})
          (pj/options {:color-scale :inferno}))))))))
   v87_l747)))


(def
 v90_l777
 (select-keys
  (pj/config)
  [:width :height :theme :palette :color-scale]))


(deftest
 t91_l779
 (is
  ((fn
    [m]
    (and (number? (:width m)) (number? (:height m)) (map? (:theme m))))
   v90_l777)))


(def v93_l796 (sort (keys pj/plot-option-docs)))


(deftest
 t94_l798
 (is
  ((fn
    [ks]
    (and
     (= 14 (count ks))
     (some #{:caption :title :y-label :x-label :subtitle} ks)))
   v93_l796)))


(def v96_l819 (sort (keys pj/layer-option-docs)))


(deftest
 t97_l821
 (is
  ((fn
    [ks]
    (and
     (pos? (count ks))
     (some #{:group :color :size :alpha :position} ks)))
   v96_l819)))


(def
 v99_l834
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:tooltip true, :brush true})))


(deftest
 t100_l838
 (is
  ((fn
    [pose]
    (let
     [s (str (pj/plot pose))]
     (and (re-find #"data-tooltip" s) (re-find #"nsk-brush-sel" s))))
   v99_l834)))
