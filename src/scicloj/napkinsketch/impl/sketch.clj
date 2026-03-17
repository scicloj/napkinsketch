(ns scicloj.napkinsketch.impl.sketch
  "Resolve views into a sketch — a plain Clojure map with data-space
   geometry, domains, tick info, legend, and layout. No membrane types,
   no datasets, no scale objects in the output."
  (:require [wadogo.scale :as ws]
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
   include 0 for marks that draw stems from baseline (lollipop, value-bar)."
  [layers scale-spec]
  (let [fill-layers (filter #(= :fill (:position %)) layers)
        stack-layers (filter #(= :stack (:position %)) layers)
        zero-baseline-marks #{:lollipop :value-bar}
        needs-zero? (some #(zero-baseline-marks (:mark %)) layers)]
    (cond
      ;; Fill mode: normalized to [0, 1]
      (seq fill-layers)
      [0.0 1.0]

      ;; Stack mode: read pre-computed y1 values from adjusted layers
      (seq stack-layers)
      (let [;; Stacked rect: max y1 across all groups and categories
            max-rect-y1 (reduce max 0
                                (for [l stack-layers
                                      :when (:categories l)
                                      g (:groups l)
                                      {:keys [y1]} (:counts g)
                                      :when y1]
                                  y1))
            ;; Stacked area: max ys (already accumulated)
            max-area-y (reduce max 0
                               (for [l stack-layers
                                     :when (and (not (:categories l)) (:groups l))
                                     g (:groups l)
                                     y (:ys g)]
                                 y))
            ;; Other (non-stacked) layers: use their y-domain
            other-yd (mapcat (fn [l]
                               (when-not (#{:stack :fill} (:position l))
                                 (:y-domain l)))
                             layers)
            hi (max max-rect-y1 max-area-y
                    (if (seq other-yd) (reduce max 0 other-yd) 0))]
        (if (pos? hi)
          (scale/pad-domain [0 hi] scale-spec)
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
          (scale/pad-domain [(min 0.0 (double (first dom)))
                             (max 0.0 (double (second dom)))]
                            scale-spec)
          dom)))))

;; ---- Tick Computation ----

(def ^:private date-fmt-year
  (java.time.format.DateTimeFormatter/ofPattern "yyyy"))

(def ^:private date-fmt-month-year
  (java.time.format.DateTimeFormatter/ofPattern "MMM yyyy"))

(def ^:private date-fmt-month-day
  (java.time.format.DateTimeFormatter/ofPattern "MMM d"))

(defn- format-date-tick
  "Format an epoch-day as a date string, choosing granularity based on
   the total span of the domain (in days)."
  [epoch-day span-days]
  (let [d (java.time.LocalDate/ofEpochDay (long epoch-day))]
    (cond
      (> span-days 1500) (.format d date-fmt-year)
      (> span-days 180) (.format d date-fmt-month-year)
      (> span-days 30) (.format d date-fmt-month-day)
      :else (str d))))

(defn compute-ticks
  "Compute tick values and labels for a domain+pixel range, using wadogo transiently.
   When temporal? is true, tick values are epoch-days and labels are date strings."
  ([domain pixel-range scale-spec spacing]
   (compute-ticks domain pixel-range scale-spec spacing false))
  ([domain pixel-range scale-spec spacing temporal?]
   (if (scale/categorical-domain? domain)
     (let [s (scale/make-scale domain pixel-range scale-spec)]
       {:values (vec (ws/ticks s))
        :labels (mapv str (ws/ticks s))
        :categorical? true})
     (let [s (scale/make-scale domain pixel-range scale-spec)
           n (scale/tick-count (Math/abs (double (- (second pixel-range) (first pixel-range)))) spacing)
           ticks (ws/ticks s n)
           labels (if temporal?
                    (let [span-days (- (double (second domain)) (double (first domain)))]
                      (mapv #(format-date-tick % span-days) ticks))
                    (scale/format-ticks s ticks))]
       {:values (vec ticks)
        :labels (vec labels)
        :categorical? false}))))

;; ---- Layout Detection ----

(defn infer-layout
  "Determine layout type from views."
  [views]
  (let [facet-rows (seq (remove nil? (map :facet-row views)))
        facet-cols (seq (remove nil? (map :facet-col views)))]
    (cond
      (or facet-rows facet-cols) :facet-grid
      :else (let [x-vars (distinct (map :x views))
                  y-vars (distinct (map :y views))]
              (if (or (> (count x-vars) 1) (> (count y-vars) 1))
                :multi-variable
                :single)))))

;; ---- Per-Panel Resolution ----

(defn resolve-panel-views
  "Resolve views and compute stats for a group of views belonging to one panel.
   If pre-resolved views are provided, skips resolve-view.
   Returns {:resolved [...] :stat-results [...] :layers [...]}."
  [panel-views all-colors cfg & {:keys [resolved]}]
  (let [resolved (or resolved (mapv view/resolve-view panel-views))
        stat-results (mapv #(stat/compute-stat (assoc % :cfg (merge cfg (:cfg %)))) resolved)
        raw-layers (vec (map (fn [rv sr]
                               (-> (extract/extract-layer rv sr all-colors cfg)
                                   (assoc :y-domain (:y-domain sr)
                                          :x-domain (:x-domain sr))))
                             resolved stat-results))
        layers (position/apply-positions raw-layers)]
    {:resolved resolved :stat-results stat-results :layers layers}))

(defn- collect-colors
  "Resolve views and collect color categories across all views.
   Attaches :__resolved to each view for downstream re-use.
   Returns {:resolved-all :numeric-color? :all-colors :color-cols :tagged-views}."
  [non-ann-views]
  (let [resolved-all (mapv view/resolve-view non-ann-views)
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

(defn- compute-grid
  "Compute grid dimensions and facet/variable lists from layout type.
   Returns {:grid-rows :grid-cols :facet-row-vals :facet-col-vals :x-vars :y-vars}."
  [non-ann-views layout-type]
  (let [x-vars (distinct (map :x non-ann-views))
        y-vars (distinct (map :y non-ann-views))
        facet-row-vals (when (= layout-type :facet-grid)
                         (vec (distinct (remove #(= "_" %) (map :facet-row non-ann-views)))))
        facet-col-vals (when (= layout-type :facet-grid)
                         (vec (distinct (remove #(= "_" %) (map :facet-col non-ann-views)))))
        [grid-rows grid-cols]
        (case layout-type
          :single [1 1]
          :facet-grid [(max 1 (count (or facet-row-vals ["_"])))
                       (max 1 (count (or facet-col-vals ["_"])))]
          :multi-variable [(count y-vars) (count x-vars)])]
    {:grid-rows grid-rows :grid-cols grid-cols
     :facet-row-vals facet-row-vals :facet-col-vals facet-col-vals
     :x-vars x-vars :y-vars y-vars}))

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
  "Group views into panel descriptors by layout type."
  [non-ann-views layout-type facet-row-vals facet-col-vals x-vars y-vars]
  (case layout-type
    :single
    [{:views non-ann-views :row 0 :col 0}]

    :facet-grid
    (let [rvs (or (seq facet-row-vals) ["_"])
          cvs (or (seq facet-col-vals) ["_"])]
      (for [[ri rv] (map-indexed vector rvs)
            [ci cv] (map-indexed vector cvs)]
        {:views (filterv #(and (= (or (:facet-row %) "_") (str rv))
                               (= (or (:facet-col %) "_") (str cv)))
                         non-ann-views)
         :row ri :col ci
         :row-label (when (not= rv "_") (str rv))
         :col-label (when (not= cv "_") (str cv))}))

    :multi-variable
    (for [[ri yv] (map-indexed vector y-vars)
          [ci xv] (map-indexed vector x-vars)]
      {:views (filterv #(and (= xv (:x %)) (= yv (:y %))) non-ann-views)
       :row ri :col ci
       :var-x xv :var-y yv
       :col-label (defaults/fmt-name xv)
       :row-label (defaults/fmt-name yv)})))

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
        mv-col-x-doms (when (= layout-type :multi-variable)
                        (into {} (for [xv x-vars]
                                   [xv (let [pds (filter #(= xv (:var-x %)) panel-data)
                                             scatter-pds (filter #(not= (:var-x %) (:var-y %)) pds)
                                             srs (mapcat :stat-results scatter-pds)]
                                         (when (seq srs)
                                           (collect-domain srs :x-domain x-scale-spec)))])))
        mv-row-y-doms (when (= layout-type :multi-variable)
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
               [(case scale-mode
                  (:shared :free-y) global-x-dom
                  local-x-dom)
                (case scale-mode
                  (:shared :free-x) global-y-dom
                  local-y-dom)]
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
             ;; Detect temporal axes from view flags
             resolved-views (:resolved pd)
             x-temporal? (some :x-temporal? resolved-views)
             y-temporal? (some :y-temporal? resolved-views)
             [x-temp? y-temp?] (if (= coord-type :flip)
                                 [y-temporal? x-temporal?]
                                 [x-temporal? y-temporal?])
             x-px [m (- pw m)]
             y-px [(- ph m) m]
             x-ticks (when x-dom' (compute-ticks x-dom' x-px x-sspec' (:tick-spacing-x cfg) x-temp?))
             y-ticks (when y-dom' (compute-ticks y-dom' y-px y-sspec' (:tick-spacing-y cfg) y-temp?))]
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
           (seq annotations) (assoc :annotations annotations)
           (:row-label pd) (assoc :row-label (:row-label pd))
           (:col-label pd) (assoc :col-label (:col-label pd))))))))

(defn- resolve-labels
  "Resolve effective title and axis labels."
  [non-ann-views x-vars y-vars x-scale-spec y-scale-spec
   title x-label y-label auto-label?]
  (let [view-title (:title (first non-ann-views))
        view-x-label (:x-label (first non-ann-views))
        view-y-label (:y-label (first non-ann-views))]
    {:eff-title (or title view-title)
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
            all-vals (mapcat #((:data %) (:color %)) color-views)
            c-min (reduce min all-vals)
            c-max (reduce max all-vals)]
        {:title (first color-cols)
         :type :continuous
         :min c-min :max c-max
         :gradient-fn grad-fn
         :stops (vec (for [t (range 0.0 1.01 0.25)]
                       {:t t :color (grad-fn t)}))})
      all-colors
      {:title (first color-cols)
       :entries (vec (for [cat all-colors]
                       {:label (str cat)
                        :color (defaults/color-for all-colors cat (:palette cfg))}))})))

(defn- compute-layout-dims
  "Compute layout dimensions: padding, legend width, total size."
  [cfg layout-type eff-title eff-x-label eff-y-label
   subtitle caption
   legend facet-row-vals facet-col-vals
   grid-rows grid-cols pw ph multi? panels legend-position]
  (let [x-label-pad (if eff-x-label (:label-offset cfg) 0)
        ;; y-label-pad must account for y-tick label width (e.g. category names)
        tick-fsize (:font-size defaults/theme)
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
        legend-w (if (and legend (= legend-pos :right)) (:legend-width cfg) 0)
        legend-h (if (and legend (#{:bottom :top} legend-pos)) 30 0)
        has-col-strips? (or (and (= layout-type :facet-grid) (seq facet-col-vals))
                            multi?)
        has-row-strips? (or (and (= layout-type :facet-grid) (seq facet-row-vals))
                            multi?)
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

(defn views->sketch
  "Resolve views + options into a sketch — a fully resolved plot specification
   with data-space geometry, domains, ticks, legend, and layout info.
   No membrane types, no datasets in the output.
   Options include :validate (default true) — when true, validates the
   resulting sketch against the Malli schema and throws on failure."
  ([views] (views->sketch views {}))
  ([views {:keys [width height config x-label y-label title subtitle caption
                  scales palette theme legend-position color-scale color-midpoint
                  validate] :as opts}]
   (let [validate? (if (some? validate) validate true)
         cfg (cond-> (merge defaults/defaults config)
               palette (assoc :palette palette)
               true (assoc :gradient-fn (defaults/resolve-gradient-fn color-scale))
               color-midpoint (assoc :color-midpoint color-midpoint))
         resolved-theme (merge defaults/theme theme)
         width (or width (:width cfg))
         height (or height (:height cfg))
         views (if (map? views) [views] views)
         ann-views (filter #(view/annotation-marks (:mark %)) views)
         non-ann-views (remove #(view/annotation-marks (:mark %)) views)
         layout-type (infer-layout non-ann-views)
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

         ;; Annotations
         annotations (vec (for [a ann-views]
                            (select-keys a [:mark :intercept :lo :hi :alpha])))

         ;; Grid
         {:keys [grid-rows grid-cols facet-row-vals facet-col-vals x-vars y-vars]}
         (compute-grid non-ann-views layout-type)

         ;; Panel pixel dimensions
         {:keys [m pw ph]}
         (compute-panel-dims cfg layout-type grid-rows grid-cols width height)

         ;; Group tagged views into panels and resolve stats/layers
         ;; (tagged views carry :__resolved so resolve-view isn't called again)
         panel-groups (group-panels tagged-views layout-type
                                    facet-row-vals facet-col-vals x-vars y-vars)
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

         ;; Subtitle and caption — opts override view-level
         subtitle (or subtitle (:subtitle (first non-ann-views)))
         caption (or caption (:caption (first non-ann-views)))
         ;; Swap labels when axes are visually transposed
         swap-labels? (or (= coord-type :flip)
                          has-ridgeline?)
         [eff-x-label eff-y-label] (if swap-labels?
                                     [eff-y-label eff-x-label]
                                     [eff-x-label eff-y-label])

         ;; Legend
         legend (build-legend resolved-all numeric-color? all-colors color-cols cfg)

         ;; Layout dimensions
         layout-dims (compute-layout-dims cfg layout-type eff-title eff-x-label eff-y-label
                                          subtitle caption
                                          legend facet-row-vals facet-col-vals
                                          grid-rows grid-cols pw ph multi? panels legend-position)

         sketch
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
          :legend-position (or legend-position :right)
          :panels panels
          :theme resolved-theme
          :layout (select-keys layout-dims [:x-label-pad :y-label-pad :title-pad
                                            :subtitle-pad :caption-pad
                                            :legend-w :legend-h :strip-h :strip-w])}]
     (when validate?
       (when-let [explanation (ss/explain sketch)]
         (throw (ex-info "Sketch does not conform to schema" {:explanation explanation}))))
     sketch)))
