(ns scicloj.napkinsketch.render.svg
  (:require [clojure.string :as str])
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

;; ---- Core conversion ----

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
         [:polygon (assoc attrs :points (points->str pts))]))

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
