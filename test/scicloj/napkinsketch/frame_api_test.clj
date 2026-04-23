(ns scicloj.napkinsketch.frame-api-test
  "Tests for sk/frame public constructor and its equivalence to the
   legacy sk/sketch + sk/view combination. Phase 3a of the pre-alpha
   refactor: sk/frame produces a leaf-frame plain map; rendering
   routes through a leaf-frame adapter."
  (:require [clojure.test :refer [deftest testing is]]
            [tablecloth.api :as tc]
            [scicloj.napkinsketch.api :as sk]))

(def tiny-ds
  (tc/dataset {:x [1.0 2.0 3.0 4.0 5.0]
               :y [2.0 4.0 1.0 5.0 3.0]
               :g ["a" "a" "b" "b" "b"]}))

;; ============================================================
;; sk/frame constructor shape
;; ============================================================

(deftest frame-constructor-test
  (testing "0-arity makes an empty leaf frame"
    (let [f (sk/frame)]
      (is (sk/frame? f))
      (is (not (sk/sketch? f)))
      (is (nil? (:data f)))
      (is (not (contains? f :mapping)))
      (is (= [] (:layers f)))))

  (testing "1-arity coerces data, no mapping"
    (let [f (sk/frame {:x [1 2] :y [3 4]})]
      (is (sk/frame? f))
      (is (tc/dataset? (:data f)))
      (is (not (contains? f :mapping)))))

  (testing "2-arity with a map arg is an aesthetic mapping"
    (let [f (sk/frame tiny-ds {:color :g})]
      (is (= {:color :g} (:mapping f)))))

  (testing "2-arity with a keyword arg sets :x only"
    (let [f (sk/frame tiny-ds :x)]
      (is (= {:x :x} (:mapping f)))))

  (testing "3-arity sets both :x and :y"
    (let [f (sk/frame tiny-ds :x :y)]
      (is (= {:x :x :y :y} (:mapping f))))))

(deftest frame-predicate-test
  (testing "frame? rejects sketches"
    (is (not (sk/frame? (sk/sketch))))
    (is (not (sk/frame? (sk/sketch tiny-ds)))))

  (testing "sketch? rejects frames"
    (is (not (sk/sketch? (sk/frame))))
    (is (not (sk/sketch? (sk/frame tiny-ds :x :y))))))

;; ============================================================
;; Equivalence to the legacy path
;; ============================================================
;;
;; The phase-3a adapter promises that a leaf frame renders to the
;; same SVG as the equivalent (sketch + view + lay-*) construction.
;; These tests compare plans (structural) rather than SVG strings
;; (which carry nondeterministic ids).

(defn- same-plan-shape? [p1 p2]
  (and (= (:grid p1) (:grid p2))
       (= (count (:panels p1)) (count (:panels p2)))
       (= (mapv :layers (:panels p1))
          (mapv :layers (:panels p2)))))

(deftest equivalence-bivariate-test
  (testing "sk/frame :x :y -> lay-point matches sk/view :x :y -> sk/lay-point"
    (let [via-frame  (-> (sk/frame tiny-ds :x :y) sk/lay-point sk/plan)
          via-view   (-> tiny-ds (sk/view :x :y) sk/lay-point sk/plan)]
      (is (same-plan-shape? via-frame via-view)))))

(deftest equivalence-aesthetic-mapping-test
  (testing "sk/frame with aesthetic mapping matches sk/sketch"
    (let [via-frame  (-> (sk/frame tiny-ds {:color :g})
                         (sk/lay-point :x :y) sk/plan)
          via-sketch (-> (sk/sketch tiny-ds {:color :g})
                         (sk/lay-point :x :y) sk/plan)]
      (is (same-plan-shape? via-frame via-sketch)))))

(deftest equivalence-layers-before-view-test
  (testing "lay-point with explicit columns on a frame is the one-shot form"
    (let [via-frame  (-> tiny-ds
                         sk/frame
                         (sk/lay-point :x :y)
                         sk/plan)
          via-legacy (-> tiny-ds
                         (sk/lay-point :x :y)
                         sk/plan)]
      (is (same-plan-shape? via-frame via-legacy)))))

;; ============================================================
;; Composite frames render via the Phase-4 compositor: each leaf
;; renders through the existing sketch pipeline and is tiled via
;; <g transform="translate(...)"> groups inside a wrapping SVG.
;; ============================================================

