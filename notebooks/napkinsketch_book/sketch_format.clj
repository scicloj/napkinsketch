;; # The Sketch Data Model
;;
;; napkinsketch separates **what** to draw from **how** to draw it using
;; an intermediate data structure called a **sketch**.
;;
;; A sketch is a fully resolved plot specification — data-space geometry,
;; domains, scale types, tick info, legend, and layout — as a plain Clojure
;; map. No membrane types, no datasets, no scale objects. Serializable data.
;;
;; This notebook explores the sketch format, validates it with Malli,
;; and demonstrates the pipeline: `views → sketch → membrane → SVG`.

(ns napkinsketch-book.sketch-format
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as ns]
   [scicloj.napkinsketch.impl.sketch-schema :as ss]
   [clojure.pprint :as pp]))

;; ## The Pipeline
;;
;; napkinsketch's rendering pipeline has three stages:
;;
;; - **Views → Sketch** — resolve data columns, compute statistics,
;;   merge domains, build layer geometry in data space
;;
;; - **Sketch → Membrane scene** — map data-space coordinates through
;;   scales to pixel space, emit membrane drawing primitives
;;
;; - **Membrane scene → SVG** — walk the scene tree and convert each
;;   membrane record to SVG hiccup elements

;; ## Building a Sketch
;;
;; The `ns/sketch` function takes views and options, just like `ns/plot`,
;; but returns the intermediate sketch instead of rendering SVG.

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

;; A simple scatter sketch:

(def scatter-sketch
  (ns/sketch [(ns/point {:data iris :x :sepal_length :y :sepal_width :color :species})]))

;; ## Top-Level Structure
;;
;; The sketch is a plain map with these keys:

(kind/table
 {:column-names ["Key" "Description"]
  :row-maps (mapv (fn [[k desc]]
                    {"Key" (str k) "Description" desc})
                  [[:width "Plot area width in pixels"]
                   [:height "Plot area height in pixels"]
                   [:margin "Margin inside the plot area"]
                   [:total-width "Total width including labels and legend"]
                   [:total-height "Total height including title and labels"]
                   [:title "Plot title (optional)"]
                   [:x-label "X-axis label"]
                   [:y-label "Y-axis label"]
                   [:config "Merged configuration map"]
                   [:legend "Legend info (optional)"]
                   [:panels "Vector of panel maps"]
                   [:layout "Layout padding/offsets"]])})

;; The top-level keys of our scatter sketch:

(keys scatter-sketch)

(kind/test-last (fn [ks] (every? (set ks) [:width :height :panels :legend :layout])))

;; Dimensions:

(select-keys scatter-sketch [:width :height :margin :total-width :total-height])

(kind/test-last (fn [m] (and (= 600 (:width m)) (= 400 (:height m)))))

;; ## Panels
;;
;; Each panel contains domains, scale specs, tick info, and layers.
;; Currently napkinsketch produces a single panel.

(def panel (first (:panels scatter-sketch)))

(keys panel)

(kind/test-last (fn [ks] (every? (set ks) [:x-domain :y-domain :x-scale :y-scale
                                            :coord :x-ticks :y-ticks :layers])))

;; ### Domains
;;
;; Domains are in data space — the actual numeric or categorical range.

(:x-domain panel)

(kind/test-last (fn [d] (and (number? (first d)) (< (first d) (second d)))))

(:y-domain panel)

(kind/test-last (fn [d] (and (number? (first d)) (< (first d) (second d)))))

;; ### Scale Specs

(:x-scale panel)

(kind/test-last (fn [s] (= :linear (:type s))))

;; ### Coordinate System

(:coord panel)

(kind/test-last (fn [c] (= :cartesian c)))

;; ### Tick Info
;;
;; Ticks are pre-computed — values, labels, and whether the axis is categorical.

(:x-ticks panel)

(kind/test-last (fn [t] (and (seq (:values t)) (seq (:labels t)) (not (:categorical? t)))))

;; ## Layers
;;
;; Layers are the heart of the sketch — data-space geometry with resolved colors.

(count (:layers panel))

(kind/test-last (fn [n] (= 1 n)))

(def point-layer (first (:layers panel)))

;; ### Point Layer Structure

(select-keys point-layer [:mark :style])

(kind/test-last (fn [m] (and (= :point (:mark m))
                              (number? (:opacity (:style m)))
                              (number? (:radius (:style m))))))

;; Each group has a resolved RGBA color and data-space coordinate vectors:

(count (:groups point-layer))

(kind/test-last (fn [n] (= 3 n)))

;; One group (setosa):

(let [g (first (:groups point-layer))]
  {:color (:color g)
   :n-points (count (:xs g))
   :x-range [(reduce min (:xs g)) (reduce max (:xs g))]
   :y-range [(reduce min (:ys g)) (reduce max (:ys g))]})

(kind/test-last (fn [m] (and (= 4 (count (:color m)))
                              (pos? (:n-points m)))))

;; ## Legend
;;
;; When color mapping is used, the sketch includes legend data.

