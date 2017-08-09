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


(def prefixes '{anti{:sem no};[:noun ]}
                de {:sem reverse};[:verb :adverb]}
                dis {:sem not};[:adjective :verb :noun]}
                en {:sem within};[:adjective :noun]}       ;"to put (something/someone) into <noun>",
                em {:sem within};[:noun :adjective]}       ;http://www.dictionary.com/browse/en-
                fore {:sem already};[:noun :adjective]}       ;{:before
                in {:sem in};[:verb :adjective :noun]}
                im {:sem in};[:verb :adjective :noun]}
                il {:sem not};[:noun :adjective :verb]}
                ir {:sem not};[:noun :adjective :verb]}
                inter{:sem interior};[:noun :adjective :verb :adverb]}
                mid {:sem middle};[:verb :adjective :verb]}
                mis {:sem wrong};[:verb :noun :adjective]}
                non {:sem not};[:adjective :verb :noun]}
                over {:sem overly};[:adjective :adverb :prep]}
                pre {:sem prior};[:verb :adjective :adverb :noun]} ;do before :re {:again     [:verb :adjective :noun :adverb]} ;do again
                semi{:sem part}; [:noun :adjective :verb :adverb :pronoun]}
                sub {:sem underneath};[:noun :adjective :adverb]}
                super{:sem greater};[:noun :adjective :adverb]}
                trans{:sem cross};[:noun :adjective :adverb :adverb]}
                un{:sem not};[:adjective :verb :pronoun :noun]}
                under{:sem beneath}});[:adverb :prep]}

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