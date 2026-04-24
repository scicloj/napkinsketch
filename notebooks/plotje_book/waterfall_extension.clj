;; # Extension Example: Waterfall Chart
;;
;; This notebook walks through building a custom chart type from
;; scratch -- a **[waterfall chart](https://en.wikipedia.org/wiki/Waterfall_chart)** that shows running totals as
;; colored bars (green for increases, red for decreases).
;;
;; It demonstrates all three extension points needed for a new mark:
;;
;; - `compute-stat` -- transform raw values into cumulative bars
;; - `extract-layer` -- convert stat output into plan geometry
;; - `layer->membrane` -- render bars as membrane drawables
;;
;; After reading this, you should be able to add any custom chart
;; type to plotje.

(ns plotje-book.waterfall-extension
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Plotje -- public API
   [scicloj.plotje.api :as sk]
   ;; Extension points -- the multimethods we will extend
   [scicloj.plotje.impl.stat :as stat]
   [scicloj.plotje.impl.extract :as extract]
   [scicloj.plotje.render.mark :as mark]
   ;; Method registry -- to register our new chart type
   [scicloj.plotje.layer-type :as layer-type]
   ;; Membrane -- drawing primitives
   [membrane.ui :as ui]
   ;; Tablecloth -- dataset operations
   [tablecloth.api :as tc]))

;; ## Sample Data
;;
;; A simple profit-and-loss waterfall: revenue flows through costs
;; to net income.

(def pnl-data
  {:category ["Revenue" "COGS" "Gross Profit" "OpEx" "Tax" "Net Income"]
   :amount   [500       -300   200            -120   -30   50]})

;; ## Step 1: The Stat
;;
;; `compute-stat` transforms raw data into the shape the extractor
;; needs. For a waterfall, we compute running totals and determine
;; the start and end of each bar.
;;
;; The stat must always return `:x-domain` and `:y-domain`. Beyond
;; that, the shape is a contract between the stat and its paired
;; extractor -- here we use `:waterfall-bars`.

(defmethod stat/compute-stat :waterfall [{:keys [data x y x-type] :as view}]
  (let [clean (tc/drop-missing data [x y])
        categories (vec (distinct (clean x)))
        values (vec (clean y))
        ;; Running total: each bar starts where the previous ended
        ends (vec (reductions + values))
        starts (vec (cons 0 (butlast ends)))
        bars (mapv (fn [cat s e v]
                     {:category cat
                      :start (double s)
                      :end (double e)
                      :value (double v)})
                   categories starts ends values)
        y-min (min 0.0 (apply min (concat starts ends)))
        y-max (apply max (concat starts ends))]
    {:waterfall-bars bars
     :categories categories
     :x-domain categories
     :y-domain [y-min y-max]}))

;; Test the stat in isolation -- always a good idea before wiring
;; into the full pipeline:

(stat/compute-stat {:stat :waterfall :data (tc/dataset pnl-data) :x :category :y :amount :x-type :categorical})

(kind/test-last [(fn [m] (and (= 6 (count (:waterfall-bars m)))
                              (= 500.0 (:end (first (:waterfall-bars m))))
                              (= ["Revenue" "COGS" "Gross Profit" "OpEx" "Tax" "Net Income"]
                                 (:categories m))))])

;; ## Step 2: The Extractor
;;
;; `extract-layer` converts the stat output into a plan layer
;; descriptor -- a plain map with `:mark`, `:style`, and geometry
;; data. Colors are resolved here using the library's color system.
;;
;; We color bars green for positive amounts and red for negative.

(defmethod extract/extract-layer :waterfall [view stat all-colors cfg]
  (let [bars (:waterfall-bars stat)
        green [0.2 0.7 0.3 1.0]
        red [0.85 0.25 0.25 1.0]]
    {:mark :waterfall
     :style {:opacity 0.85}
     :categories (:categories stat)
     :bars (mapv (fn [{:keys [category start end value]}]
                   {:category category
                    :start start
                    :end end
                    :color (if (>= value 0) green red)})
                 bars)}))

;; ## Step 3: The Renderer
;;
;; `layer->membrane` turns the plan layer into membrane drawable
;; primitives. The rendering context (`ctx`) provides:
;;
;; - `:sx` -- the x scale function mapping data value to pixel x
;; - `:sy` -- the y scale function mapping data value to pixel y
;; - `:panel-height`, `:panel-width`, `:margin` -- layout dimensions
;;
;; For a band (categorical) x-scale, `(sx category true)` returns
;; band info including `:rstart`, `:rend`, and `:point`.