(:legend scatter-sketch)

(kind/test-last (fn [leg] (and (= :species (:title leg))
                                (= 3 (count (:entries leg))))))

;; ## Histogram Sketch
;;
;; Histogram layers use `:bar` mark with binned data.

(def hist-sketch
  (ns/sketch [(ns/histogram {:data iris :x :sepal_length})]))

(def hist-layer (first (:layers (first (:panels hist-sketch)))))

(:mark hist-layer)

(kind/test-last (fn [m] (= :bar m)))

;; Each bar group has lo/hi/count bins:

(let [g (first (:groups hist-layer))]
  {:color (:color g)
   :n-bins (count (:bars g))
   :first-bin (first (:bars g))})

(kind/test-last (fn [m] (and (pos? (:n-bins m))
                              (contains? (:first-bin m) :lo)
                              (contains? (:first-bin m) :hi)
                              (contains? (:first-bin m) :count))))

;; ## Categorical Bar Sketch
;;
;; Count-based bars use `:rect` mark with categories and counts.

(def bar-sketch
  (ns/sketch [(ns/bar {:data iris :x :species})]))

(def bar-layer (first (:layers (first (:panels bar-sketch)))))

(select-keys bar-layer [:mark :position])

(kind/test-last (fn [m] (and (= :rect (:mark m)) (= :dodge (:position m)))))

(:categories bar-layer)

(kind/test-last (fn [cats] (= 3 (count cats))))

;; Each group's counts per category:

(let [g (first (:groups bar-layer))]
  {:label (:label g)
   :counts (:counts g)})

(kind/test-last (fn [m] (and (string? (:label m))
                              (every? #(contains? % :category) (:counts m)))))

;; ## Regression Line Sketch
;;
;; Regression lines produce line segments in data space.

(def lm-sketch
  (ns/sketch [(ns/point {:data iris :x :sepal_length :y :sepal_width})
              (ns/lm {:data iris :x :sepal_length :y :sepal_width})]))

(def lm-layer (second (:layers (first (:panels lm-sketch)))))

(:mark lm-layer)

(kind/test-last (fn [m] (= :line m)))

(:stat-origin lm-layer)

(kind/test-last (fn [s] (= :lm s)))

;; Line segment endpoints in data space:

(let [g (first (:groups lm-layer))]
  (select-keys g [:x1 :y1 :x2 :y2]))

(kind/test-last (fn [m] (every? number? (vals m))))

;; ## Polyline Sketch
;;
;; Connected-point lines use xs/ys vectors.

(def line-data (tc/dataset {:x (range 20) :y (map #(Math/sin (* % 0.3)) (range 20))}))

(def line-sketch
  (ns/sketch [(ns/line {:data line-data :x :x :y :y})]))

(def line-layer (first (:layers (first (:panels line-sketch)))))

(let [g (first (:groups line-layer))]
  {:n-points (count (:xs g))
   :has-xs? (some? (:xs g))
   :has-ys? (some? (:ys g))})

(kind/test-last (fn [m] (and (:has-xs? m) (:has-ys? m) (= 20 (:n-points m)))))

;; ## Value Bar Sketch
;;
;; Value bars map categorical x to numeric y without counting.

(def vbar-data (tc/dataset {:category [:a :b :c :d] :value [10 25 15 30]}))

(def vbar-sketch
  (ns/sketch [(ns/value-bar {:data vbar-data :x :category :y :value})]))

(def vbar-layer (first (:layers (first (:panels vbar-sketch)))))

(:mark vbar-layer)

(kind/test-last (fn [m] (= :rect m)))

(let [g (first (:groups vbar-layer))]
  {:xs (:xs g) :ys (:ys g)})

(kind/test-last (fn [m] (and (= 4 (count (:xs m))) (= 4 (count (:ys m))))))

;; ## Malli Validation
;;
;; Every sketch conforms to a Malli schema.

(ss/valid? scatter-sketch)

(kind/test-last true?)

(ss/valid? hist-sketch)

(kind/test-last true?)

(ss/valid? bar-sketch)

(kind/test-last true?)

(ss/valid? lm-sketch)

(kind/test-last true?)

(ss/valid? line-sketch)

(kind/test-last true?)

(ss/valid? vbar-sketch)

(kind/test-last true?)

;; ## Serialization
;;
;; Sketches are plain Clojure data — fully serializable with pr-str/read-string.

(let [s (pr-str scatter-sketch)
      back (read-string s)]
  (= scatter-sketch back))

(kind/test-last true?)

;; ## Sketch → Plot
;;
;; The `ns/plot` function internally calls `ns/sketch`, then renders through
;; membrane and SVG. Both functions accept the same arguments.

;; Sketch (data):
(ns/sketch [(ns/point {:data iris :x :sepal_length :y :sepal_width :color :species})]
           {:title "Iris Petals"})

;; Plot (SVG):
(ns/plot [(ns/point {:data iris :x :sepal_length :y :sepal_width :color :species})]
         {:title "Iris Petals"})
