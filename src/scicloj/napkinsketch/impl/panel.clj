(ns scicloj.napkinsketch.impl.panel
  (:require [membrane.ui :as ui]
            [wadogo.scale :as ws]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.stat :as stat]
            [scicloj.napkinsketch.impl.scale :as scale]
            [scicloj.napkinsketch.impl.coord :as coord]
            [scicloj.napkinsketch.impl.mark :as mark]))

;; ---- Grid Lines ----

(defn render-grid
  "Render grid lines as membrane scene."
  [sx sy pw ph m cfg]
  (let [cfg (or cfg defaults/defaults)
        {:keys [grid]} defaults/theme
        grid-rgba (defaults/hex->rgba grid)
        grid-w (:grid-stroke-width cfg)
        x-ticks (ws/ticks sx (scale/tick-count (- pw (* 2 m)) (:tick-spacing-x cfg)))
        y-ticks (ws/ticks sy (scale/tick-count (- ph (* 2 m)) (:tick-spacing-y cfg)))]
    (vec
     (concat
      (for [t x-ticks :let [px (sx t)]]
        (ui/with-color grid-rgba
          (ui/with-stroke-width grid-w
            (ui/with-style ::ui/style-stroke
              (ui/path [px m] [px (- ph m)])))))
      (for [t y-ticks :let [py (sy t)]]
        (ui/with-color grid-rgba
          (ui/with-stroke-width grid-w
            (ui/with-style ::ui/style-stroke
              (ui/path [m py] [(- pw m) py])))))))))

;; ---- Tick Labels ----

(defn render-x-ticks-numeric [sx pw ph m cfg]
  (let [n (scale/tick-count (- pw (* 2 m)) (:tick-spacing-x cfg))
        ticks (ws/ticks sx n)
        labels (scale/format-ticks sx ticks)
        fsize (:font-size defaults/theme)
        tick-color [0.4 0.4 0.4 1.0]]
    (vec
     (map (fn [t label]
            (let [px (sx t)]
              (ui/translate (- (double px) (* (count label) (/ fsize 3.5)))
                            (- (double ph) (double fsize) 1)
                            (ui/with-color tick-color
                              (ui/label label (ui/font nil fsize))))))
          ticks labels))))

(defn render-x-ticks-categorical [sx pw ph m cfg]
  (let [ticks (ws/ticks sx)
        labels (map str ticks)
        fsize (:font-size defaults/theme)
        tick-color [0.4 0.4 0.4 1.0]]
    (vec
     (map (fn [t label]
            (let [px (sx t)]
              (ui/translate (- (double px) (* (count label) (/ fsize 3.5)))
                            (- (double ph) (double fsize) 1)
                            (ui/with-color tick-color
                              (ui/label label (ui/font nil fsize))))))
          ticks labels))))

(defn render-y-ticks-numeric [sy pw ph m cfg]
  (let [n (scale/tick-count (- ph (* 2 m)) (:tick-spacing-y cfg))
        ticks (ws/ticks sy n)
        labels (scale/format-ticks sy ticks)
        fsize (:font-size defaults/theme)
        tick-color [0.4 0.4 0.4 1.0]]
    (vec
     (map (fn [t label]
            (let [py (sy t)
                  lw (* (count label) (/ fsize 2.0))]
              (ui/translate (- (double m) lw 3)
                            (- (double py) (/ fsize 2.0))
                            (ui/with-color tick-color
                              (ui/label label (ui/font nil fsize))))))
          ticks labels))))

(defn render-y-ticks-categorical [sy pw ph m cfg]
  (let [ticks (ws/ticks sy)
        labels (map str ticks)
        fsize (:font-size defaults/theme)
        tick-color [0.4 0.4 0.4 1.0]]
    (vec
     (map (fn [t label]
            (let [py (sy t)
                  lw (* (count label) (/ fsize 2.0))]
              (ui/translate (- (double m) lw 3)
                            (- (double py) (/ fsize 2.0))
                            (ui/with-color tick-color
                              (ui/label label (ui/font nil fsize))))))
          ticks labels))))

;; ---- render-panel ----

