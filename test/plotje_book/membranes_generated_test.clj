(ns
 plotje-book.membranes-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [scicloj.plotje.impl.membrane :as plotje-mem]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [membrane.ui :as ui]
  [clojure.test :refer [deftest is]]))


(def
 v3_l37
 (def
  iris-pose
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/options {:title "Iris"}))))


(def v4_l43 iris-pose)


(deftest
 t5_l45
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v4_l43)))


(def v7_l55 (def iris-membrane (pj/membrane iris-pose)))


(def v8_l57 (pj/membrane? iris-membrane))


(deftest t9_l59 (is (true? v8_l57)))


(def v11_l65 (def iris-plan (pj/plan iris-pose)))


(def v12_l67 (pj/membrane? (pj/plan->membrane iris-plan)))


(deftest t13_l69 (is (true? v12_l67)))


(def
 v15_l78
 (kind/pprint
  {:width (ui/width iris-membrane),
   :height (ui/height iris-membrane),
   :origin (ui/origin iris-membrane),
   :title (:plotje/title iris-membrane),
   :n-drawables (count (ui/children iris-membrane))}))


(deftest
 t16_l85
 (is
  ((fn
    [info]
    (and
     (= 600 (:width info))
     (= 400 (:height info))
     (= [0 0] (:origin info))
     (= "Iris" (:title info))
     (= 9 (:n-drawables info))))
   v15_l78)))


(def v18_l105 (sort (filter keyword? (keys iris-membrane))))


(deftest
 t19_l107
 (is
  ((fn [ks] (= [:drawables :height :width :plotje/title] ks))
   v18_l105)))


(def
 v21_l142
 (:plotje/title
  (pj/membrane
   (->
    (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width)))))


(deftest t22_l145 (is (nil? v21_l142)))


(def
 v24_l160
 (def
  two-up
  (ui/horizontal-layout
   (pj/membrane
    (->
     (rdatasets/datasets-iris)
     (pj/lay-point :sepal-length :sepal-width {:color :species})
     (pj/options {:title "Sepal length vs sepal width"})))
   (pj/membrane
    (->
     (rdatasets/datasets-iris)
     (pj/lay-point :sepal-length :petal-length {:color :species})
     (pj/options {:title "Sepal length vs petal length"}))))))


(def
 v26_l175
 (kind/pprint {:width (ui/width two-up), :height (ui/height two-up)}))


(deftest
 t27_l179
 (is
  ((fn [info] (and (= 1201 (:width info)) (= 400 (:height info))))
   v26_l175)))


(def
 v29_l191
 (def
  two-up-png
  ((requiring-resolve 'membrane.java2d/draw-to-image)
   two-up
   [(ui/width two-up) (ui/height two-up)])))


(def v30_l196 (instance? java.awt.image.BufferedImage two-up-png))


(deftest t31_l198 (is (true? v30_l196)))


(def v33_l202 two-up-png)


(def v35_l218 (first (pj/membrane->plot iris-membrane :svg {})))


(deftest t36_l220 (is ((fn [v] (= :svg v)) v35_l218)))


(def v37_l222 (class (pj/membrane->plot iris-membrane :bufimg {})))


(deftest
 t38_l224
 (is
  ((fn [c] (= "java.awt.image.BufferedImage" (.getName c))) v37_l222)))


(def v40_l240 (plotje-mem/valid? iris-membrane))


(deftest t41_l242 (is (true? v40_l240)))


(def v43_l246 (some? (plotje-mem/explain {:not :a-membrane})))


(deftest t44_l248 (is (true? v43_l246)))
