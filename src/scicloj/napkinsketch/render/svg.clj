(ns scicloj.napkinsketch.render.svg
  "Convert membrane drawable trees to SVG hiccup."
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [membrane.ui :as ui]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.render.membrane :as membrane]
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

;; ---- Membrane → SVG conversion ----

(defn- fmt
  "Format a numeric value to 2 decimal places for SVG coordinates."
  [v]
  (format "%.2f" (double v)))

(defn- points->str
  "Convert a seq of [x y] pairs to SVG points attribute string."
  [pts]
  (str/join " " (map (fn [[x y]] (str (fmt x) "," (fmt y))) pts)))

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

(declare membrane->svg)

(defprotocol ToSVG
  "Convert a membrane drawable element to SVG hiccup."
  (-to-svg [elem ctx]))

(defn- data-attrs
  "Extract data-* attributes from extra keys on a membrane element."
  [elem]
  (cond-> {}
    (:row-idx elem) (assoc :data-row-idx (:row-idx elem))
    (:tooltip elem) (assoc :data-tooltip (:tooltip elem))
    (:legend elem) (assoc :data-legend "true")))

(extend-protocol ToSVG
  Translate
  (-to-svg [elem ctx]
    (let [{:keys [x y drawable]} elem
          inner (membrane->svg drawable ctx)
          attrs (merge {:transform (str "translate(" (fmt x) "," (fmt y) ")")}
                       (data-attrs elem))]
      (when inner
        [:g attrs inner])))

  Rotate
  (-to-svg [elem ctx]
    (let [{:keys [degrees drawable]} elem
          inner (membrane->svg drawable ctx)]
      (when inner
        [:g {:transform (str "rotate(" (fmt degrees) ")")}
         inner])))

  WithColor
  (-to-svg [elem ctx]
    (let [{:keys [color drawables]} elem
          ctx' (assoc ctx :color color)
          children (keep #(membrane->svg % ctx') drawables)]
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
          children (keep #(membrane->svg % ctx') drawables)]
      (when (seq children)
        (into [:g] children))))

  WithStrokeWidth
  (-to-svg [elem ctx]
    (let [{:keys [stroke-width drawables]} elem
          ctx' (assoc ctx :stroke-width stroke-width)
          children (keep #(membrane->svg % ctx') drawables)]
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
          font-name (when font (:name font))
          text-anchor (:text-anchor elem)]
      [:text (cond-> {:fill color :fill-opacity opacity
                      :font-size font-size
                      :dominant-baseline "hanging"}
               font-name (assoc :font-family font-name)
               text-anchor (assoc :text-anchor text-anchor))
       text]))

  Object
  (-to-svg [_ _ctx] nil))

(defn membrane->svg
  "Convert a membrane drawable element to SVG hiccup.
   ctx tracks inherited drawing state (color, style, stroke-width)."
  ([elem] (membrane->svg elem default-ctx))
  ([elem ctx]
   (cond
     (nil? elem) nil
     (sequential? elem)
     (let [children (keep #(membrane->svg % ctx) elem)]
       (when (seq children)
         (if (= 1 (count children))
           (first children)
           (into [:g] children))))
     :else (-to-svg elem ctx))))

;; ---- Wrap in SVG root ----

(defn wrap-svg
  "Wrap SVG hiccup body in an <svg> root element."
  ([width height body]
   (wrap-svg width height body nil))
  ([width height body title]
   (let [attrs (cond-> {:xmlns "http://www.w3.org/2000/svg"
                        :width width :height height
                        :viewBox (str "0 0 " width " " height)
                        :role "img"
                        :font-family "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif"}
                 title (assoc :aria-label title))]
     [:svg attrs body])))

;; ---- Plan → Membrane (drawable tree) ----

;; Membrane-building code lives in render/membrane.clj.
;; This namespace handles membrane → SVG conversion only.

;; ---- plan->figure :svg ----

;; ---- Tooltip interactivity ----

(def ^:private tooltip-css
  ".nsk-tooltip { display:none; position:absolute; pointer-events:none; background:rgba(0,0,0,0.8); color:#fff; padding:6px 10px; border-radius:4px; font-family:sans-serif; font-size:13px; white-space:nowrap; z-index:10; }")

(defn- tooltip-script
  "Scittle script for tooltips on elements with data-tooltip attribute.
   Uses .closest() to walk up the DOM from the event target."
  [div-id]
  (list 'let ['container (list '.getElementById 'js/document div-id)
              'svg '(.querySelector container "svg")
              'tip-el '(.createElement js/document "div")]
        '(set! (.-className tip-el) "nsk-tooltip")
        '(.appendChild container tip-el)
        '(let [show! (fn [e]
                       (when-let [el (.closest (.-target e) "[data-tooltip]")]
                         (let [text (.getAttribute el "data-tooltip")]
                           (when text
                             (set! (.-textContent tip-el) text)
                             (set! (.. tip-el -style -display) "block")))))
               hide! (fn [_]
                       (set! (.. tip-el -style -display) "none"))
               move! (fn [e]
                       (let [r (.getBoundingClientRect container)
                             x (+ (- (.-clientX e) (.-left r)) 12)
                             y (+ (- (.-clientY e) (.-top r)) 12)]
                         (set! (.. tip-el -style -left) (str x "px"))
                         (set! (.. tip-el -style -top) (str y "px"))))]
           (.addEventListener svg "mouseover" show!)
           (.addEventListener svg "mouseout" hide!)
           (.addEventListener svg "mousemove" move!))))

(def ^:private brush-css
  ".nsk-brush-sel { fill: rgba(100,100,255,0.2); stroke: #66f; stroke-width: 1; }")

(defn- brush-script
  "Scittle script for drag-to-select brush interaction on data points.
   Drag creates a selection rectangle; points inside are highlighted,
   others dimmed. Tiny drag (< 3px) resets all to default opacity.
   Cross-panel: selection by data-row-idx works across all panels."
  [div-id]
  (list 'let ['svg (list '.querySelector (list '.getElementById 'js/document div-id) "svg")
              'pts '(.querySelectorAll svg "[data-row-idx]")
              'state '(atom {:drag false :x0 0 :y0 0 :sel nil})]
        '(.addEventListener svg "mousedown"
                            (fn [e]
                              (let [r (.getBoundingClientRect svg)
                                    x0 (- (.-clientX e) (.-left r))
                                    y0 (- (.-clientY e) (.-top r))
                                    sel (.createElementNS js/document "http://www.w3.org/2000/svg" "rect")]
                                (.setAttribute sel "class" "nsk-brush-sel")
                                (.appendChild svg sel)
                                (reset! state {:drag true :x0 x0 :y0 y0 :sel sel}))))
        '(.addEventListener svg "mousemove"
                            (fn [e]
                              (when (:drag @state)
                                (let [{:keys [x0 y0 sel]} @state
                                      r (.getBoundingClientRect svg)
                                      x1 (- (.-clientX e) (.-left r))
                                      y1 (- (.-clientY e) (.-top r))]
                                  (.setAttribute sel "x" (min x0 x1))
                                  (.setAttribute sel "y" (min y0 y1))
                                  (.setAttribute sel "width" (js/Math.abs (- x1 x0)))
                                  (.setAttribute sel "height" (js/Math.abs (- y1 y0)))))))
        '(.addEventListener svg "mouseup"
                            (fn [e]
                              (when (:drag @state)
                                (let [{:keys [sel]} @state
                                      _ (swap! state assoc :drag false)
                                      bx (js/parseFloat (.getAttribute sel "x"))
                                      by (js/parseFloat (.getAttribute sel "y"))
                                      bw (js/parseFloat (.getAttribute sel "width"))
                                      bh (js/parseFloat (.getAttribute sel "height"))]
                                  (.removeChild svg sel)
                                  (if (and (< bw 3) (< bh 3))
                                    (.forEach pts (fn [p] (.setAttribute p "opacity" "0.7")))
                                    (let [sr (.getBoundingClientRect svg)
                                          selected (atom #{})]
                                      (.forEach pts
                                                (fn [p]
                                                  (let [pr (.getBoundingClientRect p)
                                                        cx (- (+ (.-left pr) (/ (.-width pr) 2)) (.-left sr))
                                                        cy (- (+ (.-top pr) (/ (.-height pr) 2)) (.-top sr))]
                                                    (when (and (>= cx bx) (<= cx (+ bx bw))
                                                               (>= cy by) (<= cy (+ by bh)))
                                                      (swap! selected conj (.getAttribute p "data-row-idx"))))))
                                      (if (zero? (count @selected))
                                        (.forEach pts (fn [p] (.setAttribute p "opacity" "0.7")))
                                        (.forEach pts
                                                  (fn [p]
                                                    (if (contains? @selected (.getAttribute p "data-row-idx"))
                                                      (.setAttribute p "opacity" "1.0")
                                                      (.setAttribute p "opacity" "0.15")))))))))))))

(defmethod render/membrane->figure :svg [membrane-tree _ opts]
  (let [{:keys [total-width total-height tooltip brush title]} opts
        svg-body (membrane->svg membrane-tree)
        svg (wrap-svg total-width total-height svg-body title)
        interactive? (or tooltip brush)]
    (if interactive?
      (let [div-id (str "nsk-" (hash [total-width total-height title (count svg-body)]))
            css-parts (cond-> []
                        tooltip (conj tooltip-css)
                        brush (conj brush-css))]
        (kind/hiccup
         (into [:div {:id div-id
                      :style {:position "relative" :display "inline-block"}}
                [:style (apply str css-parts)]
                svg]
               (cond-> []
                 tooltip (conj (tooltip-script div-id))
                 brush (conj (brush-script div-id))))))
      (kind/hiccup svg))))

(defmethod render/plan->figure :svg [plan _ opts]
  (let [render-opts (select-keys opts [:tooltip :width :height :theme :palette
                                       :color-scale :color-midpoint])
        membrane-tree (apply membrane/plan->membrane plan
                             (mapcat identity render-opts))]
    (render/membrane->figure membrane-tree :svg
                             (assoc opts
                                    :total-width (:total-width plan)
                                    :total-height (:total-height plan)
                                    :title (:title plan)))))

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

(defn- collect-elements-excluding-legend
  "Walk SVG hiccup and collect elements matching a tag, excluding those
   inside <g data-legend=\"true\"> groups."
  [svg tag]
  (let [result (atom [])]
    (letfn [(walk-node [x in-legend?]
              (cond
                (and (vector? x) (= :g (first x)) (map? (second x))
                     (= "true" (get (second x) :data-legend)))
                nil ;; skip entire legend subtree

                (and (vector? x) (= tag (first x)) (map? (second x))
                     (not in-legend?))
                (do (swap! result conj x)
                    (doseq [child (rest (rest x))]
                      (walk-node child in-legend?)))

                (vector? x)
                (doseq [child x]
                  (walk-node child in-legend?))

                :else nil))]
      (walk-node svg false))
    @result))

(defn- escape-xml
  "Escape special characters for XML/SVG content."
  [s]
  (-> (str s)
      (str/replace "&" "&amp;")
      (str/replace "<" "&lt;")
      (str/replace ">" "&gt;")
      (str/replace "\"" "&quot;")))

(defn- attrs->str
  "Convert a map of attributes to an SVG attribute string."
  [attrs]
  (when (seq attrs)
    (str/join " " (for [[k v] attrs
                        :when (some? v)]
                    (str (name k) "=\"" (if (string? v) (escape-xml v) v) "\"")))))

(defn hiccup->svg-str
  "Convert SVG hiccup to an SVG string.
   Handles the subset of hiccup used by napkinsketch:
   vectors with tag + optional attrs map + children."
  [elem]
  (cond
    (nil? elem) ""
    (string? elem) (escape-xml elem)
    (number? elem) (str elem)
    (and (vector? elem) (keyword? (first elem)))
    (let [tag (name (first elem))
          has-attrs? (map? (second elem))
          attrs (when has-attrs? (second elem))
          children (if has-attrs? (drop 2 elem) (rest elem))
          attrs-s (attrs->str attrs)]
      (if (seq children)
        (str "<" tag (when attrs-s (str " " attrs-s)) ">"
             (str/join "" (map hiccup->svg-str children))
             "</" tag ">")
        (str "<" tag (when attrs-s (str " " attrs-s)) "/>")))
    (sequential? elem)
    (str/join "" (map hiccup->svg-str elem))
    :else (str elem)))

(defn svg-summary
  "Extract structural summary from SVG hiccup for testing.
   Returns a map with :width, :height, :panels, :points, :lines,
   :polygons, :tiles, :visible-tiles, and :texts — useful for asserting
   plot structure.
   (svg-summary (plot views))  — summary of rendered SVG

   Counts:
   :panels  — number of plot panels (large background rectangles)
   :points  — number of data point markers (small rounded rects)
   :lines   — number of non-grid polylines (data lines, annotations, whiskers)
   :polygons — number of filled polygons (bars, histogram bins, areas, violins)
   :tiles   — number of heatmap tile rectangles (small rects without border-radius)
   :visible-tiles — tiles with positive width and height (excludes degenerate zero-extent tiles)
   :texts   — vector of all text content strings

   Accepts an optional theme map to detect grid-colored polylines correctly
   when a custom theme is used."
  ([svg] (svg-summary svg nil))
  ([svg theme]
   (let [attrs (when (and (vector? svg) (map? (second svg))) (second svg))
         ;; Grid color from theme — used to filter grid polylines
         the-theme (or theme defaults/theme)
         grid-hex (:grid the-theme)
         grid-color (str "rgb(" (str/join ","
                                          (mapv #(int (* 255 (double %)))
                                                (take 3 (defaults/hex->rgba grid-hex)))) ")")
         ;; Background color from theme — used to identify panel rects
         bg-hex (:bg the-theme)
         bg-color (str "rgb(" (str/join ","
                                        (mapv #(int (* 255 (double %)))
                                              (take 3 (defaults/hex->rgba bg-hex)))) ")")
         sw (double defaults/legend-swatch-size)
         ;; Collect rects excluding those inside data-legend groups
         rects (collect-elements-excluding-legend svg :rect)
         polylines (collect-elements svg :polyline)
         polygons (collect-elements svg :polygon)
         texts (collect-elements svg :text)
         ;; Panels: large rects with theme background fill color
         panel-rects (filter #(let [a (second %)]
                                (and (nil? (:rx a))
                                     (number? (:width a))
                                     (> (double (:width a)) 50)
                                     (number? (:height a))
                                     (> (double (:height a)) 50)
                                     (= bg-color (:fill a))))
                             rects)
         panel-set (set panel-rects)
         ;; Points: rects with rx > 0, excluding legend swatches (known size)
         legend-rects (filter #(let [a (second %)]
                                 (and (= sw (double (or (:width a) 0)))
                                      (= sw (double (or (:height a) 0)))))
                              rects)
         legend-set (set legend-rects)
         data-rects (filter #(let [a (second %)]
                               (and (not (legend-set %))
                                    (some? (:rx a))
                                    (number? (:rx a))
                                    (pos? (double (:rx a)))))
                            rects)
         ;; Tiles: rects without rx that are not panels or legend swatches
         tile-rects (filter #(let [a (second %)]
                               (and (not (panel-set %))
                                    (not (legend-set %))
                                    (nil? (:rx a))
                                    (number? (:width a))
                                    (number? (:height a))))
                            rects)
         ;; Visible tiles: tiles with positive width and height
         visible-tile-rects (filter #(let [a (second %)]
                                       (and (pos? (double (:width a)))
                                            (pos? (double (:height a)))))
                                    tile-rects)
         ;; Lines: filter out grid-colored polylines (theme-derived)
         data-polylines (remove #(= grid-color (get (second %) :stroke)) polylines)]
     {:width (:width attrs)
      :height (:height attrs)
      :panels (count panel-rects)
      :points (count data-rects)
      :lines (count data-polylines)
      :polygons (count polygons)
      :tiles (count tile-rects)
      :visible-tiles (count visible-tile-rects)
      :texts (mapv last texts)})))