(deftest composite-renders-to-svg-test
  (testing "sk/plot on a two-leaf horizontal composite returns an SVG
            whose svg-summary shows two panels"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :horizontal :weights [1 1]}
                     :frames [{:layers [{:layer-type :point}]}
                              {:layers [{:layer-type :line}]}]}
          svg (sk/plot composite)
          summary (sk/svg-summary svg)]
      (is (= :svg (first svg)))
      (is (= 2 (:panels summary))
          "one panel per leaf")
      (is (pos? (:points summary))
          "point leaf rendered")
      (is (pos? (:lines summary))
          "line leaf rendered"))))

(deftest svg-summary-auto-renders-frames-test
  (testing "svg-summary accepts a leaf-frame plain map and auto-renders"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          leaf (sk/frame ds :x :y)
          summary (sk/svg-summary leaf)]
      (is (= 1 (:panels summary)))
      (is (pos? (:points summary))
          "inferred scatter points rendered")))

  (testing "svg-summary accepts a composite frame and auto-renders"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :horizontal :weights [1 1]}
                     :frames [{:layers [{:layer-type :point}]}
                              {:layers [{:layer-type :line}]}]}
          summary (sk/svg-summary composite)]
      (is (= 2 (:panels summary)))
      (is (pos? (:points summary)))
      (is (pos? (:lines summary))))))

(deftest composite-renders-vertical-test
  (testing "vertical layout produces stacked leaves"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :vertical :weights [1 1]}
                     :frames [{:layers [{:layer-type :point}]}
                              {:layers [{:layer-type :point}]}]}
          svg (sk/plot composite)]
      (is (= 2 (:panels (sk/svg-summary svg)))))))

(deftest composite-renders-nested-test
  (testing "nested composite (horizontal outer, vertical inner) renders
            every descendant leaf"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :horizontal :weights [1 1]}
                     :frames [{:layers [{:layer-type :point}]}
                              {:layout {:direction :vertical :weights [1 1]}
                               :frames [{:layers [{:layer-type :point}]}
                                        {:layers [{:layer-type :point}]}]}]}
          svg (sk/plot composite)]
      (is (= 3 (:panels (sk/svg-summary svg)))
          "three leaves -> three panels"))))

;; ============================================================
;; Composite plans (Phase 4 Slice B)
;; ============================================================
;;
;; sk/plan on a composite returns a Plan record with :composite? true
;; and :sub-plots [{:path :rect :plan} ...] -- one entry per resolved
;; leaf. sk/plan? still returns true so downstream predicate checks
;; keep working.

(deftest composite-plan-shape-test
  (testing "sk/plan on a composite returns a composite plan"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :horizontal :weights [1 1]}
                     :frames [{:layers [{:layer-type :point}]}
                              {:layers [{:layer-type :line}]}]}
          p (sk/plan composite)]
      (is (sk/plan? p)
          "composite plan still passes plan? predicate")
      (is (:composite? p)
          ":composite? flag set")
      (is (= 2 (count (:sub-plots p)))
          "one :sub-plots entry per leaf")
      (is (every? sk/plan? (map :plan (:sub-plots p)))
          "each :plan entry is itself a plan")
      (is (every? (fn [sp] (= 4 (count (:rect sp)))) (:sub-plots p))
          "each entry carries a 4-tuple :rect"))))

(deftest composite-plan-rects-tile-frame-test
  (testing "horizontal composite sub-plot rects partition the outer width"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :opts {:width 600 :height 400}
                     :layout {:direction :horizontal :weights [1 1]}
                     :frames [{:layers [{:layer-type :point}]}
                              {:layers [{:layer-type :point}]}]}
          p (sk/plan composite)
          rects (map :rect (:sub-plots p))]
      (is (= [[0.0 0.0 300.0 400.0]
              [300.0 0.0 300.0 400.0]]
             (vec rects))
          "two leaves split 600-wide frame evenly left/right"))))

(deftest composite-plan-nested-paths-test
  (testing "paths in :sub-plots match the DFS order of leaves"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :horizontal :weights [1 1]}
                     :frames [{:layers [{:layer-type :point}]}
                              {:layout {:direction :vertical :weights [1 1]}
                               :frames [{:layers [{:layer-type :point}]}
                                        {:layers [{:layer-type :point}]}]}]}
          p (sk/plan composite)]
      (is (= [[0] [1 0] [1 1]]
             (mapv :path (:sub-plots p)))))))

