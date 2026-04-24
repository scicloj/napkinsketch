(ns
 napkinsketch-book.gallery-generated-test
 (:require
  [scicloj.napkinsketch.api :as sk]
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
  (sk/frame :displ :hwy {:color :class})
  sk/lay-point
  sk/lay-smooth
  (sk/options
   {:title "Fuel Efficiency by Engine Size",
    :x-label "Engine Displacement (L)",
    :y-label "Highway MPG"})))


(deftest
 t4_l36
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v3_l28)))


(def
 v6_l46
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 500)
  (sk/frame :carat :price {:color :cut, :size :depth})
  sk/lay-point
  (sk/options
   {:title "Diamond Price vs Carat (bubble)",
    :x-label "Carat",
    :y-label "Price (USD)"})))


(deftest
 t7_l54
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 500 (:points s)))) v6_l46)))


(def
 v9_l62
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day :total-bill {:color :sex})
  sk/lay-point
  (sk/options
   {:title "Total Bill by Day",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t10_l69
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 244 (:points s)))) v9_l62)))


(def
 v12_l79
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/frame :price)
  sk/lay-histogram
  (sk/options
   {:title "Distribution of Diamond Prices",
    :x-label "Price (USD)",
    :y-label "Count"})))


(deftest
 t13_l86
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s)))) v12_l79)))


(def
 v15_l91
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/frame :price {:color :cut})
  sk/lay-histogram
  (sk/options
   {:title "Diamond Prices by Cut",
    :x-label "Price (USD)",
    :y-label "Count"})))


(deftest
 t16_l98
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (< 1 (:polygons s)))) v15_l91)))


(def
 v18_l106
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/frame :carat {:color :cut})
  sk/lay-density
  (sk/options
   {:title "Carat Distribution by Cut",
    :x-label "Carat",
    :y-label "Density"})))


(deftest
 t19_l113
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v18_l106)))


(def
 v21_l118
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 500)
  (sk/frame :carat)
  sk/lay-density
  sk/lay-rug
  (sk/options
   {:title "Carat Distribution with Rug",
    :x-label "Carat",
    :y-label "Density"})))


(deftest
 t22_l127
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:lines s)))))
   v21_l118)))


(def
 v24_l136
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day :total-bill {:color :day})
  sk/lay-boxplot
  (sk/options
   {:title "Total Bill by Day",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t25_l143
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v24_l136)))


(def
 v27_l148
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day :total-bill)
  sk/lay-boxplot
  sk/lay-point
  (sk/options
   {:title "Total Bill by Day (box + points)",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t28_l156
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:points s)))))
   v27_l148)))


(def
 v30_l165
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day :total-bill {:color :day})
  sk/lay-violin
  (sk/options
   {:title "Total Bill by Day (violin)",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t31_l172
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v30_l165)))


(def
 v33_l177
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day :total-bill {:color :day})
  sk/lay-violin
  sk/lay-boxplot
  (sk/options
   {:title "Total Bill Distribution by Day",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t34_l185
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v33_l177)))


(def
 v36_l193
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/frame :cut :price)
  sk/lay-ridgeline
  (sk/options
   {:title "Price Distribution by Cut (ridgeline)",
    :x-label "Cut",
    :y-label "Price (USD)"})))


(deftest
 t37_l200
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v36_l193)))


(def
 v39_l210
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/frame :cut)
  sk/lay-bar
  (sk/options
   {:title "Diamond Count by Cut", :x-label "Cut", :y-label "Count"})))


(deftest
 t40_l217
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:polygons s)))) v39_l210)))


(def
 v42_l225
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/frame :cut)
  sk/lay-bar
  (sk/coord :flip)
  (sk/options
   {:title "Diamond Count by Cut (horizontal)",
    :x-label "Cut",
    :y-label "Count"})))


(deftest
 t43_l233
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:polygons s)))) v42_l225)))


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
  (sk/frame :manufacturer :count)
  sk/lay-lollipop
  (sk/options
   {:title "Top Manufacturers by Model Count",
    :x-label "Manufacturer",
    :y-label "Count"})))


(deftest
 t47_l255
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v46_l248)))


(def
 v49_l261
 (->
  mpg-mfr-counts
  (sk/frame :manufacturer :count)
  sk/lay-lollipop
  (sk/coord :flip)
  (sk/options
   {:title "Top Manufacturers (horizontal lollipop)",
    :x-label "Manufacturer",
    :y-label "Count"})))


(deftest
 t50_l269
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v49_l261)))


(def
 v52_l280
 (->
  (rdatasets/ggplot2-economics)
  (sk/frame :date :unemploy)
  sk/lay-line
  (sk/options
   {:title "US Unemployment Over Time",
    :x-label "Date",
    :y-label "Unemployed (thousands)"})))


(deftest
 t53_l287
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v52_l280)))


(def
 v55_l292
 (->
  (rdatasets/gapminder-gapminder)
  (tc/select-rows
   (fn*
    [p1__153712#]
    (#{"Australia" "Brazil" "Japan" "Nigeria" "Germany"}
     (:country p1__153712#))))
  (sk/frame :year :life-exp {:color :country})
  sk/lay-line
  sk/lay-point
  (sk/options
   {:title "Life Expectancy Over Time",
    :x-label "Year",
    :y-label "Life Expectancy"})))


(deftest
 t56_l302
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v55_l292)))


(def
 v58_l311
 (->
  (rdatasets/ggplot2-economics)
  (sk/frame :date :unemploy)
  sk/lay-area
  (sk/options
   {:title "US Unemployment Over Time (area)",
    :x-label "Date",
    :y-label "Unemployed (thousands)"})))


(deftest
 t59_l318
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v58_l311)))


(def
 v61_l328
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 2000)
  (sk/frame :carat :price)
  sk/lay-density-2d
  (sk/options
   {:title "Diamond Carat vs Price (density)",
    :x-label "Carat",
    :y-label "Price (USD)"})))


(deftest
 t62_l336
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v61_l328)))


(def
 v64_l344
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :total-bill :tip {:color :sex})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  (sk/options
   {:title "Tip vs Total Bill (with regression)",
    :x-label "Total Bill (USD)",
    :y-label "Tip (USD)"})))


(deftest
 t65_l352
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v64_l344)))


