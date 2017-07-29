(ns ops
  (:gen-class)
  (:require
            [util :refer :all]
            ))

;(ops-search mock-dyn '((on (forklift 324) pistons-crate 312))
;state-ops
;:debug true :world mock-world)

(def state-ops
  '{

    move-to-obj{
                :pre
                    (
                      ;   (isa bay (?bx ?by))
                      ;   (at bay (?bx ?by) (shelf ?s))
                      (goal (?arg1 ?arg2) ?obj ?o)
                      (isa forklift (forklift ?f))
                      )
                :add((at (?obj ?o)(forklift ?f)))
                :del()
                :txt(forklift ?f moves to ?obj ?o)
                :cmd(sock2.socket/socket-write s25
                                        (str '(set-forklift-destination forklift ?f collectable ?o)));Hard coded the socket here)
                }

    pick-up {
             :pre
                 (
                   (at (?object ?o)(forklift ?f))
                   (stored-on (shelf ?s) ?object ?o)
                   (at bay (?bx ?by) (shelf ?s))
                   ;(is-height ?h (shelf ?s)) ;(is-height 0 (shelf 116))
                   ;(is-height ?h (forklift ?f))
                   )
             :add((holds (forklift ?f) ?object ?o))
             :del((stored-on (shelf ?s) ?object ?o))
             :txt(forklift ?f picked up ?object ?o from shelf ?s)
             :cmd(sock2.socket/socket-write s25 (str '(pickup-collectable forklift ?f collectable ?o)))
             }

    drop-off{
             :pre (
                    (isa forklift (forklift ?f))
                    (holds (forklift ?f) ?object ?o)
                    )
             :add ((stored-on (loading bay) ?object ?o)
                    )
             :del ((holds (forklift ?f) ?object ?o))
             :txt (forklift ?f dropped off ?object ?o at the loading bay)
             :cmd (sock2.socket/socket-write s25 (str '(drop-off collectable ?o)))
             }
    ;move-arm{
    ;         :pre(
    ;               (is-height ?dy (shelf ?s)) ;(is-height 0 (shelf 116))
    ;               (is-height ?y (forklift ?f))
    ;
    ;               )
    ;         :add ((is-height ?dy (forklift ?f))(moved-arm))
    ;         :del ((is-height ?y (forklift ?f)))
    ;         :txt(forklift ?f moved it's arm to elevation ?dy)
    ;         :cmd(sock2.socket/socket-write (str "move-arm forklift " (? f) " " (? dy)))
    ;         }
    ;
    ;
    ;move-freely { ;cannot freely move around
    ;             :pre ((isa forklift (forklift ?n))
    ;                    (on (?ox ?oy) (forklift ?n))
    ;                    (goal (?gx ?gy) (forklift ?n))
    ;
    ;                    )
    ;             :add (
    ;                    (goal (forklift ?n) pistons-crate)
    ;                    ;(at (bay ?b) (forklift ?n))
    ;                    (goal (?gx ?gy) (forklift ?n))
    ;                    (on (11 11) (forklift 425))
    ;
    ;
    ;                    )
    ;             :del ( (on (?ox ?oy) (forklift ?n)))
    ;             :txt (forklift moves to ?gx ?gy )
    ;             :cmd (sock2.socket/socket-write
    ;                    (str "move-forklift " (? gx) (? gy)));Hard coded the socket here
    ;             }
    })


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
