(ns
 plotje-book.gallery-generated-test
 (:require
  [scicloj.plotje.api :as pj]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [tablecloth.api :as tc]
  [tech.v3.datatype.functional :as dfn]
  [fastmath.stats :as fstats]
  [clojure.test :refer [deftest is]]))


(def
 v3_l28
 (->
  (rdatasets/ggplot2-mpg)
  (pj/frame :displ :hwy {:color :class})
  pj/lay-point
  pj/lay-smooth
  (pj/options
   {:title "Fuel Efficiency by Engine Size",
    :x-label "Engine Displacement (L)",
    :y-label "Highway MPG"})))


(deftest
 t4_l36
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v3_l28)))


(def
 v6_l46
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 500)
  (pj/frame :carat :price {:color :cut, :size :depth})
  pj/lay-point
  (pj/options
   {:title "Diamond Price vs Carat (bubble)",
    :x-label "Carat",
    :y-label "Price (USD)"})))


(deftest
 t7_l54
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 500 (:points s)))) v6_l46)))


(def
 v9_l62
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day :total-bill {:color :sex})
  pj/lay-point
  (pj/options
   {:title "Total Bill by Day",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t10_l69
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 244 (:points s)))) v9_l62)))


(def
 v12_l79
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/frame :price)
  pj/lay-histogram
  (pj/options
   {:title "Distribution of Diamond Prices",
    :x-label "Price (USD)",
    :y-label "Count"})))


(deftest
 t13_l86
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s)))) v12_l79)))


(def
 v15_l91
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/frame :price {:color :cut})
  pj/lay-histogram
  (pj/options
   {:title "Diamond Prices by Cut",
    :x-label "Price (USD)",
    :y-label "Count"})))


(deftest
 t16_l98
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (< 1 (:polygons s)))) v15_l91)))


(def
 v18_l106
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/frame :carat {:color :cut})
  pj/lay-density
  (pj/options
   {:title "Carat Distribution by Cut",
    :x-label "Carat",
    :y-label "Density"})))


(deftest
 t19_l113
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v18_l106)))


(def
 v21_l118
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 500)
  (pj/frame :carat)
  pj/lay-density
  pj/lay-rug
  (pj/options
   {:title "Carat Distribution with Rug",
    :x-label "Carat",
    :y-label "Density"})))


(deftest
 t22_l127
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:lines s)))))
   v21_l118)))


(def
 v24_l136
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day :total-bill {:color :day})
  pj/lay-boxplot
  (pj/options
   {:title "Total Bill by Day",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t25_l143
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v24_l136)))


(def
 v27_l148
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day :total-bill)
  pj/lay-boxplot
  pj/lay-point
  (pj/options
   {:title "Total Bill by Day (box + points)",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t28_l156
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:points s)))))
   v27_l148)))


(def
 v30_l165
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day :total-bill {:color :day})
  pj/lay-violin
  (pj/options
   {:title "Total Bill by Day (violin)",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t31_l172
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v30_l165)))


(def
 v33_l177
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day :total-bill {:color :day})
  pj/lay-violin
  pj/lay-boxplot
  (pj/options
   {:title "Total Bill Distribution by Day",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t34_l185
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v33_l177)))


(def
 v36_l193
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/frame :cut :price)
  pj/lay-ridgeline
  (pj/options
   {:title "Price Distribution by Cut (ridgeline)",
    :x-label "Cut",
    :y-label "Price (USD)"})))


(deftest
 t37_l200
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v36_l193)))


(def
 v39_l210
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/frame :cut)
  pj/lay-bar
  (pj/options
   {:title "Diamond Count by Cut", :x-label "Cut", :y-label "Count"})))


(deftest
 t40_l217
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 5 (:polygons s)))) v39_l210)))


(def
 v42_l225
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/frame :cut)
  pj/lay-bar
  (pj/coord :flip)
  (pj/options
   {:title "Diamond Count by Cut (horizontal)",
    :x-label "Cut",
    :y-label "Count"})))


(deftest
 t43_l233
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 5 (:polygons s)))) v42_l225)))


(def
 v45_l241
 (def
  mpg-mfr-counts
  (->
   (rdatasets/ggplot2-mpg)
   (tc/group-by [:manufacturer])
   (tc/aggregate {:count tc/row-count})
   (tc/order-by [:count] :desc)
   (tc/select-rows (range 8)))))


(def
 v46_l248
 (->
  mpg-mfr-counts
  (pj/frame :manufacturer :count)
  pj/lay-lollipop
  (pj/options
   {:title "Top Manufacturers by Model Count",
    :x-label "Manufacturer",
    :y-label "Count"})))


(deftest
 t47_l255
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v46_l248)))


(def
 v49_l261
 (->
  mpg-mfr-counts
  (pj/frame :manufacturer :count)
  pj/lay-lollipop
  (pj/coord :flip)
  (pj/options
   {:title "Top Manufacturers (horizontal lollipop)",
    :x-label "Manufacturer",
    :y-label "Count"})))


(deftest
 t50_l269
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v49_l261)))


(def
 v52_l280
 (->
  (rdatasets/ggplot2-economics)
  (pj/frame :date :unemploy)
  pj/lay-line
  (pj/options
   {:title "US Unemployment Over Time",
    :x-label "Date",
    :y-label "Unemployed (thousands)"})))


(deftest
 t53_l287
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:lines s)))) v52_l280)))


(def
 v55_l292
 (->
  (rdatasets/gapminder-gapminder)
  (tc/select-rows
   (fn*
    [p1__84692#]
    (#{"Australia" "Brazil" "Japan" "Nigeria" "Germany"}
     (:country p1__84692#))))
  (pj/frame :year :life-exp {:color :country})
  pj/lay-line
  pj/lay-point
  (pj/options
   {:title "Life Expectancy Over Time",
    :x-label "Year",
    :y-label "Life Expectancy"})))


(deftest
 t56_l302
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v55_l292)))


(def
 v58_l311
 (->
  (rdatasets/ggplot2-economics)
  (pj/frame :date :unemploy)
  pj/lay-area
  (pj/options
   {:title "US Unemployment Over Time (area)",
    :x-label "Date",
    :y-label "Unemployed (thousands)"})))


(deftest
 t59_l318
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 1 (:polygons s)))) v58_l311)))


(def
 v61_l328
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 2000)
  (pj/frame :carat :price)
  pj/lay-density-2d
  (pj/options
   {:title "Diamond Carat vs Price (density)",
    :x-label "Carat",
    :y-label "Price (USD)"})))


(deftest
 t62_l336
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:visible-tiles s))))
   v61_l328)))


