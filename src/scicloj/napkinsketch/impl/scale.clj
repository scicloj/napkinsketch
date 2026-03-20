(ns scicloj.napkinsketch.impl.scale
  (:require [wadogo.scale :as ws]
            [scicloj.napkinsketch.impl.defaults :as defaults]))

(defn categorical-domain?
  "True if domain is a sequence of non-numeric values (categorical)."
  [dom]
  (and (sequential? dom) (seq dom) (not (number? (first dom)))))

(defn scale-kind
  "Determine the wadogo scale type (:categorical, :log, or :linear) from domain and spec."
  [domain scale-spec]
  (cond
    (categorical-domain? domain) :categorical
    (= :log (:type scale-spec)) :log
    :else :linear))

(defmulti make-scale
  "Create a wadogo scale mapping domain values to a pixel range."
  (fn [domain pixel-range scale-spec] (scale-kind domain scale-spec)))

(defmethod make-scale :categorical [domain pixel-range _]
  (ws/scale :bands {:domain domain :range pixel-range}))

(defmethod make-scale :linear [domain pixel-range _]
  (ws/scale :linear {:domain domain :range pixel-range}))

(defmethod make-scale :log [domain pixel-range _]
  (ws/scale :log {:domain domain :range pixel-range}))

(defn pad-domain
  "Add padding to a numeric domain."
  [[lo hi] scale-spec]
  (let [log? (= :log (:type scale-spec))
        padding (:domain-padding defaults/defaults)
        ;; Guard: log scale requires positive values; clamp to small positive
        [lo hi] (if log?
                  [(max 1e-10 (double lo)) (max 1e-10 (double hi))]
                  [lo hi])
        [a b] (if log? [(Math/log lo) (Math/log hi)] [lo hi])
        pad (* padding (max 1e-6 (- b a)))
        from (if log? #(Math/exp %) identity)]
    [(from (- a pad)) (from (+ b pad))]))

(defn format-ticks
  "Format tick values: strip .0 when all ticks are whole numbers."
  [sx ticks]
  (let [labels (ws/format sx ticks)]
    (if (every? #(== (Math/floor %) %) ticks)
      (mapv #(str (long %)) ticks)
      labels)))

(defn format-log-ticks
  "Format log scale tick values. Powers of 10 that are whole numbers
   are shown as integers (e.g., 1, 10, 100). Small decimals keep
   their decimal form (e.g., 0.001, 0.01)."
  [ticks]
  (mapv (fn [v]
          (if (and (>= v 1.0) (== v (Math/floor v)))
            (str (long v))
            (let [s (str v)]
              (if (.endsWith s ".0")
                (subs s 0 (- (count s) 2))
                s))))
        ticks))

(defn tick-count
  "Suggested tick count based on available pixel range."
  [pixel-range spacing]
  (max 2 (int (/ pixel-range spacing))))
