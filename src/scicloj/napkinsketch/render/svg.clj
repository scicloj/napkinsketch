(ns scicloj.napkinsketch.render.svg
  "Convert membrane scene trees to SVG hiccup."
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [membrane.ui :as ui]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.render.scene :as scene]
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

(declare scene->svg)

(defprotocol ToSVG
  "Convert a membrane scene element to SVG hiccup."
  (-to-svg [elem ctx]))

(extend-protocol ToSVG
  Translate
  (-to-svg [elem ctx]
    (let [{:keys [x y drawable]} elem
          inner (scene->svg drawable ctx)]
      (when inner
        [:g {:transform (str "translate(" (double x) "," (double y) ")")}
         inner])))

  Rotate
  (-to-svg [elem ctx]
    (let [{:keys [degrees drawable]} elem
          inner (scene->svg drawable ctx)]
      (when inner
        [:g {:transform (str "rotate(" (double degrees) ")")}
         inner])))

  WithColor
  (-to-svg [elem ctx]
    (let [{:keys [color drawables]} elem
          ctx' (assoc ctx :color color)
          children (keep #(scene->svg % ctx') drawables)]
      (when (seq children)
        (into [:g] children))))

  WithStyle
  (-to-svg [elem ctx]
    (let [{:keys [style drawables]} elem
          style-key (case style
                      :membrane.ui/style-fill :fill
                      :membrane.ui/style-stroke :stroke
                      :membrane.ui/style-stroke-and-fill :stroke-and-fill
                      :fill)
          ctx' (assoc ctx :style style-key)
          children (keep #(scene->svg % ctx') drawables)]
      (when (seq children)
        (into [:g] children))))

  WithStrokeWidth
  (-to-svg [elem ctx]
    (let [{:keys [stroke-width drawables]} elem
          ctx' (assoc ctx :stroke-width stroke-width)
          children (keep #(scene->svg % ctx') drawables)]
      (when (seq children)
        (into [:g] children))))

  Path
  (-to-svg [elem ctx]
    (let [pts (:points elem)
          attrs (apply-style-attrs ctx)]
      (if (= :stroke (:style ctx))
        [:polyline (assoc attrs :points (points->str pts))]
        [:polygon (assoc attrs
                         :points (points->str pts)
                         :shape-rendering "crispEdges")])))

  RoundedRectangle
  (-to-svg [elem ctx]
    (let [{:keys [width height border-radius]} elem
          attrs (apply-style-attrs ctx)]
      [:rect (merge attrs {:x 0 :y 0 :width width :height height
                           :rx border-radius :ry border-radius})]))

  Rectangle
  (-to-svg [elem ctx]
    (let [{:keys [width height]} elem
          attrs (apply-style-attrs ctx)]
      [:rect (merge attrs {:x 0 :y 0 :width width :height height})]))

  Label
  (-to-svg [elem ctx]
    (let [{:keys [text font]} elem
          {:keys [color opacity]} (rgba->css (:color ctx))
          font-size (if font (:size font) 14)
          font-name (when font (:name font))]
      [:text (cond-> {:fill color :fill-opacity opacity
                      :font-size font-size
                      :dominant-baseline "hanging"}
               font-name (assoc :font-family font-name))
       text]))

  Object
  (-to-svg [_ _ctx] nil))

(defn scene->svg
  "Convert a membrane scene element to SVG hiccup.
   ctx tracks inherited drawing state (color, style, stroke-width)."
  ([elem] (scene->svg elem default-ctx))
  ([elem ctx]
   (cond
     (nil? elem) nil
     (sequential? elem)
     (let [children (keep #(scene->svg % ctx) elem)]
       (when (seq children)
         (if (= 1 (count children))
           (first children)
           (into [:g] children))))
     :else (-to-svg elem ctx))))

;; ---- Wrap in SVG root ----

(defn wrap-svg
  "Wrap SVG hiccup body in an <svg> root element."
  [width height body]
  [:svg {:xmlns "http://www.w3.org/2000/svg"
         :width width :height height
         :viewBox (str "0 0 " width " " height)}
   body])

;; ---- Sketch → Scene (membrane scene tree) ----

;; Scene-building code lives in render/scene.clj.
;; This namespace handles scene → SVG conversion only.

;; ---- render-figure :svg ----

(defmethod render/render-figure :svg [sketch _ _opts]
  (let [{:keys [total-width total-height]} sketch
        scene-tree (scene/sketch->scene sketch)
        svg-body (scene->svg scene-tree)
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
