;; # Composability Exploration
;;
;; A standalone exploration of the algebra behind napkinsketch.
;; Defines its own tiny verbs and nouns to test composability.
;; No napkinsketch dependency — just Clojure data and simple SVG.

(ns scratch-composability
  (:require [tablecloth.api :as tc]
            [scicloj.kindly.v4.kind :as kind]))

;; ## The Noun: Sketch
;;
;; A sketch is a map with independent axes:

(defn sketch
  "Create an empty sketch from data."
  [data]
  {:data (if (tc/dataset? data) data (tc/dataset data))
   :bindings []    ;; list of binding-sets (each is a map of channel → column)
   :methods []     ;; list of method-maps (each is {:mark ... :stat ... :opts ...})
   :shared {}      ;; shared bindings (apply to all)
   :opts {}})

;; ## The Verbs

(defn view
  "Add column bindings. Each binding-set maps visual channels to columns.
   (view sk :x :y)           → one binding-set {:x :a :y :b}
   (view sk {:x :a :color :c}) → one binding-set with aesthetics
   (view sk [[:x :y] [:a :b]]) → two binding-sets"
  ([sk x y]
   (update sk :bindings conj {:x x :y y}))
  ([sk spec]
   (cond
     (map? spec)
     (update sk :bindings conj spec)

     (and (sequential? spec) (sequential? (first spec)))
     (update sk :bindings into (map (fn [[x y]] {:x x :y y}) spec))

     (and (sequential? spec) (keyword? (first spec)))
     (update sk :bindings conj {:x (first spec) :y (second spec)})

     :else
     (update sk :bindings conj spec))))

(defn lay
  "Add a drawing method.
   (lay sk :point)           → scatter
   (lay sk :point {:alpha 0.3}) → scatter with options"
  ([sk mark]
   (update sk :methods conj {:mark mark}))
  ([sk mark opts]
   (update sk :methods conj (merge {:mark mark} opts))))

(defn shared
  "Add shared bindings (apply to all views × all methods)."
  [sk bindings]
  (update sk :shared merge bindings))

(defn cross
  "Cartesian product of two column lists."
  [xs ys]
  (vec (for [x xs, y ys] [x y])))

;; ## What Does a Sketch Look Like?

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

;; ### One view, one method:

(-> (sketch iris)
    (view :sepal_length :sepal_width)
    (lay :point)
    kind/pprint)

;; ### One view, two methods:

(-> (sketch iris)
    (view :sepal_length :sepal_width)
    (lay :point)
    (lay :lm)
    kind/pprint)

;; ### Two views, one method:

(-> (sketch iris)
    (view [[:sepal_length :sepal_width]
           [:petal_length :petal_width]])
    (lay :point)
    kind/pprint)

;; ### Two views, two methods (the cross product):

(-> (sketch iris)
    (view [[:sepal_length :sepal_width]
           [:petal_length :petal_width]])
    (lay :point)
    (lay :lm)
    kind/pprint)

;; ## Key Test: Order Independence
;;
;; Does `view` then `lay` give the same sketch as `lay` then `view`?

(let [a (-> (sketch iris)
            (view :sepal_length :sepal_width)
            (lay :point))
      b (-> (sketch iris)
            (lay :point)
            (view :sepal_length :sepal_width))]
  {:a-bindings (:bindings a) :a-methods (:methods a)
   :b-bindings (:bindings b) :b-methods (:methods b)
   :equal? (and (= (:bindings a) (:bindings b))
                (= (:methods a) (:methods b)))})

(kind/test-last [(fn [m] (true? (:equal? m)))])

;; Yes! Because bindings and methods are on independent axes,
;; the order doesn't matter.

;; ## Shared Bindings vs View Bindings
;;
;; Both map channels to columns. The difference:
;; - **view bindings** create separate panels (one per binding-set)
;; - **shared bindings** apply to ALL panels
;;
;; Think of it as: view bindings are the "rows" of the cross product,
;; shared bindings are added to every row.

(-> (sketch iris)
    (view [[:sepal_length :sepal_width]
           [:petal_length :petal_width]])
    (shared {:color :species})
    (lay :point)
    kind/pprint)

;; ## Resolving the Cross Product
;;
;; At render time, we compute: bindings × methods.
;; Shared bindings are merged into each binding-set.