(def
 v67_l361
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-contour
  (sk/options
   {:title "Iris Sepal Dimensions (contour)",
    :x-label "Sepal Length",
    :y-label "Sepal Width"})))


(deftest
 t68_l369
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v67_l361)))


(def
 v70_l380
 (->
  (rdatasets/ggplot2-mpg)
  (sk/frame :displ :hwy {:color :class})
  sk/lay-point
  (sk/facet-grid :drv nil)
  (sk/options
   {:title "Highway MPG by Engine Size, faceted by Drive",
    :x-label "Displacement",
    :y-label "Highway MPG"})))


(deftest
 t71_l388
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:panels s)))) v70_l380)))


(def
 v73_l393
 (->
  (rdatasets/ggplot2-mpg)
  (sk/frame :hwy)
  sk/lay-histogram
  (sk/facet-grid :drv nil)
  (sk/options
   {:title "Highway MPG by Drive Type",
    :x-label "Highway MPG",
    :y-label "Count"})))


(deftest
 t74_l401
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:panels s)))) v73_l393)))


(def
 v76_l411
 (->
  (rdatasets/datasets-iris)
  (sk/frame {:color :species})
  sk/lay-point
  (sk/frame
   (sk/cross
    [:sepal-length :sepal-width :petal-length :petal-width]
    [:sepal-length :sepal-width :petal-length :petal-width]))
  (sk/options {:title "Iris SPLOM"})))


(deftest
 t77_l418
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 16 (:panels s)))) v76_l411)))


(def
 v79_l428
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day {:color :sex})
  (sk/lay-bar {:position :stack})
  (sk/options
   {:title "Tips by Day and Sex (stacked bar)",
    :x-label "Day",
    :y-label "Count"})))


(deftest t80_l435 (is ((fn [v] (sk/frame? v)) v79_l428)))


(def
 v82_l439
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day {:color :sex})
  (sk/lay-bar {:position :fill})
  (sk/options
   {:title "Proportion by Day and Sex",
    :x-label "Day",
    :y-label "Proportion"})))


(deftest t83_l446 (is ((fn [v] (sk/frame? v)) v82_l439)))


(def
 v85_l453
 (->
  (rdatasets/gapminder-gapminder)
  (tc/group-by [:year :continent])
  (tc/aggregate {:pop (fn [ds] (reduce + (ds :pop)))})
  (tc/order-by [:year :continent])
  (sk/frame :year :pop {:color :continent})
  (sk/lay-area {:position :stack})
  (sk/options
   {:title "World Population by Continent",
    :x-label "Year",
    :y-label "Population"})))


(deftest
 t86_l463
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:polygons s)))) v85_l453)))


(def
 v88_l473
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/frame :cut)
  sk/lay-bar
  (sk/coord :polar)
  (sk/options {:title "Diamond Cut (rose chart)"})))


(deftest
 t89_l479
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:polygons s)))) v88_l473)))


(def
 v91_l488
 (->
  (rdatasets/datasets-mtcars)
  (sk/frame :wt :mpg)
  sk/lay-point
  (sk/lay-text {:text :rownames})
  (sk/options
   {:title "Motor Trend Cars",
    :x-label "Weight (1000 lbs)",
    :y-label "Miles per Gallon"})))


(deftest
 t92_l496
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (count (:texts s))))))
   v91_l488)))


(def
 v94_l503
 (->
  (rdatasets/datasets-mtcars)
  (sk/frame :wt :mpg)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model, :confidence-band true})
  (sk/options
   {:title "Weight vs MPG with Linear Fit",
    :x-label "Weight (1000 lbs)",
    :y-label "Miles per Gallon"})))


(deftest
 t95_l511
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v94_l503)))


(def
 v97_l518
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-bar :day {:color :sex})
  (sk/options {:title "Tips by Day and Gender"})))


(deftest
 t98_l522
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v97_l518)))


(def
 v100_l527
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/lay-point :carat :price {:alpha 0.1})
  (sk/scale :y :log)
  (sk/options
   {:title "Diamond Price by Carat (Log Scale)",
    :x-label "Carat",
    :y-label "Price ($, log scale)"})))


(deftest
 t101_l534
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v100_l527)))


(def
 v103_l539
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-summary :day :total-bill {:color :sex})
  (sk/options {:title "Average Bill with Standard Error"})))


(deftest
 t104_l543
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:points s)))) v103_l539)))


(def
 v106_l549
 (->
  (rdatasets/gapminder-gapminder)
  (tc/select-rows (fn* [p1__153713#] (= 2007 (:year p1__153713#))))
  (sk/lay-point :gdp-percap :life-exp {:color :continent, :size :pop})
  (sk/scale :x :log)
  (sk/options
   {:title "Gapminder 2007: Life Expectancy vs GDP",
    :x-label "GDP per Capita (log)",
    :y-label "Life Expectancy"})))


(deftest
 t107_l557
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v106_l549)))


(def
 v109_l562
 (->
  (rdatasets/gapminder-gapminder)
  (tc/select-rows
   (fn*
    [p1__153714#]
    (#{"Brazil" "United States" "Japan" "China" "India"}
     (:country p1__153714#))))
  (sk/lay-line :year :life-exp {:color :country})
  (sk/options
   {:title "Life Expectancy Over Time",
    :x-label "Year",
    :y-label "Life Expectancy"})))


(deftest
 t110_l569
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v109_l562)))


(def
 v112_l574
 (->
  (rdatasets/ggplot2-economics)
  (sk/lay-step :date :unemploy)
  (sk/options
   {:title "US Unemployment (Step)",
    :x-label "Date",
    :y-label "Unemployed (thousands)"})))


(deftest
 t113_l580
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v112_l574)))


(def
 v115_l585
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length)
  sk/lay-density
  sk/lay-rug
  (sk/options {:title "Iris Sepal Length: Density + Rug"})))


(deftest
 t116_l591
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v115_l585)))


(def
 v118_l596
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :total-bill :tip {:color :smoker})
  sk/lay-point
  (sk/lay-smooth {:confidence-band true})
  (sk/options
   {:title "Tips: Bill vs Tip by Smoking Status",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t119_l604
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v118_l596)))


(def
 v121_l611
 (->
  (rdatasets/ggplot2-mpg)
  (sk/lay-histogram :hwy {:color :drv})
  (sk/facet :drv)
  (sk/options {:title "Highway MPG by Drive Type"})))


