;; # Timelines
;;
;; Five recipes for visualizing events, intervals, and movement
;; over time. The chapter introduces `pj/lay-interval-h` for
;; horizontal interval bars and shows how existing primitives
;; combine to build calendar-aware visualizations.
;;
;; Pipeline reminder: every example threads data through one or
;; more `pj/lay-*` calls, then through `pj/options` for chrome.
;; Plotje detects temporal columns automatically and picks
;; calendar-aware tick labels for the date axis.

(ns plotje-book.timelines
  (:require
   ;; Tablecloth -- dataset manipulation
   [tablecloth.api :as tc]
   ;; rdatasets -- bundled R datasets (ggplot2-presidential, etc.)
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]))

;; ## Historical event timeline
;;
;; Five milestones in computing history. Each event is a point on
;; a single horizontal line, with a text label nudged above. The
;; vertical position is fixed (`y = 1`) because the y-axis here
;; carries no meaning -- it just provides a baseline.

(def computing-milestones
  {:date  [#inst "1936-01-01" #inst "1947-12-23" #inst "1969-10-29"
           #inst "1989-03-12" #inst "2007-06-29"]
   :y     [1 1 1 1 1]
   :event ["Turing machine"
           "Transistor"
           "ARPANET first link"
           "World Wide Web"
           "iPhone"]})

(-> computing-milestones
    (pj/lay-point :date :y {:size 6 :color "#2c3e50"})
    (pj/lay-text  :date :y {:text :event :nudge-y 0.3 :color "#2c3e50"})
    (pj/options {:title "Five milestones in computing"
                 :height 220
                 :y-label ""
                 :x-label "year"}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 5 (:points s))
                                (every? (set (:texts s))
                                        ["Turing machine" "Transistor"
                                         "ARPANET first link"
                                         "World Wide Web" "iPhone"]))))])

;; The text labels read horizontally even when dates are tightly
;; clustered. For dense timelines, a different y for each event
;; is a clean way to spread labels apart.

(def with-staggered-y
  (assoc computing-milestones :y [2 1 1.5 2 1]))

(-> with-staggered-y
    (pj/lay-point :date :y {:size 6 :color "#2c3e50"})
    (pj/lay-text  :date :y {:text :event :nudge-y 0.18 :color "#2c3e50"})
    (pj/options {:title "Same milestones, staggered y for label clarity"
                 :height 260
                 :y-label ""
                 :x-label "year"}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 1 (:panels s)))))])

;; ## Annotated time series
;;
;; A regular line chart gains context when key dates appear as
;; vertical reference lines. The rules sit at constants in the
;; options map -- not in a data column -- so they are treated as
;; annotations.

(def unemployment
  (-> (rdatasets/ggplot2-economics)
      (tc/select-rows #(let [d (:date %)]
                         (and (>= (.getYear d) 2000)
                              (<= (.getYear d) 2014))))))

(-> unemployment
    (pj/lay-line :date :unemploy {:color "#34495e"})
    (pj/lay-rule-v {:x-intercept (java.time.LocalDate/parse "2008-09-15")
                    :color "#c0392b" :alpha 0.6})
    (pj/lay-rule-v {:x-intercept (java.time.LocalDate/parse "2001-03-01")
                    :color "#7f8c8d" :alpha 0.5})
    (pj/options {:title "US unemployment with recession markers"
                 :y-label "thousands unemployed"
                 :x-label "date"
                 :height 320}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (>= (:lines s) 1))))])

;; The two rules mark the start of the dot-com recession (March
;; 2001) and the Lehman Brothers collapse (September 2008). The
;; line shape relative to the rules tells the recession story
;; without any prose.

;; ## Gantt chart with `lay-interval-h`
;;
;; `pj/lay-interval-h` draws one horizontal bar per row, from
;; `x` to `:x-end`, sitting at the lane named by the categorical
;; `y` column. The classic project Gantt:

(def project
  {:start  [#inst "2024-01-01" #inst "2024-02-15" #inst "2024-04-01"
            #inst "2024-05-10" #inst "2024-06-20"]
   :end    [#inst "2024-03-15" #inst "2024-04-20" #inst "2024-06-30"
            #inst "2024-07-10" #inst "2024-08-30"]
   :task   ["Design" "Build" "Test" "Deploy" "Document"]
   :team   ["UX" "Eng" "QA" "Eng" "UX"]})

(-> project
    (pj/lay-interval-h :start :task {:x-end :end :color :team})
    (pj/options {:title "Project plan -- bars colored by team"
                 :y-label "task"
                 :x-label ""
                 :height 320}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:polygons s)))))])

;; A real-world Gantt: every US president's term since 1953,
;; from `rdatasets/ggplot2-presidential`. Color encodes party.

