;intructions at present

;run, load this file in the *ns* then enter in repl (-main) - should make this a config
;press setup in nl

(ns core
  (:gen-class)
  (:require [sock2.socket_utils :refer :all]
            [ops_search.ops_search :refer :all]
            [ops :refer :all]
            [cgsx.tools.matcher :refer :all]
            [clojure.string :as str]))
; ([sock2.socket :refer :all]))
    (defn -main [port]
      (def s25 (open-socket port))
      (def world-state
                          (clojure.string/replace
                          (receive-state s25 '#{})
                          (#"\(quote ")
                          ("'"))
                         )                  ;starts with literal paren + quote + space
      (print world-state)
      )


(defn hello-world []
  println ("hello, world!"))


;Sample

;(clojure.string/replace "[[at (quote (11 9)) (agentset 0 turtles)] [at (quote (1 8)) (agentset 0 turtles)]]" #"^\(quote/s" "'")


;(def mock-input
;[(at (quote (2 12)) pistons-crate) (at (quote (2 12)) circular-saws-crate) (at (quote (2 12)) engines-crate) (at (quote (2 12)) nails-crate) (at (quote (2 12)) bolts-crate) (at (quote (2 12)) screws-crate) (at (quote (2 12)) lights-crate) (at (quote (2 12)) compressors-crate) (at (quote (2 12)) wheels-crate) (at (quote (2 12)) tyres-crate) (at (quote (2 12)) girders-crate) (at (quote (2 12)) wooden-planks-crate) (at (quote (2 12)) grates-crate) (at (quote (2