;; ============================================================
;; Composite shared scales (Phase 4 Slice C)
;; ============================================================
;;
;; :share-scales #{:x} on a composite pins an identical x-domain
;; across sibling leaves that use the same x-column. Leaves with a
;; different x-column get their own bucket (column-bucketing).

(deftest composite-share-scales-x-same-column-test
  (testing "two leaves sharing :x column get a unified x-domain"
    (let [ds1 (tc/dataset {:x [0 1 2] :y [1 2 3]})
          ds2 (tc/dataset {:x [10 20 30] :y [1 2 3]})
          composite {:mapping {:y :y}
                     :share-scales #{:x}
                     :layout {:direction :horizontal :weights [1 1]}
                     :frames [{:data ds1 :mapping {:x :x}
                               :layers [{:layer-type :point}]}
                              {:data ds2 :mapping {:x :x}
                               :layers [{:layer-type :point}]}]}
          p (sk/plan composite)
          dom0 (-> p :sub-plots (get 0) :plan :panels first :x-domain)
          dom1 (-> p :sub-plots (get 1) :plan :panels first :x-domain)]
      (is (= dom0 dom1)
          "both sub-plots have the same x-domain")
      (is (= [0 30] dom0)
          "shared domain is the union of each leaf's data range"))))

(deftest composite-share-scales-x-column-bucketing-test
  (testing "leaves with different x-columns get independent buckets"
    (let [ds-a (tc/dataset {:a [0 1 2] :y [1 2 3]})
          ds-b (tc/dataset {:b [100 200] :y [1 2]})
          composite {:mapping {:y :y}
                     :share-scales #{:x}
                     :layout {:direction :horizontal :weights [1 1]}
                     :frames [{:data ds-a :mapping {:x :a}
                               :layers [{:layer-type :point}]}
                              {:data ds-b :mapping {:x :b}
                               :layers [{:layer-type :point}]}]}
          p (sk/plan composite)
          dom0 (-> p :sub-plots (get 0) :plan :panels first :x-domain)
          dom1 (-> p :sub-plots (get 1) :plan :panels first :x-domain)]
      (is (not= dom0 dom1)
          "different columns -> different domains")
      (is (= [0 2] dom0))
      (is (= [100 200] dom1)))))

(deftest composite-share-scales-marginal-style-test
  (testing "marginal-style: scatter + top density share :x = :a, right density
            uses :b (independent bucket)"
    (let [ds (tc/dataset {:a [0 1 2 3 4] :b [10 20 30 40 50]})
          composite {:data ds
                     :share-scales #{:x :y}
                     :layout {:direction :vertical :weights [1 3]}
                     :frames [;; top density on :a
                              {:mapping {:x :a}
                               :layers [{:layer-type :density}]}
                              ;; bottom row: scatter (a,b) + density on b (bucket-of-one)
                              {:layout {:direction :horizontal :weights [3 1]}
                               :frames [{:mapping {:x :a :y :b}
                                         :layers [{:layer-type :point}]}
                                        {:mapping {:x :b}
                                         :layers [{:layer-type :density}]}]}]}
          p (sk/plan composite)
          sub (:sub-plots p)
          top-density-x (-> sub (get 0) :plan :panels first :x-domain)
          scatter-x     (-> sub (get 1) :plan :panels first :x-domain)]
      (is (= top-density-x scatter-x)
          "top density and scatter share x-domain (same :a column)"))))

;; ============================================================
;; Composite lay-* identity (Phase 3b)
;; ============================================================
;;
;; The rule extends sketch_rules.clj rules 5-7 into composite frames:
;; position-carrying lay-* targets the last leaf (DFS) whose effective
;; :x/:y match, else appends a new leaf at the root level. Sketch-level
;; lay-* attaches at the root's :layers so descendants inherit it.

