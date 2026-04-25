(ns scicloj.plotje.impl.compositor
  "Composite-frame rendering. Each leaf produces its own membrane
   tree via plan->membrane, and the compositor tiles those trees by
   wrapping them in ui/translate. The resulting membrane tree is
   handed to membrane->plot, which dispatches on format (:svg,
   :bufimg, ...).

   Shared scales are reconciled before rendering by stamping a forced
   domain on matching leaves (impl.frame/inject-shared-scales).

   When the composite root carries a legend-producing mapping
   (:color/:size/:alpha), the compositor renders ONE legend at
   composite level rather than duplicating it per leaf -- each leaf
   gets :suppress-legend true and the shared legend is drawn into a
   reserved strip on the right side of the grid."
  (:require [membrane.ui :as ui]
            [scicloj.plotje.impl.defaults :as defaults]
            [scicloj.plotje.impl.frame :as frame]
            [scicloj.plotje.impl.plan :as plan]
            [scicloj.plotje.impl.render :as render-impl]
            [scicloj.plotje.impl.resolve :as resolve]
            [scicloj.plotje.render.membrane :as membrane]))

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

(defn- has-shared-aesthetic?
  "True if the composite's root :mapping carries an aesthetic that
   produces a legend at render time (:color, :size, :alpha). These
   flow to every descendant leaf via resolve-tree, so rendering a
   per-leaf legend duplicates the same information N times."
  [composite]
  (let [m (:mapping composite)]
    (or (contains? m :color)
        (contains? m :size)
        (contains? m :alpha))))

(def ^:private shared-legend-strip-w
  "Pixel width reserved on the right side of the composite for the
   shared legend strip."
  120)

(def ^:private grid-strip-font-size
  "Font size for column/row strip labels on grid composites."
  11)

(def ^:private grid-strip-h
  "Pixel height reserved at the top of a grid composite for column
   strip labels."
  20)

(defn- grid-strip-w
  "Pixel width reserved at the left of a grid composite for row strip
   labels. Scales with the longest label so long column names fit."
  [row-labels]
  (let [max-chars (reduce max 0 (map count row-labels))]
    (+ 8 (* max-chars 7))))

(defn- matrix-col-strip-drawables
  "Like col-strip-drawables but for `:direction :matrix` composites,
   where leaves are at flat paths `[i]` and the (col, row) position
   comes from frame/matrix-axes. Places one centered label above
   each column at strip-top, computing the column center directly
   from the grid rect rather than looking it up via a SPLOM path."
  [col-labels [grid-x _ grid-w _] n-cols strip-top]
  (vec
   (for [ci (range n-cols)
         :let [label (nth col-labels ci nil)]
         :when label]
     (let [cw (/ (double grid-w) n-cols)
           cx (+ (double grid-x) (* ci cw) (/ cw 2.0))]
       (ui/translate cx (double strip-top)
                     (ui/with-color [0.1 0.1 0.1 1.0]
                       (assoc (ui/label label (ui/font nil grid-strip-font-size))
                              :text-anchor "middle")))))))

(defn- matrix-row-strip-drawables
  "Like row-strip-drawables but for `:direction :matrix` composites.
   Places one centered label to the left of each row, computing the
   row center directly from the grid rect."
  [row-labels [_ grid-y _ grid-h] n-rows strip-left strip-right]
  (let [label-x (+ (double strip-left)
                   (/ (- (double strip-right) (double strip-left)) 2.0))]
    (vec
     (for [ri (range n-rows)
           :let [label (nth row-labels ri nil)]
           :when label]
       (let [rh (/ (double grid-h) n-rows)
             cy (+ (double grid-y) (* ri rh) (/ rh 2.0))]
         (ui/translate label-x cy
                       (ui/with-color [0.1 0.1 0.1 1.0]
                         (assoc (ui/label label (ui/font nil grid-strip-font-size))
                                :text-anchor "middle"))))))))

