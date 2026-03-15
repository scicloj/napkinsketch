;; # API Reference
;;
;; Complete reference for every public function in
;; `scicloj.napkinsketch.api`.
;;
;; Each entry shows the docstring, a live example, and a test.

^{:kindly/hide-code true
  :kindly/options {:kinds-that-hide-code #{:kind/doc}}}
(ns napkinsketch-book.api-reference
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]
   [fastmath.random :as rng]))

;; ## Sample Data

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(def tiny (tc/dataset {:x [1 2 3 4 5]
                       :y [2 4 1 5 3]
                       :group [:a :a :b :b :b]}))

(def sales (tc/dataset {:product [:widget :gadget :gizmo :doohickey]
                        :revenue [120 340 210 95]}))

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                      {:key-fn keyword}))

(def measurements (tc/dataset {:treatment ["A" "B" "C" "D"]
                               :mean [10.0 15.0 12.0 18.0]
                               :ci_lo [8.0 12.0 9.5 15.5]
                               :ci_hi [12.0 18.0 14.5 20.5]}))

;; ## Data Setup

(kind/doc #'sk/view)

;; Single scatter view — two columns as `[x y]`:

(-> iris (sk/view [[:sepal_length :sepal_width]]) (sk/lay (sk/point)) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Histogram view — a single keyword means x = y (diagonal):

(-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Multiple views — a vector of `[x y]` pairs:

(-> iris
    (sk/view [[:sepal_length :sepal_width]
              [:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Map form — explicit keys:

(-> (sk/view iris {:x :sepal_length :y :sepal_width})
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/lay)

;; Apply one mark:

(-> iris (sk/view [[:sepal_length :sepal_width]]) (sk/lay (sk/point)) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Apply multiple marks — scatter with regression:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Marks

(kind/doc #'sk/point)

;; Default scatter:

(sk/plot [(sk/point {:data tiny :x :x :y :y})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; Color by column:

(sk/plot [(sk/point {:data tiny :x :x :y :y :color :group})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; Fixed color:

(sk/plot [(sk/point {:data tiny :x :x :y :y :color "#E74C3C"})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; Size by column:

(sk/plot [(sk/point {:data iris :x :sepal_length :y :sepal_width
                     :size :petal_length :color :species})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Constant size — uniform radius for all points:

(sk/plot [(sk/point {:data tiny :x :x :y :y :size 6})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; Constant alpha — uniform transparency:

(sk/plot [(sk/point {:data tiny :x :x :y :y :alpha 0.3})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; Combined — large, semi-transparent, colored points:

(sk/plot [(sk/point {:data iris :x :sepal_length :y :sepal_width
                     :color :species :alpha 0.5 :size 5})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Alpha by column — transparency varies with petal length:

(sk/plot [(sk/point {:data iris :x :sepal_length :y :sepal_width
                     :color :species :alpha :petal_length})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/line)

;; Connected line through data points:

(def wave (tc/dataset {:x (range 30)
                       :y (mapv #(Math/sin (* % 0.3)) (range 30))}))

(sk/plot [(sk/line {:data wave :x :x :y :y})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:lines s)))))])

;; Grouped lines:

(def waves (tc/dataset {:x (vec (concat (range 30) (range 30)))
                        :y (vec (concat (mapv #(Math/sin (* % 0.3)) (range 30))
                                        (mapv #(Math/cos (* % 0.3)) (range 30))))
                        :fn (vec (concat (repeat 30 :sin) (repeat 30 :cos)))}))

(sk/plot [(sk/line {:data waves :x :x :y :y :color :fn})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 2 (:lines s)))))])

;; Thick line — constant stroke width:

(sk/plot [(sk/line {:data wave :x :x :y :y :size 4})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:lines s)))))])

(kind/doc #'sk/histogram)

;; Default binning:

(-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Colored histogram — one set of bins per group:

(-> iris (sk/view :sepal_length) (sk/lay (sk/histogram {:color :species})) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

(kind/doc #'sk/bar)

;; Count occurrences of a categorical column:

(-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

;; Grouped (dodged) bars:

(-> iris (sk/view :species) (sk/lay (sk/bar {:color :species})) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Semi-transparent bars:

(-> iris (sk/view :species) (sk/lay (sk/bar {:alpha 0.4})) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

(kind/doc #'sk/stacked-bar)

;; Stacked categorical bars:

(def penguins (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
                          {:key-fn keyword}))

(-> penguins (sk/view :island) (sk/lay (sk/stacked-bar {:color :species})) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])


(kind/doc #'sk/stacked-bar-fill)

;; 100% stacked bars — shows proportions instead of counts:

(-> penguins (sk/view :island) (sk/lay (sk/stacked-bar-fill {:color :species})) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

(kind/doc #'sk/value-bar)

;; Categorical x, numeric y — no counting:

(sk/plot [(sk/value-bar {:data sales :x :product :y :revenue})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:polygons s)))))])

(kind/doc #'sk/lm)

;; Single regression line:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/lm))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Per-group regression:

(-> iris
    (sk/view [[:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

(kind/doc #'sk/loess)

;; Smooth LOESS curve through data:

(def noisy-wave (let [r (rng/rng :jdk 42)]
                  (tc/dataset {:x (range 50)
                               :y (mapv #(+ (Math/sin (* % 0.2)) (* 0.3 (- (rng/drandom r) 0.5)))
                                        (range 50))})))

(-> noisy-wave
    (sk/view [[:x :y]])
    (sk/lay (sk/point) (sk/loess))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'sk/density)

;; KDE density curve — a smooth alternative to histograms:

(-> iris
    (sk/view [[:sepal_length]])
    (sk/lay (sk/density))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:polygons s)))))])

;; Per-group density:

(-> iris
    (sk/view [[:sepal_length]])
    (sk/lay (sk/density {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

;; Custom bandwidth:

(-> iris
    (sk/view [[:sepal_length]])
    (sk/lay (sk/density {:bandwidth 0.3}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:polygons s)))))])

(kind/doc #'sk/area)

;; Filled area under a line:

(-> (tc/dataset {:x (range 30)
                 :y (mapv #(Math/sin (* % 0.3)) (range 30))})
    (sk/view [[:x :y]])
    (sk/lay (sk/area))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:polygons s)))))])

(kind/doc #'sk/stacked-area)

;; Stacked areas — each group fills above the previous:

(-> (tc/dataset {:x (vec (concat (range 10) (range 10) (range 10)))
                 :y (vec (concat [1 2 3 4 5 4 3 2 1 0]
                                 [2 2 2 3 3 3 2 2 2 2]
                                 [1 1 1 1 2 2 2 1 1 1]))
                 :group (vec (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C")))})
    (sk/view [[:x :y]])
    (sk/lay (sk/stacked-area {:color :group}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

(kind/doc #'sk/text)

;; Data-driven text labels at point positions:

(-> (tc/dataset {:x [1 2 3 4] :y [4 7 5 8] :name ["A" "B" "C" "D"]})
    (sk/view [[:x :y]])
    (sk/lay (sk/text {:text :name}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (every? (set (:texts s)) ["A" "B" "C" "D"]))))])

;; Combine text with points for labeled scatter:

(-> (tc/dataset {:x [1 2 3 4] :y [4 7 5 8] :name ["A" "B" "C" "D"]})
    (sk/view [[:x :y]])
    (sk/lay (sk/point) (sk/text {:text :name}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:points s)))))])

(kind/doc #'sk/boxplot)

;; Boxplot — median, quartiles, whiskers, and outliers:

(-> iris
    (sk/view [[:species :sepal_width]])
    (sk/lay (sk/boxplot))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s))
                                (pos? (:lines s)))))])

;; Grouped boxplot with color:

(-> tips
    (sk/view [[:day :total_bill]])
    (sk/lay (sk/boxplot {:color :smoker}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 8 (:polygons s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/violin)

;; Violin — mirrored density curve per category:

(-> tips
    (sk/view [[:day :total_bill]])
    (sk/lay (sk/violin))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:polygons s)))))])

;; Grouped violins with color:

(-> tips
    (sk/view [[:day :total_bill]])
    (sk/lay (sk/violin {:color :smoker}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 8 (:polygons s)))))])

(kind/doc #'sk/errorbar)

;; Error bars — pre-computed confidence intervals:

(-> measurements
    (sk/view [[:treatment :mean]])
    (sk/lay (sk/point)
            (sk/errorbar {:ymin :ci_lo :ymax :ci_hi}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 12 (:lines s)))))])

(kind/doc #'sk/lollipop)

;; Lollipop — stem + dot (lighter alternative to bar chart):

(-> sales
    (sk/view [[:product :revenue]])
    (sk/lay (sk/lollipop))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 4 (:lines s)))))])

;; Horizontal lollipop:

(-> sales
    (sk/view [[:product :revenue]])
    (sk/lay (sk/lollipop))
    (sk/coord :flip)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 4 (:lines s)))))])


(kind/doc #'sk/tile)

;; Auto-binned heatmap — bin x and y into a grid, count points per cell:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/tile))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:tiles s)))))])

