(ns scicloj.napkinsketch.impl.compositor
  "Composite-frame rendering. Why a separate module: a composite has
   no single Sketch to feed the existing plan pipeline. Each leaf is
   rendered on its own via the existing pipeline and the results are
   tiled. Shared scales are reconciled before rendering by stamping a
   forced domain on matching leaves."
  (:require [scicloj.napkinsketch.impl.frame :as frame]
            [scicloj.napkinsketch.impl.sketch :as sketch]
            [scicloj.napkinsketch.impl.plan :as plan]
            [scicloj.napkinsketch.impl.render :as render-impl]
            [scicloj.napkinsketch.impl.resolve :as resolve]
            [scicloj.napkinsketch.render.svg :as svg]))

(def ^:private default-width 600)
(def ^:private default-height 400)

(defn- long-or [x default]
  (long (Math/round (double (or x default)))))

(defn- domain->scale-entry
  [existing-scale domain]
  (let [base (or existing-scale {:type :linear})]
    (assoc base :domain domain)))

(defn- apply-shared-scale-domains
  "If :x-scale-domain / :y-scale-domain were stamped on the leaf by
   inject-shared-scales, translate them into the :x-scale / :y-scale
   map shape the downstream plan pipeline consumes."
  [leaf]
  (let [opts (or (:opts leaf) {})
        x-dom (:x-scale-domain opts)
        y-dom (:y-scale-domain opts)]
    (if (or x-dom y-dom)
      (assoc leaf :opts
             (cond-> (dissoc opts :x-scale-domain :y-scale-domain)
               x-dom (update :x-scale domain->scale-entry x-dom)
               y-dom (update :y-scale domain->scale-entry y-dom)))
      leaf)))

(defn leaf->sketch
  "Build a Sketch from a resolved leaf (output of
   impl.frame/resolve-tree). Shared-scale domains on :opts are
   translated before delegating to sketch/leaf-frame->sketch."
  [leaf]
  (sketch/leaf-frame->sketch (apply-shared-scale-domains leaf)))

(defn- leaf->plan
  [leaf [_ _ rw rh]]
  (let [sk (leaf->sketch leaf)
        leaf-opts (assoc (or (:opts sk) {})
                         :width (max 1 (long-or rw 1))
                         :height (max 1 (long-or rh 1)))]
    (plan/draft->plan (sketch/sketch->draft sk) leaf-opts)))

(defn- leaf->svg
  [leaf [x y w h :as rect]]
  (let [p (leaf->plan leaf rect)
        leaf-opts {:width (max 1 (long-or w 1)) :height (max 1 (long-or h 1))}
        ;; plan->plot returns [:svg attrs body] via render.svg/wrap-svg,
        ;; so body sits at index 2.
        [_ _ inner] (render-impl/plan->plot p :svg leaf-opts)]
    [:g {:transform (format "translate(%s,%s)" (double x) (double y))}
     inner]))

(defn- outer-dimensions
  [frame]
  (let [opts (or (:opts frame) {})]
    [(long-or (:width opts) default-width)
     (long-or (:height opts) default-height)]))

(defn composite->plot
  "Render a composite frame to SVG hiccup."
  [composite]
  (let [[w h] (outer-dimensions composite)
        injected (frame/inject-shared-scales composite)
        leaves (frame/resolve-tree injected)
        layout (frame/compute-layout injected [0 0 w h])
        leaf-gs (mapv (fn [leaf]
                        (leaf->svg leaf (get layout (:path leaf))))
                      leaves)]
    (svg/wrap-svg w h (into [:g] leaf-gs))))

(defn composite->plan
  "Return a Plan record flagged :composite? with a :sub-plots vector
   of {:path :rect :plan} so downstream tooling can introspect without
   re-resolving."
  [composite]
  (let [[w h] (outer-dimensions composite)
        injected (frame/inject-shared-scales composite)
        leaves (frame/resolve-tree injected)
        layout (frame/compute-layout injected [0 0 w h])
        sub-plots (mapv (fn [leaf]
                          (let [rect (get layout (:path leaf))]
                            {:path (:path leaf)
                             :rect rect
                             :plan (leaf->plan leaf rect)}))
                        leaves)]
    (assoc (resolve/->Plan [] w h)
           :composite? true
           :sub-plots sub-plots)))
