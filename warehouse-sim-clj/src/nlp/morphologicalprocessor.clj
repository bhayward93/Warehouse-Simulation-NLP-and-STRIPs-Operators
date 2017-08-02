(ns nlp.morphologicalprocessor
 (use [clojure.string :only (join split)]
      [clojure.string :as s]
      [nlp.lexicon]
      [clojure.walk]))

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
 

   
     

   

;replace that part of input with the alternative meaning recur
;(prefix-remover "non")  

;(defn find-match-in-word [query lex]
; (re-find #ery lex))


;Example input: '(Push the blue block forward)
;pick apart words by looking for common suffixes and
;prefixes, and then transform the words accordingly

;[1] Had to use java here. So much simpler. Alternative is 
;    a lot of map/reducing type stuff. Example of clojure below
;    (map s/includes? (map first (stringify-keys prefixes)) "anti" )
;    http://stackoverflow.com/questions/26386766/check-if-string-contains-substring-in-clojure