;; Pre-computed fill — use a numeric column for tile color:

(def grid-data
  (tc/dataset {:x (for [i (range 5) j (range 5)] i)
               :y (for [i (range 5) j (range 5)] j)
               :value (vec (repeatedly 25 #(rand-int 100)))}))

(sk/plot [(sk/tile {:data grid-data :x :x :y :y :fill :value})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:tiles s)))))])

(kind/doc #'sk/density2d)

;; KDE-smoothed 2D density heatmap — smoother alternative to binned tiles:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/density2d))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:tiles s)))))])

;; Overlay density with scatter points:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/density2d))
    (sk/lay (sk/point {:alpha 0.5}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:tiles s)))))])


(kind/doc #'sk/contour)

;; Iso-density contour lines from 2D KDE — shows density structure:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/contour))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:lines s)))))])

;; Contour lines overlaid on scatter points:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:alpha 0.3}) (sk/contour {:levels 8}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])
(kind/doc #'sk/ridgeline)

;; Stacked density curves per category — good for comparing distributions:

(-> iris
    (sk/view [[:species :sepal_length]])
    (sk/lay (sk/ridgeline))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

(kind/doc #'sk/rug)

;; Tick marks along axis margins showing individual observations:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}) (sk/rug {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 150 (:lines s)))))])

