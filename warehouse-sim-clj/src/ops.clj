(ns ops
  (:gen-class)
  (:require
            [util :refer :all]
            ))

;TODO The preconditions for move are never triggered.
;TODO Quite puzzled but at a state where I can hop from one square to another. Doing it via pathfinding though is proving to be rather confusing.
;MATCHER DOCS http://s573859921.websitehome.co.uk/pub/clj/matcher/user%20guide.htm#_:not_and_:guard

(def state-ops
  (let [floor '(? dx)]
  '{move {
          :pre (
                ; (adjacent (?forklift ?n) (?dx ?dy))
                 (isa forklift (?forklift ?n))
                 (on (?x ?y) (?forklift ?n))
                 (connects (?x ?y) (?dx ?dy)) ;going to need to implement conflict resolution
                 (is floor (?dx ?dy))
                 (:not (unavailable (?dx ?dy))) ;THis should work fine, but temporarily taking it out to be safe whilst debugging guard
                 (:guard  (do (println "HELLO FROM THE GUARD INSIDE")(and ;this println works! TODO Start here tomorrow.
                            (or
                              (= (+ (? dx) 1) (? dx)) ;this should be native clojure; need to check the variables using the above.
                              (= (- (? dx) 1) (? dx)) ;Also try EAP builds for cursive to see if the debugger works!
                              (= (? dx)(? dx)))
                            (or
                              (= (? dy) (? dy))
                              (= (? dy) (? dy))
                              (= (? dy)(? dy))))))
                 )


          :add (
                 (on (?dx ?dy) (?forklift ?n)) ;Not added? implies the operator is never triggered
                 (unavailable (?x ?y))
                ;(adjacent (?forklift ?n) not-set)
                 )
          :del (
                 (on (?x ?y) (?forklift ?n)
                 (adjacent (?forklift ?n) (?dx ?dy)))
                 )
          :txt (forklift ?n moves to (?dx ?dy))
          :cmd (+ 1 1)
          }}))
    ;init {
    ;      :pre ((?anything)) ;should be able to do things before operating (e.g. adding a goal in; but how? Can I get hold of the goal arg from ops-search.
    ;      :add ((init-lock))
    ;      :del (())
    ;      :txt (())
    ;      :cmd (())}


;(def test-pass-state
;  '#{(at (1 0) (forklift 156))
;     (isa forklift (forklift 156)) ;(isa forklift (forklift 495))
;     (at (0 0) none)  ;(at (7 0) none)
   ;  (:guard within-one-patch? '(?x) '(?y) '(?dx) '(?dy))
;     (connects (1 0) (0 0))
;     (at (0 1) none) (connects (0 0) (0 1))})
;
