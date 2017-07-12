(ns ops
  (:gen-class)
  (:require
            [util :refer :all]
            ))

;TODO The preconditions for move are never triggered.
;TODO Quite puzzled but at a state where I can hop from one square to another. Doing it via pathfinding though is proving to be rather confusing.
;MATCHER DOCS http://s573859921.websitehome.co.uk/pub/clj/matcher/user%20guide.htm#_:not_and_:guard

(def state-ops
  '{move {
          :pre  (
                  (isa forklift (?forklift ?n))
                  (on (?x ?y) (?forklift ?n))
                  (is floor (?dx ?dy))              ;this will cause it to work, taking away the below statement.
                                                    ;if the logic there is valid though why is it not working?g

                  (:guard
                            (or                       ;LOGIC HERE IS VALID (or atleast it is when fed through REPL.
                                (= (? dx) (+ (? x) 1));Try find a way to see these variables??? print in ops-search perhaps?
                                (= (? dx) (- (? x) 1));(is floor) was added in to try and give the matcher something initial about dx dy
                                (= (? dx) (? y)))     ;  the implication of this; because without the guard the, ops work,
                            (or (= (? dy) (+ (? y) 1));  is that the guard is causing the fault; perhaps something minor syntactical that i am missing?
                                (= (? dy) (- (? y) 1))
                                (= (? dy) (? y)))
                    )


;                  (:not (unavailable (?dx ?dy)))) ;THis should work fine, but temporarily taking it out to be safe whilst debugging guard
          :add ((on (?dx ?dy) (?forklift ?n)) ;Not added? implies the operator is never triggered
                (unavailable (?x ?y)))
          :del ((on (?x ?y) (?forklift ?n))) ;I believe this worked fine last time it was tested? double check by commenting the guard out.
          :txt (forklift ?n moves to (?dx ?dy)) ;should be fine, possibly a slight text error but thats fine
          :cmd (move forklift) ;should be fine
     }

;    :test {:pre ()
;           :add  (success)
;           :del  ((at (?x ?y) (?forklift ?n)))
;           :txt  (?
;     forklift moves to (?dx ?dy))
;           :cmd  ()
          })


;(def test-pass-state
;  '#{(at (1 0) (forklift 156))
;     (isa forklift (forklift 156)) ;(isa forklift (forklift 495))
;     (at (0 0) none)  ;(at (7 0) none)
   ;  (:guard within-one-patch? '(?x) '(?y) '(?dx) '(?dy))
;     (connects (1 0) (0 0))
;     (at (0 1) none) (connects (0 0) (0 1))})
;





















