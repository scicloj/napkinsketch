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
      (is (= {} (:mapping f)))
      (is (= [] (:layers f)))))

  (testing "1-arity coerces data, no mapping"
    (let [f (sk/frame {:x [1 2] :y [3 4]})]
      (is (sk/frame? f))
      (is (tc/dataset? (:data f)))
      (is (= {} (:mapping f)))))

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
;; Composite frames are not yet rendered (Phase 4); sk/plot rejects
;; them with a helpful message. Layer addition (this phase) is the
;; structural half -- tested below against literal composite maps,
;; since no composite-producing constructor exists yet.
;; ============================================================

(deftest composite-rejects-test
  (testing "sk/plot on a composite frame throws a clear Phase-4 error"
    (let [composite {:frames [{:layers []} {:layers []}]}]
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Composite frames"
                            (sk/plot composite))))))

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
  (testing "composite with empty :frames -- miss creates a root leaf"
    (let [fr {:frames []}
          result (sk/lay-point fr :a :b)]
      ;; An empty :frames vector counts as a leaf per impl.frame/leaf?,
      ;; so the dispatch should take the non-composite path and behave
      ;; like a data-less sketch here.
      (is (sk/sketch? result)
          "empty-frames input is treated as a leaf and routed through ensure-sk"))))

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
