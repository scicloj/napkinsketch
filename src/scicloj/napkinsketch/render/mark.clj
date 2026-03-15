(ns scicloj.napkinsketch.render.mark
  (:require [clojure.string :as str]
            [membrane.ui :as ui]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [fastmath.random :as rng]
            [wadogo.scale :as ws]))

;; ---- Helpers ----

(defn orient-scales
  "Extract oriented scales from rendering context.
   For categorical marks that need band positioning, returns the
   categorical (band) scale and numeric scale, swapped when flipped.
   Returns {:flipped? :band-s :num-s}."
  [ctx]
  (let [flipped? (= (:coord-type ctx) :flip)]
    {:flipped? flipped?
     :band-s (if flipped? (:sy ctx) (:sx ctx))
     :num-s (if flipped? (:sx ctx) (:sy ctx))}))

(defn arc-interpolate
  "Subdivide a line segment in pixel space through coord-px reprojection.
   Given two pixel-space endpoints (already in Cartesian panel coords),
   interpolates n intermediate points, projects each through coord-px,
   and returns a seq of [x y] pairs. Used for smooth wedge edges in polar."
  [coord-px px1 py1 px2 py2 n-seg]
  (mapv (fn [i]
          (let [t (/ (double i) (double n-seg))
                px (+ px1 (* t (- px2 px1)))
                py (+ py1 (* t (- py2 py1)))]
            (coord-px px py)))
        (range (inc n-seg))))

(defn bar-polygon
  "Compute polygon points for a rectangular bar, with arc interpolation
   when coord-px is provided (polar). In cartesian/flip, produces a
   simple 4-corner closed polygon.
   - coord-px: pixel-space reprojection fn, or nil for cartesian
   - flipped?: true when axes are swapped
   - cat-lo, cat-hi: pixel range along the categorical (band) axis
   - val-lo, val-hi: pixel range along the numeric (value) axis"
  [coord-px flipped? cat-lo cat-hi val-lo val-hi]
  (if (and coord-px (not flipped?))
    (let [arc-seg 20
          top (arc-interpolate coord-px cat-lo val-hi cat-hi val-hi arc-seg)
          bottom (arc-interpolate coord-px cat-hi val-lo cat-lo val-lo arc-seg)]
      (concat top bottom))
    (let [[x1 y1 x2 y2 x3 y3 x4 y4]
          (if flipped?
            [val-lo cat-lo val-hi cat-lo val-hi cat-hi val-lo cat-hi]
            [cat-lo val-lo cat-hi val-lo cat-hi val-hi cat-lo val-hi])]
      [[x1 y1] [x2 y2] [x3 y3] [x4 y4] [x1 y1]])))

(defn band-position
  "Compute dodged position within a categorical band.
   Returns {:lo :hi :mid} pixel coordinates for one sub-band.
   - band-s: wadogo band scale
   - category: category value to look up
   - group-idx: index of this group within n-groups
   - n-groups: total number of dodge groups
   - frac: fraction of band width to use (e.g. 0.8)"
  [band-s category group-idx n-groups frac]
  (let [bw (ws/data band-s :bandwidth)
        band-info (band-s category true)
        band-start (:rstart band-info)
        band-end (:rend band-info)
        band-mid (/ (+ band-start band-end) 2.0)
        sub-bw (/ (* bw frac) (max 1 n-groups))
        group-start (- band-mid (/ (* n-groups sub-bw) 2.0))
        lo (+ group-start (* group-idx sub-bw))
        hi (+ lo sub-bw)]
    {:lo lo :hi hi :mid (/ (+ lo hi) 2.0) :sub-bw sub-bw}))

(defn- draw-shape
  "Draw a shape symbol centered at (0,0) with given radius."
  [shape-kw r]
  (let [d (* 2 r)]
    (case shape-kw
      :square (ui/with-style ::ui/style-fill
                (ui/rounded-rectangle d d 0))
      :triangle (let [h (* r 1.73)] ;; equilateral triangle height
                  (ui/with-style ::ui/style-fill
                    (ui/path [r 0] [(+ r r) h] [(- r r) h] [r 0])))
      :diamond (ui/with-style ::ui/style-fill
                 (ui/path [r 0] [d r] [r d] [0 r] [r 0]))
      ;; default: circle
      (ui/with-style ::ui/style-fill
        (ui/rounded-rectangle d d r)))))

