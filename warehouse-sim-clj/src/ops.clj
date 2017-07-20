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
          :pre (
                 (isa forklift (forklift ?n))
                 (on (?ox ?oy) (forklift ?n))
                 (goal (?gx ?gy) (forklift ?n))
                 )


          :add (
                 (on (?gx ?gy) (forklift ?n))
                 (goal (?gx ?gy) (forklift ?n))
                 )
          :del ((on (?ox ?oy) (forklift ?n)))
          :txt (forklift moves to (?gx ?gy))
          :cmd (move-forklift ?gx ?gy)
          }})

1
;init {
;      :pre ((?anything)) ;should be able to do things before operating (e.g. adding a goal in; but how? Can I get hold of the goal arg from ops-search.
;      :add ((init-lock))
;      :del (())
;      :txt (())
;      :cmd (())}




;(def state-ops
;  '{move {
;          :pre (
;                 (:not (unavailable (?dx4 ?dx4)))
;                 (isa forklift (forklift ?n))
;                 (on (?ox ?oy) (forklift ?n))
;                 (connects (?ox ?oy) (?dx1 ?dy1))
;                 (connects (?ox ?oy) (?dx2 ?dy2))
;                 (connects (?ox ?oy) (?dx3 ?dy3))
;                 (connects (?ox ?oy) (?dx4 ?dy4))
;
;                 ;(adjacent (forklift ?n) (?dx1 ?dy1))
;                 ;(adjacent (forklift ?n) (?dx2 ?dy2))
;                 ;(adjacent (forklift ?n) (?dx3 ?dy3))
;                 ;(adjacent (forklift ?n) (?dx4 ?dy4))
;
;                 (goal (?gx ?gy) (forklift ?n))
;
;                 ;NO INDICATION OF WHAT IS TOWARDS THE G0AL,
;                 ;currently this is working as a search algorithm T=(O^2)
;
;                 (:guard
;                 ;  (or(not= (? dx1) (? dx2))
;                 ;     (not= (? dy1) (? dy2)))
;                 ;  (or (not= (? dx2) (? dx3))
;                 ;     (not= (? dy2) (? dy3)))
;                 ;(or  (not= (? dx3) (? dx4))
;                 ;     (not= (? dy3) (? dy4)))
;                 ;(or  (not= (? dx1) (? dx4))
;                 ;     (not= (? dy1) (? dy4)))
;                 ;(or  (not= (? dx1) (? dx3))
;                 ;     (not= (? dy1) (? dy3)))
;                 ;(or  (not= (? dx2) (? dx4))
;                 ;     (not= (? dy2) (? dy4))) ;this ensures dx4 dy4 is the closest to the goal
;                 ;(>  (or (- (? gx) (? dx1))
;                 ;        (- (? gy) (? dy1)))
;                 ;    (or (- (? gx) (? dx2))
;                 ;        (- (? gy) (? dy2))))
;                 ;(>  (or (- (? gx) (? dx2))
;                 ;        (- (? gy) (? dy2)))
;                 ;    (or (- (? gx) (? dx3))
;                 ;        (- (? gy) (? dy3))))
;                 (>  (- (? gx) (? dx4))
;                     (- (? gx) (? dy3)))
;                     )
;
;
;                 ;(:guard  (print
;                 ;           "--------------------------\n"
;                 ;           " 1 (" (? dx1) (? dy1)")\n"
;                 ;         "2 (" (? dx2) (? dy2)")\n"
;                 ;         "3 (" (? dx3) (? dy3)")\n"
;                 ;         "4 (" (? dx4) (? dy4)")\n"))
;
;
;                 )
;
;
;          :add (
;                 (on (?dx4 ?dy4) (forklift ?n)) ;Not added? implies the operator is never triggered
;                 (unavailable (?ox ?oy))
;                 (goal (?gx ?gy) (forklift ?n)) ;I have no idea why this is removed
;
;
;                 ;(on (7 0) (forklift 201))
;                ;(adjacent (?forklift ?n) not-set)
;                 )
;          :del ((on (?ox ?oy) (forklift ?n)))
;          :txt (forklift moves to (?dx4 ?dy4))
;          :cmd (move-forklift ?dx4 ?dy4)
;          }})
;    ;init {
;    ;      :pre ((?anything)) ;should be able to do things before operating (e.g. adding a goal in; but how? Can I get hold of the goal arg from ops-search.
;    ;      :add ((init-lock))
;    ;      :del (())
;    ;      :txt (())
;    ;      :cmd (())}


;(def test-pass-state
;  '#{(at (1 0) (forklift 156))
;     (isa forklift (forklift 156)) ;(isa forklift (forklift 495))
;     (at (0 0) none)  ;(at (7 0) none)
   ;  (:guard within-one-patch? '(?x) '(?y) '(?dx) '(?dy))
;     (connects (1 0) (0 0))
;     (at (0 1) none) (connects (0 0) (0 1))})
;