(deftest
 t122_l616
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:panels s)))) v121_l611)))


(def
 v124_l622
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})
  (sk/lay-rule-v {:x-intercept 6.0})
  (sk/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1})
  (sk/options {:title "Iris with Reference Lines and Band"})))


(deftest
 t125_l629
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v124_l622)))


(def
 v127_l636
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day :total-bill)
  sk/lay-violin
  sk/lay-boxplot
  (sk/options {:title "Tips Distribution by Day"})))


(deftest
 t128_l642
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v127_l636)))


(def
 v130_l648
 (->
  (rdatasets/datasets-mtcars)
  (sk/lay-lollipop :rownames :mpg)
  (sk/coord :flip)
  (sk/options {:title "Cars Ranked by MPG"})))


(deftest
 t131_l653
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v130_l648)))


(def
 v133_l658
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-bar :day {:position :fill, :color :sex})
  (sk/options {:title "Gender Proportion by Day (100% stacked)"})))


(deftest
 t134_l662
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v133_l658)))


(def
 v136_l667
 (->
  (rdatasets/datasets-iris)
  (sk/frame {:color :species})
  sk/lay-point
  (sk/frame
   (sk/cross
    [:sepal-length :sepal-width :petal-length :petal-width]
    [:sepal-length :sepal-width :petal-length :petal-width]))))


(deftest
 t137_l673
 (is ((fn [v] (= 16 (:panels (sk/svg-summary v)))) v136_l667)))


(def
 v139_l678
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length)
  (sk/lay-histogram {:normalize :density})
  sk/lay-density
  (sk/options {:title "Sepal Length: Histogram + Density Curve"})))


(deftest
 t140_l684
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v139_l678)))


(def
 v142_l716
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :fixed)
  (sk/options {:title "Iris Sepals (Equal Aspect Ratio)"})))


(deftest
 t143_l721
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v142_l716)))


(def
 v145_l726
 (->
  (tc/dataset
   {:category ["Mon" "Tue" "Wed" "Thu" "Fri" "Sat" "Sun"],
    :value [120 200 150 80 70 110 130]})
  (sk/lay-value-bar :category :value)
  (sk/options {:title "Weekly Sales"})))


(deftest
 t146_l731
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v145_l726)))


(def
 v148_l736
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density :sepal-length {:color :species})
  (sk/options
   {:title "Sepal Length by Species", :x-label "Sepal Length (cm)"})))


(deftest
 t149_l741
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v148_l736)))


(def
 v151_l746
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:confidence-band true})
  (sk/options {:title "Iris: Scatter + LOESS by Species"})))


(deftest
 t152_l752
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v151_l746)))


(def
 v154_l759
 (->
  (rdatasets/datasets-iris)
  (sk/lay-summary :species :sepal-length)
  (sk/options {:title "Mean Sepal Length +/- SE by Species"})))


(deftest
 t155_l763
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v154_l759)))


(def
 v157_l768
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-rug
  (sk/options {:title "Iris: Scatter with Rug Marks"})))


(deftest
 t158_l774
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v157_l768)))


(def
 v160_l811
 (->
  (rdatasets/ggplot2-economics)
  (as-> econ (tc/select-rows econ (range 0 (tc/row-count econ) 12)))
  (sk/frame :unemploy :pce)
  sk/lay-line
  sk/lay-point
  (sk/options
   {:title "US Economy: Unemployment vs Personal Consumption",
    :x-label "Unemployed (thousands)",
    :y-label "Personal Consumption Expenditures"})))


(deftest
 t161_l820
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v160_l811)))


(def
 v163_l830
 (->
  (rdatasets/ggplot2-economics)
  (sk/frame :date :unemploy)
  sk/lay-step
  sk/lay-area
  (sk/options
   {:title "US Unemployment (Step Area)",
    :x-label "Date",
    :y-label "Unemployed (thousands)"})))


(deftest
 t164_l838
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:polygons s)))))
   v163_l830)))


(def
 v166_l847
 (->
  (rdatasets/ggplot2-economics)
  (sk/frame :date :psavert)
  sk/lay-area
  sk/lay-line
  (sk/options
   {:title "US Personal Savings Rate",
    :x-label "Date",
    :y-label "Savings Rate (%)"})))


(deftest
 t167_l855
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:lines s)))))
   v166_l847)))


(def
 v169_l862
 (->
  (rdatasets/ggplot2-txhousing)
  (tc/select-rows
   (fn*
    [p1__153715#]
    (#{"Houston" "Dallas" "San Antonio" "Austin"}
     (:city p1__153715#))))
  (sk/frame :date :median {:color :city})
  sk/lay-line
  (sk/options
   {:title "Texas Median Home Prices",
    :x-label "Date",
    :y-label "Median Price ($)"})))


(deftest
 t170_l870
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v169_l862)))


(def
 v172_l877
 (->
  (rdatasets/lme4-sleepstudy)
  (sk/frame
   :days
   :reaction
   {:color :subject, :color-type :categorical})
  sk/lay-line
  sk/lay-point
  (sk/options
   {:title "Sleep Deprivation: Reaction Time by Subject",
    :x-label "Days of Sleep Deprivation",
    :y-label "Reaction Time (ms)"})))


(deftest
 t173_l885
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (= 180 (:points s)))))
   v172_l877)))


(def
 v175_l892
 (->
  (rdatasets/lme4-sleepstudy)
  (tc/select-rows
   (fn* [p1__153716#] (= "308" (str (:subject p1__153716#)))))
  (sk/frame :days :reaction)
  sk/lay-step
  sk/lay-point
  (sk/options
   {:title "Subject 308: Reaction Time (Step)",
    :x-label "Days",
    :y-label "Reaction Time (ms)"})))


(deftest
 t176_l901
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v175_l892)))


(def
 v178_l911
 (->
  (rdatasets/datasets-faithful)
  (sk/frame :eruptions :waiting)
  sk/lay-point
  (sk/options
   {:title "Old Faithful Geyser",
    :x-label "Eruption Duration (min)",
    :y-label "Waiting Time (min)"})))


(deftest
 t179_l918
 (is ((fn [v] (= 272 (:points (sk/svg-summary v)))) v178_l911)))


(def
 v181_l923
 (->
  (rdatasets/datasets-faithful)
  (sk/frame :eruptions :waiting)
  sk/lay-point
  sk/lay-smooth
  (sk/options
   {:title "Old Faithful with LOESS",
    :x-label "Eruption Duration (min)",
    :y-label "Waiting Time (min)"})))


(deftest
 t182_l931
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 272 (:points s)) (pos? (:lines s)))))
   v181_l923)))