(defn render-panel
  "Render a single panel as a membrane scene tree."
  [panel-views pw ph m & {:keys [x-domain y-domain show-x? show-y?
                                 all-colors cfg]
                          :or {show-x? true show-y? true}}]
  (let [cfg (or cfg defaults/defaults)
        v1 (first panel-views)
        coord-type (or (:coord v1) :cartesian)
        x-scale-spec (or (:x-scale v1) {:type :linear})
        y-scale-spec (or (:y-scale v1) {:type :linear})

        ;; Compute stats for data views
        view-stats (for [v panel-views
                         :let [rv (view/resolve-view v)]
                         :when (and (:mark rv)
                                    (not (view/annotation-marks (:mark rv))))]
                     (let [st (stat/compute-stat (assoc rv :cfg cfg))]
                       {:view rv :stat st}))

        ;; Merge domains
        stat-x-doms (keep #(get-in % [:stat :x-domain]) view-stats)
        stat-y-doms (keep #(get-in % [:stat :y-domain]) view-stats)

        merged-x-dom (or x-domain
                         (:domain x-scale-spec)
                         (if (scale/categorical-domain? (first stat-x-doms))
                           (distinct (mapcat identity stat-x-doms))
                           (let [lo (reduce min (map first stat-x-doms))
                                 hi (reduce max (map second stat-x-doms))]
                             (scale/pad-domain [lo hi] x-scale-spec))))
        merged-y-dom (or y-domain
                         (:domain y-scale-spec)
                         (if (scale/categorical-domain? (first stat-y-doms))
                           (distinct (mapcat identity stat-y-doms))
                           (let [lo (reduce min (map first stat-y-doms))
                                 hi (reduce max (map second stat-y-doms))]
                             (scale/pad-domain [lo hi] y-scale-spec))))

        ;; Stack adjustment
        merged-y-dom (if (and (sequential? merged-y-dom) (number? (first merged-y-dom))
                              (some #(= :stack (:position (:view %))) view-stats))
                       (let [stacked (filter #(= :stack (:position (:view %))) view-stats)
                             max-stack (reduce max 0
                                               (for [{:keys [stat]} stacked
                                                     :when (:bars stat)
                                                     cat (:categories stat)]
                                                 (reduce + (for [{:keys [counts]} (:bars stat)
                                                                 {:keys [category count]} counts
                                                                 :when (= category cat)]
                                                             count))))]
                         (if (pos? max-stack)
                           (scale/pad-domain [0 max-stack] y-scale-spec)
                           merged-y-dom))
                       merged-y-dom)

        ;; Build scales (swap for flip)
        [x-dom' y-dom'] (if (= coord-type :flip)
                          [merged-y-dom merged-x-dom]
                          [merged-x-dom merged-y-dom])
        x-px [m (- pw m)]
        y-px [(- ph m) m]
        sx (scale/make-scale x-dom' x-px (if (= coord-type :flip) y-scale-spec x-scale-spec))
        sy (scale/make-scale y-dom' y-px (if (= coord-type :flip) x-scale-spec y-scale-spec))
        cat-x? (scale/categorical-domain? x-dom')
        cat-y? (scale/categorical-domain? y-dom')

        ;; Coord function
        coord-fn (coord/make-coord coord-type sx sy pw ph m)

        ;; Mark context — include coord-type so bar renderers can handle flip
        ctx {:coord-fn coord-fn :all-colors all-colors :sx sx :sy sy
             :coord-type coord-type :cfg cfg}

        ;; Background
        bg-rgba (defaults/hex->rgba (:bg defaults/theme))
        background (ui/with-color bg-rgba
                     (ui/with-style ::ui/style-fill
                       (ui/rectangle pw ph)))

        ;; Grid
        grid (render-grid sx sy pw ph m cfg)

        ;; Data marks
        marks (vec
               (mapcat (fn [{:keys [view stat]}]
                         (let [mk (:mark view)
                               mark-ctx (cond-> (assoc ctx :position (or (:position view) :dodge))
                                          (:fixed-color view) (assoc :fixed-color (:fixed-color view))
                                          (:fixed-size view) (assoc :fixed-size (:fixed-size view)))]
                           (mark/render-mark mk stat mark-ctx)))
                       view-stats))

        ;; Ticks
        x-ticks (when show-x?
                  (if cat-x?
                    (render-x-ticks-categorical sx pw ph m cfg)
                    (render-x-ticks-numeric sx pw ph m cfg)))
        y-ticks (when show-y?
                  (if cat-y?
                    (render-y-ticks-categorical sy pw ph m cfg)
                    (render-y-ticks-numeric sy pw ph m cfg)))]

    ;; Assemble scene
    (vec (concat [background] grid marks
                 (or x-ticks []) (or y-ticks [])))))

;; ==== Sketch-Based Panel Rendering ====

(defn render-grid-from-ticks
  "Render grid lines using pre-computed tick values from a sketch."
  [sx sy x-ticks y-ticks pw ph m cfg]
  (let [{:keys [grid]} defaults/theme
        grid-rgba (defaults/hex->rgba grid)
        grid-w (:grid-stroke-width cfg)]
    (vec
     (concat
      (for [t (:values x-ticks) :let [px (sx t)]]
        (ui/with-color grid-rgba
          (ui/with-stroke-width grid-w
            (ui/with-style ::ui/style-stroke
              (ui/path [px m] [px (- ph m)])))))
      (for [t (:values y-ticks) :let [py (sy t)]]
        (ui/with-color grid-rgba
          (ui/with-stroke-width grid-w
            (ui/with-style ::ui/style-stroke
              (ui/path [m py] [(- pw m) py])))))))))

(defn render-tick-labels
  "Render tick labels from pre-computed tick info in a sketch."
  [axis tick-info scale pw ph m]
  (let [{:keys [values labels]} tick-info
        fsize (:font-size defaults/theme)
        tick-color [0.4 0.4 0.4 1.0]]
    (when (seq values)
      (vec
       (map (fn [t label]
              (if (= axis :x)
                (let [px (scale t)]
                  (ui/translate (- (double px) (* (count label) (/ fsize 3.5)))
                                (- (double ph) (double fsize) 1)
                                (ui/with-color tick-color
                                  (ui/label label (ui/font nil fsize)))))
                (let [py (scale t)
                      lw (* (count label) (/ fsize 2.0))]
                  (ui/translate (- (double m) lw 3)
                                (- (double py) (/ fsize 2.0))
                                (ui/with-color tick-color
                                  (ui/label label (ui/font nil fsize)))))))
            values labels)))))

(defn render-panel-from-sketch
  "Render a sketch panel as a membrane scene tree.
   Takes a panel map from resolve-sketch and pixel dimensions."
  [panel pw ph m cfg]
  (let [{:keys [x-domain y-domain x-scale y-scale coord
                x-ticks y-ticks layers]} panel
        coord-type (or coord :cartesian)

        ;; Build wadogo scales from domains + pixel ranges
        x-px [m (- pw m)]
        y-px [(- ph m) m]
        sx (scale/make-scale x-domain x-px x-scale)
        sy (scale/make-scale y-domain y-px y-scale)

        ;; Coord function
        coord-fn (coord/make-coord coord-type sx sy pw ph m)

        ;; Rendering context for mark/render-layer
        ctx {:coord-fn coord-fn :sx sx :sy sy
             :coord-type coord-type :cfg cfg}

        ;; Background
        bg-rgba (defaults/hex->rgba (:bg defaults/theme))
        background (ui/with-color bg-rgba
                     (ui/with-style ::ui/style-fill
                       (ui/rectangle pw ph)))

        ;; Grid
        grid (render-grid-from-ticks sx sy x-ticks y-ticks pw ph m cfg)

        ;; Data marks from sketch layers
        marks (vec (mapcat #(mark/render-layer % ctx) layers))

        ;; Tick labels
        x-tick-scene (render-tick-labels :x x-ticks sx pw ph m)
        y-tick-scene (render-tick-labels :y y-ticks sy pw ph m)]

    (vec (concat [background] grid marks
                 (or x-tick-scene []) (or y-tick-scene [])))))

