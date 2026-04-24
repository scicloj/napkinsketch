(ns scicloj.napkinsketch.core-test
  "Hand-written unit tests for napkinsketch core logic."
  (:require [clojure.test :refer [deftest testing is are]]
            [tablecloth.api :as tc]
            [java-time.api :as jt]
            [scicloj.napkinsketch.api :as sk]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.stat :as stat]
            [scicloj.napkinsketch.impl.scale :as scale]
            [scicloj.napkinsketch.impl.position :as position]
            [scicloj.napkinsketch.impl.extract :as extract]
            [scicloj.napkinsketch.impl.resolve :as resolve]
            [scicloj.napkinsketch.layer-type :as layer-type]
            [scicloj.metamorph.ml.rdatasets :as rdatasets]))

;; ============================================================
;; defaults.clj
;; ============================================================

(deftest hex->rgba-test
  (testing "6-digit hex"
    (let [[r g b a] (defaults/hex->rgba "#FF0000")]
      (is (== 1.0 r))
      (is (== 0.0 g))
      (is (== 0.0 b))
      (is (== 1.0 a))))
  (testing "8-digit hex with alpha"
    (let [[_ _ _ a] (defaults/hex->rgba "#FF000080")]
      (is (< 0.3 a 0.6))))
  (testing "3-digit shorthand"
    (let [[r g b _] (defaults/hex->rgba "#F00")]
      (is (== 1.0 r))
      (is (== 0.0 g))))
  (testing "without #"
    (let [[r _ _ _] (defaults/hex->rgba "00FF00")]
      (is (== 0.0 r)))))

(deftest color-for-test
  (testing "index-based default palette"
    (let [cats ["a" "b" "c"]
          c1 (defaults/color-for cats "a")
          c2 (defaults/color-for cats "b")]
      (is (= 4 (count c1)))
      (is (not= c1 c2))))
  (testing "palette as map"
    (let [cats ["a" "b"]
          c (defaults/color-for cats "a" {"a" "#FF0000"})]
      (is (== 1.0 (first c)))))
  (testing "palette as vector"
    (let [cats ["x" "y"]
          c (defaults/color-for cats "x" ["#00FF00" "#0000FF"])]
      (is (== 0.0 (first c)))
      (is (== 1.0 (second c)))))
  (testing "wrap-around index"
    (let [cats (mapv str (range 20))
          c (defaults/color-for cats "0")
          c2 (defaults/color-for cats "15")]
      (is (= 4 (count c)))
      (is (= 4 (count c2))))))

(deftest gradient-color-test
  (testing "t=0.0 (dark blue — low end)"
    (let [[r g b a] (defaults/gradient-color 0.0)]
      (is (< r 0.1))
      (is (< b 0.3))
      (is (== 1.0 a))))
  (testing "t=1.0 (light blue — high end)"
    (let [[r g b _] (defaults/gradient-color 1.0)]
      (is (> b 0.9))
      (is (> g 0.6))))
  (testing "t=0.5 (mid blue)"
    (let [[r g b _] (defaults/gradient-color 0.5)]
      (is (> b 0.5))
      (is (> g 0.3))
      (is (< r 0.3))))
  (testing "clamping"
    (is (= (defaults/gradient-color -1.0) (defaults/gradient-color 0.0)))
    (is (= (defaults/gradient-color 2.0) (defaults/gradient-color 1.0)))))

(deftest legend-serializable-test
  (testing "continuous legend has :color-scale keyword, no :gradient-fn"
    (let [ds (tc/dataset {:x (range 50) :y (range 50) :c (range 50)})
          pl (sk/plan (-> ds (sk/lay-point :x :y {:color :c})))
          legend (:legend pl)]
      (is (= :continuous (:type legend)))
      (is (contains? legend :color-scale))
      (is (not (contains? legend :gradient-fn)))
      (is (nil? (:color-scale legend)) "default color-scale is nil")))
  (testing "explicit :color-scale is stored as keyword"
    (let [ds (tc/dataset {:x (range 50) :y (range 50) :c (range 50)})
          pl (sk/plan (-> ds (sk/lay-point :x :y {:color :c}))
                      {:color-scale :inferno})
          legend (:legend pl)]
      (is (= :inferno (:color-scale legend)))))
  (testing "legend has 20 pre-computed stops"
    (let [ds (tc/dataset {:x (range 50) :y (range 50) :c (range 50)})
          pl (sk/plan (-> ds (sk/lay-point :x :y {:color :c})))
          legend (:legend pl)]
      (is (= 20 (count (:stops legend))))
      (is (== 0.0 (:t (first (:stops legend)))))
      (is (== 1.0 (:t (last (:stops legend))))))))

(deftest fmt-name-test
  (is (= "sepal length" (defaults/fmt-name :sepal_length)))
  (is (= "sepal length" (defaults/fmt-name :sepal-length)))
  (is (= "x" (defaults/fmt-name :x))))

;; ============================================================
;; stat.clj
;; ============================================================

(def tiny-ds (tc/dataset {:x [1 2 3 4 5] :y [2 4 6 8 10]}))
(def cat-ds (tc/dataset {:cat ["a" "a" "b" "b" "b"] :val [1 2 3 4 5]}))

(deftest compute-stat-identity-test
  (let [view {:mark :point :data tiny-ds :x :x :y :y :x-type :numerical :y-type :numerical}
        result (stat/compute-stat (assoc view :cfg defaults/defaults))]
    (is (seq (:points result)))
    (is (= 5 (count (:xs (first (:points result))))))))

(deftest compute-stat-bin-test
  (let [view {:mark :bar :stat :bin :data tiny-ds :x :x :x-type :numerical
              :cfg defaults/defaults}
        result (stat/compute-stat view)]
    (is (= 1 (count (:bins result))))
    (is (pos? (count (:bin-maps (first (:bins result))))))))

