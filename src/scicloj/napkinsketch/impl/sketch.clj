(ns scicloj.napkinsketch.impl.sketch
  "Sketch: the composable data model and views-to-plan pipeline.
   A sketch is a record with :data :mapping :views :layers :opts.
   Resolution: merge(sketch-mapping, view-mapping, method-info, layer-mapping)
   -> flat view maps -> plan."
  (:require [wadogo.scale :as ws]
            [java-time.api :as jt]
            [tablecloth.api :as tc]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [tech.v3.datatype.casting :as casting]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.stat :as stat]
            [scicloj.napkinsketch.impl.scale :as scale]
            [scicloj.napkinsketch.impl.coord :as coord]
            [scicloj.napkinsketch.impl.position :as position]
            [scicloj.napkinsketch.impl.extract :as extract]
            [scicloj.napkinsketch.impl.sketch-schema :as ss]
            [scicloj.napkinsketch.impl.render :as render-impl]
            [scicloj.napkinsketch.method :as method]
            [scicloj.kindly.v4.kind :as kind]))

(declare views->plan)

;; ---- Record ----
;;
;; Sketch [data mapping views layers opts]
;;
;; :data     -- dataset (sketch-level)
;; :mapping  -- sketch-level aesthetic mappings (visible to all views, all layers)
;; :views    -- vector of view maps, each with :mapping, :layers, optional :data
;; :layers   -- sketch-level layers (crossed with all views)
;; :opts     -- plot options + scale/coord/facet/annotations
;;
;; Three scope levels: sketch -> view -> layer.
;; Mappings flow downward; lower overrides higher (lexical scope).

(defrecord Sketch [data mapping views layers opts])

(defn sketch?
  "True if x is a sketch."
  [x]
  (instance? Sketch x))

;; ---- Resolution ----

(defn- ensure-dataset
  "Coerce raw data to a Tablecloth dataset. Returns nil for nil input."
  [d]
  (when d (if (tc/dataset? d) d (tc/dataset d))))

(defn- expand-facets
  "Expand views by facet columns from opts into per-value views.
   Each faceted view gets filtered :data and string :facet-col/:facet-row labels."
  [views data facet-col facet-row]
  (let [nc? (view/column-ref? facet-col)
        nr? (view/column-ref? facet-row)]
    (if-not (or nc? nr?)
      views
      (vec
       (mapcat
        (fn [view]
          (let [ds (ensure-dataset (or (:data view) data))]
            (cond
              (and nc? nr?)
              (let [fcol (view/resolve-col-name ds facet-col)
                    frow (view/resolve-col-name ds facet-row)]
                (for [cv (distinct (ds fcol)) rv (distinct (ds frow))]
                  (assoc view
                         :facet-col (str cv) :facet-row (str rv)
                         :data (tc/select-rows ds (fn [r] (and (= (r fcol) cv) (= (r frow) rv)))))))

              nc?
              (let [fcol (view/resolve-col-name ds facet-col)]
                (for [cv (distinct (ds fcol))]
                  (assoc view
                         :facet-col (str cv)
                         :data (tc/select-rows ds (fn [r] (= (r fcol) cv))))))

              nr?
              (let [frow (view/resolve-col-name ds facet-row)]
                (for [rv (distinct (ds frow))]
                  (assoc view
                         :facet-row (str rv)
                         :data (tc/select-rows ds (fn [r] (= (r frow) rv)))))))))
        views)))))

(defn- resolve-method-info
  "Look up method info from a layer's :method key.
   Keyword -> registry lookup. Map -> pass through. :infer -> sentinel."
  [method-key]
  (cond
    (= :infer method-key)
    {:mark :infer}

    (keyword? method-key)
    (let [m (method/lookup method-key)]
      (or (not-empty (select-keys (or m {}) [:mark :stat :position]))
          {:mark method-key :stat :identity}))

    :else ;; raw map -- pass through
    method-key))

(defn- validate-columns
  "Validate that referenced x/y columns exist in the dataset.
   Checks both keyword and string forms for cross-type compatibility."
  [resolved d]
  (when d
    (let [col-names (set (tc/column-names d))
          x-col (:x resolved)
          y-col (:y resolved)
          check-pairs (cond-> []
                        (and x-col (view/column-ref? x-col))
                        (conj [:x x-col])
                        (and y-col (view/column-ref? y-col) (not= y-col x-col))
                        (conj [:y y-col]))]
      (doseq [[role col] check-pairs
              :when (and (not (col-names col))
                         (not (and (keyword? col) (col-names (name col))))
                         (not (and (string? col) (col-names (keyword col)))))]
        (throw (ex-info (str "Column " col " (from " role ") not found in dataset. Available: " (sort col-names))
                        {:key role :column col :available (sort col-names)}))))))

