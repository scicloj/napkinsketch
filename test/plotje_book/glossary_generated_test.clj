(ns
 plotje-book.glossary-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [clojure2d.color :as c2d]
  [clojure.test :refer [deftest is]]))


(def
 v3_l28
 (def
  my-pose
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/options {:title "Iris"}))))


(def v4_l33 my-pose)


(deftest
 t5_l35
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v4_l33)))


(def v7_l41 (kind/pprint my-pose))


(deftest
 t8_l43
 (is
  ((fn
    [pose]
    (and
     (some? (:data pose))
     (= :sepal-length (get-in pose [:mapping :x]))
     (= :sepal-width (get-in pose [:mapping :y]))
     (= :species (get-in pose [:layers 0 :mapping :color]))
     (= "Iris" (get-in pose [:opts :title]))))
   v7_l41)))


(def v10_l104 (-> my-pose :layers first :layer-type))


(deftest t11_l106 (is ((fn [k] (= :point k)) v10_l104)))


(def
 v13_l140
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v14_l144
 (->
  tips
  (pj/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t15_l147
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
   v14_l144)))


(def v17_l172 (-> my-pose pj/draft kind/pprint))


(deftest
 t18_l174
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v17_l172)))


(def v20_l188 (-> my-pose pj/draft first kind/pprint))


(deftest
 t21_l190
 (is
  ((fn
    [d]
    (and
     (some? (:data d))
     (= :sepal-length (:x d))
     (= :sepal-width (:y d))
     (= :species (:color d))
     (= :point (:mark d))))
   v20_l188)))


(def
 v23_l249
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point
   :sepal-length
   :sepal-width
   {:color :species, :size :petal-length, :alpha 0.7})))


(deftest
 t24_l253
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= #{0.7} (:alphas s)))))
   v23_l249)))


(def
 v26_l268
 (->
  (rdatasets/datasets-iris)
  (pj/lay-line :sepal-length :sepal-width {:group :species})))


(deftest
 t27_l271
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
   v26_l268)))


(def
 v29_l292
 (-> {:x [1 2 3], :y [4 5 6]} (pj/lay-point :x :y {:nudge-x 0.5})))


(deftest
 t30_l295
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
   v29_l292)))


(def v32_l316 (merge (pj/layer-type-lookup :point) {:jitter true}))


(deftest t33_l318 (is ((fn [m] (true? (:jitter m))) v32_l316)))


(def
 v35_l330
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point))


(deftest
 t36_l334
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v35_l330)))


(def v38_l348 (def my-plan (pj/plan my-pose)))


(def v39_l350 (kind/pprint my-plan))


(deftest
 t40_l352
 (is
  ((fn
    [plan]
    (and
     (vector? (:panels plan))
     (= 1 (count (:panels plan)))
     (= 600 (:width plan))
     (= 400 (:height plan))
     (some? (:legend plan))))
   v39_l350)))


(def v42_l367 (kind/pprint (first (:panels my-plan))))


(deftest
 t43_l369
 (is
  ((fn
    [p]
    (and
     (= :cartesian (:coord p))
     (= [4.12 8.08] (:x-domain p))
     (= 1 (count (:layers p)))))
   v42_l367)))


(def v45_l381 (kind/pprint (get-in my-plan [:panels 0 :layers 0])))


(deftest
 t46_l383
 (is
  ((fn
    [layer]
    (and
     (= :point (:mark layer))
     (= 3 (count (:groups layer)))
     (every? :xs (:groups layer))))
   v45_l381)))


(def
 v48_l455
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t49_l459
 (is
  ((fn
    [m]
    (and
     (= [4.12 8.08] (:x-domain m))
     (= 2 (count (:y-domain m)))
     (number? (first (:y-domain m)))))
   v48_l455)))


(def v51_l474 (-> my-plan :panels first :x-ticks))


(deftest
 t52_l476
 (is
  ((fn
    [m]
    (and
     (vector? (:values m))
     (vector? (:labels m))
     (= (count (:values m)) (count (:labels m)))
     (false? (:categorical? m))))
   v51_l474)))


(def
 v54_l515
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t55_l519
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
   v54_l515)))


(def
 v57_l542
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/facet :species)))


(deftest
 t58_l546
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
   v57_l542)))


(def
 v60_l581
 (def
  annotated
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width)
   (pj/lay-rule-h {:y-intercept 3.0}))))


(def v61_l586 annotated)


(def v62_l588 (kind/pprint (nth (:layers annotated) 1)))


(deftest
 t63_l590
 (is
  ((fn
    [layer]
    (and
     (= :rule-h (:layer-type layer))
     (= 3.0 (get-in layer [:mapping :y-intercept]))))
   v62_l588)))


(def v65_l602 (kind/pprint (:legend my-plan)))


(deftest
 t66_l604
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
   v65_l602)))


(def
 v68_l628
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})))


(deftest
 t69_l632
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v68_l628)))


(def v71_l646 (def my-membrane (pj/plan->membrane my-plan)))


(def v73_l652 (kind/pprint my-membrane))


(deftest
 t74_l654
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
      texts
      (mapv walk-text m)]
     (and
      (vector? m)
      (= 9 (count m))
      (=
       ["Iris" "sepal width" "sepal length" "species"]
       (vec (take 4 texts))))))
   v73_l652)))


(def v76_l677 (def my-plot (pj/plan->plot my-plan :svg {})))


(def v78_l684 (kind/hiccup my-plot))


(deftest
 t79_l686
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= :svg (first my-plot))
      (= 150 (:points s))
      (= 600.0 (double (:width s))))))
   v78_l684)))


(def
 v81_l702
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette :set2})))


(deftest
 t82_l706
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v81_l702)))


(def v84_l712 (count (c2d/find-palette #".*")))


(deftest t85_l714 (is ((fn [n] (<= 5000 n)) v84_l712)))


(def
 v87_l726
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (pj/lay-point :x :y {:color :c})
  (pj/options {:color-scale :inferno})))


(deftest
 t88_l730
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
   v87_l726)))


(def
 v90_l756
 (select-keys
  (pj/config)
  [:width :height :theme :palette :color-scale]))


(deftest
 t91_l758
 (is
  ((fn
    [m]
    (and (number? (:width m)) (number? (:height m)) (map? (:theme m))))
   v90_l756)))


(def v93_l775 (sort (keys pj/plot-option-docs)))


(deftest
 t94_l777
 (is
  ((fn
    [ks]
    (and
     (= 14 (count ks))
     (some #{:caption :title :y-label :x-label :subtitle} ks)))
   v93_l775)))


(def v96_l798 (sort (keys pj/layer-option-docs)))


(deftest
 t97_l800
 (is
  ((fn
    [ks]
    (and
     (pos? (count ks))
     (some #{:group :color :size :alpha :position} ks)))
   v96_l798)))


(def
 v99_l813
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:tooltip true, :brush true})))


(deftest
 t100_l817
 (is
  ((fn
    [pose]
    (let
     [s (str (pj/plot pose))]
     (and (re-find #"data-tooltip" s) (re-find #"nsk-brush-sel" s))))
   v99_l813)))
