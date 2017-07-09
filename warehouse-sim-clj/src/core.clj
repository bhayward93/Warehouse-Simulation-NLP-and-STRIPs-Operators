;intructions at present
;TODO: Check out sokoban project more, see how the world state comes in and is stored. JUST LOAD IT.



;run, load this file in the *ns* then enter in repl (-main) - should make this a config
;press setup in nl

(ns core
  (:gen-class)
  (:require [sock2.socket_utils :refer :all]
            [ops_search.ops_search :refer :all]
            [ops :refer :all]
            [cgsx.tools.matcher :refer :all]
            [clojure.string :as str]
            [nl-injector :refer :all]
            [util :refer :all]
            )) ;remove. testing purposes (nl-injector)
; ([sock2.socket :refer :all]))

(defn -main [port]
      (def s25 (open-socket port))
      (println "Awaiting World State")
      (def world-state (receive-state s25 '#{}))

      (println "Awaiting Dynamic State...")
      (def dynamic-state (receive-state s25 '#{}))
      (print world-state)
      (println "-------------------------")
      (print dynamic-state)
      )
;(ops-search nl-injector/mock-dyn '(at '(1 1) forklift) ops/state-ops :world nl-injector/mock-world)

(defn hello-world []
  println ("hello, world!"))


;Sample
;(clojure.string/replace "[[at (quote (11 9)) (agentset 0 turtles)] [at (quote (1 8)) (agentset 0 turtles)]]" #"^\(quote/s" "'")