(defn- col-strip-drawables
  "Build a vector of membrane drawables: one centered text per column
   label, positioned above its column's top-row rect."
  [col-labels layout n-cols strip-top]
  (vec
   (for [ci (range n-cols)
         :let [label (nth col-labels ci nil)
               rect  (get layout [0 ci])]
         :when (and label rect)]
     (let [[x _ w _] rect
           cx (+ (double x) (/ (double w) 2.0))]
       (ui/translate cx (double strip-top)
                     (ui/with-color [0.1 0.1 0.1 1.0]
                       (assoc (ui/label label (ui/font nil grid-strip-font-size))
                              :text-anchor "middle")))))))

(defn- row-strip-drawables
  "Build a vector of membrane drawables: one text per row label,
   positioned to the left of its row's leftmost rect."
  [row-labels layout n-rows strip-left strip-right]
  (let [label-x (+ (double strip-left)
                   (/ (- (double strip-right) (double strip-left)) 2.0))]
    (vec
     (for [ri (range n-rows)
           :let [label (nth row-labels ri nil)
                 rect  (get layout [ri 0])]
           :when (and label rect)]
       (let [[_ y _ h] rect
             cy (+ (double y) (/ (double h) 2.0))]
         (ui/translate label-x cy
                       (ui/with-color [0.1 0.1 0.1 1.0]
                         (assoc (ui/label label (ui/font nil grid-strip-font-size))
                                :text-anchor "middle"))))))))

(defn- rep-leaf-plan
  "Build a plan for the first resolved leaf with :suppress-legend
   removed, so the plan carries legend data. Returns the plan or
   nil if there are no leaves.

   Uses a comfortably large :width/:height so the plan's layout math
   always succeeds -- we only consume :legend/:size-legend/:alpha-legend
   from the returned plan, not its panel geometry."
  [leaves]
  (when-let [first-leaf (first leaves)]
    (let [leaf (apply-shared-scale-domains first-leaf)
          leaf-opts (-> (or (:opts leaf) {})
                        (dissoc :suppress-legend)
                        (assoc :width 600 :height 400))]
      (plan/draft->plan (frame/leaf->draft leaf) leaf-opts))))

(defn- shared-legend-drawables
  "Build membrane drawables for the shared legend positioned at
   (legend-x, legend-y-top). Takes the representative plan's legend
   spec and renders color / size / alpha legends stacked vertically.
   Returns a vector of drawables; empty when the rep plan has no
   legend data."
  [rep-plan legend-x legend-y-top]
  (let [{:keys [legend size-legend alpha-legend]} rep-plan
        cfg       (defaults/resolve-config {})
        ;; Stack sections with vertical spacing; each section returns
        ;; a seq of drawables anchored at (x, y-top + section offset)
        sections  (keep (fn [[drawer data]]
                          (when data (drawer data)))
                        [[(fn [l] (membrane/render-legend-from-plan
                                   l legend-x (+ legend-y-top 18) cfg))
                          legend]
                         [(fn [l] (membrane/render-size-legend
                                   l legend-x (+ legend-y-top 168)))
                          size-legend]
                         [(fn [l] (membrane/render-alpha-legend
                                   l legend-x (+ legend-y-top 288)))
                          alpha-legend]])]
    (vec (apply concat sections))))

