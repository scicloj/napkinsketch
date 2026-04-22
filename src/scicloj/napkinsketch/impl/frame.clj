(ns scicloj.napkinsketch.impl.frame
  "Frame substrate -- the recursive plain-map type that replaces
   sketch + view in the pre-alpha refactor. This namespace holds the
   pure tree operations (resolve, layout, shared-scale injection) that
   downstream phases build on.

   Shape of a frame:
     {:data         ?  dataset (inherited from ancestor if absent)
      :mapping      ?  aesthetic mappings (merges with ancestors)
      :layers       ?  layers at this level (accumulate into leaves)
      :frames       ?  sub-frames; absence = leaf
      :layout       ?  {:direction :horizontal|:vertical
                        :weights   [pos-num ...]}
      :opts         ?  plot options (inheritable)
      :share-scales ?  #{:x :y}  for composites}

   This namespace is intentionally free of rendering concerns. Phase 4
   wires a compositor on top of it."
  (:require [tablecloth.api :as tc]
            [tech.v3.datatype.functional :as dfn]))

;; ---- Structural predicates ----

(defn frame?
  "True if x looks frame-shaped: a map carrying at least one of
   :layers or :frames. Permissive by design -- schema-level validation
   lives in impl.frame-schema."
  [x]
  (and (map? x)
       (or (contains? x :layers)
           (contains? x :frames))))

(defn leaf?
  "A leaf frame has no sub-frames. (An empty :frames vector also
   counts as leaf because it has nothing to tile.)"
  [f]
  (not (seq (:frames f))))

(defn composite?
  "A composite frame has at least one sub-frame."
  [f]
  (not (leaf? f)))

;; ---- Tree resolver ----

