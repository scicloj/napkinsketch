(ns scicloj.plotje.input-validation-test
  "Tests for the four input-validation guards added to close silent-
   failure gaps: nil data, non-collection data, empty-pose save, and
   non-map last arg in pj/pose's 4-arity."
  (:require [clojure.test :refer [deftest testing is]]
            [scicloj.plotje.api :as pj]))

(def tiny {:x [1.0 2.0 3.0] :y [4.0 5.0 6.0]})

(deftest nil-data-throws
  (testing "(pj/pose nil) throws with helpful message"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"requires data, but got nil"
         (pj/pose nil))))

  (testing "(pj/lay-point nil :x :y) throws (via ensure-pose)"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"requires data, but got nil"
         (pj/lay-point nil :x :y))))

  (testing "(pj/options nil ...) throws (via ensure-pose)"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"requires data, but got nil"
         (pj/options nil {:title "x"}))))

  (testing "the empty-pose 0-arity is unaffected"
    (is (pj/pose? (pj/pose)))))

(deftest non-collection-data-throws
  (testing "(pj/pose 42) throws"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"requires data"
         (pj/pose 42))))

  (testing "(pj/pose \"hello\") throws"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"requires data"
         (pj/pose "hello"))))

  (testing "(pj/pose :a-keyword) throws"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"requires data"
         (pj/pose :a-keyword))))

  (testing "(pj/pose 42 :x :y) also throws (multi-arity rejects scalar)"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"requires data"
         (pj/pose 42 :x :y))))

  (testing "valid map data still works"
    (is (pj/pose? (pj/pose tiny))))

  (testing "valid sequence-of-maps data still works"
    (is (pj/pose? (pj/pose [{:a 1 :b 2} {:a 3 :b 4}])))))

(deftest template-idiom-still-works
  (testing "(pj/pose nil {:x :x :y :y}) supported as template"
    (let [tmpl (pj/pose nil {:x :x :y :y})]
      (is (pj/pose? tmpl))
      (is (nil? (:data tmpl)))
      (is (= {:x :x :y :y} (:mapping tmpl)))))

  (testing "(pj/pose nil :x :y) supported as template"
    (is (pj/pose? (pj/pose nil :x :y))))

  (testing "(pj/pose nil :x :y {:color :c}) supported as template"
    (let [tmpl (pj/pose nil :x :y {:color :c})]
      (is (pj/pose? tmpl))
      (is (= :c (-> tmpl :mapping :color))))))

(deftest empty-pose-save-throws
  (testing "(pj/save (pj/pose) ...) throws"
    (let [path "/tmp/_plotje_input_validation_test.svg"]
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"empty pose -- nothing to render"
           (pj/save (pj/pose) path)))
      (is (not (.exists (java.io.File. path)))
          "no file written")))

  (testing "(pj/save-png (pj/pose) ...) throws"
    (let [path "/tmp/_plotje_input_validation_test.png"]
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"empty pose -- nothing to render"
           (pj/save-png (pj/pose) path)))))

  (testing "save still works on a non-empty pose"
    (let [path "/tmp/_plotje_input_validation_test_ok.svg"]
      (is (= path (pj/save (pj/lay-point tiny :x :y) path)))
      (is (.exists (java.io.File. path)))
      (.delete (java.io.File. path)))))

(deftest non-map-opts-throws
  (testing "(pj/pose data x y :not-a-map) throws with helpful message"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"opts map as the last argument"
         (pj/pose [{:a 1 :b 2}] :a :b :c))))

  (testing "(pj/pose data x y nil) is accepted as no-opts"
    (is (pj/pose? (pj/pose [{:a 1 :b 2}] :a :b nil))))

  (testing "(pj/pose data x y {}) is accepted"
    (is (pj/pose? (pj/pose [{:a 1 :b 2}] :a :b {})))))

(deftest options-non-map-throws
  (testing "(pj/options pose <vector>) throws with helpful message"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"opts map as the second argument"
         (pj/options (pj/lay-point tiny :x :y) [:not :a :map]))))

  (testing "(pj/options pose 42) throws"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"opts map as the second argument"
         (pj/options (pj/lay-point tiny :x :y) 42))))

  (testing "(pj/options pose nil) is accepted (no-op)"
    (is (pj/pose? (pj/options (pj/lay-point tiny :x :y) nil))))

  (testing "(pj/options pose {}) is accepted"
    (is (pj/pose? (pj/options (pj/lay-point tiny :x :y) {})))))