(def
 v64_l344
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :total-bill :tip {:color :sex})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/options
   {:title "Tip vs Total Bill (with regression)",
    :x-label "Total Bill (USD)",
    :y-label "Tip (USD)"})))


(deftest
 t65_l352
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v64_l344)))


(def
 v67_l361
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width {:color :species})
  pj/lay-point
  pj/lay-contour
  (pj/options
   {:title "Iris Sepal Dimensions (contour)",
    :x-label "Sepal Length",
    :y-label "Sepal Width"})))


(deftest
 t68_l369
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v67_l361)))


(def
 v70_l380
 (->
  (rdatasets/ggplot2-mpg)
  (pj/frame :displ :hwy {:color :class})
  pj/lay-point
  (pj/facet-grid :drv nil)
  (pj/options
   {:title "Highway MPG by Engine Size, faceted by Drive",
    :x-label "Displacement",
    :y-label "Highway MPG"})))


(deftest
 t71_l388
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:panels s)))) v70_l380)))


(def
 v73_l393
 (->
  (rdatasets/ggplot2-mpg)
  (pj/frame :hwy)
  pj/lay-histogram
  (pj/facet-grid :drv nil)
  (pj/options
   {:title "Highway MPG by Drive Type",
    :x-label "Highway MPG",
    :y-label "Count"})))


(deftest
 t74_l401
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:panels s)))) v73_l393)))


(def
 v76_l412
 (->
  (rdatasets/datasets-iris)
  (pj/frame {:color :species})
  (pj/frame
   (pj/cross
    [:sepal-length :sepal-width :petal-length :petal-width]
    [:sepal-length :sepal-width :petal-length :petal-width]))
  (pj/options {:title "Iris SPLOM"})))


(deftest
 t77_l418
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 16 (:panels s))
      (= (* 12 150) (:points s))
      (pos? (:polygons s)))))
   v76_l412)))


(def
 v79_l430
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day {:color :sex})
  (pj/lay-bar {:position :stack})
  (pj/options
   {:title "Tips by Day and Sex (stacked bar)",
    :x-label "Day",
    :y-label "Count"})))


(deftest t80_l437 (is ((fn [v] (pj/frame? v)) v79_l430)))


(def
 v82_l441
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day {:color :sex})
  (pj/lay-bar {:position :fill})
  (pj/options
   {:title "Proportion by Day and Sex",
    :x-label "Day",
    :y-label "Proportion"})))


(deftest t83_l448 (is ((fn [v] (pj/frame? v)) v82_l441)))


(def
 v85_l455
 (->
  (rdatasets/gapminder-gapminder)
  (tc/group-by [:year :continent])
  (tc/aggregate {:pop (fn [ds] (reduce + (ds :pop)))})
  (tc/order-by [:year :continent])
  (pj/frame :year :pop {:color :continent})
  (pj/lay-area {:position :stack})
  (pj/options
   {:title "World Population by Continent",
    :x-label "Year",
    :y-label "Population"})))


(deftest
 t86_l465
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 5 (:polygons s)))) v85_l455)))


(def
 v88_l475
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/frame :cut)
  pj/lay-bar
  (pj/coord :polar)
  (pj/options {:title "Diamond Cut (rose chart)"})))


(deftest
 t89_l481
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 5 (:polygons s)))) v88_l475)))


(def
 v91_l490
 (->
  (rdatasets/datasets-mtcars)
  (pj/frame :wt :mpg)
  pj/lay-point
  (pj/lay-text {:text :rownames})
  (pj/options
   {:title "Motor Trend Cars",
    :x-label "Weight (1000 lbs)",
    :y-label "Miles per Gallon"})))


(deftest
 t92_l498
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (pos? (count (:texts s))))))
   v91_l490)))


(def
 v94_l505
 (->
  (rdatasets/datasets-mtcars)
  (pj/frame :wt :mpg)
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model, :confidence-band true})
  (pj/options
   {:title "Weight vs MPG with Linear Fit",
    :x-label "Weight (1000 lbs)",
    :y-label "Miles per Gallon"})))


(deftest
 t95_l513
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v94_l505)))


(def
 v97_l520
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-bar :day {:color :sex})
  (pj/options {:title "Tips by Day and Gender"})))


(deftest
 t98_l524
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v97_l520)))


(def
 v100_l529
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/lay-point :carat :price {:alpha 0.1})
  (pj/scale :y :log)
  (pj/options
   {:title "Diamond Price by Carat (Log Scale)",
    :x-label "Carat",
    :y-label "Price ($, log scale)"})))


(deftest
 t101_l536
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v100_l529)))


(def
 v103_l541
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-summary :day :total-bill {:color :sex})
  (pj/options {:title "Average Bill with Standard Error"})))


(deftest
 t104_l545
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:points s)))) v103_l541)))


(def
 v106_l551
 (->
  (rdatasets/gapminder-gapminder)
  (tc/select-rows (fn* [p1__84693#] (= 2007 (:year p1__84693#))))
  (pj/lay-point :gdp-percap :life-exp {:color :continent, :size :pop})
  (pj/scale :x :log)
  (pj/options
   {:title "Gapminder 2007: Life Expectancy vs GDP",
    :x-label "GDP per Capita (log)",
    :y-label "Life Expectancy"})))


(deftest
 t107_l559
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v106_l551)))


(def
 v109_l564
 (->
  (rdatasets/gapminder-gapminder)
  (tc/select-rows
   (fn*
    [p1__84694#]
    (#{"Brazil" "United States" "Japan" "China" "India"}
     (:country p1__84694#))))
  (pj/lay-line :year :life-exp {:color :country})
  (pj/options
   {:title "Life Expectancy Over Time",
    :x-label "Year",
    :y-label "Life Expectancy"})))


(deftest
 t110_l571
 (is ((fn [v] (pos? (:lines (pj/svg-summary v)))) v109_l564)))


(def
 v112_l576
 (->
  (rdatasets/ggplot2-economics)
  (pj/lay-step :date :unemploy)
  (pj/options
   {:title "US Unemployment (Step)",
    :x-label "Date",
    :y-label "Unemployed (thousands)"})))


(deftest
 t113_l582
 (is ((fn [v] (pos? (:lines (pj/svg-summary v)))) v112_l576)))


(def
 v115_l587
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length)
  pj/lay-density
  pj/lay-rug
  (pj/options {:title "Iris Sepal Length: Density + Rug"})))


(deftest
 t116_l593
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v115_l587)))


(def
 v118_l598
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :total-bill :tip {:color :smoker})
  pj/lay-point
  (pj/lay-smooth {:confidence-band true})
  (pj/options
   {:title "Tips: Bill vs Tip by Smoking Status",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t119_l606
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v118_l598)))


(def
 v121_l613
 (->
  (rdatasets/ggplot2-mpg)
  (pj/lay-histogram :hwy {:color :drv})
  (pj/facet :drv)
  (pj/options {:title "Highway MPG by Drive Type"})))


(deftest
 t122_l618
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:panels s)))) v121_l613)))


