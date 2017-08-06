(ns nlp.syntactical-analyser
  (:gen-class)
  (:require [clojure.string :as str]
            [cgsx.tools.matcher :refer :all]
            [rules :refer :all]
            [nlp.lexicon :refer :all]
            [util :refer :all]))


(defn split-to-symbols [_str]
  "Splits a string and converts the split strings into symbols,
   returns the word type of the symbols, provided they are in the lexicon."
  ;  (map get-word-type  ;Get the type of word.
  (mapv symbol   ;Change all elements to symbol.
        (str/split _str #" "))) ;Split the string on whitespace.


;Shrldu
(defn word-check [wtype word]
  (println "word-check : type = " wtype " / word = " word)
    (if-let [wdef (word lexicon)]
      (if (= (:cat wdef) wtype)
        (or (:sem wdef) 'undef))))


(defn adj?   [x] (word-check 'adj x))
(defn det?   [x] (word-check 'det  x))
(defn noun?  [x] (word-check 'noun x))
(defn verb?  [x] (word-check 'verb x))  ;; verb with arity 1
(defn put2?  [x] (word-check 'put2 x))   ;; verb with arity 1
(defn num?   [x] (word-check 'num x))
(defn noun-phrase? [x] (word-check 'np x));(noun-phrase x))
(defn connective?  [x] (word-check 'con x))

()

(defmatch parse []
          "I am aware that instead of repetition, i should be using
          (??d det?) in place of repeating code for multiple dets. I could not get this to work

          Example of how I was trying to use ??det:
          (((-> ?v verb?) (-> ??d det?) (-> ?n noun?)      :=>)  (mout ((? v)  (? n)  (? d))))"
          (((-> ?v verb?) (-> ?d det?) (-> ?n noun?))       :=>   {:cat 'ng}(mout (list (? v) (? d) (? n)))) ;move the box
          (((-> ?v verb?) (-> ?num num-txt?) (-> ?n noun?)) :=>   (mout (list (? v) (? num) (? n)))) ;could use a nested list to keep num and obj together
          (((-> ?v verb?) (-> ?n noun?))                    :=>   (mout (list (? v) 'the (? n))))) ;grab box
         ; (((-> ?v verb?) (-> ?d det?) (-> ?d det?) (-> ?n noun?)      :=>    (mout (? v))));(mfor [(?? d)(mout(list (? v)  (? n)  (?? d)))])))




          ;(((-> cmd?
          ;(((-> ?d ?)  n v det n))
          ;(([??ante (?con connective?) ??post])            :=>   (mout (? con)))
          ;(((-> )))
          ;  (((-> ?v verb?) (-> ?np noun-phrase)) :=> (println "SUCCESS!!"))
          ;  (((-> ?vp verb-phrase)(-> ?np noun-phrase)) :=> (list (? vp) (? np))); move a box to drop-off
          ;  (((-> ?vp verb-phrase)) :=> (? vp)) ;Should be a simple case of something like move a box, drop a crate.






(defn num-txt? [x]
  "Returns true or false if value is either an integer or a symbol matching
 the word of a number i.e 'ten, 'one"
  (let [number-words '[zero no none nought naught one two three four five six seven eight nine ten]] ;decided upon using this instead of an enum/vars.
    ;(re-find #"\b[A-Z]+\b" could come in useful in the future
    (or
      (integer? x)
      (some #(= x %) number-words)))) ;


(defn get-word-type [w]
  "Return the type of a word fed in, providing that it is in the lexicon."
  (get (get nlp.lexicon/lexicon w) :cat))


(defn split-lis-at-and [_str]
  "Split a string into vectors, at the place of the word 'and'
   Could be adapted to have wider usage and split on any word."
  "Partial Source: #Clojure IRC on FreeNode"
 ; (println "split list => " _str)
  (->
    (split-to-symbols _str)
    (->> (partition-by (complement '#{and}))
         (remove '#{[and]}))))

(defn syn-analyser [_str & [debug]]
  (map parse (split-lis-at-and _str))
  )



(def semantic-rules
  "Rules for semantics; specifically compound sentences."
  '(
     (??a and ??b) => '(??a n ??b)          ; Determiner + Noun = Noun-Phrase
     (??a and then ??b) => '(??a n ??b)))


(defn apply-morph-rules [rules sentence]
  (if (empty? rules) sentence
                     (mlet ['(?pre => ?post) (first rules)]
                           (mif [(? pre) sentence]
                                (recur rules (mout (? post)))
                                (recur (rest rules) sentence)))))





;(defn analyze-sem [_str]
;  (let [_strvec (str/split _str #"and")
;        _newstr ""] ;vector with multiple strings, split on and
;    (fn itt-vec [_strvec _newstr]
;      (when    (not= (count _strvec) 1)
;        (cons
;          _newstr
;          (parse (split-to-symbols (first _strvec))))
;        (itt-vec     (rest _strvec))))
;    (cons _newstr
;          (parse   (split-to-symbols(first _strvec)))
;          )
;    _newstr
;    ))
;
;
;;
;(defmatch noun-group []
;          (((-> ?n1 noun?) (-> ?n2 noun?)) ;if ?d is of type det
;            :=> {:cat  'noun-group              ;mark catagory as NG
;                 :sem  (list (? n)(? n2))     ;Implies a list
;                 })
;
;          )
;
;(defmatch noun-phrase []
;          (((-> ?d det?)(-> ?n noun?)) :=>  { :cat 'noun-phrase
;                                              :sem (? n)}) ; So, the box -> move box
;            (((-> ?d det?)(-> ?a adj?)(-> ?n noun?)) :=> { :cat 'noun-phrase
;                                                           :sem (? n)}) ; So, move the box -> move box)
;            )
;
;
;
;
;;(defn adjG [lis]                   ; AdjG -> *Adj
;;  (and (every? #(adj? %) lis)
;;       (map #(adj? %) lis)
;;       ))
;
;
;(defmatch verb-phrase []
;          (((-> ?np noun-phrase) (-> ?v verb?)) :=>
;            :=>
;            {:cat 'vp              ;mark catagory as NG
;             :sem (list (? v) (noun-phrase))     ;Modifies the meaning of the verb, need to add this properly with adjectives
;             })
;          (((-> ?v verb?) (-> ??np noun-phrase)) :=>
;            :=>
;            {:cat 'vp
;             :sem (list (? v) (? np))
;             })
;          (((-> ?v verb?) (-> ?num num-txt?) (-> ?np noun-phrase)) :=>
;            :=>
;            {:cat 'vp
;             :sem (list (? v) (? np))           ;(fn [x] (when (not=(? num) x
;             })                                        ;(do ()
;          )                                            ;(recur (inc x)))) ;Modifies the meaning of the verb, need to add this properly with adjectives
;
;          ;(re-find #"\b[A-Z]+\b" could come in useful in the future
;
;
;
;(defn find-verb-phrase [_str]
;  (let [split (split-to-symbols _str)]
;    (mfor*(verb-phrase split)
;        (mout split))))

;(def semantic-rules
;  "Rules for semantics; specifically compound sentences."
;  (('rule1 (?x det?) :=> '(noun-phrase))            ; Determiner + Noun = Noun-Phrase
;    ('rule2 (?x noun-phrase) :=> '(verb-phrase))    ; Verb + Noun-Phrase = Verb-Phrase
;    ('rule3 (?x verb-phrase) :=> '(sentence)); Noun-Phrase + Verb-Phrase = Sentence
;    ))
;