(def
 v184_l940
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/lay-point :carat :price {:alpha 0.05})
  (sk/options
   {:title "Diamond Price vs Carat (alpha = 0.05)",
    :x-label "Carat",
    :y-label "Price ($)"})))


(deftest
 t185_l946
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v184_l940)))


(def
 v187_l953
 (->
  (rdatasets/datasets-mtcars)
  (sk/lay-point :wt :mpg {:color :hp})
  (sk/options
   {:title "Cars: Color by Horsepower",
    :x-label "Weight (1000 lbs)",
    :y-label "Miles per Gallon"})))


(deftest
 t188_l959
 (is ((fn [v] (= 32 (:points (sk/svg-summary v)))) v187_l953)))


(def
 v190_l964
 (->
  (rdatasets/datasets-mtcars)
  (sk/lay-point :hp :mpg {:color :cyl, :size :disp})
  (sk/options
   {:title "Cars: Color by Cylinders, Size by Displacement",
    :x-label "Horsepower",
    :y-label "Miles per Gallon"})))


(deftest
 t191_l970
 (is ((fn [v] (= 32 (:points (sk/svg-summary v)))) v190_l964)))


(def
 v193_l975
 (->
  (tc/select-rows
   (rdatasets/gapminder-gapminder)
   (fn* [p1__153717#] (= 2007 (:year p1__153717#))))
  (sk/lay-point
   :gdp-percap
   :life-exp
   {:color :continent, :size :pop, :alpha 0.6})
  (sk/scale :x :log)
  (sk/options
   {:title "Gapminder 2007",
    :x-label "GDP per Capita (log)",
    :y-label "Life Expectancy"})))


(deftest
 t194_l982
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v193_l975)))


(def
 v196_l987
 (->
  (rdatasets/ggplot2-midwest)
  (sk/lay-point
   :percollege
   :percbelowpoverty
   {:color :state, :size :poptotal, :alpha 0.5})
  (sk/options
   {:title "Midwest: College Education vs Poverty",
    :x-label "Percent College Educated",
    :y-label "Percent Below Poverty"})))


(deftest
 t197_l993
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v196_l987)))


(def
 v199_l998
 (def
  msleep
  (tc/drop-missing
   (rdatasets/ggplot2-msleep)
   [:sleep-total :bodywt :brainwt :vore])))


(def
 v200_l1001
 (->
  msleep
  (sk/lay-point :bodywt :brainwt {:color :vore})
  (sk/scale :x :log)
  (sk/scale :y :log)
  (sk/options
   {:title "Mammal Body vs Brain Weight (log-log)",
    :x-label "Body Weight (kg, log)",
    :y-label "Brain Weight (kg, log)"})))


(deftest
 t201_l1009
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v200_l1001)))


(def
 v203_l1014
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :petal-length {:color :species})
  sk/lay-point
  (sk/coord :fixed)
  (sk/options
   {:title "Iris: Sepal vs Petal Length (1:1 Aspect)",
    :x-label "Sepal Length",
    :y-label "Petal Length"})))


(deftest
 t204_l1022
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v203_l1014)))


(def
 v206_l1027
 (->
  (rdatasets/datasets-mtcars)
  (tc/order-by [:mpg] :desc)
  (tc/select-rows (range 5))
  (sk/frame :wt :mpg)
  sk/lay-point
  (sk/lay-label {:text :rownames})
  (sk/options
   {:title "Top 5 Most Fuel Efficient Cars",
    :x-label "Weight (1000 lbs)",
    :y-label "Miles per Gallon"})))


(deftest
 t207_l1037
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (pos? (count (:texts s))))))
   v206_l1027)))


(def
 v209_l1044
 (->
  (rdatasets/datasets-iris)
  (sk/frame :petal-length :petal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  (sk/options
   {:title "Iris Petals with Linear Fit per Species",
    :x-label "Petal Length",
    :y-label "Petal Width"})))


(deftest
 t210_l1052
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v209_l1044)))


(def
 v212_l1059
 (->
  (rdatasets/datasets-mtcars)
  (sk/frame :wt :mpg)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model, :confidence-band true})
  (sk/options
   {:title "Weight vs MPG with 95% Confidence Band",
    :x-label "Weight (1000 lbs)",
    :y-label "Miles per Gallon"})))


(deftest
 t213_l1067
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 32 (:points s)) (pos? (:lines s)) (pos? (:polygons s)))))
   v212_l1059)))


(def
 v215_l1077
 (->
  (rdatasets/datasets-mtcars)
  (sk/frame :wt :mpg)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  sk/lay-smooth
  (sk/options
   {:title "Cars: LM and LOESS Smoothers",
    :x-label "Weight (1000 lbs)",
    :y-label "Miles per Gallon"})))


(deftest
 t216_l1086
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 32 (:points s)) (>= (:lines s) 2))))
   v215_l1077)))


(def
 v218_l1096
 (->
  (rdatasets/datasets-faithful)
  (sk/frame :eruptions)
  (sk/lay-histogram {:normalize :density, :binwidth 0.25})
  sk/lay-density
  (sk/options
   {:title "Old Faithful: Histogram + Density",
    :x-label "Eruption Duration (min)",
    :y-label "Density"})))


(deftest
 t219_l1104
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v218_l1096)))


(def
 v221_l1109
 (->
  (rdatasets/datasets-faithful)
  (sk/frame :eruptions)
  sk/lay-density
  sk/lay-rug
  (sk/options
   {:title "Old Faithful: Density with Rug",
    :x-label "Eruption Duration (min)",
    :y-label "Density"})))


(deftest
 t222_l1117
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:lines s)))))
   v221_l1109)))


(def
 v224_l1124
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/frame :depth)
  sk/lay-density
  (sk/options
   {:title "Distribution of Diamond Depth",
    :x-label "Depth (%)",
    :y-label "Density"})))


(deftest
 t225_l1131
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v224_l1124)))


(def
 v227_l1136
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/frame :depth)
  (sk/lay-histogram {:normalize :density})
  sk/lay-density
  (sk/options
   {:title "Diamond Depth: Histogram + Density",
    :x-label "Depth (%)",
    :y-label "Density"})))


