(ns scicloj.napkinsketch.impl.coord)

(defmulti make-coord
  "Build a coordinate function: (coord data-x data-y) -> [pixel-x pixel-y]."
  (fn [coord-type sx sy pw ph m] coord-type))

(defmethod make-coord :cartesian [_ sx sy pw ph m]
  (fn [dx dy] [(sx dx) (sy dy)]))

(defmethod make-coord :flip [_ sx sy pw ph m]
  (fn [dx dy] [(sx dy) (sy dx)]))
