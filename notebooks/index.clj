;; Simple and easy plotting

;; # Preface

^{:clay {:hide-code true}}
(ns index
  (:require
   [clojure.string :as str]
   [scicloj.kindly.v4.kind :as kind]))

^{:kindly/hide-code true
  :kind/md true}
(->> "README.md"
     slurp
     str/split-lines
     (drop 2)
     (str/join "\n"))