(-> (rdatasets/ggplot2-presidential)
    (pj/lay-interval-h :start :name {:x-end :end :color :party})
    (pj/options {:title "US presidential terms since 1953"
                 :y-label ""
                 :x-label "year"
                 :height 420
                 :palette ["#3498db" "#e74c3c"]}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 12 (:polygons s)))))])

;; The blue-and-red palette mirrors the conventional US party
;; colors (Democratic / Republican). Each bar's length is the
;; literal duration of the term -- tight bars (Kennedy, Ford)
;; jump out next to long ones (Reagan, Obama).

;; ## Adjusting bar height
;;
;; The `:height` option controls how much of each row's band the
;; bar fills. The default is `0.7`; smaller values leave more
;; whitespace between rows, larger values approach overlap.

(-> project
    (pj/lay-interval-h :start :task {:x-end :end :color :team :height 0.4})
    (pj/options {:title "height = 0.4 -- thin bars"
                 :y-label "task"
                 :x-label ""
                 :height 320}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 5 (:polygons s))))])

;; ## Marey train schedule
;;
;; Étienne-Jules Marey's classic 1885 diagram of Paris-Lyon
;; trains has a categorical y-axis (stations, in physical order)
;; and a temporal x-axis. Each train is a polyline; segments
;; between stations slope down because time moves forward as the
;; train moves south.
;;
;; Plotje builds this with `pj/lay-line` plus the existing
;; `:y-type :categorical` override, since the station order is
;; the physical north-to-south sequence (not alphabetical).

(def trains
  ;; Four trains, each visiting five stations in order. Express trains
  ;; (A, C) take ~7 hours total; locals (B, D) ~9 hours -- visible as a
  ;; shallower slope on the plot.
  (let [stations ["Paris" "Dijon" "Lyon" "Avignon" "Marseille"]
        express  [6.0 8.0  9.5 11.5 13.0]
        local    [7.0 9.5 11.5 14.0 16.0]
        train-shifts [["Express A" 0.0  express]
                      ["Local B"   1.0  local]
                      ["Express C" 2.5  express]
                      ["Local D"   4.0  local]]]
    (vec
     (for [[name shift schedule] train-shifts
           [station hour] (map vector stations schedule)
           :let [h (+ hour shift)
                 hh (int h)
                 mm (int (* 60 (- h hh)))]]
       {:station station
        :time (java.time.LocalDateTime/of 2024 6 1 hh mm)
        :train name}))))

(-> trains
    (pj/lay-line  :time :station {:color :train :y-type :categorical :size 1.5})
    (pj/lay-point :time :station {:color :train :y-type :categorical :size 5})
    (pj/options {:title "Marey schedule -- Paris to Marseille"
                 :y-label ""
                 :x-label "time of day"
                 :height 320}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:lines s))
                                (= 20 (:points s)))))])

;; The slope of each line carries information: steeper means
;; slower (more time between two stations). The point overlay
;; marks the actual stops; remove `lay-point` and the schedule
;; reads as continuous travel.

;; ## Multi-track activity timeline
;;
;; A within-day comparison reads more clearly when the x-axis is
;; hour-of-day (a small numeric range) and each day is its own
;; lane. The `lay-interval-h` data shape is unchanged -- only
;; the meaning of x changes from absolute time to hours since
;; midnight.

(def activity
  ;; A developer's week: meetings, deep-work blocks. Hours since
  ;; midnight; each row is one block.
  {:start [9.0 10.5 13.0      ;; Mon
           9.0 11.0 14.5      ;; Tue
           9.5 13.0 15.0      ;; Wed
           9.0 10.0 13.5      ;; Thu
           9.0 11.0 15.0]     ;; Fri
   :end   [10.5 12.0 17.0     ;; Mon
           11.0 12.5 17.0     ;; Tue
           13.0 15.0 17.0     ;; Wed
           10.0 12.5 17.0     ;; Thu
           11.0 15.0 17.0]    ;; Fri
   :day   ["Mon" "Mon" "Mon"
           "Tue" "Tue" "Tue"
           "Wed" "Wed" "Wed"
           "Thu" "Thu" "Thu"
           "Fri" "Fri" "Fri"]
   :kind  ["meeting" "deep work" "deep work"
           "deep work" "meeting" "deep work"
           "deep work" "meeting" "deep work"
           "meeting" "meeting" "deep work"
           "deep work" "meeting" "deep work"]})

(-> activity
    (pj/lay-interval-h :start :day {:x-end :end :color :kind})
    (pj/options {:title "A week, hour-by-hour"
                 :y-label ""
                 :x-label "hour of day"
                 :height 320}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 15 (:polygons s)))))])

;; At a glance: Wednesday's afternoon block is the longest
;; uninterrupted deep-work stretch; Mondays start with a meeting,
;; Fridays with deep work.

;; ## Faceting by category
;;
;; Splitting one chart into one-panel-per-category makes
;; overlapping intervals easier to compare. Faceting the activity
;; timeline by `:kind` produces two stacked tracks -- meetings on
;; one panel, deep work on the other.