(def
 v124_l624
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})
  (pj/lay-rule-v {:x-intercept 6.0})
  (pj/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1})
  (pj/options {:title "Iris with Reference Lines and Band"})))


(deftest
 t125_l631
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v124_l624)))


(def
 v127_l638
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day :total-bill)
  pj/lay-violin
  pj/lay-boxplot
  (pj/options {:title "Tips Distribution by Day"})))


(deftest
 t128_l644
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v127_l638)))


(def
 v130_l650
 (->
  (rdatasets/datasets-mtcars)
  (pj/lay-lollipop :rownames :mpg)
  (pj/coord :flip)
  (pj/options {:title "Cars Ranked by MPG"})))


(deftest
 t131_l655
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v130_l650)))


(def
 v133_l660
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-bar :day {:position :fill, :color :sex})
  (pj/options {:title "Gender Proportion by Day (100% stacked)"})))


(deftest
 t134_l664
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v133_l660)))


(def
 v136_l669
 (->
  (rdatasets/datasets-iris)
  (pj/frame {:color :species})
  (pj/frame
   (pj/cross
    [:sepal-length :sepal-width :petal-length :petal-width]
    [:sepal-length :sepal-width :petal-length :petal-width]))))


(deftest
 t137_l674
 (is ((fn [v] (= 16 (:panels (pj/svg-summary v)))) v136_l669)))


(def
 v139_l679
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length)
  (pj/lay-histogram {:normalize :density})
  pj/lay-density
  (pj/options {:title "Sepal Length: Histogram + Density Curve"})))


(deftest
 t140_l685
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v139_l679)))


(def
 v142_l717
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/coord :fixed)
  (pj/options {:title "Iris Sepals (Equal Aspect Ratio)"})))


(deftest
 t143_l722
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v142_l717)))


(def
 v145_l727
 (->
  (tc/dataset
   {:category ["Mon" "Tue" "Wed" "Thu" "Fri" "Sat" "Sun"],
    :value [120 200 150 80 70 110 130]})
  (pj/lay-value-bar :category :value)
  (pj/options {:title "Weekly Sales"})))


(deftest
 t146_l732
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v145_l727)))


(def
 v148_l737
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density :sepal-length {:color :species})
  (pj/options
   {:title "Sepal Length by Species", :x-label "Sepal Length (cm)"})))


(deftest
 t149_l742
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v148_l737)))


(def
 v151_l747
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:confidence-band true})
  (pj/options {:title "Iris: Scatter + LOESS by Species"})))


(deftest
 t152_l753
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v151_l747)))


(def
 v154_l760
 (->
  (rdatasets/datasets-iris)
  (pj/lay-summary :species :sepal-length)
  (pj/options {:title "Mean Sepal Length +/- SE by Species"})))


(deftest
 t155_l764
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v154_l760)))


(def
 v157_l769
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  pj/lay-point
  pj/lay-rug
  (pj/options {:title "Iris: Scatter with Rug Marks"})))


(deftest
 t158_l775
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v157_l769)))


(def
 v160_l812
 (->
  (rdatasets/ggplot2-economics)
  (as-> econ (tc/select-rows econ (range 0 (tc/row-count econ) 12)))
  (pj/frame :unemploy :pce)
  pj/lay-line
  pj/lay-point
  (pj/options
   {:title "US Economy: Unemployment vs Personal Consumption",
    :x-label "Unemployed (thousands)",
    :y-label "Personal Consumption Expenditures"})))


(deftest
 t161_l821
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v160_l812)))


(def
 v163_l831
 (->
  (rdatasets/ggplot2-economics)
  (pj/frame :date :unemploy)
  pj/lay-step
  pj/lay-area
  (pj/options
   {:title "US Unemployment (Step Area)",
    :x-label "Date",
    :y-label "Unemployed (thousands)"})))


(deftest
 t164_l839
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:polygons s)))))
   v163_l831)))


(def
 v166_l848
 (->
  (rdatasets/ggplot2-economics)
  (pj/frame :date :psavert)
  pj/lay-area
  pj/lay-line
  (pj/options
   {:title "US Personal Savings Rate",
    :x-label "Date",
    :y-label "Savings Rate (%)"})))


(deftest
 t167_l856
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:lines s)))))
   v166_l848)))


(def
 v169_l863
 (->
  (rdatasets/ggplot2-txhousing)
  (tc/select-rows
   (fn*
    [p1__84695#]
    (#{"Houston" "Dallas" "San Antonio" "Austin"} (:city p1__84695#))))
  (pj/frame :date :median {:color :city})
  pj/lay-line
  (pj/options
   {:title "Texas Median Home Prices",
    :x-label "Date",
    :y-label "Median Price ($)"})))


(deftest
 t170_l871
 (is ((fn [v] (pos? (:lines (pj/svg-summary v)))) v169_l863)))


(def
 v172_l878
 (->
  (rdatasets/lme4-sleepstudy)
  (pj/frame
   :days
   :reaction
   {:color :subject, :color-type :categorical})
  pj/lay-line
  pj/lay-point
  (pj/options
   {:title "Sleep Deprivation: Reaction Time by Subject",
    :x-label "Days of Sleep Deprivation",
    :y-label "Reaction Time (ms)"})))


(deftest
 t173_l886
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:lines s)) (= 180 (:points s)))))
   v172_l878)))


(def
 v175_l893
 (->
  (rdatasets/lme4-sleepstudy)
  (tc/select-rows
   (fn* [p1__84696#] (= "308" (str (:subject p1__84696#)))))
  (pj/frame :days :reaction)
  pj/lay-step
  pj/lay-point
  (pj/options
   {:title "Subject 308: Reaction Time (Step)",
    :x-label "Days",
    :y-label "Reaction Time (ms)"})))


(deftest
 t176_l902
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v175_l893)))


(def
 v178_l912
 (->
  (rdatasets/datasets-faithful)
  (pj/frame :eruptions :waiting)
  pj/lay-point
  (pj/options
   {:title "Old Faithful Geyser",
    :x-label "Eruption Duration (min)",
    :y-label "Waiting Time (min)"})))


(deftest
 t179_l919
 (is ((fn [v] (= 272 (:points (pj/svg-summary v)))) v178_l912)))


(def
 v181_l924
 (->
  (rdatasets/datasets-faithful)
  (pj/frame :eruptions :waiting)
  pj/lay-point
  pj/lay-smooth
  (pj/options
   {:title "Old Faithful with LOESS",
    :x-label "Eruption Duration (min)",
    :y-label "Waiting Time (min)"})))


(deftest
 t182_l932
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 272 (:points s)) (pos? (:lines s)))))
   v181_l924)))


