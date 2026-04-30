;; Composable plotting in Clojure
;; # Preface
^{:clay {:hide-code true}}
(ns index
  (:require
   [clojure.edn :as edn]
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

;; ## Chapters of this book

^:kind/hidden
(defn chapter->title [chapter]
  (or (some->> chapter
               (format "notebooks/plotje_book/%s.clj")
               slurp
               str/split-lines
               (filter #(re-matches #"^;; # .*" %))
               first
               (#(str/replace % #"^;; # " "")))
      chapter))

^:kind/md
(->> "notebooks/chapters.edn"
     slurp
     edn/read-string
     (mapcat (fn [[part chapters]]
               (cons (format "- %s" part)
                     (map (fn [chapter]
                            (format "  - [%s](plotje_book.%s.html)"
                                    (chapter->title chapter)
                                    chapter))
                          chapters))))
     (str/join "\n"))