(defn composite->plot
  "Render a composite frame by building a single membrane tree (one
   translated sub-tree per leaf, plus an optional title band and an
   optional shared legend) and dispatching to membrane->plot. Format
   defaults to :svg; :bufimg and other registered formats work the
   same as for single plots."
  ([composite] (composite->plot composite :svg))
  ([composite format]
   (let [[w h] (outer-dimensions composite)
         opts (or (:opts composite) {})
         title (:title opts)
         top-pad (if title title-band-h 0)
         ;; Strip composite-chrome keys before resolve-tree so leaves
         ;; don't inherit them. The composite itself still renders
         ;; chrome via the title band above the leaf trees.
         stripped (composite-with-stripped-leaf-opts composite)
         injected (frame/inject-shared-scales stripped)
         leaves (frame/resolve-tree injected)
         ;; Detect whether the composite has a shared aesthetic that
         ;; will produce a legend at each leaf. If so, reserve a
         ;; strip on the right for one shared legend and shrink the
         ;; grid area accordingly.
         shared? (has-shared-aesthetic? composite)
         legend-w (if shared? shared-legend-strip-w 0)
         ;; Grid-composite (rows-of-cols SPLOM) stamps :grid-strip-labels
         ;; on its root. Matrix-direction composites have the labels
         ;; derived lazily from leaf positions via frame/matrix-axes.
         ;; Either way, we end up with :col-labels / :row-labels.
         ;; Reserve strip-h at the top for column labels and strip-w
         ;; at the left for row labels, and draw the strips outside
         ;; the cell rects so per-cell layout stays untouched.
         matrix-axes (when (= :matrix (:direction (:layout composite)))
                       (frame/matrix-axes composite))
         {:keys [col-labels row-labels]} (or (:grid-strip-labels composite)
                                             (when matrix-axes
                                               {:col-labels (:col-labels matrix-axes)
                                                :row-labels (:row-labels matrix-axes)}))
         strips? (boolean (or (seq col-labels) (seq row-labels)))
         strip-h (if (seq col-labels) grid-strip-h 0)
         strip-w (if (seq row-labels) (grid-strip-w row-labels) 0)
         n-cols (count col-labels)
         n-rows (count row-labels)
         grid-w (max 1 (- w legend-w strip-w))
         grid-rect [(double strip-w)
                    (double (+ top-pad strip-h))
                    (double grid-w)
                    (double (- h top-pad strip-h))]
         layout (frame/compute-layout injected grid-rect)
         ;; In matrix layout, the strip labels at the top carry the
         ;; column's x-col name and the strip labels on the left carry
         ;; the row's y-col name. Suppress the per-leaf x-label /
         ;; y-label so they don't render redundantly inside each cell.
         ;; Same idea SPLOM cells use, applied uniformly here.
         leaves (if matrix-axes
                  (let [suppress-x? (seq (:col-labels matrix-axes))
                        suppress-y? (seq (:row-labels matrix-axes))]
                    (mapv (fn [leaf]
                            (update leaf :opts
                                    (fn [opts]
                                      (cond-> (or opts {})
                                        suppress-x? (assoc :suppress-x-label true)
                                        suppress-y? (assoc :suppress-y-label true)))))
                          leaves))
                  leaves)
         leaf-trees (mapv (fn [leaf]
                            (leaf->membrane leaf (get layout (:path leaf))))
                          leaves)
         col-strips (when (and strips? (seq col-labels))
                      (if matrix-axes
                        (matrix-col-strip-drawables col-labels grid-rect n-cols
                                                    (+ top-pad 2))
                        (col-strip-drawables col-labels layout n-cols
                                             (+ top-pad 2))))
         row-strips (when (and strips? (seq row-labels))
                      (if matrix-axes
                        (matrix-row-strip-drawables row-labels grid-rect n-rows
                                                    0 strip-w)
                        (row-strip-drawables row-labels layout n-rows
                                             0 strip-w)))
         ;; Build the shared legend from a representative leaf's plan.
         ;; Use the first leaf's rectangle as the sizing context.
         legend-tree (when shared?
                       (let [rep-plan (rep-leaf-plan leaves)]
                         (shared-legend-drawables
                          rep-plan (+ (double strip-w) grid-w 20)
                          (double (+ top-pad strip-h)))))
         composed (cond-> leaf-trees
                    (seq col-strips) (into col-strips)
                    (seq row-strips) (into row-strips)
                    (seq legend-tree) (into legend-tree)
                    title             (conj (title-drawable title w)))
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
