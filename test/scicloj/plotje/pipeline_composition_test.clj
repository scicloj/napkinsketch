(ns scicloj.plotje.pipeline-composition-test
  "Tests that nail down the compositional principle: pj/draft, pj/plan,
   and pj/plot are literal compositions of the atomic public arrow
   functions. Equivalence tests prove the composition holds; with-redefs
   tests prove the user-facing functions actually call through to the
   atomic steps (not via an internal bypass)."
  (:require [clojure.test :refer [deftest is testing]]
            [tablecloth.api :as tc]
            [scicloj.plotje.api :as pj]
            [scicloj.plotje.render.membrane :as membrane]))

(def tiny (tc/dataset {:x [1 2 3] :y [4 5 6]}))

;; ============================================================
;; ->pose: polymorphic and idempotent
;; ============================================================

(deftest ->pose-accepts-data-and-pose
  (testing "raw data becomes a leaf pose with :data set"
    (let [p (pj/->pose tiny)]
      (is (pj/pose? p))
      (is (some? (:data p)))
      (is (empty? (:layers p)))))
  (testing "an existing pose passes through"
    (let [pose (pj/lay-point tiny :x :y)
          lifted (pj/->pose pose)]
      (is (pj/pose? lifted))
      (is (= (:layers pose) (:layers lifted))))))

(deftest ->pose-idempotent
  (testing "lifting twice equals lifting once"
    (let [once (pj/->pose tiny)
          twice (pj/->pose once)]
      (is (= (:data once) (:data twice)))
      (is (= (:layers once) (:layers twice))))))

;; ============================================================
;; pose->draft: shape dispatch
;; ============================================================

(deftest pose->draft-leaf-returns-LeafDraft
  (let [pose (pj/lay-point tiny :x :y)
        draft (pj/pose->draft pose)]
    (is (pj/leaf-draft? draft))
    (is (vector? (:layers draft)))
    (is (map? (:opts draft)))))

(deftest pose->draft-composite-returns-CompositeDraft
  (let [composite (pj/arrange [(pj/lay-point tiny :x :y)
                               (pj/lay-line tiny :x :y)])
        draft (pj/pose->draft composite)]
    (is (pj/composite-draft? draft))))

(deftest pose->draft-rejects-non-pose
  (is (thrown-with-msg? Exception #"expects a pose"
                        (pj/pose->draft "not a pose"))))

;; ============================================================
;; LeafDraft carries pose-level opts
;; ============================================================

(deftest leaf-draft-carries-opts
  (let [pose (-> (pj/lay-point tiny :x :y)
                 (pj/options {:title "T" :x-label "X" :width 700}))
        draft (pj/pose->draft pose)]
    (is (pj/leaf-draft? draft))
    (is (= "T" (:title (:opts draft))))
    (is (= "X" (:x-label (:opts draft))))
    (is (= 700 (:width (:opts draft))))))

;; ============================================================
;; pj/plan = ->pose ; pose->draft ; draft->plan
;; ============================================================

(deftest plan-equals-arrow-composition-no-opts
  (testing "no opts: pj/plan agrees with the literal arrow chain"
    (let [pose (pj/lay-point tiny :x :y)
          via-public (pj/plan pose)
          via-arrows (-> pose pj/->pose pj/pose->draft pj/draft->plan)]
      (is (= (:total-width via-public) (:total-width via-arrows)))
      (is (= (:total-height via-public) (:total-height via-arrows)))
      (is (= (count (:panels via-public)) (count (:panels via-arrows)))))))

(deftest plan-equals-arrow-composition-with-pose-opts
  (testing "opts on the pose: pj/plan agrees with the literal arrow chain"
    (let [pose (-> (pj/lay-point tiny :x :y)
                   (pj/options {:title "T" :x-label "Xax" :width 700}))
          via-public (pj/plan pose)
          via-arrows (-> pose pj/->pose pj/pose->draft pj/draft->plan)]
      (is (= "T" (:title via-public)) "title flowed through public path")
      (is (= "T" (:title via-arrows)) "title flowed through arrows path")
      (is (= "Xax" (:x-label via-public)))
      (is (= "Xax" (:x-label via-arrows)))
      (is (= 700 (:width via-public)))
      (is (= 700 (:width via-arrows))))))

