(ns scicloj.plotje.input-validation-test
  "Tests for the four input-validation guards added to close silent-
   failure gaps: nil data, non-collection data, empty-pose save, and
   non-map last arg in pj/pose's 4-arity."
  (:require [clojure.test :refer [deftest testing is]]
            [tablecloth.api :as tc]
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

(deftest draft-on-plan-throws
  (testing "(pj/draft (pj/plan pose)) throws with helpful message"
    (let [pl (pj/plan (pj/lay-point tiny :x :y))]
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"pj/draft expects a pose, not a plan"
           (pj/draft pl)))))

  (testing "(pj/draft (pj/plan composite)) throws on composite plans too"
    (let [pl (pj/plan (pj/arrange [(pj/lay-point tiny :x :y)
                                   (pj/lay-line  tiny :x :y)]))]
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"pj/draft expects a pose, not a plan"
           (pj/draft pl))))))

(deftest plot-on-draft-throws
  (testing "(pj/plot (pj/draft pose)) throws with helpful message"
    (let [d (pj/draft (pj/lay-point tiny :x :y))]
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"pj/plot expects a pose, not a draft"
           (pj/plot d)))))

  (testing "(pj/plot (pj/draft composite)) throws on composite drafts too"
    (let [d (pj/draft (pj/arrange [(pj/lay-point tiny :x :y)
                                   (pj/lay-line  tiny :x :y)]))]
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"pj/plot expects a pose, not a draft"
           (pj/plot d))))))

(deftest alpha-constant-out-of-range-throws
  (testing ":alpha > 1 throws at build-layer with helpful message"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":alpha must be in \[0, 1\]"
         (pj/lay-point tiny :x :y {:alpha 1.5}))))

  (testing ":alpha < 0 throws"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":alpha must be in \[0, 1\]"
         (pj/lay-point tiny :x :y {:alpha -0.1}))))

  (testing ":alpha 0 and 1 are accepted (closed interval)"
    (is (pj/pose? (pj/lay-point tiny :x :y {:alpha 0})))
    (is (pj/pose? (pj/lay-point tiny :x :y {:alpha 1}))))

  (testing ":alpha as a column reference passes through"
    (is (pj/pose? (pj/lay-point tiny :x :y {:alpha :y})))
    (is (pj/pose? (pj/lay-point tiny :x :y {:alpha "y"})))))

(deftest size-constant-non-positive-throws
  (testing ":size 0 throws"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":size must be positive"
         (pj/lay-point tiny :x :y {:size 0}))))

  (testing ":size negative throws"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":size must be positive"
         (pj/lay-point tiny :x :y {:size -3}))))

  (testing "positive :size constant is accepted"
    (is (pj/pose? (pj/lay-point tiny :x :y {:size 5}))))

  (testing ":size as a column reference passes through"
    (is (pj/pose? (pj/lay-point tiny :x :y {:size :y})))))

(deftest mapping-value-symbol-throws
  (testing "(pj/pose data {:x 'col}) -- symbol value rejects with did-you-mean"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":x is a symbol .* did you mean :x\?"
         (pj/pose tiny {:x 'x :y :y}))))

  (testing "(pj/pose data {:color 'species}) -- symbol on color"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":color is a symbol .* did you mean :species\?"
         (pj/pose tiny {:x :x :y :y :color 'species}))))

  (testing "(pj/lay-point ... {:color 'species}) at lay-* path"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":color is a symbol"
         (pj/lay-point tiny :x :y {:color 'species})))))

(deftest mapping-value-nil-passes-through
  (testing "{:color nil} on a layer cancels an inherited color (intentional)"
    (is (some? (-> (pj/pose tiny {:x :x :y :y :color :y})
                   (pj/lay-point {:color nil})
                   pj/plan
                   :panels)))))

(deftest legend-position-enum-validates
  (testing "non-enum :legend-position throws with accepted-set guidance"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":legend-position must be one of"
         (pj/options (pj/lay-point tiny :x :y) {:legend-position :weird}))))

  (testing "string is rejected -- must be a keyword from the enum"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":legend-position must be one of"
         (pj/options (pj/lay-point tiny :x :y) {:legend-position "right"}))))

  (testing "all enum values pass: :right :bottom :top :none"
    (doseq [v [:right :bottom :top :none]]
      (is (pj/pose? (pj/options (pj/lay-point tiny :x :y)
                                {:legend-position v}))
          (str "legend-position " v " should be accepted"))))

  (testing "options without :legend-position is unaffected"
    (is (pj/pose? (pj/options (pj/lay-point tiny :x :y) {:title "ok"})))))

(deftest scales-enum-validates
  (testing "non-enum :scales throws with accepted-set guidance"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":scales must be one of"
         (pj/options (pj/lay-point tiny :x :y) {:scales :weird}))))

  (testing "string is rejected -- must be a keyword from the enum"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":scales must be one of"
         (pj/options (pj/lay-point tiny :x :y) {:scales "free"}))))

  (testing "all enum values pass: :shared :free :free-x :free-y"
    (doseq [v [:shared :free :free-x :free-y]]
      (is (pj/pose? (pj/options (pj/lay-point tiny :x :y)
                                {:scales v}))
          (str "scales " v " should be accepted"))))

  (testing "options without :scales is unaffected"
    (is (pj/pose? (pj/options (pj/lay-point tiny :x :y) {:title "ok"})))))

(deftest arrange-rejection-branches-on-type
  (testing "(pj/arrange [nil]) names nil specifically"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"is nil\. Each input must be a leaf pose"
         (pj/arrange [nil]))))

  (testing "(pj/arrange [non-pose-map]) names the missing :layers/:poses"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"is a map but not a pose"
         (pj/arrange [{:not-a :pose}]))))

  (testing "(pj/arrange [hiccup-vector]) keeps hiccup-specific guidance"
    (let [pre-rendered (pj/plot (pj/lay-point tiny :x :y))]
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"looks like rendered hiccup"
           (pj/arrange [pre-rendered pre-rendered])))))

  (testing "(pj/arrange [scalar]) names the actual type"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"must be a leaf pose\. Got: java.lang.Long"
         (pj/arrange [42]))))

  (testing "plain vector inside an explicit grid hits the plain-vector branch"
    (let [p (pj/lay-point tiny :x :y)]
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"is a plain vector"
           (pj/arrange [[p [1 2 3]]]))))))

