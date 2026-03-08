;; # Quickstart
;;
;; A minimal introduction to napkinsketch.

(ns napkinsketch-book.quickstart
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]))

;; ## Hello

;; Your first example goes here.

(+ 1 2)

(kind/test-last
 (= 3 *1))
