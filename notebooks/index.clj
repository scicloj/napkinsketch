;; Simple and easy plotting

;; # Preface

^{:clay {:hide-code true}}
(ns index
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [scicloj.kindly.v4.kind :as kind]))

;; Copy readme_files to notebooks path so Clay can find them for the book
^{:kindly/hide-code true}
(let [src (io/file "readme_files")
      dst (io/file "notebooks" "readme_files")]
  (when (.exists src)
    (.mkdirs dst)
    (doseq [f (.listFiles src)]
      (io/copy f (io/file dst (.getName f))))))

^{:kindly/hide-code true
  :kind/md true}
(->> "README.md"
     slurp
     str/split-lines
     (drop 2)
     (str/join "\n"))