(defn resolve-tree
  "Walk the frame tree top-down, merging parent context into each
   descendant. Returns a vector of resolved leaves; each leaf carries
   merged :data, :mapping, :layers, :opts, and a :path vector of
   indices describing its position in the tree.

   Context inheritance rules:
   - :data     -- nearest ancestor wins (child overrides parent).
   - :mapping  -- merged, with child keys overriding parent keys.
   - :layers   -- concatenated (ancestor layers distribute down, then
                  the leaf's own layers append).
   - :opts     -- merged (child overrides on key collision).

   Extra keys on a leaf (anything not in
   #{:data :mapping :layers :frames :layout :opts :share-scales})
   pass through to the resolved leaf so callers can attach metadata
   like :path-labels from facet-style generators."
  ([frame]
   (resolve-tree frame {} []))
  ([frame parent-ctx path]
   (let [ctx {:data    (or (:data frame) (:data parent-ctx))
              :mapping (merge {} (:mapping parent-ctx) (:mapping frame))
              :layers  (into (vec (:layers parent-ctx))
                             (:layers frame))
              :opts    (merge {} (:opts parent-ctx) (:opts frame))}]
     (if (leaf? frame)
       (let [structural-keys #{:data :mapping :layers :frames :layout
                               :opts :share-scales}
             extras (into {} (remove (fn [[k _]] (structural-keys k))
                                     frame))]
         [(merge extras (assoc ctx :path path))])
       (into []
             (mapcat (fn [[i child]]
                       (resolve-tree child ctx (conj path i))))
             (map vector (range) (:frames frame)))))))

;; ---- Layout computer ----

(defn- normalize-weights
  "Convert weights to fractions summing to 1."
  [weights]
  (let [total (double (reduce + weights))]
    (when (or (zero? total) (neg? total))
      (throw (ex-info "Layout :weights must sum to a positive number."
                      {:weights (vec weights)})))
    (mapv #(/ (double %) total) weights)))

(defn compute-layout
  "Walk the frame tree and assign a pixel rectangle to each leaf.
   Returns a map of path -> [x y w h].

   Composite :layout is {:direction :horizontal|:vertical
                         :weights   [pos-num ...]}. Defaults:
     :direction :horizontal
     :weights   (repeat n 1)  (equal share)

   Rectangle arithmetic is in doubles; callers that need integer
   pixels should coerce at the render boundary (see sk/plot's
   width/height coercion)."
  ([frame rect]
   (compute-layout frame rect []))
  ([frame [x y w h] path]
   (if (leaf? frame)
     {path [(double x) (double y) (double w) (double h)]}
     (let [{:keys [direction weights] :or {direction :horizontal}}
           (:layout frame)
           children (:frames frame)
           n (count children)
           ws (normalize-weights (or weights (repeat n 1)))
           horizontal? (= direction :horizontal)]
       (loop [i 0
              cursor (double (if horizontal? x y))
              acc {}]
         (if (>= i n)
           acc
           (let [child (nth children i)
                 frac (nth ws i)
                 child-span (if horizontal? (* (double w) frac) (* (double h) frac))
                 child-rect (if horizontal?
                              [cursor y child-span h]
                              [x cursor w child-span])
                 child-path (conj path i)
                 sub (compute-layout child child-rect child-path)]
             (recur (inc i)
                    (+ cursor child-span)
                    (merge acc sub)))))))))

;; ---- Tree utilities ----

(defn last-leaf-path
  "Return the path vector of the last leaf visited in left-to-right
   depth-first order. Nil if the frame is itself a leaf with no path
   context (the caller is the root leaf)."
  [frame]
  (if (leaf? frame)
    []
    (let [n (count (:frames frame))]
      (when (pos? n)
        (into [(dec n)] (last-leaf-path (peek (:frames frame))))))))

(defn leaf-at
  "Fetch the leaf at `path` in `frame`. Returns nil if the path does
   not land on a leaf."
  [frame path]
  (let [node (reduce (fn [f i] (get-in f [:frames i])) frame path)]
    (when (leaf? node) node)))

(defn path->update-in-path
  "Translate a leaf path like [0 1] into the get-in / update-in navigation
   [:frames 0 :frames 1]. A root path [] translates to []."
  [path]
  (into [] (mapcat (fn [i] [:frames i])) path))

(defn- canonicalize-col
  "Canonicalize a column ref to a string key for matching. A keyword
   and a string with the same name are treated as the same column
   (matches api.clj's col-key semantics)."
  [col]
  (cond
    (nil? col) nil
    (keyword? col) (name col)
    :else (str col)))

(defn last-matching-leaf-path
  "Walk `frame` in left-to-right DFS order. Return the :path of the
   last leaf whose effective :x and :y (after ancestor-merge of
   :mapping) match `position-mapping`. Matching is keyword/string
   tolerant. Returns nil if no leaf matches.

   `position-mapping` may carry either or both of :x and :y; a nil
   value matches a leaf whose effective mapping has no entry for that
   axis. This mirrors the sketch-world rule (api.clj's add-view-layer):
   matching is against resolved positional mappings only, and a bare
   view (no :x/:y) matches a bare position mapping."
  [frame position-mapping]
  (let [px (canonicalize-col (:x position-mapping))
        py (canonicalize-col (:y position-mapping))]
    (->> (resolve-tree frame)
         (keep (fn [leaf]
                 (when (and (= (canonicalize-col (get-in leaf [:mapping :x])) px)
                            (= (canonicalize-col (get-in leaf [:mapping :y])) py))
                   (:path leaf))))
         last)))

;; ---- Shared-scale injection ----
;;
;; The load-bearing primitive surfaced by the nested-frames PoC.
;;
;; When a composite declares :share-scales #{:x} (or :y, or both), we
;; compute a shared domain across descendants and stamp it onto each
;; leaf's :opts as :x-scale-domain / :y-scale-domain. The compositor
;; later reads these keys and forces the matching scale.
;;
;; Column bucketing: sharing is scoped to leaves whose effective
;; column for the axis matches. Leaves with different columns get
;; their own bucket, independent of the composite's share-scales set.
;; This gives the right behavior for SPLOM (columns align across rows,
;; rows align across columns), marginal plots (x shares between
;; scatter and top density; right density uses its own column), and
;; mosaic-of-scatters.

(defn- effective-axis-col
  "The column ref this resolved leaf uses for `axis`. Layer-level
   mappings take precedence over the leaf's own :mapping. If layers
   disagree, the first non-nil wins -- a PoC simplification we keep
   for alpha; Phase 4 may refine per-layer resolution."
  [leaf axis]
  (or (some (fn [layer] (get-in layer [:mapping axis]))
            (:layers leaf))
      (get-in leaf [:mapping axis])))

(defn- col-values
  "Non-nil values for a column ref from a dataset, tolerant of
   keyword/string name mismatches (tablecloth sometimes stores names
   as strings)."
  [ds col-ref]
  (when (and ds col-ref)
    (let [col-names (set (tc/column-names ds))
          col-name (cond
                     (contains? col-names col-ref) col-ref
                     (and (keyword? col-ref)
                          (contains? col-names (name col-ref)))
                     (name col-ref)
                     (and (string? col-ref)
                          (contains? col-names (keyword col-ref)))
                     (keyword col-ref))]
      (when col-name
        (remove nil? (ds col-name))))))

(defn- numeric-domain
  "[lo hi] across the numeric values in a sequence, or nil if none."
  [vals]
  (let [nums (filter number? vals)]
    (when (seq nums)
      [(dfn/reduce-min nums) (dfn/reduce-max nums)])))

(defn- union-domain
  "Merge two [lo hi] pairs into a single enclosing [lo hi]. Nil-safe."
  [a b]
  (cond
    (nil? a) b
    (nil? b) a
    :else [(min (first a) (first b))
           (max (second a) (second b))]))

(defn inject-shared-scales
  "Walk a frame tree. For each composite with :share-scales, compute a
   union domain per (axis, effective-column) bucket across descendant
   leaves, and stamp those domains onto matching leaves' :opts as
   :x-scale-domain / :y-scale-domain. Returns a new tree.

   `inherited-domains` carries `{axis {col-ref [lo hi]}}` down the
   tree. `inherited-mapping` carries the ancestor-merged mapping so a
   leaf can resolve its effective axis column from (inherited + own +
   layer) when deciding which bucket to claim."
  ([frame]
   (inject-shared-scales frame {} {}))
  ([frame inherited-domains inherited-mapping]
   (let [my-mapping  (merge inherited-mapping (:mapping frame))
         my-shares   (:share-scales frame)
         new-domains (when (and my-shares (seq (:frames frame)))
                       (let [subtree (resolve-tree frame
                                                   {:mapping inherited-mapping}
                                                   [])]
                         (into {}
                               (keep
                                (fn [axis]
                                  (let [by-col (group-by #(effective-axis-col % axis)
                                                         subtree)
                                        col->dom (into {}
                                                       (keep
                                                        (fn [[col leaves]]
                                                          (when col
                                                            (when-let [d (numeric-domain
                                                                          (mapcat #(col-values (:data %)
                                                                                               (effective-axis-col % axis))
                                                                                  leaves))]
                                                              [col d])))
                                                        by-col))]
                                    (when (seq col->dom)
                                      [axis col->dom])))
                                my-shares))))
         child-domains (merge-with (partial merge-with union-domain)
                                   inherited-domains
                                   new-domains)]
     (if (leaf? frame)
       (if (seq child-domains)
         (let [frame-ctx {:mapping my-mapping :layers (:layers frame)}
               x-col (effective-axis-col frame-ctx :x)
               y-col (effective-axis-col frame-ctx :y)
               x-dom (get-in child-domains [:x x-col])
               y-dom (get-in child-domains [:y y-col])]
           (if (or x-dom y-dom)
             (update frame :opts merge
                     (cond-> {}
                       x-dom (assoc :x-scale-domain x-dom)
                       y-dom (assoc :y-scale-domain y-dom)))
             frame))
         frame)
       (update frame :frames
               (fn [children]
                 (mapv #(inject-shared-scales % child-domains my-mapping)
                       children)))))))