(defn resolve-sketch
  "Expand the sketch into a flat list of {binding-set + method} combinations."
  [{:keys [bindings methods shared]}]
  (let [bindings (if (empty? bindings) [{}] bindings)
        methods (if (empty? methods) [{}] methods)
        expanded-bindings (mapv #(merge shared %) bindings)]
    (vec (for [b expanded-bindings
               m methods]
           (merge b m)))))

;; ### One view × one method:

(resolve-sketch
 (-> (sketch iris)
     (view :sepal_length :sepal_width)
     (lay :point)))

;; ### Two views × two methods = four combinations:

(resolve-sketch
 (-> (sketch iris)
     (view [[:sepal_length :sepal_width]
            [:petal_length :petal_width]])
     (lay :point)
     (lay :lm)))

(kind/test-last [(fn [v] (= 4 (count v)))])

;; ### Shared bindings merge into each combination:

(resolve-sketch
 (-> (sketch iris)
     (view [[:sepal_length :sepal_width]
            [:petal_length :petal_width]])
     (shared {:color :species})
     (lay :point)))

(kind/test-last [(fn [v] (and (= 2 (count v))
                              (every? #(= :species (:color %)) v)))])

;; ## Adding Views After Methods
;;
;; Because axes are independent, this works:

(let [result (-> (sketch iris)
                 (lay :point)
                 (lay :lm)
                 (view :sepal_length :sepal_width)
                 (view :petal_length :petal_width))]
  (resolve-sketch result))

(kind/test-last [(fn [v] (= 4 (count v)))])

;; Methods first, views second — same cross product.

;; ## SPLOM via Cross
;;
;; `cross` generates column pairs. `view` adds them all.
;; Without explicit methods, inference would fill them in per view.

(def cols [:sepal_length :sepal_width :petal_length])

(resolve-sketch
 (-> (sketch iris)
     (view (cross cols cols))
     (shared {:color :species})
     (lay :point)))

(kind/test-last [(fn [v] (= 9 (count v)))])

;; 9 views × 1 method = 9 combinations.
;; Inference could vary the method per view (histogram on diagonal).

;; ## The Distributive Law
;;
;; (views₁ + views₂) × methods = views₁ × methods + views₂ × methods
;;
;; Adding views is additive, methods multiply across all of them.

(let [together (-> (sketch iris)
                   (view [[:sepal_length :sepal_width]
                          [:petal_length :petal_width]])
                   (lay :point)
                   (lay :lm)
                   resolve-sketch)
      separate (concat
                (resolve-sketch
                 (-> (sketch iris)
                     (view :sepal_length :sepal_width)
                     (lay :point)
                     (lay :lm)))
                (resolve-sketch
                 (-> (sketch iris)
                     (view :petal_length :petal_width)
                     (lay :point)
                     (lay :lm))))]
  {:together-count (count together)
   :separate-count (count separate)
   :same-content? (= (set (map #(dissoc % :data) together))
                     (set (map #(dissoc % :data) separate)))})

(kind/test-last [(fn [m] (and (= 4 (:together-count m))
                              (= 4 (:separate-count m))
                              (true? (:same-content? m))))])

;; ## Reusable Styles
;;
;; Because methods are independent of views, you can define
;; a "style" (methods + shared aesthetics) and apply it to
;; different data/views:

(defn apply-style [sk style]
  (-> sk
      (update :methods into (:methods style))
      (update :shared merge (:shared style))
      (update :opts merge (:opts style))))

(def scatter+lm
  {:methods [{:mark :point :alpha 0.5} {:mark :lm :se true}]
   :shared {:color :species}
   :opts {}})

(resolve-sketch
 (-> (sketch iris)
     (view :sepal_length :sepal_width)
     (apply-style scatter+lm)))

(kind/test-last [(fn [v] (and (= 2 (count v))
                              (every? #(= :species (:color %)) v)))])

;; ## Summary
;;
;; The algebra has:
;;
;; - **One noun**: sketch — a map with independent axes
;; - **Two verbs**: `view` (what) and `lay` (how)
;; - **One modifier**: `shared` (aesthetics for everything)
;; - **One operator**: `cross` (generate column pairs)
;;
;; The cross product `bindings × methods` is the core operation.
;; It's what makes small descriptions produce rich visualizations.
;;
;; Bindings and methods are orthogonal. Order doesn't matter.
;; Shared bindings (`:color`, `:size`) apply to all combinations.
;; Column-pair bindings (`:x`, `:y`) create separate panels.
;;
;; The only distinction between "column pairs" and "shared aesthetics"
;; is whether they **multiply** (creating panels) or **add** (enriching
;; each panel). `:x :y` pairs multiply. `:color :species` adds.
