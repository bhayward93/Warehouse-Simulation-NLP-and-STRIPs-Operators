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


(def dyn-rules
  '((rule 1 ;(on (6 0) (forklift 201))
            (on (?x ?y) (forklift ?n))
             (connects (?ox ?oy) (?dx1 ?dy1))
             (connects (?ox ?oy) (?dx2 ?dy2))
             (connects (?ox ?oy) (?dx3 ?dy3))
             (connects (?ox ?oy) (?dx4 ?dy4))
             (goal (?gx ?gy) (forklift ?n))
             (:guard (or
                       (< (-
                           (? gx)
                           (? dx1)
                         )
                         (-
                           (? gx)
                           (? ox)
                        ))
                       (<
                         (-
                           (? gy)
                           (? dy1)
                         )
                         (-
                         (? gy)
                         (? oy)
                ))))
          :=>
          (issorted ?gx ?dy)
                       )))





;
;(def dyn-rules
;  (let [x ?x]
;  '(rule 1
;         (on (?x ?y) (?forklift ?n))
;         :=>
;         (adjacent ~(+ x 1 )))))
;
;(defn closest-direction-to-goal [state goal]
;  )
;
;(def directional-conflict-resolver '((rule 1 )
;                                           :=> (adjacent-patch)))
;
;
;
;








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

;((on (7 0) (forklift 201)))(() (goal (9 0) (forklift 495)) (at (4 13) grates-crate 192) (adjacent (forklift 201) (7 0)) (at (4 13) wheels-crate 199) (at (12 12) tyres-crate 183) (adjacent (forklift 201) (6 1)) (at (12 10) girders-crate 184) (at (12 13) circular-saws-crate 197) (at (8 2) engines-crate 189) (at (12 6) nails-crate 190) (at (12 10) jacks-crate 188) (at (8 4) lights-crate 194) (at (8 3) lights-crate 187) (at (12 15) pistons-crate 198) (at (12 1) lights-crate 186) (adjacent (forklift 201) (6 16)) (at (12 3) girders-crate 195) (at (4 13) circular-saws-crate 180) (at (12 14) jacks-crate 200) (adjacent (forklift 201) (5 0)) (at (8 13) bolts-crate 185) (at (12 5) wheels-crate 191) (at (12 1) wooden-planks-crate 196) (at (4 11) bolts-crate 182) (at (4 10) grates-crate 181) (on (6 0) (forklift 201)) (isa forklift (forklift 201)) (at (12 3) wheels-crate 193))
