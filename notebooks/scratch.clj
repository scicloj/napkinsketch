(ns scratch
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.napkinsketch.api :as pj]))

(kind/fn {:x 1
          :y 2}
  {:kindly/f (fn [{:keys [x y]}]
               (+ x y))})


(-> {:A [1 2 3 4]
     :B [4 5 2 9]}
    pj/lay-point)


{:data _unnamed [4 2]:

 | :A | :B |
 |---:|---:|
 |  1 |  4 |
 |  2 |  5 |
 |  3 |  2 |
 |  4 |  9 |
 ,
 :mapping {},
 :views [{:mapping {:x :A, :y :B}, :layers [{:method :point, :mapping {}}]}],
 :layers [],
 :opts {}}
