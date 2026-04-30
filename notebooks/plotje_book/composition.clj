;; # Composition
;;
;; Plotje's pose substrate lets you combine whole plots into a
;; single rendered image. A **composite pose** holds other poses
;; and a layout; each sub-pose renders independently and the
;; composite tiles them together.
;;
;; This chapter walks through composition patterns from simple
;; side-by-side arrangements to shared-scale marginal plots, using
;; `pj/arrange` and explicit composite-pose maps -- compactly for
;; simple cases, with a bit of literal map construction for nested
;; layouts.

(ns plotje-book.composition
  (:require
   ;; Tablecloth -- dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]
   ;; Rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]))

;; ## Side-by-Side via `pj/arrange`
;;
;; The simplest composite: two independent poses, placed next to
;; each other. `pj/arrange` takes a vector of poses and returns a
;; composite. Each sub-pose has its own data, mapping, layers, and
;; options.

(pj/arrange
 [(-> (rdatasets/datasets-iris) (pj/lay-point :sepal-length :sepal-width {:color :species}))
  (-> (rdatasets/datasets-iris) (pj/lay-point :petal-length :petal-width {:color :species}))])

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Pass `{:cols 1}` for a stacked arrangement (one column means each
;; pose goes on its own row):

(pj/arrange
 [(-> (rdatasets/datasets-iris) (pj/lay-point :sepal-length :sepal-width {:color :species}))
  (-> (rdatasets/datasets-iris) (pj/lay-point :petal-length :petal-width {:color :species}))]
 {:cols 1})

(kind/test-last [(fn [v] (= 2 (:panels (pj/svg-summary v))))])

;; `pj/arrange` divides space equally among its sub-poses. For
;; unequal splits (e.g., give the first panel twice the space of the
;; second), construct the composite as an explicit map; the next
;; section shows how.

;; ## Explicit Composite Poses
;;
;; Under `pj/arrange` there is a plain-map composite pose. You can
;; construct one directly when you need finer control -- unequal
;; weights, shared scales, or (in future work) non-plot leaves like
;; text panels and KPIs.
;;
;; An explicit `:layout` accepts `:direction` (`:horizontal` or
;; `:vertical`) and `:weights` (one weight per sub-pose). Here the
;; first panel gets twice the space of the second:

(def weighted
  (pj/pose
   {:layout {:direction :horizontal :weights [2 1]}
    :poses [{:mapping {:x :sepal-length :y :sepal-width}
             :layers [{:layer-type :point}]}
            {:mapping {:x :petal-length :y :petal-width}
             :layers [{:layer-type :point}]}]
    :data (rdatasets/datasets-iris)}))

;; `pj/pose` accepts a literal composite map and tags it for notebook
;; auto-render -- the same pose value the threaded API produces:

weighted

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; And printed, showing the composite's structure -- `:layout` with
;; direction and weights at the top, then each sub-pose with its
;; own `:mapping` and `:layers`, and the outer `:data` dataset:

(kind/pprint weighted)

(kind/test-last [(fn [pose] (and (= [2 1] (get-in pose [:layout :weights]))
                                 (= 2 (count (:poses pose)))))])

;; The outer `:data` is inherited by both sub-poses. Each sub-pose
;; has its own `:mapping` and `:layers`, and need not repeat the
;; dataset. Subsequent examples in this chapter follow the same
;; shape and show only the rendered plot.

;; ## Shared Scales
;;
;; By default, sibling poses in a composite compute their own
;; domains. That is fine when their columns differ, but for the same
;; column shown twice (e.g., a marginal above a scatter, or a mosaic
;; of scatters all measuring the same variable) you want the axes
;; aligned. `:share-scales` pins scales across siblings by effective
;; column:

(def shared-x
  (pj/pose
   {:share-scales #{:x}
    :layout {:direction :horizontal :weights [1 1]}
    :poses [{:mapping {:x :sepal-length :y :sepal-width}
             :layers [{:layer-type :point}]}
            {:mapping {:x :sepal-length :y :petal-length}
             :layers [{:layer-type :point}]}]
    :data (rdatasets/datasets-iris)}))

shared-x

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
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
  (pj/pose
   {:share-scales #{:x}
    :layout {:direction :vertical :weights [1 3]}
    :poses [{:mapping {:x :sepal-length}
             :layers [{:layer-type :density}]}
            {:mapping {:x :sepal-length :y :sepal-width :color :species}
             :layers [{:layer-type :point}]}]
    :data (rdatasets/datasets-iris)}))

marginal

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 150 (:points s))
                                (pos? (:polygons s)))))])

;; The top panel's density curve aligns with the scatter's x-axis.
;; Each panel retains its own y-axis because `:share-scales` here
;; contains only `:x`.

;; ## A Small Dashboard
;;
;; Composite poses can combine heterogeneous chart types. Here is a
;; dashboard-style 2x2 layout: a histogram of sepal length, a boxplot
;; of sepal width by species, a scatter of petal dimensions, and a
;; density of petal length.
;;
;; Each cell is built as its own leaf pose, then `pj/arrange` takes
;; rows of cells and produces the 2x2 grid:

(def dashboard
  (let [iris (rdatasets/datasets-iris)]
    (pj/arrange
     [[(-> iris (pj/lay-histogram :sepal-length))
       (-> iris (pj/lay-boxplot :species :sepal-width {:color :species}))]
      [(-> iris (pj/lay-point :petal-length :petal-width {:color :species}))
       (-> iris (pj/lay-density :petal-length {:color :species}))]])))

dashboard

(kind/test-last [(fn [v] (= 4 (:panels (pj/svg-summary v))))])

;; Four panels, each its own layer type. `pj/arrange` stacks the rows
;; vertically; each row is laid out horizontally. Every cell is a
;; leaf pose -- `pj/arrange` does not accept composite cells.

;; ## Notes on the Current Implementation
;;
;; A few details about how composition renders today, in case they
;; matter for a layout you're sketching:
;;
;; - **Each leaf draws its own axes, labels, and ticks.** Shared
;;   scales align the data ranges across panels, but these
;;   decorations are per-leaf, so a marginal plot shows the
;;   x-axis label on both the main and the marginal panel.
;; - **Plot-area edges may not line up** across composite siblings,
;;   since each leaf reserves its own padding for axes and labels --
;;   two sub-poses with different label lengths can produce visibly
;;   different panel widths.
;; - **Each sub-pose produces its own legend** -- if you map `:color`
;;   in two sibling sub-poses, the rendered plot shows two legends
;;   rather than a merged one.
;; - **Multi-row layouts go through `pj/arrange`.** Both `pj/arrange`
;;   and the explicit-map form accept only leaf cells; a row of rows
;;   or column of rows is built by passing nested vectors of leaves
;;   to `pj/arrange` (the dashboard example above shows the shape).
;;   Nested composites (a sub-pose that is itself composite) are out
;;   of scope today.

;; ## What's Next
;;
;; - [**Options and Scopes**](./plotje_book.options_and_scopes.html) --
;;   the taxonomy of layer options, plot options, and configuration
;;   (next chapter in Foundations)
;; - [**Faceting**](./plotje_book.faceting.html) -- panel splits by
;;   a categorical column (a data-driven composite, covered in How-to)
;; - [**Gallery**](./plotje_book.gallery.html) -- more composition
;;   examples alongside single-plot chart types
