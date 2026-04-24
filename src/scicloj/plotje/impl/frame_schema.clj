(ns scicloj.plotje.impl.frame-schema
  "Malli schema for the Frame data model.

   A frame is a plain recursive map. A leaf frame has no :frames (or
   an empty :frames vector). A composite frame has :frames and an
   optional :layout describing how sub-frames tile a bounding
   rectangle.

   Validation is not wired into any runtime path yet; Phase 6 of the
   pre-alpha refactor (see dev-notes/pre-alpha-refactor-plan.md) adds
   validation at public API boundaries. Until then, impl.frame operates
   on structurally-valid frames by convention; this schema is the
   authoritative definition of that convention.

   Decisions made in Phase 2:
   - A leaf with no :data and no :mapping is valid -- leaves inherit
     context from ancestors via impl.frame/resolve-tree.
   - :share-scales is structurally allowed on any frame; it is a no-op
     on leaves (nothing to share).
   - :layout :weights length is not required to equal (count :frames);
     impl.frame/compute-layout tolerates short/long weight vectors."
  (:require [malli.core :as m]))

;; ---- Sub-schemas ----

(def Layout
  "Compositor layout spec for a composite frame."
  [:map
   [:direction {:optional true} [:enum :horizontal :vertical]]
   [:weights {:optional true} [:vector pos?]]])

(def ShareScales
  "Axis keys whose scale domains union across descendants of a composite."
  [:set [:enum :x :y]])

(def Mapping
  "Aesthetic mapping: keyword key -> column ref or literal value."
  [:map-of keyword? any?])

(def Layer
  "A layer declaration inside a frame. :layer-type names a registered
   entry; :mark / :stat / :position are layer-structural siblings
   extracted from user opts by build-layer (Phase 6 decision 1)."
  [:map
   [:layer-type {:optional true} keyword?]
   [:mark       {:optional true} keyword?]
   [:stat       {:optional true} keyword?]
   [:position   {:optional true} keyword?]
   [:mapping    {:optional true} Mapping]
   [:data       {:optional true} any?]])

;; ---- Frame (recursive) ----

(def FrameSchema
  "Structural schema for a frame tree.

   Shape:
     {:data         ?  dataset (inherited from ancestor if absent)
      :mapping      ?  aesthetic mappings (merges with ancestors)
      :layers       ?  Layer vec at this level (accumulates into leaves)
      :frames       ?  sub-frames; absent or empty = leaf
      :layout       ?  Layout for composites
      :opts         ?  plot options (inheritable)
      :share-scales ?  ShareScales}

   Permissive {:closed false} intentionally -- generators like facet
   and mosaic attach metadata keys (:panel-label, :facet-row, ...) to
   leaves that pass through resolve-tree unchanged."
  [:schema {:registry {::frame [:map
                                [:data {:optional true} any?]
                                [:mapping {:optional true} Mapping]
                                [:layers {:optional true} [:vector Layer]]
                                [:frames {:optional true} [:vector [:ref ::frame]]]
                                [:layout {:optional true} Layout]
                                [:opts {:optional true} map?]
                                [:share-scales {:optional true} ShareScales]]}}
   [:ref ::frame]])

;; ---- Validation Helpers ----

(defn valid?
  "Check if x conforms to the frame schema."
  [x]
  (m/validate FrameSchema x))

(defn explain
  "Explain why x does not conform to the frame schema, or nil if valid."
  [x]
  (m/explain FrameSchema x))
