(ns scicloj.napkinsketch.impl.sketch-schema
  "Malli schemas for the sketch data model."
  (:require [malli.core :as m]))

;; ---- Primitive Schemas ----

(def Color
  "RGBA color vector [r g b a], values 0-1."
  [:vector {:min 3 :max 4} number?])

(def ScaleSpec
  "Scale specification."
  [:map
   [:type [:enum :linear :log :categorical]]])

;; ---- Tick Info ----

(def TickInfo
  "Tick values and labels for an axis."
  [:map
   [:values [:vector any?]]
   [:labels [:vector string?]]
   [:categorical? boolean?]])

;; ---- Layer Groups (per mark type) ----

(def PointGroup
  [:map
   [:color Color]
   [:xs [:vector number?]]
   [:ys [:vector number?]]
   [:sizes {:optional true} [:vector number?]]
   [:alphas {:optional true} [:vector number?]]
   [:shapes {:optional true} [:vector any?]]
   [:row-indices {:optional true} [:vector int?]]])

(def BarBin
  [:map
   [:lo number?]
   [:hi number?]
   [:count number?]])

(def BarGroup
  [:map
   [:color Color]
   [:bars [:vector BarBin]]])

(def CountEntry
  [:map
   [:category any?]
   [:count number?]])

(def RectCountGroup
  [:map
   [:color Color]
   [:label string?]
   [:counts [:vector CountEntry]]])

(def RectValueGroup
  [:map
   [:color Color]
   [:xs [:vector any?]]
   [:ys [:vector number?]]])

(def LineSegmentGroup
  "Regression line segment."
  [:map
   [:color Color]
   [:x1 number?] [:y1 number?]
   [:x2 number?] [:y2 number?]])

(def PolylineGroup
  "Connected points."
  [:map
   [:color Color]
   [:xs [:vector number?]]
   [:ys [:vector number?]]])

;; ---- Layer ----

(def MarkStyle
  [:map
   [:opacity {:optional true} number?]
   [:radius {:optional true} number?]
   [:stroke-width {:optional true} number?]])

(def Layer
  "A rendered mark layer with data-space geometry."
  [:map
   [:mark [:enum :point :bar :line :rect]]
   [:style MarkStyle]
   [:groups [:vector [:or PointGroup BarGroup RectCountGroup RectValueGroup
                      LineSegmentGroup PolylineGroup]]]
   [:stat-origin {:optional true} [:enum :identity :bin :count :lm :loess]]
   [:position {:optional true} [:enum :dodge :stack]]
   [:categories {:optional true} [:vector any?]]])

;; ---- Panel ----

(def Panel
  "A single plot panel."
  [:map
   [:x-domain [:vector any?]]
   [:y-domain [:vector any?]]
   [:x-scale ScaleSpec]
   [:y-scale ScaleSpec]
   [:coord [:enum :cartesian :flip]]
   [:x-ticks TickInfo]
   [:y-ticks TickInfo]
   [:layers [:vector Layer]]])

;; ---- Legend ----

(def LegendEntry
  [:map
   [:label string?]
   [:color Color]])

(def Legend
  [:map
   [:title keyword?]
   [:entries [:vector LegendEntry]]])

;; ---- Layout ----

(def Layout
  [:map
   [:x-label-pad number?]
   [:y-label-pad number?]
   [:title-pad number?]
   [:legend-w number?]])

;; ---- Sketch (top-level) ----

(def Sketch
  "A fully resolved plot specification.
   Data-space geometry, no membrane types, no datasets."
  [:map
   [:width pos-int?]
   [:height pos-int?]
   [:margin number?]
   [:total-width number?]
   [:total-height number?]
   [:title {:optional true} [:maybe string?]]
   [:x-label {:optional true} [:maybe string?]]
   [:y-label {:optional true} [:maybe string?]]
   [:config map?]
   [:legend {:optional true} [:maybe Legend]]
   [:panels [:vector Panel]]
   [:layout Layout]])

;; ---- Validation Helpers ----

(defn valid?
  "Check if a sketch is valid."
  [sketch]
  (m/validate Sketch sketch))

(defn explain
  "Explain why a sketch is invalid."
  [sketch]
  (m/explain Sketch sketch))
