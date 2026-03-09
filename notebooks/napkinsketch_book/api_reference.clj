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
   [scicloj.napkinsketch.api :as sk]))

;; ## Sample Data

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(def tiny (tc/dataset {:x [1 2 3 4 5]
                       :y [2 4 1 5 3]
                       :group [:a :a :b :b :b]}))

(def sales (tc/dataset {:product [:widget :gadget :gizmo :doohickey]
                        :revenue [120 340 210 95]}))

;; ## Data Setup

(kind/doc #'sk/view)

;; Single scatter view — two columns as `[x y]`:

(-> iris (sk/view [[:sepal_length :sepal_width]]) (sk/lay (sk/point)) sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Histogram view — a single keyword means x = y (diagonal):

(-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Multiple views — a vector of `[x y]` pairs:

(-> iris
    (sk/view [[:sepal_length :sepal_width]
              [:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Map form — explicit keys:

(-> (sk/view iris {:x :sepal_length :y :sepal_width})
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

(kind/doc #'sk/lay)

;; Apply one mark:

(-> iris (sk/view [[:sepal_length :sepal_width]]) (sk/lay (sk/point)) sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Apply multiple marks — scatter with regression:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; ## Marks

(kind/doc #'sk/point)

;; Default scatter:

(sk/plot [(sk/point {:data tiny :x :x :y :y})])

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Color by column:

(sk/plot [(sk/point {:data tiny :x :x :y :y :color :group})])

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Fixed color:

(sk/plot [(sk/point {:data tiny :x :x :y :y :color "#E74C3C"})])

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Size by column:

(sk/plot [(sk/point {:data iris :x :sepal_length :y :sepal_width
                     :size :petal_length :color :species})])

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Constant size — uniform radius for all points:

(sk/plot [(sk/point {:data tiny :x :x :y :y :size 6})])

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Constant alpha — uniform transparency:

(sk/plot [(sk/point {:data tiny :x :x :y :y :alpha 0.3})])

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Combined — large, semi-transparent, colored points:

(sk/plot [(sk/point {:data iris :x :sepal_length :y :sepal_width
                     :color :species :alpha 0.5 :size 5})])

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

(kind/doc #'sk/line)

;; Connected line through data points:

(def wave (tc/dataset {:x (range 30)
                       :y (mapv #(Math/sin (* % 0.3)) (range 30))}))

(sk/plot [(sk/line {:data wave :x :x :y :y})])

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Grouped lines:

(def waves (tc/dataset {:x (vec (concat (range 30) (range 30)))
                        :y (vec (concat (mapv #(Math/sin (* % 0.3)) (range 30))
                                        (mapv #(Math/cos (* % 0.3)) (range 30))))
                        :fn (vec (concat (repeat 30 :sin) (repeat 30 :cos)))}))

(sk/plot [(sk/line {:data waves :x :x :y :y :color :fn})])

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Thick line — constant stroke width:

(sk/plot [(sk/line {:data wave :x :x :y :y :size 4})])

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

(kind/doc #'sk/histogram)

;; Default binning:

(-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Colored histogram — one set of bins per group:

(-> iris (sk/view :sepal_length) (sk/lay (sk/histogram {:color :species})) sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

(kind/doc #'sk/bar)

;; Count occurrences of a categorical column:

(-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Grouped (dodged) bars:

(-> iris (sk/view :species) (sk/lay (sk/bar {:color :species})) sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Semi-transparent bars:

(-> iris (sk/view :species) (sk/lay (sk/bar {:alpha 0.4})) sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

(kind/doc #'sk/stacked-bar)

;; Stacked categorical bars:

(def penguins (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
                          {:key-fn keyword}))

(-> penguins (sk/view :island) (sk/lay (sk/stacked-bar {:color :species})) sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

(kind/doc #'sk/value-bar)

;; Categorical x, numeric y — no counting:

(sk/plot [(sk/value-bar {:data sales :x :product :y :revenue})])

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

(kind/doc #'sk/lm)

;; Single regression line:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/lm))
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Per-group regression:

(-> iris
    (sk/view [[:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; ## Rendering

(kind/doc #'sk/plot)

;; Default rendering:

(sk/plot [(sk/point {:data tiny :x :x :y :y})])

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; With options — title, labels, dimensions:

(sk/plot [(sk/point {:data iris :x :sepal_length :y :sepal_width :color :species})]
         {:title "Iris Scatter"
          :x-label "Sepal Length (cm)"
          :y-label "Sepal Width (cm)"
          :width 800
          :height 300})

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Direct mark styling — `:alpha` and `:size`:

(sk/plot [(sk/point {:data iris :x :sepal_length :y :sepal_width
                     :alpha 0.5 :size 4})])

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

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

;; ## Transforms

(kind/doc #'sk/coord)

;; Flip axes — horizontal bar chart:

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    (sk/coord :flip)
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

(kind/doc #'sk/scale)

;; Log scale on x-axis:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point))
    (sk/scale :x :log)
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Fixed domain:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point))
    (sk/scale :x {:domain [3 9]})
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Custom axis label via scale:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point))
    (sk/scale :x {:label "Length (cm)"})
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

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

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])