(def
 v184_l941
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/lay-point :carat :price {:alpha 0.05})
  (pj/options
   {:title "Diamond Price vs Carat (alpha = 0.05)",
    :x-label "Carat",
    :y-label "Price ($)"})))


(deftest
 t185_l947
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v184_l941)))


(def
 v187_l954
 (->
  (rdatasets/datasets-mtcars)
  (pj/lay-point :wt :mpg {:color :hp})
  (pj/options
   {:title "Cars: Color by Horsepower",
    :x-label "Weight (1000 lbs)",
    :y-label "Miles per Gallon"})))


(deftest
 t188_l960
 (is ((fn [v] (= 32 (:points (pj/svg-summary v)))) v187_l954)))


(def
 v190_l965
 (->
  (rdatasets/datasets-mtcars)
  (pj/lay-point :hp :mpg {:color :cyl, :size :disp})
  (pj/options
   {:title "Cars: Color by Cylinders, Size by Displacement",
    :x-label "Horsepower",
    :y-label "Miles per Gallon"})))


(deftest
 t191_l971
 (is ((fn [v] (= 32 (:points (pj/svg-summary v)))) v190_l965)))


(def
 v193_l976
 (->
  (tc/select-rows
   (rdatasets/gapminder-gapminder)
   (fn* [p1__84697#] (= 2007 (:year p1__84697#))))
  (pj/lay-point
   :gdp-percap
   :life-exp
   {:color :continent, :size :pop, :alpha 0.6})
  (pj/scale :x :log)
  (pj/options
   {:title "Gapminder 2007",
    :x-label "GDP per Capita (log)",
    :y-label "Life Expectancy"})))


(deftest
 t194_l983
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v193_l976)))


(def
 v196_l988
 (->
  (rdatasets/ggplot2-midwest)
  (pj/lay-point
   :percollege
   :percbelowpoverty
   {:color :state, :size :poptotal, :alpha 0.5})
  (pj/options
   {:title "Midwest: College Education vs Poverty",
    :x-label "Percent College Educated",
    :y-label "Percent Below Poverty"})))


(deftest
 t197_l994
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v196_l988)))


(def
 v199_l999
 (def
  msleep
  (tc/drop-missing
   (rdatasets/ggplot2-msleep)
   [:sleep-total :bodywt :brainwt :vore])))


(def
 v200_l1002
 (->
  msleep
  (pj/lay-point :bodywt :brainwt {:color :vore})
  (pj/scale :x :log)
  (pj/scale :y :log)
  (pj/options
   {:title "Mammal Body vs Brain Weight (log-log)",
    :x-label "Body Weight (kg, log)",
    :y-label "Brain Weight (kg, log)"})))


(deftest
 t201_l1010
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v200_l1002)))


(def
 v203_l1015
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :petal-length {:color :species})
  pj/lay-point
  (pj/coord :fixed)
  (pj/options
   {:title "Iris: Sepal vs Petal Length (1:1 Aspect)",
    :x-label "Sepal Length",
    :y-label "Petal Length"})))


(deftest
 t204_l1023
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v203_l1015)))


(def
 v206_l1028
 (->
  (rdatasets/datasets-mtcars)
  (tc/order-by [:mpg] :desc)
  (tc/select-rows (range 5))
  (pj/frame :wt :mpg)
  pj/lay-point
  (pj/lay-label {:text :rownames})
  (pj/options
   {:title "Top 5 Most Fuel Efficient Cars",
    :x-label "Weight (1000 lbs)",
    :y-label "Miles per Gallon"})))


(deftest
 t207_l1038
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 5 (:points s)) (pos? (count (:texts s))))))
   v206_l1028)))


(def
 v209_l1045
 (->
  (rdatasets/datasets-iris)
  (pj/frame :petal-length :petal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/options
   {:title "Iris Petals with Linear Fit per Species",
    :x-label "Petal Length",
    :y-label "Petal Width"})))


(deftest
 t210_l1053
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v209_l1045)))


(def
 v212_l1060
 (->
  (rdatasets/datasets-mtcars)
  (pj/frame :wt :mpg)
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model, :confidence-band true})
  (pj/options
   {:title "Weight vs MPG with 95% Confidence Band",
    :x-label "Weight (1000 lbs)",
    :y-label "Miles per Gallon"})))


(deftest
 t213_l1068
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 32 (:points s)) (pos? (:lines s)) (pos? (:polygons s)))))
   v212_l1060)))


(def
 v215_l1078
 (->
  (rdatasets/datasets-mtcars)
  (pj/frame :wt :mpg)
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  pj/lay-smooth
  (pj/options
   {:title "Cars: LM and LOESS Smoothers",
    :x-label "Weight (1000 lbs)",
    :y-label "Miles per Gallon"})))


(deftest
 t216_l1087
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 32 (:points s)) (>= (:lines s) 2))))
   v215_l1078)))


(def
 v218_l1097
 (->
  (rdatasets/datasets-faithful)
  (pj/frame :eruptions)
  (pj/lay-histogram {:normalize :density, :binwidth 0.25})
  pj/lay-density
  (pj/options
   {:title "Old Faithful: Histogram + Density",
    :x-label "Eruption Duration (min)",
    :y-label "Density"})))


(deftest
 t219_l1105
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v218_l1097)))


(def
 v221_l1110
 (->
  (rdatasets/datasets-faithful)
  (pj/frame :eruptions)
  pj/lay-density
  pj/lay-rug
  (pj/options
   {:title "Old Faithful: Density with Rug",
    :x-label "Eruption Duration (min)",
    :y-label "Density"})))


(deftest
 t222_l1118
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:lines s)))))
   v221_l1110)))


(def
 v224_l1125
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/frame :depth)
  pj/lay-density
  (pj/options
   {:title "Distribution of Diamond Depth",
    :x-label "Depth (%)",
    :y-label "Density"})))


(deftest
 t225_l1132
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v224_l1125)))


(def
 v227_l1137
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/frame :depth)
  (pj/lay-histogram {:normalize :density})
  pj/lay-density
  (pj/options
   {:title "Diamond Depth: Histogram + Density",
    :x-label "Depth (%)",
    :y-label "Density"})))


