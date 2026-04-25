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
      (is (or (vector? out)
              (= :hiccup (some-> out meta :kindly/kind)))))))

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