(-> activity
    (pj/lay-interval-h :start :day {:x-end :end :color :kind})
    (pj/facet :kind)
    (pj/options {:title "Same week, faceted by activity kind"
                 :x-label "hour of day"
                 :y-label ""
                 :height 360}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 15 (:polygons s)))))])

;; ## Interactive preview (experimental)
;;
;; Plotje renders SVG hiccup. Any Kindly-compatible notebook tool
;; that handles `kind/hiccup` with `<script>` blocks can wrap a
;; Plotje plot in a small JavaScript layer for pan, zoom, or
;; hover -- without any library changes.
;;
;; The cell below wraps a presidential-Gantt SVG in an HTML
;; container with a tiny viewBox-based pan / zoom script. It is
;; purely additive: the SVG is the same one Plotje produces; the
;; wrapper adds an id, a cursor style, and a script that mutates
;; the viewBox in response to mouse events.

(let [plot-svg (pj/plot
                (-> (rdatasets/ggplot2-presidential)
                    (pj/lay-interval-h :start :name {:x-end :end :color :party})
                    (pj/options {:title "Presidential terms (drag/wheel to explore)"
                                 :height 420
                                 :palette ["#3498db" "#e74c3c"]})))
      attrs (second plot-svg)
      body (drop 2 plot-svg)
      plot-id (str "pj-" (System/nanoTime))
      ;; The pan/zoom script reads data-original-viewbox to know
      ;; how to reset, then mutates the viewBox attribute on each
      ;; mouse event. Building the script as a single string keeps
      ;; the source code display readable.
      script (str "(function(){var s=document.getElementById('" plot-id "');"
                  "if(!s)return;"
                  "var o=s.dataset.origVb.split(/\\s+/).map(Number),"
                  "v={x:o[0],y:o[1],w:o[2],h:o[3]},d=false,"
                  "sx=0,sy=0,vx=0,vy=0;"
                  "function a(){s.setAttribute('viewBox',v.x+' '+v.y+' '+v.w+' '+v.h);}"
                  "s.addEventListener('mousedown',function(e){d=true;sx=e.clientX;sy=e.clientY;vx=v.x;vy=v.y;s.style.cursor='grabbing';});"
                  "window.addEventListener('mouseup',function(){d=false;s.style.cursor='grab';});"
                  "window.addEventListener('mousemove',function(e){if(!d)return;"
                  "var r=s.getBoundingClientRect();"
                  "v.x=vx-(e.clientX-sx)*v.w/r.width;v.y=vy-(e.clientY-sy)*v.h/r.height;a();});"
                  "s.addEventListener('wheel',function(e){e.preventDefault();"
                  "var r=s.getBoundingClientRect(),"
                  "px=(e.clientX-r.left)/r.width,"
                  "py=(e.clientY-r.top)/r.height,"
                  "f=(e.deltaY*-1>0)?0.9:1.1,nw=v.w*f,nh=v.h*f;"
                  "v.x+=(v.w-nw)*px;v.y+=(v.h-nh)*py;v.w=nw;v.h=nh;a();},{passive:false});"
                  "s.addEventListener('dblclick',function(){v={x:o[0],y:o[1],w:o[2],h:o[3]};a();});"
                  "})();")]
  (kind/hiccup
   [:div {:style "border:1px solid #ddd; padding:4px;"}
    [:div {:style "font-size:12px; color:#666; margin-bottom:4px;"}
     "drag to pan; mouse wheel to zoom; double-click to reset"]
    (into [:svg (assoc attrs
                       :id plot-id
                       :style "cursor:grab; user-select:none;"
                       :data-orig-vb (:viewBox attrs))]
          body)
    [:script script]]))

;; What the wrapper does, end-to-end:
;;
;; 1. `pj/plot` returns SVG hiccup (a vector starting with `:svg`).
;; 2. The let-bindings pull off the attributes map and child elements,
;;    then re-emit the same SVG with an injected `:id` and a `:data-orig-vb`
;;    holding the original viewBox so the script can reset on double-click.
;; 3. A single `:script` element runs an IIFE: it attaches mousedown,
;;    mousemove, wheel, and dblclick listeners that mutate `viewBox`.
;; 4. The whole thing is wrapped in `kind/hiccup` so Clay's HTML output
;;    treats it as live HTML rather than data.
;;
;; A production-grade interactive layer would add: bounded panning so
;; the chart doesn't drift off-screen, animation easing on reset,
;; programmatic reset from outside, and tap gestures on touchscreens.

;; ## What's next
;;
;; - [**Change Over Time**](./plotje_book.change_over_time.html) -- line, step, area on a date axis
;; - [**Faceting**](./plotje_book.faceting.html) -- splitting one chart into many panels
;; - [**Customization**](./plotje_book.customization.html) -- titles, palettes, size, scales