(deftest
 t228_l1145
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v227_l1137)))


(def
 v230_l1150
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density :petal-width {:color :species})
  (pj/options
   {:title "Iris Petal Width by Species",
    :x-label "Petal Width (cm)",
    :y-label "Density"})))


(deftest
 t231_l1156
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v230_l1150)))


(def
 v233_l1161
 (->
  msleep
  (pj/lay-density :sleep-total {:color :vore})
  (pj/options
   {:title "Sleep Duration by Diet Type",
    :x-label "Total Sleep (hours)",
    :y-label "Density"})))


(deftest
 t234_l1167
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v233_l1161)))


(def
 v236_l1172
 (->
  (rdatasets/datasets-faithful)
  (pj/frame :waiting)
  (pj/lay-histogram {:bins 15})
  (pj/options
   {:title "Waiting Time Between Eruptions (15 bins)",
    :x-label "Waiting Time (min)",
    :y-label "Count"})))


(deftest
 t237_l1179
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v236_l1172)))


(def
 v239_l1184
 (->
  (tc/dataset
   {:value
    (repeatedly
     500
     (fn* [] (+ (* 2.0 (rand)) (* 2.0 (rand)) (* 2.0 (rand)) -3.0)))})
  (pj/frame :value)
  (pj/lay-histogram {:bins 30, :normalize :density})
  pj/lay-density
  (pj/options
   {:title "Simulated Distribution: Histogram + Density",
    :x-label "Value",
    :y-label "Density"})))


(deftest
 t240_l1192
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v239_l1184)))


(def
 v242_l1197
 (->
  (rdatasets/datasets-chickwts)
  (pj/frame :feed :weight {:color :feed})
  pj/lay-boxplot
  (pj/options
   {:title "Chick Weight by Feed Type",
    :x-label "Feed",
    :y-label "Weight (g)"})))


(deftest
 t243_l1204
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v242_l1197)))


(def
 v245_l1209
 (->
  (rdatasets/datasets-iris)
  (pj/frame :species :sepal-length {:color :species})
  pj/lay-boxplot
  (pj/coord :flip)
  (pj/options
   {:title "Iris Sepal Length (Horizontal Box)",
    :x-label "Species",
    :y-label "Sepal Length (cm)"})))


(deftest
 t246_l1217
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v245_l1209)))


(def
 v248_l1224
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day :total-bill {:color :sex})
  pj/lay-boxplot
  (pj/options
   {:title "Tips by Day and Gender (Grouped Boxplot)",
    :x-label "Day",
    :y-label "Total Bill ($)"})))


(deftest
 t249_l1231
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v248_l1224)))


(def
 v251_l1236
 (->
  (rdatasets/datasets-iris)
  (pj/frame :species :sepal-width {:color :species})
  pj/lay-violin
  (pj/options
   {:title "Iris Sepal Width (Violin)",
    :x-label "Species",
    :y-label "Sepal Width (cm)"})))


(deftest
 t252_l1243
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v251_l1236)))


(def
 v254_l1248
 (->
  (rdatasets/datasets-iris)
  (pj/frame :species :petal-width {:color :species})
  pj/lay-violin
  (pj/coord :flip)
  (pj/options
   {:title "Iris Petal Width (Horizontal Violin)",
    :x-label "Species",
    :y-label "Petal Width (cm)"})))


(deftest
 t255_l1256
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v254_l1248)))


(def
 v257_l1264
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day :total-bill)
  pj/lay-violin
  pj/lay-point
  (pj/options
   {:title "Tips: Violin with Individual Points",
    :x-label "Day",
    :y-label "Total Bill ($)"})))


(deftest
 t258_l1272
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:polygons s)) (= 244 (:points s)))))
   v257_l1264)))


(def
 v260_l1281
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day :total-bill)
  pj/lay-violin
  pj/lay-boxplot
  pj/lay-point
  (pj/options
   {:title "Tips: Violin + Boxplot + Points",
    :x-label "Day",
    :y-label "Total Bill ($)"})))


(deftest
 t261_l1290
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:points s)))))
   v260_l1281)))


(def
 v263_l1297
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :smoker :total-bill {:color :smoker})
  pj/lay-violin
  (pj/options
   {:title "Total Bill by Smoking Status",
    :x-label "Smoker",
    :y-label "Total Bill ($)"})))


(deftest
 t264_l1304
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v263_l1297)))


(def
 v266_l1309
 (->
  (rdatasets/datasets-iris)
  (pj/frame :species :petal-length)
  pj/lay-ridgeline
  (pj/options
   {:title "Iris Petal Length by Species (Ridgeline)",
    :x-label "Species",
    :y-label "Petal Length (cm)"})))


(deftest
 t267_l1316
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v266_l1309)))


(def
 v269_l1321
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/frame :color :price)
  pj/lay-ridgeline
  (pj/options
   {:title "Diamond Price by Color Grade (Ridgeline)",
    :x-label "Color",
    :y-label "Price ($)"})))


(deftest
 t270_l1328
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v269_l1321)))


(def
 v272_l1333
 (def
  airquality
  (->
   (rdatasets/datasets-airquality)
   (tc/drop-missing :ozone)
   (tc/add-column
    :month-name
    (fn
     [ds]
     (map
      (fn*
       [p1__84698#]
       (get {5 "May", 6 "Jun", 7 "Jul", 8 "Aug", 9 "Sep"} p1__84698#))
      (ds :month)))))))


(def
 v273_l1340
 (->
  airquality
  (pj/frame :month-name :ozone {:color :month-name})
  pj/lay-boxplot
  (pj/options
   {:title "New York Ozone by Month",
    :x-label "Month",
    :y-label "Ozone (ppb)"})))


(deftest
 t274_l1347
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v273_l1340)))


(def
 v276_l1355
 (->
  (rdatasets/ggplot2-mpg)
  (pj/frame :class)
  pj/lay-bar
  (pj/options
   {:title "Vehicle Count by Class",
    :x-label "Class",
    :y-label "Count"})))


(deftest
 t277_l1362
 (is ((fn [v] (= 7 (:polygons (pj/svg-summary v)))) v276_l1355)))


(def
 v279_l1367
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-bar :day {:color :sex})
  (pj/options
   {:title "Tips Count by Day and Gender",
    :x-label "Day",
    :y-label "Count"})))


(deftest
 t280_l1373
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v279_l1367)))


(def
 v282_l1378
 (->
  (tc/dataset
   {:country ["US" "China" "Japan" "Germany" "UK" "India" "France"],
    :gdp [21.4 14.7 5.1 3.8 2.8 2.7 2.6]})
  (pj/lay-value-bar :country :gdp)
  (pj/coord :flip)
  (pj/options
   {:title "GDP by Country (2019)",
    :x-label "Country",
    :y-label "GDP (Trillion $)"})))


