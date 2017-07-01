(ns ops)
;on minor goal, e.g. traveling to a destination succesfuly, remove unavailable status.

;key ?forklift was ?w
(def state-ops
  '{:move
    {:name move
     :pre  ((at '(?new-x ?new-y) ?none)
             (at '(?x ?y) ?forklift)
             (connects '(?x ?y) '(?new-x ?new-y))
             (unavailable '(?new-x, ?new-y)))
     :del  ((at '(?x ?y) ?forklift)
             (at '(?new-x ?new-y) ?none))

     :add  ((at '(?new-x ?new-y) ?forklift)
             (unavailable '(?x, ?y)))
     ;:cmd  ;(move ?w '(?dx ?dy))
     :txt  (?forklift moved to '(?new-x ?new-y))
     }

    :move-back
    {:name move-back}

    }
  )

(def test-goal-1
  '(at '(10 16) forklift)
  )

(def test-goal-2
  '(at '(10 16) forklift)
  )