(deftest
 t228_l1144
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v227_l1136)))


(def
 v230_l1149
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density :petal-width {:color :species})
  (sk/options
   {:title "Iris Petal Width by Species",
    :x-label "Petal Width (cm)",
    :y-label "Density"})))


(deftest
 t231_l1155
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v230_l1149)))


(def
 v233_l1160
 (->
  msleep
  (sk/lay-density :sleep-total {:color :vore})
  (sk/options
   {:title "Sleep Duration by Diet Type",
    :x-label "Total Sleep (hours)",
    :y-label "Density"})))


(deftest
 t234_l1166
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v233_l1160)))


(def
 v236_l1171
 (->
  (rdatasets/datasets-faithful)
  (sk/frame :waiting)
  (sk/lay-histogram {:bins 15})
  (sk/options
   {:title "Waiting Time Between Eruptions (15 bins)",
    :x-label "Waiting Time (min)",
    :y-label "Count"})))


(deftest
 t237_l1178
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v236_l1171)))


(def
 v239_l1183
 (->
  (tc/dataset
   {:value
    (repeatedly
     500
     (fn* [] (+ (* 2.0 (rand)) (* 2.0 (rand)) (* 2.0 (rand)) -3.0)))})
  (sk/frame :value)
  (sk/lay-histogram {:bins 30, :normalize :density})
  sk/lay-density
  (sk/options
   {:title "Simulated Distribution: Histogram + Density",
    :x-label "Value",
    :y-label "Density"})))


(deftest
 t240_l1191
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v239_l1183)))


(def
 v242_l1196
 (->
  (rdatasets/datasets-chickwts)
  (sk/frame :feed :weight {:color :feed})
  sk/lay-boxplot
  (sk/options
   {:title "Chick Weight by Feed Type",
    :x-label "Feed",
    :y-label "Weight (g)"})))


(deftest
 t243_l1203
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v242_l1196)))


(def
 v245_l1208
 (->
  (rdatasets/datasets-iris)
  (sk/frame :species :sepal-length {:color :species})
  sk/lay-boxplot
  (sk/coord :flip)
  (sk/options
   {:title "Iris Sepal Length (Horizontal Box)",
    :x-label "Species",
    :y-label "Sepal Length (cm)"})))


(deftest
 t246_l1216
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v245_l1208)))


(def
 v248_l1223
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day :total-bill {:color :sex})
  sk/lay-boxplot
  (sk/options
   {:title "Tips by Day and Gender (Grouped Boxplot)",
    :x-label "Day",
    :y-label "Total Bill ($)"})))


(deftest
 t249_l1230
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v248_l1223)))


(def
 v251_l1235
 (->
  (rdatasets/datasets-iris)
  (sk/frame :species :sepal-width {:color :species})
  sk/lay-violin
  (sk/options
   {:title "Iris Sepal Width (Violin)",
    :x-label "Species",
    :y-label "Sepal Width (cm)"})))


(deftest
 t252_l1242
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v251_l1235)))


(def
 v254_l1247
 (->
  (rdatasets/datasets-iris)
  (sk/frame :species :petal-width {:color :species})
  sk/lay-violin
  (sk/coord :flip)
  (sk/options
   {:title "Iris Petal Width (Horizontal Violin)",
    :x-label "Species",
    :y-label "Petal Width (cm)"})))


(deftest
 t255_l1255
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v254_l1247)))


(def
 v257_l1263
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day :total-bill)
  sk/lay-violin
  sk/lay-point
  (sk/options
   {:title "Tips: Violin with Individual Points",
    :x-label "Day",
    :y-label "Total Bill ($)"})))


(deftest
 t258_l1271
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:polygons s)) (= 244 (:points s)))))
   v257_l1263)))


(def
 v260_l1280
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day :total-bill)
  sk/lay-violin
  sk/lay-boxplot
  sk/lay-point
  (sk/options
   {:title "Tips: Violin + Boxplot + Points",
    :x-label "Day",
    :y-label "Total Bill ($)"})))


(deftest
 t261_l1289
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:points s)))))
   v260_l1280)))


(def
 v263_l1296
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :smoker :total-bill {:color :smoker})
  sk/lay-violin
  (sk/options
   {:title "Total Bill by Smoking Status",
    :x-label "Smoker",
    :y-label "Total Bill ($)"})))


(deftest
 t264_l1303
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v263_l1296)))


(def
 v266_l1308
 (->
  (rdatasets/datasets-iris)
  (sk/frame :species :petal-length)
  sk/lay-ridgeline
  (sk/options
   {:title "Iris Petal Length by Species (Ridgeline)",
    :x-label "Species",
    :y-label "Petal Length (cm)"})))


(deftest
 t267_l1315
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v266_l1308)))


(def
 v269_l1320
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/frame :color :price)
  sk/lay-ridgeline
  (sk/options
   {:title "Diamond Price by Color Grade (Ridgeline)",
    :x-label "Color",
    :y-label "Price ($)"})))


(deftest
 t270_l1327
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v269_l1320)))


(def
 v272_l1332
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
       [p1__153718#]
       (get {5 "May", 6 "Jun", 7 "Jul", 8 "Aug", 9 "Sep"} p1__153718#))
      (ds :month)))))))


(def
 v273_l1339
 (->
  airquality
  (sk/frame :month-name :ozone {:color :month-name})
  sk/lay-boxplot
  (sk/options
   {:title "New York Ozone by Month",
    :x-label "Month",
    :y-label "Ozone (ppb)"})))


(deftest
 t274_l1346
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v273_l1339)))


(def
 v276_l1354
 (->
  (rdatasets/ggplot2-mpg)
  (sk/frame :class)
  sk/lay-bar
  (sk/options
   {:title "Vehicle Count by Class",
    :x-label "Class",
    :y-label "Count"})))


(deftest
 t277_l1361
 (is ((fn [v] (= 7 (:polygons (sk/svg-summary v)))) v276_l1354)))


(def
 v279_l1366
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-bar :day {:color :sex})
  (sk/options
   {:title "Tips Count by Day and Gender",
    :x-label "Day",
    :y-label "Count"})))


(deftest
 t280_l1372
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v279_l1366)))


