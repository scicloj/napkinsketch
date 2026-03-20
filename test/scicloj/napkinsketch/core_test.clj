(ns scicloj.napkinsketch.core-test
  "Hand-written unit tests for napkinsketch core logic."
  (:require [clojure.test :refer [deftest testing is are]]
            [tablecloth.api :as tc]
            [scicloj.napkinsketch.api :as sk]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.stat :as stat]
            [scicloj.napkinsketch.impl.scale :as scale]
            [scicloj.napkinsketch.impl.position :as position]
            [scicloj.napkinsketch.impl.extract :as extract]
            [scicloj.napkinsketch.impl.view :as view]))

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
  (testing "t=0.0 (viridis start — dark purple)"
    (let [[r g b a] (defaults/gradient-color 0.0)]
      (is (< r 0.4))
      (is (== 1.0 a))))
  (testing "t=1.0 (viridis end — yellow)"
    (let [[r g b _] (defaults/gradient-color 1.0)]
      (is (> r 0.9))
      (is (> g 0.8))))
  (testing "t=0.5 (mid — teal)"
    (let [[r g b _] (defaults/gradient-color 0.5)]
      (is (< r 0.3))
      (is (> g 0.4))
      (is (> b 0.4))))
  (testing "clamping"
    (is (= (defaults/gradient-color -1.0) (defaults/gradient-color 0.0)))
    (is (= (defaults/gradient-color 2.0) (defaults/gradient-color 1.0)))))