(deftest arrange-share-scales-validation
  (testing "non-#{:x :y} keys are rejected with the accepted set named"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":share-scales must be a subset of"
         (pj/arrange [(pj/lay-point tiny :x :y)] {:share-scales #{:x :z}}))))

  (testing ":share-scales accepts both #{:x} and #{:y} subsets"
    (doseq [v [#{} #{:x} #{:y} #{:x :y} [:x] [:y]]]
      (is (some? (pj/arrange [(pj/lay-point tiny :x :y)] {:share-scales v}))
          (str "share-scales " v " should be accepted")))))

(deftest single-leaf-arrange-overrides-width-warns
  (testing "single-leaf arrange with conflicting :width prints a warning"
    (let [pose (-> tiny
                   (pj/lay-point :x :y)
                   (pj/options {:width 800 :height 400}))
          out (with-out-str (pj/arrange [pose]))]
      (is (re-find #"pj/arrange wraps a single leaf" out)
          "warns about the single-leaf override")
      (is (re-find #"800x400" out)
          "names the leaf's overridden dimensions")))

  (testing "matching :width does not warn"
    (let [pose (-> tiny
                   (pj/lay-point :x :y)
                   (pj/options {:width 600 :height 400}))
          out (with-out-str (pj/arrange [pose]))]
      (is (= "" out)
          "silent when leaf dims match composite dims")))

  (testing "no width set on the leaf does not warn"
    (let [pose (pj/lay-point tiny :x :y)
          out (with-out-str (pj/arrange [pose]))]
      (is (= "" out))))

  (testing "multi-leaf arrange does not warn even with :width on a leaf"
    (let [a (-> tiny (pj/lay-point :x :y) (pj/options {:width 800}))
          b (pj/lay-point tiny :x :y)
          out (with-out-str (pj/arrange [a b]))]
      (is (= "" out)))))

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

  (testing "extending an existing pose: opts :data replaces the dataset"
    (let [base (pj/pose {:a [1 2] :b [3 4]} {:x :a :y :b})
          new  {:a [10 20 30] :b [40 50 60]}
          ext  (pj/pose base {:data new :color :a})]
      (is (= (tc/dataset new) (:data ext))
          "opts :data replaces base's data on extend (consistent across arities)"))))

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

(deftest hiccup-vector-input-throws-test
  ;; user-report-3 issue 3: pj/save-png on the hiccup output of pj/plot
  ;; previously failed with a deep "Tensors must be 2 dimensional" error
  ;; from tc/dataset. Detect a hiccup-shaped vector at the input gate.
  (let [hiccup [:svg {:width 100} [:rect {:x 1 :y 1 :width 50 :height 50}]]]
    (testing "(pj/pose hiccup) throws with rendered-hiccup guidance"
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"rendered hiccup vector"
           (pj/pose hiccup))))

    (testing "(pj/lay-point hiccup ...) throws with rendered-hiccup guidance"
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"rendered hiccup vector"
           (pj/lay-point hiccup :x :y))))

    (testing "(pj/save-png hiccup ...) throws with rendered-hiccup guidance"
      (let [path "/tmp/_plotje_hiccup_input_test.png"]
        (is (thrown-with-msg?
             clojure.lang.ExceptionInfo
             #"rendered hiccup vector"
             (pj/save-png hiccup path)))
        (is (not (.exists (java.io.File. path)))
            "no file written"))))

  (testing "vectors of row-maps are not flagged as hiccup"
    (is (pj/pose? (pj/pose [{:a 1 :b 2} {:a 3 :b 4}])))))

(deftest empty-collection-data-throws
  (testing "(pj/pose []) throws with empty-collection guidance"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"empty collection"
         (pj/pose []))))

  (testing "(pj/pose {}) throws with empty-collection guidance"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"empty collection"
         (pj/pose {}))))

  (testing "(pj/lay-point [] :x :y) also throws (via ensure-pose)"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"empty collection"
         (pj/lay-point [] :x :y))))

  (testing "the empty-pose 0-arity is unaffected"
    (is (pj/pose? (pj/pose))))

  (testing "the guidance points at (pj/pose) for the template"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"empty pose template"
         (pj/pose [])))))