(def
 v282_l1377
 (->
  (tc/dataset
   {:country ["US" "China" "Japan" "Germany" "UK" "India" "France"],
    :gdp [21.4 14.7 5.1 3.8 2.8 2.7 2.6]})
  (sk/lay-value-bar :country :gdp)
  (sk/coord :flip)
  (sk/options
   {:title "GDP by Country (2019)",
    :x-label "Country",
    :y-label "GDP (Trillion $)"})))


(deftest
 t283_l1385
 (is ((fn [v] (= 7 (:polygons (sk/svg-summary v)))) v282_l1377)))


(def
 v285_l1392
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
  (sk/lay-value-bar :metric :score)
  (sk/lay-rule-h {:y-intercept 0})
  (sk/coord :flip)
  (sk/options
   {:title "Customer Satisfaction Scores",
    :x-label "Metric",
    :y-label "Net Score"})))


(deftest
 t286_l1401
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 8 (:polygons s)) (pos? (:lines s)))))
   v285_l1392)))


(def
 v288_l1408
 (->
  (rdatasets/datasets-chickwts)
  (tc/group-by [:feed])
  (tc/aggregate {:mean-weight (fn [ds] (dfn/mean (ds :weight)))})
  (sk/lay-lollipop :feed :mean-weight)
  (sk/coord :flip)
  (sk/options
   {:title "Mean Chick Weight by Feed Type",
    :x-label "Feed",
    :y-label "Mean Weight (g)"})))


(deftest
 t289_l1417
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v288_l1408)))


(def
 v291_l1424
 (->
  (rdatasets/datasets-iris)
  (tc/group-by [:species])
  (tc/aggregate {:mean-sl (fn [ds] (fstats/mean (ds :sepal-length)))})
  (sk/lay-lollipop :species :mean-sl)
  (sk/coord :flip)
  (sk/options
   {:title "Mean Sepal Length by Species",
    :x-label "Species",
    :y-label "Mean Sepal Length (cm)"})))


(deftest
 t292_l1433
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (pos? (:lines s)))))
   v291_l1424)))


(def
 v294_l1443
 (->
  (rdatasets/datasets-faithful)
  (sk/frame :eruptions :waiting)
  sk/lay-density-2d
  (sk/options
   {:title "Old Faithful: 2D Density",
    :x-label "Eruption Duration (min)",
    :y-label "Waiting Time (min)"})))


(deftest
 t295_l1450
 (is ((fn [v] (pos? (:visible-tiles (sk/svg-summary v)))) v294_l1443)))


(def
 v297_l1455
 (->
  (rdatasets/datasets-faithful)
  (sk/frame :eruptions :waiting)
  sk/lay-point
  sk/lay-density-2d
  (sk/options
   {:title "Old Faithful: Scatter + Density",
    :x-label "Eruption Duration (min)",
    :y-label "Waiting Time (min)"})))


(deftest
 t298_l1463
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 272 (:points s)) (pos? (:visible-tiles s)))))
   v297_l1455)))


(def
 v300_l1470
 (->
  (rdatasets/datasets-faithful)
  (sk/frame :eruptions :waiting)
  sk/lay-point
  sk/lay-contour
  (sk/options
   {:title "Old Faithful: Scatter + Contour",
    :x-label "Eruption Duration (min)",
    :y-label "Waiting Time (min)"})))


(deftest
 t301_l1478
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 272 (:points s)) (pos? (:lines s)))))
   v300_l1470)))


(def
 v303_l1485
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :petal-length)
  sk/lay-contour
  (sk/options
   {:title "Iris: Sepal vs Petal Length Contour",
    :x-label "Sepal Length",
    :y-label "Petal Length"})))


(deftest
 t304_l1492
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v303_l1485)))


(def
 v306_l1499
 (->
  (rdatasets/ggplot2-faithfuld)
  (sk/frame :eruptions :waiting {:fill :density})
  sk/lay-tile
  (sk/options
   {:title "Old Faithful: Pre-computed Density Heatmap",
    :x-label "Eruption Duration",
    :y-label "Waiting Time"})))


(deftest
 t307_l1506
 (is ((fn [v] (pos? (:visible-tiles (sk/svg-summary v)))) v306_l1499)))


(def
 v309_l1511
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 3000)
  (sk/frame :carat :price)
  sk/lay-point
  sk/lay-density-2d
  (sk/options
   {:title "Diamonds: Scatter + 2D Density",
    :x-label "Carat",
    :y-label "Price ($)"})))


(deftest
 t310_l1520
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:visible-tiles s)))))
   v309_l1511)))


(def
 v312_l1527
 (->
  (rdatasets/ggplot2-mpg)
  (sk/frame :displ :hwy)
  sk/lay-density-2d
  (sk/options
   {:title "MPG: Displacement vs Highway (Density)",
    :x-label "Displacement (L)",
    :y-label "Highway MPG"})))


(deftest
 t313_l1534
 (is ((fn [v] (pos? (:visible-tiles (sk/svg-summary v)))) v312_l1527)))


(def
 v315_l1539
 (->
  (tc/dataset
   {:row (mapcat (fn* [p1__153719#] (repeat 6 p1__153719#)) (range 6)),
    :col (flatten (repeat 6 (range 6))),
    :value
    (map
     (fn* [p1__153720#] (Math/sin (* p1__153720# 0.5)))
     (range 36))})
  (sk/frame :col :row {:fill :value})
  sk/lay-tile
  (sk/options
   {:title "Synthetic Heatmap (sin wave)",
    :x-label "Column",
    :y-label "Row"})))


(deftest
 t316_l1548
 (is ((fn [v] (pos? (:visible-tiles (sk/svg-summary v)))) v315_l1539)))


(def
 v318_l1556
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
  (sk/lay-errorbar :species :mean {:y-min :y-min, :y-max :y-max})
  (sk/lay-point :species :mean)
  (sk/options
   {:title "Mean Sepal Length +/- SD by Species",
    :x-label "Species",
    :y-label "Sepal Length (cm)"})))


(deftest
 t319_l1569
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (pos? (:lines s)))))
   v318_l1556)))


(def
 v321_l1576
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-summary :day :tip {:color :sex})
  (sk/options
   {:title "Mean Tip +/- SE by Day and Gender",
    :x-label "Day",
    :y-label "Tip ($)"})))


(deftest
 t322_l1582
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v321_l1576)))


