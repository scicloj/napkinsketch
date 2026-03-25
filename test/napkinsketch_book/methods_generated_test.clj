(ns
 napkinsketch-book.methods-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.string :as str]
  [clojure.test :refer [deftest is]]))


(def
 v3_l32
 (defn
  method-table
  []
  (kind/table
   {:column-names ["Method" "Mark" "Stat" "Position"],
    :row-maps
    (for
     [k method/method-order :let [m (method/lookup k)]]
     {"Method" (name k),
      "Mark" (name (:mark m)),
      "Stat" (name (:stat m)),
      "Position" (name (or (:position m) :identity))})})))


(def v4_l43 (method-table))


(deftest t5_l45 (is ((fn [t] (= 25 (count (:row-maps t)))) v4_l43)))


(def
 v7_l55
 (defn-
  used-by-mark
  "Return a sorted comma-separated string of method names using `mk`."
  [mk]
  (->>
   (method/registered)
   (filter (fn [[_ m]] (= mk (:mark m))))
   (map (comp name key))
   sort
   (str/join ", "))))


(def
 v8_l64
 (defn-
  distinct-marks-in-order
  "Return distinct marks preserving first-seen order from method-order."
  []
  (let
   [seen (volatile! #{})]
   (reduce
    (fn
     [acc k]
     (let
      [mk (:mark (method/lookup k))]
      (if (@seen mk) acc (do (vswap! seen conj mk) (conj acc mk)))))
    []
    method/method-order))))


(def
 v9_l74
 (defn
  mark-table
  []
  (kind/table
   {:column-names ["Mark" "Shape" "Used by"],
    :row-maps
    (for
     [mk (distinct-marks-in-order)]
     {"Mark" (name mk),
      "Shape" (sk/mark-doc mk),
      "Used by" (used-by-mark mk)})})))


(def v10_l83 (mark-table))


(deftest t11_l85 (is ((fn [t] (= 17 (count (:row-maps t)))) v10_l83)))


(def
 v13_l95
 (defn-
  used-by-stat
  "Return a sorted comma-separated string of method names using `st`."
  [st]
  (->>
   (method/registered)
   (filter (fn [[_ m]] (= st (:stat m))))
   (map (comp name key))
   sort
   (str/join ", "))))


(def
 v14_l104
 (defn-
  distinct-stats-in-order
  "Return distinct stats preserving first-seen order from method-order."
  []
  (let
   [seen (volatile! #{})]
   (reduce
    (fn
     [acc k]
     (let
      [st (:stat (method/lookup k))]
      (if (@seen st) acc (do (vswap! seen conj st) (conj acc st)))))
    []
    method/method-order))))


(def
 v15_l114
 (defn
  stat-table
  []
  (kind/table
   {:column-names ["Stat" "What it computes" "Used by"],
    :row-maps
    (for
     [st (distinct-stats-in-order)]
     {"Stat" (name st),
      "What it computes" (sk/stat-doc st),
      "Used by" (used-by-stat st)})})))


(def v16_l123 (stat-table))


(deftest t17_l125 (is ((fn [t] (= 11 (count (:row-maps t)))) v16_l123)))


(def
 v19_l134
 (def
  position-order
  "Canonical order for positions."
  [:identity :dodge :stack :fill]))


(def
 v20_l138
 (defn-
  used-by-position
  "Return method names that default to `pos`."
  [pos]
  (let
   [methods
    (method/registered)
    matches
    (filter (fn [[_ m]] (= pos (or (:position m) :identity))) methods)]
   (->> matches (map (comp name key)) sort (str/join ", ")))))


(def
 v21_l147
 (defn
  position-table
  []
  (kind/table
   {:column-names ["Position" "What it does" "Used by"],
    :row-maps
    (for
     [pos position-order]
     {"Position" (name pos),
      "What it does" (sk/position-doc pos),
      "Used by" (used-by-position pos)})})))


(def v22_l156 (position-table))


(deftest t23_l158 (is ((fn [t] (= 4 (count (:row-maps t)))) v22_l156)))
