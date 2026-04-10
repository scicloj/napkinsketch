(ns scicloj.napkinsketch.impl.render)

(defmulti plan->figure
  "Convert a plan into a figure for the given format.
   Returns format-specific output (e.g., SVG hiccup, Plotly spec).
   Dispatches on the format keyword (:svg, :plotly, etc.)."
  (fn [plan format opts] format))

(defmethod plan->figure :default [_ format _]
  (throw (ex-info (str "Unknown render format: " (pr-str format)
                       ". Supported formats: " (vec (sort (remove #{:default} (keys (methods plan->figure)))))
                       ". Renderers register themselves via defmethod plan->figure.")
                  {:format format :supported (vec (sort (keys (methods plan->figure))))})))

(defmulti membrane->figure
  "Convert a membrane drawable tree into a figure for the given format.
   Dispatches on the format keyword (:svg, etc.)."
  (fn [membrane-tree format opts] format))

(defmethod membrane->figure :default [_ format _]
  (throw (ex-info (str "Unknown membrane render format: " (pr-str format)
                       ". Supported formats: " (vec (sort (remove #{:default} (keys (methods membrane->figure))))))
                  {:format format :supported (vec (sort (keys (methods membrane->figure))))})))
