(ns scicloj.napkinsketch.impl.plot
  (:require [scicloj.napkinsketch.impl.sketch :as sketch]
            [scicloj.napkinsketch.impl.render :as render]
            ;; Side-effect require: loads the :svg defmethod on render/render-figure.
            ;; Without this, (plot ...) would fail with "no method for :svg".
            ;; Additional renderers (e.g., :plotly) follow the same pattern —
            ;; require the namespace to register the defmethod.
            [scicloj.napkinsketch.render.svg]))

(defn plot
  "Render views as a figure (default: SVG hiccup wrapped with kind/hiccup).
   Internally: views → sketch → figure.
   Pass {:format :svg} (default) or other format keywords for alternative renderers."
  ([views] (plot views {}))
  ([views opts]
   (let [views (if (map? views) [views] views)
         fmt (or (:format opts) :svg)
         sk (sketch/resolve-sketch views opts)]
     (render/render-figure sk fmt opts))))
