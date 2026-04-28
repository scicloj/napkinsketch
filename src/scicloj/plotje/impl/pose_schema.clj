(ns scicloj.plotje.impl.pose-schema
  "Malli schema for the Pose data model.

   A pose is a plain recursive map. A leaf pose has no :poses (or
   an empty :poses vector). A composite pose has :poses and an
   optional :layout describing how sub-poses tile a bounding
   rectangle.

   Validation is not wired into any runtime path yet; Phase 6 of the
   pre-alpha refactor (see dev-notes/pre-alpha-refactor-plan.md) adds
   validation at public API boundaries. Until then, impl.pose operates
   on structurally-valid poses by convention; this schema is the
   authoritative definition of that convention.

   Decisions made in Phase 2:
   - A leaf with no :data and no :mapping is valid -- leaves inherit
     context from ancestors via impl.pose/resolve-tree.
   - :share-scales is structurally allowed on any pose; it is a no-op
     on leaves (nothing to share).
   - :layout :weights length is not required to equal (count :poses);
     impl.pose/compute-layout tolerates short/long weight vectors."
  (:require [malli.core :as m]))

;; ---- Sub-schemas ----

(def Layout
  "Compositor layout spec for a composite pose."
  [:map
   [:direction {:optional true} [:enum :horizontal :vertical]]
   [:weights {:optional true} [:vector pos?]]])

(def ShareScales
  "Axis keys whose scale domains union across descendants of a composite."
  [:set [:enum :x :y]])

(def Mapping
  "Aesthetic mapping: keyword key -> column ref or literal value."
  [:map-of keyword? any?])

(def PoseLayer
  "A pose-layer: a layer declaration attached to a pose. :layer-type
   names a registered entry; :mark / :stat / :position are
   layer-structural siblings extracted from user opts by build-layer
   (Phase 6 decision 1)."
  [:map
   [:layer-type {:optional true} keyword?]
   [:mark       {:optional true} keyword?]
   [:stat       {:optional true} keyword?]
   [:position   {:optional true} keyword?]
   [:mapping    {:optional true} Mapping]
   [:data       {:optional true} any?]])

;; ---- Pose (recursive) ----

(def PoseSchema
  "Structural schema for a pose tree.

   Shape:
     {:data         ?  dataset (inherited from ancestor if absent)
      :mapping      ?  aesthetic mappings (merges with ancestors)
      :layers       ?  PoseLayer vec at this level (accumulates into leaves)
      :poses       ?  sub-poses; absent or empty = leaf
      :layout       ?  Layout for composites
      :opts         ?  plot options (inheritable)
      :share-scales ?  ShareScales}

   Permissive {:closed false} intentionally -- generators like facet
   and mosaic attach metadata keys (:panel-label, :facet-row, ...) to
   leaves that pass through resolve-tree unchanged."
  [:schema {:registry {::pose [:map
                               [:data {:optional true} any?]
                               [:mapping {:optional true} Mapping]
                               [:layers {:optional true} [:vector PoseLayer]]
                               [:poses {:optional true} [:vector [:ref ::pose]]]
                               [:layout {:optional true} Layout]
                               [:opts {:optional true} map?]
                               [:share-scales {:optional true} ShareScales]]}}
   [:ref ::pose]])

;; ---- Validation Helpers ----

(defn valid?
  "Check if x conforms to the pose schema."
  [x]
  (m/validate PoseSchema x))

(defn explain
  "Explain why x does not conform to the pose schema, or nil if valid."
  [x]
  (m/explain PoseSchema x))
