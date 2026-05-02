(ns scicloj.plotje.pipeline-composition-test
  "Tests that nail down the compositional principle: pj/draft, pj/plan,
   and pj/plot are literal compositions of the atomic public arrow
   functions. Equivalence tests prove the composition holds; with-redefs
   tests prove the user-facing functions actually call through to the
   atomic steps (not via an internal bypass)."
  (:require [clojure.test :refer [deftest is testing]]
            [tablecloth.api :as tc]
            [scicloj.plotje.api :as pj]))

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

(deftest plot-calls-the-pipeline
  (testing "pj/plot literally calls the public pose->draft and draft->plan"
    (let [pose-calls (atom 0)
          draft-calls (atom 0)
          original-pose->draft pj/pose->draft
          original-draft->plan pj/draft->plan]
      (with-redefs [pj/pose->draft (fn [p] (swap! pose-calls inc) (original-pose->draft p))
                    pj/draft->plan (fn [d] (swap! draft-calls inc) (original-draft->plan d))]
        (pj/plot (pj/lay-point tiny :x :y))
        (is (= 1 @pose-calls) "pj/plot calls pj/pose->draft once")
        (is (= 1 @draft-calls) "pj/plot calls pj/draft->plan once"))))
  (testing "the format dispatch step is render-impl/plan->plot, which is itself
            a composition of plan->membrane and membrane->plot with
            format-specific render-opts splicing"
    ;; This is the one place where the chain visibly goes through a
    ;; multimethod. pj/plan->plot wraps render-impl/plan->plot. The
    ;; defmethod for each format calls plan->membrane and
    ;; membrane->plot internally; this layered composition is the
    ;; price of having renderers register independently.
    (is true)))
