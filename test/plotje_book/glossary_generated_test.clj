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


(def v17_l175 (-> my-pose pj/draft kind/pprint))


(deftest
 t18_l177
 (is
  ((fn
    [d]
    (and
     (pj/leaf-draft? d)
     (= 1 (count (:layers d)))
     (= :point (:mark (first (:layers d))))))
   v17_l175)))


(def v20_l191 (-> my-pose pj/draft :layers first kind/pprint))


(deftest
 t21_l193
 (is
  ((fn
    [d]
    (and
     (some? (:data d))
     (= :sepal-length (:x d))
     (= :sepal-width (:y d))
     (= :species (:color d))
     (= :point (:mark d))))
   v20_l191)))


(def
 v23_l252
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point
   :sepal-length
   :sepal-width
   {:color :species, :size :petal-length, :alpha 0.7})))


(deftest
 t24_l256
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= #{0.7} (:alphas s)))))
   v23_l252)))


(def
 v26_l271
 (->
  (rdatasets/datasets-iris)
  (pj/lay-line :sepal-length :sepal-width {:group :species})))


(deftest
 t27_l274
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
   v26_l271)))


(def
 v29_l295
 (-> {:x [1 2 3], :y [4 5 6]} (pj/lay-point :x :y {:nudge-x 0.5})))


(deftest
 t30_l298
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
   v29_l295)))


(def v32_l319 (merge (pj/layer-type-lookup :point) {:jitter true}))


(deftest t33_l321 (is ((fn [m] (true? (:jitter m))) v32_l319)))


(def
 v35_l333
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point))


(deftest
 t36_l337
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v35_l333)))


(def v38_l351 (def my-plan (pj/plan my-pose)))


(def v39_l353 (kind/pprint my-plan))


(deftest
 t40_l355
 (is
  ((fn
    [plan]
    (and
     (vector? (:panels plan))
     (= 1 (count (:panels plan)))
     (= 600 (:width plan))
     (= 400 (:height plan))
     (some? (:legend plan))))
   v39_l353)))


(def v42_l370 (kind/pprint (first (:panels my-plan))))


(deftest
 t43_l372
 (is
  ((fn
    [p]
    (and
     (= :cartesian (:coord p))
     (= [4.12 8.08] (:x-domain p))
     (= 1 (count (:layers p)))))
   v42_l370)))


(def v45_l384 (kind/pprint (get-in my-plan [:panels 0 :layers 0])))


(deftest
 t46_l386
 (is
  ((fn
    [layer]
    (and
     (= :point (:mark layer))
     (= 3 (count (:groups layer)))
     (every? :xs (:groups layer))))
   v45_l384)))


(def
 v48_l458
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t49_l462
 (is
  ((fn
    [m]
    (and
     (= [4.12 8.08] (:x-domain m))
     (= 2 (count (:y-domain m)))
     (number? (first (:y-domain m)))))
   v48_l458)))


(def v51_l477 (-> my-plan :panels first :x-ticks))


(deftest
 t52_l479
 (is
  ((fn
    [m]
    (and
     (vector? (:values m))
     (vector? (:labels m))
     (= (count (:values m)) (count (:labels m)))
     (false? (:categorical? m))))
   v51_l477)))


(def
 v54_l518
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t55_l522
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
   v54_l518)))


(def
 v57_l545
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/facet :species)))


(deftest
 t58_l549
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
   v57_l545)))


(def
 v60_l585
 (def
  annotated
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width)
   (pj/lay-rule-h {:y-intercept 3.0}))))


(def v61_l590 annotated)


(def v62_l592 (kind/pprint (nth (:layers annotated) 1)))


(deftest
 t63_l594
 (is
  ((fn
    [layer]
    (and
     (= :rule-h (:layer-type layer))
     (= 3.0 (get-in layer [:mapping :y-intercept]))))
   v62_l592)))


(def v65_l606 (kind/pprint (:legend my-plan)))


(deftest
 t66_l608
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
   v65_l606)))


(def
 v68_l632
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})))


(deftest
 t69_l636
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v68_l632)))


(def v71_l650 (def my-membrane (pj/plan->membrane my-plan)))


(def v73_l656 (kind/pprint my-membrane))


(deftest
 t74_l658
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
   v73_l656)))


(def v76_l681 (def my-plot (pj/plan->plot my-plan :svg {})))


(def v78_l688 (kind/hiccup my-plot))


(deftest
 t79_l690
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= :svg (first my-plot))
      (= 150 (:points s))
      (= 600.0 (double (:width s))))))
   v78_l688)))


(def
 v81_l706
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette :set2})))


(deftest
 t82_l710
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v81_l706)))


(def v84_l716 (count (c2d/find-palette #".*")))


(deftest t85_l718 (is ((fn [n] (<= 5000 n)) v84_l716)))


(def
 v87_l730
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (pj/lay-point :x :y {:color :c})
  (pj/options {:color-scale :inferno})))


(deftest
 t88_l734
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
   v87_l730)))


(def
 v90_l760
 (select-keys
  (pj/config)
  [:width :height :theme :palette :color-scale]))


(deftest
 t91_l762
 (is
  ((fn
    [m]
    (and (number? (:width m)) (number? (:height m)) (map? (:theme m))))
   v90_l760)))


(def v93_l779 (sort (keys pj/plot-option-docs)))


(deftest
 t94_l781
 (is
  ((fn
    [ks]
    (and
     (= 14 (count ks))
     (some #{:caption :title :y-label :x-label :subtitle} ks)))
   v93_l779)))


(def v96_l802 (sort (keys pj/layer-option-docs)))


(deftest
 t97_l804
 (is
  ((fn
    [ks]
    (and
     (pos? (count ks))
     (some #{:group :color :size :alpha :position} ks)))
   v96_l802)))


(def
 v99_l817
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:tooltip true, :brush true})))


(deftest
 t100_l821
 (is
  ((fn
    [pose]
    (let
     [s (str (pj/plot pose))]
     (and (re-find #"data-tooltip" s) (re-find #"nsk-brush-sel" s))))
   v99_l817)))
