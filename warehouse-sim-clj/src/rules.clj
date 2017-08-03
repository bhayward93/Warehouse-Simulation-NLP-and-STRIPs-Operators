(ns rules
  (:require [cgsx.tools.matcher :refer :all]
            ;[clojure.math.combinatorics :as combinatorics]
            ;[nl-injector :refer :all]
            )
  )
;
;;A defined rule set
;(def semantic-rules
;  "Rules for semantics; specifically compound sentences."
;  (('rule1 (-> ?d det?) (-> ?n noun? :=> '(noun-phrase (? d) (?n)))            ; Determiner + Noun = Noun-Phrase
;     ('rule2 (verb? noun-phrase) :=> (verb-phrase))    ; Verb + Noun-Phrase = Verb-Phrase
;     ('rule3 (noun-phrase? verb-phrase) :=> (sentence)) ; Noun-Phrase + Verb-Phrase = Sentence
;     ))
  (def dyn-rules
    '((rule 1  ;(on (6 0) (forklift 201))
            ;(on (?x ?y) (forklift ?n))
            ;(connects (?ox ?oy) (?dx1 ?dy1))
            ;(connects (?ox ?oy) (?dx2 ?dy2))
            ;(connects (?ox ?oy) (?dx3 ?dy3))
            (connects (?ox ?oy) (?dx4 ?dy4))
            (goal (?gx ?gy) (?forklift ?n))
            ;(:guard (println ("ox" (? ox) )
            ;                 ("dx1" (? dx1))
            ;                 ("dx2" (? dx2))
            ;                 ("dx3" (? dx3))
            ;                 ("dx4" (? dx4))
            ;  (:guard (or
            ;           (< (-
            ;               (? gx)
            ;               (? dx1)
            ;             )
            ;             (-
            ;               (? gx)
            ;               (? ox)
            ;            ))
            ;           (<
            ;             (-
            ;               (? gy)
            ;               (? dy1)
            ;             )
            ;             (-
            ;             (? gy)
            ;             (? oy)
            ;    ))))
            :=>
            (issorted ?gx ?dy)
            )))


  ;(def dyn-rules
  ;  (let [x ?x]
  ;  '(rule 1
  ;         (on (?x ?y) (?forklift ?n))
  ;         :=>
  ;         (adjacent ~(+ x 1 )))))

  ;The below are slight modifications of Dr Ian Wood/Simon Lynch's functions.

  ;Applies a singular rules
  (defmatch apply-single-rule [facts]
            ((rule ?n ??ante :=> ??cons)   ;Destructuring the rule.
              :=> (mfor* [(? ante) facts]
                         (mout (? cons))
                         ))) ;out the consequents.)

  ;Applies all rules
  (defn apply-all-rules [rules facts]
    (reduce concat ;concats the output from the map anon function below.
            (map #(apply-single-rule % facts) rules)
            ))


  ;Forward chaining to apply the rules
  (defn fwd-chain [rules facts]
    (let [new-facts (apply-all-rules rules facts)]
      (if (clojure.set/subset? new-facts facts)
        facts
        (recur rules (clojure.set/union facts new-facts))
        )))