;; Rug ticks on both axes:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/rug {:side :both}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 300 (:lines s))))])



(kind/doc #'sk/step)

;; Step lines connect points with horizontal-then-vertical segments:

(-> tiny
    (sk/view [[:x :y]])
    (sk/lay (sk/step) (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 1 (:lines s)))))])

;; Step lines by group:

(-> tiny
    (sk/view [[:x :y]])
    (sk/lay (sk/step {:color :group}) (sk/point {:color :group}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 2 (:lines s)))))])

(kind/doc #'sk/summary)

;; Mean ± SE per category:

(-> iris
    (sk/view [[:species :sepal_length]])
    (sk/lay (sk/summary))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:points s))
                                (= 3 (:lines s)))))])

;; Summary by group:

(-> iris
    (sk/view [[:species :sepal_length]])
    (sk/lay (sk/summary {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:points s))
                                (= 3 (:lines s)))))])

;; ## Rendering

(kind/doc #'sk/plot)

;; Default rendering:

(sk/plot [(sk/point {:data tiny :x :x :y :y})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; With options — title, labels, dimensions:

(sk/plot [(sk/point {:data iris :x :sepal_length :y :sepal_width :color :species})]
         {:title "Iris Scatter"
          :x-label "Sepal Length (cm)"
          :y-label "Sepal Width (cm)"
          :width 800
          :height 300})

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (>= (:width s) 800))))])

;; Direct mark styling — `:alpha` and `:size`:

(sk/plot [(sk/point {:data iris :x :sepal_length :y :sepal_width
                     :alpha 0.5 :size 4})])

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])


;; Tooltip on hover — shows data values on mouseover:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:tooltip true}))

(kind/test-last [(fn [v] (= :div (first v)))])

;; Brush selection — drag to highlight, click to reset:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:brush true}))

(kind/test-last [(fn [v] (= :div (first v)))])

;; Theme — customize background, grid color, font size:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:title "White Theme"
              :theme {:bg "#FFFFFF" :grid "#EEEEEE" :font-size 10}}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; Legend position — `:right` (default), `:bottom`, `:top`, `:none`:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:legend-position :bottom}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (< (:width s) 700))))])

(kind/doc #'sk/sketch)

;; Returns the intermediate data structure instead of SVG.
;; Same arguments as `plot`:

(def sk1 (sk/sketch [(sk/point {:data tiny :x :x :y :y})]))

(select-keys sk1 [:width :height :x-label :y-label :title])

(kind/test-last [(fn [m] (and (= 600 (:width m))
                              (= "x" (:x-label m))))])

;; The sketch contains panels with domains and layers:

(let [panel (first (:panels sk1))]
  {:x-domain (:x-domain panel)
   :y-domain (:y-domain panel)
   :n-layers (count (:layers panel))
   :mark (:mark (first (:layers panel)))})

(kind/test-last [(fn [m] (and (= 1 (:n-layers m))
                              (= :point (:mark m))))])

;; Sketches are plain serializable maps — useful for debugging
;; and testing. See the *Exploring Sketches* chapter for a full walkthrough.

;; ## Pipeline

(kind/doc #'sk/views->sketch)

;; Same as `sk/sketch` but with an explicit pipeline-style name:

(def sk2 (sk/views->sketch [(sk/point {:data tiny :x :x :y :y})]))

(= (keys sk1) (keys sk2))

(kind/test-last [true?])

(kind/doc #'sk/sketch->membrane)

;; Convert a sketch to a membrane drawable tree:

(def m1 (sk/sketch->membrane sk1))

(vector? m1)

(kind/test-last [true?])

(kind/doc #'sk/membrane->figure)

;; Convert a membrane tree to a figure:

(first (sk/membrane->figure m1 :svg
                             {:total-width (:total-width sk1)
                              :total-height (:total-height sk1)}))

(kind/test-last [(fn [v] (= :svg v))])

(kind/doc #'sk/sketch->figure)

;; Convert a sketch directly to a figure (orchestrates the full path):

(first (sk/sketch->figure sk1 :svg {}))

(kind/test-last [(fn [v] (= :svg v))])


;; ## Transforms

(kind/doc #'sk/coord)

;; Flip axes — horizontal bar chart:

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    (sk/coord :flip)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))

;; Polar coordinates — rose chart (bar chart in polar space):

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

                                (= 3 (:polygons s)))))])

