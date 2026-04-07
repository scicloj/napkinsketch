(ns
 napkinsketch-book.methods-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.string :as str]
  [clojure.test :refer [deftest is]]))


(def
 v3_l30
 (defn
  used-by
  "Sorted comma-separated method names whose `field` equals `value`."
  [field value]
  (->>
   (method/registered)
   (filter (fn [[_ m]] (= value (or (get m field) :identity))))
   (map (comp name key))
   sort
   (str/join ", "))))


(def
 v4_l39
 (defn
  distinct-in-order
  "Distinct values of `field` across methods, in first-seen order."
  [field]
  (let
   [seen (volatile! #{})]
   (reduce
    (fn
     [acc k]
     (let
      [v (get (method/lookup k) field)]
      (if (@seen v) acc (do (vswap! seen conj v) (conj acc v)))))
    []
    method/method-order))))


(def
 v6_l53
 (kind/table
  {:column-names ["Method" "Mark" "Stat" "Position"],
   :row-maps
   (for
    [k method/method-order :let [m (method/lookup k)]]
    {"Method" (kind/code (pr-str k)),
     "Mark" (kind/code (pr-str (:mark m))),
     "Stat" (kind/code (pr-str (:stat m))),
     "Position" (kind/code (pr-str (or (:position m) :identity)))})}))


(deftest t7_l63 (is ((fn [t] (= 25 (count (:row-maps t)))) v6_l53)))


(def
 v9_l73
 (kind/table
  {:column-names ["Mark" "Shape" "Used by"],
   :row-maps
   (for
    [mk (distinct-in-order :mark)]
    {"Mark" (kind/code (pr-str mk)),
     "Shape" (sk/mark-doc mk),
     "Used by" (used-by :mark mk)})}))


(deftest t10_l81 (is ((fn [t] (= 17 (count (:row-maps t)))) v9_l73)))


(def
 v12_l91
 (kind/table
  {:column-names ["Stat" "What it computes" "Used by"],
   :row-maps
   (for
    [st (distinct-in-order :stat)]
    {"Stat" (kind/code (pr-str st)),
     "What it computes" (sk/stat-doc st),
     "Used by" (used-by :stat st)})}))


(deftest t13_l99 (is ((fn [t] (= 11 (count (:row-maps t)))) v12_l91)))


(def
 v15_l108
 (kind/table
  {:column-names ["Position" "What it does" "Used by"],
   :row-maps
   (for
    [pos [:identity :dodge :stack :fill]]
    {"Position" (kind/code (pr-str pos)),
     "What it does" (sk/position-doc pos),
     "Used by" (used-by :position pos)})}))


(deftest t16_l116 (is ((fn [t] (pos? (count (:row-maps t)))) v15_l108)))


(def
 v18_l134
 (kind/table
  {:column-names ["Option" "Description"],
   :row-maps
   (for
    [k method/universal-layer-options]
    {"Option" (kind/code (pr-str k)),
     "Description" (get method/layer-option-docs k)})}))


(deftest t19_l141 (is ((fn [t] (pos? (count (:row-maps t)))) v18_l134)))


(def
 v21_l149
 (kind/table
  {:column-names ["Method" "Additional options"],
   :row-maps
   (for
    [k
     method/method-order
     :let
     [m (method/lookup k) accepts (:accepts m)]
     :when
     (seq accepts)]
    {"Method" (kind/code (pr-str k)), "Additional options" accepts})}))


(deftest t22_l159 (is ((fn [t] (pos? (count (:row-maps t)))) v21_l149)))


(def
 v24_l164
 (kind/table
  {:column-names ["Option" "Description"],
   :row-maps
   (for
    [[k desc] (sort-by key method/layer-option-docs)]
    {"Option" (kind/code (pr-str k)), "Description" desc})}))


(deftest t25_l171 (is ((fn [t] (pos? (count (:row-maps t)))) v24_l164)))
