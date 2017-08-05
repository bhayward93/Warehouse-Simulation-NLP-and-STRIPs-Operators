(ns nlp.utils)

(defn in? ;https://stackoverflow.com/questions/3249334/test-whether-a-list-contains-a-specific-value-in-clojure
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))