(ns scicloj.plotje.impl.draft-schema
  "Malli schemas for the draft data model -- the records returned by
   `pj/pose->draft` (and `pj/draft`).

   Drafts are the intermediate stage between pose and plan. They are
   user-observable (the `pj/draft` shortcut and the predicates
   `pj/leaf-draft?` / `pj/composite-draft?` are public) but are
   primarily inspected, not traversed programmatically. The schemas
   here document the top-level structure -- the keys on the records
   themselves -- without enumerating every key a layer map may carry.
   That post-scope-merge layer shape would essentially duplicate the
   pose mapping schema in a different state, and the drift risk
   would outweigh the documentation value.

   Backend authors who consume drafts directly should rely on
   destructuring `:layers` and `:opts` on a leaf draft, or
   `:sub-drafts` / `:chrome-spec` / `:layout` on a composite, and
   apply the layer-type registry to interpret each layer's
   `:layer-type`, `:mark`, and `:stat`."
  (:require [malli.core :as m]
            [scicloj.plotje.impl.plan-schema :as ps]))

(def LeafDraftSchema
  "A leaf draft -- the post-scope-merge intermediate produced by
   `pj/pose->draft` for a leaf pose. Canonical contract for the
   `LeafDraft` defrecord in `impl/resolve.clj`.

   `:layers` is a vector of layer maps, one per applicable layer.
   Each layer map carries the merged scope (`:data`, `:layer-type`,
   `:mark`, `:stat`, position aesthetics, appearance aesthetics,
   plus internal markers like `:__panel-idx`). The exact key set
   depends on the layer type and is not modeled here -- consult
   the layer-type registry for what each `:layer-type` consumes."
  [:map
   [:layers [:vector map?]]
   [:opts map?]])

(def SubDraft
  "One entry in a CompositeDraft's `:sub-drafts`: a leaf placed at a
   pose-tree path with its rect inside the composite, plus per-leaf
   `:opts` (suppress-x-label / suppress-y-label / suppress-legend
   flags applied during composite drafting). Parallel to `SubPlot`
   in plan-schema.

   Note: `:draft` here is a bare vector of layer maps (not a
   `LeafDraft` record). Composite sub-drafts skip the `LeafDraft`
   wrapper -- the layers travel directly through the compositor's
   per-leaf draft->plan call."
  [:map
   [:path [:vector int?]]
   [:rect ps/Rect]
   [:draft [:vector map?]]
   [:opts map?]])

(def CompositeDraftSchema
  "A composite draft -- produced by `pj/pose->draft` for a composite
   pose. Canonical contract for the `CompositeDraft` defrecord in
   `impl/resolve.clj`.

   Sub-drafts wrap leaf drafts in an envelope (path + rect + opts),
   parallel to plan-schema's SubPlot. `:chrome-spec` is shaped like
   `CompositeChrome` from plan-schema; the chrome-spec on a draft is
   the same value that flows through to the plan stage's `:chrome`.
   `:layout` maps each sub-draft's pose-tree path to its rectangle
   inside the composite."
  [:map
   [:width pos-int?]
   [:height pos-int?]
   [:sub-drafts [:vector SubDraft]]
   [:chrome-spec ps/CompositeChrome]
   [:layout [:map-of [:vector int?] ps/Rect]]])

(def DraftSchema
  "Top-level draft schema -- accepts either shape."
  [:or LeafDraftSchema CompositeDraftSchema])

(defn valid?
  "True if x conforms to a draft schema (leaf or composite)."
  [x]
  (m/validate DraftSchema x))

(defn explain
  "Explain why x does not conform to the draft schema. Returns nil
   if valid."
  [x]
  (m/explain DraftSchema x))