(defn- fmt-val
  "Format a value for tooltip display."
  [v]
  (cond
    (float? v) (format "%.4g" (double v))
    (double? v) (format "%.4g" (double v))
    :else (str v)))

(defn- make-tooltip
  "Build a tooltip text string from context and point data."
  [ctx x-val y-val color-label]
  (let [{:keys [x-col-name y-col-name]} ctx
        parts (cond-> [(str x-col-name ": " (fmt-val x-val))
                       (str y-col-name ": " (fmt-val y-val))]
                color-label (conj (str "color: " color-label)))]
    (str/join ", " parts)))

;; ---- layer->membrane multimethod ----
;; layer->membrane takes sketch layer descriptors (data-space geometry,
;; resolved colors) and renders them as membrane drawable primitives.

(defmulti layer->membrane
  "Convert a sketch layer into membrane drawable primitives.
   `layer` is a sketch layer map with data-space geometry and resolved colors.
   `ctx` contains :coord-fn, :sx, :sy, :coord-type.
   Dispatches on (:mark layer)."
  (fn [layer ctx] (:mark layer)))

;; ---- Point ----

(defmethod layer->membrane :point [layer ctx]
  (let [{:keys [style groups]} layer
        {:keys [coord-fn tooltip]} ctx
        {:keys [opacity radius jitter]} style
        jitter-amount (cond (number? jitter) (double jitter)
                            jitter 5.0
                            :else 0.0)
        jitter? (pos? jitter-amount)
        size-bufs (keep :sizes groups)
        size-scale (when (seq size-bufs)
                     (let [all-sizes (apply concat size-bufs)
                           lo (reduce min all-sizes)
                           hi (reduce max all-sizes)
                           span (max 1e-6 (- (double hi) (double lo)))]
                       (fn [v] (+ 2.0 (* 6.0 (/ (- (double v) (double lo)) span))))))
        alpha-bufs (keep :alphas groups)
        alpha-scale (when (seq alpha-bufs)
                      (let [all-alphas (apply concat alpha-bufs)
                            lo (reduce min all-alphas)
                            hi (reduce max all-alphas)
                            span (max 1e-6 (- (double hi) (double lo)))]
                        (fn [v] (+ 0.2 (* 0.8 (/ (- (double v) (double lo)) span))))))
        shape-bufs (keep :shapes groups)
        shape-map (when (seq shape-bufs)
                    (let [all-vals (distinct (apply concat shape-bufs))]
                      (zipmap all-vals (cycle defaults/shape-syms))))]
    (vec
     (for [{:keys [color colors xs ys sizes alphas shapes row-indices color-label] :as group} groups
           :let [;; One seeded RNG per group for deterministic jitter
                 jitter-rng (when jitter? (rng/rng :jdk (hash (:color group))))]
           i (range (count xs))
           :let [[px py] (coord-fn (nth xs i) (nth ys i))
                 [px py] (if jitter?
                           [(+ (double px) (* jitter-amount (- (* 2.0 (rng/drandom jitter-rng)) 1.0)))
                            (+ (double py) (* jitter-amount (- (* 2.0 (rng/drandom jitter-rng)) 1.0)))]
                           [px py])
                 pt-r (if sizes (size-scale (nth sizes i)) radius)
                 pt-alpha (if alphas (alpha-scale (nth alphas i)) (or opacity 1.0))
                 pt-shape (if shapes (get shape-map (nth shapes i) :circle) :circle)
                 [cr cg cb _] (if colors (nth colors i) color)]]
       (-> (ui/translate (- (double px) pt-r) (- (double py) pt-r)
                         (ui/with-color [cr cg cb pt-alpha]
                           (draw-shape pt-shape pt-r)))
           (cond-> row-indices (assoc :row-idx (nth row-indices i))
                   tooltip (assoc :tooltip (make-tooltip ctx (nth xs i) (nth ys i) color-label))))))))

