(ns scicloj.plotje.pose-api-test
  "Tests for the pj/pose public constructor, polymorphic dispatch,
   and composite-pose semantics (lay-*, options/scale/coord,
   prepare-pose)."
  (:require [clojure.test :refer [deftest testing is]]
            [tablecloth.api :as tc]
            [scicloj.plotje.api :as pj]))

(def tiny-ds
  (tc/dataset {:x [1.0 2.0 3.0 4.0 5.0]
               :y [2.0 4.0 1.0 5.0 3.0]
               :g ["a" "a" "b" "b" "b"]}))

;; ============================================================
;; pj/pose constructor shape
;; ============================================================

(deftest pose-constructor-test
  (testing "0-arity makes an empty leaf pose"
    (let [f (pj/pose)]
      (is (pj/pose? f))
      (is (nil? (:data f)))
      (is (not (contains? f :mapping)))
      (is (= [] (:layers f)))))

  (testing "1-arity coerces data and auto-infers mapping on 1-3 cols"
    (let [f (pj/pose {:x [1 2] :y [3 4]})]
      (is (pj/pose? f))
      (is (tc/dataset? (:data f)))
      (is (= {:x :x :y :y} (:mapping f))
          "two-column data infers {:x col0, :y col1}")))

  (testing "1-arity leaves mapping empty on 4+ cols (no ambiguous default)"
    (let [f (pj/pose {:a [1] :b [2] :c [3] :d [4]})]
      (is (pj/pose? f))
      (is (tc/dataset? (:data f)))
      (is (not (contains? f :mapping)))))

  (testing "2-arity with a map arg is an aesthetic mapping"
    (let [f (pj/pose tiny-ds {:color :g})]
      (is (= {:color :g} (:mapping f)))))

  (testing "2-arity with a keyword arg sets :x only"
    (let [f (pj/pose tiny-ds :x)]
      (is (= {:x :x} (:mapping f)))))

  (testing "3-arity sets both :x and :y"
    (let [f (pj/pose tiny-ds :x :y)]
      (is (= {:x :x :y :y} (:mapping f))))))

;; ============================================================
;; Few-column inference: parity with the pre-Phase-6 Sketch era,
;; where `sk/view` 1-arity and `sk/lay-* data` fresh-sketch paths
;; both inferred from the first 1-3 columns.
;; ============================================================

(deftest few-column-inference-test
  (testing "pj/pose 1-arity infers mapping from 1-3 columns"
    (is (= {:x :a} (:mapping (pj/pose {:a [1 2 3]}))))
    (is (= {:x :a :y :b} (:mapping (pj/pose {:a [1 2] :b [3 4]}))))
    (is (= {:x :a :y :b :color :c}
           (:mapping (pj/pose {:a [1] :b [2] :c ["x"]})))))

  (testing "threading through pj/pose preserves lay-* auto-infer"
    (let [raw      (pj/lay-point {:x [1 2] :y [3 4]})
          threaded (-> {:x [1 2] :y [3 4]} pj/pose pj/lay-point)]
      (is (= {:x :x :y :y} (:mapping threaded))
          "(-> data pj/pose pj/lay-point) matches (pj/lay-point data)")
      (is (= (:mapping raw) (:mapping threaded)))
      (is (= (:layers raw) (:layers threaded)))))

  (testing "4+ column lay-* still throws (no ambiguous default)"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"Cannot auto-infer columns from 4 columns"
         (pj/lay-point {:a [1] :b [2] :c [3] :d [4]})))))

;; ============================================================
;; Composite poses render via the Phase-4 compositor: each leaf
;; renders through the existing sketch pipeline and is tiled via
;; <g transform="translate(...)"> groups inside a wrapping SVG.
;; ============================================================

(deftest composite-renders-to-svg-test
  (testing "pj/plot on a two-leaf horizontal composite returns an SVG
            whose svg-summary shows two panels"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :horizontal :weights [1 1]}
                     :poses [{:layers [{:layer-type :point}]}
                             {:layers [{:layer-type :line}]}]}
          svg (pj/plot composite)
          summary (pj/svg-summary svg)]
      (is (= :svg (first svg)))
      (is (= 2 (:panels summary))
          "one panel per leaf")
      (is (pos? (:points summary))
          "point leaf rendered")
      (is (pos? (:lines summary))
          "line leaf rendered"))))

