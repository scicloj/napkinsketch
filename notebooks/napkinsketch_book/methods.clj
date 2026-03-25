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

;; ## Method table
;;
;; Each row is a registered method showing its mark, stat, and position.

(defn method-table []
  (kind/table
   {:column-names ["Method" "Mark" "Stat" "Position"]
    :row-maps
    (for [k method/method-order
          :let [m (method/lookup k)]]
      {"Method" (name k)
       "Mark" (name (:mark m))
       "Stat" (name (:stat m))
       "Position" (name (or (:position m) :identity))})}))

(method-table)

(kind/test-last
 [(fn [t]
    (= 25 (count (:row-maps t))))])

;; ## Mark table
;;
;; A **mark** is the visual shape drawn for each data point or group.
;; Several methods may share the same mark — for instance, `histogram`
;; and `value-bar` both draw bars, and `lm` and `loess` both draw lines.

(defn- used-by-mark
  "Return a sorted comma-separated string of method names using `mk`."
  [mk]
  (->> (method/registered)
       (filter (fn [[_ m]] (= mk (:mark m))))
       (map (comp name key))
       sort
       (str/join ", ")))

(defn- distinct-marks-in-order
  "Return distinct marks preserving first-seen order from method-order."
  []
  (let [seen (volatile! #{})]
    (reduce (fn [acc k]
              (let [mk (:mark (method/lookup k))]
                (if (@seen mk) acc
                    (do (vswap! seen conj mk) (conj acc mk)))))
            [] method/method-order)))

(defn mark-table []
  (kind/table
   {:column-names ["Mark" "Shape" "Used by"]
    :row-maps
    (for [mk (distinct-marks-in-order)]
      {"Mark" (name mk)
       "Shape" (sk/mark-doc mk)
       "Used by" (used-by-mark mk)})}))

(mark-table)

(kind/test-last
 [(fn [t]
    (= 17 (count (:row-maps t))))])

;; ## Stat table
;;
;; A **stat** (statistical transform) processes raw data before
;; rendering. Each stat takes data-space inputs and produces
;; the geometry that its mark will draw.

(defn- used-by-stat
  "Return a sorted comma-separated string of method names using `st`."
  [st]
  (->> (method/registered)
       (filter (fn [[_ m]] (= st (:stat m))))
       (map (comp name key))
       sort
       (str/join ", ")))

(defn- distinct-stats-in-order
  "Return distinct stats preserving first-seen order from method-order."
  []
  (let [seen (volatile! #{})]
    (reduce (fn [acc k]
              (let [st (:stat (method/lookup k))]
                (if (@seen st) acc
                    (do (vswap! seen conj st) (conj acc st)))))
            [] method/method-order)))

(defn stat-table []
  (kind/table
   {:column-names ["Stat" "What it computes" "Used by"]
    :row-maps
    (for [st (distinct-stats-in-order)]
      {"Stat" (name st)
       "What it computes" (sk/stat-doc st)
       "Used by" (used-by-stat st)})}))

(stat-table)

(kind/test-last
 [(fn [t]
    (= 11 (count (:row-maps t))))])

;; ## Position table
;;
;; A **position** adjustment determines how groups share a categorical
;; axis slot. Position runs between stat computation and rendering.

(def position-order
  "Canonical order for positions."
  [:identity :dodge :stack :fill])

(defn- used-by-position
  "Return method names that default to `pos`."
  [pos]
  (let [methods (method/registered)
        matches (filter (fn [[_ m]]
                          (= pos (or (:position m) :identity)))
                        methods)]
    (->> matches (map (comp name key)) sort (str/join ", "))))

(defn position-table []
  (kind/table
   {:column-names ["Position" "What it does" "Used by"]
    :row-maps
    (for [pos position-order]
      {"Position" (name pos)
       "What it does" (sk/position-doc pos)
       "Used by" (used-by-position pos)})}))

(position-table)

(kind/test-last
 [(fn [t]
    (= 4 (count (:row-maps t))))])

;; You can override the default position by passing `:position` in
;; the layer options.
;; When multiple layers share `:position :dodge`, they are coordinated
;; together — error bars automatically align with bars.
