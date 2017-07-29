(ns nlp.lexicon
  (:gen-class)
  (:require [cgsx.tools.matcher :refer :all]
            [clojure.string :as str]))
;stores the words known
;(def old-lexicon {:box :noun,    :square :noun,   :object :noun, :crate :noun  ;for box
;                  :move :verb,  :go :verb, :drive                              ;verb / for moving
;                  :worker :noun, :player :noun, :agent :noun, :man :noun, :woman :noun,
;                  :person :noun, :guy :noun, :character :noun ;the player
;                  :gentle :adjective  :force :verb
;                  :block :noun :switch :noun
;              })

;A lexicon of words that can be used; some taken from Shrldu

(def lexocon '{box         {:cat noun :sem (isa ?x crate)}
               square      {:cat noun :sem (isa ?x crate)}
               object      {:cat noun :sem (isa ?x crate)}
               crate       {:cat noun :sem (isa ?x crate)}
               collectable {:cat noun :sem (isa ?x crate)}
               item        {:cat noun :sem (isa ?x crate)}

               bay         {:cat noun :sem (isa ?x bay)}
               loading-bay {:cat noun :sem (isa ?x bay)}
               shelf       {:cat noun :sem (isa ?x bay)}

               forklift    {:cat noun :sem (isa forklift (forklift ?f))}
               picker      {:cat noun :sem (isa forklift (forklift ?f))}

               move        {:cat verb :sem pickup}
               grab        {:cat verb :sem pickup}
               grasp       {:cat verb :sem pickup}
               pickup      {:cat verb :sem pickup}
               get         {:cat verb :sem pickup}
               lift        {:cat verb :sem pickup}
               collect     {:cat verb :sem pickup}

               to          {:cat verb :sem drop-off}
               drop-off    {:cat verb :sem drop-off}
               place       {:cat verb :sem drop-off} ;Conflicts with this. Noun/Verb
               drop        {:cat verb :sem drop-off}

               engine-crate{:cat noun :sem (isa ?x engine-crate)}

               on          {:cat prep :sem (stored-on (shelf ?s) ?object ?o)}
               under       {:cat prep :sem (stored-on (shelf ?s) ?object ?o)}

               the         {:cat det}
               a           {:cat det}
               an          {:cat det}
               any         {:cat det}
               that        {:cat det}
               this        {:cat det}
               })

;Shrldu
(defn word-check [wtype word]
  (if-let [wdef (word lexicon)]
    (if (= (:cat wdef) wtype)
      (or (:sem wdef) 'undef)
      )))

(defn adj?   [x] (word-check 'adj x  ))
(defn det?   [x] (word-check 'det x  ))
(defn noun?  [x] (word-check 'noun x ))
(defn prep?  [x] (word-check 'prep x ))
(defn verb1? [x] (word-check 'verb1 x))  ;; verb with arity 1
(defn put2?  [x] (word-check 'put2 x ))   ;; verb with arity 1
(defn make?  [x] (word-check 'make x ))

(defn get-word-type [w]
  (get (get lexicon w) :cat)
  )



(defn split-to-symbols [_str]
  "Splits a string and converts the split strings into symbols,
   returns the word type of the symbols, provided they are in the lexicon."
  (map get-word-type ;get the type of word
       (mapv symbol ;change all elements to symbol
             (str/split _str #" ") ;split the string on whitespace
             )))

  ;NLP-intro3a


;(defmatch build-grammar []
;  '( (s1 (sentence -> noun-phrase verb-phrase))
;     (np (noun-phrase -> determiner noun))
;     (vp (verb-phrase -> verb noun-phrase))
;     ))

;(defmatch noun-group []
;          ((-> ?d det?) (-> ?n noun?))       ; Det N => NG
;            :=> {:cat  'noun-group
;                 :sem  (list (? n))}
;
;          (((-> ?d det?) (-> ??a adjG) (-> ?n noun?))    ; Det AdjG N => NG
;            :=> {:cat  'ng
;                 :sem  (mout '(??a ?n))
;                 }))

(defn adjG [lis]                   ; AdjG -> *Adj
  (and (every? #(adj? %) lis)
       (map #(adj? %) lis)
       ))

;(defn get-word-type [wo]
;  mfor
;  )

;suffixes - note: if someone uses the word size, will it find ize in the suffixes list,
;           and wrongly deduce its meaning.
;(def sufixes (:acy :al :ance :dom :er :or :ism :ist :ty
;               :ment :ness :ship :sion :tion :ate :en
;               :ify :fy :ize :ise :able :ible :al :esque
;               :ful :ic :ical :ious :ous :ish :ive :less :y))
;taken from http://grammar.about.com/od/words/a/comsuffixes.htm

;prefixes - note, can convert this and suffixes into a different datatype using REPL.
;           should we be storing the values of these symbols in a map, with the value being the meaning
;      format:  {prefix {reworded [word type]}} - note word type feild may be off, and also require ordering.

(def prefixes {:anti{:no        [:noun ]}
                :de {:reverse   [:verb :adverb]}
               :dis {:not       [:adjective :verb :noun]}
                :en {:within    [:adjective :noun]}       ;"to put (something/someone) into <noun>",
                :em {:within    [:noun :adjective]}       ;http://www.dictionary.com/browse/en-
              :fore {:already   [:noun :adjective]}       ;{:before
                :in {:in        [:verb :adjective :noun]}
                :im {:in        [:verb :adjective :noun]}
                :il {:not       [:noun :adjective :verb]}
                :ir {:not       [:noun :adjective :verb]}
              :inter{:interior  [:noun :adjective :verb :adverb]}
               :mid {:middle    [:verb :adjective :verb]}
               :mis {:wrong     [:verb :noun :adjective]}
               :non {:not       [:adjective :verb :noun]}
              :over {:overly    [:adjective :adverb :prep]}
               :pre {:prior     [:verb :adjective :adverb :noun]} ;do before
                :re {:again     [:verb :adjective :noun :adverb]} ;do again
               :semi{:part      [:noun :adjective :verb :adverb :pronoun]}
               :sub {:underneath[:noun :adjective :adverb]}
              :super{:greater   [:noun :adjective :adverb]}
              :trans{:cross     [:noun :adjective :adverb :adverb]}
                 :un{:not       [:adjective :verb :pronoun :noun]}
              :under{:beneath   [:adverb :prep]}
          ;:--balancer--{:mapbalance[:map :balancing]}
                        })

;see Dictionary.com. Look up "re-" for the prefix re.

;taken from http://teacher.scholastic.com/reading/bestpractices/vocabulary/pdf/prefixes_suffixes.pdf
;Considerations:
;-Group words by type; efficiency?
;-Need to scan lexicon fully every time, to pick up ambiguty.
;-the colors used in the nlp. these can change or be added to but
;-need to be fed in with the lexicon. Note that colours can be nouns or adjectives.
;-switch could be taken multiple ways