(defn resolve-sketch
  "Resolve a sketch into a flat vector of view maps for views->plan.
   Reads facet/scale/coord from opts. Expands facets on views.
   Crosses views x layers: each view's own :layers ∪ sketch :layers.
   Merges mappings downward: sketch-mapping < view-mapping < method-info < layer-mapping.
   Annotation views (with annotation marks in own layers) skip sketch layers.
   Views with no applicable layers get {:mark :infer} for downstream inference."
  [{:keys [data mapping views layers opts]}]
  (let [;; Plot-level settings from opts
        x-scale (:x-scale opts)
        y-scale (:y-scale opts)
        coord-type (:coord opts)
        ;; Expand facets
        expanded (expand-facets views data (:facet-col opts) (:facet-row opts))
        sketch-layers layers
        sketch-mapping (or mapping {})]
    (let [idx (atom -1)]
      (vec
       (mapcat
        (fn [view]
          (let [view-idx (swap! idx inc)
                view-mapping (or (:mapping view) {})
                view-layers (:layers view)
                view-data (:data view)
                ;; Annotation views: own layers contain annotation marks
                ann-view? (some (fn [layer]
                                  (let [mk (:method layer)]
                                    (view/annotation-marks
                                     (if (keyword? mk) mk (:mark mk)))))
                                view-layers)
                ;; Combine: view layers ∪ sketch layers (unless annotation)
                combined (if ann-view?
                           view-layers
                           (concat (or view-layers nil) sketch-layers))
                applicable (if (seq combined) (vec combined) [{:method :infer}])]
            (map (fn [layer]
                   (let [method-info (resolve-method-info (:method layer))
                         layer-mapping (or (:mapping layer) {})
                         ;; Four-level merge: sketch < view < method < layer
                         ;; Method sets mark/stat/position; layer can override all
                         resolved (merge sketch-mapping
                                         view-mapping
                                         method-info
                                         layer-mapping)
                         ;; Data: layer > view > sketch
                         d (ensure-dataset (or (:data layer) view-data data))]
                     (validate-columns resolved d)
                     (-> resolved
                         (assoc :data d
                                :__entry-idx view-idx)
                         ;; Stamp plot-level settings from opts
                         (cond->
                          x-scale (assoc :x-scale x-scale)
                          y-scale (assoc :y-scale y-scale)
                          coord-type (assoc :coord coord-type)
                          (:facet-col view) (assoc :facet-col (:facet-col view))
                          (:facet-row view) (assoc :facet-row (:facet-row view)))
                         ;; Mark inference: remove :mark/:stat so downstream infers
                         (cond-> (= :infer (:mark resolved))
                           (-> (dissoc :mark :stat))))))
                 applicable)))
        expanded)))))

;; ---- Rendering ----

