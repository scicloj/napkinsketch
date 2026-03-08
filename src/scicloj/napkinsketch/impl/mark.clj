(ns scicloj.napkinsketch.impl.mark
  (:require [membrane.ui :as ui]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [clojure.string :as str]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [wadogo.scale :as ws]))

;; ---- Helpers ----

(defn resolve-color
  "Resolve a color to [r g b a] given the context."
  [{:keys [all-colors fixed-color cfg]} color-val]
  (let [cfg (or cfg defaults/defaults)]
    (cond
      color-val (defaults/color-for all-colors color-val)
      fixed-color (if (string? fixed-color) (defaults/hex->rgba fixed-color) fixed-color)
      :else (defaults/hex->rgba (:default-color cfg)))))

(defn resolve-opacity
  "Get opacity from config."
  [cfg key]
  (get (or cfg defaults/defaults) key 1.0))

;; ---- render-mark multimethod ----

(defmulti render-mark
  "Render a mark layer as membrane scene tree."
  (fn [mark stat ctx] mark))

;; ---- Point ----

(defmethod render-mark :point [_ stat ctx]
  (let [{:keys [all-colors cfg]} ctx
        cfg (or cfg defaults/defaults)
        r (:point-radius cfg)
        opacity (:point-opacity cfg)
        size-bufs (keep :sizes (:points stat))
        size-scale (when (seq size-bufs)
                     (let [all-sizes (dtype/concat-buffers size-bufs)
                           lo (dfn/reduce-min all-sizes) hi (dfn/reduce-max all-sizes)
                           span (max 1e-6 (- (double hi) (double lo)))]
                       (fn [v] (+ 2.0 (* 6.0 (/ (- (double v) (double lo)) span))))))]
    (vec
     (for [{:keys [color xs ys sizes row-indices]} (:points stat)
           :let [rgba (resolve-color ctx color)]
           i (range (count xs))
           :let [[px py] ((:coord-fn ctx) (nth xs i) (nth ys i))
                 pt-r (if sizes (size-scale (nth sizes i))
                          (or (:fixed-size ctx) r))
                 [cr cg cb _] rgba]]
       (-> (ui/translate (- (double px) pt-r) (- (double py) pt-r)
                         (ui/with-color [cr cg cb opacity]
                           (ui/with-style ::ui/style-fill
                             (ui/rounded-rectangle (* 2 pt-r) (* 2 pt-r) pt-r))))
           (assoc :row-idx (when row-indices (nth row-indices i))))))))

(defmethod render-mark :default [_ stat ctx]
  (render-mark :point stat ctx))

;; ---- Bar (histogram) ----

(defmethod render-mark :bar [_ stat ctx]
  (let [{:keys [coord-fn all-colors cfg]} ctx
        cfg (or cfg defaults/defaults)
        opacity (:bar-opacity cfg)]
    (vec
     (for [{:keys [color bin-maps]} (:bins stat)
           :let [rgba (resolve-color ctx color)]
           {:keys [min max count]} bin-maps
           :let [[x1 y1] (coord-fn min 0)
                 [x2 y2] (coord-fn max 0)
                 [x3 y3] (coord-fn max count)
                 [x4 y4] (coord-fn min count)
                 [cr cg cb _] rgba]]
       (ui/with-color [cr cg cb opacity]
         (ui/with-style ::ui/style-fill
           (ui/path [x1 y1] [x2 y2] [x3 y3] [x4 y4] [x1 y1])))))))

;; ---- Line ----