(deftest svg-summary-auto-renders-poses-test
  (testing "svg-summary accepts a leaf-pose plain map and auto-renders"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          leaf (pj/pose ds :x :y)
          summary (pj/svg-summary leaf)]
      (is (= 1 (:panels summary)))
      (is (pos? (:points summary))
          "inferred scatter points rendered")))

  (testing "svg-summary accepts a composite pose and auto-renders"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :horizontal :weights [1 1]}
                     :poses [{:layers [{:layer-type :point}]}
                             {:layers [{:layer-type :line}]}]}
          summary (pj/svg-summary composite)]
      (is (= 2 (:panels summary)))
      (is (pos? (:points summary)))
      (is (pos? (:lines summary))))))

(deftest composite-renders-vertical-test
  (testing "vertical layout produces stacked leaves"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :vertical :weights [1 1]}
                     :poses [{:layers [{:layer-type :point}]}
                             {:layers [{:layer-type :point}]}]}
          svg (pj/plot composite)]
      (is (= 2 (:panels (pj/svg-summary svg)))))))

(deftest composite-renders-nested-test
  (testing "nested composite (horizontal outer, vertical inner) renders
            every descendant leaf"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :horizontal :weights [1 1]}
                     :poses [{:layers [{:layer-type :point}]}
                             {:layout {:direction :vertical :weights [1 1]}
                              :poses [{:layers [{:layer-type :point}]}
                                      {:layers [{:layer-type :point}]}]}]}
          svg (pj/plot composite)]
      (is (= 3 (:panels (pj/svg-summary svg)))
          "three leaves -> three panels"))))

;; ============================================================
;; Composite plans (Phase 4 Slice B)
;; ============================================================
;;
;; pj/plan on a composite returns a Plan record with :composite? true
;; and :sub-plots [{:path :rect :plan} ...] -- one entry per resolved
;; leaf. pj/plan? still returns true so downstream predicate checks
;; keep working.

(deftest composite-plan-shape-test
  (testing "pj/plan on a composite returns a composite plan"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :horizontal :weights [1 1]}
                     :poses [{:layers [{:layer-type :point}]}
                             {:layers [{:layer-type :line}]}]}
          p (pj/plan composite)]
      (is (pj/plan? p)
          "composite plan still passes plan? predicate")
      (is (:composite? p)
          ":composite? flag set")
      (is (= 2 (count (:sub-plots p)))
          "one :sub-plots entry per leaf")
      (is (every? pj/plan? (map :plan (:sub-plots p)))
          "each :plan entry is itself a plan")
      (is (every? (fn [sp] (= 4 (count (:rect sp)))) (:sub-plots p))
          "each entry carries a 4-tuple :rect"))))

(deftest composite-plan-rects-tile-pose-test
  (testing "horizontal composite sub-plot rects partition the outer width"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :opts {:width 600 :height 400}
                     :layout {:direction :horizontal :weights [1 1]}
                     :poses [{:layers [{:layer-type :point}]}
                             {:layers [{:layer-type :point}]}]}
          p (pj/plan composite)
          rects (map :rect (:sub-plots p))]
      (is (= [[0.0 0.0 300.0 400.0]
              [300.0 0.0 300.0 400.0]]
             (vec rects))
          "two leaves split 600-wide pose evenly left/right"))))

(deftest composite-plan-nested-paths-test
  (testing "paths in :sub-plots match the DFS order of leaves"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :horizontal :weights [1 1]}
                     :poses [{:layers [{:layer-type :point}]}
                             {:layout {:direction :vertical :weights [1 1]}
                              :poses [{:layers [{:layer-type :point}]}
                                      {:layers [{:layer-type :point}]}]}]}
          p (pj/plan composite)]
      (is (= [[0] [1 0] [1 1]]
             (mapv :path (:sub-plots p)))))))

;; ============================================================
;; Composite drafts (Slice 3 of the composite-pipeline-seam refactor)
;; ============================================================
;;
;; pj/draft on a composite returns a CompositeDraft -- a record with
;; :sub-drafts (one entry per resolved leaf, each carrying :path :rect
;; :draft :opts), :chrome-spec (the resolved chrome geometry inputs),
;; and :layout (path -> rect). Per-leaf drafts inside :sub-drafts are
;; contextualized: shared-scale domains have been injected and the
;; composite has applied any chrome-driven leaf-opt adjustments.

