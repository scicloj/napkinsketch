(ns scratch
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.plotje.api :as pj]))


(-> {:A [1 2 3 4]
     :B [4 5 2 9]}
    pj/lay-point)

