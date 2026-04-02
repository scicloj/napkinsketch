(ns scicloj.napkinsketch.impl.render)

(defmulti abcdefgh->figure
  "Convert a abcdefgh into a figure for the given format.
   Returns format-specific output (e.g., SVG hiccup, Plotly spec).
   Dispatches on the format keyword (:svg, :plotly, etc.)."
  (fn [abcdefgh format opts] format))

(defmulti membrane->figure
  "Convert a membrane drawable tree into a figure for the given format.
   Dispatches on the format keyword (:svg, etc.)."
  (fn [membrane-tree format opts] format))