(deftest plan-2arity-equals-arrow-composition
  (testing "pj/plan with opts arg: equals threading through pj/options"
    (let [pose (pj/lay-point tiny :x :y)
          opts {:title "T2" :width 800}
          via-public (pj/plan pose opts)
          via-arrows (-> pose pj/->pose (pj/options opts) pj/pose->draft pj/draft->plan)]
      (is (= (:title via-public) (:title via-arrows)))
      (is (= (:width via-public) (:width via-arrows))))))

;; ============================================================
;; pj/draft = ->pose ; pose->draft (with optional pj/options)
;; ============================================================

(deftest draft-equals-arrow-composition
  (testing "1-arity"
    (let [pose (pj/lay-point tiny :x :y)
          via-public (pj/draft pose)
          via-arrows (-> pose pj/->pose pj/pose->draft)]
      (is (pj/leaf-draft? via-public))
      (is (pj/leaf-draft? via-arrows))
      (is (= (:layers via-public) (:layers via-arrows)))
      (is (= (:opts via-public) (:opts via-arrows)))))
  (testing "2-arity threads through pj/options"
    (let [pose (pj/lay-point tiny :x :y)
          opts {:title "Hi"}
          via-public (pj/draft pose opts)
          via-arrows (-> pose pj/->pose (pj/options opts) pj/pose->draft)]
      (is (= "Hi" (:title (:opts via-public))))
      (is (= "Hi" (:title (:opts via-arrows)))))))

;; ============================================================
;; with-redefs: pj/plan calls the public arrow functions
;; ============================================================
;;
;; This proves the compositional structure holds at runtime, not just
;; in the source. If pj/plan ever bypasses the public arrow functions
;; via a private shortcut, these tests fail.

(deftest plan-calls-public-pose->draft
  (let [calls (atom 0)
        original pj/pose->draft]
    (with-redefs [pj/pose->draft (fn [pose] (swap! calls inc) (original pose))]
      (pj/plan (pj/lay-point tiny :x :y))
      (is (= 1 @calls)
          "pj/plan calls pj/pose->draft once for a leaf pose"))))

(deftest plan-calls-public-draft->plan
  (let [calls (atom 0)
        original pj/draft->plan]
    (with-redefs [pj/draft->plan (fn [d] (swap! calls inc) (original d))]
      (pj/plan (pj/lay-point tiny :x :y))
      (is (= 1 @calls)
          "pj/plan calls pj/draft->plan once for a leaf pose"))))

(deftest draft-calls-public-pose->draft
  (let [calls (atom 0)
        original pj/pose->draft]
    (with-redefs [pj/pose->draft (fn [pose] (swap! calls inc) (original pose))]
      (pj/draft (pj/lay-point tiny :x :y))
      (is (= 1 @calls)
          "pj/draft calls pj/pose->draft once"))))

(deftest membrane-equals-arrow-composition
  (testing "pj/membrane = (-> pose ->pose pose->draft draft->plan (plan->membrane opts))"
    (let [pose (-> (pj/lay-point tiny :x :y)
                   (pj/options {:title "T" :width 700}))
          via-public (pj/membrane pose)
          via-arrows (-> pose
                         pj/->pose
                         pj/pose->draft
                         pj/draft->plan
                         (pj/plan->membrane (:opts pose {})))]
      (is (vector? via-public))
      (is (vector? via-arrows))
      (is (= (:total-width (meta via-public))
             (:total-width (meta via-arrows)))
          "plan-derived dimensions agree on both paths")
      (is (= "T" (:title (meta via-public)))
          "title rides on the membrane meta from the pose's :opts"))))

(deftest membrane-calls-the-pipeline
  (testing "pj/membrane literally calls the public atomic steps up through plan->membrane"
    (let [pose-draft-calls (atom 0)
          draft-plan-calls (atom 0)
          plan-membrane-calls (atom 0)
          orig-pose->draft pj/pose->draft
          orig-draft->plan pj/draft->plan
          orig-plan->membrane pj/plan->membrane]
      (with-redefs [pj/pose->draft   (fn [p] (swap! pose-draft-calls inc) (orig-pose->draft p))
                    pj/draft->plan   (fn [d] (swap! draft-plan-calls inc) (orig-draft->plan d))
                    pj/plan->membrane (fn
                                        ([p] (swap! plan-membrane-calls inc) (orig-plan->membrane p))
                                        ([p o] (swap! plan-membrane-calls inc) (orig-plan->membrane p o)))]
        (pj/membrane (pj/lay-point tiny :x :y))
        (is (= 1 @pose-draft-calls) "pj/membrane calls pj/pose->draft once")
        (is (= 1 @draft-plan-calls) "pj/membrane calls pj/draft->plan once")
        (is (= 1 @plan-membrane-calls) "pj/membrane calls pj/plan->membrane once")))))

