(ns scicloj.napkinsketch.render.svg
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [membrane.ui :as ui]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.panel :as panel]
            [scicloj.napkinsketch.impl.render :as render])
  (:import [membrane.ui Translate WithColor WithStyle WithStrokeWidth
            Path RoundedRectangle Rectangle Label Rotate]))

;; ---- Color helpers ----

(defn rgba->css
  "Convert [r g b] or [r g b a] to CSS rgb string and opacity."
  [[r g b a]]
  {:color (str "rgb(" (int (* 255 (double r))) ","
               (int (* 255 (double g))) ","
               (int (* 255 (double b))) ")")
   :opacity (if a (double a) 1.0)})

;; ---- Drawing context ----

(def default-ctx
  {:color [0.2 0.2 0.2 1.0]
   :style :fill
   :stroke-width 1})

;; ---- Scene → SVG conversion ----

(defn- points->str
  "Convert a seq of [x y] pairs to SVG points attribute string."
  [pts]
  (str/join " " (map (fn [[x y]] (str (double x) "," (double y))) pts)))

(defn- apply-style-attrs
  "Generate SVG attributes from drawing context for a shape element."
  [ctx]
  (let [{:keys [color opacity]} (rgba->css (:color ctx))
        style (:style ctx)]
    (case style
      :fill {:fill color :fill-opacity opacity :stroke "none"}
      :stroke {:fill "none" :stroke color :stroke-opacity opacity
               :stroke-width (:stroke-width ctx)}
      :stroke-and-fill {:fill color :fill-opacity opacity
                        :stroke color :stroke-opacity opacity
                        :stroke-width (:stroke-width ctx)}
      {:fill color :fill-opacity opacity :stroke "none"})))