(defmethod mark/layer->membrane :waterfall [layer ctx]
  (let [{:keys [bars style]} layer
        {:keys [sx sy coord-fn]} ctx
        {:keys [opacity]} style
        ;; Get band info from the scale to find bar width.
        ;; (sx category true) returns {:rstart :rend :point} for that band.
        sample-info (sx (-> bars first :category) true)
        bw (- (:rend sample-info) (:rstart sample-info))
        w (* 0.8 bw)]
    (vec
     (for [{:keys [category start end color]} bars
           :let [[cr cg cb ca] color
                 ;; Use the band info for precise positioning
                 band (sx category true)
                 mid-x (:point band)
                 py-start (double (sy start))
                 py-end (double (sy end))
                 top (min py-start py-end)
                 bot (max py-start py-end)
                 x0 (- mid-x (/ w 2.0))
                 x1 (+ mid-x (/ w 2.0))]]
       (ui/with-color [cr cg cb (or opacity ca)]
         (ui/with-style :membrane.ui/style-fill
           (ui/path [x0 top] [x1 top] [x1 bot] [x0 bot])))))))

;; ## Step 4: Register and Plot
;;
;; Register the layer type so the pipeline recognizes `:waterfall`:

(layer-type/register! :waterfall
                      {:mark :waterfall :stat :waterfall
                       :doc "Waterfall -- running total with increase/decrease bars."})

;; Now we can plot it using the frame API. Since there is
;; no built-in `sk/lay-waterfall`, we use `sk/lay` with
;; the layer-type lookup.
;;
;; We use `sk/plot` to force eager rendering to SVG before the
;; cleanup section at the end of this notebook removes the
;; extension's defmethods:

(-> pnl-data
    (sk/frame :category :amount)
    (sk/lay (layer-type/lookup :waterfall))
    (sk/options {:title "Profit & Loss Waterfall"
                 :width 500 :height 350})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 6 (:polygons s)))))])

;; Six bars -- one per category. Green for positive amounts (Revenue,
;; Gross Profit, Net Income), red for negative (COGS, OpEx, Tax).

;; ## Optional: Convenience Function
;;
;; For a polished API, wrap the pattern in a frame-compatible
;; function:

(defn lay-waterfall
  ([sk] (sk/lay sk (layer-type/lookup :waterfall)))
  ([data x y] (-> data (sk/frame x y) (sk/lay (layer-type/lookup :waterfall))))
  ([data x y opts] (-> data (sk/frame x y) (sk/lay (merge (layer-type/lookup :waterfall) opts)))))

;; Now the call is as clean as any built-in layer type:

(-> pnl-data
    (lay-waterfall :category :amount)
    (sk/options {:title "Quarterly Cash Flow" :width 500})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 6 (:polygons s))))])

;; ## Optional: Self-Documenting Extension
;;
;; Register `[:key :doc]` defmethods so the extension appears in
;; the generated tables in the Extensibility chapter:

(defmethod stat/compute-stat [:waterfall :doc] [_]
  "Compute running totals for waterfall bars")

(defmethod extract/extract-layer [:waterfall :doc] [_ _ _ _]
  "Colored bars (green/red) with start/end positions")

(defmethod mark/layer->membrane [:waterfall :doc] [_ _]
  "Filled rectangles positioned by running total")

(sk/stat-doc :waterfall)

(kind/test-last [(fn [v] (= "Compute running totals for waterfall bars" v))])

;; ## Cleanup
;;
;; Remove the extension so it does not affect other notebooks:

(remove-method stat/compute-stat :waterfall)
(remove-method stat/compute-stat [:waterfall :doc])
(remove-method extract/extract-layer :waterfall)
(remove-method extract/extract-layer [:waterfall :doc])
(remove-method mark/layer->membrane :waterfall)
(remove-method mark/layer->membrane [:waterfall :doc])
(swap! @(resolve 'scicloj.plotje.layer-type/registry*) dissoc :waterfall)

;; Verify cleanup:

(nil? (layer-type/lookup :waterfall))

(kind/test-last [(fn [v] (true? v))])

;; ## What's Next
;;
;; - [**Extensibility**](./plotje_book.extensibility.html) -- reference for all seven extension points
;; - [**Architecture**](./plotje_book.architecture.html) -- the five-stage pipeline in detail