(deftest lay-star-non-map-opts-throws
  (testing "(pj/lay-point ds :x :y <vector>) throws with helpful message"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"opts map as the last argument"
         (pj/lay-point tiny :x :y [:not :a :map]))))

  (testing "(pj/lay-point ds :x :y 42) throws"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"opts map as the last argument"
         (pj/lay-point tiny :x :y 42))))

  (testing "(pj/lay-line ds :x :y :not-a-map) throws"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"opts map as the last argument"
         (pj/lay-line tiny :x :y :foo))))

  (testing "(pj/lay-point ds :x :y nil) is accepted as no-opts"
    (is (pj/pose? (pj/lay-point tiny :x :y nil))))

  (testing "(pj/lay-point ds :x :y {}) is accepted"
    (is (pj/pose? (pj/lay-point tiny :x :y {})))))

(deftest error-messages-name-the-public-caller
  (testing "error from pj/lay-point on nil names pj/lay-point, not the private helper"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"^pj/lay-point requires data"
         (pj/lay-point nil :x :y))))

  (testing "error from pj/options on nil names pj/options"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"^pj/options requires data"
         (pj/options nil {:title "x"}))))

  (testing "error from pj/save on nil names pj/save"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"^pj/save requires data"
         (pj/save nil "/tmp/x.svg"))))

  (testing "error from pj/draft on nil names pj/draft"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"^pj/draft requires data"
         (pj/draft nil)))))

(deftest save-rejects-nonexistent-parent-dir
  (testing "pj/save into a nonexistent directory throws guidance, not raw IOException"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"parent directory .* does not exist"
         (pj/save (pj/lay-point tiny :x :y)
                  "/tmp/_plotje_no_such_dir_at_all/x.svg")))))

(deftest strict-config-rejects-non-boolean
  (testing "non-boolean :strict value throws at first read with explanation"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":strict config value must be true or false"
         (pj/with-config {:strict :yes}
           (pj/options (pj/lay-point tiny :x :y)
                       {:nonexistent-key 1}))))))

(deftest plot-on-plan-throws
  (testing "(pj/plot (pj/plan pose)) throws with helpful message"
    (let [pl (pj/plan (pj/lay-point tiny :x :y))]
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"pj/plot expects a pose, not a plan"
           (pj/plot pl)))))

  (testing "the helpful error mentions pj/plan->plot as alternative"
    (let [pl (pj/plan (pj/lay-point tiny :x :y))]
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"pj/plan->plot"
           (pj/plot pl))))))

(deftest pose-2-arity-extracts-data-from-opts
  (testing "(pj/pose nil {:data X :x ... :y ...}) attaches X as data, mapping omits :data"
    (let [data {:a [1 2 3] :b [4 5 6]}
          p    (pj/pose nil {:data data :x :a :y :b})]
      (is (pj/pose? p))
      (is (= data (:data p)))
      (is (= {:x :a :y :b} (:mapping p)))))

  (testing "(pj/pose data {:data new-data ...}) opts :data overrides positional data"
    (let [orig {:x [1 2] :y [3 4]}
          new  {:a [10 20] :b [30 40]}
          p    (pj/pose orig {:data new :x :a :y :b})]
      (is (= new (:data p)))
      (is (= {:x :a :y :b} (:mapping p)))))

  (testing "(pj/pose data {:x ... :y ...}) without :data uses positional data"
    (let [data {:a [1 2 3] :b [4 5 6]}
          p    (pj/pose data {:x :a :y :b})]
      (is (= data (:data p)))
      (is (= {:x :a :y :b} (:mapping p)))))

  (testing "extending an existing pose ignores opts :data (mirror 3/4-arity)"
    (let [base (pj/pose {:a [1 2] :b [3 4]} {:x :a :y :b})
          ext  (pj/pose base {:data {:c [9]} :color :a})]
      (is (= (:data base) (:data ext))
          "existing pose's data wins; use pj/with-data to replace"))))

(deftest pose-scalar-column-ref-throws
  (testing "(pj/pose data {:x 5}) throws with helpful column-ref message"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":x must be a column reference"
         (pj/pose tiny {:x 5}))))

  (testing "(pj/pose data {:y 3.14}) throws"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":y must be a column reference"
         (pj/pose tiny {:x :x :y 3.14}))))

  (testing "(pj/pose data 5 :y) 3-arity scalar in x-slot also throws"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":x must be a column reference"
         (pj/pose tiny 5 :y))))

  (testing "(pj/pose data :x 5) 3-arity scalar in y-slot also throws"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":y must be a column reference"
         (pj/pose tiny :x 5))))

  (testing "valid keyword column refs still work"
    (is (pj/pose? (pj/pose tiny {:x :x :y :y}))))

  (testing "valid string column refs still work"
    (is (pj/pose? (pj/pose tiny {:x "x" :y "y"})))))
