(ns scicloj.plotje.format-test
  "Tests for pj/plot honoring :format from a pose's :opts on both
   leaf and composite paths. Before this fix, the leaf path
   hardcoded :svg and the composite branch of pj/plot 1-arity also
   dropped format; only the Kindly auto-render of composites
   (render-composite) read :format. Now pj/plot is the single
   honoring point and render-composite delegates to it."
  (:require [clojure.test :refer [deftest testing is]]
            [scicloj.plotje.api :as pj]))

(def tiny {:x [1.0 2.0 3.0] :y [4.0 5.0 6.0]})

(deftest leaf-pose-default-format-is-svg
  (testing "leaf with no :format returns SVG hiccup"
    (let [out (pj/plot (pj/lay-point tiny :x :y))]
      (is (vector? out))
      (is (= :svg (first out))))))

(deftest leaf-pose-bufimg-format
  (testing "leaf with {:format :bufimg} in opts returns a BufferedImage"
    (let [pose (-> tiny
                   (pj/lay-point :x :y)
                   (pj/options {:format :bufimg}))]
      (is (instance? java.awt.image.BufferedImage (pj/plot pose))))))

(deftest composite-pose-default-format-is-svg
  (testing "composite with no :format returns SVG hiccup"
    (let [out (pj/plot (pj/arrange [(pj/lay-point tiny :x :y)
                                    (pj/lay-point tiny :x :y)]))]
      (is (vector? out) "SVG hiccup is a vector")
      (is (= :svg (first out))
          "SVG hiccup starts with :svg, not a wrapper tag"))))

(deftest composite-pose-bufimg-format
  (testing "composite with {:format :bufimg} returns a BufferedImage"
    (let [comp (-> (pj/arrange [(pj/lay-point tiny :x :y)
                                (pj/lay-point tiny :x :y)])
                   (pj/options {:format :bufimg}))]
      (is (instance? java.awt.image.BufferedImage (pj/plot comp))))))

(deftest format-via-with-config
  (testing "with-config {:format :bufimg} also flows through"
    (let [pose (pj/lay-point tiny :x :y)]
      (pj/with-config {:format :bufimg}
        ;; with-config sets a thread-local; pj/plot reads (:format
        ;; (:opts fr)) -- so for this to work, the format needs to
        ;; be on the pose's :opts. Demonstrate the per-pose path
        ;; is the current contract:
        (is (vector? (pj/plot pose))
            "with-config on its own does NOT inject :format into pose opts")))))

(deftest unknown-format-throws
  (testing "unknown format raises a clear ex-info from plan->plot dispatch"
    (let [pose (-> tiny
                   (pj/lay-point :x :y)
                   (pj/options {:format :nonexistent}))]
      (is (thrown-with-msg?
           clojure.lang.ExceptionInfo
           #"Unknown render format"
           (pj/plot pose))))))

;; ---- pj/save format resolution (B2: opts > extension > :svg default) ----

(defn- read-magic [path]
  (let [bs (with-open [in (java.io.FileInputStream. ^String path)]
             (let [buf (byte-array 8)
                   n (.read in buf)]
               (vec (take n buf))))]
    bs))

(defn- svg? [path]
  ;; SVG starts with "<?xml" => bytes 0x3C 0x3F 0x78 0x6D 0x6C
  (let [bs (read-magic path)]
    (= [0x3C 0x3F 0x78 0x6D 0x6C] (mapv #(bit-and ^int % 0xFF) (take 5 bs)))))

(defn- png? [path]
  ;; PNG magic: 0x89 0x50 0x4E 0x47 0x0D 0x0A 0x1A 0x0A
  (let [bs (read-magic path)]
    (= [0x89 0x50 0x4E 0x47] (mapv #(bit-and ^int % 0xFF) (take 4 bs)))))

(deftest save-svg-extension-default
  (testing "(pj/save pose \"x.svg\") writes SVG"
    (let [path "/tmp/_plotje_save_format_a.svg"
          pose (pj/lay-point tiny :x :y)]
      (pj/save pose path)
      (is (svg? path))
      (.delete (java.io.File. path)))))

(deftest save-png-extension-infers-bufimg
  (testing "(pj/save pose \"x.png\") infers :bufimg from extension and writes PNG"
    (let [path "/tmp/_plotje_save_format_b.png"
          pose (pj/lay-point tiny :x :y)]
      (pj/save pose path)
      (is (png? path))
      (.delete (java.io.File. path)))))

(deftest save-opts-format-overrides-extension
  (testing "(pj/save pose \"x.png\" {:format :svg}) writes SVG (opts wins, warns)"
    (let [path "/tmp/_plotje_save_format_c.png"
          pose (pj/lay-point tiny :x :y)]
      (pj/save pose path {:format :svg})
      (is (svg? path))
      (.delete (java.io.File. path))))

  (testing "(pj/save pose \"x.svg\" {:format :bufimg}) writes PNG (opts wins, warns)"
    (let [path "/tmp/_plotje_save_format_d.svg"
          pose (pj/lay-point tiny :x :y)]
      (pj/save pose path {:format :bufimg})
      (is (png? path))
      (.delete (java.io.File. path)))))

(deftest save-opts-format-on-pose
  (testing ":format set via pj/options on the pose flows through pj/save"
    (let [path "/tmp/_plotje_save_format_e.png"
          pose (-> tiny
                   (pj/lay-point :x :y)
                   (pj/options {:format :bufimg}))]
      (pj/save pose path)
      (is (png? path))
      (.delete (java.io.File. path)))))

(deftest save-png-wrapper-pins-bufimg
  (testing "pj/save-png writes PNG regardless of path extension"
    (let [path "/tmp/_plotje_save_format_f.svg"
          pose (pj/lay-point tiny :x :y)]
      (pj/save-png pose path)
      (is (png? path))
      (.delete (java.io.File. path)))))

(deftest save-default-fallback-svg
  (testing "(pj/save pose \"x.unknownext\") falls back to :svg"
    (let [path "/tmp/_plotje_save_format_g.unknownext"
          pose (pj/lay-point tiny :x :y)]
      (pj/save pose path)
      (is (svg? path))
      (.delete (java.io.File. path)))))

(deftest save-non-map-opts-throws
  (testing "(pj/save pose path <vector>) throws with helpful message"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"opts map as the third"
         (pj/save (pj/lay-point tiny :x :y) "/tmp/_x.svg" [:not :a :map])))))
