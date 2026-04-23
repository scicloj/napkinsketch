(ns scicloj.napkinsketch.impl.compositor
  "Composite-frame rendering. A composite has no single Sketch to feed
   the existing plan pipeline. Each leaf produces its own membrane tree
   via plan->membrane, and the compositor tiles those trees by wrapping
   them in ui/translate. The resulting membrane tree is then handed to
   membrane->plot, which dispatches on format (:svg, :bufimg, ...).

   Shared scales are reconciled before rendering by stamping a forced
   domain on matching leaves (impl.frame/inject-shared-scales)."
  (:require [membrane.ui :as ui]
            [scicloj.napkinsketch.impl.frame :as frame]
            [scicloj.napkinsketch.impl.plan :as plan]
            [scicloj.napkinsketch.impl.render :as render-impl]
            [scicloj.napkinsketch.impl.resolve :as resolve]
            [scicloj.napkinsketch.render.membrane :as membrane]))

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

(defn- leaf->plan
  [leaf [_ _ rw rh]]
  (let [leaf (apply-shared-scale-domains leaf)
        leaf-opts (assoc (or (:opts leaf) {})
                         :width (max 1 (long-or rw 1))
                         :height (max 1 (long-or rh 1)))]
    (plan/draft->plan (frame/leaf->draft leaf) leaf-opts)))

(defn- leaf->membrane
  "Build a translated membrane tree for a leaf at its pixel rectangle.
   Calls plan->membrane on the leaf's plan and wraps the result in a
   ui/translate positioned at (x, y) in the composite coordinate space."
  [leaf [x y _ _ :as rect]]
  (let [p (leaf->plan leaf rect)
        ;; plan->membrane accepts tooltip as a kwarg; forward it from
        ;; the plan's opts when present so interactive behaviour on
        ;; single plots carries over to leaves in a composite.
        tooltip? (:tooltip (or (:opts p) {}))
        tree (if tooltip?
               (membrane/plan->membrane p :tooltip true)
               (membrane/plan->membrane p))]
    (ui/translate (double x) (double y) tree)))

(defn- outer-dimensions
  [frame]
  (let [opts (or (:opts frame) {})]
    [(long-or (:width opts) default-width)
     (long-or (:height opts) default-height)]))

(def ^:private title-band-h
  "Pixel height reserved at the top of a composite when :title is set."
  30)

(defn- title-drawable
  "Membrane drawable for a centered title band at the top of a
   composite of width w. Nil when no title."
  [title w]
  (when title
    (ui/translate (/ (double w) 2.0) 16
                  (ui/with-color [0.1 0.1 0.1 1.0]
                    (assoc (ui/label title (ui/font nil 15))
                           :text-anchor "middle")))))

(def ^:private composite-chrome-opt-keys
  "Opts that live at the composite root -- the outer title band, etc.
   They must not inherit down to leaves (where resolve-tree's parent/
   child merge would otherwise stamp them into every cell's chrome)."
  [:title :subtitle :caption])

(defn- composite-with-stripped-leaf-opts
  "Return the composite with composite-level chrome opts removed from
   its root :opts, so resolve-tree doesn't propagate them into leaves."
  [composite]
  (update composite :opts #(apply dissoc % composite-chrome-opt-keys)))

(defn composite->plot
  "Render a composite frame by building a single membrane tree (one
   translated sub-tree per leaf, plus an optional title band) and
   dispatching to membrane->plot. Format defaults to :svg; :bufimg and
   other registered formats work the same as for single plots."
  ([composite] (composite->plot composite :svg))
  ([composite format]
   (let [[w h] (outer-dimensions composite)
         opts (or (:opts composite) {})
         title (:title opts)
         top-pad (if title title-band-h 0)
         layout-rect [0 (double top-pad) (double w) (double (- h top-pad))]
         ;; Strip composite-chrome keys before resolve-tree so leaves
         ;; don't inherit them. The composite itself still renders
         ;; chrome via the title band above the leaf trees.
         stripped (composite-with-stripped-leaf-opts composite)
         injected (frame/inject-shared-scales stripped)
         leaves (frame/resolve-tree injected)
         layout (frame/compute-layout injected layout-rect)
         leaf-trees (mapv (fn [leaf]
                            (leaf->membrane leaf (get layout (:path leaf))))
                          leaves)
         composed (cond-> leaf-trees
                    title (conj (title-drawable title w)))
         render-opts (assoc opts
                            :total-width w
                            :total-height h)]
     (render-impl/membrane->plot (vec composed) format render-opts))))

(defn composite->plan
  "Return a Plan record flagged :composite? with a :sub-plots vector
   of {:path :rect :plan} so downstream tooling can introspect without
   re-resolving."
  [composite]
  (let [[w h] (outer-dimensions composite)
        stripped (composite-with-stripped-leaf-opts composite)
        injected (frame/inject-shared-scales stripped)
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
