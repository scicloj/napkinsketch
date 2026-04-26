(ns scicloj.plotje.impl.compositor
  "Composite-pose rendering. Each leaf produces its own membrane
   tree via plan->membrane, and the compositor tiles those trees by
   wrapping them in ui/translate. The resulting membrane tree is
   handed to membrane->plot, which dispatches on format (:svg,
   :bufimg, ...).

   Shared scales are reconciled before rendering by stamping a forced
   domain on matching leaves (impl.pose/inject-shared-scales).

   When the composite root carries a legend-producing mapping
   (:color/:size/:alpha), the compositor renders ONE legend at
   composite level rather than duplicating it per leaf -- each leaf
   gets :suppress-legend true and the shared legend is drawn into a
   reserved strip on the right side of the grid."
  (:require [membrane.ui :as ui]
            [scicloj.plotje.impl.defaults :as defaults]
            [scicloj.plotje.impl.pose :as pose]
            [scicloj.plotje.impl.plan :as plan]
            [scicloj.plotje.impl.resolve :as resolve]
            [scicloj.plotje.render.membrane :as membrane])
  (:import [scicloj.plotje.impl.resolve CompositePlan]))

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

(defn- outer-dimensions
  [pose]
  (let [opts (or (:opts pose) {})]
    [(long-or (:width opts) default-width)
     (long-or (:height opts) default-height)]))

(def ^:private title-band-h
  "Pixel height reserved at the top of a composite when :title is set."
  30)

(def ^:private composite-text-color
  "Text color for composite-level chrome (title + strip labels).
   Matches the leaf title color in render/membrane.clj so single
   plots and composite plots use the same shade -- earlier code
   used [0.1 0.1 0.1] which rendered as rgb(25,25,25), visibly
   darker than leaf titles' rgb(51,51,51)."
  [0.2 0.2 0.2 1.0])

(defn- title-drawable
  "Membrane drawable for a centered title band at the top of a
   composite of width w. Nil when no title."
  [title w]
  (when title
    (ui/translate (/ (double w) 2.0) 16
                  (ui/with-color composite-text-color
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
   comes from pose/matrix-axes. Places one centered label above
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
                     (ui/with-color composite-text-color
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
                       (ui/with-color composite-text-color
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
                     (ui/with-color composite-text-color
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
                       (ui/with-color composite-text-color
                         (assoc (ui/label label (ui/font nil grid-strip-font-size))
                                :text-anchor "middle"))))))))

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

(defn- resolve-composite-chrome
  "Compute the resolved leaves, layout map, and chrome geometry for a
   composite pose. Used by composite-pose->draft to produce the
   sub-drafts and the chrome-spec.

   Returns:
     {:width  outer width
      :height outer height
      :leaves resolved leaves with shared-scale + suppress-* applied
      :layout map of leaf-path -> [x y w h]
      :shared? true when the composite carries a legend-producing root mapping
      :chrome {:title :title-band-h :grid-rect :legend-w :strip-h :strip-w
               :col-labels :row-labels :n-cols :n-rows :matrix? :layout}}"
  [composite]
  (let [[w h] (outer-dimensions composite)
        opts (or (:opts composite) {})
        title (:title opts)
        top-pad (if title title-band-h 0)
        ;; Strip composite-chrome keys before resolve-tree so leaves
        ;; don't inherit them. The composite itself still renders
        ;; chrome via the title band above the leaf trees.
        stripped (composite-with-stripped-leaf-opts composite)
        injected (pose/inject-shared-scales stripped)
        ;; Detect whether the composite has a shared aesthetic that
        ;; will produce a legend at each leaf. If so, reserve a
        ;; strip on the right for one shared legend and shrink the
        ;; grid area accordingly.
        shared? (has-shared-aesthetic? composite)
        legend-w (if shared? shared-legend-strip-w 0)
        ;; Grid-composite (rows-of-cols SPLOM) stamps :grid-strip-labels
        ;; on its root. Matrix-direction composites have the labels
        ;; derived lazily from leaf positions via pose/matrix-axes.
        ;; Either way, we end up with :col-labels / :row-labels.
        ;; Reserve strip-h at the top for column labels and strip-w
        ;; at the left for row labels, and draw the strips outside
        ;; the cell rects so per-cell layout stays untouched.
        matrix-axes (when (= :matrix (:direction (:layout composite)))
                      (pose/matrix-axes composite))
        {:keys [col-labels row-labels]}
        (or (:grid-strip-labels composite)
            (when matrix-axes
              {:col-labels (:col-labels matrix-axes)
               :row-labels (:row-labels matrix-axes)}))
        col-labels (vec col-labels)
        row-labels (vec row-labels)
        strip-h (if (seq col-labels) grid-strip-h 0)
        strip-w (if (seq row-labels) (grid-strip-w row-labels) 0)
        n-cols (count col-labels)
        n-rows (count row-labels)
        grid-w (max 1 (- w legend-w strip-w))
        grid-rect [(double strip-w)
                   (double (+ top-pad strip-h))
                   (double grid-w)
                   (double (- h top-pad strip-h))]
        layout (pose/compute-layout injected grid-rect)
        leaves (pose/resolve-tree injected)
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
                                   (fn [o]
                                     (cond-> (or o {})
                                       suppress-x? (assoc :suppress-x-label true)
                                       suppress-y? (assoc :suppress-y-label true)))))
                         leaves))
                 leaves)
        chrome {:title title
                :title-band-h top-pad
                :grid-rect grid-rect
                :legend-w legend-w
                :strip-h strip-h
                :strip-w strip-w
                :col-labels col-labels
                :row-labels row-labels
                :n-cols n-cols
                :n-rows n-rows
                :matrix? (boolean matrix-axes)
                :layout layout}]
    {:width w
     :height h
     :leaves leaves
     :layout layout
     :shared? shared?
     :chrome chrome}))

