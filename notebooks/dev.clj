(ns dev
  (:require [scicloj.clay.v2.api :as clay]))

(def ^:private read-chapters
  (fn []
    (-> "notebooks/chapters.edn" slurp clojure.edn/read-string)))

(def ^:private chapters->source-paths
  (fn [chapters]
    (into [] (mapcat (fn [[_part names]]
                       (map #(format "napkinsketch_book/%s.clj" %) names)))
          chapters)))

(def ^:private chapters->parts
  (fn [chapters]
    (mapv (fn [[part names]]
            {:part part
             :chapters (mapv #(format "napkinsketch_book/%s.clj" %) names)})
          chapters)))

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