(deftest composite-draft-shape-test
  (testing "pj/draft on a composite returns a CompositeDraft"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :horizontal :weights [1 1]}
                     :poses [{:layers [{:layer-type :point}]}
                             {:layers [{:layer-type :line}]}]}
          d (pj/draft composite)]
      (is (= "scicloj.plotje.impl.resolve.CompositeDraft"
             (.getName (class d)))
          "draft is a CompositeDraft record")
      (is (= 2 (count (:sub-drafts d)))
          "one :sub-drafts entry per leaf")
      (is (every? (fn [sd] (every? (set (keys sd)) [:path :rect :draft :opts]))
                  (:sub-drafts d))
          "each sub-draft carries :path :rect :draft :opts")
      (is (every? vector? (map :draft (:sub-drafts d)))
          "each :draft is a vector of layer maps")
      (is (= [[0] [1]] (mapv :path (:sub-drafts d)))
          "DFS paths in order"))))

(deftest composite-draft-contextualizes-shared-scales-test
  (testing "shared-scale injection runs at draft emission, so per-leaf
            drafts inside :sub-drafts already carry forced :x-scale"
    (let [ds-a (tc/dataset {:x [0 1 2] :y [1 2 3]})
          ds-b (tc/dataset {:x [10 20 30] :y [1 2 3]})
          composite {:share-scales #{:x}
                     :layout {:direction :horizontal :weights [1 1]}
                     :poses [{:data ds-a :mapping {:x :x :y :y}
                              :layers [{:layer-type :point}]}
                             {:data ds-b :mapping {:x :x :y :y}
                              :layers [{:layer-type :point}]}]}
          d (pj/draft composite)
          ;; Each leaf's draft entries should now carry :x-scale with
          ;; the shared domain [0 30], not their own narrow domains.
          leaf0-x-scale (-> d :sub-drafts (get 0) :draft first :x-scale)
          leaf1-x-scale (-> d :sub-drafts (get 1) :draft first :x-scale)]
      (is (= leaf0-x-scale leaf1-x-scale)
          "both per-leaf drafts carry the same :x-scale")
      (is (= [0 30] (:domain leaf0-x-scale))
          "the shared domain is the union of each leaf's data range"))))

(deftest composite-plan-validates-test
  (testing "pj/valid-plan? returns true for a CompositePlan;
            pj/explain-plan returns nil"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :horizontal :weights [1 1]}
                     :poses [{:layers [{:layer-type :point}]}
                             {:layers [{:layer-type :line}]}]}
          p (pj/plan composite)]
      (is (pj/valid-plan? p)
          "a well-formed composite plan validates")
      (is (nil? (pj/explain-plan p))
          "explain-plan returns nil for a valid composite plan")))
  (testing "schema rejects a malformed composite plan"
    (let [ds (tc/dataset {:x [1 2 3] :y [1 2 3]})
          composite {:data ds
                     :mapping {:x :x :y :y}
                     :layout {:direction :horizontal :weights [1 1]}
                     :poses [{:layers [{:layer-type :point}]}
                             {:layers [{:layer-type :point}]}]}
          p (pj/plan composite)
          bad (assoc p :width "not-a-number")]
      (is (false? (pj/valid-plan? bad))
          "string width is rejected")
      (is (some? (pj/explain-plan bad))
          "explain-plan surfaces the error"))))

;; ============================================================
;; Composite shared scales (Phase 4 Slice C)
;; ============================================================
;;
;; :share-scales #{:x} on a composite pins an identical x-domain
;; across sibling leaves that use the same x-column. Leaves with a
;; different x-column get their own bucket (column-bucketing).
;;
;; :share-scales canonically lives in :opts (set via pj/options or
;; pj/arrange). The compositor also reads it from the legacy
;; top-level location for hand-built composites.