(def
 v324_l1592
 (->
  (rdatasets/ggplot2-economics)
  (sk/lay-line :date :unemploy)
  (sk/lay-line :date :uempmed)
  (sk/options
   {:title "Unemployment: Total vs Median Duration",
    :x-label "Date",
    :y-label "Value"})))


(deftest
 t325_l1599
 (is ((fn [v] (>= (:lines (sk/svg-summary v)) 2)) v324_l1592)))


(def
 v327_l1604
 (->
  (rdatasets/ggplot2-economics)
  (sk/lay-line :date :unemploy)
  (sk/lay-line :date :uempmed)
  (sk/lay-line :date :psavert)
  (sk/options
   {:title "US Economic Indicators (Three Series)",
    :x-label "Date",
    :y-label "Value"})))


(deftest
 t328_l1612
 (is ((fn [v] (>= (:lines (sk/svg-summary v)) 3)) v327_l1604)))


(def
 v330_l1619
 (->
  (rdatasets/ggplot2-mpg)
  (sk/lay-point :displ :hwy)
  (sk/lay-line :displ :cty)
  (sk/options
   {:title "MPG: Highway (points) vs City (line)",
    :x-label "Displacement (L)",
    :y-label "MPG"})))


(deftest
 t331_l1626
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v330_l1619)))


(def
 v333_l1636
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/lay-rule-h {:y-intercept 3.0})
  (sk/lay-rule-h {:y-intercept 4.0})
  (sk/lay-rule-v {:x-intercept 5.0})
  (sk/lay-rule-v {:x-intercept 7.0})
  (sk/options
   {:title "Iris: Scatter with Grid Lines",
    :x-label "Sepal Length",
    :y-label "Sepal Width"})))


(deftest
 t334_l1647
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:lines s) 4))))
   v333_l1636)))


(def
 v336_l1654
 (->
  (rdatasets/datasets-mtcars)
  (sk/frame :wt :mpg)
  sk/lay-point
  (sk/lay-band-h {:y-min 20, :y-max 30})
  (sk/lay-band-v {:x-min 2.5, :x-max 3.5})
  (sk/options
   {:title "Cars: Scatter with Highlight Bands",
    :x-label "Weight (1000 lbs)",
    :y-label "MPG"})))


(deftest
 t337_l1663
 (is ((fn [v] (= 32 (:points (sk/svg-summary v)))) v336_l1654)))


(def
 v339_l1668
 (->
  (rdatasets/ggplot2-economics)
  (sk/frame :date :unemploy)
  sk/lay-area
  (sk/lay-rule-h {:y-intercept 8000})
  (sk/options
   {:title "US Unemployment with 8000 Threshold",
    :x-label "Date",
    :y-label "Unemployed (thousands)"})))


(deftest
 t340_l1676
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:lines s)))))
   v339_l1668)))


(def
 v342_l1683
 (->
  airquality
  (sk/lay-line :rownames :ozone)
  (sk/lay-rule-h {:y-intercept 60})
  (sk/options
   {:title "NYC Ozone with Threshold at 60 ppb",
    :x-label "Observation",
    :y-label "Ozone (ppb)"})))


(deftest
 t343_l1690
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (and (pos? (:lines s)))))
   v342_l1683)))


(def
 v345_l1696
 (->
  airquality
  (sk/frame :wind :ozone)
  sk/lay-point
  (sk/lay-band-h {:y-min 0, :y-max 40})
  (sk/options
   {:title "Ozone vs Wind: Safe Zone Highlighted",
    :x-label "Wind Speed (mph)",
    :y-label "Ozone (ppb)"})))


(deftest
 t346_l1704
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v345_l1696)))


(def
 v348_l1712
 (->
  (rdatasets/ggplot2-mpg)
  (sk/frame :displ :hwy)
  sk/lay-point
  (sk/facet :class)
  (sk/options
   {:title "MPG: Faceted by Vehicle Class",
    :x-label "Displacement",
    :y-label "Highway MPG"})))


(deftest
 t349_l1720
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v348_l1712)))


(def
 v351_l1725
 (->
  (rdatasets/ggplot2-mpg)
  (sk/frame :displ :hwy)
  sk/lay-point
  (sk/facet-grid :drv :year)
  (sk/options
   {:title "MPG: Drive Type x Model Year",
    :x-label "Displacement",
    :y-label "Highway MPG"})))


(deftest
 t352_l1733
 (is ((fn [v] (= 6 (:panels (sk/svg-summary v)))) v351_l1725)))


(def
 v354_l1742
 (->
  (rdatasets/ggplot2-mpg)
  (sk/frame :displ :hwy)
  sk/lay-point
  (sk/facet-grid :drv :class)
  (sk/options
   {:title "MPG: Drive x Class",
    :x-label "Displacement",
    :y-label "Highway MPG"})))


(deftest
 t355_l1750
 (is ((fn [v] (pos? (:panels (sk/svg-summary v)))) v354_l1742)))


(def
 v357_l1755
 (->
  (rdatasets/ggplot2-mpg)
  (sk/frame :displ :hwy)
  sk/lay-point
  (sk/facet-grid nil :drv)
  (sk/options
   {:title "MPG: Column Facets by Drive Type",
    :x-label "Displacement",
    :y-label "Highway MPG"})))


(deftest
 t358_l1763
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v357_l1755)))


(def
 v360_l1768
 (->
  (rdatasets/datasets-iris)
  (sk/frame :petal-length)
  sk/lay-density
  (sk/facet :species)
  (sk/options
   {:title "Petal Length Density by Species",
    :x-label "Petal Length (cm)",
    :y-label "Density"})))


(deftest
 t361_l1776
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v360_l1768)))


(def
 v363_l1781
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day :total-bill)
  sk/lay-boxplot
  (sk/facet :sex)
  (sk/options
   {:title "Total Bill by Day, Faceted by Gender",
    :x-label "Day",
    :y-label "Total Bill ($)"})))


(deftest
 t364_l1789
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:polygons s)) (= 2 (:panels s)))))
   v363_l1781)))


(def
 v366_l1796
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day :total-bill)
  sk/lay-violin
  (sk/facet :sex)
  (sk/options
   {:title "Total Bill Violin by Day, Faceted by Gender",
    :x-label "Day",
    :y-label "Total Bill ($)"})))


(deftest
 t367_l1804
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:polygons s)) (= 2 (:panels s)))))
   v366_l1796)))


