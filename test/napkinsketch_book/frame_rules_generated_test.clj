(ns
 napkinsketch-book.frame-rules-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l29 (def iris (rdatasets/datasets-iris)))


(def
 v5_l35
 (defn
  strip-data
  [fr]
  (cond->
   (dissoc fr :data)
   (:layers fr)
   (update
    :layers
    (partial mapv (fn* [p1__142948#] (dissoc p1__142948# :data))))
   (:frames fr)
   (update :frames (partial mapv strip-data)))))


(def
 v6_l40
 (defn
  fr-summary
  "Print frame structure without :data (for readability)."
  [fr]
  (kind/pprint (strip-data fr))))


(def v8_l82 (-> iris (sk/frame :sepal-length :sepal-width)))


(deftest
 t9_l85
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v8_l82)))


(def v10_l87 (-> iris (sk/frame :sepal-length :sepal-width) fr-summary))


(deftest
 t11_l91
 (is
  ((fn
    [fr]
    (and
     (= {:x :sepal-length, :y :sepal-width} (:mapping fr))
     (= [] (:layers fr))
     (not (contains? fr :frames))))
   v10_l87)))


(def v13_l101 (-> iris (sk/frame {:color :species}) fr-summary))


(deftest
 t14_l105
 (is
  ((fn
    [fr]
    (and
     (= {:color :species} (:mapping fr))
     (not (contains? fr :frames))))
   v13_l101)))


(def v16_l117 (-> iris sk/frame (sk/frame :sepal-length :sepal-width)))


(deftest
 t17_l121
 (is
  ((fn
    [fr]
    (and
     (= {:x :sepal-length, :y :sepal-width} (:mapping fr))
     (not (contains? fr :frames))))
   v16_l117)))


(def
 v19_l128
 (->
  iris
  (sk/frame {:color :species})
  (sk/frame :sepal-length :sepal-width)))


(deftest
 t20_l132
 (is
  ((fn
    [fr]
    (=
     {:x :sepal-length, :y :sepal-width, :color :species}
     (:mapping fr)))
   v19_l128)))


(def
 v22_l140
 (->
  iris
  sk/frame
  (sk/frame {:color :species})
  (sk/frame :sepal-length :sepal-width)))


(deftest
 t23_l145
 (is
  ((fn
    [fr]
    (=
     fr
     (sk/frame iris :sepal-length :sepal-width {:color :species})))
   v22_l140)))


(def
 v25_l158
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)))


(deftest
 t26_l162
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:frames fr)))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:frames fr))))
     (=
      {:x :petal-length, :y :petal-width}
      (:mapping (second (:frames fr))))))
   v25_l158)))


(def
 v28_l174
 (->
  iris
  (sk/frame :sepal-length :sepal-width {:color :species})
  (sk/frame :petal-length :petal-width)
  fr-summary))


(deftest
 t29_l179
 (is
  ((fn
    [fr]
    (and
     (= {:color :species} (:mapping fr))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:frames fr))))
     (=
      {:x :petal-length, :y :petal-width}
      (:mapping (second (:frames fr))))))
   v28_l174)))


(def
 v31_l191
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/options {:title "Iris"})
  (sk/frame :petal-length :petal-width)))


(deftest
 t32_l196
 (is
  ((fn
    [fr]
    (and
     (= "Iris" (get-in fr [:opts :title]))
     (not (contains? (first (:frames fr)) :opts))))
   v31_l191)))


(def
 v34_l209
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame {:color :species})))


(deftest
 t35_l213
 (is
  ((fn
    [fr]
    (and
     (= 1 (count (:frames fr)))
     (= {:color :species} (:mapping fr))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:frames fr))))))
   v34_l209)))


(def
 v37_l226
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame {:color :species})
  (sk/frame :petal-length :petal-width)))


(deftest
 t38_l231
 (is
  ((fn
    [fr]
    (=
     fr
     (->
      iris
      (sk/frame :sepal-length :sepal-width {:color :species})
      (sk/frame :petal-length :petal-width))))
   v37_l226)))


(def
 v40_l248
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/frame :petal-length :petal-width)))


(deftest
 t41_l253
 (is
  ((fn
    [fr]
    (and
     (= 1 (count (:layers fr)))
     (= :point (:layer-type (first (:layers fr))))
     (= 2 (count (:frames fr)))
     (= [] (:layers (first (:frames fr))))
     (= [] (:layers (second (:frames fr))))))
   v40_l248)))


(def
 v43_l266
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)))


(deftest
 t44_l271
 (is
  ((fn
    [fr]
    (and
     (or (not (contains? fr :layers)) (= [] (:layers fr)))
     (= 1 (count (:layers (first (:frames fr)))))
     (= [] (:layers (second (:frames fr))))))
   v43_l266)))


(def
 v46_l287
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  (sk/frame :sepal-length :petal-length)))


(deftest
 t47_l292
 (is
  ((fn
    [fr]
    (and
     (= 3 (count (:frames fr)))
     (=
      [{:x :sepal-length, :y :sepal-width}
       {:x :petal-length, :y :petal-width}
       {:x :sepal-length, :y :petal-length}]
      (mapv :mapping (:frames fr)))))
   v46_l287)))


(def
 v49_l303
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  (sk/frame {:color :species})
  fr-summary))


(deftest
 t50_l309
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:frames fr)))
     (= {:color :species} (:mapping fr))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:frames fr))))))
   v49_l303)))


(def
 v52_l322
 (let
  [fr (-> iris (sk/frame :sepal-length :sepal-width))]
  (= fr (sk/frame fr))))


(deftest t53_l325 (is (true? v52_l322)))


(def
 v54_l327
 (let
  [fr
   (->
    iris
    (sk/frame :sepal-length :sepal-width)
    (sk/frame :petal-length :petal-width))]
  (= fr (sk/frame fr))))


(deftest t55_l332 (is (true? v54_l327)))


(def
 v57_l341
 (sk/arrange
  [(-> iris (sk/frame :sepal-length :sepal-width) sk/lay-point)
   (-> iris (sk/frame :petal-length :petal-width) sk/lay-point)]))


(deftest
 t58_l345
 (is
  ((fn
    [fr]
    (and
     (contains? fr :frames)
     (= :vertical (get-in fr [:layout :direction]))
     (= 1 (count (:frames fr)))
     (= 2 (count (:frames (first (:frames fr)))))))
   v57_l341)))


(def
 v60_l357
 (sk/arrange
  [(sk/frame iris :sepal-length :sepal-width)
   (sk/frame iris :petal-length :petal-width)]
  {:title "Arranged", :share-scales #{:y}}))


(deftest
 t61_l363
 (is
  ((fn
    [fr]
    (and
     (= "Arranged" (get-in fr [:opts :title]))
     (= #{:y} (:share-scales fr))))
   v60_l357)))