(deftest compute-stat-count-test
  (let [view {:mark :rect :stat :count :data cat-ds :x :cat :x-type :categorical
              :cfg defaults/defaults}
        result (stat/compute-stat view)]
    (is (= ["a" "b"] (vec (:categories result))))
    (is (seq (:bars result)))
    ;; counts is a vector of {:category "a" :count N} maps
    (let [counts (:counts (first (:bars result)))]
      (is (= 2 (:count (first (filter #(= "a" (:category %)) counts)))))
      (is (= 3 (:count (first (filter #(= "b" (:category %)) counts))))))))

(deftest compute-stat-lm-test
  (testing "perfect linear data y=2x"
    (let [view {:mark :line :stat :linear-model :data tiny-ds :x :x :y :y
                :x-type :numerical :cfg defaults/defaults}
          result (stat/compute-stat view)
          line (first (:lines result))]
      (is line)
      (is (< (:x1 line) (:x2 line)))
      (is (< (Math/abs (- (:y1 line) 2.0)) 0.01))
      (is (< (Math/abs (- (:y2 line) 10.0)) 0.01))))
  (testing "n=2 produces nil (needs >= 3)"
    (let [ds2 (tc/dataset {:x [1 2] :y [3 4]})
          view {:mark :line :stat :linear-model :data ds2 :x :x :y :y
                :x-type :numerical :cfg defaults/defaults}
          result (stat/compute-stat view)]
      (is (empty? (:lines result))))))

(deftest compute-stat-kde-test
  (let [view {:mark :area :stat :density :data tiny-ds :x :x :x-type :numerical
              :cfg defaults/defaults}
        result (stat/compute-stat view)]
    (is (seq (:points result)))
    (is (> (count (:xs (first (:points result)))) 10))))

(deftest compute-stat-boxplot-test
  (let [ds (tc/dataset {:cat ["a" "a" "a" "a" "a" "a"]
                        :val [1.0 2.0 3.0 4.0 5.0 100.0]})
        view {:mark :boxplot :stat :boxplot :data ds :x :cat :y :val
              :x-type :categorical :cfg defaults/defaults}
        result (stat/compute-stat view)
        box (first (:boxes result))]
    (is box)
    (is (= "a" (:category box)))
    (is (<= (:q1 box) (:median box) (:q3 box)))
    (is (seq (:outliers box)))))

(deftest compute-stat-summary-test
  (let [ds (tc/dataset {:cat ["a" "a" "a" "b" "b" "b"]
                        :val [10.0 20.0 30.0 40.0 50.0 60.0]})
        view {:mark :pointrange :stat :summary :data ds :x :cat :y :val
              :x-type :categorical :cfg defaults/defaults}
        result (stat/compute-stat view)]
    (is (seq (:points result)))
    (let [p (first (:points result))]
      (is (= 2 (count (:xs p))))
      ;; mean of [10 20 30] = 20, mean of [40 50 60] = 50
      (is (< (Math/abs (- (first (:ys p)) 20.0)) 0.01))
      (is (< (Math/abs (- (second (:ys p)) 50.0)) 0.01)))))

(deftest compute-stat-bin2d-test
  (let [ds (tc/dataset {:x (range 100) :y (range 100)})
        view {:mark :tile :stat :bin2d :data ds :x :x :y :y
              :x-type :numerical :y-type :numerical :cfg defaults/defaults}
        result (stat/compute-stat view)]
    (is (seq (:tiles result)))
    (is (= #{:x-lo :x-hi :y-lo :y-hi :fill} (set (tc/column-names (:tiles result)))))))

;; ============================================================
;; position.clj
;; ============================================================

(deftest apply-positions-identity-test
  (let [layers [{:mark :point :groups [{:xs [1 2] :ys [3 4]}]}]]
    (is (= layers (position/apply-positions layers)))))

(deftest apply-positions-dodge-test
  (let [layers [{:mark :rect :position :dodge
                 :categories ["a" "b"]
                 :groups [{:label "g1" :counts [{:category "a" :count 10} {:category "b" :count 20}]}
                          {:label "g2" :counts [{:category "a" :count 30} {:category "b" :count 40}]}]}]
        result (position/apply-positions layers)
        layer (first result)]
    (is (:dodge-ctx layer))
    (is (= 2 (:n-groups (:dodge-ctx layer))))
    (is (= 0 (:dodge-idx (first (:groups layer)))))
    (is (= 1 (:dodge-idx (second (:groups layer)))))))

(deftest apply-positions-stack-test
  (let [layers [{:mark :rect :position :stack
                 :categories ["a" "b"]
                 :groups [{:label "g1" :counts [{:category "a" :count 10} {:category "b" :count 20}]}
                          {:label "g2" :counts [{:category "a" :count 30} {:category "b" :count 40}]}]}]
        result (position/apply-positions layers)
        g2-counts (:counts (second (:groups (first result))))]
    ;; g2 stacked on g1: y0 should be g1's count
    (is (= 10.0 (:y0 (first g2-counts))))
    (is (= 40.0 (:y1 (first g2-counts))))))

(deftest apply-positions-fill-test
  (let [layers [{:mark :rect :position :fill
                 :categories ["a" "b"]
                 :groups [{:label "g1" :counts [{:category "a" :count 10} {:category "b" :count 20}]}
                          {:label "g2" :counts [{:category "a" :count 30} {:category "b" :count 40}]}]}]
        result (position/apply-positions layers)
        g1-counts (:counts (first (:groups (first result))))
        g2-counts (:counts (second (:groups (first result))))]
    ;; fill normalizes: cat "a": g1=10/(10+30)=0.25
    (is (< (Math/abs (- (:y1 (first g1-counts)) 0.25)) 0.01))
    ;; g2 top = 1.0
    (is (< (Math/abs (- (:y1 (first g2-counts)) 1.0)) 0.01))))

(deftest cross-layer-dodge-test
  (let [layers [{:mark :rect :position :dodge
                 :categories ["a" "b"]
                 :groups [{:label "g1" :counts [{:category "a" :count 10} {:category "b" :count 20}]}
                          {:label "g2" :counts [{:category "a" :count 30} {:category "b" :count 40}]}]}
                {:mark :errorbar :position :dodge
                 :groups [{:label "g1" :xs ["a" "b"] :ys [10 20]
                           :ymins [8 18] :ymaxs [12 22]}]}]
        result (position/apply-positions layers)]
    ;; Both layers should share same n-groups
    (is (= (:n-groups (:dodge-ctx (first result)))
           (:n-groups (:dodge-ctx (second result)))))))

(deftest count-stat-x-equals-color-test
  (testing "count stat when x and color map to the same column"
    (let [pl (-> {:species ["setosa" "setosa" "versicolor" "versicolor"]}
                 (sk/lay-bar :species {:color :species})
                 sk/plan)
          groups (get-in pl [:panels 0 :layers 0 :groups])]
      ;; Each group should only have non-zero count for its own species
      (doseq [g groups]
        (doseq [{:keys [category count]} (:counts g)]
          (if (= category (:label g))
            (is (pos? count) (str (:label g) " should have count for " category))
            (is (zero? count) (str (:label g) " should have 0 count for " category))))))))

(deftest value-bar-stacking-y0s-test
  (testing "stacked value bars use y0s baselines"
    (let [pl (-> {:day ["Mon" "Mon"] :count [30 20] :meal ["lunch" "dinner"]}
                 (sk/lay-value-bar :day :count {:color :meal :position :stack})
                 sk/plan)
          groups (get-in pl [:panels 0 :layers 0 :groups])
          dinner (second groups)]
      ;; dinner baseline should equal lunch value (30)
      (is (= [30.0] (vec (:y0s dinner)))))))

;; ============================================================
;; scale.clj
;; ============================================================

(deftest make-scale-linear-test
  (let [s (scale/make-scale [0 100] [0 500] {})]
    (is (== 0 (s 0)))
    (is (== 500 (s 100)))
    (is (== 250 (s 50)))))

(deftest make-scale-categorical-test
  (let [s (scale/make-scale ["a" "b" "c"] [0 300] {})]
    (is (some? s))
    (is (pos? (wadogo.scale/data s :bandwidth)))))

(deftest make-scale-log-test
  (let [s (scale/make-scale [1 1000] [0 300] {:type :log})]
    (is (== 0 (s 1)))
    (is (== 300 (s 1000)))))

(deftest pad-domain-test
  (testing "numeric padding"
    (let [[lo hi] (scale/pad-domain [0.0 10.0] {})]
      (is (< lo 0.0))
      (is (> hi 10.0))))
  (testing "log padding stays positive"
    (let [[lo hi] (scale/pad-domain [1.0 100.0] {:type :log})]
      (is (pos? lo))
      (is (> hi 100.0)))))

(deftest format-ticks-test
  (testing "integer ticks"
    (let [s (scale/make-scale [0 10] [0 500] {})
          labels (scale/format-ticks s [0.0 5.0 10.0])]
      (is (= ["0" "5" "10"] labels))))
  (testing "decimal ticks"
    (let [s (scale/make-scale [0 1] [0 500] {})
          labels (scale/format-ticks s [0.0 0.5 1.0])]
      (is (every? string? labels)))))

;; ============================================================
;; extract.clj
;; ============================================================

(deftest resolve-color-test
  (let [cfg (assoc defaults/defaults :palette nil)]
    (testing "column color"
      (let [c (extract/resolve-color ["a" "b"] "a" nil cfg)]
        (is (= 4 (count c)))))
    (testing "fixed hex color"
      (let [c (extract/resolve-color nil nil "#FF0000" cfg)]
        (is (== 1.0 (first c)))))
    (testing "nil falls to default"
      (let [c (extract/resolve-color nil nil nil cfg)]
        (is (= 4 (count c)))))))

(deftest extract-layer-point-test
  (let [view {:mark :point :data tiny-ds :x :x :y :y
              :x-type :numerical :y-type :numerical}
        rv (resolve/resolve-draft-layer view)
        stat-result (stat/compute-stat (assoc rv :cfg defaults/defaults))
        layer (extract/extract-layer rv stat-result [] defaults/defaults)]
    (is (= :point (:mark layer)))
    (is (seq (:groups layer)))
    (is (= 5 (count (:xs (first (:groups layer))))))))

(deftest extract-layer-bar-test
  (let [view {:mark :bar :stat :bin :data tiny-ds :x :x
              :x-type :numerical}
        rv (resolve/resolve-draft-layer view)
        stat-result (stat/compute-stat (assoc rv :cfg defaults/defaults))
        layer (extract/extract-layer rv stat-result [] defaults/defaults)]
    (is (= :bar (:mark layer)))
    (is (seq (:groups layer)))
    (is (seq (:bars (first (:groups layer)))))))

(deftest apply-nudge-test
  (testing "nudge-x shifts xs"
    (let [view {:mark :point :data (tc/dataset {:x [1.0 2.0] :y [3.0 4.0]})
                :x :x :y :y :x-type :numerical :nudge-x 0.5}
          rv (resolve/resolve-draft-layer view)
          stat-result (stat/compute-stat (assoc rv :cfg defaults/defaults))
          layer (extract/extract-layer rv stat-result [] defaults/defaults)]
      (is (= [1.5 2.5] (:xs (first (:groups layer)))))))
  (testing "no nudge is no-op"
    (let [view {:mark :point :data tiny-ds :x :x :y :y :x-type :numerical}
          rv (resolve/resolve-draft-layer view)
          stat-result (stat/compute-stat (assoc rv :cfg defaults/defaults))
          layer (extract/extract-layer rv stat-result [] defaults/defaults)]
      (is (= [1 2 3 4 5] (:xs (first (:groups layer))))))))

;; ============================================================
;; method.clj — mark constructors
;; ============================================================

(deftest mark-constructors-test
  (testing "registry entries have the correct :mark key"
    (are [k mk] (= mk (:mark (layer-type/lookup k)))
      :point :point
      :line :line
      :step :step
      :histogram :bar
      :bar :rect
      :value-bar :rect
      :smooth :line
      :text :text
      :label :label
      :area :area
      :density :area
      :tile :tile
      :density-2d :tile
      :contour :contour
      :ridgeline :ridgeline
      :boxplot :boxplot
      :violin :violin
      :rug :rug
      :summary :pointrange
      :errorbar :errorbar
      :lollipop :lollipop)))

;; ============================================================
;; draft->plan (integration)
;; ============================================================

(deftest views-to-plan-test
  (let [views (-> tiny-ds
                  (sk/frame [[:x :y]])
                  sk/lay-point)
        pl (sk/plan views)]
    (is (map? pl))
    (is (contains? pl :panels))
    (is (contains? pl :width))
    (is (contains? pl :height))
    (is (= 1 (count (:panels pl))))
    (let [panel (first (:panels pl))]
      (is (seq (:layers panel)))
      (is (contains? panel :x-domain))
      (is (contains? panel :y-domain)))))

(deftest plan-with-color-test
  (let [ds (tc/dataset {:x [1 2 3 4] :y [1 2 3 4] :g ["a" "a" "b" "b"]})
        views (-> ds (sk/frame [[:x :y]]) (sk/lay-point {:color :g}))
        pl (sk/plan views)]
    (is (:legend pl))
    (is (= 2 (count (:entries (:legend pl)))))))

(deftest plan-faceted-test
  (let [ds (tc/dataset {:x [1 2 3 4 5 6] :y [1 2 3 4 5 6]
                        :g ["a" "a" "b" "b" "c" "c"]})
        views (-> ds (sk/frame [[:x :y]]) (sk/facet :g) sk/lay-point)
        pl (sk/plan views)]
    (is (= 3 (count (:panels pl))))))

(deftest coord-fixed-test
  (testing "coord :fixed end-to-end — equal ranges produce square panel"
    (let [ds (tc/dataset {:x [0 10 5] :y [0 10 5]})
          pl (-> ds (sk/frame :x :y) (sk/coord :fixed) sk/lay-point sk/plan)]
      (is (== (:panel-width pl) (:panel-height pl)) "Equal data ranges → square panel")))
  (testing "coord :fixed end-to-end — asymmetric ranges"
    (let [ds (tc/dataset {:x [0 100 50] :y [0 10 5]})
          pl (-> ds (sk/frame :x :y) (sk/coord :fixed) sk/lay-point sk/plan)]
      (is (> (:panel-width pl) (:panel-height pl)) "Wide data → wider panel"))))

(deftest diverging-color-test
  (testing "diverging-color endpoints"
    (let [[r g b _] (defaults/diverging-color 0.0)]
      (is (> r g) "t=0 red > green")
      (is (> r b) "t=0 red > blue"))
    (let [[r g b _] (defaults/diverging-color 1.0)]
      (is (> b r) "t=1 blue > red")
      (is (> b g) "t=1 blue > green"))
    (let [[r g b _] (defaults/diverging-color 0.5)]
      (is (> r 0.9) "t=0.5 is whitish (r)")
      (is (> g 0.9) "t=0.5 is whitish (g)")
      (is (> b 0.9) "t=0.5 is whitish (b)")))
  (testing "normalize-midpoint"
    (is (== 0.0 (defaults/normalize-midpoint -5 -5 5 0)))
    (is (== 0.5 (defaults/normalize-midpoint 0 -5 5 0)))
    (is (== 1.0 (defaults/normalize-midpoint 5 -5 5 0)))
    (is (== 0.25 (defaults/normalize-midpoint -2.5 -5 5 0)))
    (is (== 0.75 (defaults/normalize-midpoint 2.5 -5 5 0)))
    (is (== 0.5 (defaults/normalize-midpoint 5 0 10 nil))))
  (testing "resolve-gradient-fn"
    (is (fn? (defaults/resolve-gradient-fn nil)))
    (is (fn? (defaults/resolve-gradient-fn :diverging)))
    (is (fn? (defaults/resolve-gradient-fn :inferno)))
    (is (fn? (defaults/resolve-gradient-fn {:low "#FF0000" :mid "#FFFFFF" :high "#0000FF"}))))
  (testing "diverging end-to-end"
    (let [ds (tc/dataset {:x (range 10) :y (range 10) :z (map #(- % 5) (range 10))})
          fig (-> ds (sk/frame :x :y)
                  (sk/lay-point {:color :z})
                  (sk/plot {:color-scale :diverging :color-midpoint 0}))
          s (sk/svg-summary fig)]
      (is (= 10 (:points s))))))

(deftest loess-se-test
  (testing "LOESS with SE produces ribbon"
    (let [ds (tc/dataset {:x (range 20) :y (map #(+ (* 0.1 % %) (Math/sin %)) (range 20))})
          fig (-> ds (sk/frame :x :y)
                  sk/lay-point
                  (sk/lay-smooth {:confidence-band true :bootstrap-resamples 50})
                  sk/plot)
          s (sk/svg-summary fig)]
      (is (= 20 (:points s)))
      (is (= 1 (:lines s)))
      (is (= 1 (:polygons s)) "confidence ribbon polygon")))
  (testing "LOESS without SE has no ribbon"
    (let [ds (tc/dataset {:x (range 20) :y (map #(+ (* 0.1 % %) (Math/sin %)) (range 20))})
          fig (-> ds (sk/frame :x :y)
                  sk/lay-point
                  sk/lay-smooth
                  sk/plot)
          s (sk/svg-summary fig)]
      (is (= 1 (:lines s)))
      (is (zero? (:polygons s)))))
  (testing "LOESS dedup handles duplicate x values"
    (let [ds (tc/dataset {:x [1 1 2 2 3 3 4 4 5 5] :y [2 3 4 5 6 7 8 9 10 11]})
          fig (-> ds (sk/frame :x :y) sk/lay-smooth sk/plot)
          s (sk/svg-summary fig)]
      (is (= 1 (:lines s))))))

(deftest arrange-test
  (testing "flat sketches -> composite frame of leaves"
    (let [sk1 (-> tiny-ds (sk/frame :x :y) sk/lay-point)
          sk2 (-> tiny-ds (sk/frame :x :y) sk/lay-point)
          result (sk/arrange [sk1 sk2])]
      (is (sk/frame? result))
      (is (= 1 (count (:frames result))) "one row with both leaves")
      (is (= 2 (count (:frames (first (:frames result))))))))
  (testing "nested rows -> outer vertical of rows"
    (let [sk1 (-> tiny-ds (sk/frame :x :y) sk/lay-point)
          result (sk/arrange [[sk1 sk1] [sk1 sk1]])]
      (is (sk/frame? result))
      (is (= 2 (count (:frames result))) "two rows")
      (is (every? #(= 2 (count (:frames %))) (:frames result))
          "each row has two leaves")))
  (testing "title flows through to composite opts"
    (let [sk1 (-> tiny-ds (sk/frame :x :y) sk/lay-point)
          result (sk/arrange [sk1 sk1] {:title "Test" :cols 2})]
      (is (= "Test" (-> result :opts :title)))
      (let [plotted (sk/plot result)]
        (is (= :svg (first plotted)))
        (is (some #(= "Test" %) (:texts (sk/svg-summary plotted)))
            "title text appears in rendered svg"))))
  (testing "hiccup input is rejected with a clear error"
    (let [pre-rendered (-> tiny-ds (sk/frame :x :y) sk/lay-point sk/plot)]
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"must be a leaf frame"
                            (sk/arrange [pre-rendered pre-rendered]))))))

(deftest valid-plan-test
  (let [views (-> tiny-ds (sk/frame [[:x :y]]) sk/lay-point)
        pl (sk/plan views)]
    (is (sk/valid-plan? pl))))

;; ============================================================
;; Configuration System
;; ============================================================

(deftest config-returns-defaults-test
  (testing "config returns a map with all expected keys"
    (let [cfg (defaults/config)]
      (is (map? cfg))
      (is (= 600 (:width cfg)))
      (is (= 400 (:height cfg)))
      (is (= 10 (:margin cfg)))
      (is (= 3.0 (:point-radius cfg)))
      (is (= 0.75 (:point-opacity cfg)))
      (is (= 0.85 (:bar-opacity cfg)))
      (is (= 2.5 (:line-width cfg)))
      (is (= 0.6 (:grid-stroke-width cfg)))
      (is (string? (:annotation-stroke cfg)))
      (is (= 0.15 (:band-opacity cfg)))
      (is (= 60 (:tick-spacing-x cfg)))
      (is (= 40 (:tick-spacing-y cfg)))
      (is (= :sturges (:bin-method cfg)))
      (is (= 0.05 (:domain-padding cfg)))
      (is (= 13 (:label-font-size cfg)))
      (is (= 15 (:title-font-size cfg)))
      (is (= 10 (:strip-font-size cfg)))
      (is (= 38 (:label-offset cfg)))
      (is (= 18 (:title-offset cfg)))
      (is (= 16 (:strip-height cfg)))
      (is (true? (:validate cfg)))))
  (testing "config includes theme nested map"
    (let [theme (:theme (defaults/config))]
      (is (map? theme))
      (is (string? (:bg theme)))
      (is (string? (:grid theme)))
      (is (number? (:font-size theme))))))

(deftest set-config!-test
  (testing "set-config! overrides specific keys"
    (try
      (defaults/set-config! {:width 800})
      (is (= 800 (:width (defaults/config))))
      ;; Other keys remain at defaults
      (is (= 400 (:height (defaults/config))))
      (finally
        (defaults/set-config! nil))))
  (testing "set-config! nil resets to defaults"
    (try
      (defaults/set-config! {:width 999})
      (is (= 999 (:width (defaults/config))))
      (defaults/set-config! nil)
      (is (= 600 (:width (defaults/config))))
      (finally
        (defaults/set-config! nil))))
  (testing "set-config! overrides theme with merge at top level"
    (try
      (defaults/set-config! {:theme {:bg "#FFFFFF" :grid "#EEEEEE" :font-size 12}})
      (let [theme (:theme (defaults/config))]
        (is (= "#FFFFFF" (:bg theme)))
        (is (= "#EEEEEE" (:grid theme)))
        (is (= 12 (:font-size theme))))
      (finally
        (defaults/set-config! nil)))))

(deftest deep-merge-config-test
  (testing "set-config! partial theme deep-merges, preserving other theme keys"
    (try
      (defaults/set-config! {:theme {:bg "#000"}})
      (let [theme (:theme (defaults/config))]
        (is (= "#000" (:bg theme)))
        ;; grid and font-size preserved from library defaults
        (is (string? (:grid theme)) "grid should be preserved")
        (is (number? (:font-size theme)) "font-size should be preserved"))
      (finally
        (defaults/set-config! nil))))
  (testing "with-config partial theme deep-merges"
    (sk/with-config {:theme {:bg "#111"}}
      (let [theme (:theme (defaults/config))]
        (is (= "#111" (:bg theme)))
        (is (string? (:grid theme)) "grid should be preserved")
        (is (number? (:font-size theme)) "font-size should be preserved"))))
  (testing "partial theme via with-config renders without error"
    (sk/with-config {:theme {:bg "#222"}}
      (let [svg (-> {:x [1 2 3] :y [4 5 6]}
                    (sk/lay-point :x :y)
                    sk/plot)]
        (is (vector? svg)))))
  (testing "sk/options deep-merges theme across calls"
    (let [sketch (-> {:x [1 2 3] :y [4 5 6]}
                     (sk/lay-point :x :y)
                     (sk/options {:theme {:bg "#FFF"} :width 800})
                     (sk/options {:theme {:font-size 14}}))]
      (is (= "#FFF" (get-in (:opts sketch) [:theme :bg])))
      (is (= 14 (get-in (:opts sketch) [:theme :font-size])))
      (is (= 800 (:width (:opts sketch)))))))

(deftest dynamic-binding-test
  (testing "binding *config* overrides set-config!"
    (try
      (defaults/set-config! {:width 800})
      (binding [defaults/*config* {:width 1200}]
        (is (= 1200 (:width (defaults/config)))))
      ;; Outside binding, set-config! still applies
      (is (= 800 (:width (defaults/config))))
      (finally
        (defaults/set-config! nil))))
  (testing "binding *config* overrides defaults"
    (binding [defaults/*config* {:point-radius 5.0}]
      (is (= 5.0 (:point-radius (defaults/config)))))
    ;; Outside binding, back to defaults
    (is (= 3.0 (:point-radius (defaults/config))))))

(deftest resolve-config-test
  (testing "per-call opts override everything"
    (try
      (defaults/set-config! {:width 800})
      (binding [defaults/*config* {:width 1200}]
        (let [cfg (defaults/resolve-config {:width 900})]
          (is (= 900 (:width cfg)))))
      (finally
        (defaults/set-config! nil))))
  (testing "per-call opts with no overrides returns config"
    (let [cfg (defaults/resolve-config {})]
      (is (= 600 (:width cfg)))))
  (testing "per-call theme merges into default theme"
    (let [cfg (defaults/resolve-config {:theme {:bg "#FFF"}})]
      (is (= "#FFF" (get-in cfg [:theme :bg])))
      ;; grid and font-size preserved from defaults
      (is (= "#F5F5F5" (get-in cfg [:theme :grid])))
      (is (= 11 (get-in cfg [:theme :font-size])))))
  (testing "per-call palette"
    (let [cfg (defaults/resolve-config {:palette :dark2})]
      (is (= :dark2 (:palette cfg)))))
  (testing "per-call color-scale"
    (let [cfg (defaults/resolve-config {:color-scale :diverging})]
      (is (= :diverging (:color-scale cfg)))))
  (testing "per-call validate false"
    (let [cfg (defaults/resolve-config {:validate false})]
      (is (false? (:validate cfg))))))

(deftest precedence-chain-test
  (testing "full precedence: per-call > binding > set-config! > defaults"
    (try
      (defaults/set-config! {:width 800 :height 300})
      (binding [defaults/*config* {:width 1200 :height 500}]
        ;; per-call wins for width; binding wins for height
        (let [cfg (defaults/resolve-config {:width 900})]
          (is (= 900 (:width cfg)))
          (is (= 500 (:height cfg)))
          ;; margin untouched by any override → from defaults
          (is (= 10 (:margin cfg)))))
      (finally
        (defaults/set-config! nil)))))

;; ---- Public API config functions ----

(deftest api-config-test
  (testing "sk/config returns resolved config"
    (let [cfg (sk/config)]
      (is (map? cfg))
      (is (= 600 (:width cfg)))))
  (testing "sk/set-config! and reset"
    (try
      (sk/set-config! {:width 777})
      (is (= 777 (:width (sk/config))))
      (sk/set-config! nil)
      (is (= 600 (:width (sk/config))))
      (finally
        (sk/set-config! nil))))
  (testing "sk/with-config overrides for body"
    (sk/with-config {:width 1234}
      (is (= 1234 (:width (sk/config)))))
    (is (= 600 (:width (sk/config))))))

;; ---- Config affects plan output ----

(deftest config-affects-plan-test
  (let [views (-> tiny-ds (sk/frame [[:x :y]]) sk/lay-point)]
    (testing "default width/height in plan"
      (let [s (sk/plan views)]
        (is (= 600 (:width s)))
        (is (= 400 (:height s)))))
    (testing "per-call opts change plan dimensions"
      (let [s (sk/plan views {:width 800 :height 300})]
        (is (= 800 (:width s)))
        (is (= 300 (:height s)))))
    (testing "set-config! changes plan dimensions"
      (try
        (sk/set-config! {:width 700})
        (let [s (sk/plan views)]
          (is (= 700 (:width s))))
        (finally
          (sk/set-config! nil))))
    (testing "with-config changes plan dimensions"
      (sk/with-config {:height 500}
        (let [s (sk/plan views)]
          (is (= 500 (:height s)))))
      ;; After with-config, back to default
      (let [s (sk/plan views)]
        (is (= 400 (:height s)))))
    (testing "plan does NOT contain :theme key"
      (let [s (sk/plan views)]
        (is (not (contains? s :theme)))))))

;; ---- Config affects rendered SVG ----

(deftest config-affects-render-test
  (let [views (-> tiny-ds (sk/frame [[:x :y]]) sk/lay-point)]
    (testing "default theme bg appears in SVG"
      (let [svg (sk/plot views)
            summary (sk/svg-summary svg)]
        (is (= 1 (:panels summary)))
        (is (= 5 (:points summary)))))
    (testing "per-call theme overrides bg in SVG"
      (let [svg (sk/plot views {:theme {:bg "#FFFFFF" :grid "#EEEEEE" :font-size 8}})
            s (str svg)]
        ;; Default bg is rgb(235,235,235); custom is rgb(255,255,255)
        (is (clojure.string/includes? s "rgb(255,255,255)"))))
    (testing "with-config theme overrides bg in SVG"
      (let [svg (sk/with-config {:theme {:bg "#FF0000" :grid "#FFFFFF" :font-size 8}}
                  (sk/plot views))
            s (str svg)]
        (is (clojure.string/includes? s "rgb(255,0,0)"))))
    (testing "per-call width changes SVG viewBox"
      ;; Under the total-dimensions semantics, :width is the TOTAL
      ;; SVG width; the panel is derived by subtracting overhead.
      ;; So the output viewBox width equals :width exactly.
      (let [svg (sk/plot views {:width 800})
            attrs (second svg)]
        (is (= 800.0 (double (:width attrs))))))))

;; ---- Config with palette ----

(deftest config-palette-test
  (let [ds (tc/dataset {:x [1 2 3 4 5 6]
                        :y [10 20 30 15 25 35]
                        :g ["a" "a" "a" "b" "b" "b"]})
        views (-> ds (sk/frame [[:x :y]]) (sk/lay-point {:color :g}))]
    (testing "default palette produces colored points"
      (let [svg (sk/plot views)
            summary (sk/svg-summary svg)]
        (is (= 6 (:points summary)))))
    (testing "per-call palette :dark2 works"
      (let [svg (sk/plot views {:palette :dark2})
            summary (sk/svg-summary svg)]
        (is (= 6 (:points summary)))))
    (testing "set-config! palette works"
      (try
        (sk/set-config! {:palette :set2})
        (let [svg (sk/plot views)
              summary (sk/svg-summary svg)]
          (is (= 6 (:points summary))))
        (finally
          (sk/set-config! nil))))))

;; ---- Config validation flag ----

(deftest config-validate-flag-test
  (let [views (-> tiny-ds (sk/frame [[:x :y]]) sk/lay-point)]
    (testing "validate true (default) — valid plan passes"
      (is (some? (sk/plan views))))
    (testing "validate false skips schema check"
      (is (some? (sk/plan views {:validate false}))))))

;; ---- Edge case tests ----

(deftest single-point-dataset-test
  (testing "plan with a single data point does not throw"
    (let [ds (tc/dataset {:x [5] :y [10]})
          pl (sk/plan (-> ds (sk/lay-point :x :y)))]
      (is (= 1 (count (:panels pl))))
      (is (some? (sk/plot (-> ds (sk/lay-point :x :y))))))))

(deftest two-point-dataset-test
  (testing "regression with exactly 2 points — lm needs n>=3 so falls back gracefully"
    (let [ds (tc/dataset {:x [1 2] :y [3 4]})
          views (-> ds (sk/frame :x :y) sk/lay-point)]
      (is (some? (sk/plan views))))))

(deftest all-same-values-test
  (testing "scatter where all x values are identical"
    (let [ds (tc/dataset {:x [5 5 5 5] :y [1 2 3 4]})
          pl (sk/plan (-> ds (sk/lay-point :x :y)))]
      (is (some? pl))
      (is (= 1 (count (:panels pl))))))
  (testing "scatter where all y values are identical"
    (let [ds (tc/dataset {:x [1 2 3 4] :y [5 5 5 5]})
          pl (sk/plan (-> ds (sk/lay-point :x :y)))]
      (is (some? pl)))))

(deftest categorical-single-category-test
  (testing "bar chart with only one category"
    (let [ds (tc/dataset {:cat ["a" "a" "a"] :val [1 2 3]})
          pl (sk/plan (-> ds (sk/lay-value-bar :cat :val)))]
      (is (= 1 (count (:panels pl)))))))

(deftest histogram-uniform-data-test
  (testing "histogram with all identical values"
    (let [ds (tc/dataset {:x [5 5 5 5 5]})
          pl (sk/plan (-> ds (sk/lay-histogram :x)))]
      (is (some? pl)))))

(deftest polar-coord-test
  (testing "polar coordinate plan structure"
    (let [ds (tc/dataset {:cat ["A" "B" "C"] :val [10 20 30]})
          views (-> ds
                    (sk/frame :cat :val)
                    sk/lay-bar
                    (sk/coord :polar))
          pl (sk/plan views)]
      (is (= :polar (get-in pl [:panels 0 :coord]))))))

(deftest flip-coord-test
  (testing "flipped coordinates swap x/y domains"
    (let [views (-> cat-ds
                    (sk/frame :cat :val)
                    sk/lay-bar
                    (sk/coord :flip))
          pl (sk/plan views)
          panel (first (:panels pl))]
      (is (= :flip (:coord panel))))))

(deftest labs-test
  (testing "axis labels propagate to plan via options"
    (let [pl (-> tiny-ds
                 (sk/frame :x :y)
                 sk/lay-point
                 (sk/options {:x-label "X Axis" :y-label "Y Axis"})
                 sk/plan)]
      (is (= "X Axis" (:x-label pl)))
      (is (= "Y Axis" (:y-label pl)))))
  (testing "title/subtitle/caption propagate via options"
    (let [pl (-> tiny-ds
                 (sk/frame :x :y)
                 sk/lay-point
                 (sk/options {:title "T" :subtitle "ST" :caption "C"})
                 sk/plan)]
      (is (= "T" (:title pl)))
      (is (= "ST" (:subtitle pl)))
      (is (= "C" (:caption pl))))))

(deftest log-scale-test
  (testing "log scale is recorded in plan"
    (let [ds (tc/dataset {:x [1 10 100 1000] :y [1 2 3 4]})
          views (-> ds
                    (sk/frame :x :y)
                    sk/lay-point
                    (sk/scale :x :log))
          pl (sk/plan views)
          panel (first (:panels pl))]
      (is (= :log (get-in panel [:x-scale :type]))))))

(deftest log-scale-nonpositive-test
  (testing "non-positive values are filtered on log-scaled x axis"
    (let [pl (sk/plan (-> {:x [0 -1 1 10 100] :y [1 2 3 4 5]}
                          (sk/lay-point :x :y)
                          (sk/scale :x :log)))
          layer (first (:layers (first (:panels pl))))
          group (first (:groups layer))]
      (is (= 3 (count (:xs group))))
      (is (= [1 10 100] (vec (:xs group))))))
  (testing "non-positive values are filtered on log-scaled y axis"
    (let [pl (sk/plan (-> {:x [1 2 3 4 5] :y [0 -1 1 10 100]}
                          (sk/lay-point :x :y)
                          (sk/scale :y :log)))
          layer (first (:layers (first (:panels pl))))
          group (first (:groups layer))]
      (is (= 3 (count (:xs group))))))
  (testing "all-positive data is not filtered"
    (let [pl (sk/plan (-> {:x [1 10 100] :y [1 2 3]}
                          (sk/lay-point :x :y)
                          (sk/scale :x :log)))
          layer (first (:layers (first (:panels pl))))
          group (first (:groups layer))]
      (is (= 3 (count (:xs group)))))))

(deftest infinity-filtering-test
  (testing "infinite y values are filtered with warning"
    (let [pl (sk/plan (-> {:x [1 2 3 4 5]
                           :y [10.0 Double/POSITIVE_INFINITY 30.0 Double/NEGATIVE_INFINITY 50.0]}
                          (sk/lay-point :x :y)))
          layer (first (:layers (first (:panels pl))))
          group (first (:groups layer))]
      (is (= 3 (count (:xs group))))
      (is (= [1 3 5] (vec (:xs group))))
      (is (= [10.0 30.0 50.0] (vec (:ys group))))))
  (testing "infinite x values are filtered"
    (let [pl (sk/plan (-> {:x [1.0 Double/POSITIVE_INFINITY 3.0]
                           :y [10 20 30]}
                          (sk/lay-point :x :y)))
          group (-> pl :panels first :layers first :groups first)]
      (is (= 2 (count (:xs group))))))
  (testing "SVG has no NaN after infinity filtering"
    (let [svg (sk/plot (-> {:x [1 2 3] :y [1.0 Double/POSITIVE_INFINITY 3.0]}
                           (sk/lay-point :x :y)))]
      (is (not (clojure.string/includes? (str svg) "NaN")))))
  (testing "all-finite data is not filtered"
    (let [pl (sk/plan (-> {:x [1 2 3] :y [10.0 20.0 30.0]}
                          (sk/lay-point :x :y)))
          group (-> pl :panels first :layers first :groups first)]
      (is (= 3 (count (:xs group)))))))

(deftest stacked-negative-domain-test
  (testing "all-negative stacked bars produce correct y-domain"
    (let [pl (sk/plan (-> {:category ["A" "A" "B" "B"]
                           :group ["g1" "g2" "g1" "g2"]
                           :value [-10 -20 -5 -15]}
                          (sk/lay-value-bar :category :value {:color :group :position :stack})))
          [lo hi] (:y-domain (first (:panels pl)))]
      (is (neg? lo) "lower bound should be negative for all-negative stacked data")
      (is (pos? hi) "upper bound includes 0 baseline with padding")))
  (testing "mixed positive/negative stacked bars span both sides"
    (let [pl (sk/plan (-> {:category ["A" "A" "B" "B"]
                           :group ["g1" "g2" "g1" "g2"]
                           :value [10 -20 5 -15]}
                          (sk/lay-value-bar :category :value {:color :group :position :stack})))
          [lo hi] (:y-domain (first (:panels pl)))]
      (is (neg? lo) "lower bound extends below zero")
      (is (pos? hi) "upper bound extends above zero")))
  (testing "all-negative stacked bars render without NaN"
    (let [svg (sk/plot (-> {:category ["A" "A" "B" "B"]
                            :group ["g1" "g2" "g1" "g2"]
                            :value [-10 -20 -5 -15]}
                           (sk/lay-value-bar :category :value {:color :group :position :stack})))]
      (is (not (clojure.string/includes? (str svg) "NaN"))))))

(deftest boolean-color-test
  (testing "Boolean false is not dropped as group key"
    (let [pl (-> {:x [1 2 3 4] :y [10 20 30 40] :flag [true false true false]}
                 (sk/lay-point :x :y {:color :flag})
                 sk/plan)
          groups (-> pl :panels first :layers first :groups)]
      (is (= 2 (count groups)) "two groups for true/false")
      (is (= "true" (:label (first groups))))
      (is (= "false" (:label (second groups))))
      ;; Both groups should get distinct palette colors (not default gray)
      (is (not= (:color (first groups)) (:color (second groups)))
          "true and false get different colors")))
  (testing "Legend matches rendering for boolean groups"
    (let [pl (-> {:x [1 2 3 4] :y [10 20 30 40] :flag [true false true false]}
                 (sk/lay-point :x :y {:color :flag})
                 sk/plan)
          legend-colors (mapv :color (:entries (:legend pl)))
          group-colors (mapv :color (-> pl :panels first :layers first :groups))]
      (is (= (count legend-colors) (count group-colors)))
      (is (= (set legend-colors) (set group-colors))
          "legend colors match group colors"))))

(deftest x-only-validation-test
  (testing "histogram rejects :y column"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"lay-histogram uses only the x column"
                          (sk/lay-histogram {:x [1 2 3] :y [4 5 6]} :x :y))))
  (testing "bar rejects :y column"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"lay-bar uses only the x column"
                          (sk/lay-bar {:x ["a" "b"] :y [1 2]} :x :y))))
  (testing "density rejects :y column"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"lay-density uses only the x column"
                          (sk/lay-density {:x [1 2 3] :y [4 5 6]} :x :y))))
  (testing "histogram with opts (not :y) still works"
    (is (some? (sk/lay-histogram {:x [1 2 3 4 5]} :x {:color :x})))))

(deftest multiple-layers-test
  (testing "plan with point + line layers"
    (let [views (-> tiny-ds
                    (sk/frame :x :y)
                    sk/lay-point
                    sk/lay-line)
          pl (sk/plan views)
          layers (get-in pl [:panels 0 :layers])]
      (is (= 2 (count layers))))))

(deftest color-groups-test
  (testing "color mapping with string values produces legend"
    (let [ds (tc/dataset {:x [1 2 3] :y [4 5 6] :g ["a" "b" "a"]})
          pl (sk/plan (-> ds (sk/lay-point :x :y {:color :g})))]
      (is (some? (:legend pl)))
      (is (= 2 (count (get-in pl [:legend :entries])))))))

(deftest plan-dimensions-test
  (testing "custom width and height"
    (let [views (-> tiny-ds (sk/frame :x :y) sk/lay-point)
          pl (sk/plan views {:width 800 :height 300})]
      (is (= 800 (:width pl)))
      (is (= 300 (:height pl))))))

(deftest cross-grid-strip-labels-test
  (testing "cross plot (full grid) shows all strip labels"
    (let [ds (tc/dataset {:a [1 2 3 4 5] :b [5 4 3 2 1] :c [2 4 6 8 10]})
          views (-> ds
                    (sk/frame (sk/cross [:a :b :c] [:a :b :c]))
                    sk/lay-point)
          svg (sk/plot views)
          s (sk/svg-summary svg)
          texts (:texts s)]
      (is (= 9 (:panels s)))
      (is (some #{"a"} texts))
      (is (some #{"b"} texts))
      (is (some #{"c"} texts)))))

(deftest save-test
  (testing "sk/save writes valid SVG file"
    (let [ds (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                         {:key-fn keyword})
          path (str (java.io.File/createTempFile "napkinsketch" ".svg"))
          views (-> ds (sk/frame :sepal_length :sepal_width)
                    (sk/lay-point {:color :species}))]
      (sk/save views path)
      (let [content (slurp path)]
        (is (.startsWith content "<?xml"))
        (is (.contains content "<svg"))
        (is (.contains content "setosa")))
      (.delete (java.io.File. path)))))

(deftest temporal-epoch-ms-test
  (testing "LocalDate converts to epoch-ms"
    (let [d (jt/local-date 2025 1 1)
          ms (resolve/temporal->epoch-ms d)]
      (is (double? ms))
      (is (== ms (* (.toEpochDay d) 86400000)))))
  (testing "LocalDateTime preserves sub-day precision"
    (let [dt (jt/local-date-time 2025 3 15 12 30 0)
          ms (resolve/temporal->epoch-ms dt)]
      (is (double? ms))
      ;; Should differ from midnight by 12.5 hours in ms
      (let [midnight-ms (resolve/temporal->epoch-ms (jt/local-date 2025 3 15))]
        (is (== (- ms midnight-ms) (* 12.5 3600000))))))
  (testing "Temporal plan has datetime ticks"
    (let [pl (-> (tc/dataset {:date [(jt/local-date 2025 1 1)
                                     (jt/local-date 2025 6 1)
                                     (jt/local-date 2025 12 1)]
                              :val [10 20 30]})
                 (sk/frame :date :val)
                 sk/lay-point
                 sk/plan)]
      (is (= 1 (count (:panels pl))))
      (is (seq (get-in pl [:panels 0 :x-ticks :labels]))))))

(deftest format-log-ticks-test
  (testing "Powers of 10 >= 1 render as integers"
    (is (= ["1" "10" "100" "1000"]
           (scale/format-log-ticks [1.0 10.0 100.0 1000.0]))))
  (testing "Decimal powers keep decimal form"
    (is (= ["0.001" "0.01" "0.1" "1" "10"]
           (scale/format-log-ticks [0.001 0.01 0.1 1.0 10.0])))))

(deftest string-column-names-test
  (let [iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                         {:key-fn keyword})]
    (testing "String column refs in 3-arity view"
      (let [s (-> iris (sk/frame "sepal_length" "sepal_width")
                  sk/lay-point sk/plot sk/svg-summary)]
        (is (= 150 (:points s)))))
    (testing "String columns in vector spec"
      (let [s (-> iris (sk/frame [["sepal_length" "sepal_width"]])
                  sk/lay-point sk/plot sk/svg-summary)]
        (is (= 150 (:points s)))))
    (testing "String column in mark options"
      (let [s (-> iris (sk/frame :sepal_length :sepal_width)
                  (sk/lay-point {:color "species"}) sk/plot sk/svg-summary)]
        (is (= 150 (:points s)))
        (is (some #{"setosa"} (:texts s)))))
    (testing "Dataset with string column names"
      (let [ds (tc/dataset {"x" [1 2 3] "y" [4 5 6]})
            s (-> ds (sk/frame :x :y) sk/lay-point sk/plot sk/svg-summary)]
        (is (= 3 (:points s)))))
    (testing "Dataset with string columns + string spec"
      (let [ds (tc/dataset {"x" [1 2 3] "y" [4 5 6]})
            s (-> ds (sk/frame "x" "y") sk/lay-point sk/plot sk/svg-summary)]
        (is (= 3 (:points s)))))
    (testing "String in facet"
      (let [s (-> iris (sk/frame :sepal_length :sepal_width)
                  (sk/facet "species") sk/lay-point sk/plot sk/svg-summary)]
        (is (= 3 (:panels s)))))
    (testing "String in cross"
      (is (= 9 (count (sk/cross ["a" "b" "c"] ["a" "b" "c"])))))
    (testing "Literal color string still works"
      (let [v (-> (tc/dataset {:x [1 2 3] :y [4 5 6]})
                  (sk/frame :x :y)
                  (sk/lay-point {:color "#FF0000"})
                  sk/plot)]
        (is (= 3 (:points (sk/svg-summary v))))))
    (testing "Typo still gives error at plan time"
      (is (thrown? clojure.lang.ExceptionInfo
                   (-> iris (sk/frame :sepl_length :sepal_width)
                       sk/lay-point sk/plot))))))

(deftest string-column-in-lay-test
  (testing "String column names in lay-point directly (no sk/view)"
    (let [s (-> {"x" [1 2 3] "y" [4 5 6]}
                (sk/lay-point "x" "y") sk/plot sk/svg-summary)]
      (is (= 3 (:points s)))))
  (testing "String column names in lay-line directly"
    (let [s (-> {"x" [1 2 3] "y" [4 5 6]}
                (sk/lay-line "x" "y") sk/plot sk/svg-summary)]
      (is (= 1 (:lines s)))))
  (testing "String column in lay-histogram directly"
    (let [s (-> {"x" [1 2 3 4 5 6 7 8 9 10]}
                (sk/lay-histogram "x") sk/plot sk/svg-summary)]
      (is (pos? (:polygons s))))))

(deftest named-color-test
  (testing "Named color strings work as fixed colors"
    (let [s (-> {:x [1 2 3] :y [4 5 6]}
                (sk/frame :x :y)
                (sk/lay-point {:color "red"})
                sk/plot sk/svg-summary)]
      (is (= 3 (:points s)))))
  (testing "Named color produces correct RGBA"
    (let [pl (-> {:x [1 2 3] :y [4 5 6]}
                 (sk/frame :x :y)
                 (sk/lay-point {:color "steelblue"})
                 sk/plan)
          c (:color (first (:groups (first (:layers (first (:panels pl)))))))]
      (is (> (nth c 2) 0.5) "steelblue should have high blue channel")))
  (testing "Unknown color string gives helpful error"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"Unknown color"
                          (-> {:x [1 2 3] :y [4 5 6]}
                              (sk/frame :x :y)
                              (sk/lay-point {:color "notacolor"})
                              sk/plot)))))

(deftest schema-all-marks-test
  (testing "Every mark type produces a valid plan"
    (let [iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                           {:key-fn keyword})
          xy-ds (tc/dataset {:x (range 10) :y (range 10)})
          eb-ds (tc/dataset {:x ["a" "b"] :y [10 20] :y-min [8 17] :y-max [12 23]})
          txt-ds (tc/dataset {:x [1 2] :y [3 4] :n ["a" "b"]})
          cases [["point" (-> iris (sk/frame :sepal_length :sepal_width) (sk/lay-point {:color :species}))]
                 ["bar" (-> iris (sk/frame :species) sk/lay-bar)]
                 ["histogram" (-> iris (sk/frame :sepal_length) sk/lay-histogram)]
                 ["line" (-> xy-ds (sk/frame :x :y) sk/lay-line)]
                 ["step" (-> xy-ds (sk/frame :x :y) sk/lay-step)]
                 ["lm" (-> iris (sk/frame :sepal_length :sepal_width) (sk/lay-smooth {:stat :linear-model :confidence-band true}))]
                 ["loess" (-> iris (sk/frame :sepal_length :sepal_width) sk/lay-smooth)]
                 ["area" (-> xy-ds (sk/frame :x :y) sk/lay-area)]
                 ["boxplot" (-> iris (sk/frame :species :sepal_width) sk/lay-boxplot)]
                 ["violin" (-> iris (sk/frame :species :sepal_width) sk/lay-violin)]
                 ["density" (-> iris (sk/frame :sepal_length) sk/lay-density)]
                 ["ridgeline" (-> iris (sk/frame :species :sepal_width) sk/lay-ridgeline)]
                 ["text" (-> txt-ds (sk/frame :x :y) (sk/lay-text {:text :n}))]
                 ["tile" (-> iris (sk/frame :sepal_length :sepal_width) sk/lay-tile)]
                 ["contour" (-> iris (sk/frame :sepal_length :sepal_width) sk/lay-contour)]
                 ["errorbar" (-> eb-ds (sk/frame :x :y) (sk/lay-errorbar {:y-min :y-min :y-max :y-max}))]
                 ["lollipop" (-> eb-ds (sk/frame :x :y) sk/lay-lollipop)]
                 ["summary" (-> iris (sk/frame :species :sepal_width) sk/lay-summary)]]]
      (doseq [[mark-name views] cases]
        (testing mark-name
          (is (sk/valid-plan? (sk/plan views {:validate false}))))))))

(deftest validation-test
  (testing "numeric faceting produces correct panels"
    (is (= 3 (-> {:x [1 2 3 4 5 6] :y [10 20 30 40 50 60]
                  :f [1.0 1.0 2.0 2.0 3.0 3.0]}
                 (sk/lay-point :x :y) (sk/facet :f) sk/plan :panels count)))
    (is (= 3 (-> {:x [1 2 3 4 5 6] :y [10 20 30 40 50 60]
                  :s ["a" "a" "b" "b" "c" "c"]}
                 (sk/lay-point :x :y) (sk/facet :s) sk/plan :panels count))))

  (testing "histogram on categorical column throws"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"numeric"
                          (-> {:x ["a" "b" "c"]} (sk/lay-histogram :x) sk/plan))))

  (testing "lm on categorical x throws"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"numeric"
                          (-> {:species ["a" "b" "c"] :y [1 2 3]}
                              (sk/lay-smooth :species :y {:stat :linear-model}) sk/plan))))

  (testing "loess on categorical x throws"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"numeric"
                          (-> {:species ["a" "b" "c" "d"] :y [1 2 3 4]}
                              (sk/lay-smooth :species :y) sk/plan))))

  (testing "errorbar without y-min/y-max throws"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"y-min.*y-max"
                          (-> {:x ["a" "b" "c"] :y [1 2 3]}
                              (sk/lay-errorbar :x :y) sk/plan))))

  (testing "text without :text column throws"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"text"
                          (-> {:x [1 2 3] :y [10 20 30]}
                              (sk/lay-text :x :y) sk/plan))))

  (testing "scale channel validation"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Scale channel"
                          (sk/scale [] :z :log))))

  (testing "coord validation"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Coordinate"
                          (sk/coord [] :invalid))))

  (testing "y-type propagated when x and y reference the same column"
    ;; Regression: previously (= x-res y-res) returned nil for y-type,
    ;; letting ClassCastException escape instead of a clear error.
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"numeric"
                          (-> {:species ["a" "b" "c"]}
                              (sk/lay-summary :species :species) sk/plan)))))

(deftest aesthetic-column-validation-test
  ;; persona-16 B1: validate-columns covers :color/:size/:alpha/:shape/:group/
  ;; :text/:y-min/:y-max/:fill, not just :x/:y. Closes the C2 epic:
  ;; P5-R2 C1, P9-R2 F5/F6, Skept-R4 F2/F5/F6, P7-R2 F1/F2/F3, P11-R2 F5,
  ;; P3-R2 footgun.
  (let [data {:x [1.0 2.0 3.0] :y [10.0 20.0 30.0] :g ["a" "b" "c"]}]
    (testing "typoed :color keyword throws with key and column name"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Column :speices \(from :color\) not found"
                            (-> data (sk/lay-point :x :y {:color :speices}) sk/plan))))

    (testing "typoed :size keyword throws"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Column :bogus \(from :size\) not found"
                            (-> data (sk/lay-point :x :y {:size :bogus}) sk/plan))))

    (testing "typoed :alpha keyword throws"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Column :bogus \(from :alpha\) not found"
                            (-> data (sk/lay-point :x :y {:alpha :bogus}) sk/plan))))

    (testing "typoed :group keyword throws"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Column :bogus \(from :group\) not found"
                            (-> data (sk/lay-point :x :y {:group :bogus}) sk/plan))))

    (testing "typoed :y-min keyword throws"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Column :bogus1 \(from :y-min\) not found"
                            (-> data (sk/lay-errorbar :x :y {:y-min :bogus1 :y-max :bogus2}) sk/plan))))

    (testing "literal :color string is not flagged (named color)"
      (is (some? (-> data (sk/lay-point :x :y {:color "red"}) sk/plan :panels))))

    (testing "literal :color hex is not flagged"
      (is (some? (-> data (sk/lay-point :x :y {:color "#FF0000"}) sk/plan :panels))))

    (testing "good :color column still works"
      (is (some? (-> data (sk/lay-point :x :y {:color :g}) sk/plan :panels))))

    (testing "nil :color (explicit cancellation) skipped by validator"
      (is (some? (-> data (sk/lay-point :x :y {:color nil}) sk/plan :panels))))

    (testing "string column ref in :color resolves correctly"
      (is (some? (-> {"a" [1 2 3] "b" [10 20 30] "g" ["x" "y" "z"]}
                     (sk/lay-point "a" "b" {:color "g"}) sk/plan :panels))))

    (testing "available columns listed in error for typo recovery"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Available: \(:g :x :y\)"
                            (-> data (sk/lay-point :x :y {:color :typo}) sk/plan))))))

(deftest facet-validation-test
  ;; persona-16 B3. Closes P9-R2 F9, Skept-R4 F7, P3-R2 Footgun 5.
  (let [data {:x [1 2 3 4 5 6] :y [10 20 30 40 50 60]
              :g ["a" "b" "a" "b" "a" "b"]
              :h ["x" "y" "x" "y" "x" "y"]}]
    (testing "typoed facet column throws with available columns"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Facet column :speices.*not found"
                            (-> data (sk/lay-point :x :y) (sk/facet :speices) sk/plan))))

    (testing "vector facet spec is rejected, points at facet-grid"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"facet-grid"
                            (-> data (sk/lay-point :x :y) (sk/facet [:g :h]) sk/plan))))

    (testing "valid facet column still works"
      (is (= 2 (-> data (sk/lay-point :x :y) (sk/facet :g) sk/plan :panels count))))

    (testing "facet-grid with valid columns still works"
      (is (= 4 (-> data (sk/lay-point :x :y) (sk/facet-grid :g :h) sk/plan :panels count))))

    (testing "typoed facet-grid column throws"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Facet column :speices.*not found"
                            (-> data (sk/lay-point :x :y) (sk/facet-grid :speices :h) sk/plan))))))

(deftest lay-method-auto-infer-test
  ;; persona-16 B4. Closes Skept-R4 F1, P5-R2 C2.
  (let [four-col {:a [1 2 3] :b [4 5 6] :c [7 8 9] :d [10 11 12]}
        three-col {:x [1 2 3] :y [10 20 30] :g ["a" "b" "c"]}]
    (testing "4+ column auto-infer throws with helpful message"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Cannot auto-infer columns from 4 columns"
                            (-> four-col sk/lay-point sk/plan))))

    (testing "error message suggests explicit x/y"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"sk/lay-point data :x :y"
                            (-> four-col sk/lay-point sk/plan))))

    (testing "error message lists available columns"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Available columns: \(:a :b :c :d\)"
                            (-> four-col sk/lay-point sk/plan))))

    (testing "3-column auto-infer still works"
      (is (= 1 (-> three-col sk/lay-point sk/plan :panels count))))

    (testing "4+ column with explicit x/y still works"
      (is (= 1 (-> four-col (sk/lay-point :a :b) sk/plan :panels count))))))

(deftest tile-color-synonym-test
  ;; persona-11-R2 F8: lay-tile used to silently paint every tile the
  ;; midpoint color when the user passed {:color :value} instead of
  ;; {:fill :value}. Now :color is accepted as a synonym (only when
  ;; stat is :bin2d, i.e. from `sk/lay-tile` -- NOT for density2d/
  ;; contour which have their own intentional kde2d stat).
  (let [data (tc/dataset {:row (mapcat #(repeat 6 %) (range 6))
                          :col (flatten (repeat 6 (range 6)))
                          :value (map #(Math/sin (* % 0.5)) (range 36))})]

    (testing "lay-tile with :color produces the same plan as :fill"
      (let [p-color (-> data (sk/frame :col :row {:color :value})
                        sk/lay-tile sk/plan)
            p-fill (-> data (sk/frame :col :row {:fill :value})
                       sk/lay-tile sk/plan)
            tiles-color (-> p-color :panels first :layers first :tiles)
            tiles-fill (-> p-fill :panels first :layers first :tiles)]
        (is (= (count tiles-color) (count tiles-fill))
            "same number of tiles in both paths")
        (is (= (distinct (map :color tiles-color))
               (distinct (map :color tiles-fill)))
            "same distinct colors in both paths")
        (is (> (count (distinct (map :color tiles-color))) 1)
            ":color path produces varying tile colors, not uniform")))

    (testing "lay-density-2d with :color :species (categorical) still works"
      ;; This used to hit the over-broad tile-override; must stay :density-2d.
      (is (some? (-> (tc/dataset {:x [1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0 10.0]
                                  :y [1.0 2.1 3.0 4.2 5.1 6.0 7.0 8.1 9.0 10.0]
                                  :g ["a" "a" "a" "a" "a" "b" "b" "b" "b" "b"]})
                     (sk/frame :x :y {:color :g})
                     sk/lay-density-2d sk/plan))))))

(deftest mixed-type-column-test
  ;; persona-skeptical-round-4 F5: a column whose values are heterogeneous
  ;; (number + string + keyword) used to crash with a multi-KB Malli
  ;; "Plan does not conform to schema" dump. Now thrown with a clear
  ;; column name and the discovered types.
  (testing "mixed-type :y column throws with type list"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"has mixed value types"
                          (-> (tc/dataset {:x [1 2 3 4 5]
                                           :y [1.0 "two" 3.0 :four 5.0]})
                              (sk/lay-point :x :y) sk/plan))))

  (testing "all-string column is fine (categorical)"
    (is (some? (-> {:x [1 2 3] :g ["a" "b" "c"]} (sk/lay-bar :g) sk/plan))))

  (testing "all-numeric column is fine"
    (is (some? (-> {:x [1.0 2.0 3.0] :y [4.0 5.0 6.0]}
                   (sk/lay-point :x :y) sk/plan)))))

(deftest aesthetic-cross-type-lookup-test
  ;; persona-skeptical-round-4 F2: aesthetic columns must work whether
  ;; the dataset has keyword or string column names. Build-legend used
  ;; to crash with "No implementation of method :elemwise-datatype" when
  ;; given a string-keyed dataset and a keyword color ref.
  (testing "string-keyed dataset + keyword :color produces a working plot"
    (let [str-ds (tc/dataset {"x" [1.0 2.0 3.0]
                              "y" [10.0 20.0 30.0]
                              "g" ["A" "B" "A"]})
          pl (-> str-ds (sk/lay-point :x :y {:color :g}) sk/plan)]
      (is (= 1 (count (:panels pl))))
      (is (some? (:legend pl)) "legend built without crash")))

  (testing "keyword-keyed dataset + string :color produces a working plot"
    (let [kw-ds (tc/dataset {:x [1.0 2.0 3.0] :y [10.0 20.0 30.0] :g ["A" "B" "A"]})
          pl (-> kw-ds (sk/lay-point :x :y {:color "g"}) sk/plan)]
      (is (= 1 (count (:panels pl)))))))

(deftest layer-x-y-override-test
  ;; persona-skeptical-round-4 F9: layer-level :x/:y overrides used to
  ;; warn as unknown options even though they actually worked. Now :x/:y
  ;; are accepted on layer opts (universal-layer-options).
  (testing "layer-level :x / :y on lay-* doesn't trigger an unknown-opt warning"
    (let [out (with-out-str
                (-> {:a [1 2 3] :b [4 5 6] :c [7 8 9] :d [10 11 12]}
                    (sk/frame :a :b)
                    (sk/lay-point {:x :c :y :d})))]
      (is (not (re-find #"does not recognize option" out))))))

(deftest plot-level-keys-stripped-from-wrong-scope-test
  ;; persona-skeptical-round-6 F1/F5/F6: plot-level keys like
  ;; :x-scale and :coord used to emit an "unrecognized option"
  ;; warning but still propagate into the layer's or frame's
  ;; mapping and leak into the final panel (identical output to
  ;; the canonical sk/scale / sk/coord form). Now the warning is
  ;; honest: unknown keys are stripped from the mapping, so the
  ;; panel uses default scales/coord.
  (let [ds {:x [1 10 100] :y [1 2 3]}]

    (testing "layer-level :x-scale is stripped, not honored"
      (let [fr (-> ds (sk/frame :x :y) (sk/lay-point {:x-scale {:type :log}}))
            layer-mapping (:mapping (first (:layers fr)))
            panel (first (:panels (sk/plan fr)))]
        (is (not (contains? (or layer-mapping {}) :x-scale))
            ":x-scale should not appear in layer mapping")
        (is (= :linear (get-in panel [:x-scale :type]))
            "panel x-scale should stay at default :linear")))

    (testing "frame-level :x-scale is stripped, not honored"
      (let [fr (-> ds (sk/frame :x :y {:x-scale {:type :log}}) sk/lay-point)
            panel (first (:panels (sk/plan fr)))]
        (is (not (contains? (:mapping fr) :x-scale))
            ":x-scale should not appear in frame mapping")
        (is (= :linear (get-in panel [:x-scale :type]))
            "panel x-scale should stay at default :linear")))

    (testing "layer-level :coord is stripped, not honored"
      (let [fr (-> ds (sk/frame :x :y) (sk/lay-point {:coord :flip}))
            layer-mapping (:mapping (first (:layers fr)))
            panel (first (:panels (sk/plan fr)))]
        (is (not (contains? (or layer-mapping {}) :coord))
            ":coord should not appear in layer mapping")
        (is (not= :flip (:coord panel))
            "panel coord should not reflect a stripped layer-level :flip")))

    (testing "canonical sk/scale still works"
      (let [fr (-> ds (sk/frame :x :y) sk/lay-point (sk/scale :x :log))
            panel (first (:panels (sk/plan fr)))]
        (is (= :log (get-in panel [:x-scale :type])))))

    (testing "canonical sk/coord still works"
      (let [fr (-> ds (sk/frame :x :y) sk/lay-point (sk/coord :flip))
            panel (first (:panels (sk/plan fr)))]
        (is (= :flip (:coord panel)))))))

(deftest input-validation-misc-test
  ;; persona-09-R2 F10/F11/F12. Low-severity input validation.
  (testing "options with width/height = 0 throws"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #":width must be a positive number"
                          (-> {:a [1 2 3] :b [4 5 6]} (sk/lay-point :a :b)
                              (sk/options {:width 0}))))
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #":height must be a positive number"
                          (-> {:a [1 2 3] :b [4 5 6]} (sk/lay-point :a :b)
                              (sk/options {:height -100})))))

  (testing "histogram with :bins <= 0 throws"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #":bins must be a positive number"
                          (-> {:x [1 2 3]} (sk/lay-histogram :x {:bins 0}) sk/plan)))
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #":bins must be a positive number"
                          (-> {:x [1 2 3]} (sk/lay-histogram :x {:bins -1}) sk/plan))))

  (testing "histogram with :binwidth <= 0 throws"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #":binwidth must be a positive number"
                          (-> {:x [1 2 3]} (sk/lay-histogram :x {:binwidth 0}) sk/plan)))))

(deftest alpha-on-marks-test
  ;; persona-16 H4 + H7. Closes P11-R2 F1, F2.
  ;; H4: line/step/lm/loess/errorbar/lollipop -- alpha was put in style but
  ;;     hardcoded to 1.0 in the renderer.
  ;; H7: boxplot/pointrange/text/label -- alpha wasn't in style at all.
  (let [data {:x [1.0 2.0 3.0 4.0 5.0] :y [10.0 20.0 15.0 25.0 18.0]
              :lab ["A" "B" "C" "D" "E"]
              :cat ["a" "b" "a" "b" "a"]}
        tmp (java.io.File/createTempFile "napkinsketch-alpha-test" ".svg")
        renders-with-opacity? (fn [sketch op-pattern]
                                (sk/save sketch (.getAbsolutePath tmp))
                                (boolean (re-find op-pattern (slurp tmp))))]
    (testing "alpha 0.5 propagates to :line"
      (is (renders-with-opacity? (-> data (sk/lay-line :x :y {:alpha 0.5}))
                                 #"opacity=\"0.5\"")))

    (testing "alpha 0.5 propagates to :step"
      (is (renders-with-opacity? (-> data (sk/lay-step :x :y {:alpha 0.5}))
                                 #"opacity=\"0.5\"")))

    (testing "alpha 0.5 propagates to :lm regression line"
      (is (renders-with-opacity? (-> data (sk/lay-smooth :x :y {:stat :linear-model :alpha 0.5}))
                                 #"opacity=\"0.5\"")))

    (testing "alpha 0.4 propagates to :errorbar"
      (is (renders-with-opacity? (-> {:x [1.0 2.0] :y [10.0 20.0]
                                      :lo [5.0 15.0] :hi [15.0 25.0]}
                                     (sk/lay-errorbar :x :y {:y-min :lo :y-max :hi :alpha 0.4}))
                                 #"opacity=\"0.4\"")))

    (testing "alpha 0.6 propagates to :lollipop"
      (is (renders-with-opacity? (-> data (sk/lay-lollipop :cat :y {:alpha 0.6}))
                                 #"opacity=\"0.6\"")))

    (testing "alpha 0.3 propagates to :text"
      (is (renders-with-opacity? (-> data (sk/lay-text :x :y {:text :lab :alpha 0.3}))
                                 #"opacity=\"0.3\"")))

    (testing "alpha 0.7 propagates to :label"
      (is (renders-with-opacity? (-> data (sk/lay-label :x :y {:text :lab :alpha 0.7}))
                                 #"opacity=\"0.7\"")))

    (testing "alpha 0.4 propagates to :boxplot"
      (is (renders-with-opacity? (-> data (sk/lay-boxplot :cat :y {:alpha 0.4}))
                                 #"opacity=\"0.4\"")))

    (testing "alpha 0.8 propagates to :pointrange (via :summary)"
      (is (renders-with-opacity? (-> data (sk/lay-summary :cat :y {:alpha 0.8}))
                                 #"opacity=\"0.8\"")))

    (.delete tmp)))

(deftest bar-numeric-x-test
  ;; persona-16 H9. Closes P9-R2 F7.
  (testing "lay-bar with numeric x throws clear error"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"Stat :count.*requires a categorical column for :x"
                          (-> {:x [1 2 3 4 5]} (sk/lay-bar :x) sk/plan))))

  (testing "lay-value-bar with numeric x throws clear error"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"Mark :rect.*requires a categorical column for :x"
                          (-> {:x [1.0 2.0 3.0] :y [10 20 30]}
                              (sk/lay-value-bar :x :y) sk/plan))))

  (testing "lay-bar with categorical x still works"
    (is (some? (-> {:cat ["a" "b" "c"]} (sk/lay-bar :cat) sk/plan))))

  (testing "lay-value-bar with categorical x still works"
    (is (some? (-> {:cat ["a" "b" "c"] :y [10 20 30]}
                   (sk/lay-value-bar :cat :y) sk/plan)))))

(deftest unknown-option-warning-test
  ;; persona-16 H1. Closes P1-R3 F2/F3/F4, Skept-R4 F4, P5-R2 L4.
  (let [data {:x [1 2 3] :y [10 20 30]}]
    (testing "sk/frame (position + opts) warns on unknown option key"
      (let [out (with-out-str (-> data (sk/frame :x :y {:colour :y}) sk/lay-point))]
        (is (re-find #"Warning: sk/frame does not recognize option" out))
        (is (re-find #":colour" out))))

    (testing "sk/frame (aesthetic-only opts) warns on unknown option key"
      (let [out (with-out-str (sk/frame data {:colour :y}))]
        (is (re-find #"Warning: sk/frame does not recognize option" out))))

    (testing "sk/options warns on unknown option key"
      (let [out (with-out-str (-> data (sk/lay-point :x :y) (sk/options {:titel "Hi"})))]
        (is (re-find #"Warning: sk/options does not recognize option" out))
        (is (re-find #":titel" out))))

    (testing "valid options stay quiet"
      (is (= "" (with-out-str (-> data (sk/frame :x :y {:color :y}))))))))

(deftest raw-data-plan-plot-test
  ;; persona-16 H2. Closes P1-R3 F2/F3, P9-R2 L2.
  (let [data {:x [1 2 3] :y [10 20 30]}]
    (testing "sk/plan on raw data does not throw"
      (is (some? (-> data sk/plan))))

    (testing "sk/plot on raw data does not throw"
      (is (some? (-> data sk/plot))))))

(deftest unknown-layer-type-test
  ;; persona-16 H3. Closes Skept-R4 F3.
  (let [data {:x [1 2 3] :y [10 20 30]}]
    (testing "unknown layer type keyword throws with registered list"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Unknown layer type: :stackedbar.*Registered layer types"
                            (-> data (sk/frame :x :y) (sk/lay :stackedbar) sk/plan))))))

(deftest scale-type-validation-test
  ;; persona-16 H12. Closes P1-R3 F5.
  (let [data {:x [1 2 3] :y [10 20 30]}]
    (testing "sk/scale with bogus type throws at API call time"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"Unknown scale type: :bogus.*linear.*log"
                            (-> data (sk/lay-point :x :y) (sk/scale :x :bogus)))))

    (testing "sk/scale :linear and :log accepted"
      (is (some? (-> data (sk/lay-point :x :y) (sk/scale :x :linear))))
      (is (some? (-> data (sk/lay-point :x :y) (sk/scale :y :log)))))))

(deftest keyword-category-label-test
  ;; persona-16 H10. Closes P17 #9, #11.
  (let [data {:cat [:widget :gadget :sprocket] :n [10 20 15]}]
    (testing "keyword categorical x-axis labels render without leading colons"
      (let [svg (-> data (sk/lay-bar :cat) sk/plot pr-str)]
        (is (zero? (count (re-seq #":(widget|gadget|sprocket)" svg))))
        (is (= 3 (count (re-seq #"\"widget\"|\"gadget\"|\"sprocket\"" svg))))))

    (testing "keyword categorical legend labels render without leading colons"
      (let [svg (-> {:x [1 2 3] :y [10 20 30] :g [:cat-0 :cat-1 :cat-2]}
                    (sk/lay-point :x :y {:color :g}) sk/plot pr-str)]
        (is (zero? (count (re-seq #":cat-[0-9]" svg))))))

    (testing "facet strip labels strip keyword colons"
      (let [svg (-> {:x [1 2 3 4] :y [10 20 30 40] :grp [:a :b :a :b]}
                    (sk/lay-point :x :y) (sk/facet :grp) sk/plot pr-str)]
        (is (zero? (count (re-seq #":(a|b)" svg))))))

    (testing "string categories still work (regression check)"
      (let [svg (-> {:cat ["widget" "gadget"] :n [10 20]} (sk/lay-bar :cat) sk/plot pr-str)]
        (is (= 2 (count (re-seq #"\"widget\"|\"gadget\"" svg))))))))

(deftest nil-in-aesthetic-columns-test
  ;; persona-16 B2. Closes C3 epic: P5-R2 C1, P9-R2 F1/F2,
  ;; P7-R2 F1/F2/F3, P11-R2 F5.
  (testing "nil in numeric :size column drops rows cleanly"
    (let [p (-> {:x [1.0 2.0 3.0 4.0] :y [10.0 20.0 30.0 40.0]
                 :sz [1.0 nil 3.0 nil]}
                (sk/lay-point :x :y {:size :sz}) sk/plan)]
      (is (= 1 (count (:panels p))))
      (is (some? (:size-legend p)) "size legend still built from 2 valid rows")))

  (testing "nil in numeric :color column drops rows cleanly"
    (let [p (-> {:x [1.0 2.0 3.0 4.0] :y [10.0 20.0 30.0 40.0]
                 :c [1.0 2.0 nil 4.0]}
                (sk/lay-point :x :y {:color :c}) sk/plan)]
      (is (= 1 (count (:panels p))))
      (is (some? (:legend p)) "numeric color legend still built")))

  (testing "NaN in numeric :size column drops rows cleanly"
    (let [p (-> {:x [1.0 2.0 3.0] :y [10.0 20.0 30.0]
                 :sz [1.0 ##NaN 3.0]}
                (sk/lay-point :x :y {:size :sz}) sk/plan)]
      (is (= 1 (count (:panels p))))))

  (testing "all-nil :size column renders without legend"
    (let [p (-> {:x [1.0 2.0] :y [10.0 20.0] :sz [nil nil]}
                (sk/lay-point :x :y {:size :sz}) sk/plan)]
      (is (= 1 (count (:panels p))))
      (is (nil? (:size-legend p)) "size legend suppressed when all nil")))

  (testing "all-nil :alpha column renders without legend"
    (let [p (-> {:x [1.0 2.0] :y [10.0 20.0] :a [nil nil]}
                (sk/lay-point :x :y {:alpha :a}) sk/plan)]
      (is (= 1 (count (:panels p))))
      (is (nil? (:alpha-legend p)) "alpha legend suppressed when all nil")))

  (testing "nil in :y-min / :y-max drops rows for errorbar"
    (let [p (-> {:x [1.0 2.0 3.0] :y [10.0 20.0 30.0]
                 :lo [5.0 nil 25.0] :hi [15.0 nil 35.0]}
                (sk/lay-errorbar :x :y {:y-min :lo :y-max :hi}) sk/plan)]
      (is (= 1 (count (:panels p)))))))

(deftest frame-color-mapping-composes-test
  ;; persona-16 B5. Closes P1-R3 F1, P9-R2 F14.
  ;; A color aesthetic on a frame composes with the position mapping.
  (let [data {:x [1 2 3] :y [10 20 30] :g ["a" "b" "c"]}]
    (testing "leaf frame renders with merged color mapping"
      (let [p (-> data (sk/frame :x :y {:color :g}) sk/lay-point sk/plan)
            layer (first (:layers (first (:panels p))))]
        (is (= 3 (count (:groups layer))) "one group per :g value")))))

(deftest facet-broadcast-test
  (testing "Global method (loess) applies to all facet panels"
    (let [iris (rdatasets/datasets-iris)
          s (-> iris
                (sk/lay-point :sepal-length :sepal-width {:color :species})
                (sk/facet :species)
                sk/lay-smooth
                sk/plot
                sk/svg-summary)]
      (is (= 3 (:panels s)) "Faceted into 3 panels by species")
      (is (= 3 (:lines s)) "LOESS should appear in all 3 panels")
      (is (= 150 (:points s)) "Scatter points still render")))
  (testing "Existing faceted behavior unchanged"
    (let [s (-> (rdatasets/datasets-iris)
                (sk/lay-point :sepal-length :sepal-width {:color :species})
                (sk/facet :species)
                sk/plot
                sk/svg-summary)]
      (is (= 3 (:panels s)))
      (is (= 150 (:points s))))))

;; ============================================================
;; PROPOSED API () tests
;; ============================================================

(defn- summary
  "Render a frame and return svg-summary."
  [fr]
  (sk/svg-summary (sk/plot fr)))

(deftest basic-test
  (let [iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                         {:key-fn keyword})]
    (testing "Scatter + lm"
      (let [s (summary (-> (sk/frame iris {:color :species})
                           (sk/frame :sepal_length :sepal_width)
                           (sk/lay-point {:alpha 0.5})
                           (sk/lay-smooth {:stat :linear-model})))]
        (is (= 1 (:panels s)))
        (is (= 150 (:points s)))
        (is (= 3 (:lines s)))))

    (testing "SPLOM inference"
      (let [s (summary (-> (sk/frame iris {:color :species})
                           (sk/frame (sk/cross [:sepal_length :sepal_width :petal_length]
                                               [:sepal_length :sepal_width :petal_length]))))]
        (is (= 9 (:panels s)))
        (is (= 900 (:points s)))))

    (testing "Simpson's paradox via nil cancellation"
      (let [s (summary (-> (sk/frame iris {:color :species})
                           (sk/frame :sepal_length :sepal_width)
                           (sk/lay-point {:alpha 0.4})
                           (sk/lay-smooth {:stat :linear-model})
                           (sk/lay-smooth {:stat :linear-model :color nil})))]
        (is (= 1 (:panels s)))
        (is (= 150 (:points s)))
        (is (= 4 (:lines s)))))

    (testing "Faceted + per-view layers"
      (let [s (summary (-> (sk/frame iris)
                           (sk/frame :sepal_length :sepal_width)
                           sk/lay-point
                           sk/lay-smooth
                           (sk/facet :species)))]
        (is (= 3 (:panels s)))
        (is (= 150 (:points s)))
        (is (= 3 (:lines s)))))

    (testing "Data-first (no sketch call)"
      (let [s (summary (-> iris
                           (sk/frame :sepal_length :sepal_width {:color :species})
                           (sk/lay-point {:alpha 0.5})
                           (sk/lay-smooth {:stat :linear-model})))]
        (is (= 1 (:panels s)))
        (is (= 150 (:points s)))
        (is (= 3 (:lines s)))))

    (testing "Recipe"
      (let [recipe (-> (sk/frame)
                       (sk/frame :sepal_length :sepal_width)
                       (sk/lay-point)
                       (sk/lay-smooth {:stat :linear-model}))
            s (summary (sk/with-data recipe iris))]
        (is (= 1 (:panels s)))
        (is (= 150 (:points s)))))

    ;; "Mixed grid" (composite + facet) is deferred. Once two sk/frame
    ;; calls promote the leaf to a composite, sk/facet can't thread
    ;; through -- facet still routes through ensure-sk, which rejects
    ;; composites (see dev-notes/facet-composite-deferral.md).

    (testing "Inference: one numerical column"
      (let [s (summary (-> (sk/frame iris)
                           (sk/frame :sepal_length)))]
        (is (pos? (:polygons s)))
        (is (zero? (:points s)))))

    (testing "Inference: one categorical column"
      (let [s (summary (-> (sk/frame iris)
                           (sk/frame :species)))]
        (is (= 3 (:polygons s)))))

    (testing "2D facet grid"
      (let [tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                             {:key-fn keyword})
            s (summary (-> (sk/frame tips {:color :smoker})
                           (sk/frame :total_bill :tip)
                           (sk/lay-point {:alpha 0.5})
                           (sk/facet-grid :day :sex)))]
        (is (= 8 (:panels s)))))

    (testing "Options pass through"
      (let [s (summary (-> (sk/frame iris)
                           (sk/frame :sepal_length :sepal_width)
                           (sk/lay-point)
                           (sk/options {:title "Test" :width 400})))]
        (is (= 1 (:panels s)))
        (is (some #{"Test"} (:texts s)))))

    (testing "Frame first, then layers"
      (let [s (summary (-> iris
                           (sk/frame :sepal_length :sepal_width {:color :species})
                           (sk/lay-point {:alpha 0.5})
                           (sk/lay-smooth {:stat :linear-model})))]
        (is (= 1 (:panels s)))
        (is (= 150 (:points s)))
        (is (= 3 (:lines s)))))

    (testing "Column inference"
      ;; Small dataset: sk/lay-point on raw data auto-infers x/y from
      ;; the first two columns (adapter behavior, preserved through
      ;; the Sketch retirement).
      (let [s (summary (sk/lay-point {:a [1 2 3 4 5] :b [2 4 3 5 4]}))]
        (is (= 5 (:points s)))))))

;; ---- Annotations-as-layers (sk/lay-rule-*, sk/lay-band-*) ----

(deftest lay-rule-band-test
  ;; Reference lines and shaded bands are first-class layers; these
  ;; tests cover sketch-scope vs view-scope, facet interaction,
  ;; color/alpha overrides, and annotation-only view domain synthesis.
  (let [ds (tc/dataset {:x [1 2 3 4 5] :y [2 4 3 5 4]})]

    (testing "sketch-scope rule-h attaches a single annotation"
      (let [p (sk/plan (-> ds
                           (sk/lay-point :x :y)
                           (sk/lay-rule-h {:y-intercept 3})))
            panel (first (:panels p))]
        (is (= 1 (count (:annotations panel))))
        (is (= :rule-h (:mark (first (:annotations panel)))))
        (is (= 3 (:y-intercept (first (:annotations panel)))))))

    (testing "sketch-scope rule applies to every facet panel (deduped)"
      ;; Without dedupe the cross-product would emit N copies per panel.
      (let [iris (tc/dataset "https://vincentarelbundock.github.io/Rdatasets/csv/datasets/iris.csv"
                             {:key-fn keyword})
            p (sk/plan (-> iris
                           (sk/lay-point :Sepal.Length :Sepal.Width)
                           (sk/facet :Species)
                           (sk/lay-rule-h {:y-intercept 3})))]
        (is (= 3 (count (:panels p))))
        (doseq [panel (:panels p)]
          (is (= 1 (count (:annotations panel)))
              (str "panel " (:row panel) "/" (:col panel) " had "
                   (count (:annotations panel)) " annotations")))))

    (testing "sk/lay-rule-v with :color and :alpha flows into the plan"
      (let [p (sk/plan (-> ds
                           (sk/lay-point :x :y)
                           (sk/lay-rule-v {:x-intercept 2 :color "red" :alpha 0.5})))
            a (first (:annotations (first (:panels p))))]
        (is (= :rule-v (:mark a)))
        (is (= 2 (:x-intercept a)))
        (is (= "red" (:color a)))
        (is (= 0.5 (:alpha a)))))

    (testing "sk/lay-band-h / sk/lay-band-v carry their min/max bounds"
      (let [p (sk/plan (-> ds
                           (sk/lay-point :x :y)
                           (sk/lay-band-h {:y-min 2 :y-max 4})
                           (sk/lay-band-v {:x-min 1 :x-max 3})))
            anns (:annotations (first (:panels p)))
            band-h (first (filter #(= :band-h (:mark %)) anns))
            band-v (first (filter #(= :band-v (:mark %)) anns))]
        (is (= 2 (:y-min band-h)))
        (is (= 4 (:y-max band-h)))
        (is (= 1 (:x-min band-v)))
        (is (= 3 (:x-max band-v)))))

    (testing "view-scope rule with sketch-level data layer renders both"
      ;; Regression: ann-view? in sketch.clj used to suppress sketch
      ;; layers when a view contained an annotation method. Stage 3
      ;; removed that check.
      (let [p (sk/plan (-> ds
                           (sk/frame :x :y)
                           sk/lay-point
                           (sk/lay-rule-h :x :y {:y-intercept 3})))
            panel (first (:panels p))]
        (is (= 1 (count (:layers panel))))
        (is (= :point (:mark (first (:layers panel)))))
        (is (= 1 (count (:annotations panel))))))

    (testing "plan with annotation layer validates against schema"
      (let [p (sk/plan (-> ds
                           (sk/lay-point :x :y)
                           (sk/lay-rule-h {:y-intercept 3 :color "red" :alpha 0.5})
                           (sk/lay-band-v {:x-min 1 :x-max 3 :color "blue" :alpha 0.2})))]
        (is (sk/valid-plan? p))))

    (testing "rendered SVG uses :color override for rule"
      (let [sk-red (-> ds
                       (sk/lay-point :x :y)
                       (sk/lay-rule-h {:y-intercept 3 :color "red"}))
            sk-default (-> ds
                           (sk/lay-point :x :y)
                           (sk/lay-rule-h {:y-intercept 3}))
            svg-red (str (sk/plan->plot (sk/plan sk-red) :svg {}))
            svg-default (str (sk/plan->plot (sk/plan sk-default) :svg {}))]
        (is (clojure.string/includes? svg-red "rgb(255,0,0)"))
        (is (not (clojure.string/includes? svg-default "rgb(255,0,0)")))))

    (testing "annotation-only view still produces a panel"
      ;; Edge case: a view declared via sk/view with only a view-scope
      ;; annotation (no data layer) should still infer a panel and
      ;; render the annotation. Domain comes from the view's data
      ;; columns plus the annotation's own position.
      (let [p (sk/plan (-> ds
                           (sk/frame :x :y)
                           (sk/lay-rule-h :x :y {:y-intercept 3})))
            panel (first (:panels p))]
        (is (= 1 (count (:panels p))))
        (is (= 0 (count (:layers panel))))
        (is (= 1 (count (:annotations panel))))
        (is (= :rule-h (:mark (first (:annotations panel)))))
        ;; Domain spans both the column data and the intercept.
        (let [[y-lo y-hi] (:y-domain panel)]
          (is (<= y-lo 2))
          (is (>= y-hi 5)))))

    (testing "annotation-only panel with band extends domain to include band"
      (let [p (sk/plan (-> ds
                           (sk/frame :x :y)
                           (sk/lay-band-h :x :y {:y-min 10 :y-max 20})))
            panel (first (:panels p))
            [y-lo y-hi] (:y-domain panel)]
        (is (<= y-lo 2))
        (is (>= y-hi 20))))))

