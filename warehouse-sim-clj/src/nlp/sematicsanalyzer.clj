(ns nlp.semantics-analyzer)
  (:gen-class )
  (:require [clojure.string :as str]
            [cgsx.tools.matcher :refer :all]
            [rules]))

(defn semantically-analyze [_str]
 "main method of the semantic analysis class, this function
  ties everything together"
  (split-to-symbols _str)
  )

(defn split-to-symbols [_str]
  "Splits a string and converts the split strings into symbols,
   returns the word type of the symbols, provided they are in the lexicon."
  (map get-word-type  ;Get the type of word.
       (mapv symbol   ;Change all elements to symbol.
             (str/split _str #" ") ;Split the string on whitespace.
             )))