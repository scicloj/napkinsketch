(ns scicloj.napkinsketch.impl.scale
  (:require [wadogo.scale :as ws]))

(defn numeric-domain? [dom]
  (and (sequential? dom) (seq dom) (number? (first dom))))

(defn categorical-domain? [dom]
  (and (sequential? dom) (seq dom) (not (number? (first dom)))))

(defn scale-kind [domain scale-spec]
  (cond
    (categorical-domain? domain) :categorical
    (= :log (:type scale-spec)) :log
    :else :linear))

(defmulti make-scale
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
        [a b] (if log? [(Math/log lo) (Math/log hi)] [lo hi])
        pad (* 0.05 (max 1e-6 (- b a)))
        from (if log? #(Math/exp %) identity)]
    [(from (- a pad)) (from (+ b pad))]))

(defn format-ticks
  "Format tick values: strip .0 when all ticks are whole numbers."
  [sx ticks]
  (let [labels (ws/format sx ticks)]
    (if (every? #(== (Math/floor %) %) ticks)
      (mapv #(str (long %)) ticks)
      labels)))

(defn tick-count
  "Suggested tick count based on available pixel range."
  [pixel-range spacing]
  (max 2 (int (/ pixel-range spacing))))
