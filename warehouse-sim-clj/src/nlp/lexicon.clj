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

(def lexicon '{box         {:cat noun :sem crate}
               square      {:cat noun :sem crate}
               object      {:cat noun :sem crate}
               crate       {:cat noun :sem crate}
               collectable {:cat noun :sem crate}
               item        {:cat noun :sem crate}

               bay         {:cat noun :sem bay}
               loading-bay {:cat noun :sem bay}
               shelf       {:cat noun :sem bay}

               forklift    {:cat noun :sem forklift}
               picker      {:cat noun :sem forklift}

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
               put         {:cat verb :sem drop-off}

               engine-crate{:cat noun :sem (isa ?x engine-crate)}

               on          {:cat prep :sem (stored-on (shelf ?s) ?object ?o)}
               under       {:cat prep :sem (stored-on (shelf ?s) ?object ?o)}

               the         {:cat det :sem last}
               a           {:cat det :sem 1}
               an          {:cat det :sem 1}
               any         {:cat det :sem random}
               that        {:cat det :sem aforementioned} ;?
               this        {:cat det :sem aforementioned}
;               to          {:cat con}

               and         {:cat con :sem next}
               then        {:cat con :sem next}
               next        {:cat con :sem next}

               '1           {:cat det :sem '1}
               '2           {:cat det :sem '2}
               '3           {:cat det :sem '3}
               '4           {:cat det :sem '4}
               '5           {:cat det :sem '5}
               '6           {:cat det :sem '6}
               '7           {:cat det :sem '7}
               '8           {:cat det :sem '8}
               '9           {:cat det :sem '9}
               one         {:cat num :sem '1}
               two         {:cat num :sem '2}
               three       {:cat num :sem '3}
               four        {:cat num :sem '4}
               five        {:cat num :sem '5}
               six         {:cat num :sem '6}
               seven       {:cat num :sem '7}
               eight       {:cat num :sem '8}
               nine        {:cat num :sem '9}

               })




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
