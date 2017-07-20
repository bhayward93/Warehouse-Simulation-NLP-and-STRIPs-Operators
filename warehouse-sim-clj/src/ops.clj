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

                 (isa forklift (?forklift ?n))
                 (on (?ox ?oy) (?forklift ?n))
                 (connects (?ox ?oy) (?dx1 ?dy1))
                 (connects (?ox ?oy) (?dx2 ?dy2))
                 (connects (?ox ?oy) (?dx3 ?dy3))
                 (connects (?ox ?oy) (?dx4 ?dy4))

                  (:guard
                    (or(not= (? dx1) (? dx2))
                       (not= (? dy1) (? dy2)))
                    (or(not= (? dx2) (? dx3))
                       (not= (? dy2) (? dy3)))
                    (or(not= (? dx3) (? dx4))
                       (not= (? dy3) (? dy4)))
                    (or(not= (? dx1) (? dx4))
                       (not= (? dy1) (? dy4)))
                    (or(not= (? dx1) (? dx3))
                       (not= (? dy1) (? dy3)))
                    (or(not= (? dx2) (? dx4))
                       (not= (? dy2) (? dy4)))

                    )
                 ;(is floor (?dx ?dy))

                           ;(:not (unavailable (?dx ?dy)))
                           (:guard (println
                                     "------------------------------\n"
                                     "ox:   (" (? ox) " " (? oy) ")\n"
                                     "dxy1: (" (? dx1) " " (? dy1) ")\n"
                                     "dxy2: (" (? dx2) " " (? dy2) ")\n"
                                     "dxy3: (" (? dx3) " " (? dy3) ")\n"
                                     "dxy4: (" (? dx4) " " (? dy4) ")\n"
                                     "------------------------------"
                                     ))


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

                           ;(:guard  (do (println "HELLO FROM THE GUARD INSIDE")(and ;this println works! TODO Start here tomorrow.
                           ;           (or
                           ;             (= (+ (? dx) 1) (? dx)) ;this should be native clojure; need to check the variables using the above.
                           ;             (= (- (? dx) 1) (? dx)) ;Also try EAP builds for cursive to see if the debugger works!
                           ;             (= (? dx)(? dx)))
                           ;           (or
                           ;             (= (? dy) (? dy))
                           ;             (= (? dy) (? dy))
                           ;             (= (? dy)(? dy))))))
                           ; (:guard (do (println (+(? dx) 1)))(= (+ (? dy) 1) (? dy)))   ;can do addition here!!!

                           ;(:guard ())
                           ;possibly protect?
                           (goal (?gx ?gy) (?forklift ?n)) ;Got hold of the goal!
                           ;(:guard (>= (- (gx ?) (dx ?)))


                           (:guard (println "GX :" (? gx) "  |  GY: " (? gy)))

                           ; (:guard (do (println "X: "(? x) "  | Y: " (? y)"  |  DX: "(? dx) "  | DY: " (? dy))))

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
          :txt (forklift moves to (?dx ?dy))
          :cmd (+ 1 1)
          }})
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