(deftest composite-lay-hit-last-matching-leaf-test
  (testing "two leaves with identical position mapping -- layer attaches to the last"
    (let [fr {:frames [{:layers [] :mapping {:x :x :y :y}}
                       {:layers [] :mapping {:x :x :y :y}}]}
          result (sk/lay-point fr :x :y)]
      (is (empty? (get-in result [:frames 0 :layers]))
          "first leaf stays empty")
      (is (= 1 (count (get-in result [:frames 1 :layers])))
          "layer lands on the last matching leaf (Rule 6)")
      (is (= :point (get-in result [:frames 1 :layers 0 :layer-type]))))))

(deftest composite-lay-miss-creates-root-leaf-test
  (testing "no matching leaf -- new leaf appended at root :frames"
    (let [fr {:frames [{:layers [] :mapping {:x :c :y :d}}]}
          result (sk/lay-point fr :a :b)]
      (is (= 2 (count (:frames result))))
      (is (= {:x :c :y :d} (get-in result [:frames 0 :mapping]))
          "existing leaf is preserved as-is")
      (is (= {:x :a :y :b} (get-in result [:frames 1 :mapping]))
          "new leaf carries the lay-* position mapping")
      (is (= :point (get-in result [:frames 1 :layers 0 :layer-type]))))))

(deftest composite-lay-miss-on-empty-frames-test
  (testing "composite with empty :frames -- lay-* extends the leaf in place"
    (let [fr {:frames []}
          result (sk/lay-point fr :a :b)]
      ;; An empty :frames vector counts as a leaf per impl.frame/leaf?.
      ;; Post-Phase-6, such a leaf stays in frame-world: the position
      ;; call extends the leaf's :mapping and appends a bare layer.
      (is (sk/frame? result))
      (is (not (sk/sketch? result)))
      (is (= {:x :a :y :b} (:mapping result)))
      (is (= 1 (count (:layers result))))
      (is (= :point (-> result :layers (nth 0) :layer-type))))))

(deftest composite-lay-sketch-level-test
  (testing "bare lay-* on a composite attaches at the root :layers"
    (let [fr {:frames [{:layers [] :mapping {:x :x :y :y}}]}
          result (sk/lay-point fr {:color :g})]
      (is (= 1 (count (:layers result)))
          "layer added at root")
      (is (= :point (get-in result [:layers 0 :layer-type])))
      (is (= {:color :g} (get-in result [:layers 0 :mapping])))
      (is (empty? (get-in result [:frames 0 :layers]))
          "leaf was not modified"))))

