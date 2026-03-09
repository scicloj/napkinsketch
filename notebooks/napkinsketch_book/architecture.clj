;; # Architecture
;;
;; napkinsketch is built around two data models separated by a clean boundary.
;; This notebook documents the pipeline, the data models, and how they connect.

(ns napkinsketch-book.architecture
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]
   [scicloj.napkinsketch.impl.sketch-schema :as ss]))

;; ## Pipeline Overview

(kind/mermaid "
graph LR
  V[\"Views<br/>(API)\"] -->|resolve| S[\"Sketch<br/>(data-space)\"]
  S -->|scales + coords| M[\"Membrane Scene<br/>(pixel-space)\"]
  M -->|tree walk| SVG[\"SVG Hiccup<br/>(output)\"]
  style V fill:#e8f5e9
  style S fill:#fff3e0
  style M fill:#e3f2fd
  style SVG fill:#fce4ec
")

;; - **Views** — user-facing compositional API: `view`, `lay`, `point`, `histogram`, etc.
;;
;; - **Sketch** — fully resolved plot specification. Data-space geometry,
;;   domains, tick info, legend. Plain Clojure maps. No rendering primitives.
;;
;; - **Membrane Scene** — positioned drawing primitives in pixel space.
;;   Translate, WithColor, Path, Label, etc.
;;
;; - **SVG Hiccup** — final output. A tree walk converts membrane records
;;   to SVG elements, which Clay/Kindly renders in notebooks.

;; ## Two Data Models
;;
;; The key architectural insight is that **what** to draw and **how** to
;; draw it are separate concerns.

(kind/mermaid "
graph TB
  subgraph WHAT [\"What to draw\"]
    direction TB
    A1[\"api.clj\"]
    A2[\"impl/view.clj\"]
    A3[\"impl/stat.clj\"]
    A4[\"impl/sketch.clj\"]
  end
  subgraph HOW [\"How to draw it\"]
    direction TB
    B1[\"impl/scale.clj\"]
    B2[\"impl/mark.clj\"]
    B3[\"impl/panel.clj\"]
    B4[\"impl/plot.clj\"]
    B5[\"render/svg.clj\"]
  end
  WHAT -->|sketch| HOW
  style WHAT fill:#e8f5e9
  style HOW fill:#e3f2fd
")

;; ### The Sketch (what)
;;
;; A sketch answers: what data, what marks, what domains, what ticks,
;; what legend entries. It uses data-space coordinates — the numbers are
;; the actual data values, not pixel positions.
;;
;; Properties:
;;
;; - Plain Clojure maps, vectors, numbers, strings, keywords
;;
;; - No membrane types, no datasets, no scale objects
;;
;; - Fully serializable: `pr-str` / `read-string` roundtrips
;;
;; - Validates against a Malli schema
;;
;; - Backend-independent — could drive Plotly, Canvas, or any renderer
;;
;; ### The Membrane Scene (how)
;;
;; A membrane scene answers: what pixel coordinates, what colors at
;; what positions, what font sizes for labels. It's a tree of
;; defrecords that membrane knows how to render.
;;
;; Properties:
;;
;; - Membrane-specific types: Translate, WithColor, Path, Label, etc.
;;
;; - Pixel-space coordinates — all positions are resolved
;;
;; - Not serializable — contains Java objects (fonts, etc.)
;;
;; - Converted to SVG by a context-passing tree walk

;; ## Namespace Structure

(kind/mermaid "
graph TD
  API[\"api.clj\"] --> VIEW[\"impl/view.clj\"]
  API --> PLOT[\"impl/plot.clj\"]
  API --> SKETCH[\"impl/sketch.clj\"]
  SKETCH --> VIEW
  SKETCH --> STAT[\"impl/stat.clj\"]
  SKETCH --> SCALE[\"impl/scale.clj\"]
  SKETCH --> DEFAULTS[\"impl/defaults.clj\"]
  PLOT --> SKETCH
  PLOT --> PANEL[\"impl/panel.clj\"]
  PLOT --> SVG[\"render/svg.clj\"]
  PANEL --> MARK[\"impl/mark.clj\"]
  PANEL --> SCALE
  PANEL --> COORD[\"impl/coord.clj\"]
  style API fill:#c8e6c9
  style SKETCH fill:#ffe0b2
  style PLOT fill:#bbdefb
  style SVG fill:#f8bbd0
")

;; ## Data Flow Example
;;
;; Let's trace a scatter plot through the pipeline.

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

;; ### Stage 1: Views
;;
;; Users compose views — declarative descriptions of what to plot.

(def views
  [(sk/point {:data iris :x :sepal_length :y :sepal_width :color :species})])

;; A view is a plain map:

(dissoc (first views) :data)

(kind/test-last [(fn [v] (= :point (:mark v)))])

;; ### Stage 2: Sketch
;;
;; `sk/sketch` resolves views into a sketch: extracts data from columns,
;; computes stats, merges domains, resolves colors, computes ticks.

(def sk (sk/sketch views))

;; The sketch has data-space coordinates:

(let [panel (first (:panels sk))
      layer (first (:layers panel))
      group (first (:groups layer))]
  {:mark (:mark layer)
   :x-domain (:x-domain panel)
   :y-domain (:y-domain panel)
   :n-groups (count (:groups layer))
   :first-group-n-points (count (:xs group))
   :first-group-color (:color group)})

(kind/test-last [(fn [m] (and (= :point (:mark m))
                             (= 3 (:n-groups m))
                             (pos? (:first-group-n-points m))))])

;; The sketch validates against Malli:

(ss/valid? sk)

(kind/test-last [true?])

;; And serializes cleanly:

(= sk (read-string (pr-str sk)))

(kind/test-last [true?])

;; ### Stage 3: Plot (Sketch → Membrane → SVG)
;;
;; `sk/plot` calls `sk/sketch` internally, then maps the data-space
;; geometry through scales to pixel space, builds a membrane scene tree,
;; and converts to SVG.

(sk/plot views)

;; ## The Sketch as a Boundary
;;
;; The sketch is the boundary between the "what" and the "how".
;; Everything above it is about data and semantics. Everything below
;; is about pixels and rendering.

(kind/mermaid "
graph LR
  subgraph WHAT [\"WHAT — data + semantics\"]
    V[\"Views\"]
    ST[\"Statistics\"]
    D[\"Domains\"]
    C[\"Colors\"]
  end
  subgraph HOW [\"HOW — pixels + rendering\"]
    SC[\"Scales (wadogo)\"]
    CO[\"Coord transforms\"]
    MS[\"Membrane scene\"]
    SV[\"SVG conversion\"]
  end
  WHAT -->|sketch| HOW
  style WHAT fill:#e8f5e9
  style HOW fill:#e3f2fd
")

;; This separation enables:
;;
;; - Inspecting the plot specification without rendering
;;
;; - Validating plot structure with Malli
;;
;; - Serializing plots for storage or transmission
;;
;; - Potentially adding other backends (Canvas, Plotly, Vega-Lite)
;;   that consume sketches

;; ## Multi-Layer Example
;;
;; Let's trace a more complex example: scatter points with regression lines,
;; colored by species.

(def multi-views
  [(sk/point {:data iris :x :petal_length :y :petal_width :color :species})
   (sk/lm {:data iris :x :petal_length :y :petal_width :color :species})])

(def multi-sk (sk/sketch multi-views {:title "Iris Petals with Regression"}))

;; Two layers in the sketch:

(count (:layers (first (:panels multi-sk))))

(kind/test-last [(fn [n] (= 2 n))])

;; Point layer:

(let [layer (first (:layers (first (:panels multi-sk))))]
  {:mark (:mark layer)
   :n-groups (count (:groups layer))})

(kind/test-last [(fn [m] (and (= :point (:mark m)) (= 3 (:n-groups m))))])

;; Line layer (regression):

(let [layer (second (:layers (first (:panels multi-sk))))]
  {:mark (:mark layer)
   :stat-origin (:stat-origin layer)
   :n-groups (count (:groups layer))
   :first-group-keys (set (keys (first (:groups layer))))})

(kind/test-last [(fn [m] (and (= :line (:mark m))
                             (= :lm (:stat-origin m))
                             (= 3 (:n-groups m))
                             (contains? (:first-group-keys m) :x1)))])

;; Title and legend in the sketch:

(:title multi-sk)

(kind/test-last [(fn [t] (= "Iris Petals with Regression" t))])

(count (:entries (:legend multi-sk)))

(kind/test-last [(fn [n] (= 3 n))])

;; And it renders:

(sk/plot multi-views {:title "Iris Petals with Regression"})
