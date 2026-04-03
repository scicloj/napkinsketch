(ns scicloj.napkinsketch.impl.sketch
  "Resolve views into a plan — a plain Clojure map with data-space
   geometry, domains, tick info, legend, and layout. No membrane types,
   no datasets, no scale objects in the output."
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
            [scicloj.napkinsketch.impl.sketch-schema :as ss]))

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
        clamp-zero (fn [[lo hi]] [(max 0.0 (double lo)) hi])]
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
            ;; Always include 0 — stacked bars are drawn from the baseline
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
                         all-yds)
            dom (when (seq vals)
                  (if (number? (first vals))
                    (scale/pad-domain [(reduce min vals) (reduce max vals)] scale-spec)
                    (distinct vals)))]
        (if (and needs-zero? (sequential? dom) (number? (first dom)))
          (clamp-zero (scale/pad-domain [(min 0.0 (double (first dom)))
                                         (max 0.0 (double (second dom)))]
                                        scale-spec))
          dom)))))

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
         (let [dt-scale (ws/scale :datetime {:domain temporal-extent :range [0.0 1.0]})
               dt-ticks (ws/ticks dt-scale n)
               labels (vec (ws/format dt-scale dt-ticks))
               values (mapv view/temporal->epoch-ms dt-ticks)]
           {:values values :labels labels :categorical? false})
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

;; ---- Unified Grid ----
;;
;; A grid has two axes (column and row). Each axis has:
;;   :key    — the view property that varies (:x, :y, :facet-col, :facet-row)
;;   :kind   — :structural (column variation) or :data (facet variation)
;;   :values — the distinct values on this axis
;;
;; Placement is one algorithm: match view's value at the axis key.
;; nil means broadcast (appear in all cells on that axis).
;; The "_" sentinel from sk/facet is normalized to nil at the boundary.

(defn- grid-coord
  "Get a view's coordinate for a grid axis, normalizing the \"_\" sentinel to nil.
   nil means broadcast (match all cells on this axis)."
  [view key]
  (when key
    (let [v (get view key)]
      (when (and (some? v) (not= "_" v))
        v))))