(deftest composite-lay-sketch-level-no-opts-test
  (testing "bare lay-* with no opts still attaches at root :layers"
    (let [fr {:frames [{:layers [] :mapping {:x :x :y :y}}
                       {:layers [] :mapping {:x :x :y :y}}]}
          result (sk/lay-point fr)]
      (is (= 1 (count (:layers result))))
      (is (every? #(empty? (:layers %)) (:frames result))
          "no leaf was modified -- the layer is sketch-level"))))

(deftest composite-lay-keyword-string-tolerance-test
  (testing "lay-* :x-kw :y-kw matches leaf with string-named mapping"
    (let [fr {:frames [{:layers [] :mapping {:x "x" :y "y"}}]}
          result (sk/lay-point fr :x :y)]
      (is (= 1 (count (get-in result [:frames 0 :layers])))
          "matched despite keyword vs string column-ref divergence"))))

(deftest composite-lay-ancestor-mapping-inherits-test
  (testing "ancestor :mapping with :x/:y makes a bare leaf matchable"
    (let [fr {:mapping {:x :x :y :y}
              :frames  [{:layers []}]}
          result (sk/lay-point fr :x :y)]
      (is (= 1 (count (get-in result [:frames 0 :layers])))
          "leaf inherited :x/:y from root and matched the lay-* call"))))

(deftest composite-lay-nested-deep-match-test
  (testing "DFS finds a match at arbitrary depth"
    (let [fr {:frames [{:layers [] :mapping {:x :a :y :b}}
                       {:frames [{:layers [] :mapping {:x :a :y :b}}]}]}
          result (sk/lay-point fr :a :b)]
      (is (empty? (get-in result [:frames 0 :layers]))
          "shallow match is not picked")
      (is (= 1 (count (get-in result [:frames 1 :frames 0 :layers])))
          "deep-last match gets the layer"))))

(deftest composite-lay-threading-respects-identity-test
  (testing "threading multiple lay-* on a composite puts each on the matching leaf"
    (let [fr {:frames [{:layers [] :mapping {:x :a :y :b}}
                       {:layers [] :mapping {:x :a :y :b}}]}
          result (-> fr
                     (sk/lay-point :a :b)
                     (sk/lay-line  :a :b))]
      (is (empty? (get-in result [:frames 0 :layers]))
          "first leaf never matches last -- untouched")
      (is (= [:point :line]
             (mapv :layer-type (get-in result [:frames 1 :layers])))
          "both layers accumulate on the last-matching leaf"))))

(deftest composite-lay-rule5-two-frames-different-layers-test
  (testing "scatter on leaf 0, view created, then smooth -- ends up on leaf 1"
    ;; Mirrors sketch_rules.clj Rule 6 worked example into the frame
    ;; world: starting from a composite with two same-position leaves,
    ;; one lay-* lands on each.
    (let [fr {:frames [{:layers [{:layer-type :point} {:layer-type :placeholder}]
                        :mapping {:x :x :y :y}}
                       {:layers [] :mapping {:x :x :y :y}}]}
          result (sk/lay-smooth fr :x :y {:stat :linear-model})]
      (is (= [:point :placeholder]
             (mapv :layer-type (get-in result [:frames 0 :layers])))
          "pre-existing leaf is unchanged")
      (is (= :smooth (get-in result [:frames 1 :layers 0 :layer-type]))
          "new layer lands on the last matching leaf"))))

(deftest composite-lay-preserves-non-matching-leaves-test
  (testing "miss on a composite with a non-matching leaf preserves the leaf"
    (let [fr {:frames [{:layers [{:layer-type :histogram}]
                        :mapping {:x :other}}]}
          result (sk/lay-point fr :a :b)]
      (is (= 2 (count (:frames result))))
      (is (= :histogram (get-in result [:frames 0 :layers 0 :layer-type]))
          "existing leaf untouched")
      (is (= :point (get-in result [:frames 1 :layers 0 :layer-type]))
          "miss appended a new leaf at root"))))

(deftest composite-lay-returns-composite-test
  (testing "the result of lay-* on a composite remains a composite frame"
    (let [fr {:frames [{:layers [] :mapping {:x :a :y :b}}]}
          result (sk/lay-point fr :a :b)]
      (is (sk/frame? result))
      (is (not (sk/sketch? result))))))

;; ============================================================
;; Composite options / scale / coord (Phase 3b)
;; ============================================================
;;
;; On a composite, these three all write to the root :opts.
;; resolve-tree merges root :opts into every descendant leaf, so
;; writing at the root is the frame-world analog of plot-level
;; options on a sketch.

(deftest composite-options-writes-root-opts-test
  (testing "sk/options on a composite writes to root :opts"
    (let [fr {:frames [{:layers [] :mapping {:x :a :y :b}}]}
          result (sk/options fr {:title "hi" :width 500})]
      (is (sk/frame? result))
      (is (= "hi" (get-in result [:opts :title])))
      (is (= 500 (get-in result [:opts :width])))
      (is (empty? (get-in result [:frames 0 :opts] {}))
          "leaf :opts is untouched"))))

(deftest composite-options-deep-merges-existing-opts-test
  (testing "sk/options deep-merges into an existing root :opts"
    (let [fr     {:opts   {:theme {:bg "black"}}
                  :frames [{:layers []}]}
          result (sk/options fr {:theme {:fg "white"}})]
      (is (= {:bg "black" :fg "white"}
             (get-in result [:opts :theme]))
          "nested map is merged rather than replaced"))))

(deftest composite-options-width-height-coerced-test
  (testing "width/height still get long-coerced on composites"
    (let [fr {:frames [{:layers []}]}
          result (sk/options fr {:width 500.7 :height 299.3})]
      (is (= 501 (get-in result [:opts :width])))
      (is (= 299 (get-in result [:opts :height]))))))

(deftest composite-scale-writes-root-opts-test
  (testing "sk/scale on a composite writes :x-scale at the root"
    (let [fr {:frames [{:layers [] :mapping {:x :a :y :b}}]}
          result (sk/scale fr :x :log)]
      (is (sk/frame? result))
      (is (= {:type :log} (get-in result [:opts :x-scale]))))))

(deftest composite-scale-accepts-map-spec-test
  (testing "sk/scale with a map scale-type fills in :linear as default :type"
    (let [fr {:frames [{:layers []}]}
          result (sk/scale fr :y {:breaks [0 5 10]})]
      (is (= {:type :linear :breaks [0 5 10]}
             (get-in result [:opts :y-scale]))))))

(deftest composite-coord-writes-root-opts-test
  (testing "sk/coord on a composite writes :coord at the root"
    (let [fr {:frames [{:layers []}]}
          result (sk/coord fr :polar)]
      (is (sk/frame? result))
      (is (= :polar (get-in result [:opts :coord]))))))

(deftest composite-options-preserves-frames-test
  (testing "options/scale/coord leave the :frames subtree intact"
    (let [fr {:frames [{:layers [{:layer-type :point}] :mapping {:x :a}}
                       {:layers [] :mapping {:x :b}}]}
          opt-result (sk/options fr {:title "x"})
          sc-result  (sk/scale fr :x :log)
          co-result  (sk/coord fr :flip)]
      (doseq [r [opt-result sc-result co-result]]
        (is (= (:frames fr) (:frames r))
            "frames subtree is untouched")))))

(deftest composite-options-chainable-test
  (testing "options/scale/coord chain on composites with each other and with lay-*"
    (let [fr {:frames [{:layers [] :mapping {:x :a :y :b}}]}
          result (-> fr
                     (sk/options {:title "chart"})
                     (sk/scale :x :log)
                     (sk/coord :polar)
                     (sk/lay-point :a :b))]
      (is (sk/frame? result))
      (is (= "chart" (get-in result [:opts :title])))
      (is (= {:type :log} (get-in result [:opts :x-scale])))
      (is (= :polar (get-in result [:opts :coord])))
      (is (= 1 (count (get-in result [:frames 0 :layers])))
          "lay-* still landed on the one matching leaf"))))

;; ============================================================
;; sk/frame 4-arity: positional x/y + opts map
;; ============================================================

(deftest frame-4-arity-basic-test
  (testing "4-arity constructs a leaf with :x :y and opts in mapping"
    (let [fr (sk/frame tiny-ds :x :y {:color :g})]
      (is (sk/frame? fr))
      (is (= {:x :x :y :y :color :g} (:mapping fr)))
      (is (tc/dataset? (:data fr))))))

(deftest frame-4-arity-positional-x-wins-test
  (testing "positional x/y override same keys in opts map"
    (let [fr (sk/frame tiny-ds :x :y {:x :override :color :g})]
      (is (= :x (get-in fr [:mapping :x])) "positional :x wins")
      (is (= :y (get-in fr [:mapping :y])))
      (is (= :g (get-in fr [:mapping :color]))))))

(deftest frame-4-arity-opts-data-wins-test
  (testing "opts' :data overrides the positional data (matches sk/view)"
    (let [other-ds (tc/dataset {:x [10 20] :y [30 40]})
          fr (sk/frame tiny-ds :x :y {:data other-ds})]
      (is (= 2 (tc/row-count (:data fr))) "opts :data wins; 2 rows, not 5")
      ;; And :data does not leak into :mapping
      (is (not (contains? (:mapping fr) :data))))))

(deftest frame-4-arity-unknown-key-warns-test
  (testing "unknown mapping key in opts triggers a warning and is stripped"
    (let [warnings (java.io.StringWriter.)
          fr (binding [*out* warnings]
               (sk/frame tiny-ds :x :y {:bogus :nope :color :g}))
          msg (str warnings)]
      (is (re-find #"sk/frame.*bogus" msg) "warning mentions the stripped key")
      (is (not (contains? (:mapping fr) :bogus)) "bogus key stripped from mapping")
      (is (= :g (get-in fr [:mapping :color])) "good keys survive"))))

;; ============================================================
;; sk/prepare-frame: promote hand-built frame maps
;; ============================================================

(deftest prepare-frame-attaches-kindly-test
  (testing "leaf map: prepare-frame attaches :kind/fn metadata"
    (let [fr (sk/prepare-frame
              {:data {:x [1 2 3] :y [4 5 6]}
               :mapping {:x :x :y :y}
               :layers [{:layer-type :point}]})]
      (is (sk/frame? fr))
      (is (= :kind/fn (:kindly/kind (meta fr))))
      (is (tc/dataset? (:data fr)) "data coerced to dataset")))

  (testing "composite map: prepare-frame attaches :kind/fn metadata"
    (let [ds (tc/dataset {:x [1 2 3] :y [4 5 6]})
          fr (sk/prepare-frame
              {:data ds
               :layout {:direction :horizontal}
               :frames [{:mapping {:x :x :y :y}
                         :layers [{:layer-type :point}]}
                        {:mapping {:x :y :y :x}
                         :layers [{:layer-type :line}]}]})]
      (is (= :kind/fn (:kindly/kind (meta fr))))
      (is (= 2 (:panels (sk/svg-summary fr)))))))

(deftest prepare-frame-coerces-data-recursively-test
  (testing ":data on nested sub-frames is coerced to a dataset"
    (let [fr (sk/prepare-frame
              {:layout {:direction :horizontal}
               :frames [{:data {:a [1 2] :b [3 4]}
                         :mapping {:x :a :y :b}
                         :layers [{:layer-type :point}]}
                        {:data {:c [10 20] :d [30 40]}
                         :mapping {:x :c :y :d}
                         :layers [{:layer-type :point}]}]})]
      (is (every? tc/dataset?
                  (map :data (:frames fr)))
          "each sub-frame's :data is a tablecloth dataset"))))

(deftest prepare-frame-key-order-test
  (testing "reorder places :data before :frames; :frames last"
    (let [ds (tc/dataset {:x [1 2] :y [3 4]})
          composite (sk/prepare-frame
                     {:data ds
                      :layout {:direction :horizontal}
                      :frames [{:mapping {:x :x :y :y}
                                :layers [{:layer-type :point}]}]})]
      (is (= [:layout :data :frames] (vec (keys composite)))
          "outer keys print in readable order")))

  (testing "leaf: :data goes last (no :frames to precede)"
    (let [leaf (sk/prepare-frame
                {:data {:x [1 2] :y [3 4]}
                 :mapping {:x :x :y :y}
                 :layers [{:layer-type :point}]
                 :opts {:title "t"}})]
      (is (= [:opts :mapping :layers :data] (vec (keys leaf)))))))

(deftest prepare-frame-idempotent-test
  (testing "wrapping twice yields a value with the same rendered SVG"
    (let [m0 {:data {:x [1 2 3] :y [4 5 6]}
              :mapping {:x :x :y :y}
              :layers [{:layer-type :point}]}
          m1 (sk/prepare-frame m0)
          m2 (sk/prepare-frame m1)]
      (is (= :kind/fn (:kindly/kind (meta m2))))
      (is (= (sk/svg-summary m1) (sk/svg-summary m2)))
      (is (tc/dataset? (:data m2)) "still coerced after double pass"))))

(deftest prepare-frame-outer-layers-distribute-test
  (testing "an outer-scope :layer on a composite renders in every leaf"
    (let [ds (tc/dataset {:x [1 2 3] :a [4 5 6] :b [7 8 9]})
          fr (sk/prepare-frame
              {:data ds
               :mapping {:x :x}
               :layers [{:layer-type :point}]
               :frames [{:mapping {:y :a}}
                        {:mapping {:y :b}}]})
          summary (sk/svg-summary fr)]
      (is (= 2 (:panels summary)))
      (is (= 6 (:points summary))
          "3 points per leaf x 2 leaves = 6; outer :layers distributes"))))

(deftest prepare-frame-rejects-sketch-test
  (testing "Sketch records raise with a clear message"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"expects a plain-map frame"
                          (sk/prepare-frame (sk/sketch tiny-ds))))))

(deftest prepare-frame-warns-unknown-keys-test
  (testing ":frame (singular) and other typos trigger a warning"
    (let [warnings (java.io.StringWriter.)
          fr (binding [*out* warnings]
               (sk/prepare-frame
                {:data {:x [1 2] :y [3 4]}
                 :mapping {:x :x :y :y}
                 :layers [{:layer-type :point}]
                 :frame []        ; typo: singular
                 :shared-scales #{:x}}))   ; typo: extra 'd'
          msg (str warnings)]
      (is (re-find #":frame" msg))
      (is (re-find #":shared-scales" msg))
      (is (contains? fr :frame)
          "unknown keys are warned but not stripped (might be extensions)"))))
