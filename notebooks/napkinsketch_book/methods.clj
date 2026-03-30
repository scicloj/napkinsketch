;; # Methods

;; A **method** is the bundle that determines how data becomes a visual element.
;; It combines three concepts:
;;
;; - **mark** — what shape to draw (points, bars, lines, ...)
;; - **stat** — what computation to apply first (pass through, bin, count, regress, ...)
;; - **position** — how overlapping groups share space (identity, dodge, stack, fill)
;;
;; Layer functions (`sk/lay-point`, `sk/lay-histogram`, `sk/lay-bar`, `sk/lay-lm`, etc.)
;; each add a layer with the corresponding method. When no layer is added,
;; napkinsketch infers a method from the column types.
;;
;; All built-in methods are registered in a data registry. The tables below
;; are generated from that registry — they stay in sync with the code.

(ns napkinsketch-book.methods
  (:require
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Method registry — for inspecting method data
   [scicloj.napkinsketch.method :as method]
   ;; String utilities
   [clojure.string :as str]))

;; Two helpers used by several tables below.

(defn used-by
  "Sorted comma-separated method names whose `field` equals `value`."
  [field value]
  (->> (method/registered)
       (filter (fn [[_ m]] (= value (or (get m field) :identity))))
       (map (comp name key))
       sort
       (str/join ", ")))

(defn distinct-in-order
  "Distinct values of `field` across methods, in first-seen order."
  [field]
  (let [seen (volatile! #{})]
    (reduce (fn [acc k]
              (let [v (get (method/lookup k) field)]
                (if (@seen v) acc
                    (do (vswap! seen conj v) (conj acc v)))))
            [] method/method-order)))

;; ## Methods 
;;
;; Each row is a registered method showing its mark, stat, and position.

(kind/table
 {:column-names ["Method" "Mark" "Stat" "Position"]
  :row-maps
  (for [k method/method-order
        :let [m (method/lookup k)]]
    {"Method" (kind/code (pr-str k))
     "Mark" (kind/code (pr-str (:mark m)))
     "Stat" (kind/code (pr-str (:stat m)))
     "Position" (kind/code (pr-str (or (:position m) :identity)))})})

(kind/test-last
 [(fn [t]
    (= 25 (count (:row-maps t))))])

;; ## Marks
;;
;; A **mark** is the visual shape drawn for each data point or group.
;; Several methods may share the same mark — for instance, `histogram`
;; and `value-bar` both draw bars, and `lm` and `loess` both draw lines.

(kind/table
 {:column-names ["Mark" "Shape" "Used by"]
  :row-maps
  (for [mk (distinct-in-order :mark)]
    {"Mark" (kind/code (pr-str mk))
     "Shape" (sk/mark-doc mk)
     "Used by" (used-by :mark mk)})})

(kind/test-last
 [(fn [t]
    (= 17 (count (:row-maps t))))])

;; ## Stats
;;
;; A **stat** (statistical transform) processes raw data before
;; rendering. Each stat takes data-space inputs and produces
;; the geometry that its mark will draw.

(kind/table
 {:column-names ["Stat" "What it computes" "Used by"]
  :row-maps
  (for [st (distinct-in-order :stat)]
    {"Stat" (kind/code (pr-str st))
     "What it computes" (sk/stat-doc st)
     "Used by" (used-by :stat st)})})

(kind/test-last
 [(fn [t]
    (= 11 (count (:row-maps t))))])

;; ## Positions
;;
;; A **position** adjustment determines how groups share a categorical
;; axis slot. Position runs between stat computation and rendering.

(kind/table
 {:column-names ["Position" "What it does" "Used by"]
  :row-maps
  (for [pos [:identity :dodge :stack :fill]]
    {"Position" (kind/code (pr-str pos))
     "What it does" (sk/position-doc pos)
     "Used by" (used-by :position pos)})})

(kind/test-last
 [(fn [t]
    (= 4 (count (:row-maps t))))])

;; You can override the default position by passing `:position` in
;; the layer options.
;; When multiple layers share `:position :dodge`, they are coordinated
;; together — error bars automatically align with bars.

;; ## Layer Options
;;
;; The options map passed to `lay-` functions controls aesthetics,
;; statistical parameters, and spatial adjustments for that layer.
;;
;; ### Universal options
;;
;; Accepted by every method:

(kind/table
 {:column-names ["Option" "Description"]
  :row-maps
  (for [k method/universal-layer-options]
    {"Option" (kind/code (pr-str k))
     "Description" (get method/layer-option-docs k)})})

(kind/test-last
 [(fn [t] (= 6 (count (:row-maps t))))])

;; ### Method-specific options
;;
;; Some methods accept additional keys beyond the universal set.
;; Methods not listed here accept only the universal options above.

(kind/table
 {:column-names ["Method" "Additional options"]
  :row-maps
  (for [k method/method-order
        :let [m (method/lookup k)
              accepts (:accepts m)]
        :when (seq accepts)]
    {"Method" (kind/code (pr-str k))
     "Additional options" accepts})})

(kind/test-last
 [(fn [t] (pos? (count (:row-maps t))))])

;; ### All layer option keys

(kind/table
 {:column-names ["Option" "Description"]
  :row-maps
  (for [[k desc] (sort-by key method/layer-option-docs)]
    {"Option" (kind/code (pr-str k))
     "Description" desc})})

(kind/test-last
 [(fn [t] (= 17 (count (:row-maps t))))])
