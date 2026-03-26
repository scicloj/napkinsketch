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
   [:xs [:sequential number?]]
   [:ys [:sequential number?]]
   [:colors {:optional true} [:sequential Color]]
   [:sizes {:optional true} [:sequential number?]]
   [:alphas {:optional true} [:sequential number?]]
   [:shapes {:optional true} [:vector any?]]
   [:row-indices {:optional true} [:sequential int?]]])

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
   [:xs [:sequential any?]]
   [:ys [:sequential number?]]])

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
   [:xs [:sequential number?]]
   [:ys [:sequential number?]]])

(def TextGroup
  "Text labels at data positions."
  [:map
   [:color Color]
   [:xs [:sequential number?]]
   [:ys [:sequential number?]]
   [:labels {:optional true} [:vector string?]]])

(def ErrorbarGroup
  "Error bars at data positions."
  [:map
   [:color Color]
   [:xs [:sequential number?]]
   [:ys [:sequential number?]]
   [:ymins [:sequential number?]]
   [:ymaxs [:sequential number?]]])

(def BoxplotBox
  "A single boxplot box with five-number summary."
  [:map
   [:category any?]
   [:color Color]
   [:color-category {:optional true} any?]
   [:median number?]
   [:q1 number?]
   [:q3 number?]
   [:whisker-lo number?]
   [:whisker-hi number?]
   [:outliers {:optional true} [:sequential number?]]])

(def ViolinEntry
  "A single violin curve with category and mirrored density."
  [:map
   [:category any?]
   [:color Color]
   [:color-category {:optional true} any?]
   [:ys [:sequential number?]]
   [:densities [:sequential number?]]])

(def TileEntry
  "A single tile (heatmap cell) with bounds and fill color."
  [:map
   [:x-lo number?]
   [:x-hi number?]
   [:y-lo number?]
   [:y-hi number?]
   [:color Color]])

;; ---- Layer ----

(def MarkStyle
  [:map
   [:opacity {:optional true} number?]
   [:radius {:optional true} number?]
   [:stroke-width {:optional true} number?]
   [:font-size {:optional true} number?]
   [:box-width {:optional true} number?]
   [:cap-width {:optional true} number?]
   [:length {:optional true} number?]
   [:jitter {:optional true} [:or boolean? number?]]])

(def Annotation
  "A reference line or band annotation."
  [:map
   [:mark [:enum :rule-v :rule-h :band-v :band-h]]
   [:intercept {:optional true} number?]
   [:lo {:optional true} number?]
   [:hi {:optional true} number?]])

(def Layer
  "A rendered mark layer with data-space geometry."
  [:map
   [:mark [:enum :point :bar :line :step :rect :text :label :area :errorbar :lollipop :boxplot :violin :tile :ridgeline :rug :pointrange :contour]]
   [:style MarkStyle]
   [:groups {:optional true} [:vector [:or PointGroup BarGroup RectCountGroup RectValueGroup
                                       LineSegmentGroup PolylineGroup TextGroup ErrorbarGroup]]]
   [:boxes {:optional true} [:vector BoxplotBox]]
   [:violins {:optional true} [:vector ViolinEntry]]
   [:tiles {:optional true} [:vector TileEntry]]
   [:ridges {:optional true} [:vector [:map [:category any?] [:color Color] [:ys [:sequential number?]] [:densities [:sequential number?]]]]]
   [:color-categories {:optional true} [:maybe [:vector any?]]]
   [:position {:optional true} [:enum :dodge :stack :fill]]
   [:categories {:optional true} [:vector any?]]
   [:side {:optional true} [:enum :x :y :both]]])

;; ---- Panel ----

(def Panel
  "A single plot panel."
  [:map
   [:x-domain [:vector any?]]
   [:y-domain [:vector any?]]
   [:x-scale ScaleSpec]
   [:y-scale ScaleSpec]
   [:coord [:enum :cartesian :flip :polar :fixed]]
   [:x-ticks TickInfo]
   [:y-ticks TickInfo]
   [:layers [:vector Layer]]
   [:row int?]
   [:col int?]
   [:annotations {:optional true} [:vector Annotation]]
   [:row-label {:optional true} [:maybe string?]]
   [:col-label {:optional true} [:maybe string?]]])

;; ---- Legend ----

(def LegendEntry
  [:map
   [:label string?]
   [:color Color]])

(def GradientStop
  [:map
   [:t number?]
   [:color Color]])

(def Legend
  [:or
   ;; Categorical legend
   [:map
    [:title keyword?]
    [:entries [:vector LegendEntry]]]
   ;; Continuous gradient legend
   [:map
    [:title keyword?]
    [:type [:= :continuous]]
    [:min number?]
    [:max number?]
    [:color-scale {:optional true} [:maybe keyword?]]
    [:stops [:vector GradientStop]]]])

(def SizeLegendEntry
  [:map
   [:value number?]
   [:radius number?]])

(def SizeLegend
  [:map
   [:title keyword?]
   [:type [:= :size]]
   [:min number?]
   [:max number?]
   [:entries [:vector SizeLegendEntry]]])

(def AlphaLegendEntry
  [:map
   [:value number?]
   [:alpha number?]])

(def AlphaLegend
  [:map
   [:title keyword?]
   [:type [:= :alpha]]
   [:min number?]
   [:max number?]
   [:entries [:vector AlphaLegendEntry]]])

;; ---- Layout ----

(def Layout
  [:map
   [:x-label-pad number?]
   [:y-label-pad number?]
   [:title-pad number?]
   [:subtitle-pad number?]
   [:caption-pad number?]
   [:legend-w number?]
   [:legend-h number?]
   [:strip-h number?]
   [:strip-w number?]])

;; ---- Sketch (top-level) ----

(def Sketch
  "A fully resolved plot specification.
   Data-space geometry, no membrane types, no datasets.
   Numeric arrays (xs, ys, etc.) may be dtype-next buffers."
  [:map
   [:width pos-int?]
   [:height pos-int?]
   [:margin number?]
   [:total-width number?]
   [:total-height number?]
   [:panel-width number?]
   [:panel-height number?]
   [:grid [:map [:rows pos-int?] [:cols pos-int?]]]
   [:layout-type [:enum :single :facet-grid :multi-variable]]
   [:title {:optional true} [:maybe string?]]
   [:subtitle {:optional true} [:maybe string?]]
   [:caption {:optional true} [:maybe string?]]
   [:x-label {:optional true} [:maybe string?]]
   [:y-label {:optional true} [:maybe string?]]
   [:legend {:optional true} [:maybe Legend]]
   [:size-legend {:optional true} [:maybe SizeLegend]]
   [:alpha-legend {:optional true} [:maybe AlphaLegend]]
   [:legend-position [:enum :right :bottom :top :none]]
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
