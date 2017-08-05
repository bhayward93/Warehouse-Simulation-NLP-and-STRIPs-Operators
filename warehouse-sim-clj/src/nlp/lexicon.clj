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

               drop-off    {:cat verb :sem drop-off}
               place       {:cat verb :sem drop-off} ;Conflicts with this. Noun/Verb
               drop        {:cat verb :sem drop-off}
               put         {:cat verb :sem drop-off}
               take        {:cat verb :sem drop-off} ;this could be problematic.

               engine-crate        {:cat noun :sem engines-crate}
               pistons-crate       {:cat noun :sem pistons-crate}
               circular-saws-crate {:cat noun :sem circular-saws-crate}
               engines-crate       {:cat noun :sem engines-crate}
               nails-crate         {:cat noun :sem nails-crate}
               bolts-crate         {:cat noun :sem bolts-crate}
               screws-crate        {:cat noun :sem screws-crate}
               lights-crate        {:cat noun :sem lights-crate} ;could implement as delicate
               compressors-crate   {:cat noun :sem compressors-crate}
               wheels-crate        {:cat noun :sem wheels-crate}
               tyres-crate         {:cat noun :sem tyres-crate}
               girders-crate       {:cat noun :sem girders-crate}
               wooden-planks-crate {:cat noun :sem wooden-planks-crate}
               grates-crate        {:cat noun :sem grates-crate}
               jacks-crate         {:cat noun :sem jacks-crate}


               on          {:cat prep :sem (stored-on (shelf ?s) ?object ?o)}
               under       {:cat prep :sem (stored-on (shelf ?s) ?object ?o)}

               to          {:cat det :sem drop-off}
               the         {:cat det :sem previous-noun}
               a           {:cat det :sem '1}
               an          {:cat det :sem '1}
               any         {:cat det :sem random}
               that        {:cat det :sem previous-noun}
               this        {:cat det :sem previous-noun}
               it          {:cat det :sem previous}

               and         {:cat con :sem next}
               then        {:cat con :sem next}
               next        {:cat con :sem next}

               1           {:cat det :sem 1}
               2           {:cat det :sem 2}
               3           {:cat det :sem 3}
               4           {:cat det :sem 4}
               5           {:cat det :sem 5}
               6           {:cat det :sem 6}
               7           {:cat det :sem 7}
               8           {:cat det :sem 8}
               9           {:cat det :sem 9}
               one         {:cat num :sem 1}
               two         {:cat num :sem 2}
               three       {:cat num :sem 3}
               four        {:cat num :sem 4}
               five        {:cat num :sem 5}
               six         {:cat num :sem 6}
               seven       {:cat num :sem 7}
               eight       {:cat num :sem 8}
               nine        {:cat num :sem 9}})





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


;see Dictionary.com. Look up "re-" for the prefix re.

;taken from http://teacher.scholastic.com/reading/bestpractices/vocabulary/pdf/prefixes_suffixes.pdf
;Considerations:
;-Group words by type; efficiency?
;-Need to scan lexicon fully every time, to pick up ambiguty.
;-the colors used in the nlp. these can change or be added to but
;-need to be fed in with the lexicon. Note that colours can be nouns or adjectives.
;-switch could be taken multiple ways
(def all-collectables '[
    pistons-crate
    circular-saws-crate
    engines-crate
    nails-crate
    bolts-crate
    screws-crate
    lights-crate ;Could implement this as delicate
    compressors-crate
    wheels-crate
    tyres-crate
    girders-crate
    wooden-planks-crate
    grates-crate
    jacks-crate])