(deftest zero-row-cell-renders-no-data-placeholder
  (testing "zero-row pose with explicit layer renders 'no data' placeholder"
    (let [empty-data (tc/dataset {:x [] :y []})
          pose (-> empty-data
                   (pj/pose {:x :x :y :y})
                   (pj/lay :point))
          texts (:texts (pj/svg-summary (pj/plot pose)))]
      (is (some #(= % "no data") texts)
          "rendered SVG includes the 'no data' placeholder text")))

  (testing "populated panel does NOT show the placeholder"
    (let [pose (pj/lay-point tiny :x :y)
          texts (:texts (pj/svg-summary (pj/plot pose)))]
      (is (not (some #(= % "no data") texts)))))

  (testing "zero-row cell inside arrange composite shows placeholder only on that cell"
    (let [a (-> tiny (pj/lay-point :x :y))
          b (-> (tc/dataset {:x [] :y []})
                (pj/pose {:x :x :y :y})
                (pj/lay :point))
          texts (:texts (pj/svg-summary (pj/plot (pj/arrange [a b]))))
          n-no-data (count (filter #(= % "no data") texts))]
      (is (= 1 n-no-data)
          "the empty cell renders 'no data'; the populated cell does not")))

  (testing "annotation-only panel does not get the placeholder"
    (let [pose (-> tiny
                   (pj/lay-point :x :y)
                   (pj/lay-rule-h {:y-intercept 2}))
          texts (:texts (pj/svg-summary (pj/plot pose)))]
      (is (not (some #(= % "no data") texts))))))

(deftest options-width-height-non-number-throws
  (testing "(pj/options pose {:width \"800\"}) throws with type message"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":width must be a number"
         (pj/options (pj/lay-point tiny :x :y) {:width "800"}))))

  (testing "(pj/options pose {:height :keyword}) throws"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #":height must be a number"
         (pj/options (pj/lay-point tiny :x :y) {:height :keyword}))))

  (testing "(pj/options pose {:width 800.5}) still rounds correctly"
    (let [p (pj/options (pj/lay-point tiny :x :y) {:width 800.5})]
      (is (= 801 (-> p :opts :width)))))

  (testing "(pj/options pose {:width 0.4}) throws round-to-zero error"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"must round to a positive integer"
         (pj/options (pj/lay-point tiny :x :y) {:width 0.4})))))

(deftest cross-argument-validation
  (testing "(pj/cross :scalar :scalar) throws"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"two sequentials of column"
         (pj/cross :a :b))))

  (testing "(pj/cross [:a] :scalar) throws naming the bad argument"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"two sequentials of column"
         (pj/cross [:a] :b))))

  (testing "(pj/cross [] [:c :d]) throws on empty xs"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"empty sequence"
         (pj/cross [] [:c :d]))))

  (testing "(pj/cross [:a :b] []) throws on empty ys"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"empty sequence"
         (pj/cross [:a :b] []))))

  (testing "(pj/cross [:a :b] [:c :d]) returns the 2x2 cross product"
    (is (= [[:a :c] [:a :d] [:b :c] [:b :d]]
           (pj/cross [:a :b] [:c :d])))))

(deftest pj-lay-eager-layer-type-validation
  (let [pose (pj/pose tiny :x :y)]
    (testing "unknown keyword throws at pj/lay, not deferred to plan"
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"Unknown layer type: :stackedbar.*Registered layer types"
           (pj/lay pose :stackedbar))))

    (testing "registered keyword passes through"
      (is (some? (pj/lay pose :point))))

    (testing ":infer sentinel passes through"
      (is (some? (pj/lay pose :infer))))

    (testing "extension map (entry shape) passes through"
      (is (some? (pj/lay pose {:mark :point}))))))
