(ns scicloj.napkinsketch.impl.frame-schema
  "Malli schema skeleton for the Frame data model.

   A frame is a plain recursive map that replaces both sketch and view.
   A leaf frame has no :frames. A composite frame has :frames and an
   optional :layout describing how sub-frames tile a bounding rectangle.

   This schema is intentionally permissive -- it describes the structural
   shape, not semantic constraints. Validation is not wired into any
   runtime path yet; Phase 2 of the pre-alpha refactor (see
   dev-notes/pre-alpha-refactor-plan.md) adds unit tests against this
   schema, and Phase 6 wires validation at public API boundaries.

   Open questions deferred to Phase 2:
   - Whether {:layers [...]} with no :data and no :mapping is a valid
     intermediate frame (leaf that inherits everything from its parent).
   - Whether :share-scales only belongs on composites or also on leaves
     (probably composites only -- a leaf has nothing to share with).
   - Whether :layout requires :weights to match (count :frames)."
  (:require [malli.core :as m]))

;; ---- Frame sub-schemas ----

(def Layout
  "Compositor layout spec for a composite frame."
  [:map
   [:direction [:enum :horizontal :vertical]]
   [:weights {:optional true} [:vector pos?]]])

(def ShareScales
  "Axis keys shared across descendants of a composite."
  [:set [:enum :x :y]])

;; ---- Frame (recursive) ----

(def FrameSchema
  "Structural schema for a frame tree. Permissive by design.

   Shape:
     {:data         ?  dataset (inherited from ancestor if absent)
      :mapping      ?  aesthetic mappings (merges with ancestors)
      :layers       ?  layers at this level (accumulates into leaves)
      :frames       ?  sub-frames; absent = leaf
      :layout       ?  Layout for composites
      :opts         ?  plot options (inheritable)
      :share-scales ?  ShareScales for composites}"
  [:schema {:registry {::frame [:map
                                [:data {:optional true} any?]
                                [:mapping {:optional true} [:map-of keyword? any?]]
                                [:layers {:optional true} [:vector map?]]
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
