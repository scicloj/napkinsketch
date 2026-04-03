;; Simple and easy plotting

;; # Preface

^{:clay {:hide-code true}}
(ns index
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [scicloj.kindly.v4.kind :as kind]))

^:kind/hidden
(let [src (io/file "readme_files")
      dst (io/file "notebooks" "readme_files")]
  ;; Copy readme_files to notebooks path so Clay can find them for the book
  (when (.exists src)
    (.mkdirs dst)
    (doseq [f (.listFiles src)]
      (io/copy f (io/file dst (.getName f))))))

^:kind/md
(->> "README.md"
     slurp
     str/split-lines
     (drop 3)
     (str/join "\n"))