(deftest
 t283_l1386
 (is ((fn [v] (= 7 (:polygons (pj/svg-summary v)))) v282_l1378)))


(def
 v285_l1393
 (->
  (tc/dataset
   {:metric
    ["Quality"
     "Speed"
     "Usability"
     "Reliability"
     "Support"
     "Price"
     "Design"
     "Docs"],
    :score [-30 -20 -10 5 15 25 35 45]})
  (pj/lay-value-bar :metric :score)
  (pj/lay-rule-h {:y-intercept 0})
  (pj/coord :flip)
  (pj/options
   {:title "Customer Satisfaction Scores",
    :x-label "Metric",
    :y-label "Net Score"})))


(deftest
 t286_l1402
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 8 (:polygons s)) (pos? (:lines s)))))
   v285_l1393)))


(def
 v288_l1409
 (->
  (rdatasets/datasets-chickwts)
  (tc/group-by [:feed])
  (tc/aggregate {:mean-weight (fn [ds] (dfn/mean (ds :weight)))})
  (pj/lay-lollipop :feed :mean-weight)
  (pj/coord :flip)
  (pj/options
   {:title "Mean Chick Weight by Feed Type",
    :x-label "Feed",
    :y-label "Mean Weight (g)"})))


(deftest
 t289_l1418
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v288_l1409)))


(def
 v291_l1425
 (->
  (rdatasets/datasets-iris)
  (tc/group-by [:species])
  (tc/aggregate {:mean-sl (fn [ds] (fstats/mean (ds :sepal-length)))})
  (pj/lay-lollipop :species :mean-sl)
  (pj/coord :flip)
  (pj/options
   {:title "Mean Sepal Length by Species",
    :x-label "Species",
    :y-label "Mean Sepal Length (cm)"})))


(deftest
 t292_l1434
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:points s)) (pos? (:lines s)))))
   v291_l1425)))


(def
 v294_l1444
 (->
  (rdatasets/datasets-faithful)
  (pj/frame :eruptions :waiting)
  pj/lay-density-2d
  (pj/options
   {:title "Old Faithful: 2D Density",
    :x-label "Eruption Duration (min)",
    :y-label "Waiting Time (min)"})))


(deftest
 t295_l1451
 (is ((fn [v] (pos? (:visible-tiles (pj/svg-summary v)))) v294_l1444)))


(def
 v297_l1456
 (->
  (rdatasets/datasets-faithful)
  (pj/frame :eruptions :waiting)
  pj/lay-point
  pj/lay-density-2d
  (pj/options
   {:title "Old Faithful: Scatter + Density",
    :x-label "Eruption Duration (min)",
    :y-label "Waiting Time (min)"})))


(deftest
 t298_l1464
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 272 (:points s)) (pos? (:visible-tiles s)))))
   v297_l1456)))


(def
 v300_l1471
 (->
  (rdatasets/datasets-faithful)
  (pj/frame :eruptions :waiting)
  pj/lay-point
  pj/lay-contour
  (pj/options
   {:title "Old Faithful: Scatter + Contour",
    :x-label "Eruption Duration (min)",
    :y-label "Waiting Time (min)"})))


(deftest
 t301_l1479
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 272 (:points s)) (pos? (:lines s)))))
   v300_l1471)))


(def
 v303_l1486
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :petal-length)
  pj/lay-contour
  (pj/options
   {:title "Iris: Sepal vs Petal Length Contour",
    :x-label "Sepal Length",
    :y-label "Petal Length"})))


(deftest
 t304_l1493
 (is ((fn [v] (pos? (:lines (pj/svg-summary v)))) v303_l1486)))


(def
 v306_l1500
 (->
  (rdatasets/ggplot2-faithfuld)
  (pj/frame :eruptions :waiting {:fill :density})
  pj/lay-tile
  (pj/options
   {:title "Old Faithful: Pre-computed Density Heatmap",
    :x-label "Eruption Duration",
    :y-label "Waiting Time"})))


(deftest
 t307_l1507
 (is ((fn [v] (pos? (:visible-tiles (pj/svg-summary v)))) v306_l1500)))


(def
 v309_l1512
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 3000)
  (pj/frame :carat :price)
  pj/lay-point
  pj/lay-density-2d
  (pj/options
   {:title "Diamonds: Scatter + 2D Density",
    :x-label "Carat",
    :y-label "Price ($)"})))


(deftest
 t310_l1521
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (pos? (:visible-tiles s)))))
   v309_l1512)))


(def
 v312_l1528
 (->
  (rdatasets/ggplot2-mpg)
  (pj/frame :displ :hwy)
  pj/lay-density-2d
  (pj/options
   {:title "MPG: Displacement vs Highway (Density)",
    :x-label "Displacement (L)",
    :y-label "Highway MPG"})))


(deftest
 t313_l1535
 (is ((fn [v] (pos? (:visible-tiles (pj/svg-summary v)))) v312_l1528)))


(def
 v315_l1540
 (->
  (tc/dataset
   {:row (mapcat (fn* [p1__84699#] (repeat 6 p1__84699#)) (range 6)),
    :col (flatten (repeat 6 (range 6))),
    :value
    (map (fn* [p1__84700#] (Math/sin (* p1__84700# 0.5))) (range 36))})
  (pj/frame :col :row {:fill :value})
  pj/lay-tile
  (pj/options
   {:title "Synthetic Heatmap (sin wave)",
    :x-label "Column",
    :y-label "Row"})))


(deftest
 t316_l1549
 (is ((fn [v] (pos? (:visible-tiles (pj/svg-summary v)))) v315_l1540)))


(def
 v318_l1557
 (->
  (rdatasets/datasets-iris)
  (tc/group-by [:species])
  (tc/aggregate
   {:mean (fn [ds] (fstats/mean (ds :sepal-length))),
    :y-min
    (fn
     [ds]
     (-
      (fstats/mean (ds :sepal-length))
      (fstats/stddev (ds :sepal-length)))),
    :y-max
    (fn
     [ds]
     (+
      (fstats/mean (ds :sepal-length))
      (fstats/stddev (ds :sepal-length))))})
  (pj/lay-errorbar :species :mean {:y-min :y-min, :y-max :y-max})
  (pj/lay-point :species :mean)
  (pj/options
   {:title "Mean Sepal Length +/- SD by Species",
    :x-label "Species",
    :y-label "Sepal Length (cm)"})))


(deftest
 t319_l1570
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:points s)) (pos? (:lines s)))))
   v318_l1557)))


(def
 v321_l1577
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-summary :day :tip {:color :sex})
  (pj/options
   {:title "Mean Tip +/- SE by Day and Gender",
    :x-label "Day",
    :y-label "Tip ($)"})))