(defmethod render-mark :line [_ stat ctx]
  (let [{:keys [coord-fn all-colors cfg]} ctx
        cfg (or cfg defaults/defaults)
        lw (:line-width cfg)]
    (vec
     (concat
      ;; Regression lines (line segments)
      (when-let [lines (:lines stat)]
        (for [{:keys [color x1 y1 x2 y2]} lines
              :let [rgba (resolve-color ctx color)
                    [px1 py1] (coord-fn x1 y1)
                    [px2 py2] (coord-fn x2 y2)
                    [cr cg cb _] rgba]]
          (ui/with-color [cr cg cb]
            (ui/with-stroke-width lw
              (ui/with-style ::ui/style-stroke
                (ui/path [px1 py1] [px2 py2]))))))
      ;; Polylines (connected points)
      (when-let [pts (:points stat)]
        (for [{:keys [color xs ys]} pts
              :let [rgba (resolve-color ctx color)
                    projected (sort-by first (map (fn [x y] (coord-fn x y)) xs ys))
                    [cr cg cb _] rgba]]
          (ui/with-color [cr cg cb]
            (ui/with-stroke-width lw
              (ui/with-style ::ui/style-stroke
                (apply ui/path projected))))))))))

;; ---- Rect (categorical bars) ----

(defn render-categorical-bars [stat ctx]
  (let [{:keys [all-colors cfg coord-type]} ctx
        cfg (or cfg defaults/defaults)
        flipped? (= coord-type :flip)
        ;; When flipped, the band scale is sy and the numeric scale is sx
        band-s (if flipped? (:sy ctx) (:sx ctx))
        num-s (if flipped? (:sx ctx) (:sy ctx))
        bw (ws/data band-s :bandwidth)
        opacity (:bar-opacity cfg)
        position (or (:position ctx) :dodge)
        cum-y (atom {})
        active-map (when (= position :dodge)
                     (into {}
                           (for [cat (:categories stat)]
                             [cat (keep-indexed
                                   (fn [bi {:keys [counts]}]
                                     (let [c (some #(when (= cat (:category %)) (:count %)) counts)]
                                       (when (and c (pos? c)) bi)))
                                   (:bars stat))])))]
    (vec
     (for [[bi {:keys [color counts]}] (map-indexed vector (:bars stat))
           {:keys [category count]} counts
           :when (or (= position :stack) (pos? count))
           :let [rgba (resolve-color ctx color)
                 [cr cg cb _] rgba
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
               ;; For cartesian: x=cat-axis, y=val-axis; for flip: swap
               [x1 y1 x2 y2 x3 y3 x4 y4]
               (if flipped?
                 [val-lo cat-lo val-hi cat-lo val-hi cat-hi val-lo cat-hi]
                 [cat-lo val-lo cat-hi val-lo cat-hi val-hi cat-lo val-hi])]
           (swap! cum-y assoc category (+ base count))
           (ui/with-color [cr cg cb opacity]
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
           (ui/with-color [cr cg cb opacity]
             (ui/with-style ::ui/style-fill
               (ui/path [x1 y1] [x2 y2] [x3 y3] [x4 y4] [x1 y1])))))))))

(defn render-value-bars [stat ctx]
  (let [{:keys [all-colors cfg coord-type]} ctx
        cfg (or cfg defaults/defaults)
        flipped? (= coord-type :flip)
        band-s (if flipped? (:sy ctx) (:sx ctx))
        num-s (if flipped? (:sx ctx) (:sy ctx))
        bw (ws/data band-s :bandwidth)
        opacity (:bar-opacity cfg)
        groups (:points stat)
        n-groups (clojure.core/count groups)
        sub-bw (/ (* bw 0.8) (max 1 n-groups))]
    (vec
     (for [[gi {:keys [color xs ys]}] (map-indexed vector groups)
           i (range (clojure.core/count xs))
           :let [rgba (resolve-color ctx color)
                 [cr cg cb _] rgba
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
       (ui/with-color [cr cg cb opacity]
         (ui/with-style ::ui/style-fill
           (ui/path [x1 y1] [x2 y2] [x3 y3] [x4 y4] [x1 y1])))))))

(defmethod render-mark :rect [_ stat ctx]
  (if (:bars stat)
    (render-categorical-bars stat ctx)
    (render-value-bars stat ctx)))
