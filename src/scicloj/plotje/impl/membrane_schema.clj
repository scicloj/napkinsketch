(ns scicloj.plotje.impl.membrane-schema
  "Malli schema for the metadata Plotje stamps onto the vector
   returned by `plan->membrane` (and so by `pj/membrane`).

   The vector itself is a Membrane drawable tree -- a sequence of
   things satisfying `membrane.ui/IOrigin`. That structural contract
   belongs to Membrane (see `membrane.ui` protocols) and is not
   modeled here. This schema is strictly Plotje's own contract: the
   keys we attach as metadata so that `membrane->plot` defmethods
   (and any third-party backend) can read plan-derived dimensions
   and title without re-walking the tree."
  (:require [malli.core :as m]))

(def MembraneMeta
  "Plotje's metadata contract on the vector returned by
   `plan->membrane`.

   - `:total-width`  -- canvas width in drawing units (positive integer).
   - `:total-height` -- canvas height in drawing units (positive integer).
   - `:title`        -- plot title, or nil if unset.

   Backend authors writing their own `membrane->plot` defmethod
   should read these from `(meta membrane-tree)` to size the canvas
   and label it. Backends that produce membrane vectors directly
   (without going through `pj/plan->membrane`) should attach metadata
   conforming to this schema."
  [:map
   [:total-width pos-int?]
   [:total-height pos-int?]
   [:title [:maybe string?]]])

(defn valid-meta?
  "True if x conforms to `MembraneMeta`."
  [x]
  (m/validate MembraneMeta x))

(defn explain-meta
  "Explain why x does not conform to `MembraneMeta`. Returns nil
   if valid, or a Malli explanation map if invalid."
  [x]
  (m/explain MembraneMeta x))