(deftest
 t322_l1583
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v321_l1577)))


(def
 v324_l1593
 (->
  (rdatasets/ggplot2-economics)
  (pj/lay-line :date :unemploy)
  (pj/lay-line :date :uempmed)
  (pj/options
   {:title "Unemployment: Total vs Median Duration",
    :x-label "Date",
    :y-label "Value"})))


(deftest
 t325_l1600
 (is ((fn [v] (>= (:lines (pj/svg-summary v)) 2)) v324_l1593)))


(def
 v327_l1605
 (->
  (rdatasets/ggplot2-economics)
  (pj/lay-line :date :unemploy)
  (pj/lay-line :date :uempmed)
  (pj/lay-line :date :psavert)
  (pj/options
   {:title "US Economic Indicators (Three Series)",
    :x-label "Date",
    :y-label "Value"})))


(deftest
 t328_l1613
 (is ((fn [v] (>= (:lines (pj/svg-summary v)) 3)) v327_l1605)))


(def
 v330_l1620
 (->
  (rdatasets/ggplot2-mpg)
  (pj/lay-point :displ :hwy)
  (pj/lay-line :displ :cty)
  (pj/options
   {:title "MPG: Highway (points) vs City (line)",
    :x-label "Displacement (L)",
    :y-label "MPG"})))


(deftest
 t331_l1627
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v330_l1620)))


(def
 v333_l1637
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  pj/lay-point
  (pj/lay-rule-h {:y-intercept 3.0})
  (pj/lay-rule-h {:y-intercept 4.0})
  (pj/lay-rule-v {:x-intercept 5.0})
  (pj/lay-rule-v {:x-intercept 7.0})
  (pj/options
   {:title "Iris: Scatter with Grid Lines",
    :x-label "Sepal Length",
    :y-label "Sepal Width"})))


(deftest
 t334_l1648
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (>= (:lines s) 4))))
   v333_l1637)))


(def
 v336_l1655
 (->
  (rdatasets/datasets-mtcars)
  (pj/frame :wt :mpg)
  pj/lay-point
  (pj/lay-band-h {:y-min 20, :y-max 30})
  (pj/lay-band-v {:x-min 2.5, :x-max 3.5})
  (pj/options
   {:title "Cars: Scatter with Highlight Bands",
    :x-label "Weight (1000 lbs)",
    :y-label "MPG"})))


(deftest
 t337_l1664
 (is ((fn [v] (= 32 (:points (pj/svg-summary v)))) v336_l1655)))


(def
 v339_l1669
 (->
  (rdatasets/ggplot2-economics)
  (pj/frame :date :unemploy)
  pj/lay-area
  (pj/lay-rule-h {:y-intercept 8000})
  (pj/options
   {:title "US Unemployment with 8000 Threshold",
    :x-label "Date",
    :y-label "Unemployed (thousands)"})))


(deftest
 t340_l1677
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:lines s)))))
   v339_l1669)))


(def
 v342_l1684
 (->
  airquality
  (pj/lay-line :rownames :ozone)
  (pj/lay-rule-h {:y-intercept 60})
  (pj/options
   {:title "NYC Ozone with Threshold at 60 ppb",
    :x-label "Observation",
    :y-label "Ozone (ppb)"})))


(deftest
 t343_l1691
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (and (pos? (:lines s)))))
   v342_l1684)))


(def
 v345_l1697
 (->
  airquality
  (pj/frame :wind :ozone)
  pj/lay-point
  (pj/lay-band-h {:y-min 0, :y-max 40})
  (pj/options
   {:title "Ozone vs Wind: Safe Zone Highlighted",
    :x-label "Wind Speed (mph)",
    :y-label "Ozone (ppb)"})))


(deftest
 t346_l1705
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v345_l1697)))


(def
 v348_l1713
 (->
  (rdatasets/ggplot2-mpg)
  (pj/frame :displ :hwy)
  pj/lay-point
  (pj/facet :class)
  (pj/options
   {:title "MPG: Faceted by Vehicle Class",
    :x-label "Displacement",
    :y-label "Highway MPG"})))


(deftest
 t349_l1721
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v348_l1713)))


(def
 v351_l1726
 (->
  (rdatasets/ggplot2-mpg)
  (pj/frame :displ :hwy)
  pj/lay-point
  (pj/facet-grid :drv :year)
  (pj/options
   {:title "MPG: Drive Type x Model Year",
    :x-label "Displacement",
    :y-label "Highway MPG"})))


(deftest
 t352_l1734
 (is ((fn [v] (= 6 (:panels (pj/svg-summary v)))) v351_l1726)))


(def
 v354_l1743
 (->
  (rdatasets/ggplot2-mpg)
  (pj/frame :displ :hwy)
  pj/lay-point
  (pj/facet-grid :drv :class)
  (pj/options
   {:title "MPG: Drive x Class",
    :x-label "Displacement",
    :y-label "Highway MPG"})))


(deftest
 t355_l1751
 (is ((fn [v] (pos? (:panels (pj/svg-summary v)))) v354_l1743)))


(def
 v357_l1756
 (->
  (rdatasets/ggplot2-mpg)
  (pj/frame :displ :hwy)
  pj/lay-point
  (pj/facet-grid nil :drv)
  (pj/options
   {:title "MPG: Column Facets by Drive Type",
    :x-label "Displacement",
    :y-label "Highway MPG"})))


(deftest
 t358_l1764
 (is ((fn [v] (= 3 (:panels (pj/svg-summary v)))) v357_l1756)))


(def
 v360_l1769
 (->
  (rdatasets/datasets-iris)
  (pj/frame :petal-length)
  pj/lay-density
  (pj/facet :species)
  (pj/options
   {:title "Petal Length Density by Species",
    :x-label "Petal Length (cm)",
    :y-label "Density"})))


(deftest
 t361_l1777
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v360_l1769)))


(def
 v363_l1782
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day :total-bill)
  pj/lay-boxplot
  (pj/facet :sex)
  (pj/options
   {:title "Total Bill by Day, Faceted by Gender",
    :x-label "Day",
    :y-label "Total Bill ($)"})))


(deftest
 t364_l1790
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:polygons s)) (= 2 (:panels s)))))
   v363_l1782)))


(def
 v366_l1797
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day :total-bill)
  pj/lay-violin
  (pj/facet :sex)
  (pj/options
   {:title "Total Bill Violin by Day, Faceted by Gender",
    :x-label "Day",
    :y-label "Total Bill ($)"})))