(deftest share-scales-via-pj-options-test
  (testing "(pj/options composite {:share-scales #{:x}}) routes through :opts and is honored"
    (let [ds1 (tc/dataset {:x [0 1 2] :y [1 2 3]})
          ds2 (tc/dataset {:x [10 20 30] :y [1 2 3]})
          composite (-> (pj/arrange [(-> ds1 (pj/lay-point :x :y))
                                     (-> ds2 (pj/lay-point :x :y))])
                        (pj/options {:share-scales #{:x}}))]
      (is (= #{:x} (get-in composite [:opts :share-scales]))
          ":share-scales was routed into :opts")
      (let [p (pj/plan composite)
            dom0 (-> p :sub-plots (get 0) :plan :panels first :x-domain)
            dom1 (-> p :sub-plots (get 1) :plan :panels first :x-domain)]
        (is (= dom0 dom1)
            "compositor honored :share-scales from :opts")
        (is (= [0 30] dom0))))))

(deftest share-scales-back-compat-top-level-test
  (testing "hand-built composites with :share-scales at top-level still work"
    (let [ds1 (tc/dataset {:x [0 1 2] :y [1 2 3]})
          ds2 (tc/dataset {:x [10 20 30] :y [1 2 3]})
          composite {:share-scales #{:x}     ;; legacy top-level location
                     :layout {:direction :horizontal :weights [1 1]}
                     :poses [{:data ds1 :mapping {:x :x :y :y}
                              :layers [{:layer-type :point}]}
                             {:data ds2 :mapping {:x :x :y :y}
                              :layers [{:layer-type :point}]}]}
          p (pj/plan composite)
          dom0 (-> p :sub-plots (get 0) :plan :panels first :x-domain)
          dom1 (-> p :sub-plots (get 1) :plan :panels first :x-domain)]
      (is (= dom0 dom1) "top-level :share-scales still honored"))))

(deftest share-scales-opts-wins-over-top-level-test
  (testing "when both locations are set, :opts wins"
    (let [ds (tc/dataset {:x [0 1 2] :y [1 2 3]})
          composite {:share-scales #{:x}     ;; legacy
                     :opts {:share-scales #{:y}}  ;; canonical
                     :layout {:direction :horizontal :weights [1 1]}
                     :poses [{:data ds :mapping {:x :x :y :y}
                              :layers [{:layer-type :point}]}
                             {:data ds :mapping {:x :x :y :y}
                              :layers [{:layer-type :point}]}]}
          ;; If :opts wins, :y is shared but :x isn't (and there's
          ;; only one bucket each anyway). The asserts below would
          ;; both be true under either interpretation here, since
          ;; both leaves use the same data; the structural check is
          ;; that pj/plan does not throw and the plan is well-formed.
          p (pj/plan composite)]
      (is (= 2 (count (:sub-plots p)))
          "both locations resolve cleanly; pj/plan does not throw"))))

(deftest composite-share-scales-x-same-column-test
  (testing "two leaves sharing :x column get a unified x-domain"
    (let [ds1 (tc/dataset {:x [0 1 2] :y [1 2 3]})
          ds2 (tc/dataset {:x [10 20 30] :y [1 2 3]})
          composite {:mapping {:y :y}
                     :share-scales #{:x}
                     :layout {:direction :horizontal :weights [1 1]}
                     :poses [{:data ds1 :mapping {:x :x}
                              :layers [{:layer-type :point}]}
                             {:data ds2 :mapping {:x :x}
                              :layers [{:layer-type :point}]}]}
          p (pj/plan composite)
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
                     :poses [{:data ds-a :mapping {:x :a}
                              :layers [{:layer-type :point}]}
                             {:data ds-b :mapping {:x :b}
                              :layers [{:layer-type :point}]}]}
          p (pj/plan composite)
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
                     :poses [;; top density on :a
                             {:mapping {:x :a}
                              :layers [{:layer-type :density}]}
                              ;; bottom row: scatter (a,b) + density on b (bucket-of-one)
                             {:layout {:direction :horizontal :weights [3 1]}
                              :poses [{:mapping {:x :a :y :b}
                                       :layers [{:layer-type :point}]}
                                      {:mapping {:x :b}
                                       :layers [{:layer-type :density}]}]}]}
          p (pj/plan composite)
          sub (:sub-plots p)
          top-density-x (-> sub (get 0) :plan :panels first :x-domain)
          scatter-x     (-> sub (get 1) :plan :panels first :x-domain)]
      (is (= top-density-x scatter-x)
          "top density and scatter share x-domain (same :a column)"))))

;; ============================================================
;; Composite lay-* identity (Phase 3b)
;; ============================================================
;;
;; The rule extends the Layer Placement section of pose_rules.clj
;; into composite poses: position-carrying lay-* targets the last leaf
;; (DFS) whose effective :x/:y match, else appends a new leaf at the
;; root level. Aesthetic-only or bare
;; lay-* attaches at the root's :layers so descendants inherit it.

(deftest composite-lay-hit-last-matching-leaf-test
  (testing "two leaves with identical position mapping -- layer attaches to the last"
    (let [fr {:poses [{:layers [] :mapping {:x :x :y :y}}
                      {:layers [] :mapping {:x :x :y :y}}]}
          result (pj/lay-point fr :x :y)]
      (is (empty? (get-in result [:poses 0 :layers]))
          "first leaf stays empty")
      (is (= 1 (count (get-in result [:poses 1 :layers])))
          "layer lands on the last matching leaf (Rule 6)")
      (is (= :point (get-in result [:poses 1 :layers 0 :layer-type]))))))

(deftest composite-lay-miss-creates-root-leaf-test
  (testing "no matching leaf -- new leaf appended at root :poses"
    (let [fr {:poses [{:layers [] :mapping {:x :c :y :d}}]}
          result (pj/lay-point fr :a :b)]
      (is (= 2 (count (:poses result))))
      (is (= {:x :c :y :d} (get-in result [:poses 0 :mapping]))
          "existing leaf is preserved as-is")
      (is (= {:x :a :y :b} (get-in result [:poses 1 :mapping]))
          "new leaf carries the lay-* position mapping")
      (is (= :point (get-in result [:poses 1 :layers 0 :layer-type]))))))

(deftest composite-lay-miss-on-empty-poses-test
  (testing "composite with empty :poses -- lay-* extends the leaf in place"
    (let [fr {:poses []}
          result (pj/lay-point fr :a :b)]
      ;; An empty :poses vector counts as a leaf per impl.pose/leaf?.
      ;; Post-Phase-6, such a leaf stays in pose-world: the position
      ;; call extends the leaf's :mapping and appends a bare layer.
      (is (pj/pose? result))
      (is (= {:x :a :y :b} (:mapping result)))
      (is (= 1 (count (:layers result))))
      (is (= :point (-> result :layers (nth 0) :layer-type))))))

(deftest composite-lay-sketch-level-test
  (testing "bare lay-* on a composite attaches at the root :layers"
    (let [fr {:poses [{:layers [] :mapping {:x :x :y :y}}]}
          result (pj/lay-point fr {:color :g})]
      (is (= 1 (count (:layers result)))
          "layer added at root")
      (is (= :point (get-in result [:layers 0 :layer-type])))
      (is (= {:color :g} (get-in result [:layers 0 :mapping])))
      (is (empty? (get-in result [:poses 0 :layers]))
          "leaf was not modified"))))

(deftest composite-lay-sketch-level-no-opts-test
  (testing "bare lay-* with no opts still attaches at root :layers"
    (let [fr {:poses [{:layers [] :mapping {:x :x :y :y}}
                      {:layers [] :mapping {:x :x :y :y}}]}
          result (pj/lay-point fr)]
      (is (= 1 (count (:layers result))))
      (is (every? #(empty? (:layers %)) (:poses result))
          "no leaf was modified -- the layer is sketch-level"))))

(deftest composite-lay-keyword-string-tolerance-test
  (testing "lay-* :x-kw :y-kw matches leaf with string-named mapping"
    (let [fr {:poses [{:layers [] :mapping {:x "x" :y "y"}}]}
          result (pj/lay-point fr :x :y)]
      (is (= 1 (count (get-in result [:poses 0 :layers])))
          "matched despite keyword vs string column-ref divergence"))))

(deftest composite-lay-ancestor-mapping-inherits-test
  (testing "ancestor :mapping with :x/:y makes a bare leaf matchable"
    (let [fr {:mapping {:x :x :y :y}
              :poses  [{:layers []}]}
          result (pj/lay-point fr :x :y)]
      (is (= 1 (count (get-in result [:poses 0 :layers])))
          "leaf inherited :x/:y from root and matched the lay-* call"))))

(deftest composite-lay-nested-deep-match-test
  (testing "DFS finds a match at arbitrary depth"
    (let [fr {:poses [{:layers [] :mapping {:x :a :y :b}}
                      {:poses [{:layers [] :mapping {:x :a :y :b}}]}]}
          result (pj/lay-point fr :a :b)]
      (is (empty? (get-in result [:poses 0 :layers]))
          "shallow match is not picked")
      (is (= 1 (count (get-in result [:poses 1 :poses 0 :layers])))
          "deep-last match gets the layer"))))

(deftest composite-lay-threading-respects-identity-test
  (testing "threading multiple lay-* on a composite puts each on the matching leaf"
    (let [fr {:poses [{:layers [] :mapping {:x :a :y :b}}
                      {:layers [] :mapping {:x :a :y :b}}]}
          result (-> fr
                     (pj/lay-point :a :b)
                     (pj/lay-line  :a :b))]
      (is (empty? (get-in result [:poses 0 :layers]))
          "first leaf never matches last -- untouched")
      (is (= [:point :line]
             (mapv :layer-type (get-in result [:poses 1 :layers])))
          "both layers accumulate on the last-matching leaf"))))

(deftest composite-lay-rule5-two-poses-different-layers-test
  (testing "scatter on leaf 0, view created, then smooth -- ends up on leaf 1"
    ;; DFS-last identity applied twice: starting from a composite with
    ;; two same-position leaves, one lay-* lands on each in turn.
    (let [fr {:poses [{:layers [{:layer-type :point} {:layer-type :placeholder}]
                       :mapping {:x :x :y :y}}
                      {:layers [] :mapping {:x :x :y :y}}]}
          result (pj/lay-smooth fr :x :y {:stat :linear-model})]
      (is (= [:point :placeholder]
             (mapv :layer-type (get-in result [:poses 0 :layers])))
          "pre-existing leaf is unchanged")
      (is (= :smooth (get-in result [:poses 1 :layers 0 :layer-type]))
          "new layer lands on the last matching leaf"))))

(deftest composite-lay-preserves-non-matching-leaves-test
  (testing "miss on a composite with a non-matching leaf preserves the leaf"
    (let [fr {:poses [{:layers [{:layer-type :histogram}]
                       :mapping {:x :other}}]}
          result (pj/lay-point fr :a :b)]
      (is (= 2 (count (:poses result))))
      (is (= :histogram (get-in result [:poses 0 :layers 0 :layer-type]))
          "existing leaf untouched")
      (is (= :point (get-in result [:poses 1 :layers 0 :layer-type]))
          "miss appended a new leaf at root"))))

(deftest composite-lay-returns-composite-test
  (testing "the result of lay-* on a composite remains a composite pose"
    (let [fr {:poses [{:layers [] :mapping {:x :a :y :b}}]}
          result (pj/lay-point fr :a :b)]
      (is (pj/pose? result)))))

;; ============================================================
;; Composite options / scale / coord (Phase 3b)
;; ============================================================
;;
;; On a composite, these three all write to the root :opts.
;; resolve-tree merges root :opts into every descendant leaf, so
;; writing at the root is the pose-world analog of plot-level
;; options on a sketch.

(deftest composite-options-writes-root-opts-test
  (testing "pj/options on a composite writes to root :opts"
    (let [fr {:poses [{:layers [] :mapping {:x :a :y :b}}]}
          result (pj/options fr {:title "hi" :width 500})]
      (is (pj/pose? result))
      (is (= "hi" (get-in result [:opts :title])))
      (is (= 500 (get-in result [:opts :width])))
      (is (empty? (get-in result [:poses 0 :opts] {}))
          "leaf :opts is untouched"))))

(deftest composite-options-deep-merges-existing-opts-test
  (testing "pj/options deep-merges into an existing root :opts"
    (let [fr     {:opts   {:theme {:bg "black"}}
                  :poses [{:layers []}]}
          result (pj/options fr {:theme {:fg "white"}})]
      (is (= {:bg "black" :fg "white"}
             (get-in result [:opts :theme]))
          "nested map is merged rather than replaced"))))

(deftest composite-options-width-height-coerced-test
  (testing "width/height still get long-coerced on composites"
    (let [fr {:poses [{:layers []}]}
          result (pj/options fr {:width 500.7 :height 299.3})]
      (is (= 501 (get-in result [:opts :width])))
      (is (= 299 (get-in result [:opts :height]))))))

(deftest composite-scale-writes-root-opts-test
  (testing "pj/scale on a composite writes :x-scale at the root"
    (let [fr {:poses [{:layers [] :mapping {:x :a :y :b}}]}
          result (pj/scale fr :x :log)]
      (is (pj/pose? result))
      (is (= {:type :log} (get-in result [:opts :x-scale]))))))

(deftest composite-scale-accepts-map-spec-test
  (testing "pj/scale with a map scale-type fills in :linear as default :type"
    (let [fr {:poses [{:layers []}]}
          result (pj/scale fr :y {:breaks [0 5 10]})]
      (is (= {:type :linear :breaks [0 5 10]}
             (get-in result [:opts :y-scale]))))))

(deftest composite-coord-writes-root-opts-test
  (testing "pj/coord on a composite writes :coord at the root"
    (let [fr {:poses [{:layers []}]}
          result (pj/coord fr :polar)]
      (is (pj/pose? result))
      (is (= :polar (get-in result [:opts :coord]))))))

(deftest composite-options-preserves-poses-test
  (testing "options/scale/coord leave the :poses subtree intact"
    (let [fr {:poses [{:layers [{:layer-type :point}] :mapping {:x :a}}
                      {:layers [] :mapping {:x :b}}]}
          opt-result (pj/options fr {:title "x"})
          sc-result  (pj/scale fr :x :log)
          co-result  (pj/coord fr :flip)]
      (doseq [r [opt-result sc-result co-result]]
        (is (= (:poses fr) (:poses r))
            "poses subtree is untouched")))))

(deftest composite-options-chainable-test
  (testing "options/scale/coord chain on composites with each other and with lay-*"
    (let [fr {:poses [{:layers [] :mapping {:x :a :y :b}}]}
          result (-> fr
                     (pj/options {:title "chart"})
                     (pj/scale :x :log)
                     (pj/coord :polar)
                     (pj/lay-point :a :b))]
      (is (pj/pose? result))
      (is (= "chart" (get-in result [:opts :title])))
      (is (= {:type :log} (get-in result [:opts :x-scale])))
      (is (= :polar (get-in result [:opts :coord])))
      (is (= 1 (count (get-in result [:poses 0 :layers])))
          "lay-* still landed on the one matching leaf"))))

;; ============================================================
;; pj/pose 4-arity: positional x/y + opts map
;; ============================================================

(deftest pose-4-arity-basic-test
  (testing "4-arity constructs a leaf with :x :y and opts in mapping"
    (let [fr (pj/pose tiny-ds :x :y {:color :g})]
      (is (pj/pose? fr))
      (is (= {:x :x :y :y :color :g} (:mapping fr)))
      (is (tc/dataset? (:data fr))))))

(deftest pose-4-arity-positional-x-wins-test
  (testing "positional x/y override same keys in opts map"
    (let [fr (pj/pose tiny-ds :x :y {:x :override :color :g})]
      (is (= :x (get-in fr [:mapping :x])) "positional :x wins")
      (is (= :y (get-in fr [:mapping :y])))
      (is (= :g (get-in fr [:mapping :color]))))))

(deftest pose-4-arity-opts-data-wins-test
  (testing "opts' :data overrides the positional data"
    (let [other-ds (tc/dataset {:x [10 20] :y [30 40]})
          fr (pj/pose tiny-ds :x :y {:data other-ds})]
      (is (= 2 (tc/row-count (:data fr))) "opts :data wins; 2 rows, not 5")
      ;; And :data does not leak into :mapping
      (is (not (contains? (:mapping fr) :data))))))

(deftest pose-4-arity-unknown-key-warns-test
  (testing "unknown mapping key in opts triggers a warning and is stripped"
    (let [warnings (java.io.StringWriter.)
          fr (binding [*out* warnings]
               (pj/pose tiny-ds :x :y {:bogus :nope :color :g}))
          msg (str warnings)]
      (is (re-find #"pj/pose.*bogus" msg) "warning mentions the stripped key")
      (is (not (contains? (:mapping fr) :bogus)) "bogus key stripped from mapping")
      (is (= :g (get-in fr [:mapping :color])) "good keys survive"))))

;; ============================================================
;; pj/prepare-pose: promote hand-built pose maps
;; ============================================================

(deftest prepare-pose-attaches-kindly-test
  (testing "leaf map: prepare-pose attaches :kind/fn metadata"
    (let [fr (pj/prepare-pose
              {:data {:x [1 2 3] :y [4 5 6]}
               :mapping {:x :x :y :y}
               :layers [{:layer-type :point}]})]
      (is (pj/pose? fr))
      (is (= :kind/fn (:kindly/kind (meta fr))))
      (is (tc/dataset? (:data fr)) "data coerced to dataset")))

  (testing "composite map: prepare-pose attaches :kind/fn metadata"
    (let [ds (tc/dataset {:x [1 2 3] :y [4 5 6]})
          fr (pj/prepare-pose
              {:data ds
               :layout {:direction :horizontal}
               :poses [{:mapping {:x :x :y :y}
                        :layers [{:layer-type :point}]}
                       {:mapping {:x :y :y :x}
                        :layers [{:layer-type :line}]}]})]
      (is (= :kind/fn (:kindly/kind (meta fr))))
      (is (= 2 (:panels (pj/svg-summary fr)))))))

(deftest prepare-pose-coerces-data-recursively-test
  (testing ":data on nested sub-poses is coerced to a dataset"
    (let [fr (pj/prepare-pose
              {:layout {:direction :horizontal}
               :poses [{:data {:a [1 2] :b [3 4]}
                        :mapping {:x :a :y :b}
                        :layers [{:layer-type :point}]}
                       {:data {:c [10 20] :d [30 40]}
                        :mapping {:x :c :y :d}
                        :layers [{:layer-type :point}]}]})]
      (is (every? tc/dataset?
                  (map :data (:poses fr)))
          "each sub-pose's :data is a tablecloth dataset"))))

(deftest prepare-pose-key-order-test
  (testing "reorder places :data before :poses; :poses last"
    (let [ds (tc/dataset {:x [1 2] :y [3 4]})
          composite (pj/prepare-pose
                     {:data ds
                      :layout {:direction :horizontal}
                      :poses [{:mapping {:x :x :y :y}
                               :layers [{:layer-type :point}]}]})]
      (is (= [:layout :data :poses] (vec (keys composite)))
          "outer keys print in readable order")))

  (testing "leaf: :data goes last (no :poses to precede)"
    (let [leaf (pj/prepare-pose
                {:data {:x [1 2] :y [3 4]}
                 :mapping {:x :x :y :y}
                 :layers [{:layer-type :point}]
                 :opts {:title "t"}})]
      (is (= [:opts :mapping :layers :data] (vec (keys leaf)))))))

(deftest prepare-pose-idempotent-test
  (testing "wrapping twice yields a value with the same rendered SVG"
    (let [m0 {:data {:x [1 2 3] :y [4 5 6]}
              :mapping {:x :x :y :y}
              :layers [{:layer-type :point}]}
          m1 (pj/prepare-pose m0)
          m2 (pj/prepare-pose m1)]
      (is (= :kind/fn (:kindly/kind (meta m2))))
      (is (= (pj/svg-summary m1) (pj/svg-summary m2)))
      (is (tc/dataset? (:data m2)) "still coerced after double pass"))))

(deftest prepare-pose-outer-layers-distribute-test
  (testing "an outer-scope :layer on a composite renders in every leaf"
    (let [ds (tc/dataset {:x [1 2 3] :a [4 5 6] :b [7 8 9]})
          fr (pj/prepare-pose
              {:data ds
               :mapping {:x :x}
               :layers [{:layer-type :point}]
               :poses [{:mapping {:y :a}}
                       {:mapping {:y :b}}]})
          summary (pj/svg-summary fr)]
      (is (= 2 (:panels summary)))
      (is (= 6 (:points summary))
          "3 points per leaf x 2 leaves = 6; outer :layers distributes"))))

(deftest prepare-pose-warns-unknown-keys-test
  (testing ":pose (singular) and other typos trigger a warning"
    (let [warnings (java.io.StringWriter.)
          fr (binding [*out* warnings]
               (pj/prepare-pose
                {:data {:x [1 2] :y [3 4]}
                 :mapping {:x :x :y :y}
                 :layers [{:layer-type :point}]
                 :pose []        ; typo: singular
                 :shared-scales #{:x}}))   ; typo: extra 'd'
          msg (str warnings)]
      (is (re-find #":pose" msg))
      (is (re-find #":shared-scales" msg))
      (is (contains? fr :pose)
          "unknown keys are warned but not stripped (might be extensions)"))))
