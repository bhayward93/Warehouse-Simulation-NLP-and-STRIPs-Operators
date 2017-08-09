(ns util)



(defn in? ;https://stackoverflow.com/questions/3249334/test-whether-a-list-contains-a-specific-value-in-clojure
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

(defmacro bench
  "Times the execution of forms, discarding their output and returning
  a long in nanoseconds.
  Academic Note: Source - Cedric Greevey @
  http://grokbase.com/t/gg/clojure/11cv6b1mdq/extracting-the-execution-time-of-a-function"
  ([& forms]
   `(let [start# (System/nanoTime)]
      ~@forms
      (- (System/nanoTime) start#))))


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

