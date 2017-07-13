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
          :pre        (
                        (isa forklift (?forklift ?n))
                        (on (?x ?y) (?forklift ?n))
                ;        (is floor (?dx ?dy))              ;this will cause it to work, taking away the below statement.
                        (connects (?x ?y) (?dx ?dy))



                                   (:not (unavailable (?dx ?dy)))) ;THis should work fine, but temporarily taking it out to be safe whilst debugging guard
                 :add ((on (?dx ?dy) (?forklift ?n)) ;Not added? implies the operator is never triggered
                        (unavailable (?x ?y)))
                 :del ((on (?x ?y) (?forklift ?n))) ;I believe this worked fine last time it was tested? double check by commenting the guard out.
                 :txt (forklift ?n moves to (?dx ?dy)) ;should be fine, possibly a slight text error but thats fine
                 :cmd (move forklift) ;should be fine
                 }
          })


;(def test-pass-state
;  '#{(at (1 0) (forklift 156))
;     (isa forklift (forklift 156)) ;(isa forklift (forklift 495))
;     (at (0 0) none)  ;(at (7 0) none)
   ;  (:guard within-one-patch? '(?x) '(?y) '(?dx) '(?dy))
;     (connects (1 0) (0 0))
;     (at (0 1) none) (connects (0 0) (0 1))})
;





