(defn render-sketch
  "Render a sketch via the grid pipeline.
   Called by Clay via kind/fn at display time.
   Restores config snapshot if present.
   When opts contain :format :bufimg, renders to BufferedImage (via
   membrane's Java2D backend) instead of SVG. Clay displays BufferedImage
   values as inline images automatically."
  [sk]
  (let [captured (:config-snapshot sk)
        render-fn (fn []
                    (let [opts (:opts sk {})
                          fmt (or (:format opts) :svg)
                          views (resolve-sketch sk)
                          plan (views->plan views opts)]
                      (if (= fmt :bufimg)
                        (do
                          ;; Ensure the bufimg renderer is loaded
                          (require 'scicloj.napkinsketch.render.bufimg)
                          (render-impl/plan->figure plan :bufimg opts))
                        (let [rendered (render-impl/plan->figure plan :svg opts)]
                          (kind/hiccup [:div {:style {:margin-bottom "1em"}} rendered])))))]
    (if captured
      (binding [defaults/*config* captured]
        (render-fn))
      (render-fn))))

;; ---- Constructor ----

(defn ->sketch
  "Create a sketch annotated with kind/fn for auto-rendering.
   Snapshots current *config* for with-config support."
  [data mapping views layers opts]
  (kind/fn (cond-> (assoc (->Sketch data mapping views layers opts)
                          :kindly/f #'render-sketch)
             defaults/*config* (assoc :config-snapshot defaults/*config*))))

;; ---- Domain Helpers ----

(defn collect-domain
  "Collect and merge domains from stat results along axis-key."
  [stat-results axis-key scale-spec]
  (let [vals (mapcat (fn [sr]
                       (let [d (axis-key sr)]
                         (if (and (= 2 (count d)) (number? (first d)))
                           d (map str d))))
                     stat-results)]
    (when (seq vals)
      (if (number? (first vals))
        (scale/pad-domain [(reduce min vals) (reduce max vals)] scale-spec)
        (distinct vals)))))

(defn compute-global-y-domain
  "Compute global y-domain from position-adjusted layers.
   Reads pre-computed :y0/:y1 from stacked layers. Extends domain to
   include 0 for marks that draw from baseline (area, lollipop, value-bar).
   Clamps the lower bound to 0 for these marks so padding doesn't extend
   below the baseline."
  [layers scale-spec]
  (let [fill-layers (filter #(= :fill (:position %)) layers)
        stack-layers (filter #(= :stack (:position %)) layers)
        zero-baseline-marks #{:lollipop :value-bar :area}
        needs-zero? (some #(zero-baseline-marks (:mark %)) layers)
        extend-to-zero (fn [[lo hi]] [(min 0.0 (double lo)) (max 0.0 (double hi))])]
    (cond
      ;; Fill mode: normalized to [0, 1]
      (seq fill-layers)
      [0.0 1.0]

      ;; Stack mode: read pre-computed y0/y1 values from adjusted layers
      (seq stack-layers)
      (let [;; Stacked rect: collect all y0 and y1 values
            rect-vals (for [l stack-layers
                            :when (:categories l)
                            g (:groups l)
                            {:keys [y0 y1]} (:counts g)
                            v [y0 y1]
                            :when v]
                        v)
            ;; Stacked area: all ys and y0s (already accumulated)
            area-vals (for [l stack-layers
                            :when (and (not (:categories l)) (:groups l))
                            g (:groups l)
                            y (concat (:ys g) (or (:y0s g) []))]
                        y)
            ;; Other (non-stacked) layers: use their y-domain
            other-yd (mapcat (fn [l]
                               (when-not (#{:stack :fill} (:position l))
                                 (:y-domain l)))
                             layers)
            ;; Always include 0 -- stacked bars are drawn from the baseline
            all-vals (concat rect-vals area-vals other-yd [0])
            lo (double (reduce min all-vals))
            hi (double (reduce max all-vals))]
        (if (< lo hi)
          (scale/pad-domain [lo hi] scale-spec)
          [0 1]))

      ;; Normal: collect y-domains from layers
      :else
      (let [all-yds (keep :y-domain layers)
            vals (mapcat (fn [d]
                           (if (and (= 2 (count d)) (number? (first d)))
                             d (map str d)))
                         all-yds)]
        (when (seq vals)
          (if (number? (first vals))
            (let [raw-lo (reduce min vals)
                  raw-hi (reduce max vals)]
              (if needs-zero?
                ;; Baseline marks: include zero, only pad away from zero.
                (let [lo (min 0.0 raw-lo)
                      hi (max 0.0 raw-hi)
                      [plo phi] (scale/pad-domain [lo hi] scale-spec)]
                  [(if (>= raw-lo 0.0) 0.0 plo)
                   (if (<= raw-hi 0.0) 0.0 phi)])
                (scale/pad-domain [raw-lo raw-hi] scale-spec)))
            (distinct vals)))))))

;; ---- Tick Computation ----

(defn- merge-temporal-extents
  "Merge temporal extents from multiple views into a single [min max] pair."
  [extents]
  (let [extents (remove nil? extents)]
    (when (seq extents)
      [(apply jt/min (map first extents))
       (apply jt/max (map second extents))])))

(defn compute-ticks
  "Compute tick values and labels for a domain+pixel range, using wadogo transiently.
   When temporal-extent is provided (a [min max] pair of temporal objects),
   uses wadogo :datetime scale for calendar-aware ticks and formatting."
  ([domain pixel-range scale-spec spacing]
   (compute-ticks domain pixel-range scale-spec spacing nil))
  ([domain pixel-range scale-spec spacing temporal-extent]
   (if (scale/categorical-domain? domain)
     (let [s (scale/make-scale domain pixel-range scale-spec)]
       {:values (vec (ws/ticks s))
        :labels (mapv str (ws/ticks s))
        :categorical? true})
     (let [n (scale/tick-count (Math/abs (double (- (second pixel-range) (first pixel-range)))) spacing)
           log? (= :log (:type scale-spec))]
       (if temporal-extent
         ;; Temporal: use wadogo :datetime scale for calendar-aware ticks
         (if (= (first temporal-extent) (second temporal-extent))
           ;; Single-value temporal domain: one tick at the single value
           {:values [(first domain)] :labels [(str (first temporal-extent))] :categorical? false}
           (let [dt-scale (ws/scale :datetime {:domain temporal-extent :range [0.0 1.0]})
                 dt-ticks (ws/ticks dt-scale n)
                 labels (vec (ws/format dt-scale dt-ticks))
                 values (mapv view/temporal->epoch-ms dt-ticks)]
             {:values values :labels labels :categorical? false}))
         ;; Numeric: use linear/log scale
         (if log?
           ;; Log: use ggplot2-style 1-2-5 nice breaks
           (let [ticks (scale/log-ticks domain n)
                 labels (scale/format-log-ticks ticks)]
             {:values (vec ticks) :labels (vec labels) :categorical? false})
           ;; Linear: use wadogo
           (let [s (scale/make-scale domain pixel-range scale-spec)
                 ticks (ws/ticks s n)
                 labels (scale/format-ticks s ticks)]
             {:values (vec ticks) :labels (vec labels) :categorical? false})))))))

;; ---- Per-Panel Resolution ----

(defn- filter-log-nonpositive
  "Filter rows with non-positive values on log-scaled axes.
   When :x-scale or :y-scale is {:type :log}, removes rows where
   the corresponding column has values <= 0 and prints a warning.
   Throws a clear error if log scale is applied to non-numeric data.
   Returns the resolved view with filtered :data."
  [rv]
  (let [ds (:data rv)]
    (if-not (tc/dataset? ds)
      rv
      (let [x-log? (= :log (:type (:x-scale rv)))
            y-log? (= :log (:type (:y-scale rv)))
            x-col (when (and x-log? (keyword? (:x rv))) (:x rv))
            y-col (when (and y-log? (keyword? (:y rv))) (:y rv))
            _ (when (and x-col (ds x-col)
                         (not (casting/numeric-type? (dtype/elemwise-datatype (ds x-col)))))
                (throw (ex-info (str "Log scale requires numeric data, but column " x-col " is non-numeric.")
                                {:column x-col :type (dtype/elemwise-datatype (ds x-col))})))
            _ (when (and y-col (ds y-col)
                         (not (casting/numeric-type? (dtype/elemwise-datatype (ds y-col)))))
                (throw (ex-info (str "Log scale requires numeric data, but column " y-col " is non-numeric.")
                                {:column y-col :type (dtype/elemwise-datatype (ds y-col))})))
            n-before (tc/row-count ds)
            ds (if (and x-col (ds x-col))
                 (tc/select-rows ds (dfn/> (ds x-col) 0))
                 ds)
            ds (if (and y-col (ds y-col))
                 (tc/select-rows ds (dfn/> (ds y-col) 0))
                 ds)
            n-after (tc/row-count ds)
            removed (- n-before n-after)]
        (when (pos? removed)
          (let [axes (cond
                       (and x-col y-col) (str x-col " and " y-col)
                       x-col (str x-col)
                       :else (str y-col))]
            (println (str "Warning: Removed " removed " rows containing non-positive values (log scale on " axes ")."))))
        (if (pos? removed)
          (assoc rv :data ds)
          rv)))))

(defn- filter-infinities
  "Filter rows containing non-finite values on numeric x or y columns.
   Removes rows where the x or y column contains nil, NaN, Inf, or -Inf
   and prints an appropriate warning. Non-numeric columns are skipped.
   Returns the resolved view with filtered :data."
  [rv]
  (let [ds (:data rv)]
    (if-not (tc/dataset? ds)
      rv
      (let [x-col (when (keyword? (:x rv)) (:x rv))
            y-col (when (keyword? (:y rv)) (:y rv))
            x-numeric? (and x-col (ds x-col)
                            (casting/numeric-type? (dtype/elemwise-datatype (ds x-col))))
            y-numeric? (and y-col (ds y-col)
                            (casting/numeric-type? (dtype/elemwise-datatype (ds y-col))))
            n-before (tc/row-count ds)
            ;; First pass: drop missing (nil/NaN)
            cols-to-check (cond-> []
                            x-numeric? (conj x-col)
                            y-numeric? (conj y-col))
            ds (if (seq cols-to-check)
                 (tc/drop-missing ds cols-to-check)
                 ds)
            n-after-missing (tc/row-count ds)
            n-missing (- n-before n-after-missing)
            ;; Second pass: drop infinite
            ds (if x-numeric?
                 (tc/select-rows ds (dfn/finite? (ds x-col)))
                 ds)
            ds (if y-numeric?
                 (tc/select-rows ds (dfn/finite? (ds y-col)))
                 ds)
            n-after (tc/row-count ds)
            n-infinite (- n-after-missing n-after)
            removed (+ n-missing n-infinite)]
        (when (pos? removed)
          (let [parts (cond-> []
                        (pos? n-missing) (conj (str n-missing " missing (nil/NaN)"))
                        (pos? n-infinite) (conj (str n-infinite " non-finite (Inf/-Inf)")))]
            (println (str "Warning: Removed " removed " rows with non-finite values: "
                          (clojure.string/join ", " parts) "."))))
        (if (pos? removed)
          (assoc rv :data ds)
          rv)))))

(defn resolve-panel-views
  "Resolve views and compute stats for a group of views belonging to one panel.
   If pre-resolved views are provided, skips resolve-view.
   Returns {:resolved [...] :stat-results [...] :layers [...]}."
  [panel-views all-colors cfg & {:keys [resolved]}]
  (let [resolved (or resolved (mapv (comp filter-log-nonpositive filter-infinities view/resolve-view) panel-views))
        stat-results (mapv #(stat/compute-stat (assoc % :cfg (merge cfg (:cfg %)))) resolved)
        raw-layers (vec (map (fn [rv sr]
                               (-> (view/map->Layer (extract/extract-layer rv sr all-colors cfg))
                                   (assoc :y-domain (:y-domain sr)
                                          :x-domain (:x-domain sr))))
                             resolved stat-results))
        layers (position/apply-positions raw-layers)]
    {:resolved resolved :stat-results stat-results :layers layers}))

(defn- collect-colors
  "Resolve views and collect color categories across all views.
   Attaches :__resolved to each view for downstream re-use.
   Filters infinite values and non-positive values on log-scaled axes.
   Returns {:resolved-all :numeric-color? :all-colors :color-cols :tagged-views}."
  [non-ann-views]
  (let [resolved-all (mapv (comp filter-log-nonpositive filter-infinities view/resolve-view) non-ann-views)
        tagged-views (mapv (fn [v rv] (assoc v :__resolved rv)) non-ann-views resolved-all)
        numeric-color? (some #(= :numerical (:color-type %)) resolved-all)
        all-colors (when-not numeric-color?
                     (let [color-views (filter #(and (view/column-ref? (:color %))
                                                     (:data %)) resolved-all)]
                       (when (seq color-views)
                         (vec (distinct (remove nil? (mapcat #((:data %) (:color %)) color-views)))))))
        color-cols (distinct (keep #(when (view/column-ref? (:color %)) (:color %)) resolved-all))]
    {:resolved-all resolved-all
     :numeric-color? numeric-color?
     :all-colors all-colors
     :color-cols color-cols
     :tagged-views tagged-views}))

(defn- adjust-fixed-aspect
  "Adjust panel dimensions for coord :fixed so that 1 data unit = 1 data unit
   on both axes. Shrinks the larger dimension to match the data aspect ratio."
  [pw ph x-domain y-domain]
  (let [x-range (- (double (second x-domain)) (double (first x-domain)))
        y-range (- (double (second y-domain)) (double (first y-domain)))]
    (if (or (<= x-range 0) (<= y-range 0))
      {:pw pw :ph ph}
      (let [data-ratio (/ x-range y-range)
            panel-ratio (/ pw ph)]
        (if (> panel-ratio data-ratio)
          {:pw (* ph data-ratio) :ph ph}
          {:pw pw :ph (/ pw data-ratio)})))))

(defn- compute-panel-dims
  "Compute per-panel pixel dimensions and margin."
  [cfg layout-type grid-rows grid-cols width height]
  (let [multi? (and (= layout-type :multi-variable) (> grid-cols 1) (> grid-rows 1))
        m (if multi? (:margin-multi cfg) (:margin cfg))
        pw (if multi?
             (double (:panel-size cfg))
             (double (/ width grid-cols)))
        ph (if multi?
             (double (:panel-size cfg))
             (double (/ height grid-rows)))]
    {:m m :pw pw :ph ph}))

(defn- resolve-labels
  "Resolve effective title and axis labels.
   Title comes from opts only; axis labels fall back to view-level :x-label/:y-label
   (set via sk/options), then scale :label, then auto-inferred column name."
  [non-ann-views x-vars y-vars x-scale-spec y-scale-spec
   title x-label y-label auto-label?]
  (let [view-x-label (:x-label (first non-ann-views))
        view-y-label (:y-label (first non-ann-views))]
    {:eff-title title
     :eff-x-label (or x-label
                      view-x-label
                      (:label x-scale-spec)
                      (when auto-label?
                        (when-let [x (first x-vars)] (defaults/fmt-name x))))
     :eff-y-label (or y-label
                      view-y-label
                      (:label y-scale-spec)
                      (when auto-label?
                        (when-let [y (first y-vars)]
                          (when (not= y (first x-vars))
                            (defaults/fmt-name y)))))}))

(defn- build-legend
  "Build legend from resolved views and color info."
  [resolved-all numeric-color? all-colors color-cols cfg]
  (let [grad-fn (:gradient-fn cfg)]
    (cond
      numeric-color?
      (let [color-views (filter #(and (view/column-ref? (:color %))
                                      (:data %)) resolved-all)
            all-bufs (map #((:data %) (:color %)) color-views)
            all-vals (dtype/concat-buffers all-bufs)
            c-min (dfn/reduce-min all-vals)
            c-max (dfn/reduce-max all-vals)
            n-stops 20]
        {:title (first color-cols)
         :type :continuous
         :min c-min :max c-max
         :color-scale (:color-scale cfg)
         :stops (vec (for [i (range n-stops)
                           :let [t (/ (double i) (dec n-stops))]]
                       {:t t :color (grad-fn t)}))})
      all-colors
      {:title (first color-cols)
       :entries (vec (for [cat all-colors]
                       {:label (str cat)
                        :color (defaults/color-for all-colors cat (:palette cfg))}))})))

(defn- nice-legend-values
  "Generate n nicely-spaced values spanning [lo, hi].
   Uses enough decimal places so that adjacent values are distinct."
  [lo hi n]
  (let [lo (double lo) hi (double hi)
        step (/ (- hi lo) (dec n))
        ;; Use 1 more decimal than the step magnitude to avoid collisions
        decimals (if (pos? step)
                   (min 6 (max 1 (+ 1 (long (Math/ceil (- (Math/log10 step)))))))
                   1)
        factor (Math/pow 10.0 decimals)]
    (mapv (fn [i]
            (let [v (+ lo (* i step))]
              (/ (Math/round (* v factor)) factor)))
          (range n))))

(defn- build-size-legend
  "Build size legend when :size maps to a numerical column."
  [resolved-all]
  (let [size-views (filter #(and (view/column-ref? (:size %))
                                 (nil? (:fixed-size %))
                                 (:data %)) resolved-all)]
    (when (seq size-views)
      (let [size-col (:size (first size-views))
            all-bufs (map #((:data %) (:size %)) size-views)
            all-vals (dtype/concat-buffers all-bufs)
            s-min (dfn/reduce-min all-vals)
            s-max (dfn/reduce-max all-vals)
            span (max 1e-6 (- (double s-max) (double s-min)))
            values (nice-legend-values s-min s-max 5)]
        {:title size-col
         :type :size
         :min s-min :max s-max
         :entries (vec (for [v values]
                         {:value v
                          :radius (+ 2.0 (* 6.0 (/ (- (double v) (double s-min)) span)))}))}))))

(defn- build-alpha-legend
  "Build alpha legend when :alpha maps to a numerical column."
  [resolved-all]
  (let [alpha-views (filter #(and (view/column-ref? (:alpha %))
                                  (nil? (:fixed-alpha %))
                                  (:data %)) resolved-all)]
    (when (seq alpha-views)
      (let [alpha-col (:alpha (first alpha-views))
            all-bufs (map #((:data %) (:alpha %)) alpha-views)
            all-vals (dtype/concat-buffers all-bufs)
            a-min (dfn/reduce-min all-vals)
            a-max (dfn/reduce-max all-vals)
            span (max 1e-6 (- (double a-max) (double a-min)))
            values (nice-legend-values a-min a-max 5)]
        {:title alpha-col
         :type :alpha
         :min a-min :max a-max
         :entries (vec (for [v values]
                         {:value v
                          :alpha (+ 0.2 (* 0.8 (/ (- (double v) (double a-min)) span)))}))}))))

(defn- compute-layout-dims
  "Compute layout dimensions: padding, legend width, total size."
  [cfg layout-type eff-title eff-x-label eff-y-label
   subtitle caption
   legend size-legend alpha-legend
   facet-row-vals facet-col-vals
   grid-rows grid-cols pw ph multi? panels legend-position]
  (let [x-label-pad (if eff-x-label (:label-offset cfg) 0)
        ;; y-label-pad must account for y-tick label width (e.g. category names)
        tick-fsize (get-in cfg [:theme :font-size] 8)
        max-y-tick-len (reduce max 0
                               (for [p panels
                                     :let [labels (get-in p [:y-ticks :labels])]
                                     label (or labels [])]
                                 (count label)))
        y-tick-width (* max-y-tick-len (/ (double tick-fsize) 2.0))
        y-label-pad (if eff-y-label
                      (+ (:label-offset cfg) (max 0.0 (- y-tick-width 12.0)))
                      0)
        title-pad (if eff-title
                    (+ (:title-offset cfg) (:title-font-size cfg))
                    0)
        subtitle-pad (if subtitle 16 0)
        caption-pad (if caption 18 0)
        legend-pos (or legend-position :right)
        any-legend? (or legend size-legend alpha-legend)
        legend-w (if (and any-legend? (= legend-pos :right)) (:legend-width cfg) 0)
        legend-h (if (and any-legend? (#{:bottom :top} legend-pos)) 30 0)
        has-col-strips? (or (and (= layout-type :facet-grid) (seq facet-col-vals))
                            multi?
                            (some :col-label panels))
        has-row-strips? (or (and (= layout-type :facet-grid) (seq facet-row-vals))
                            multi?
                            (some :row-label panels))
        strip-h (if has-col-strips? (:strip-height cfg 16) 0)
        ;; Compute strip-w from actual row label text lengths
        strip-w (if has-row-strips?
                  (let [max-row-label-len (reduce max 0
                                                  (for [p panels
                                                        :let [rl (:row-label p)]
                                                        :when rl]
                                                    (count rl)))
                        text-w (* max-row-label-len (/ (double tick-fsize) 1.8))]
                    (max 40.0 (+ text-w 12.0)))
                  0)
        top-legend-pad (if (= legend-pos :top) legend-h 0)]
    {:x-label-pad x-label-pad
     :y-label-pad y-label-pad
     :title-pad (+ title-pad subtitle-pad top-legend-pad)
     :subtitle-pad subtitle-pad
     :caption-pad caption-pad
     :legend-w legend-w
     :legend-h legend-h
     :strip-h strip-h
     :strip-w strip-w
     :total-w (+ y-label-pad (* grid-cols pw) strip-w legend-w)
     :total-h (+ title-pad subtitle-pad top-legend-pad strip-h (* grid-rows ph) x-label-pad
                 caption-pad
                 (if (= legend-pos :bottom) legend-h 0))}))

;; ---- Main Entry Point ----

(def ^:private polar-supported-marks
  "Marks that render correctly under polar coordinates."
  #{:point :bar :rect :text :rug})

(defn- validate-polar-marks
  "Check that all resolved views use marks compatible with polar coordinates.
   Throws an ex-info with details when an unsupported mark is found."
  [resolved-views coord-type]
  (when (= coord-type :polar)
    (doseq [v resolved-views
            :let [m (:mark v)]
            :when (and m (not (polar-supported-marks m)))]
      (throw (ex-info (str "Mark :" (name m) " is not supported with polar coordinates. "
                           "Supported polar marks: " (sort polar-supported-marks))
                      {:mark m :supported polar-supported-marks})))))

(defn- warn-conflicting-specs
  "Warn when views disagree about scale or coord specs.
   Only the first view's specs are used -- conflicting specs are silently ignored
   without this warning."
  [views]
  (let [x-types (distinct (keep (comp :type :x-scale) views))
        y-types (distinct (keep (comp :type :y-scale) views))
        coords (distinct (keep :coord views))]
    (when (> (count x-types) 1)
      (println (str "Warning: Views have conflicting x-scale types " (vec x-types)
                    ". Using first view's scale: " (first x-types) ".")))
    (when (> (count y-types) 1)
      (println (str "Warning: Views have conflicting y-scale types " (vec y-types)
                    ". Using first view's scale: " (first y-types) ".")))
    (when (> (count coords) 1)
      (println (str "Warning: Views have conflicting coord types " (vec coords)
                    ". Using first view's coord: " (first coords) ".")))))

;; ================================================================
;; Grid Layout
;; ================================================================

(defn- infer-grid
  "Infer grid structure from entry-grouped views.
   Each entry = one panel. Grid position determined by:
   - Faceted entries: :facet-col -> grid col, :facet-row -> grid row
   - Non-faceted entries: :x column -> grid col, :y column -> grid row
   Returns {:grid-cols N :grid-rows N :panels [{:views [...] :row R :col C ...}]}"
  [entry-groups {:keys [grid-cols grid-rows] :as user-grid}]
  (let [;; Detect faceting: check if any entry has :facet-col or :facet-row
        first-views (mapv #(first (:views %)) entry-groups)
        has-facet-col? (some :facet-col first-views)
        has-facet-row? (some :facet-row first-views)
        faceted? (or has-facet-col? has-facet-row?)

        ;; Collect grid axis values
        col-vals (if faceted?
                   (vec (distinct (keep :facet-col first-views)))
                   (or grid-cols (vec (distinct (map :x first-views)))))
        row-vals (if faceted?
                   (vec (distinct (keep :facet-row first-views)))
                   (or grid-rows (vec (distinct (map :y first-views)))))
        ;; For non-faceted with single x/y, use nil sentinels
        col-vals (if (empty? col-vals) [nil] col-vals)
        row-vals (if (empty? row-vals) [nil] row-vals)

        ;; Position each entry
        stack-counts (atom {})
        panels (vec
                (for [eg entry-groups
                      :let [v (first (:views eg))
                            ;; Determine grid position
                            [ci col-label]
                            (if has-facet-col?
                              (let [fc (:facet-col v)
                                    i (.indexOf ^java.util.List col-vals fc)]
                                [(max 0 i) (str fc)])
                              (let [xv (:x v)
                                    i (.indexOf ^java.util.List col-vals xv)
                                    ;; Only show col-label when multiple columns exist
                                    ;; (avoids redundant x-label on single-column layouts)
                                    show-label? (> (count col-vals) 1)]
                                [(max 0 i) (when (and xv show-label?) (defaults/fmt-name xv))]))
                            [ri row-label]
                            (if has-facet-row?
                              (let [fr (:facet-row v)
                                    i (.indexOf ^java.util.List row-vals fr)]
                                [(max 0 i) (str fr)])
                              (let [yv (:y v)
                                    i (.indexOf ^java.util.List row-vals yv)
                                    ;; Only show row-label when multiple rows exist
                                    ;; (avoids redundant y-label on single-row facets)
                                    show-label? (> (count row-vals) 1)]
                                [(max 0 i) (when (and yv show-label?) (defaults/fmt-name yv))]))
                            ;; Stacking: when multiple entries share the same
                            ;; grid position, offset each by one row below the base.
                            ;; sub=0 -> base position; sub>0 -> stacked below.
                            stack-key [ri ci]
                            sub (get @stack-counts stack-key 0)
                            _ (swap! stack-counts update stack-key (fnil inc 0))]]
                  {:views (:views eg)
                   :row (if (> sub 0) (+ (* ri (count col-vals)) sub) ri)
                   :col ci
                   :var-x (:x v) :var-y (:y v)
                   :col-label col-label
                   :row-label row-label}))
        ;; Compute actual grid dimensions
        max-row (if (seq panels) (inc (apply max (map :row panels))) 1)
        max-col (if (seq panels) (inc (apply max (map :col panels))) 1)
        layout-type (cond
                      faceted? :facet-grid
                      (and (= 1 max-row) (= 1 max-col)) :single
                      :else :multi-variable)]
    {:grid-cols max-col
     :grid-rows max-row
     :layout-type layout-type
     :x-vars (vec (distinct (map :x first-views)))
     :y-vars (vec (distinct (map :y first-views)))
     :facet-col-vals (when has-facet-col? col-vals)
     :facet-row-vals (when has-facet-row? row-vals)
     :panels panels}))

(defn views->plan
  "Pipeline: resolve views into a plan using entry-based grid layout.
   Each entry = one panel. Grid position from structural columns.
   Reuses existing stat computation, domain, tick, legend, and layout logic."
  ([views] (views->plan views {}))
  ([views {:keys [x-label y-label title subtitle caption
                  scales legend-position grid-cols grid-rows] :as opts}]
   (let [cfg (defaults/resolve-config opts)
         cfg (assoc cfg :gradient-fn (defaults/resolve-gradient-fn (:color-scale cfg)))
         validate? (:validate cfg true)
         width (:width cfg)
         height (:height cfg)
         views (if (map? views) [views] views)

         ;; Annotations come from opts (not from resolved views)
         non-ann-views views

         ;; Group resolved views by source view index
         view-groups (vec
                      (for [[idx vs] (sort-by key (group-by :__entry-idx non-ann-views))]
                        {:entry-idx idx :views (vec vs)}))

         ;; Infer grid from view groups
         grid (infer-grid view-groups
                          (cond-> {}
                            grid-cols (assoc :grid-cols grid-cols)
                            grid-rows (assoc :grid-rows grid-rows)))
         {:keys [layout-type x-vars y-vars]} grid
         grid-rows-n (:grid-rows grid)
         grid-cols-n (:grid-cols grid)

         ;; Colors
         {:keys [resolved-all numeric-color? all-colors color-cols tagged-views]}
         (collect-colors non-ann-views)

         ;; Default scale & coord specs (fallback for panels that don't specify)
         default-x-scale {:type :linear}
         default-y-scale {:type :linear}
         default-coord :cartesian
         ;; Representative specs for plot-level decisions (labels, ridgeline, fixed-aspect).
         ;; Per-panel specs override these in the panel-building loop below.
         rep-x-scale (or (:x-scale (first non-ann-views)) default-x-scale)
         rep-y-scale (or (:y-scale (first non-ann-views)) default-y-scale)
         rep-coord (or (:coord (first non-ann-views)) default-coord)
         _ (validate-polar-marks resolved-all rep-coord)
         _ (warn-conflicting-specs non-ann-views)

         ;; Annotations from opts
         annotations (mapv #(select-keys % [:mark :intercept :lo :hi :alpha :x :y])
                           (or (:annotations opts) []))

         ;; Panel dimensions (before fixed-aspect adjustment)
         {:keys [m pw ph]}
         (compute-panel-dims cfg layout-type grid-rows-n grid-cols-n width height)

         ;; Tag views for reuse (match tagged-views back to panels)
         tagged-by-idx (group-by :__entry-idx tagged-views)

         ;; Resolve each panel's views using tagged (color-resolved) versions
         panel-data (mapv
                     (fn [pg]
                       (let [eidx (:__entry-idx (first (:views pg)))
                             panel-tagged (or (get tagged-by-idx eidx)
                                              (:views pg))
                             pre-resolved (mapv :__resolved panel-tagged)]
                         (if (seq panel-tagged)
                           (merge pg (resolve-panel-views panel-tagged all-colors cfg
                                                          :resolved pre-resolved))
                           pg)))
                     (:panels grid))

         ;; Build panels with per-entry domains
         panels (vec
                 (for [pd panel-data
                       :when (seq (:views pd))]
                   (let [local-srs (:stat-results pd)
                         local-layers (:layers pd)
                         ;; Per-panel scale/coord from the panel's own views
                         panel-view (first (:views pd))
                         x-scale-spec (or (:x-scale panel-view) default-x-scale)
                         y-scale-spec (or (:y-scale panel-view) default-y-scale)
                         coord-type (or (:coord panel-view) default-coord)
                         ;; Per-entry domains -- each panel gets its own
                         x-dom (or (:domain x-scale-spec)
                                   (collect-domain local-srs :x-domain x-scale-spec))
                         y-dom (or (:domain y-scale-spec)
                                   (compute-global-y-domain local-layers y-scale-spec)
                                   [0 1]) ;; fallback for x-only panels (rug, histogram alone)
                         [x-dom' y-dom'] (if (= coord-type :flip)
                                           [y-dom x-dom]
                                           [x-dom y-dom])
                         [x-sspec' y-sspec'] (if (= coord-type :flip)
                                               [y-scale-spec x-scale-spec]
                                               [x-scale-spec y-scale-spec])
                         ;; Temporal extents
                         resolved-views (:resolved pd)
                         x-temp-ext (merge-temporal-extents (map :x-temporal-extent resolved-views))
                         y-temp-ext (merge-temporal-extents (map :y-temporal-extent resolved-views))
                         [x-te y-te] (if (= coord-type :flip)
                                       [y-temp-ext x-temp-ext]
                                       [x-temp-ext y-temp-ext])
                         x-px [m (- pw m)]
                         y-px [(- ph m) m]
                         x-ticks (when x-dom' (compute-ticks x-dom' x-px x-sspec' (:tick-spacing-x cfg) x-te))
                         y-ticks (when y-dom' (compute-ticks y-dom' y-px y-sspec' (:tick-spacing-y cfg) y-te))]
                     (cond-> {:x-domain (vec (if (sequential? x-dom') x-dom' [x-dom']))
                              :y-domain (vec (if (sequential? y-dom') y-dom' [y-dom']))
                              :x-scale x-sspec'
                              :y-scale y-sspec'
                              :coord coord-type
                              :x-ticks (or x-ticks {:values [] :labels [] :categorical? false})
                              :y-ticks (or y-ticks {:values [] :labels [] :categorical? false})
                              :layers (or local-layers [])
                              :row (:row pd)
                              :col (:col pd)}
                       (seq annotations)
                       (assoc :annotations
                              (let [panel-anns
                                    (filterv
                                     (fn [a]
                                       (and (or (nil? (:x a)) (= (:x a) (:var-x pd)))
                                            (or (nil? (:y a)) (= (:y a) (:var-y pd)))))
                                     annotations)]
                                (mapv #(dissoc % :facet-col :facet-row :x :y) panel-anns)))
                       (:row-label pd) (assoc :row-label (:row-label pd))
                       (:col-label pd) (assoc :col-label (:col-label pd))))))

         ;; Ridgeline axis swap -- the user specifies category on x and
         ;; numeric values on y, but a ridgeline renders categories
         ;; vertically (stacked rows) and density horizontally.
         ;; The pipeline resolves everything in the normal orientation
         ;; first, then swaps x/y here so the renderer draws curves
         ;; left-to-right with categories top-to-bottom.
         has-ridgeline? (some #(= :ridgeline (:mark %)) non-ann-views)
         panels (if has-ridgeline?
                  (mapv (fn [p]
                          (-> p
                              (assoc :x-domain (:y-domain p) :y-domain (:x-domain p)
                                     :x-ticks (:y-ticks p) :y-ticks (:x-ticks p)
                                     :x-scale (:y-scale p) :y-scale (:x-scale p))))
                        panels)
                  panels)

         ;; Adjust panel dims for coord :fixed
         [pw ph] (if (= rep-coord :fixed)
                   (let [p1 (first panels)
                         gx (:x-domain p1)
                         gy (:y-domain p1)]
                     (if (and (sequential? gx) (= 2 (count gx)) (number? (first gx))
                              (sequential? gy) (= 2 (count gy)) (number? (first gy)))
                       (let [{pw' :pw ph' :ph} (adjust-fixed-aspect pw ph gx gy)]
                         [pw' ph'])
                       [pw ph]))
                   [pw ph])

         ;; Labels
         multi? (and (= layout-type :multi-variable) (> grid-cols-n 1) (> grid-rows-n 1))
         auto-label? (and (not multi?) (coord/show-ticks? rep-coord))
         {:keys [eff-title eff-x-label eff-y-label]}
         (resolve-labels non-ann-views x-vars y-vars rep-x-scale rep-y-scale
                         title x-label y-label auto-label?)
         swap-labels? (or (= rep-coord :flip) has-ridgeline?)
         [eff-x-label eff-y-label] (if swap-labels?
                                     [eff-y-label eff-x-label]
                                     [eff-x-label eff-y-label])

         ;; Legends
         legend (build-legend resolved-all numeric-color? all-colors color-cols cfg)
         ;; If no color legend was built, check for tile layers with computed
         ;; fill colors (e.g., density2d, bin2d, or identity tiles with :fill).
         legend (or legend
                    (let [;; Path 1: stat produces :fill-range (kde2d, bin2d)
                          stat-fill-range (some (fn [pd]
                                                  (some :fill-range (:stat-results pd)))
                                                panel-data)
                          ;; Path 2: resolved view has a :fill column (identity tiles)
                          view-fill-range (when-not stat-fill-range
                                            (some (fn [rv]
                                                    (when (and (= :tile (:mark rv)) (:fill rv) (:data rv))
                                                      (let [vals ((:data rv) (:fill rv))]
                                                        (when (seq vals)
                                                          [(dfn/reduce-min vals) (dfn/reduce-max vals)]))))
                                                  resolved-all))
                          [f-lo f-hi] (or stat-fill-range view-fill-range)]
                      (when f-lo
                        (let [grad-fn (:gradient-fn cfg)
                              title (if stat-fill-range :density :fill)
                              n-stops 20]
                          {:title title
                           :type :continuous
                           :min f-lo :max f-hi
                           :color-scale (:color-scale cfg)
                           :stops (vec (for [i (range n-stops)
                                             :let [t (/ (double i) (dec n-stops))]]
                                         {:t t :color (grad-fn t)}))}))))
         size-legend (build-size-legend resolved-all)
         alpha-legend (build-alpha-legend resolved-all)

         ;; Layout dims
         layout-dims (compute-layout-dims cfg layout-type eff-title eff-x-label eff-y-label
                                          subtitle caption
                                          legend size-legend alpha-legend
                                          (:facet-row-vals grid) (:facet-col-vals grid)
                                          grid-rows-n grid-cols-n pw ph multi? panels legend-position)

         plan
         (view/map->Plan
          {:width width :height height :margin m
           :total-width (:total-w layout-dims) :total-height (:total-h layout-dims)
           :panel-width pw :panel-height ph
           :grid {:rows grid-rows-n :cols grid-cols-n}
           :layout-type layout-type
           :title eff-title :subtitle subtitle :caption caption
           :x-label eff-x-label :y-label eff-y-label
           :legend legend :size-legend size-legend :alpha-legend alpha-legend
           :legend-position (or legend-position :right)
           :panels panels
           :layout (select-keys layout-dims [:x-label-pad :y-label-pad :title-pad
                                             :subtitle-pad :caption-pad
                                             :legend-w :legend-h :strip-h :strip-w])})]
     (when validate?
       (when-let [explanation (ss/explain plan)]
         (throw (ex-info "Plan does not conform to schema" {:explanation explanation}))))
     plan)))

