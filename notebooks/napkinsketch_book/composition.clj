;; # Composition
;;
;; Napkinsketch's frame substrate lets you combine whole plots into a
;; single figure. A **composite frame** holds other frames and a
;; layout; each sub-frame renders independently and the composite
;; tiles them together.
;;
;; This chapter walks through composition patterns from simple
;; side-by-side arrangements to shared-scale marginal plots, using
;; `sk/arrange` and explicit composite-frame maps. Dedicated
;; constructors (`sk/mosaic`, `sk/with-marginals`) will land
;; post-alpha; until then, the primitives below cover the same
;; ground in a few extra lines.

(ns napkinsketch-book.composition
  (:require
   ;; Tablecloth -- dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; RDatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]))

(def iris (rdatasets/datasets-iris))

;; ## Side-by-Side via `sk/arrange`
;;
;; The simplest composite: two independent frames, placed next to
;; each other. `sk/arrange` takes a vector of frames and returns a
;; composite. Each sub-frame has its own data, mapping, layers, and
;; options.

(sk/arrange
 [(-> iris (sk/lay-point :sepal-length :sepal-width {:color :species}))
  (-> iris (sk/lay-point :petal-length :petal-width {:color :species}))])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Pass `{:direction :vertical}` for a stacked arrangement:

(sk/arrange
 [(-> iris (sk/lay-point :sepal-length :sepal-width {:color :species}))
  (-> iris (sk/lay-point :petal-length :petal-width {:color :species}))]
 {:direction :vertical})

(kind/test-last [(fn [v] (= 2 (:panels (sk/svg-summary v))))])

;; Each sub-frame gets a rectangle proportional to its weight
;; (default equal weights). To give the first panel twice the space,
;; pass `:weights`:

(sk/arrange
 [(-> iris (sk/lay-point :sepal-length :sepal-width))
  (-> iris (sk/lay-point :petal-length :petal-width))]
 {:weights [2 1]})

(kind/test-last [(fn [v] (= 2 (:panels (sk/svg-summary v))))])

;; ## Explicit Composite Frames
;;
;; Under `sk/arrange` there is a plain-map composite frame. You can
;; construct one directly when you need finer control -- for example,
;; to share a dataset across sub-frames, set shared scales, or mix in
;; non-plot leaves (text panels, KPIs) in future work.

(def bare-composite
  {:data iris
   :layout {:direction :horizontal :weights [1 1]}
   :frames [{:mapping {:x :sepal-length :y :sepal-width}
             :layers [{:layer-type :point}]}
            {:mapping {:x :petal-length :y :petal-width}
             :layers [{:layer-type :point}]}]})

bare-composite

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; The outer `:data` is inherited by both sub-frames. Each sub-frame
;; has its own `:mapping` and `:layers`, and need not repeat the
;; dataset.

;; ## Shared Scales
;;
;; By default, sibling frames in a composite compute their own
;; domains. That is fine when their columns differ, but for the same
;; column shown twice (e.g., a marginal above a scatter, or a mosaic
;; of scatters all measuring the same variable) you want the axes
;; aligned. `:share-scales` pins scales across siblings by effective
;; column:

(def shared-x
  {:data iris
   :share-scales #{:x}
   :layout {:direction :horizontal :weights [1 1]}
   :frames [{:mapping {:x :sepal-length :y :sepal-width}
             :layers [{:layer-type :point}]}
            {:mapping {:x :sepal-length :y :petal-length}
             :layers [{:layer-type :point}]}]})

shared-x

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Both panels share the sepal-length x-domain even though their y
;; columns differ. Column bucketing is automatic: only siblings whose
;; effective x-column matches share a scale. Panels with different
;; x-columns would each get their own domain.

;; ## A Marginal-Plot Pattern
;;
;; The classic "scatter with top density" -- a distribution strip
;; above the main plot -- is a vertical composite with shared x:

(def marginal
  {:data iris
   :share-scales #{:x}
   :layout {:direction :vertical :weights [1 3]}
   :frames [{:mapping {:x :sepal-length}
             :layers [{:layer-type :density}]}
            {:mapping {:x :sepal-length :y :sepal-width :color :species}
             :layers [{:layer-type :point}]}]})

marginal

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 150 (:points s))
                                (pos? (:polygons s)))))])

;; The top panel's density curve aligns with the scatter's x-axis.
;; Each panel retains its own y-axis because `:share-scales` here
;; contains only `:x`.

;; ## A Small Dashboard
;;
;; Composite frames can combine heterogeneous chart types. Here is a
;; dashboard-style 2x2 layout: a histogram of sepal length, a boxplot
;; of sepal width by species, a scatter of petal dimensions, and a
;; density of petal length.
;;
;; Layouts today are one-dimensional (`:horizontal` or `:vertical`),
;; so a 2x2 grid is built as a vertical pair of horizontal pairs.
;; The current `sk/arrange` does not accept composite frames as
;; inputs, so the nesting is expressed as an explicit map:

(def dashboard
  {:data iris
   :layout {:direction :vertical :weights [1 1]}
   :frames [{:layout {:direction :horizontal :weights [1 1]}
             :frames [{:mapping {:x :sepal-length}
                       :layers [{:layer-type :histogram}]}
                      {:mapping {:x :species :y :sepal-width :color :species}
                       :layers [{:layer-type :boxplot}]}]}
            {:layout {:direction :horizontal :weights [1 1]}
             :frames [{:mapping {:x :petal-length :y :petal-width :color :species}
                       :layers [{:layer-type :point}]}
                      {:mapping {:x :petal-length :color :species}
                       :layers [{:layer-type :density}]}]}]})

dashboard

(kind/test-last [(fn [v] (= 4 (:panels (sk/svg-summary v))))])

;; Four panels, each its own layer type. The outer composite stacks
;; two rows; each row is itself a horizontal composite of two plots.
;; The top-level `:data` is inherited by every leaf.

;; ## Known Limitations
;;
;; The alpha ships composition with a few deliberate gaps. Each will
;; be addressed in 0.2.0 or later:
;;
;; - **Chrome duplication.** Each leaf renders its own axes, labels,
;;   and ticks. Shared scales align the ranges but not the visible
;;   chrome -- you will see the x-axis label on both panels of a
;;   marginal plot.
;; - **Plot-area alignment.** Leaves render with their own chrome
;;   padding, so plot-area edges may not line up across composite
;;   siblings.
;; - **No shared legend.** Each sub-frame produces its own legend.
;; - **Threading builds flat composites only.** For nested
;;   composites, use literal maps or nested `sk/arrange` calls as
;;   shown above.
;;
;; See the alpha release notes for the full list.

;; ## What's Next
;;
;; - [**Faceting**](./napkinsketch_book.faceting.html) -- panel
;;   splits by a categorical column (a data-driven composite)
;; - [**Gallery**](./napkinsketch_book.gallery.html) -- more
;;   composition examples alongside single-plot chart types
