(ns dev
  (:require [scicloj.clay.v2.api :as clay]))

(defn- read-chapters []
  (-> "notebooks/chapters.edn" slurp clojure.edn/read-string))

(defn- chapters->source-paths
  "Convert chapters map to flat vector of source paths."
  [chapters]
  (into [] (mapcat (fn [[_part names]]
                     (map #(format "napkinsketch_book/%s.clj" %) names)))
        chapters))

(defn- chapters->parts
  "Convert chapters map to Clay's :source-path part structure."
  [chapters]
  (mapv (fn [[part names]]
          {:part part
           :chapters (mapv #(format "napkinsketch_book/%s.clj" %) names)})
        chapters))

(defn make-book!
  "Render book HTML through Quarto."
  []
  (clay/make! {:format [:quarto :html]
               :base-source-path "notebooks"
               :source-path (into ["index.clj"] (chapters->parts (read-chapters)))
               :base-target-path "docs"
               :book {:title "Napkinsketch"}
               :clean-up-target-dir true}))

(defn make-gfm!
  "Render all (or specified) notebooks as GitHub-flavored Markdown."
  [& paths]
  (clay/make! {:format [:gfm]
               :base-source-path "notebooks"
               :source-path (or (seq paths)
                                (into ["index.clj"]
                                      (chapters->source-paths (read-chapters))))
               :base-target-path "gfm"
               :show false}))

(comment
  (make-book!)
  (make-gfm!)
  (make-gfm! "napkinsketch_book/quickstart.clj"))
