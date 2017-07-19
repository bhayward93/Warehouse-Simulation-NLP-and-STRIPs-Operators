(ns util)


;x:  quote
;y:  (4 10)
;dx: quote
;dy:  (6 2)

;There isn't a need to send the connections from NL, this is incredibly wasteful.

;16
;(defn connections-generator []
;   ;Can't I set a default value for this arg
;  (let [coll '#{}]
;    (loop [x 16]
;     (
;       (print x)
;         (loop [y 0]

;           (when not= y 16
;          (print "'(links x y)'(links x y)'(links x y)'(links x y)'(links x y)
;                     '(links x y)'(links x y)'(links x y)'(links x y)'(links x y)"))
;
;       (if (not= x 16 )(recur (inc x)))))
;     )
;    ))


;unused
 ;(use 'clojure.stacktrace)
 ; (print-stack-trace *e 10)

  (defn within-one-patch? [x y dx dy]
    (let [this-x  (int  x)
          this-y  (int  y)
          this-dx (int dx)
          this-dy (int dy)]
      (and
        (or
          (= (+ this-x 1) this-dx)
          (= (- this-x 1) this-dx)
          (= this-dx this-x))
        (or
          (= (+ this-y 1) this-dy)
          (= (- this-y 1) this-dy)
          (= this-dy this-y)))
      ))

;A defined use of clojure.string/replace, specifically for
; quickly uncommenting large blocks of code, and printing the
; output to console. This is useful because intellij can
; sometimes get confused when trying to use ctrl+/ , and comments a second time.

(defn uncomment-string [base-string]
  (println (clojure.string/replace
              base-string   ";"    ""))
  )

;TEMP DEVELOPMENT FUNCTION. this needs to be switched to work with the actual dynamic state when it is complete.
(defn add-goal-state [goal-state mock]
  (conj mock goal-state)
  )

;https://stackoverflow.com/questions/5057047/how-to-do-exponentiation-in-clojure
(defn exp
  [x n]
  (if (zero? n) 1
                (* x (exp x (dec n)))
                ))

(defn distance?
  [x y dx dy]
  (
      (Math/sqrt (+ (exp (- dy x) 2) (exp (- dy y) 2)))
      )
  )

;https://gist.github.com/edbond/665401
(defmacro xor
  "Evaluates exprs one at a time, from left to right.  If only one form returns
  a logical true value (neither nil nor false), returns true.  If more than one
  value returns logical true or no value returns logical true, retuns a logical
  false value.  As soon as two logically true forms are encountered, no
  remaining expression is evaluated.  (xor) returns nil."
  ([] nil)
  ([f & r]
   `(loop [t# false f# '[~f ~@r]]
      (if-not (seq f#) t#
                       (let [fv# (eval (first f#))]
                         (cond
                           (and t# fv#) false
                           (and (not t#) fv#) (recur true (rest f#))
                           :else (recur t# (rest f#))))))))


;https://stackoverflow.com/questions/15003103/clojure-form-to-string
(defmacro string-it [x] (str x))


;testfn
(def testintersect '#{(connects (4 16) (3 16))
                     (connects (16 7) (0 7))
                     (connects (2 11) (3 11))
                     (connects (10 9) (11 9))
                     (connects (0 0) (0 1))
                     (connects (16 9) (15 9))
                     (at (4 13) grates-crate 192)
                     (connects (0 6) (1 6))
                     (connects (14 8) (13 8))
                     (connects (12 4) (12 3))
                     (connects (8 11) (7 11))
                     (connects (12 8) (12 7))
                     (connects (8 2) (9 2))
                     (connects (14 7) (13 7))
                     (connects (8 14) (7 14))
                     (connects (12 6) (12 7))
                     (connects (6 6) (6 7))
                     (connects (14 14) (14 15))
                     (connects (12 9) (13 9))
                     (connects (14 12) (14 11))
                     (connects (4 12) (5 12))
                     (connects (14 0) (14 16))
                     (adjacent (forklift 201) (7 0))
                     (connects (16 3) (0 3))
                     (connects (2 12) (3 12))
                     (connects (16 6) (0 6))
                     (connects (10 2) (10 1))
                     (connects (12 1) (13 1))
                     (connects (10 8) (11 8))
                     (connects (16 16) (16 0))
                     (connects (0 6) (0 5))
                     (connects (4 12) (3 12))
                     (connects (14 16) (13 16))
                     (connects (14 0) (14 1))
                     (connects (10 5) (9 5))
                     (connects (2 10) (2 9))
                     (connects (12 2) (12 3))
                     (connects (12 13) (11 13))
                     (at (4 13) wheels-crate 199)
                     (connects (12 6) (12 5))
                     (connects (16 4) (16 3))
                     (connects (8 14) (9 14))
                     (connects (6 16) (5 16))
                     (at (12 12) tyres-crate 183)
                     (adjacent (forklift 201) (6 1))
                     (connects (12 12) (13 12))
                     (connects (12 5) (13 5))
                     (connects (0 9) (16 9))
                     (connects (2 5) (1 5))
                     (connects (6 12) (5 12))
                     (connects (0 12) (1 12))
                     (connects (0 5) (16 5))
                     (connects (16 10) (0 10))
                     (connects (12 6) (11 6))
                     (connects (6 14) (7 14))
                     (connects (4 16) (4 0))
                     (connects (0 16) (0 0))
                     (connects (10 1) (9 1))
                     (connects (16 0) (16 16))
                     (connects (4 9) (3 9))
                     (at (12 10) girders-crate 184)
                     (connects (14 15) (15 15))
                     (connects (2 0) (3 0))
                     (connects (14 16) (14 15))
                     (at (12 13) circular-saws-crate 197)
                     (connects (16 0) (0 0))
                     (connects (2 12) (2 13))
                     (connects (4 11) (3 11))
                     (connects (12 6) (13 6))
                     (connects (12 4) (12 5))
                     (connects (4 14) (5 14))
                     (connects (16 1) (0 1))
                     (connects (0 16) (0 15))
                     (connects (6 15) (5 15))
                     (connects (10 14) (10 13))
                     (connects (10 3) (9 3))
                     (connects (0 12) (16 12))
                     (connects (10 4) (11 4))
                     (connects (8 6) (8 5))
                     (connects (10 2) (9 2))
                     (connects (14 3) (15 3))
                     (connects (8 14) (8 13))
                     (at (8 2) engines-crate 189)
                     (connects (0 11) (1 11))
                     (connects (12 5) (11 5))
                     (connects (4 0) (3 0))
                     (connects (14 4) (15 4))
                     (connects (6 4) (5 4))
                     (connects (10 6) (10 7))
                     (connects (6 6) (6 5))
                     (connects (16 15) (15 15))
                     (connects (8 4) (8 5))
                     (connects (16 16) (15 16))
                     (connects (4 1) (3 1))
                     (connects (14 2) (15 2))
                     (connects (8 2) (8 3))
                     (connects (0 1) (16 1))
                     (connects (14 11) (15 11))
                     (connects (8 2) (7 2))
                     (connects (4 4) (3 4))
                     (connects (4 0) (4 16))
                     (connects (16 16) (16 15))
                     (connects (0 4) (0 3))
                     (connects (8 16) (7 16))
                     (connects (4 9) (5 9))
                     (connects (6 0) (5 0))
                     (connects (2 9) (1 9))
                     (connects (2 7) (3 7))
                     (connects (12 0) (13 0))
                     (connects (4 2) (5 2))
                     (connects (14 1) (15 1))
                     (connects (2 14) (2 15))
                     (connects (2 16) (2 0))
                     (connects (8 10) (7 10))
                     (connects (8 0) (8 1))
                     (at (12 6) nails-crate 190)
                     (connects (10 10) (10 11))
                     (connects (12 2) (11 2))
                     (connects (6 8) (5 8))
                     (connects (14 11) (13 11))
                     (connects (2 11) (1 11))
                     (connects (10 2) (11 2))
                     (connects (16 12) (0 12))
                     (connects (8 6) (8 7))
                     (connects (14 14) (15 14))
                     (connects (14 1) (13 1))
                     (connects (14 2) (14 1))
                     (connects (2 0) (1 0))
                     (connects (8 8) (9 8))
                     (connects (4 6) (4 7))
                     (connects (10 12) (10 13))
                     (connects (0 16) (16 16))
                     (connects (2 2) (2 1))
                     (at (12 10) jacks-crate 188)
                     (connects (0 14) (0 15))
                     (connects (12 13) (13 13))
                     (connects (8 0) (7 0))
                     (connects (8 13) (9 13))
                     (connects (0 10) (1 10))
                     (connects (8 0) (9 0))
                     (connects (12 8) (13 8))
                     (connects (4 12) (4 11))
                     (connects (2 0) (2 1))
                     (connects (16 11) (0 11))
                     (connects (8 5) (9 5))
                     (connects (4 16) (4 15))
                     (connects (8 9) (7 9))
                     (at (8 4) lights-crate 194)
                     (connects (14 13) (13 13))
                     (connects (0 2) (0 3))
                     (connects (2 3) (1 3))
                     (connects (14 7) (15 7))
                     (at (8 3) lights-crate 187)
                     (connects (8 3) (7 3))
                     (connects (12 0) (12 1))
                     (connects (0 0) (1 0))
                     (connects (14 15) (13 15))
                     (connects (14 4) (14 5))
                     (connects (2 6) (2 5))
                     (connects (10 11) (9 11))
                     (connects (0 6) (0 7))
                     (connects (12 4) (11 4))
                     (connects (0 16) (1 16))
                     (connects (8 13) (7 13))
                     (connects (8 6) (7 6))
                     (connects (10 12) (9 12))
                     (connects (2 16) (1 16))
                     (connects (0 10) (0 9))
                     (connects (10 5) (11 5))
                     (connects (10 16) (10 15))
                     (connects (12 7) (13 7))
                     (connects (12 2) (12 1))
                     (at (12 15) pistons-crate 198)
                     (connects (16 8) (15 8))
                     (connects (10 4) (10 5))
                     (connects (10 15) (9 15))
                     (connects (14 6) (15 6))
                     (connects (16 11) (15 11))
                     (connects (14 6) (13 6))
                     (at (12 1) lights-crate 186)
                     (connects (2 4) (3 4))
                     (connects (12 7) (11 7))
                     (connects (16 8) (16 9))
                     (connects (12 16) (12 15))
                     (adjacent (forklift 201) (6 16))
                     (connects (0 13) (1 13))
                     (connects (2 3) (3 3))
                     (connects (0 8) (1 8))
                     (connects (4 2) (4 3))
                     (connects (8 11) (9 11))
                     (connects (8 15) (7 15))
                     (connects (0 0) (0 16))
                     (connects (6 6) (7 6))
                     (connects (8 12) (8 11))
                     (connects (0 2) (16 2))
                     (connects (12 16) (11 16))
                     (connects (2 16) (2 15))
                     (connects (0 7) (1 7))
                     (connects (12 3) (11 3))
                     (connects (10 10) (9 10))
                     (connects (0 9) (1 9))
                     (connects (0 4) (1 4))
                     (connects (6 13) (5 13))
                     (connects (16 12) (16 13))
                     (connects (10 3) (11 3))
                     (connects (6 0) (6 1))
                     (connects (10 8) (9 8))
                     (connects (12 14) (11 14))
                     (connects (2 2) (2 3))
                     (connects (2 4) (2 5))
                     (connects (4 7) (3 7))
                     (connects (14 4) (14 3))
                     (connects (16 14) (15 14))
                     (connects (14 16) (14 0))
                     (connects (12 10) (12 9))
                     (connects (10 12) (10 11))
                     (connects (14 2) (13 2))
                     (connects (2 15) (1 15))
                     (connects (8 15) (9 15))
                     (connects (6 10) (6 9))
                     (connects (8 6) (9 6))
                     (connects (6 2) (6 1))
                     (connects (14 3) (13 3))
                     (connects (16 13) (15 13))
                     (connects (10 4) (10 3))
                     (connects (12 8) (12 9))
                     (connects (10 11) (11 11))
                     (connects (0 15) (1 15))
                     (connects (6 11) (7 11))
                     (connects (2 2) (1 2))
                     (connects (2 14) (3 14))
                     (connects (4 2) (3 2))
                     (connects (6 10) (6 11))
                     (connects (10 13) (11 13))
                     (connects (8 4) (7 4))
                     (connects (8 1) (9 1))
                     (connects (0 6) (16 6))
                     (connects (2 2) (3 2))
                     (connects (6 5) (7 5))
                     (connects (14 12) (13 12))
                     (connects (14 9) (13 9))
                     (connects (6 8) (7 8))
                     (connects (14 0) (13 0))
                     (connects (4 15) (3 15))
                     (connects (12 11) (13 11))
                     (connects (6 1) (7 1))
                     (connects (4 5) (3 5))
                     (connects (8 8) (8 9))
                     (connects (0 4) (16 4))
                     (connects (6 12) (6 13))
                     (connects (4 12) (4 13))
                     (connects (0 14) (1 14))
                     (connects (0 11) (16 11))
                     (connects (2 9) (3 9))
                     (connects (0 8) (0 9))
                     (connects (14 8) (15 8))
                     (connects (14 16) (15 16))
                     (connects (16 7) (15 7))
                     (connects (8 7) (7 7))
                     (connects (0 13) (16 13))
                     (connects (4 3) (5 3))
                     (connects (16 16) (0 16))
                     (connects (4 5) (5 5))
                     (connects (6 9) (5 9))
                     (connects (4 1) (5 1))
                     (connects (8 12) (9 12))
                     (connects (12 15) (13 15))
                     (connects (4 8) (4 9))
                     (connects (4 14) (4 15))
                     (connects (16 0) (16 1))
                     (connects (2 6) (1 6))
                     (connects (6 13) (7 13))
                     (connects (10 16) (11 16))
                     (connects (14 2) (14 3))
                     (connects (2 16) (3 16))
                     (connects (0 14) (16 14))
                     (connects (8 2) (8 1))
                     (connects (10 0) (9 0))
                     (connects (12 14) (12 15))
                     (connects (6 12) (6 11))
                     (connects (12 8) (11 8))
                     (at (12 3) girders-crate 195)
                     (connects (2 8) (3 8))
                     (connects (0 5) (1 5))
                     (connects (2 0) (2 16))
                     (connects (6 5) (5 5))
                     (connects (8 10) (9 10))
                     (connects (6 11) (5 11))
                     (connects (10 12) (11 12))
                     (connects (16 12) (15 12))
                     (connects (4 10) (3 10))
                     (at (4 13) circular-saws-crate 180)
                     (connects (16 9) (0 9))
                     (connects (4 15) (5 15))
                     (connects (4 6) (4 5))
                     (connects (10 4) (9 4))
                     (connects (4 0) (5 0))
                     (connects (10 0) (10 16))
                     (connects (4 11) (5 11))
                     (connects (16 2) (16 3))
                     (connects (12 11) (11 11))
                     (connects (10 8) (10 9))
                     (connects (6 10) (5 10))
                     (connects (10 14) (10 15))
                     (connects (10 2) (10 3))
                     (connects (2 7) (1 7))
                     (connects (4 8) (4 7))
                     (connects (6 8) (6 7))
                     (connects (16 6) (16 7))
                     (connects (2 10) (3 10))
                     (connects (16 15) (0 15))
                     (connects (16 14) (16 13))
                     (connects (6 3) (5 3))
                     (connects (6 4) (6 3))
                     (connects (14 8) (14 7))
                     (connects (4 13) (5 13))
                     (at (12 14) jacks-crate 200)
                     (connects (0 1) (1 1))
                     (connects (12 10) (11 10))
                     (adjacent (forklift 201) (5 0))
                     (connects (6 15) (7 15))
                     (connects (14 12) (14 13))
                     (connects (14 6) (14 7))
                     (connects (12 15) (11 15))
                     (connects (16 14) (0 14))
                     (connects (2 13) (1 13))
                     (connects (14 8) (14 9))
                     (connects (6 14) (6 13))
                     (connects (6 3) (7 3))
                     (connects (2 1) (1 1))
                     (connects (4 3) (3 3))
                     (at (8 13) bolts-crate 185)
                     (connects (2 5) (3 5))
                     (connects (4 6) (3 6))
                     (connects (0 4) (0 5))
                     (connects (4 14) (3 14))
                     (at (12 5) wheels-crate 191)
                     (connects (16 2) (0 2))
                     (connects (0 0) (16 0))
                     (connects (16 2) (16 1))
                     (connects (8 16) (8 15))
                     (at (12 1) wooden-planks-crate 196)
                     (connects (6 2) (7 2))
                     (connects (2 1) (3 1))
                     (connects (8 5) (7 5))
                     (connects (8 7) (9 7))
                     (connects (0 12) (0 13))
                     (connects (6 2) (6 3))
                     (connects (16 13) (0 13))
                     (connects (8 8) (7 8))
                     (connects (4 4) (5 4))
                     (connects (4 14) (4 13))
                     (connects (0 3) (1 3))
                     (connects (10 14) (9 14))
                     (connects (14 10) (14 11))
                     (connects (2 10) (1 10))
                     (connects (10 14) (11 14))
                     (connects (10 10) (10 9))
                     (connects (8 4) (9 4))
                     (connects (6 10) (7 10))
                     (connects (4 10) (5 10))
                     (connects (6 7) (5 7))
                     (connects (0 10) (16 10))
                     (connects (2 4) (2 3))
                     (connects (4 6) (5 6))
                     (connects (16 4) (0 4))
                     (connects (8 4) (8 3))
                     (connects (10 6) (11 6))
                     (connects (16 8) (16 7))
                     (connects (12 10) (13 10))
                     (connects (10 0) (11 0))
                     (connects (6 16) (7 16))
                     (connects (4 10) (4 9))
                     (connects (12 14) (13 14))
                     (connects (10 6) (10 5))
                     (connects (12 0) (12 16))
                     (connects (12 3) (13 3))
                     (connects (16 2) (15 2))
                     (connects (6 2) (5 2))
                     (connects (12 4) (13 4))
                     (connects (14 10) (14 9))
                     (at (4 11) bolts-crate 182)
                     (connects (8 9) (9 9))
                     (connects (14 9) (15 9))
                     (connects (6 6) (5 6))
                     (connects (0 12) (0 11))
                     (connects (0 2) (1 2))
                     (connects (16 14) (16 15))
                     (connects (6 4) (7 4))
                     (connects (2 8) (2 9))
                     (connects (8 10) (8 9))
                     (connects (8 16) (8 0))
                     (connects (10 16) (10 0))
                     (connects (12 12) (11 12))
                     (connects (10 13) (9 13))
                     (connects (12 12) (12 11))
                     (connects (0 8) (16 8))
                     (connects (16 3) (15 3))
                     (connects (12 12) (12 13))
                     (connects (12 1) (11 1))
                     (connects (4 7) (5 7))
                     (connects (0 8) (0 7))
                     (at (4 10) grates-crate 181)
                     (connects (12 10) (12 11))
                     (connects (0 14) (0 13))
                     (connects (10 9) (9 9))
                     (connects (16 6) (16 5))
                     (connects (4 0) (4 1))
                     (connects (2 12) (1 12))
                     (connects (2 6) (3 6))
                     (connects (10 6) (9 6))
                     (connects (10 16) (9 16))
                     (connects (14 14) (14 13))
                     (connects (6 7) (7 7))
                     (connects (16 10) (16 11))
                     (on (6 0) (forklift 201))
                     (connects (6 12) (7 12))
                     (connects (0 3) (16 3))
                     (connects (10 1) (11 1))
                     (connects (6 9) (7 9))
                     (connects (2 6) (2 7))
                     (connects (16 12) (16 11))
                     (connects (14 14) (13 14))
                     (connects (0 7) (16 7))
                     (connects (8 16) (9 16))
                     (connects (6 0) (6 16))
                     (connects (6 16) (6 15))
                     (connects (8 10) (8 11))
                     (connects (12 0) (11 0))
                     (connects (4 13) (3 13))
                     (connects (10 0) (10 1))
                     (connects (2 15) (3 15))
                     (connects (16 10) (16 9))
                     (connects (16 8) (0 8))
                     (connects (12 16) (12 0))
                     (connects (14 10) (15 10))
                     (connects (4 2) (4 1))
                     (connects (6 16) (6 0))
                     (connects (8 0) (8 16))
                     (connects (8 14) (8 15))
                     (issorted ?gx ?dy)
                     (connects (16 1) (15 1))
                     (connects (0 10) (0 11))
                     (connects (10 15) (11 15))
                     (connects (16 4) (16 5))
                     (connects (10 10) (11 10))
                     (connects (8 8) (8 7))
                     (connects (10 7) (11 7))
                     (connects (4 4) (4 5))
                     (connects (6 1) (5 1))
                     (connects (6 8) (6 9))
                     (connects (2 8) (2 7))
                     (connects (14 4) (13 4))
                     (connects (10 7) (9 7))
                     (connects (12 16) (13 16))
                     (connects (16 5) (15 5))
                     (connects (16 10) (15 10))
                     (connects (8 1) (7 1))
                     (connects (14 6) (14 5))
                     (connects (2 14) (1 14))
                     (connects (8 12) (8 13))
                     (connects (12 9) (11 9))
                     (connects (6 0) (7 0))
                     (connects (16 6) (15 6))
                     (connects (2 12) (2 11))
                     (connects (2 4) (1 4))
                     (connects (14 10) (13 10))
                     (connects (10 8) (10 7))
                     (connects (16 4) (15 4))
                     (connects (12 2) (13 2))
                     (connects (0 2) (0 1))
                     (connects (4 10) (4 11))
                     (connects (14 12) (15 12))
                     (connects (6 4) (6 5))
                     (connects (0 15) (16 15))
                     (isa forklift (forklift 201))
                     (connects (16 0) (15 0))
                     (connects (4 16) (5 16))
                     (connects (2 14) (2 13))
                     (connects (8 12) (7 12))
                     (connects (14 13) (15 13))
                     (connects (4 8) (5 8))
                     (connects (16 5) (0 5))
                     (connects (4 4) (4 3))
                     (connects (14 0) (15 0))
                     (connects (14 5) (15 5))
                     (connects (2 10) (2 11))
                     (connects (2 8) (1 8))
                     (connects (6 14) (6 15))
                     (connects (2 13) (3 13))
                     (connects (6 14) (5 14))
                     (connects (12 14) (12 13))
                     (connects (14 5) (13 5))
                     (connects (4 8) (3 8))
                     (connects (8 3) (9 3))
                     (at (12 3) wheels-crate 193)}
  )