(deftest
 t367_l1805
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:polygons s)) (= 2 (:panels s)))))
   v366_l1797)))


(def
 v369_l1812
 (->
  (rdatasets/ggplot2-mpg)
  (pj/frame :class)
  pj/lay-bar
  (pj/facet :year)
  (pj/options
   {:title "Vehicle Class Count by Model Year",
    :x-label "Class",
    :y-label "Count"})))


(deftest
 t370_l1820
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v369_l1812)))


(def
 v372_l1825
 (->
  (rdatasets/datasets-iris)
  (pj/frame :petal-length :petal-width)
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/facet :species)
  (pj/options
   {:title "Iris Petals: Faceted Regression",
    :x-label "Petal Length",
    :y-label "Petal Width"})))


(deftest
 t373_l1834
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (= 3 (:lines s)) (= 3 (:panels s)))))
   v372_l1825)))


(def
 v375_l1842
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day :total-bill)
  pj/lay-boxplot
  (pj/facet-grid :time :smoker)
  (pj/options
   {:title "Tips: Day x Time x Smoker",
    :x-label "Day",
    :y-label "Total Bill ($)"})))


(deftest
 t376_l1850
 (is ((fn [v] (= 4 (:panels (pj/svg-summary v)))) v375_l1842)))


(def
 v378_l1855
 (->
  (tc/select-rows
   (rdatasets/gapminder-gapminder)
   (fn* [p1__84701#] (= 2007 (:year p1__84701#))))
  (pj/frame :gdp-percap :life-exp)
  pj/lay-point
  (pj/scale :x :log)
  (pj/facet :continent)
  (pj/options
   {:title "Gapminder 2007 by Continent",
    :x-label "GDP per Capita (log)",
    :y-label "Life Expectancy"})))


(deftest
 t379_l1864
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v378_l1855)))


(def
 v381_l1869
 (->
  (rdatasets/lme4-sleepstudy)
  (pj/frame :days :reaction)
  pj/lay-line
  pj/lay-point
  (pj/facet :subject)
  (pj/options
   {:title "Sleep Study: Each Subject",
    :x-label "Days",
    :y-label "Reaction Time (ms)"})))


(deftest
 t382_l1878
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:lines s)) (= 180 (:points s)))))
   v381_l1869)))


(def
 v384_l1885
 (->
  (rdatasets/ggplot2-mpg)
  (pj/frame :displ :hwy)
  pj/lay-point
  pj/lay-smooth
  (pj/facet :cyl)
  (pj/options
   {:title "MPG: Scatter + LOESS by Cylinder Count",
    :x-label "Displacement",
    :y-label "Highway MPG"})))


(deftest
 t385_l1894
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v384_l1885)))


(def
 v387_l1904
 (->
  (rdatasets/datasets-mtcars)
  (pj/frame (pj/cross [:mpg :hp :wt] [:mpg :hp :wt]))
  (pj/options {:title "Motor Trend Cars: 3x3 SPLOM"})))


(deftest
 t388_l1908
 (is ((fn [v] (= 9 (:panels (pj/svg-summary v)))) v387_l1904)))


(def
 v390_l1913
 (->
  (rdatasets/datasets-mtcars)
  (pj/frame (pj/cross [:mpg :wt] [:mpg :wt]))
  (pj/options {:title "MPG vs Weight: 2x2 SPLOM"})))


(deftest
 t391_l1917
 (is ((fn [v] (= 4 (:panels (pj/svg-summary v)))) v390_l1913)))


(def
 v393_l1925
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day)
  pj/lay-bar
  (pj/coord :polar)
  (pj/options {:title "Tips Count by Day (Rose)"})))


(deftest
 t394_l1931
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v393_l1925)))


(def
 v396_l1936
 (->
  (rdatasets/datasets-chickwts)
  (pj/frame :feed)
  pj/lay-bar
  (pj/coord :polar)
  (pj/options {:title "Chick Count by Feed (Rose)"})))


(deftest
 t397_l1942
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v396_l1936)))


(def
 v399_l1947
 (->
  (tc/dataset
   {:day ["Mon" "Tue" "Wed" "Thu" "Fri" "Sat" "Sun"],
    :hours [8 7 6 9 5 3 4]})
  (pj/lay-value-bar :day :hours)
  (pj/coord :polar)
  (pj/options {:title "Weekly Working Hours (Polar)"})))


(deftest
 t400_l1953
 (is ((fn [v] (= 7 (:polygons (pj/svg-summary v)))) v399_l1947)))


(def
 v402_l1961
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 2000)
  (pj/lay-point :carat :price {:alpha 0.15})
  (pj/scale :y :log)
  (pj/options
   {:title "Diamond Price (Log Scale)",
    :x-label "Carat",
    :y-label "Price ($, log)"})))


(deftest
 t403_l1969
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v402_l1961)))


(def
 v405_l1974
 (->
  msleep
  (pj/lay-point :bodywt :sleep-total {:color :vore})
  (pj/scale :x :log)
  (pj/options
   {:title "Body Weight vs Sleep (log x-axis)",
    :x-label "Body Weight (kg, log)",
    :y-label "Total Sleep (hours)"})))


(deftest
 t406_l1981
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v405_l1974)))


(def
 v408_l1992
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day {:color :time})
  (pj/lay-bar {:position :stack})
  (pj/options
   {:title "Tips by Day and Meal Time (Stacked)",
    :x-label "Day",
    :y-label "Count"})))


(deftest t409_l1999 (is ((fn [v] (pj/frame? v)) v408_l1992)))


(def
 v411_l2006
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :day :total-bill)
  pj/lay-bar
  pj/lay-point
  (pj/options
   {:title "Tips: Bar Count with Individual Points",
    :x-label "Day",
    :y-label "Total Bill ($)"})))


(deftest
 t412_l2014
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:points s)))))
   v411_l2006)))


(def
 v414_l2026
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width {:color :species})
  pj/lay-density-2d
  (pj/options
   {:title "Iris: 2D Density by Species",
    :x-label "Sepal Length",
    :y-label "Sepal Width"})))


(deftest
 t415_l2033
 (is ((fn [v] (pos? (:visible-tiles (pj/svg-summary v)))) v414_l2026)))


(def
 v417_l2038
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 1000)
  (pj/frame :carat :price)
  pj/lay-contour
  pj/lay-point
  (pj/options
   {:title "Diamonds: Contour + Scatter",
    :x-label "Carat",
    :y-label "Price ($)"})))


(deftest
 t418_l2047
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v417_l2038)))