(defn composite-pose->draft
  "Resolve a composite pose into a CompositeDraft. Each sub-draft entry
   carries the leaf's path, its rect inside the composite, the
   contextualized leaf draft (shared-scale domains injected,
   per-leaf opts adjusted), and the per-leaf opts (width/height
   merged from the rect).

   Per-leaf draft contextualization happens here, not at plan stage:
     - Shared-scale domains are stamped via inject-shared-scales /
       apply-shared-scale-domains, so per-leaf drafts carry forced
       :x-scale / :y-scale.
     - Matrix-layout strip labels suppress the per-leaf x-label /
       y-label (so axis labels appear only on the strip, not inside
       each cell).

   The chrome-spec captures the resolved chrome geometry for the
   composite as a whole; the layout map (path -> rect) is kept as a
   first-class field on the CompositeDraft so downstream stages do
   not need to recompute layout from the original pose tree."
  [composite]
  (let [{:keys [width height leaves layout shared? chrome]}
        (resolve-composite-chrome composite)
        sub-drafts (mapv (fn [leaf]
                           (let [rect (get layout (:path leaf))
                                 [_ _ rw rh] rect
                                 leaf' (apply-shared-scale-domains leaf)
                                 leaf-opts (assoc (or (:opts leaf') {})
                                                  :width (max 1 (long-or rw 1))
                                                  :height (max 1 (long-or rh 1)))
                                 draft (pose/leaf->draft leaf')]
                             {:path (:path leaf)
                              :rect rect
                              :draft draft
                              :opts leaf-opts}))
                         leaves)
        chrome-spec (assoc chrome :shared? shared?)]
    (resolve/->CompositeDraft width height sub-drafts chrome-spec layout)))

(defn composite-draft->plan
  "Convert a CompositeDraft into a CompositePlan. Per sub-draft, this
   calls draft->plan to produce a leaf plan and wraps it with its rect
   and path in :sub-plots. The shared-legend spec is computed once
   from a representative leaf draft (eliminating the rep-leaf-plan
   N+1 issue from the round-2 internals review)."
  [composite-draft]
  (let [{:keys [width height sub-drafts chrome-spec layout]} composite-draft
        sub-plots (mapv (fn [{:keys [path rect draft opts]}]
                          {:path path
                           :rect rect
                           :plan (plan/draft->plan draft opts)})
                        sub-drafts)
        shared-legend (when (:shared? chrome-spec)
                        (when-let [first-sub (first sub-drafts)]
                          (let [rep-opts (-> (:opts first-sub)
                                             (dissoc :suppress-legend)
                                             (assoc :width 600 :height 400))
                                rep-plan (plan/draft->plan (:draft first-sub) rep-opts)]
                            (select-keys rep-plan
                                         [:legend :size-legend :alpha-legend]))))
        chrome (-> chrome-spec
                   (dissoc :shared?)
                   (assoc :shared-legend shared-legend)
                   (assoc :layout layout))]
    (assoc (resolve/->CompositePlan width height sub-plots chrome)
           :composite? true
           ;; Mirror the LeafPlan keys read by the plan->plot
           ;; defmethods (render/svg.clj, render/bufimg.clj) -- these
           ;; are how render-opts flow into membrane->plot. Without
           ;; them, plan->plot on a CompositePlan would build a
           ;; figure with no title and zero outer dimensions.
           :total-width width
           :total-height height
           :title (:title chrome))))

(defn composite->plan
  "Compose pose -> draft -> plan for a composite pose. Returns a
   CompositePlan. The defrecord carries `:composite?` true for
   back-compat with code that treated the previous flagged-Plan shape
   as the composite indicator."
  [composite]
  (composite-draft->plan (composite-pose->draft composite)))

(defn pose->plan
  "Single entry point that resolves any pose into a plan -- LeafPlan
   for leaves, CompositePlan for composites. Public-API entry points
   (`pj/plan`, `pj/plot`, `pj/save`) call through here so the
   shape-dispatch happens in one place."
  [pose opts]
  (if (pose/composite? pose)
    (composite->plan pose)
    (plan/draft->plan (pose/leaf->draft pose) opts)))

;; ---- plan->membrane dispatch for composites ----
;;
;; The CompositePlan defmethod recurses: it calls plan->membrane on
;; each sub-plot's leaf plan (dispatching to the LeafPlan method),
;; translates each result into the composite coordinate space, and
;; layers chrome drawables (title, strip labels, shared legend) on
;; top.
(defmethod membrane/plan->membrane CompositePlan
  [composite-plan _opts]
  (let [{:keys [width sub-plots chrome]} composite-plan
        {:keys [title title-band-h grid-rect strip-h strip-w
                col-labels row-labels n-cols n-rows matrix?
                shared-legend layout]} chrome
        strips? (boolean (or (seq col-labels) (seq row-labels)))
        leaf-trees (mapv (fn [{:keys [plan rect]}]
                           (let [tooltip? (:tooltip (or (:opts plan) {}))
                                 tree (if tooltip?
                                        (membrane/plan->membrane plan {:tooltip true})
                                        (membrane/plan->membrane plan {}))
                                 [x y _ _] rect]
                             (ui/translate (double x) (double y) tree)))
                         sub-plots)
        col-strips (when (and strips? (seq col-labels))
                     (if matrix?
                       (matrix-col-strip-drawables col-labels grid-rect n-cols
                                                   (+ title-band-h 2))
                       (col-strip-drawables col-labels layout n-cols
                                            (+ title-band-h 2))))
        row-strips (when (and strips? (seq row-labels))
                     (if matrix?
                       (matrix-row-strip-drawables row-labels grid-rect n-rows
                                                   0 strip-w)
                       (row-strip-drawables row-labels layout n-rows
                                            0 strip-w)))
        [_ _ grid-w _] grid-rect
        legend-tree (when shared-legend
                      (shared-legend-drawables
                       shared-legend
                       (+ (double strip-w) (double grid-w) 20)
                       (double (+ title-band-h strip-h))))
        composed (cond-> leaf-trees
                   (seq col-strips) (into col-strips)
                   (seq row-strips) (into row-strips)
                   (seq legend-tree) (into legend-tree)
                   title             (conj (title-drawable title width)))]
    (vec composed)))

