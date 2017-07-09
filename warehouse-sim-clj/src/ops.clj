(ns ops)
;on minor goal, e.g. traveling to a destination succesfuly, remove unavailable status.
;split into states - :world (unchangable) and another

;(ops-search mock-dyn '((at '(1 1) forklift 497)) state-ops :world mock-world :debug true)
;key ?forklift was ?w


;USE AN OR GUARD TO HALF THE CONNECTS! X Y DX DY OR DX DY X Y

(def state-ops
  '{:move
    {
     :pre  (
             (at '(?x ?y) ?forklift ?n)
             (:guard (or
                       '(connects '(?x ?y) '(?dx ?dy)))
                       '(connects '(?dx ?dy) '(?x ?y)))
             (:not (at '(?dx ?dy) ?anything))
             ;(:not (unavailable (?dx, ?dy))) ;Use a guard to ensure that this is NOT true.
             ;(:guard (util/within-one-patch? (? x) (? y) (? dx)(? dy))


            ;   )
             )
     :del  ((at '(?x ?y) ?forklift ?n)
             (at '(?dx ?dy) ?none))

     :add  ((at '(?dx ?dy) ?forklift ?n)
             (unavailable '(?x  ?y)))
     :cmd  (print ?forklift "moved to ("dx" "dy") ")
     :txt  (?forklift ?n moved to '(?new-x ?new-y))
     }


    }
  )



























