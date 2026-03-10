(ns scicloj.napkinsketch.impl.mark
  (:require [membrane.ui :as ui]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [wadogo.scale :as ws]))

;; ---- Helpers ----

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

;; ---- render-layer multimethod ----
;; render-layer takes sketch layer descriptors (data-space geometry,
;; resolved colors) and renders them as membrane scene primitives.

(defmulti render-layer
  "Render a sketch layer as membrane scene primitives.
   `layer` is a sketch layer map with data-space geometry and resolved colors.
   `ctx` contains :coord-fn, :sx, :sy, :coord-type, :cfg."
  (fn [layer ctx] (:mark layer)))

;; ---- Point ----

(defmethod render-layer :point [layer ctx]
  (let [{:keys [style groups]} layer
        {:keys [coord-fn]} ctx
        {:keys [opacity radius]} style
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
     (for [{:keys [color xs ys sizes alphas shapes row-indices]} groups
           i (range (count xs))
           :let [[px py] (coord-fn (nth xs i) (nth ys i))
                 pt-r (if sizes (size-scale (nth sizes i)) radius)
                 pt-alpha (if alphas (alpha-scale (nth alphas i)) (or opacity 1.0))
                 pt-shape (if shapes (get shape-map (nth shapes i) :circle) :circle)
                 [cr cg cb _] color]]
       (-> (ui/translate (- (double px) pt-r) (- (double py) pt-r)
                         (ui/with-color [cr cg cb pt-alpha]
                           (draw-shape pt-shape pt-r)))
           (cond-> row-indices (assoc :row-idx (nth row-indices i))))))))

(defmethod render-layer :default [layer ctx]
  (render-layer (assoc layer :mark :point) ctx))

;; ---- Bar (histogram) ----

(defmethod render-layer :bar [layer ctx]
  (let [{:keys [style groups]} layer
        {:keys [coord-fn]} ctx
        {:keys [opacity]} style]
    (vec
     (for [{:keys [color bars]} groups
           {:keys [lo hi count]} bars
           :let [[x1 y1] (coord-fn lo 0)
                 [x2 y2] (coord-fn hi 0)
                 [x3 y3] (coord-fn hi count)
                 [x4 y4] (coord-fn lo count)
                 [cr cg cb _] color]]
       (ui/with-color [cr cg cb (or opacity 1.0)]
         (ui/with-style ::ui/style-fill
           (ui/path [x1 y1] [x2 y2] [x3 y3] [x4 y4] [x1 y1])))))))

;; ---- Line ----

(defmethod render-layer :line [layer ctx]
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

;; ---- Rect (categorical bars / value bars) ----

(defn render-layer-categorical-bars
  "Render categorical count bars from a sketch :rect layer."
  [layer ctx]
  (let [{:keys [style groups position categories]} layer
        {:keys [coord-type]} ctx
        flipped? (= coord-type :flip)
        band-s (if flipped? (:sy ctx) (:sx ctx))
        num-s (if flipped? (:sx ctx) (:sy ctx))
        bw (ws/data band-s :bandwidth)
        {:keys [opacity]} style
        position (or position :dodge)
        cum-y (atom {})
        active-map (when (= position :dodge)
                     (into {}
                           (for [cat categories]
                             [cat (keep-indexed
                                   (fn [bi {:keys [counts]}]
                                     (let [c (some #(when (= (:category %) cat) (:count %)) counts)]
                                       (when (and c (pos? c)) bi)))
                                   groups)])))]
    (vec
     (for [[bi {:keys [color counts]}] (map-indexed vector groups)
           {:keys [category count]} counts
           :when (or (= position :stack) (pos? count))
           :let [[cr cg cb _] color
                 band-info (band-s category true)
                 band-start (:rstart band-info)
                 band-end (:rend band-info)
                 band-mid (/ (+ band-start band-end) 2.0)]]
       (if (= position :stack)
         (let [base (get @cum-y category 0)
               val-lo (num-s base)
               val-hi (num-s (+ base count))
               cat-lo (- band-mid (* bw 0.4))
               cat-hi (+ band-mid (* bw 0.4))
               [x1 y1 x2 y2 x3 y3 x4 y4]
               (if flipped?
                 [val-lo cat-lo val-hi cat-lo val-hi cat-hi val-lo cat-hi]
                 [cat-lo val-lo cat-hi val-lo cat-hi val-hi cat-lo val-hi])]
           (swap! cum-y assoc category (+ base count))
           (ui/with-color [cr cg cb (or opacity 1.0)]
             (ui/with-style ::ui/style-fill
               (ui/path [x1 y1] [x2 y2] [x3 y3] [x4 y4] [x1 y1]))))
         (let [active (get active-map category)
               n-active (clojure.core/count active)
               active-idx (.indexOf ^java.util.List active bi)
               sub-bw (/ (* bw 0.8) (max 1 n-active))
               cat-lo (+ (- band-mid (/ (* n-active sub-bw) 2.0)) (* active-idx sub-bw))
               cat-hi (+ cat-lo sub-bw)
               val-lo (num-s 0)
               val-hi (num-s count)
               [x1 y1 x2 y2 x3 y3 x4 y4]
               (if flipped?
                 [val-lo cat-lo val-hi cat-lo val-hi cat-hi val-lo cat-hi]
                 [cat-lo val-lo cat-hi val-lo cat-hi val-hi cat-lo val-hi])]
           (ui/with-color [cr cg cb (or opacity 1.0)]
             (ui/with-style ::ui/style-fill
               (ui/path [x1 y1] [x2 y2] [x3 y3] [x4 y4] [x1 y1])))))))))

(defn render-layer-value-bars
  "Render value bars from a sketch :rect layer."
  [layer ctx]
  (let [{:keys [style groups]} layer
        {:keys [coord-type]} ctx
        flipped? (= coord-type :flip)
        band-s (if flipped? (:sy ctx) (:sx ctx))
        num-s (if flipped? (:sx ctx) (:sy ctx))
        bw (ws/data band-s :bandwidth)
        {:keys [opacity]} style
        n-groups (clojure.core/count groups)
        sub-bw (/ (* bw 0.8) (max 1 n-groups))]
    (vec
     (for [[gi {:keys [color xs ys]}] (map-indexed vector groups)
           i (range (clojure.core/count xs))
           :let [[cr cg cb _] color
                 cat (nth xs i)
                 val (nth ys i)
                 band-info (band-s cat true)
                 band-start (:rstart band-info)
                 band-end (:rend band-info)
                 band-mid (/ (+ band-start band-end) 2.0)
                 cat-lo (+ (- band-mid (/ (* n-groups sub-bw) 2.0)) (* gi sub-bw))
                 cat-hi (+ cat-lo sub-bw)
                 val-lo (num-s 0)
                 val-hi (num-s val)
                 [x1 y1 x2 y2 x3 y3 x4 y4]
                 (if flipped?
                   [val-lo cat-lo val-hi cat-lo val-hi cat-hi val-lo cat-hi]
                   [cat-lo val-lo cat-hi val-lo cat-hi val-hi cat-lo val-hi])]]
       (ui/with-color [cr cg cb (or opacity 1.0)]
         (ui/with-style ::ui/style-fill
           (ui/path [x1 y1] [x2 y2] [x3 y3] [x4 y4] [x1 y1])))))))

(defmethod render-layer :rect [layer ctx]
  (if (:categories layer)
    (render-layer-categorical-bars layer ctx)
    (render-layer-value-bars layer ctx)))
