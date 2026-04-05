;; # Minimal reproduction of test-generation issue
;;
;; This notebook isolates the Column/postwalk error
;; encountered when generating tests from Blueprint notebooks.

(ns scratch-clay-test-issue
  (:require [tablecloth.api :as tc]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.napkinsketch.api :as sk]))

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

;; ## Case 1: plain value — should work

42

(kind/test-last [(fn [v] (= 42 v))])

;; ## Case 2: kind/pprint of a map — should work

(kind/pprint {:a 1 :b 2})

(kind/test-last [(fn [m] (= 1 (:a m)))])

;; ## Case 3: auto-rendered Blueprint — does this error?

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## Case 4: kind/pprint of bp-summary (no dataset) — should work

(let [bp (-> iris (sk/xkcd7-lay-point :sepal_length :sepal_width))]
  (kind/pprint {:shared (:shared bp)
                :entries (mapv #(dissoc % :data) (:entries bp))
                :methods (:methods bp)}))

(kind/test-last [(fn [m] (= 1 (count (:entries m))))])

;; ## Case 5: old API Sketch — does this error too?

(-> iris
    (sk/lay-point :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])