(defn- axis-values
  "Collect distinct real values of a key across views (excludes nil and \"_\")."
  [views key]
  (vec (distinct (keep #(grid-coord % key) views))))

(defn infer-grid
  "Infer grid structure from views. Returns a grid spec with:
   :col-axis, :row-axis — axis descriptors (or nil)
   :grid-cols, :grid-rows — dimensions
   :layout-type — :single, :multi-variable, or :facet-grid (backward compat)
   :x-vars, :y-vars, :facet-col-vals, :facet-row-vals (backward compat)"
  [views]
  (let [fcv (axis-values views :facet-col)
        frv (axis-values views :facet-row)
        x-vars (vec (distinct (map :x views)))
        y-vars (vec (distinct (map :y views)))

        col-axis (cond
                   (seq fcv) {:key :facet-col :kind :data :values fcv}
                   (> (count x-vars) 1) {:key :x :kind :structural :values x-vars}
                   :else nil)

        row-axis (cond
                   (seq frv) {:key :facet-row :kind :data :values frv}
                   (> (count y-vars) 1) {:key :y :kind :structural :values y-vars}
                   :else nil)

        grid-cols (if col-axis (count (:values col-axis)) 1)
        grid-rows (if row-axis (count (:values row-axis)) 1)

        layout-type (cond
                      (or (= :data (:kind col-axis))
                          (= :data (:kind row-axis))) :facet-grid
                      (or col-axis row-axis) :multi-variable
                      :else :single)]

    {:col-axis col-axis
     :row-axis row-axis
     :grid-cols grid-cols
     :grid-rows grid-rows
     :layout-type layout-type
     ;; Backward compat — downstream code uses these directly
     :x-vars x-vars
     :y-vars y-vars
     :facet-col-vals (when (= :data (:kind col-axis)) fcv)
     :facet-row-vals (when (= :data (:kind row-axis)) frv)}))

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
                         (vec (distinct (mapcat #((:data %) (:color %)) color-views))))))
        color-cols (distinct (keep #(when (view/column-ref? (:color %)) (:color %)) resolved-all))]
    {:resolved-all resolved-all
     :numeric-color? numeric-color?
     :all-colors all-colors
     :color-cols color-cols
     :tagged-views tagged-views}))

;; compute-grid is absorbed into infer-grid above

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
          ;; Panel is wider than data — shrink pw
          {:pw (* ph data-ratio) :ph ph}
          ;; Panel is taller than data — shrink ph
          {:pw pw :ph (/ pw data-ratio)})))))

(defn- group-panels
  "Place views into grid cells. One unified algorithm.
   Views without a coordinate on an axis broadcast into all cells on that axis."
  [views {:keys [col-axis row-axis]}]
  (let [col-vals (or (:values col-axis) [nil])
        row-vals (or (:values row-axis) [nil])
        col-key (:key col-axis)
        row-key (:key row-axis)]
    (vec
     (for [[ri rv] (map-indexed vector row-vals)
           [ci cv] (map-indexed vector col-vals)]
       (let [matching (filterv
                       #(and (or (nil? cv) (nil? (grid-coord % col-key)) (= cv (grid-coord % col-key)))
                             (or (nil? rv) (nil? (grid-coord % row-key)) (= rv (grid-coord % row-key))))
                       views)]
         {:views matching
          :row ri :col ci
          ;; For multi-variable domain sharing (downstream compat)
          :var-x (when (= :x col-key) cv)
          :var-y (when (= :y row-key) rv)
          ;; Labels — structural axes use column names, data axes use values
          :col-label (when cv
                       (if (= :structural (:kind col-axis))
                         (defaults/fmt-name cv)
                         (str cv)))
          :row-label (when rv
                       (if (= :structural (:kind row-axis))
                         (defaults/fmt-name rv)
                         (str rv)))})))))

(defn- build-panels
  "Build panel specs with domains, ticks, and annotations."
  [panel-data layout-type scale-mode coord-type
   x-scale-spec y-scale-spec annotations
   x-vars y-vars pw ph m cfg]
  (let [all-stat-results (mapcat :stat-results panel-data)
        all-layers (mapcat :layers panel-data)
        global-x-dom (or (:domain x-scale-spec)
                         (collect-domain all-stat-results :x-domain x-scale-spec))
        global-y-dom (or (:domain y-scale-spec)
                         (compute-global-y-domain all-layers y-scale-spec))
        ;; Per-column/row domains — used for multi-variable and mixed grids
        ;; (any grid where structural axes produce :var-x/:var-y on panels)
        has-var-x? (some :var-x panel-data)
        has-var-y? (some :var-y panel-data)
        mv-col-x-doms (when has-var-x?
                        (into {} (for [xv x-vars]
                                   [xv (let [pds (filter #(= xv (:var-x %)) panel-data)
                                             scatter-pds (filter #(not= (:var-x %) (:var-y %)) pds)
                                             srs (mapcat :stat-results scatter-pds)]
                                         (when (seq srs)
                                           (collect-domain srs :x-domain x-scale-spec)))])))
        mv-row-y-doms (when has-var-y?
                        (into {} (for [yv y-vars]
                                   [yv (let [pds (filter #(= yv (:var-y %)) panel-data)
                                             scatter-pds (filter #(not= (:var-x %) (:var-y %)) pds)
                                             srs (mapcat :stat-results scatter-pds)]
                                         (when (seq srs)
                                           (collect-domain srs :y-domain y-scale-spec)))])))]
    (vec
     (for [pd panel-data
           :when (seq (:views pd))]
       (let [local-srs (:stat-results pd)
             local-x-dom (collect-domain local-srs :x-domain x-scale-spec)
             local-y-dom (compute-global-y-domain (:layers pd) y-scale-spec)
             [eff-x-dom eff-y-dom]
             (case layout-type
               :single
               [global-x-dom global-y-dom]
               :facet-grid
               [(if (and has-var-x? (:var-x pd))
                  ;; Mixed grid: structural x-axis → per-column domains
                  (or (get mv-col-x-doms (:var-x pd)) local-x-dom)
                  (case scale-mode
                    (:shared :free-y) global-x-dom
                    local-x-dom))
                (if (and has-var-y? (:var-y pd))
                  ;; Mixed grid: structural y-axis → per-row domains
                  (or (get mv-row-y-doms (:var-y pd)) local-y-dom)
                  (case scale-mode
                    (:shared :free-x) global-y-dom
                    local-y-dom))]
               :multi-variable
               (let [diagonal? (= (:var-x pd) (:var-y pd))]
                 [(or (get mv-col-x-doms (:var-x pd)) local-x-dom)
                  (if diagonal? local-y-dom
                      (or (get mv-row-y-doms (:var-y pd)) local-y-dom))]))
             [x-dom' y-dom'] (if (= coord-type :flip)
                               [eff-y-dom eff-x-dom]
                               [eff-x-dom eff-y-dom])
             [x-sspec' y-sspec'] (if (= coord-type :flip)
                                   [y-scale-spec x-scale-spec]
                                   [x-scale-spec y-scale-spec])
             ;; Collect temporal extents from resolved views
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
                  :layers (or (:layers pd) [])
                  :row (:row pd)
                  :col (:col pd)}
           ;; Filter annotations by grid coordinates — nil = broadcast
           (seq annotations)
           (assoc :annotations
                  (let [panel-anns
                        (filterv
                         (fn [a]
                           (and (or (nil? (grid-coord a :facet-col))
                                    (= (grid-coord a :facet-col) (:col-label pd)))
                                (or (nil? (grid-coord a :facet-row))
                                    (= (grid-coord a :facet-row) (:row-label pd)))
                                (or (nil? (grid-coord a :x))
                                    (= (grid-coord a :x) (:var-x pd))
                                    (nil? (:var-x pd)))
                                (or (nil? (grid-coord a :y))
                                    (= (grid-coord a :y) (:var-y pd))
                                    (nil? (:var-y pd)))))
                         annotations)]
                    (mapv #(dissoc % :facet-col :facet-row :x :y) panel-anns)))
           (:row-label pd) (assoc :row-label (:row-label pd))
           (:col-label pd) (assoc :col-label (:col-label pd))))))))

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
        title-pad (if eff-title (:title-offset cfg) 0)
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
        strip-w (if has-row-strips? 60 0)
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
   Only the first view's specs are used — conflicting specs are silently ignored
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

(defn views->plan
  "Resolve views + options into a plan — a fully resolved plan
   with data-space geometry, domains, ticks, legend, and layout info.
   No membrane types, no datasets in the output.
   Options include :validate (default true) — when true, validates the
   resulting plan against the Malli schema and throws on failure."
  ([views] (views->plan views {}))
  ([views {:keys [x-label y-label title subtitle caption
                  scales legend-position] :as opts}]
   (let [cfg (defaults/resolve-config opts)
         cfg (assoc cfg :gradient-fn (defaults/resolve-gradient-fn (:color-scale cfg)))

         validate? (:validate cfg true)
         width (:width cfg)
         height (:height cfg)
         views (if (map? views) [views] views)
         ann-views (filter #(view/annotation-marks (:mark %)) views)
         non-ann-views (remove #(view/annotation-marks (:mark %)) views)
         ;; Grid — unified detection
         grid (infer-grid non-ann-views)
         layout-type (:layout-type grid)
         {:keys [grid-rows grid-cols facet-row-vals facet-col-vals x-vars y-vars]} grid
         scale-mode (or scales :shared)

         ;; Colors (also resolves all views once — tagged for reuse)
         {:keys [resolved-all numeric-color? all-colors color-cols tagged-views]}
         (collect-colors non-ann-views)

         ;; Scale & coord specs
         x-scale-spec (or (:x-scale (first non-ann-views)) {:type :linear})
         y-scale-spec (or (:y-scale (first non-ann-views)) {:type :linear})
         coord-type (or (:coord (first non-ann-views)) :cartesian)
         ;; Polar+mark compatibility check
         _ (validate-polar-marks resolved-all coord-type)
         _ (warn-conflicting-specs non-ann-views)

         ;; Annotations — preserve grid coordinates for per-panel placement
         annotations (vec (for [a ann-views]
                            (select-keys a [:mark :intercept :lo :hi :alpha
                                            :facet-col :facet-row :x :y])))

         ;; Panel pixel dimensions
         {:keys [m pw ph]}
         (compute-panel-dims cfg layout-type grid-rows grid-cols width height)

         ;; Group tagged views into panels — one unified algorithm
         panel-groups (group-panels tagged-views grid)
         panel-data (mapv (fn [pg]
                            (if (seq (:views pg))
                              (let [pre-resolved (mapv :__resolved (:views pg))]
                                (merge pg (resolve-panel-views (:views pg) all-colors cfg
                                                               :resolved pre-resolved)))
                              pg))
                          panel-groups)

         ;; Adjust panel dimensions for coord :fixed
         [pw ph] (if (= coord-type :fixed)
                   (let [all-srs (mapcat :stat-results panel-data)
                         all-lrs (mapcat :layers panel-data)
                         gx (or (:domain x-scale-spec)
                                (collect-domain all-srs :x-domain x-scale-spec))
                         gy (or (:domain y-scale-spec)
                                (compute-global-y-domain all-lrs y-scale-spec))]
                     (if (and (sequential? gx) (= 2 (count gx)) (number? (first gx))
                              (sequential? gy) (= 2 (count gy)) (number? (first gy)))
                       (let [{pw' :pw ph' :ph} (adjust-fixed-aspect pw ph gx gy)]
                         [pw' ph'])
                       [pw ph]))
                   [pw ph])

         ;; Build panel specs with domains, ticks, annotations
         panels (build-panels panel-data layout-type scale-mode coord-type
                              x-scale-spec y-scale-spec annotations
                              x-vars y-vars pw ph m cfg)

         ;; Ridgeline renders categories vertically and numeric horizontally
         ;; (manual axis transposition). Swap domains, ticks, and scale specs in
         ;; the panel so that sx becomes numeric (for bottom ticks) and sy becomes
         ;; categorical (for left ticks), matching the visual layout.
         has-ridgeline? (some #(= :ridgeline (:mark %)) non-ann-views)
         panels (if has-ridgeline?
                  (mapv (fn [p]
                          (-> p
                              (assoc :x-domain (:y-domain p)
                                     :y-domain (:x-domain p)
                                     :x-ticks (:y-ticks p)
                                     :y-ticks (:x-ticks p)
                                     :x-scale (:y-scale p)
                                     :y-scale (:x-scale p))))
                        panels)
                  panels)

         ;; Labels — suppress auto-labels for multi-variable grids and polar coords
         multi? (and (= layout-type :multi-variable) (> grid-cols 1) (> grid-rows 1))
         auto-label? (and (not multi?) (coord/show-ticks? coord-type))
         {:keys [eff-title eff-x-label eff-y-label]}
         (resolve-labels non-ann-views x-vars y-vars x-scale-spec y-scale-spec
                         title x-label y-label auto-label?)
         ;; Subtitle and caption — from opts only
         ;; Swap labels when axes are visually transposed
         swap-labels? (or (= coord-type :flip)
                          has-ridgeline?)
         [eff-x-label eff-y-label] (if swap-labels?
                                     [eff-y-label eff-x-label]
                                     [eff-x-label eff-y-label])

         ;; Legends
         legend (build-legend resolved-all numeric-color? all-colors color-cols cfg)
         size-legend (build-size-legend resolved-all)
         alpha-legend (build-alpha-legend resolved-all)

         ;; Layout dimensions
         layout-dims (compute-layout-dims cfg layout-type eff-title eff-x-label eff-y-label
                                          subtitle caption
                                          legend size-legend alpha-legend
                                          facet-row-vals facet-col-vals
                                          grid-rows grid-cols pw ph multi? panels legend-position)

         plan
         (view/map->Plan
          {:width width :height height :margin m
           :total-width (:total-w layout-dims) :total-height (:total-h layout-dims)
           :panel-width pw :panel-height ph
           :grid {:rows grid-rows :cols grid-cols}
           :layout-type layout-type
           :title eff-title
           :subtitle subtitle
           :caption caption
           :x-label eff-x-label :y-label eff-y-label
           :legend legend
           :size-legend size-legend
           :alpha-legend alpha-legend
           :legend-position (or legend-position :right)
           :panels panels
           :layout (select-keys layout-dims [:x-label-pad :y-label-pad :title-pad
                                             :subtitle-pad :caption-pad
                                             :legend-w :legend-h :strip-h :strip-w])})]
     (when validate?
       (when-let [explanation (ss/explain plan)]
         (throw (ex-info "Plan does not conform to schema" {:explanation explanation}))))
     plan)))