(kind/doc #'sk/scale)

;; Log scale on x-axis:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point))
    (sk/scale :x :log)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Fixed domain:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point))
    (sk/scale :x {:domain [3 9]})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Custom axis label via scale:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point))
    (sk/scale :x {:label "Length (cm)"})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/labs)

;; Set title and axis labels:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/labs {:title "Iris Dimensions" :x "Sepal Length (cm)" :y "Sepal Width (cm)"})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (some #{"Iris Dimensions"} (:texts s))
                                (some #{"Sepal Length (cm)"} (:texts s)))))])

;; ## Annotations

(kind/doc #'sk/rule-v)

;; Vertical reference line:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point)
            (sk/rule-v 6.0))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/rule-h)

;; Horizontal reference line:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point)
            (sk/rule-h 3.0))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/band-v)

;; Vertical shaded band — highlights a range on the x-axis:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point)
            (sk/band-v 5.5 6.5))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 2 (:panels s)))))])

(kind/doc #'sk/band-h)

;; Horizontal shaded band — highlights a range on the y-axis:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point)
            (sk/band-h 2.5 3.5))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 2 (:panels s)))))])


;; ## Utilities

(kind/doc #'sk/cross)

;; Cartesian product — useful for building multi-view specs:

(sk/cross [:a :b] [1 2 3])

(kind/test-last [(fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))])

;; Use with `view` to create a grid of scatter plots:

(-> iris
    (sk/view (sk/cross [:sepal_length :petal_length]
                       [:sepal_width :petal_width]))
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 600 (:points s)))))])

(kind/doc #'sk/pairs)

;; Upper-triangle column pairs — no diagonal, no mirrored pairs:

(sk/pairs [:a :b :c])

(kind/test-last [(fn [v] (= [[:a :b] [:a :c] [:b :c]] v))])

;; Use with `view` for pairwise scatter plots:

(-> iris
    (sk/view (sk/pairs [:sepal_length :sepal_width :petal_length]))
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 450 (:points s)))))])

(kind/doc #'sk/distribution)

;; Create diagonal views (x = y) — each column becomes a histogram:

(-> (sk/distribution iris :sepal_length :sepal_width)
    (sk/lay (sk/histogram))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Faceting

(kind/doc #'sk/facet)

;; Split by one column — horizontal row of panels:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species)
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; Vertical column of panels:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species :col)
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/facet-grid)

;; Split by two columns — row × column grid:

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/facet-grid :smoker :sex)
    (sk/lay (sk/point {:color :sex}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 244 (:points s)))))])

;; Free scales — each panel gets its own y-range:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species)
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:scales :free-y}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; ## Inspection

(kind/doc #'sk/svg-summary)

;; Pass an SVG plot to get structural counts and text content:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot
    sk/svg-summary)

(kind/test-last [(fn [m] (and (= 1 (:panels m))
                              (= 150 (:points m))
                              (zero? (:lines m))
                              (zero? (:polygons m))))])

;; With faceting — the panel count reflects the number of facets:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species)
    (sk/lay (sk/point {:color :species}))
    sk/plot
    sk/svg-summary
    (select-keys [:panels :points]))

(kind/test-last [(fn [m] (and (= 3 (:panels m))
                              (= 150 (:points m))))])

;; With regression lines — the `:lines` count is non-zero:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot
    sk/svg-summary
    (select-keys [:points :lines]))

(kind/test-last [(fn [m] (and (= 150 (:points m))
                              (= 3 (:lines m))))])

;; The `:texts` vector contains all rendered text — axis labels,
;; tick values, legend entries, and titles:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/labs {:title "Iris Scatter"})
    sk/plot
    sk/svg-summary
    :texts)

(kind/test-last [(fn [ts] (and (some #{"Iris Scatter"} ts)
                               (some #{"sepal length"} ts)
                               (some #{"setosa"} ts)))])

(kind/doc #'sk/valid-sketch?)

(sk/valid-sketch? sk1)

(kind/test-last [true?])

(kind/doc #'sk/explain-sketch)

;; Returns nil for valid sketches, a Malli explanation map for invalid ones:

(sk/explain-sketch sk1)

(kind/test-last [nil?])
