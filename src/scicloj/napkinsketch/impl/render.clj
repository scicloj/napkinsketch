(ns scicloj.napkinsketch.impl.render)

(defmulti render-figure
  "Render a sketch into a figure for the given format.
   Returns format-specific output (e.g., SVG hiccup, Plotly spec).
   Dispatches on the format keyword (:svg, :plotly, etc.)."
  (fn [sketch format opts] format))

(defmulti membrane->figure
  "Convert a membrane drawable tree into a figure for the given format.
   Dispatches on the format keyword (:svg, etc.)."
  (fn [membrane-tree format opts] format))
