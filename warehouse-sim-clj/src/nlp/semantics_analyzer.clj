(ns nlp.semantics-analyser
  (use [clojure.string :only (join split)]
       [clojure.string :as s]
       [nlp.lexicon]
       [clojure.walk])
  (:require [clojure.string :as str]))

;Remove this in final build and let core.clj load it
(load-file "./src/nlp/lexicon.clj")
;Gets the users input
(defn get-input [x])

;will call get-input when everything is linked up.
;iterate through words in the input
;(prefix-remover "non anti interaction anti nonbeliever anti-matter person thing man d")

(defn prefix-remover
  ([input]                                                                  ;default input
   (prefix-remover (split input #"\s+") (split input #"\s+")))             ;split into words e.g. '("foo bar") => '("foo" "bar")

  ([current found] ;second pass

   (println "recurred/started \n> Current:       " current
            "  \n> First Currrent:" (first current)
            "  \n> Rest Current:  " (into [](rest current))) ;debugging
   (if
     (and
       (.contains (map first (stringify-keys prefixes))(first current)); if the first of current contains a prefix
       (not (empty? current)))                                         ;and current is not empty
     (
       (print "==========================================\n")          ;debugging; shows a found prefix
       (prefix-remover (into [] (rest current)) (conj found current))));recur with the rest of current

   (if (not (empty? (rest current)))                                   ;if is just not empty, recur with the rest
     (prefix-remover (into [] (rest current)) (conj found current)))   ;see above
   "> Finished | Found:" found))
;
;(def remove-prefix [word & [debug]]
;  (if (not (lexicon word))
;    (
;      (when debug (println word))
;      (when prefixes word)
;      str/replace
;                 (map #(.contains % word) lexicon))
;        (println "hello"))
;
;     ;   (when debug (println  "prefix/suffix found "  ))
;1
;      )