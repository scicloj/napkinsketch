(ns scicloj.plotje.impl.render)

(defmulti plan->plot
  "Convert a plan into a figure for the given format.
   Returns format-specific output (e.g., SVG hiccup, Plotly spec).
   Dispatches on the format keyword (:svg, :plotly, etc.)."
  (fn [plan format opts] format))

(defmethod plan->plot :default [_ format _]
  (throw (ex-info (str "Unknown render format: " (pr-str format)
                       ". Supported formats: " (vec (sort (remove #{:default} (keys (methods plan->plot)))))
                       ". Renderers register themselves via defmethod plan->plot.")
                  {:format format :supported (vec (sort (keys (methods plan->plot))))})))

(defmulti membrane->plot
  "Convert a membrane drawable tree into a figure for the given format.
   Dispatches on the format keyword (:svg, etc.)."
  (fn [membrane-tree format opts] format))

(defmethod membrane->plot :default [_ format _]
  (throw (ex-info (str "Unknown render format: " (pr-str format)
                       ". Supported formats: " (vec (sort (remove #{:default} (keys (methods membrane->plot))))))
                  {:format format :supported (vec (sort (keys (methods membrane->plot))))})))