(def
 v369_l1811
 (->
  (rdatasets/ggplot2-mpg)
  (sk/frame :class)
  sk/lay-bar
  (sk/facet :year)
  (sk/options
   {:title "Vehicle Class Count by Model Year",
    :x-label "Class",
    :y-label "Count"})))


(deftest
 t370_l1819
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v369_l1811)))


(def
 v372_l1824
 (->
  (rdatasets/datasets-iris)
  (sk/frame :petal-length :petal-width)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  (sk/facet :species)
  (sk/options
   {:title "Iris Petals: Faceted Regression",
    :x-label "Petal Length",
    :y-label "Petal Width"})))


(deftest
 t373_l1833
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (= 3 (:lines s)) (= 3 (:panels s)))))
   v372_l1824)))


(def
 v375_l1841
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day :total-bill)
  sk/lay-boxplot
  (sk/facet-grid :time :smoker)
  (sk/options
   {:title "Tips: Day x Time x Smoker",
    :x-label "Day",
    :y-label "Total Bill ($)"})))


(deftest
 t376_l1849
 (is ((fn [v] (= 4 (:panels (sk/svg-summary v)))) v375_l1841)))


(def
 v378_l1854
 (->
  (tc/select-rows
   (rdatasets/gapminder-gapminder)
   (fn* [p1__153721#] (= 2007 (:year p1__153721#))))
  (sk/frame :gdp-percap :life-exp)
  sk/lay-point
  (sk/scale :x :log)
  (sk/facet :continent)
  (sk/options
   {:title "Gapminder 2007 by Continent",
    :x-label "GDP per Capita (log)",
    :y-label "Life Expectancy"})))


(deftest
 t379_l1863
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v378_l1854)))


(def
 v381_l1868
 (->
  (rdatasets/lme4-sleepstudy)
  (sk/frame :days :reaction)
  sk/lay-line
  sk/lay-point
  (sk/facet :subject)
  (sk/options
   {:title "Sleep Study: Each Subject",
    :x-label "Days",
    :y-label "Reaction Time (ms)"})))


(deftest
 t382_l1877
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (= 180 (:points s)))))
   v381_l1868)))


(def
 v384_l1884
 (->
  (rdatasets/ggplot2-mpg)
  (sk/frame :displ :hwy)
  sk/lay-point
  sk/lay-smooth
  (sk/facet :cyl)
  (sk/options
   {:title "MPG: Scatter + LOESS by Cylinder Count",
    :x-label "Displacement",
    :y-label "Highway MPG"})))


(deftest
 t385_l1893
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v384_l1884)))


(def
 v387_l1903
 (->
  (rdatasets/datasets-mtcars)
  (sk/frame (sk/cross [:mpg :hp :wt] [:mpg :hp :wt]))
  (sk/options {:title "Motor Trend Cars: 3x3 SPLOM"})))


(deftest
 t388_l1907
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v387_l1903)))


(def
 v390_l1912
 (->
  (rdatasets/datasets-mtcars)
  (sk/frame (sk/cross [:mpg :wt] [:mpg :wt]))
  (sk/options {:title "MPG vs Weight: 2x2 SPLOM"})))


(deftest
 t391_l1916
 (is ((fn [v] (= 4 (:panels (sk/svg-summary v)))) v390_l1912)))


(def
 v393_l1924
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day)
  sk/lay-bar
  (sk/coord :polar)
  (sk/options {:title "Tips Count by Day (Rose)"})))


(deftest
 t394_l1930
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v393_l1924)))


(def
 v396_l1935
 (->
  (rdatasets/datasets-chickwts)
  (sk/frame :feed)
  sk/lay-bar
  (sk/coord :polar)
  (sk/options {:title "Chick Count by Feed (Rose)"})))


(deftest
 t397_l1941
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v396_l1935)))


(def
 v399_l1946
 (->
  (tc/dataset
   {:day ["Mon" "Tue" "Wed" "Thu" "Fri" "Sat" "Sun"],
    :hours [8 7 6 9 5 3 4]})
  (sk/lay-value-bar :day :hours)
  (sk/coord :polar)
  (sk/options {:title "Weekly Working Hours (Polar)"})))


(deftest
 t400_l1952
 (is ((fn [v] (= 7 (:polygons (sk/svg-summary v)))) v399_l1946)))


(def
 v402_l1960
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 2000)
  (sk/lay-point :carat :price {:alpha 0.15})
  (sk/scale :y :log)
  (sk/options
   {:title "Diamond Price (Log Scale)",
    :x-label "Carat",
    :y-label "Price ($, log)"})))


(deftest
 t403_l1968
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v402_l1960)))


(def
 v405_l1973
 (->
  msleep
  (sk/lay-point :bodywt :sleep-total {:color :vore})
  (sk/scale :x :log)
  (sk/options
   {:title "Body Weight vs Sleep (log x-axis)",
    :x-label "Body Weight (kg, log)",
    :y-label "Total Sleep (hours)"})))


(deftest
 t406_l1980
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v405_l1973)))


(def
 v408_l1991
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day {:color :time})
  (sk/lay-bar {:position :stack})
  (sk/options
   {:title "Tips by Day and Meal Time (Stacked)",
    :x-label "Day",
    :y-label "Count"})))


(deftest t409_l1998 (is ((fn [v] (sk/frame? v)) v408_l1991)))


(def
 v411_l2005
 (->
  (rdatasets/reshape2-tips)
  (sk/frame :day :total-bill)
  sk/lay-bar
  sk/lay-point
  (sk/options
   {:title "Tips: Bar Count with Individual Points",
    :x-label "Day",
    :y-label "Total Bill ($)"})))


(deftest
 t412_l2013
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:points s)))))
   v411_l2005)))


(def
 v414_l2025
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-density-2d
  (sk/options
   {:title "Iris: 2D Density by Species",
    :x-label "Sepal Length",
    :y-label "Sepal Width"})))


(deftest
 t415_l2032
 (is ((fn [v] (pos? (:visible-tiles (sk/svg-summary v)))) v414_l2025)))


(def
 v417_l2037
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 1000)
  (sk/frame :carat :price)
  sk/lay-contour
  sk/lay-point
  (sk/options
   {:title "Diamonds: Contour + Scatter",
    :x-label "Carat",
    :y-label "Price ($)"})))


(deftest
 t418_l2046
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v417_l2037)))
