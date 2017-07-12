(ns     util)


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


  (defn within-one-patch? [x y dx dy]
    (println "x:"x "y:"y "dx:"dx "dy: "dy )
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