(defn scene->svg
  "Convert a membrane scene element to SVG hiccup.
   ctx tracks inherited drawing state (color, style, stroke-width)."
  ([elem] (scene->svg elem default-ctx))
  ([elem ctx]
   (cond
     (nil? elem) nil

     (instance? Translate elem)
     (let [{:keys [x y drawable]} elem
           inner (scene->svg drawable ctx)]
       (when inner
         [:g {:transform (str "translate(" (double x) "," (double y) ")")}
          inner]))

     (instance? Rotate elem)
     (let [{:keys [degrees drawable]} elem
           inner (scene->svg drawable ctx)]
       (when inner
         [:g {:transform (str "rotate(" (double degrees) ")")}
          inner]))

     (instance? WithColor elem)
     (let [{:keys [color drawables]} elem
           ctx' (assoc ctx :color color)]
       (let [children (keep #(scene->svg % ctx') drawables)]
         (when (seq children)
           (into [:g] children))))

     (instance? WithStyle elem)
     (let [{:keys [style drawables]} elem
           style-key (case style
                       :membrane.ui/style-fill :fill
                       :membrane.ui/style-stroke :stroke
                       :membrane.ui/style-stroke-and-fill :stroke-and-fill
                       :fill)
           ctx' (assoc ctx :style style-key)]
       (let [children (keep #(scene->svg % ctx') drawables)]
         (when (seq children)
           (into [:g] children))))

     (instance? WithStrokeWidth elem)
     (let [{:keys [stroke-width drawables]} elem
           ctx' (assoc ctx :stroke-width stroke-width)]
       (let [children (keep #(scene->svg % ctx') drawables)]
         (when (seq children)
           (into [:g] children))))

     (instance? Path elem)
     (let [pts (:points elem)
           attrs (apply-style-attrs ctx)]
       (if (= :stroke (:style ctx))
         [:polyline (assoc attrs :points (points->str pts))]
         [:polygon (assoc attrs
                          :points (points->str pts)
                          :shape-rendering "crispEdges")]))

     (instance? RoundedRectangle elem)
     (let [{:keys [width height border-radius]} elem
           attrs (apply-style-attrs ctx)]
       [:rect (merge attrs {:x 0 :y 0 :width width :height height
                            :rx border-radius :ry border-radius})])

     (instance? Rectangle elem)
     (let [{:keys [width height]} elem
           attrs (apply-style-attrs ctx)]
       [:rect (merge attrs {:x 0 :y 0 :width width :height height})])

     (instance? Label elem)
     (let [{:keys [text font]} elem
           {:keys [color opacity]} (rgba->css (:color ctx))
           font-size (if font (:size font) 14)
           font-name (when font (:name font))]
       [:text (cond-> {:fill color :fill-opacity opacity
                       :font-size font-size
                       :dominant-baseline "hanging"}
                font-name (assoc :font-family font-name))
        text])

     (sequential? elem)
     (let [children (keep #(scene->svg % ctx) elem)]
       (when (seq children)
         (if (= 1 (count children))
           (first children)
           (into [:g] children))))

     ;; Unknown — skip
     :else nil)))

;; ---- Wrap in SVG root ----

(defn wrap-svg
  "Wrap SVG hiccup body in an <svg> root element."
  [width height body]
  [:svg {:xmlns "http://www.w3.org/2000/svg"
         :width width :height height
         :viewBox (str "0 0 " width " " height)}
   body])

;; ---- Sketch → Scene (membrane scene tree) ----

(defn- render-legend-from-sketch
  "Render legend from sketch legend data as a membrane scene."
  [legend x y]
  (let [{:keys [title]} legend
        fsize 10
        title-color [0.2 0.2 0.2 1.0]]
    (if (= :continuous (:type legend))
      ;; Continuous gradient legend
      (let [{:keys [min max stops]} legend
            bar-h 120 bar-w 12 n-stops 20]
        (vec
         (concat
          (when title
            [(ui/translate x (- y 12)
                           (ui/with-color title-color
                             (ui/label (defaults/fmt-name title) (ui/font nil 9))))])
          ;; Gradient bar: stack of small colored rectangles
          (for [i (range n-stops)
                :let [t (/ (double i) (dec n-stops))
                      c (defaults/gradient-color t)
                      [cr cg cb _] c
                      ry (+ y (* (- 1.0 t) bar-h))]]
            (ui/translate x ry
                          (ui/with-color [cr cg cb 1.0]
                            (ui/with-style ::ui/style-fill
                              (ui/rectangle bar-w (/ bar-h n-stops))))))
          ;; Min/max labels
          [(ui/translate (+ x bar-w 4) (+ y bar-h -4)
                         (ui/with-color title-color
                           (ui/label (format "%.4g" (double min)) (ui/font nil 8))))
           (ui/translate (+ x bar-w 4) (+ y 6)
                         (ui/with-color title-color
                           (ui/label (format "%.4g" (double max)) (ui/font nil 8))))])))
      ;; Categorical swatch legend
      (let [{:keys [entries]} legend]
        (vec
         (concat
          (when title
            [(ui/translate x (- y 12)
                           (ui/with-color title-color
                             (ui/label (defaults/fmt-name title) (ui/font nil 9))))])
          (for [[i {:keys [label color]}] (map-indexed vector entries)
                :let [[cr cg cb _] color]]
            (ui/translate x (+ y (* i 16))
                          [(ui/translate 0 0
                                         (ui/with-color [cr cg cb 1.0]
                                           (ui/with-style ::ui/style-fill
                                             (ui/rounded-rectangle 8 8 4))))
                           (ui/translate 12 0
                                         (ui/with-color title-color
                                           (ui/label label (ui/font nil fsize))))]))))))))

(defn- render-x-label
  "Render x-axis label centered below the plot area."
  [label total-w y-pos]
  (let [fsize (:label-font-size defaults/defaults)]
    (ui/translate (/ total-w 2.0) y-pos
                  (ui/with-color [0.2 0.2 0.2 1.0]
                    (ui/label label (ui/font nil fsize))))))

(defn- render-y-label
  "Render y-axis label. Uses a Rotate to place vertically."
  [label total-h x-pos]
  (let [fsize (:label-font-size defaults/defaults)
        cy (/ total-h 2.0)]
    (ui/translate x-pos cy
                  (membrane.ui.Rotate. -90
                                       (ui/with-color [0.2 0.2 0.2 1.0]
                                         (ui/label label (ui/font nil fsize)))))))

(defn- render-title
  "Render plot title centered above the plot area."
  [title total-w]
  (let [fsize (:title-font-size defaults/defaults)]
    (ui/translate (/ total-w 2.0) 14
                  (ui/with-color [0.2 0.2 0.2 1.0]
                    (ui/label title (ui/font nil fsize))))))

(defn sketch->scene
  "Build a membrane scene tree from a sketch.
   Returns a vector of membrane drawables representing the complete plot."
  [sketch]
  (let [{:keys [margin total-width total-height panel-width panel-height
                title x-label y-label legend panels layout grid]} sketch
        {:keys [x-label-pad y-label-pad title-pad legend-w strip-h strip-w]} layout
        grid-rows (:rows grid)
        grid-cols (:cols grid)
        pw panel-width
        ph panel-height

        ;; Render each panel, positioned in the grid
        panel-scenes
        (vec
         (for [p panels
               :let [ri (:row p)
                     ci (:col p)
                     show-x? (= ri (dec grid-rows))
                     show-y? (zero? ci)
                     x-off (+ y-label-pad (* ci pw))
                     y-off (+ title-pad strip-h (* ri ph))]]
           (ui/translate x-off y-off
                         (panel/render-panel-from-sketch p pw ph margin
                                                         :show-x? show-x?
                                                         :show-y? show-y?))))

        ;; Strip labels (column headers on top, row headers on right)
        strip-label-color [0.2 0.2 0.2 1.0]
        strip-fsize (or (:strip-font-size defaults/defaults) 10)

        col-strips
        (when (pos? strip-h)
          (vec
           (for [p panels
                 :when (and (zero? (:row p)) (:col-label p))]
             (let [cx (+ y-label-pad (* (:col p) pw) (/ pw 2.0))
                   label (:col-label p)]
               (ui/translate (- cx (* (count label) (/ strip-fsize 3.5)))
                             (+ title-pad 2)
                             (ui/with-color strip-label-color
                               (ui/label label (ui/font nil strip-fsize))))))))

        row-strips
        (when (pos? strip-w)
          (vec
           (for [p panels
                 :when (and (= (:col p) (dec grid-cols)) (:row-label p))]
             (let [cy (+ title-pad strip-h (* (:row p) ph) (/ ph 2.0))]
               (ui/translate (+ y-label-pad (* grid-cols pw) 5)
                             (- cy 5)
                             (ui/with-color strip-label-color
                               (ui/label (:row-label p) (ui/font nil strip-fsize))))))))]

    ;; Build full scene
    (vec
     (concat
      ;; Title
      (when title
        [(render-title title total-width)])
      ;; Y-axis label
      (when y-label
        [(render-y-label y-label total-height 12)])
      ;; X-axis label
      (when x-label
        [(render-x-label x-label total-width (- total-height 3))])
      ;; Legend
      (when legend
        (render-legend-from-sketch legend
                                   (+ y-label-pad (* grid-cols pw) strip-w 10)
                                   (+ title-pad strip-h 20)))
      ;; Panels
      panel-scenes
      ;; Strip labels
      (or col-strips [])
      (or row-strips [])))))

;; ---- render-figure :svg ----

(defmethod render/render-figure :svg [sketch _ _opts]
  (let [{:keys [total-width total-height]} sketch
        scene (sketch->scene sketch)
        svg-body (scene->svg scene)
        svg (wrap-svg total-width total-height svg-body)]
    (kind/hiccup svg)))

;; ---- SVG inspection ----

(defn- collect-elements
  "Walk SVG hiccup and collect all elements matching a tag keyword."
  [svg tag]
  (let [result (atom [])]
    (walk/postwalk
     (fn [x]
       (when (and (vector? x) (= tag (first x)) (map? (second x)))
         (swap! result conj x))
       x)
     svg)
    @result))

(defn svg-summary
  "Extract structural summary from SVG hiccup for testing.
   Returns a map with :width, :height, :panels, :points, :lines,
   :polygons, and :texts — useful for asserting plot structure.
   (svg-summary (plot views))  — summary of rendered SVG

   Counts:
   :panels  — number of plot panels (background rectangles)
   :points  — number of data point markers (excludes legend swatches)
   :lines   — number of data polylines (excludes grid lines)
   :polygons — number of filled polygons (bars, histogram bins)
   :texts   — vector of all text content strings"
  [svg]
  (let [attrs (when (and (vector? svg) (map? (second svg))) (second svg))
        bg-color (str "rgb(" (clojure.string/join ","
                                                  (mapv #(int (* 255 (double %)))
                                                        (take 3 (defaults/hex->rgba (:bg defaults/theme))))) ")")
        grid-color (str "rgb(" (clojure.string/join ","
                                                    (mapv #(int (* 255 (double %)))
                                                          (take 3 (defaults/hex->rgba (:grid defaults/theme))))) ")")
        rects (collect-elements svg :rect)
        polylines (collect-elements svg :polyline)
        polygons (collect-elements svg :polygon)
        texts (collect-elements svg :text)
        panel-rects (filter #(= bg-color (get (second %) :fill)) rects)
        legend-rects (filter #(and (= 8 (get (second %) :width))
                                   (= 8 (get (second %) :height))) rects)
        legend-set (set legend-rects)
        panel-set (set panel-rects)
        ;; Data rects are rounded (rx > 0); exclude panels, legend swatches,
        ;; and gradient bar segments (which have no rx attribute)
        data-rects (filter #(let [a (second %)]
                              (and (not (panel-set %))
                                   (not (legend-set %))
                                   (some? (:rx a))))
                           rects)
        data-polylines (remove #(= grid-color (get (second %) :stroke)) polylines)]
    {:width (:width attrs)
     :height (:height attrs)
     :panels (count panel-rects)
     :points (count data-rects)
     :lines (count data-polylines)
     :polygons (count polygons)
     :texts (mapv last texts)}))
