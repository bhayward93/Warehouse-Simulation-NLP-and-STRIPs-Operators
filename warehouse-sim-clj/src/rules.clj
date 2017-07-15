(ns rules
  (:require [cgsx.tools.matcher :refer :all]
            [clojure.math.combinatorics :as combinatorics]
            [nl-injector :refer :all]
            )
  )


;A defined rule set
(def rule-set
  '( (rule 1 (connects (?x ?y) (?dx ?dy))
           :=> (connects (?dx ?dy) (?x ?y))
               (is floor (?dx ?dy))) ;Mirroring the connection statements

     ))


(def directional-conflict-resolver '((rule 1 ())))












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