(deftest legend-serializable-test
  (testing "continuous legend has :color-scale keyword, no :gradient-fn"
    (let [ds (tc/dataset {:x (range 50) :y (range 50) :c (range 50)})
          sk (sk/sketch [(sk/point {:data ds :x :x :y :y :color :c})])
          legend (:legend sk)]
      (is (= :continuous (:type legend)))
      (is (contains? legend :color-scale))
      (is (not (contains? legend :gradient-fn)))
      (is (nil? (:color-scale legend)) "default color-scale is nil")))
  (testing "explicit :color-scale is stored as keyword"
    (let [ds (tc/dataset {:x (range 50) :y (range 50) :c (range 50)})
          sk (sk/sketch [(sk/point {:data ds :x :x :y :y :color :c})]
                        {:color-scale :inferno})
          legend (:legend sk)]
      (is (= :inferno (:color-scale legend)))))
  (testing "legend has 20 pre-computed stops"
    (let [ds (tc/dataset {:x (range 50) :y (range 50) :c (range 50)})
          sk (sk/sketch [(sk/point {:data ds :x :x :y :y :color :c})])
          legend (:legend sk)]
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
    (let [view {:mark :line :stat :lm :data tiny-ds :x :x :y :y
                :x-type :numerical :cfg defaults/defaults}
          result (stat/compute-stat view)
          line (first (:lines result))]
      (is line)
      (is (< (:x1 line) (:x2 line)))
      (is (< (Math/abs (- (:y1 line) 2.0)) 0.01))
      (is (< (Math/abs (- (:y2 line) 10.0)) 0.01))))
  (testing "n=2 produces nil (needs >= 3)"
    (let [ds2 (tc/dataset {:x [1 2] :y [3 4]})
          view {:mark :line :stat :lm :data ds2 :x :x :y :y
                :x-type :numerical :cfg defaults/defaults}
          result (stat/compute-stat view)]
      (is (empty? (:lines result))))))

(deftest compute-stat-kde-test
  (let [view {:mark :area :stat :kde :data tiny-ds :x :x :x-type :numerical
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
    (is (every? #(and (:x-lo %) (:y-lo %) (:fill %)) (:tiles result)))))

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
        rv (view/resolve-view view)
        stat-result (stat/compute-stat (assoc rv :cfg defaults/defaults))
        layer (extract/extract-layer rv stat-result [] defaults/defaults)]
    (is (= :point (:mark layer)))
    (is (seq (:groups layer)))
    (is (= 5 (count (:xs (first (:groups layer))))))))

(deftest extract-layer-bar-test
  (let [view {:mark :bar :stat :bin :data tiny-ds :x :x
              :x-type :numerical}
        rv (view/resolve-view view)
        stat-result (stat/compute-stat (assoc rv :cfg defaults/defaults))
        layer (extract/extract-layer rv stat-result [] defaults/defaults)]
    (is (= :bar (:mark layer)))
    (is (seq (:groups layer)))
    (is (seq (:bars (first (:groups layer)))))))

(deftest apply-nudge-test
  (testing "nudge-x shifts xs"
    (let [view {:mark :point :data (tc/dataset {:x [1.0 2.0] :y [3.0 4.0]})
                :x :x :y :y :x-type :numerical :nudge-x 0.5}
          rv (view/resolve-view view)
          stat-result (stat/compute-stat (assoc rv :cfg defaults/defaults))
          layer (extract/extract-layer rv stat-result [] defaults/defaults)]
      (is (= [1.5 2.5] (:xs (first (:groups layer)))))))
  (testing "no nudge is no-op"
    (let [view {:mark :point :data tiny-ds :x :x :y :y :x-type :numerical}
          rv (view/resolve-view view)
          stat-result (stat/compute-stat (assoc rv :cfg defaults/defaults))
          layer (extract/extract-layer rv stat-result [] defaults/defaults)]
      (is (= [1 2 3 4 5] (:xs (first (:groups layer))))))))

;; ============================================================
;; view.clj (via api) — mark constructors
;; ============================================================

(deftest mark-constructors-test
  (testing "marks return the correct resolved :mark key"
    (are [f mk] (= mk (:mark (f)))
      sk/point :point
      sk/line :line
      sk/step :step
      sk/histogram :bar
      sk/bar :rect
      sk/stacked-bar :rect
      sk/stacked-bar-fill :rect
      sk/value-bar :rect
      sk/lm :line
      sk/loess :line
      sk/text :text
      sk/label :label
      sk/area :area
      sk/stacked-area :area
      sk/density :area
      sk/tile :tile
      sk/density2d :tile
      sk/contour :contour
      sk/ridgeline :ridgeline
      sk/boxplot :boxplot
      sk/violin :violin
      sk/rug :rug
      sk/summary :pointrange
      sk/errorbar :errorbar
      sk/lollipop :lollipop)))

(deftest view-arities-test
  (testing "2-arity (data, spec)"
    (let [v (sk/view tiny-ds [[:x :y]])]
      (is (vector? v))
      (is (= 1 (count v)))
      (is (= :x (:x (first v))))
      (is (= :y (:y (first v))))))
  (testing "3-arity (data, x, y)"
    (let [v (sk/view tiny-ds :x :y)]
      (is (= 1 (count v)))
      (is (= :x (:x (first v))))))
  (testing "histogram (1 column)"
    (let [v (sk/view tiny-ds :x)]
      (is (= 1 (count v)))
      (is (= :x (:x (first v)))))))

(deftest lay-test
  (testing "lay merges mark into views"
    (let [views (sk/view tiny-ds [[:x :y]])
          layered (sk/lay views (sk/point {:color :x}))]
      (is (= :point (:mark (first layered))))
      (is (= :x (:color (first layered))))))
  (testing "lay is additive"
    (let [views (sk/view tiny-ds [[:x :y]])
          l1 (sk/lay views (sk/point))
          l2 (sk/lay l1 (sk/lm))]
      (is (= 2 (count l2))))))

;; ============================================================
;; views->sketch (integration)
;; ============================================================

(deftest views-to-sketch-test
  (let [views (-> tiny-ds
                  (sk/view [[:x :y]])
                  (sk/lay (sk/point)))
        sk (sk/sketch views)]
    (is (map? sk))
    (is (contains? sk :panels))
    (is (contains? sk :width))
    (is (contains? sk :height))
    (is (= 1 (count (:panels sk))))
    (let [panel (first (:panels sk))]
      (is (seq (:layers panel)))
      (is (contains? panel :x-domain))
      (is (contains? panel :y-domain)))))

(deftest sketch-with-color-test
  (let [ds (tc/dataset {:x [1 2 3 4] :y [1 2 3 4] :g ["a" "a" "b" "b"]})
        views (-> ds (sk/view [[:x :y]]) (sk/lay (sk/point {:color :g})))
        sk (sk/sketch views)]
    (is (:legend sk))
    (is (= 2 (count (:entries (:legend sk)))))))

(deftest sketch-faceted-test
  (let [ds (tc/dataset {:x [1 2 3 4 5 6] :y [1 2 3 4 5 6]
                        :g ["a" "a" "b" "b" "c" "c"]})
        views (-> ds (sk/view [[:x :y]]) (sk/facet :g) (sk/lay (sk/point)))
        sk (sk/sketch views)]
    (is (= 3 (count (:panels sk))))))

(deftest coord-fixed-test
  (testing "adjust-fixed-aspect: equal ranges → square panel"
    (let [{:keys [pw ph]} (#'scicloj.napkinsketch.impl.sketch/adjust-fixed-aspect 500 300 [0 10] [0 10])]
      (is (== pw ph) "Equal data ranges should produce equal panel dims")
      (is (== ph 300) "Shrinks pw to match ph for equal ranges")))
  (testing "adjust-fixed-aspect: wide data → wide panel"
    (let [{:keys [pw ph]} (#'scicloj.napkinsketch.impl.sketch/adjust-fixed-aspect 500 300 [0 100] [0 10])]
      (is (== pw 500) "pw stays at max")
      (is (< ph 300) "ph shrinks for wide data")))
  (testing "adjust-fixed-aspect: tall data → tall panel"
    (let [{:keys [pw ph]} (#'scicloj.napkinsketch.impl.sketch/adjust-fixed-aspect 500 300 [0 10] [0 100])]
      (is (< pw 500) "pw shrinks for tall data")
      (is (== ph 300) "ph stays at max")))
  (testing "adjust-fixed-aspect: degenerate domain → no change"
    (let [{:keys [pw ph]} (#'scicloj.napkinsketch.impl.sketch/adjust-fixed-aspect 500 300 [5 5] [0 10])]
      (is (== pw 500))
      (is (== ph 300))))
  (testing "coord :fixed end-to-end — equal ranges produce square SVG"
    (let [ds (tc/dataset {:x [0 10 5] :y [0 10 5]})
          fig (-> ds (sk/view :x :y) (sk/coord :fixed) (sk/lay (sk/point)) sk/plot)
          svg (if (= :svg (first fig)) fig (second fig))
          {:keys [width height]} (second svg)]
      (is (== width height) "Equal data ranges → square SVG")))
  (testing "coord :fixed end-to-end — asymmetric ranges"
    (let [ds (tc/dataset {:x [0 100 50] :y [0 10 5]})
          fig (-> ds (sk/view :x :y) (sk/coord :fixed) (sk/lay (sk/point)) sk/plot)
          svg (if (= :svg (first fig)) fig (second fig))
          {:keys [width height]} (second svg)]
      (is (> width height) "Wide data → wider SVG"))))

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
          fig (-> ds (sk/view :x :y)
                  (sk/lay (sk/point {:color :z}))
                  (sk/plot {:color-scale :diverging :color-midpoint 0}))
          s (sk/svg-summary fig)]
      (is (= 10 (:points s))))))

(deftest loess-se-test
  (testing "LOESS with SE produces ribbon"
    (let [ds (tc/dataset {:x (range 20) :y (map #(+ (* 0.1 % %) (Math/sin %)) (range 20))})
          fig (-> ds (sk/view :x :y)
                  (sk/lay (sk/point) (sk/loess {:se true :se-boot 50}))
                  sk/plot)
          s (sk/svg-summary fig)]
      (is (= 20 (:points s)))
      (is (= 1 (:lines s)))
      (is (= 1 (:polygons s)) "confidence ribbon polygon")))
  (testing "LOESS without SE has no ribbon"
    (let [ds (tc/dataset {:x (range 20) :y (map #(+ (* 0.1 % %) (Math/sin %)) (range 20))})
          fig (-> ds (sk/view :x :y)
                  (sk/lay (sk/point) (sk/loess))
                  sk/plot)
          s (sk/svg-summary fig)]
      (is (= 1 (:lines s)))
      (is (zero? (:polygons s)))))
  (testing "LOESS dedup handles duplicate x values"
    (let [ds (tc/dataset {:x [1 1 2 2 3 3 4 4 5 5] :y [2 3 4 5 6 7 8 9 10 11]})
          fig (-> ds (sk/view :x :y) (sk/lay (sk/loess)) sk/plot)
          s (sk/svg-summary fig)]
      (is (= 1 (:lines s))))))

(deftest arrange-test
  (testing "flat plots → CSS grid"
    (let [p1 (-> tiny-ds (sk/view :x :y) (sk/lay (sk/point)) sk/plot)
          p2 (-> tiny-ds (sk/view :x :y) (sk/lay (sk/point)) sk/plot)
          result (sk/arrange [p1 p2])]
      (is (= :div (first result)))
      (is (= :kind/hiccup (:kindly/kind (meta result))))))
  (testing "nested rows → correct cols"
    (let [p (-> tiny-ds (sk/view :x :y) (sk/lay (sk/point)) sk/plot)
          result (sk/arrange [[p p] [p p]])]
      (is (= "repeat(2, 1fr)"
             (-> result second :style :grid-template-columns)))))
  (testing "title appears as first child"
    (let [p (-> tiny-ds (sk/view :x :y) (sk/lay (sk/point)) sk/plot)
          result (sk/arrange [p p] {:title "Test" :cols 2})
          title-div (nth result 2)]
      (is (= :div (first title-div)))
      (is (= "Test" (last title-div))))))

(deftest valid-sketch-test
  (let [views (-> tiny-ds (sk/view [[:x :y]]) (sk/lay (sk/point)))
        sk (sk/sketch views)]
    (is (sk/valid-sketch? sk))))

;; ============================================================
;; Configuration System
;; ============================================================

(deftest config-returns-defaults-test
  (testing "config returns a map with all expected keys"
    (let [cfg (defaults/config)]
      (is (map? cfg))
      (is (= 600 (:width cfg)))
      (is (= 400 (:height cfg)))
      (is (= 25 (:margin cfg)))
      (is (= 2.5 (:point-radius cfg)))
      (is (= 0.7 (:point-opacity cfg)))
      (is (= 0.7 (:bar-opacity cfg)))
      (is (= 2 (:line-width cfg)))
      (is (= 1.5 (:grid-stroke-width cfg)))
      (is (string? (:annotation-stroke cfg)))
      (is (= 0.15 (:band-opacity cfg)))
      (is (= 60 (:tick-spacing-x cfg)))
      (is (= 40 (:tick-spacing-y cfg)))
      (is (= :sturges (:bin-method cfg)))
      (is (= 0.05 (:domain-padding cfg)))
      (is (= 11 (:label-font-size cfg)))
      (is (= 13 (:title-font-size cfg)))
      (is (= 10 (:strip-font-size cfg)))
      (is (= 18 (:label-offset cfg)))
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
    (is (= 2.5 (:point-radius (defaults/config))))))

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
      (is (= "#FFFFFF" (get-in cfg [:theme :grid])))
      (is (= 8 (get-in cfg [:theme :font-size])))))
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
          (is (= 25 (:margin cfg)))))
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

;; ---- Config affects sketch output ----

(deftest config-affects-sketch-test
  (let [views (-> tiny-ds (sk/view [[:x :y]]) (sk/lay (sk/point)))]
    (testing "default width/height in sketch"
      (let [s (sk/sketch views)]
        (is (= 600 (:width s)))
        (is (= 400 (:height s)))))
    (testing "per-call opts change sketch dimensions"
      (let [s (sk/sketch views {:width 800 :height 300})]
        (is (= 800 (:width s)))
        (is (= 300 (:height s)))))
    (testing "set-config! changes sketch dimensions"
      (try
        (sk/set-config! {:width 700})
        (let [s (sk/sketch views)]
          (is (= 700 (:width s))))
        (finally
          (sk/set-config! nil))))
    (testing "with-config changes sketch dimensions"
      (sk/with-config {:height 500}
        (let [s (sk/sketch views)]
          (is (= 500 (:height s)))))
      ;; After with-config, back to default
      (let [s (sk/sketch views)]
        (is (= 400 (:height s)))))
    (testing "sketch does NOT contain :theme key"
      (let [s (sk/sketch views)]
        (is (not (contains? s :theme)))))))

;; ---- Config affects rendered SVG ----

(deftest config-affects-render-test
  (let [views (-> tiny-ds (sk/view [[:x :y]]) (sk/lay (sk/point)))]
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
      (let [svg (sk/plot views {:width 800})
            attrs (second svg)]
        (is (> (:width attrs) 800))))))

;; ---- Config with palette ----

(deftest config-palette-test
  (let [ds (tc/dataset {:x [1 2 3 4 5 6]
                        :y [10 20 30 15 25 35]
                        :g ["a" "a" "a" "b" "b" "b"]})
        views (-> ds (sk/view [[:x :y]]) (sk/lay (sk/point {:color :g})))]
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
  (let [views (-> tiny-ds (sk/view [[:x :y]]) (sk/lay (sk/point)))]
    (testing "validate true (default) — valid sketch passes"
      (is (some? (sk/sketch views))))
    (testing "validate false skips schema check"
      (is (some? (sk/sketch views {:validate false}))))))

;; ---- Edge case tests ----

(deftest single-point-dataset-test
  (testing "sketch with a single data point does not throw"
    (let [ds (tc/dataset {:x [5] :y [10]})
          sk (sk/sketch [(sk/point {:data ds :x :x :y :y})])]
      (is (= 1 (count (:panels sk))))
      (is (some? (sk/plot [(sk/point {:data ds :x :x :y :y})]))))))

(deftest two-point-dataset-test
  (testing "regression with exactly 2 points — lm needs n>=3 so falls back gracefully"
    (let [ds (tc/dataset {:x [1 2] :y [3 4]})
          views (-> ds (sk/view :x :y) (sk/lay (sk/point)))]
      (is (some? (sk/sketch views))))))

(deftest all-same-values-test
  (testing "scatter where all x values are identical"
    (let [ds (tc/dataset {:x [5 5 5 5] :y [1 2 3 4]})
          sk (sk/sketch [(sk/point {:data ds :x :x :y :y})])]
      (is (some? sk))
      (is (= 1 (count (:panels sk))))))
  (testing "scatter where all y values are identical"
    (let [ds (tc/dataset {:x [1 2 3 4] :y [5 5 5 5]})
          sk (sk/sketch [(sk/point {:data ds :x :x :y :y})])]
      (is (some? sk)))))

(deftest categorical-single-category-test
  (testing "bar chart with only one category"
    (let [ds (tc/dataset {:cat ["a" "a" "a"] :val [1 2 3]})
          sk (sk/sketch [(sk/bar {:data ds :x :cat :y :val})])]
      (is (= 1 (count (:panels sk)))))))

(deftest histogram-uniform-data-test
  (testing "histogram with all identical values"
    (let [ds (tc/dataset {:x [5 5 5 5 5]})
          sk (sk/sketch [(sk/histogram {:data ds :x :x :y :x})])]
      (is (some? sk)))))

(deftest polar-coord-test
  (testing "polar coordinate sketch structure"
    (let [ds (tc/dataset {:cat ["A" "B" "C"] :val [10 20 30]})
          views (-> ds
                    (sk/view :cat :val)
                    (sk/lay (sk/bar))
                    (sk/coord :polar))
          sk (sk/sketch views)]
      (is (= :polar (get-in sk [:panels 0 :coord]))))))

(deftest flip-coord-test
  (testing "flipped coordinates swap x/y domains"
    (let [views (-> cat-ds
                    (sk/view :cat :val)
                    (sk/lay (sk/bar))
                    (sk/coord :flip))
          sk (sk/sketch views)
          panel (first (:panels sk))]
      (is (= :flip (:coord panel))))))

(deftest labs-test
  (testing "labels propagate to sketch"
    (let [views (-> tiny-ds
                    (sk/view :x :y)
                    (sk/lay (sk/point))
                    (sk/labs {:title "T" :subtitle "ST" :caption "C"
                              :x "X Axis" :y "Y Axis"}))
          sk (sk/sketch views)]
      (is (= "T" (:title sk)))
      (is (= "ST" (:subtitle sk)))
      (is (= "C" (:caption sk)))
      (is (= "X Axis" (:x-label sk)))
      (is (= "Y Axis" (:y-label sk))))))

(deftest log-scale-test
  (testing "log scale is recorded in sketch"
    (let [ds (tc/dataset {:x [1 10 100 1000] :y [1 2 3 4]})
          views (-> ds
                    (sk/view :x :y)
                    (sk/lay (sk/point))
                    (sk/scale :x :log))
          sk (sk/sketch views)
          panel (first (:panels sk))]
      (is (= :log (get-in panel [:x-scale :type]))))))

(deftest multiple-layers-test
  (testing "sketch with point + line layers"
    (let [views (-> tiny-ds
                    (sk/view :x :y)
                    (sk/lay (sk/point) (sk/line)))
          sk (sk/sketch views)
          layers (get-in sk [:panels 0 :layers])]
      (is (= 2 (count layers))))))

(deftest color-groups-test
  (testing "color mapping with string values produces legend"
    (let [ds (tc/dataset {:x [1 2 3] :y [4 5 6] :g ["a" "b" "a"]})
          sk (sk/sketch [(sk/point {:data ds :x :x :y :y :color :g})])]
      (is (some? (:legend sk)))
      (is (= 2 (count (get-in sk [:legend :entries])))))))

(deftest sketch-dimensions-test
  (testing "custom width and height"
    (let [views (-> tiny-ds (sk/view :x :y) (sk/lay (sk/point)))
          sk (sk/sketch views {:width 800 :height 300})]
      (is (= 800 (:width sk)))
      (is (= 300 (:height sk))))))

(deftest triangular-grid-strip-labels-test
  (testing "pairs plot (triangular grid) shows all strip labels"
    (let [ds (tc/dataset {:a [1 2 3 4 5] :b [5 4 3 2 1] :c [2 4 6 8 10]})
          views (-> ds
                    (sk/view (sk/pairs [:a :b :c]))
                    (sk/lay (sk/point)))
          svg (sk/plot views)
          s (sk/svg-summary svg)
          texts (:texts s)]
      (is (= 3 (:panels s)))
      (is (= 15 (:points s)))
      ;; 2 col-strips (a, b) + 2 row-strips (b, c) = 4 strip labels
      (is (some #{"a"} texts))
      (is (some #{"b"} texts))
      (is (some #{"c"} texts)))))

(deftest save-test
  (testing "sk/save writes valid SVG file"
    (let [ds (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                         {:key-fn keyword})
          path (str (java.io.File/createTempFile "napkinsketch" ".svg"))
          views (-> ds (sk/view :sepal_length :sepal_width)
                    (sk/lay (sk/point {:color :species})))]
      (sk/save views path)
      (let [content (slurp path)]
        (is (.startsWith content "<?xml"))
        (is (.contains content "<svg"))
        (is (.contains content "setosa")))
      (.delete (java.io.File. path)))))

(deftest temporal-epoch-ms-test
  (testing "LocalDate converts to epoch-ms"
    (let [d (java.time.LocalDate/of 2025 1 1)
          ms (view/temporal->epoch-ms d)]
      (is (double? ms))
      (is (== ms (* (.toEpochDay d) 86400000)))))
  (testing "LocalDateTime preserves sub-day precision"
    (let [dt (java.time.LocalDateTime/of 2025 3 15 12 30 0)
          ms (view/temporal->epoch-ms dt)]
      (is (double? ms))
      ;; Should differ from midnight by 12.5 hours in ms
      (let [midnight-ms (view/temporal->epoch-ms (java.time.LocalDate/of 2025 3 15))]
        (is (== (- ms midnight-ms) (* 12.5 3600000))))))
  (testing "Temporal sketch has datetime ticks"
    (let [sk (-> (tc/dataset {:date [(java.time.LocalDate/of 2025 1 1)
                                     (java.time.LocalDate/of 2025 6 1)
                                     (java.time.LocalDate/of 2025 12 1)]
                              :val [10 20 30]})
                 (sk/view :date :val)
                 (sk/lay (sk/point))
                 sk/sketch)]
      (is (= 1 (count (:panels sk))))
      (is (seq (get-in sk [:panels 0 :x-ticks :labels]))))))

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
      (let [s (-> iris (sk/view "sepal_length" "sepal_width")
                  (sk/lay (sk/point)) sk/plot sk/svg-summary)]
        (is (= 150 (:points s)))))
    (testing "String columns in vector spec"
      (let [s (-> iris (sk/view [["sepal_length" "sepal_width"]])
                  (sk/lay (sk/point)) sk/plot sk/svg-summary)]
        (is (= 150 (:points s)))))
    (testing "String column in mark options"
      (let [s (-> iris (sk/view :sepal_length :sepal_width)
                  (sk/lay (sk/point {:color "species"})) sk/plot sk/svg-summary)]
        (is (= 150 (:points s)))
        (is (some #{"setosa"} (:texts s)))))
    (testing "Dataset with string column names"
      (let [ds (tc/dataset {"x" [1 2 3] "y" [4 5 6]})
            s (-> ds (sk/view :x :y) (sk/lay (sk/point)) sk/plot sk/svg-summary)]
        (is (= 3 (:points s)))))
    (testing "Dataset with string columns + string spec"
      (let [ds (tc/dataset {"x" [1 2 3] "y" [4 5 6]})
            s (-> ds (sk/view "x" "y") (sk/lay (sk/point)) sk/plot sk/svg-summary)]
        (is (= 3 (:points s)))))
    (testing "String in facet"
      (let [s (-> iris (sk/view :sepal_length :sepal_width)
                  (sk/facet "species") (sk/lay (sk/point)) sk/plot sk/svg-summary)]
        (is (= 3 (:panels s)))))
    (testing "String in pairs"
      (is (= [[:a :b] [:a :c] [:b :c]]
             (sk/pairs ["a" "b" "c"]))))
    (testing "Literal color string still works"
      (let [v (-> (tc/dataset {:x [1 2 3] :y [4 5 6]})
                  (sk/view :x :y)
                  (sk/lay (sk/point {:color "#FF0000"}))
                  sk/plot)]
        (is (= 3 (:points (sk/svg-summary v))))))
    (testing "Typo still gives good error"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo
                            #"not found in dataset"
                            (-> iris (sk/view :sepl_length :sepal_width)
                                (sk/lay (sk/point)) sk/plot))))))

(deftest schema-all-marks-test
  (testing "Every mark type produces a valid sketch"
    (let [iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                           {:key-fn keyword})
          xy-ds (tc/dataset {:x (range 10) :y (range 10)})
          eb-ds (tc/dataset {:x ["a" "b"] :y [10 20] :ymin [8 17] :ymax [12 23]})
          txt-ds (tc/dataset {:x [1 2] :y [3 4] :n ["a" "b"]})
          cases [["point" (-> iris (sk/view :sepal_length :sepal_width) (sk/lay (sk/point {:color :species})))]
                 ["bar" (-> iris (sk/view :species) (sk/lay (sk/bar)))]
                 ["histogram" (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)))]
                 ["line" (-> xy-ds (sk/view :x :y) (sk/lay (sk/line)))]
                 ["step" (-> xy-ds (sk/view :x :y) (sk/lay (sk/step)))]
                 ["lm" (-> iris (sk/view :sepal_length :sepal_width) (sk/lay (sk/lm {:se true})))]
                 ["loess" (-> iris (sk/view :sepal_length :sepal_width) (sk/lay (sk/loess)))]
                 ["area" (-> xy-ds (sk/view :x :y) (sk/lay (sk/area)))]
                 ["boxplot" (-> iris (sk/view :species :sepal_width) (sk/lay (sk/boxplot)))]
                 ["violin" (-> iris (sk/view :species :sepal_width) (sk/lay (sk/violin)))]
                 ["density" (-> iris (sk/view :sepal_length) (sk/lay (sk/density)))]
                 ["ridgeline" (-> iris (sk/view :species :sepal_width) (sk/lay (sk/ridgeline)))]
                 ["text" (-> txt-ds (sk/view :x :y) (sk/lay (sk/text {:text :n})))]
                 ["tile" (-> iris (sk/view :sepal_length :sepal_width) (sk/lay (sk/tile)))]
                 ["contour" (-> iris (sk/view :sepal_length :sepal_width) (sk/lay (sk/contour)))]
                 ["errorbar" (-> eb-ds (sk/view :x :y) (sk/lay (sk/errorbar {:ymin :ymin :ymax :ymax})))]
                 ["lollipop" (-> eb-ds (sk/view :x :y) (sk/lay (sk/lollipop)))]
                 ["summary" (-> iris (sk/view :species :sepal_width) (sk/lay (sk/summary)))]]]
      (doseq [[mark-name views] cases]
        (testing mark-name
          (is (sk/valid-sketch? (sk/sketch views {:validate false}))))))))