;; ---- Text ----

(defmethod layer->membrane :text [layer ctx]
  (let [{:keys [style groups]} layer
        {:keys [coord-fn]} ctx
        {:keys [font-size]} style
        fsize (or font-size 10)]
    (vec
     (for [{:keys [color xs ys labels]} groups
           i (range (count xs))
           :let [[px py] (coord-fn (nth xs i) (nth ys i))
                 label (if labels (nth labels i) "")
                 [cr cg cb _] color]]
       (ui/translate (double px) (- (double py) (/ fsize 2.0))
                     (ui/with-color [cr cg cb 1.0]
                       (ui/label label (ui/font nil fsize))))))))

;; ---- Area ----

(defmethod layer->membrane :area [layer ctx]
  (let [{:keys [style groups position]} layer
        {:keys [coord-fn y-domain-min]} ctx
        {:keys [opacity]} style
        baseline (or y-domain-min 0)]
    (if (#{:stack :fill} position)
      ;; Stacked: each group's baseline is the cumulative sum of previous groups
      (let [group-maps (mapv (fn [{:keys [xs ys]}]
                               (into (sorted-map) (map vector xs ys)))
                             groups)
            all-xs (sort (distinct (mapcat keys group-maps)))
            {:keys [elements]}
            (reduce
             (fn [{:keys [elements cum]} [group gm]]
               (let [base-vals (mapv #(get cum % 0.0) all-xs)
                     top-vals (mapv #(+ (get cum % 0.0) (get gm % 0.0)) all-xs)
                     top-pts (map (fn [x tv] (coord-fn x tv)) all-xs top-vals)
                     base-pts (reverse (map (fn [x bv] (coord-fn x bv)) all-xs base-vals))
                     all-pts (concat top-pts base-pts)
                     [cr cg cb _] (:color group)
                     new-cum (into cum (map vector all-xs top-vals))]
                 {:elements (conj elements
                                  (ui/with-color [cr cg cb (or opacity 0.5)]
                                    (ui/with-style ::ui/style-fill
                                      (apply ui/path all-pts))))
                  :cum new-cum}))
             {:elements [] :cum {}}
             (map vector groups group-maps))]
        elements)
      ;; Non-stacked: each group has baseline at y-domain-min
      (vec
       (for [{:keys [color xs ys]} groups
             :let [sorted (sort-by first (map vector xs ys))
                   top-pts (map (fn [[x y]] (coord-fn x y)) sorted)
                   x-first (ffirst sorted)
                   x-last (first (last sorted))
                   [bx-right by-right] (coord-fn x-last baseline)
                   [bx-left by-left] (coord-fn x-first baseline)
                   all-pts (concat top-pts [[bx-right by-right] [bx-left by-left]])
                   [cr cg cb _] color]]
         (ui/with-color [cr cg cb (or opacity 0.5)]
           (ui/with-style ::ui/style-fill
             (apply ui/path all-pts))))))))

;; ---- Errorbar ----

(defmethod layer->membrane :errorbar [layer ctx]
  (let [{:keys [style groups]} layer
        {:keys [coord-fn]} ctx
        {:keys [stroke-width cap-width]} style
        sw (or stroke-width 1.5)
        cap-hw (/ (or cap-width 6) 2.0)]
    (vec
     (for [{:keys [color xs ys ymins ymaxs]} groups
           i (range (count xs))
           :let [x (nth xs i)
                 ymin-val (nth ymins i)
                 ymax-val (nth ymaxs i)
                 [px py-min] (coord-fn x ymin-val)
                 [_ py-max] (coord-fn x ymax-val)
                 [cr cg cb _] color]]
       (ui/with-color [cr cg cb 1.0]
         [(ui/with-style ::ui/style-stroke
            (ui/with-stroke-width sw
              ;; Vertical line from ymin to ymax
              (ui/path [px py-min] [px py-max])))
          ;; Bottom cap
          (ui/with-style ::ui/style-stroke
            (ui/with-stroke-width sw
              (ui/path [(- (double px) cap-hw) py-min]
                       [(+ (double px) cap-hw) py-min])))
          ;; Top cap
          (ui/with-style ::ui/style-stroke
            (ui/with-stroke-width sw
              (ui/path [(- (double px) cap-hw) py-max]
                       [(+ (double px) cap-hw) py-max])))])))))

;; ---- Lollipop ----

(defmethod layer->membrane :lollipop [layer ctx]
  (let [{:keys [style groups]} layer
        {:keys [flipped? band-s num-s]} (orient-scales ctx)
        {:keys [radius stroke-width]} style
        n-groups (count groups)
        r (or radius 4)]
    (vec
     (for [[gi {:keys [color xs ys]}] (map-indexed vector groups)
           i (range (count xs))
           :let [[cr cg cb _] color
                 cat (nth xs i)
                 val (nth ys i)
                 bp (band-position band-s cat gi n-groups 0.8)
                 cat-pos (:mid bp)
                 val-base (num-s 0)
                 val-top (num-s val)]]
       [(ui/with-color [cr cg cb 1.0]
          (ui/with-style ::ui/style-stroke
            (ui/with-stroke-width (or stroke-width 1.5)
              (if flipped?
                (ui/path [val-base cat-pos] [val-top cat-pos])
                (ui/path [cat-pos val-base] [cat-pos val-top])))))
        (ui/translate (if flipped? (- (double val-top) r) (- (double cat-pos) r))
                      (if flipped? (- (double cat-pos) r) (- (double val-top) r))
                      (ui/with-color [cr cg cb 1.0]
                        (ui/with-style ::ui/style-fill
                          (ui/rounded-rectangle (* 2 r) (* 2 r) r))))]))))

;; ---- Boxplot ----

(defmethod layer->membrane :boxplot [layer ctx]
  (let [{:keys [style boxes color-categories]} layer
        {:keys [flipped? band-s num-s]} (orient-scales ctx)
        {:keys [box-width stroke-width]} style
        box-frac (or box-width 0.6)
        sw (or stroke-width 1.5)
        n-colors (if (seq color-categories) (count color-categories) 1)
        color-idx-map (when (seq color-categories)
                        (into {} (map-indexed (fn [i c] [c i]) color-categories)))]
    (vec
     (mapcat
      (fn [{:keys [category color color-category median q1 q3 whisker-lo whisker-hi outliers]}]
        (let [[cr cg cb _] color
              ci (if (and color-idx-map color-category)
                   (get color-idx-map color-category 0)
                   0)
              bp (band-position band-s category ci n-colors box-frac)
              box-lo (:lo bp)
              box-hi (:hi bp)
              box-mid (:mid bp)
              sub-bw (:sub-bw bp)
              ;; Y positions via numeric scale
              py-q1 (num-s q1)
              py-q3 (num-s q3)
              py-med (num-s median)
              py-wlo (num-s whisker-lo)
              py-whi (num-s whisker-hi)
              ;; Build primitives depending on orientation
              mk-box (fn [x1 y1 x2 y2 x3 y3 x4 y4]
                       [(ui/with-color [cr cg cb 0.7]
                          (ui/with-style ::ui/style-fill
                            (ui/path [x1 y1] [x2 y2] [x3 y3] [x4 y4] [x1 y1])))
                        (ui/with-color [cr cg cb 1.0]
                          (ui/with-stroke-width sw
                            (ui/with-style ::ui/style-stroke
                              (ui/path [x1 y1] [x2 y2] [x3 y3] [x4 y4] [x1 y1]))))])
              mk-line (fn [xa ya xb yb]
                        (ui/with-color [cr cg cb 1.0]
                          (ui/with-stroke-width sw
                            (ui/with-style ::ui/style-stroke
                              (ui/path [xa ya] [xb yb])))))
              mk-point (fn [px py]
                         (let [r 2.5 d (* 2 r)]
                           (ui/translate (- (double px) r) (- (double py) r)
                                         (ui/with-color [cr cg cb 1.0]
                                           (ui/with-style ::ui/style-fill
                                             (ui/rounded-rectangle d d r))))))]
          (if flipped?
            (concat
             (mk-box py-q1 box-lo py-q3 box-lo py-q3 box-hi py-q1 box-hi)
             [(mk-line py-med box-lo py-med box-hi)
              (mk-line py-wlo box-mid py-q1 box-mid)
              (mk-line py-whi box-mid py-q3 box-mid)
              (mk-line py-wlo (- box-mid (* sub-bw 0.15))
                       py-wlo (+ box-mid (* sub-bw 0.15)))
              (mk-line py-whi (- box-mid (* sub-bw 0.15))
                       py-whi (+ box-mid (* sub-bw 0.15)))]
             (for [o outliers] (mk-point (num-s o) box-mid)))
            (concat
             (mk-box box-lo py-q3 box-hi py-q3 box-hi py-q1 box-lo py-q1)
             [(mk-line box-lo py-med box-hi py-med)
              (mk-line box-mid py-q1 box-mid py-wlo)
              (mk-line box-mid py-q3 box-mid py-whi)
              (mk-line (- box-mid (* sub-bw 0.15)) py-wlo
                       (+ box-mid (* sub-bw 0.15)) py-wlo)
              (mk-line (- box-mid (* sub-bw 0.15)) py-whi
                       (+ box-mid (* sub-bw 0.15)) py-whi)]
             (for [o outliers] (mk-point box-mid (num-s o)))))))
      boxes))))

;; ---- Violin ----

(defmethod layer->membrane :violin [layer ctx]
  (let [{:keys [style violins color-categories]} layer
        {:keys [flipped? band-s num-s]} (orient-scales ctx)
        {:keys [opacity stroke-width]} style
        n-colors (if (seq color-categories) (count color-categories) 1)
        color-idx-map (when (seq color-categories)
                        (into {} (map-indexed (fn [i c] [c i]) color-categories)))]
    (vec
     (mapcat
      (fn [{:keys [category color color-category ys densities]}]
        (let [[cr cg cb _] color
              ci (if (and color-idx-map color-category)
                   (get color-idx-map color-category 0)
                   0)
              bp (band-position band-s category ci n-colors 0.8)
              viol-mid (:mid bp)
              half-w (/ (:sub-bw bp) 2.0)
              ;; Normalize densities so max density fills half the band width
              max-d (reduce max 0.001 densities)
              norm (/ half-w max-d)
              ;; Build mirrored polygon points
              n (count ys)
              right-pts (mapv (fn [i]
                                (let [y-val (nth ys i)
                                      d (nth densities i)
                                      py (num-s y-val)
                                      px (+ viol-mid (* d norm))]
                                  (if flipped? [py px] [px py])))
                              (range n))
              left-pts (mapv (fn [i]
                               (let [y-val (nth ys (- n 1 i))
                                     d (nth densities (- n 1 i))
                                     py (num-s y-val)
                                     px (- viol-mid (* d norm))]
                                 (if flipped? [py px] [px py])))
                             (range n))
              all-pts (concat right-pts left-pts)]
          [(ui/with-color [cr cg cb (or opacity 0.7)]
             (ui/with-style ::ui/style-fill
               (apply ui/path all-pts)))
           (ui/with-color [cr cg cb 1.0]
             (ui/with-stroke-width (or stroke-width 1.0)
               (ui/with-style ::ui/style-stroke
                 (apply ui/path all-pts))))]))
      violins))))

;; ---- Tile (heatmap) ----

(defmethod layer->membrane :tile [layer ctx]
  (let [{:keys [style tiles]} layer
        {:keys [coord-fn]} ctx
        {:keys [opacity]} style]
    (vec
     (for [{:keys [x-lo x-hi y-lo y-hi color]} tiles
           :let [[px1 py1] (coord-fn x-lo y-lo)
                 [px2 py2] (coord-fn x-hi y-hi)
                 x-min (min px1 px2)
                 y-min (min py1 py2)
                 w (Math/abs (- (double px2) (double px1)))
                 h (Math/abs (- (double py2) (double py1)))
                 [cr cg cb _] color]]
       (ui/translate x-min y-min
                     (ui/with-color [cr cg cb (or opacity 1.0)]
                       (ui/with-style ::ui/style-fill
                         (ui/rectangle w h))))))))

(defmethod layer->membrane :contour [layer ctx]
  (let [{:keys [levels style]} layer
        {:keys [coord-fn]} ctx
        {:keys [stroke-width opacity]} style]
    (vec
     (for [{:keys [color segments]} levels
           :let [[cr cg cb _] color]
           [p1 p2] segments
           :let [[x1 y1] p1
                 [x2 y2] p2
                 [px1 py1] (coord-fn x1 y1)
                 [px2 py2] (coord-fn x2 y2)]]
       (ui/with-color [cr cg cb (or opacity 0.8)]
         (ui/with-stroke-width (or stroke-width 1.5)
           (ui/with-style ::ui/style-stroke
             (ui/path [px1 py1] [px2 py2]))))))))

;; ---- Ridgeline ----

(defmethod layer->membrane :ridgeline [layer ctx]
  (let [{:keys [style ridges categories]} layer
        {:keys [sx sy margin panel-height]} ctx
        {:keys [opacity]} style
        n-cats (count categories)
        m (or margin 25)
        ;; Band positions use panel-height because the categorical axis
        ;; maps to SVG y-space (points are [py, px] — px is the y-coordinate).
        ph (or panel-height 400)
        ;; Overlap factor: each ridge can extend this × band width
        overlap 1.5
        ;; Compute band positions with padding for curve amplitude.
        ;; Padding = bw * (overlap - 0.5) on each side ensures the peak
        ;; of the tallest curve exactly reaches the data area edge.
        ;; Total = n*bw + 2*bw*(overlap-0.5) = bw*(n + 2*overlap - 1)
        usable (- (double ph) (* 2.0 m))
        bw (/ usable (+ (double n-cats) (* 2.0 overlap) -1.0))
        pad (* bw (- overlap 0.5))
        cat-positions (into {} (map-indexed
                                (fn [i cat]
                                  [cat {:mid (+ m pad (* bw (+ (double i) 0.5)))
                                        :bw bw}])
                                categories))
        ;; Max density across all ridges for normalization
        max-d (reduce max 0.001 (mapcat :densities ridges))
        norm (* bw overlap (/ 1.0 max-d))
        ;; After domain swap in sketch, sx is the numeric scale (maps to x-pixels)
        ;; and sy is categorical. Use sx for numeric value → pixel mapping.
        num-scale sx]
    (vec
     ;; Render from back (last category) to front (first category)
     ;; so that front ridges overlap back ones
     (for [ridge (reverse ridges)
           :let [{:keys [category color ys densities]} ridge
                 {:keys [mid]} (get cat-positions category)
                 n (count ys)
                 [cr cg cb _] color
                 ;; Build polygon: baseline at mid, curve goes upward (toward lower px)
                 curve-pts (mapv (fn [i]
                                   (let [y-val (nth ys i)
                                         d (nth densities i)
                                         py (num-scale y-val)
                                         px (- (double mid) (* d norm))]
                                     [py px]))
                                 (range n))
                 ;; Baseline points (flat line at mid)
                 base-pts [(let [py (num-scale (nth ys (dec n)))] [py mid])
                           (let [py (num-scale (nth ys 0))] [py mid])]
                 all-pts (concat curve-pts base-pts)]]
       [(ui/with-color [cr cg cb (or opacity 0.7)]
          (ui/with-style ::ui/style-fill
            (apply ui/path all-pts)))
        (ui/with-color [cr cg cb 1.0]
          (ui/with-stroke-width 1.0
            (ui/with-style ::ui/style-stroke
              (apply ui/path curve-pts))))]))))

;; ---- Rug ----

(defmethod layer->membrane :rug [layer ctx]
  (let [{:keys [style groups side]} layer
        {:keys [coord-fn panel-width panel-height margin]} ctx
        {:keys [length stroke-width opacity]} style
        len (or length 6)]
    (vec
     (for [{:keys [color xs ys]} groups
           i (range (count xs))
           :let [[cr cg cb _] color
                 [px py] (coord-fn (nth xs i) (nth ys i))]
           tick (cond-> []
                  (#{:x :both} (or side :x))
                  (conj (ui/with-color [cr cg cb (or opacity 0.5)]
                          (ui/with-stroke-width (or stroke-width 1.0)
                            (ui/with-style ::ui/style-stroke
                              (ui/path [px (- (double panel-height) (double margin))]
                                       [px (- (double panel-height) (double margin) (- len))])))))
                  (#{:y :both} (or side :x))
                  (conj (ui/with-color [cr cg cb (or opacity 0.5)]
                          (ui/with-stroke-width (or stroke-width 1.0)
                            (ui/with-style ::ui/style-stroke
                              (ui/path [(double margin) py]
                                       [(+ (double margin) len) py]))))))]
       tick))))

;; ---- Pointrange (dot + vertical line from ymin to ymax) ----

(defmethod layer->membrane :pointrange [layer ctx]
  (let [{:keys [style groups]} layer
        {:keys [coord-fn]} ctx
        {:keys [radius stroke-width]} style
        r (or radius 3.5)
        sw (or stroke-width 1.5)]
    (vec
     (for [{:keys [color xs ys ymins ymaxs]} groups
           i (range (count xs))
           :let [[cr cg cb _] color
                 x (nth xs i)
                 y (nth ys i)
                 ymin-val (nth ymins i)
                 ymax-val (nth ymaxs i)
                 [px py] (coord-fn x y)
                 [_ py-min] (coord-fn x ymin-val)
                 [_ py-max] (coord-fn x ymax-val)]]
       [(ui/with-color [cr cg cb 1.0]
          (ui/with-stroke-width sw
            (ui/with-style ::ui/style-stroke
              (ui/path [px py-min] [px py-max]))))
        (ui/translate (- (double px) r) (- (double py) r)
                      (ui/with-color [cr cg cb 1.0]
                        (ui/with-style ::ui/style-fill
                          (ui/rounded-rectangle (* 2 r) (* 2 r) r))))]))))

(defmethod layer->membrane :default [layer ctx]
  (layer->membrane (assoc layer :mark :point) ctx))

;; ---- Bar (histogram) ----

(defmethod layer->membrane :bar [layer ctx]
  (let [{:keys [style groups]} layer
        {:keys [sx sy]} ctx
        coord-px (:coord-px ctx)
        {:keys [opacity]} style]
    (vec
     (for [{:keys [color bars]} groups
           {:keys [lo hi count]} bars
           :let [pts (bar-polygon coord-px false (sx lo) (sx hi) (sy 0) (sy count))
                 [cr cg cb _] color]]
       (ui/with-color [cr cg cb (or opacity 1.0)]
         (ui/with-style ::ui/style-fill
           (apply ui/path pts)))))))

;; ---- Line ----

(defmethod layer->membrane :line [layer ctx]
  (let [{:keys [style groups]} layer
        {:keys [coord-fn]} ctx
        {:keys [stroke-width]} style]
    (vec
     (for [group groups
           :let [[cr cg cb _] (:color group)]]
       (if (:x1 group)
         ;; Regression line segment
         (let [{:keys [x1 y1 x2 y2]} group
               [px1 py1] (coord-fn x1 y1)
               [px2 py2] (coord-fn x2 y2)]
           (ui/with-color [cr cg cb 1.0]
             (ui/with-stroke-width (or stroke-width 2)
               (ui/with-style ::ui/style-stroke
                 (ui/path [px1 py1] [px2 py2])))))
         ;; Polyline (connected points)
         (let [{:keys [xs ys]} group
               projected (sort-by first (map coord-fn xs ys))]
           (ui/with-color [cr cg cb 1.0]
             (ui/with-stroke-width (or stroke-width 2)
               (ui/with-style ::ui/style-stroke
                 (apply ui/path projected))))))))))

;; ---- Step Line ----

(defmethod layer->membrane :step [layer ctx]
  (let [{:keys [style groups]} layer
        {:keys [coord-fn]} ctx
        {:keys [stroke-width]} style]
    (vec
     (for [{:keys [color xs ys]} groups
           :let [[cr cg cb _] color
                 sorted (sort-by first (map vector xs ys))
                 ;; Build step path: for each pair of consecutive points,
                 ;; go horizontal first, then vertical
                 step-pts (loop [pts []
                                 remaining sorted]
                            (if (empty? remaining)
                              pts
                              (let [[x y] (first remaining)
                                    [px py] (coord-fn x y)]
                                (if (empty? pts)
                                  (recur [[px py]] (rest remaining))
                                  (let [[prev-px _prev-py] (last pts)]
                                    ;; Horizontal to new x at old y, then vertical to new y
                                    (recur (conj pts [px (second (last pts))] [px py])
                                           (rest remaining)))))))]]
       (when (>= (count step-pts) 2)
         (ui/with-color [cr cg cb 1.0]
           (ui/with-stroke-width (or stroke-width 2)
             (ui/with-style ::ui/style-stroke
               (apply ui/path step-pts)))))))))

;; ---- Rect (categorical bars / value bars) ----

(defn layer->membrane-categorical-bars
  "Render categorical count bars from a sketch :rect layer."
  [layer ctx]
  (let [{:keys [style groups position categories]} layer
        {:keys [flipped? band-s num-s]} (orient-scales ctx)
        {:keys [opacity]} style
        coord-px (:coord-px ctx)
        position (or position :dodge)
        mk-rect (fn [[cr cg cb _] cat-lo cat-hi val-lo val-hi]
                  (let [pts (bar-polygon coord-px flipped? cat-lo cat-hi val-lo val-hi)]
                    (ui/with-color [cr cg cb (or opacity 1.0)]
                      (ui/with-style ::ui/style-fill
                        (apply ui/path pts)))))]
    (if (#{:stack :fill} position)
      ;; Stacked: accumulate base heights per category
      (let [items (for [[_bi {:keys [color counts]}] (map-indexed vector groups)
                        {:keys [category count]} counts]
                    {:color color :category category :count count})
            {:keys [elements]}
            (reduce (fn [{:keys [elements cum-y]} {:keys [color category count]}]
                      (let [base (get cum-y category 0)
                            bp (band-position band-s category 0 1 0.8)
                            elem (mk-rect color (:lo bp) (:hi bp)
                                          (num-s base) (num-s (+ base count)))]
                        {:elements (conj elements elem)
                         :cum-y (assoc cum-y category (+ base count))}))
                    {:elements [] :cum-y {}}
                    items)]
        elements)
      ;; Dodged: filter to active bars per category
      (let [active-map (into {}
                             (for [cat categories]
                               [cat (keep-indexed
                                     (fn [bi {:keys [counts]}]
                                       (let [c (some #(when (= (:category %) cat) (:count %)) counts)]
                                         (when (and c (pos? c)) bi)))
                                     groups)]))]
        (vec
         (for [[bi {:keys [color counts]}] (map-indexed vector groups)
               {:keys [category count]} counts
               :when (pos? count)
               :let [active (get active-map category)
                     n-active (clojure.core/count active)
                     active-idx (.indexOf ^java.util.List active bi)
                     bp (band-position band-s category active-idx n-active 0.8)]]
           (mk-rect color (:lo bp) (:hi bp) (num-s 0) (num-s count))))))))

(defn layer->membrane-value-bars
  "Render value bars from a sketch :rect layer."
  [layer ctx]
  (let [{:keys [style groups]} layer
        {:keys [flipped? band-s num-s]} (orient-scales ctx)
        {:keys [opacity]} style
        coord-px (:coord-px ctx)
        n-groups (count groups)]
    (vec
     (for [[gi {:keys [color xs ys]}] (map-indexed vector groups)
           i (range (count xs))
           :let [[cr cg cb _] color
                 cat (nth xs i)
                 val (nth ys i)
                 bp (band-position band-s cat gi n-groups 0.8)
                 pts (bar-polygon coord-px flipped? (:lo bp) (:hi bp) (num-s 0) (num-s val))]]
       (ui/with-color [cr cg cb (or opacity 1.0)]
         (ui/with-style ::ui/style-fill
           (apply ui/path pts)))))))

(defmethod layer->membrane :rect [layer ctx]
  (if (:categories layer)
    (layer->membrane-categorical-bars layer ctx)
    (layer->membrane-value-bars layer ctx)))