(deftest plot-calls-all-five-atomic-steps
  (testing "pj/plot literally calls each of the five public atomic steps"
    (let [pose-draft-calls (atom 0)
          draft-plan-calls (atom 0)
          plan-membrane-calls (atom 0)
          membrane-plot-calls (atom 0)
          orig-pose->draft pj/pose->draft
          orig-draft->plan pj/draft->plan
          orig-plan->membrane pj/plan->membrane
          orig-membrane->plot pj/membrane->plot]
      (with-redefs [pj/pose->draft   (fn [p] (swap! pose-draft-calls inc) (orig-pose->draft p))
                    pj/draft->plan   (fn [d] (swap! draft-plan-calls inc) (orig-draft->plan d))
                    pj/plan->membrane (fn
                                        ([p] (swap! plan-membrane-calls inc) (orig-plan->membrane p))
                                        ([p o] (swap! plan-membrane-calls inc) (orig-plan->membrane p o)))
                    pj/membrane->plot (fn [m fmt o] (swap! membrane-plot-calls inc) (orig-membrane->plot m fmt o))]
        (pj/plot (pj/lay-point tiny :x :y))
        (is (= 1 @pose-draft-calls) "pj/plot calls pj/pose->draft once")
        (is (= 1 @draft-plan-calls) "pj/plot calls pj/draft->plan once")
        (is (= 1 @plan-membrane-calls) "pj/plot calls pj/plan->membrane once")
        (is (= 1 @membrane-plot-calls) "pj/plot calls pj/membrane->plot once")))))

;; ============================================================
;; Cross-stage misuse guards
;; ============================================================
;;
;; Drafts and membrane vectors are user-observable but are not
;; poses. The public shortcuts must reject them with clean
;; "expects a pose, not a ..." messages -- not silently corrupt
;; (the LeafDraft case before this fix) or produce degenerate
;; output (the membrane case).

(deftest draft-rejects-draft-input
  (let [d (pj/draft (pj/lay-point tiny :x :y))]
    (is (thrown-with-msg? Exception #"expects a pose, not a draft"
                          (pj/draft d)))))

(deftest plan-rejects-draft-input
  (let [d (pj/draft (pj/lay-point tiny :x :y))]
    (is (thrown-with-msg? Exception #"expects a pose, not a draft"
                          (pj/plan d)))))

(deftest membrane-rejects-membrane-input
  (let [m (pj/membrane (pj/lay-point tiny :x :y))]
    (is (thrown-with-msg? Exception #"Membrane drawable tree"
                          (pj/membrane m)))))

(deftest plot-rejects-membrane-input
  (let [m (pj/membrane (pj/lay-point tiny :x :y))]
    (is (thrown-with-msg? Exception #"Membrane drawable tree"
                          (pj/plot m)))))

(deftest plan-rejects-membrane-input
  (let [m (pj/membrane (pj/lay-point tiny :x :y))]
    (is (thrown-with-msg? Exception #"Membrane drawable tree"
                          (pj/plan m)))))

;; ============================================================
;; Multimethod dispatch invariants
;; ============================================================
;;
;; `plan->membrane` must dispatch on a boolean derived from the
;; plan, not on the plan's class. Class dispatch leaks defrecord
;; classes across :reload of impl/resolve.clj (see the defmulti
;; docstring at render/membrane.clj for the full story). This test
;; catches a regression at CI time even if a contributor edits the
;; defmulti without reading the warning.

(deftest plan->membrane-uses-boolean-dispatch
  (testing "plan->membrane methods are keyed by booleans only"
    (let [ks (set (keys (methods membrane/plan->membrane)))]
      (is (every? boolean? ks)
          (str "plan->membrane multimethod must dispatch on booleans. "
               "Class-based dispatch leaks defrecord classes across "
               ":reload of impl/resolve.clj. See render/membrane.clj "
               "defmulti docstring. Got non-boolean keys: "
               (pr-str (remove boolean? ks))))
      (is (= #{false true} ks)
          "expected exactly two methods: leaf (false) and composite (true)"))))
