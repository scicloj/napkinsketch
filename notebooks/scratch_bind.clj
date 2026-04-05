;; # One Verb
;;
;; An exploration of compositional plotting with a single operation: `bind`.
;;
;; Inspired by Wilkinson's
;; [Grammar of Graphics](https://link.springer.com/book/10.1007/0-387-28695-0)
;; and Julia's [AlgebraOfGraphics.jl](https://aog.makie.org/stable/),
;; shaped by Clojure idioms: plain maps, `merge`, threading.

(ns scratch-bind
  (:require [tablecloth.api :as tc]
            [scicloj.kindly.v4.kind :as kind]))

;; ## The Model
;;
;; A **sketch** is a dataset paired with a map of **bindings**.
;; Each binding maps a visual channel to one or more values.
;;
;; `bind` is the single verb. It associates a channel with values.

(defn sketch [data]
  {:data (if (tc/dataset? data) data (tc/dataset data))
   :bindings {}})

(defn bind
  "Bind a channel to a value or collection of values.
   A single value is shared. Multiple values create variation."
  [sk channel value]
  (assoc-in sk [:bindings channel]
            (if (and (sequential? value) (not (map? value)))
              (vec value)
              [value])))

;; That's it. One noun, one verb.
;;
;; Everything else — `view`, `lay`, `cross` — is sugar.

;; ## Channels
;;
;; The system knows three kinds of channels. The kind determines
;; what happens when a channel has multiple values:
;;
;; | Kind | Channels | Multiple values → |
;; |:-----|:---------|:------------------|
;; | **Layout** | `:x`, `:y`, `:facet-row`, `:facet-col` | Grid of panels |
;; | **Method** | `:mark` | Layers within a panel |
;; | **Aesthetic** | `:color`, `:size`, `:alpha`, `:shape` | Mapped within a layer |
;;
;; Layout and method channels participate in the **cross product**.
;; Aesthetic channels are shared — they don't create new entries.

(def layout-channels #{:x :y :facet-row :facet-col})
(def method-channels #{:mark})

;; ## Resolution
;;
;; At render time, the bindings are expanded into a flat list of
;; entries — one per panel-layer combination. This is the cross
;; product of layout channels × method channels, with aesthetics
;; and per-method options merged into each entry.

(defn resolve-sketch
  "Expand bindings into entries via cross product."
  [{:keys [bindings]}]
  (let [mark-specs (get bindings :mark [{:mark :infer}])
        other (dissoc bindings :mark)
        ;; Which layout channels vary?
        varying (filter #(and (layout-channels %)
                              (> (count (other %)) 1))
                        (keys other))
        ;; Fixed channels: single-valued layout + all aesthetics
        fixed-keys (remove (set varying) (keys other))
        fixed (into {} (map (fn [k] [k (first (other k))]) fixed-keys))
        ;; Cross product of varying layout channels
        panels (if (empty? varying)
                 [{}]
                 (reduce (fn [acc ch]
                           (for [row acc, val (other ch)]
                             (assoc row ch val)))
                         [{}] varying))]
    ;; Cross panels × methods, merge fixed + per-method opts
    (vec (for [p panels, m mark-specs]
           (merge fixed p m)))))

;; ## Convenience: `view` and `lay`
;;
;; These are just common patterns of `bind`.

(defn view
  "Bind `:x` and `:y`, with optional shared aesthetics."
  ([sk x y]
   (-> sk (bind :x x) (bind :y y)))
  ([sk x y opts]
   (reduce-kv bind (-> sk (bind :x x) (bind :y y)) opts)))

(defn lay
  "Add a method. Accumulates — each `lay` call adds a layer.
   Looks up the full method (mark + stat) from the registry."
  ([sk mark]
   (let [method (or (sk/method-lookup mark) {:mark mark})]
     (update-in sk [:bindings :mark] (fnil conj []) method)))
  ([sk mark opts]
   (let [method (merge (or (sk/method-lookup mark) {:mark mark}) opts)]
     (update-in sk [:bindings :mark] (fnil conj []) method))))

(defn cross
  "Cartesian product of two sequences."
  [xs ys]
  (vec (for [x xs, y ys] [x y])))

;; ## Examples
;;
;; ### A simple scatter

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(-> (sketch iris)
    (view :sepal_length :sepal_width)
    (lay :point))

;; The sketch:

(-> (sketch iris)
    (view :sepal_length :sepal_width)
    (lay :point)
    :bindings
    kind/pprint)

;; The resolved entries:

(-> (sketch iris)
    (view :sepal_length :sepal_width)
    (lay :point)
    resolve-sketch
    kind/pprint)

(kind/test-last [(fn [v] (and (= 1 (count v))
                              (= :point (:mark (first v)))))])

;; ### Scatter + regression (two methods)

(-> (sketch iris)
    (view :sepal_length :sepal_width {:color :species})
    (lay :point {:alpha 0.5})
    (lay :lm {:se true})
    resolve-sketch
    kind/pprint)

(kind/test-last [(fn [v] (and (= 2 (count v))
                              (= :species (:color (first v)))
                              (= 0.5 (:alpha (first v)))
                              (true? (:se (second v)))))])

;; Two entries. Both share `:x`, `:y`, `:color`.
;; Each carries its own per-method options.

;; ### Two views × two methods = four entries

(-> (sketch iris)
    (view [:sepal_length :petal_length]
          [:sepal_width :petal_width])
    (lay :point)
    (lay :lm)
    resolve-sketch
    kind/pprint)

(kind/test-last [(fn [v] (= 4 (count v)))])

;; The cross product: 2 panels × 2 layers.

;; ### SPLOM (9 panels, inference)

(def cols [:sepal_length :sepal_width :petal_length])

(-> (sketch iris)
    (bind :x cols)
    (bind :y cols)
    (bind :color :species)
    resolve-sketch
    kind/pprint)

(kind/test-last [(fn [v] (and (= 9 (count v))
                              (= :infer (:mark (first v)))
                              (every? #(= :species (:color %)) v)))])

;; Nine entries, all with `:mark :infer`.
;; The renderer would infer per entry:
;; diagonal (x = y) → histogram, off-diagonal → scatter.

;; ### SPLOM with explicit methods

(-> (sketch iris)
    (bind :x cols)
    (bind :y cols)
    (bind :color :species)
    (lay :point {:alpha 0.4})
    (lay :lm)
    resolve-sketch
    count)

(kind/test-last [(fn [n] (= 18 n))])

;; 9 panels × 2 methods = 18.

;; ### Order independence
;;
;; Methods first, views second — same result.

(let [a (-> (sketch iris) (view :sepal_length :sepal_width) (lay :point) (lay :lm))
      b (-> (sketch iris) (lay :point) (lay :lm) (view :sepal_length :sepal_width))]
  (= (resolve-sketch a) (resolve-sketch b)))

(kind/test-last [(fn [v] (true? v))])

;; The axes are independent. Order doesn't matter.

;; ### Everything is just `bind`
;;
;; `view` and `lay` are sugar. You can write everything with `bind`:

(let [with-sugar (-> (sketch iris)
                     (view :sepal_length :sepal_width {:color :species})
                     (lay :point))
      with-bind (-> (sketch iris)
                    (bind :x :sepal_length)
                    (bind :y :sepal_width)
                    (bind :color :species)
                    (bind :mark (sk/method-lookup :point)))]
  (= (:bindings with-sugar)
     (:bindings with-bind)))

(kind/test-last [(fn [v] (true? v))])

;; ### Reusable styles
;;
;; A style is just a bindings map. Apply it with `merge`.

(def scatter+lm
  {:mark [{:mark :point :alpha 0.5}
          {:mark :lm :se true}]
   :color [:species]})

(-> (sketch iris)
    (bind :x :sepal_length)
    (bind :y :sepal_width)
    (update :bindings merge scatter+lm)
    resolve-sketch
    kind/pprint)

(kind/test-last [(fn [v] (and (= 2 (count v))
                              (= :species (:color (first v)))))])

;; The style travels independently of the data and columns.
;; Apply the same style to different data:

(def mpg (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/mpg.csv"
                     {:key-fn keyword}))

(-> (sketch mpg)
    (view :horsepower :mpg)
    (update :bindings merge {:mark [{:mark :point :alpha 0.5}
                                    {:mark :lm :se true}]
                             :color [:origin]})
    resolve-sketch
    kind/pprint)

(kind/test-last [(fn [v] (= 2 (count v)))])

;; ## Rendering Through the Real Pipeline
;;
;; The bind model resolves to the same view-maps that napkinsketch's
;; `views->plan` already consumes. A two-function bridge connects them:

(require '[scicloj.napkinsketch.impl.sketch :as sketch-impl])
(require '[scicloj.napkinsketch.api :as sk])

(defn bind->views
  "Resolve a bind-model sketch into view-maps for the rendering pipeline.
   Attaches :data to each entry. Strips :mark :infer for inference."
  [{:keys [data] :as sk}]
  (mapv (fn [e]
          (let [e (assoc e :data data)]
            (if (= :infer (:mark e))
              (dissoc e :mark)
              e)))
        (resolve-sketch sk)))

(defn plot
  "Render a bind-model sketch using the full napkinsketch pipeline."
  [sk & [opts]]
  (let [views (bind->views sk)
        plan (sketch-impl/views->plan views (or opts {}))]
    (sk/plan->figure plan :svg {})))

;; ### Scatter with color

(-> (sketch iris)
    (view :sepal_length :sepal_width {:color :species})
    (lay :point)
    plot)

;; ### Scatter + regression — two layers

(-> (sketch iris)
    (view :sepal_length :sepal_width {:color :species})
    (lay :point {:alpha 0.5})
    (lay :lm)
    plot)

;; ### SPLOM with inference — histograms on diagonal

(-> (sketch iris)
    (bind :x cols)
    (bind :y cols)
    (bind :color :species)
    plot)

;; ### SPLOM with explicit scatter + regression — 18 layers

(-> (sketch iris)
    (bind :x cols)
    (bind :y cols)
    (bind :color :species)
    (lay :point {:alpha 0.3})
    (lay :lm)
    plot)

;; ### Order independence — methods first, views second

(-> (sketch iris)
    (lay :point {:alpha 0.5})
    (lay :lm)
    (view :sepal_length :sepal_width {:color :species})
    plot)

;; Same result as view-first. The axes are independent.

;; ## Challenging Composability

;; ### Faceting

;; `bind :facet-col` is an instruction — the renderer splits data
;; by its values. It composes with views and methods.

(-> (sketch iris)
    (view :sepal_length :sepal_width {:color :species})
    (bind :facet-col :species)
    (lay :point)
    (lay :lm)
    plot)

;; Three panels (one per species), each with scatter + regression.

;; ### Per-method column bindings

;; Error bars need `:ymin` and `:ymax` — these live in the method map.
;; They compose with the cross product via per-method options.

(def experiment {:condition ["A" "B" "C" "D"]
                 :mean [10.0 15.0 12.0 18.0]
                 :ci_lo [8.0 12.0 9.5 15.5]
                 :ci_hi [12.0 18.0 14.5 20.5]})

(-> (sketch experiment)
    (view :condition :mean)
    (lay :point {:size 5})
    (lay :errorbar {:ymin :ci_lo :ymax :ci_hi})
    plot)

;; Two methods on the same view — point + errorbar. Each carries
;; its own options. The cross product is 1 view × 2 methods = 2 entries.

;; ### Asymmetric grid

;; `:x` varies (3 values), `:y` is fixed. Three panels, same y-axis.

(-> (sketch iris)
    (bind :x [:sepal_length :petal_length :petal_width])
    (bind :y :sepal_width)
    (bind :color :species)
    (lay :point)
    plot)

;; ### Coordinate flip

(-> (sketch iris)
    (view :species :sepal_width)
    (lay :boxplot)
    (bind :coord :flip)
    plot)

;; ### Log scale

(def wide-range {:x [1 10 100 1000 10000]
                 :y [2 20 200 2000 20000]})

(-> (sketch wide-range)
    (view :x :y)
    (lay :point)
    (bind :x-scale {:type :log})
    (bind :y-scale {:type :log})
    plot)

;; ### Histogram (1D — bind :x = :y)

(-> (sketch iris)
    (bind :x :sepal_length)
    (bind :y :sepal_length)
    (bind :color :species)
    plot)

;; No method bound → inference detects x=y → histogram.

;; ### Reusable style via sketch merging

(defn merge-sketches
  "Merge two sketches. Methods accumulate, other bindings prefer sk2."
  [sk1 sk2]
  {:data (or (:data sk2) (:data sk1))
   :bindings (merge-with (fn [a b]
                           (if (and (vector? a) (vector? b) (some map? a))
                             (vec (concat a b))
                             b))
                         (:bindings sk1) (:bindings sk2))
   :opts (merge (:opts sk1) (:opts sk2))})

(def regression-style
  {:data nil
   :bindings {:mark [{:mark :point :alpha 0.4}
                     {:mark :lm :se true}]
              :color [:species]}
   :opts {}})

;; Apply the same style to different data and columns:

(-> (merge-sketches
     (-> (sketch iris) (view :sepal_length :sepal_width))
     regression-style)
    plot)

(-> (merge-sketches
     (-> (sketch iris) (view :petal_length :petal_width))
     regression-style)
    plot)

;; ### Incremental building

;; Save a partial sketch, extend it in different directions:

(def base (-> (sketch iris)
              (bind :color :species)
              (lay :point {:alpha 0.5})))

;; Add regression:
(-> base (lay :lm) (view :sepal_length :sepal_width) plot)

;; Add LOESS instead:
(-> base (lay :loess) (view :sepal_length :sepal_width) plot)

;; Different columns entirely:
(-> base (lay :lm) (view :petal_length :petal_width) plot)

;; ### SPLOM — 4 columns, 16 panels

(def all-cols [:sepal_length :sepal_width :petal_length :petal_width])

(-> (sketch iris)
    (bind :x all-cols)
    (bind :y all-cols)
    (bind :color :species)
    plot)

;; 16 panels. Diagonal: histograms. Off-diagonal: scatters. All inferred.

;; ### Mixed chart types via direct binding

;; Histogram + scatter on different columns, in one sketch:
;; (This shows that sk/lay-* with different columns creates
;; independent entries, not cross products.)

(-> (sketch iris)
    (view :sepal_length :sepal_width {:color :species})
    (lay :point)
    plot)

(-> (sketch iris)
    (view :sepal_length :sepal_length {:color :species})
    plot)

;; These are separate plots. Combining them in one sketch
;; would need the "specifics" mechanism from sketch3 —
;; pinned entries that bypass the cross product.

;; ### Inspect the bindings — everything is data

(-> (sketch iris)
    (bind :x [:sepal_length :petal_length])
    (bind :y :sepal_width)
    (bind :color :species)
    (lay :point {:alpha 0.5})
    (lay :lm)
    :bindings
    kind/pprint)

;; A plain map. You can inspect it, merge it, serialize it.

;; ### Inspect the resolved entries

(-> (sketch iris)
    (bind :x [:sepal_length :petal_length])
    (bind :y :sepal_width)
    (bind :color :species)
    (lay :point {:alpha 0.5})
    (lay :lm)
    resolve-sketch
    kind/pprint)

;; 4 entries: 2 x-values × 2 methods. Each entry is a flat map
;; ready for the rendering pipeline.

;; ## Summary
;;
;; | Concept | What | How |
;; |:--------|:-----|:----|
;; | **Sketch** | dataset + bindings | `(sketch data)` |
;; | **Bind** | map channel → value(s) | `(bind sk :channel value)` |
;; | **View** | bind :x :y + aesthetics | `(view sk :x :y {:color :c})` |
;; | **Lay** | accumulate a method | `(lay sk :point {:alpha 0.3})` |
;; | **Cross** | generate value sets | `(cross cols cols)` |
;; | **Resolve** | expand cross product | `(resolve-sketch sk)` |
;;
;; A channel with one value is **shared**.
;; A channel with many values creates **variation**.
;; Layout variation → panels. Method variation → layers.
;; Aesthetic variation → mapped within layers.
;;
;; The rendering is:
;;
;; ```
;; entries = panels × layers
;; panels = cross product of varying layout channels
;; layers = values of :mark
;; each entry = fixed channels + panel bindings + method bindings
;; ```
;;
;; Unbound `:mark` → inference per entry.
;;
;; Everything is data. Everything composes through `->`.
;; `view` and `lay` are the familiar verbs, but underneath
;; they're both just `bind`.
