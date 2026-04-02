(ns scicloj.napkinsketch.impl.plot
  (:require [scicloj.napkinsketch.impl.sketch :as sketch]
            [scicloj.napkinsketch.impl.render :as render]
            ;; Side-effect require: loads the :svg defmethod on render/plan->figure.
            ;; Without this, (plot ...) would fail with "no method for :svg".
            ;; Additional renderers (e.g., :plotly) follow the same pattern —
            ;; require the namespace to register the defmethod.
            [scicloj.napkinsketch.render.svg]))

(defn plot
  "Render views as a figure (default: SVG hiccup wrapped with kind/hiccup).
   Internally: views → plan → figure.
   Pass {:format :svg} (default) or other format keywords for alternative renderers."
  ([views] (plot views {}))
  ([views opts]
   (let [views (if (map? views) [views] views)
         fmt (or (:format opts) :svg)
         pl (sketch/views->plan views opts)]
     (render/plan->figure pl fmt opts))))
