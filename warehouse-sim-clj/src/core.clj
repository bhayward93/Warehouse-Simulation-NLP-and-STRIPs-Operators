;Intructions at present:

;run, load this file in the *ns* then enter in repl (-main) - should make this a config
;press setup in nl
;wait for the world state and dyn state to populate
;run (fwd-chain rule-set mock-world)

;(ops-search mock-dyn '((on (6 16) (forklift 495))) state-ops :debug true :world (apply-all-rules rule-set mock-world))


;Res
;https://jafingerhut.github.io/cheatsheet/grimoire/cheatsheet-tiptip-cdocs-summary.html
(ns core
  (:gen-class)
  (:require [sock2.socket_utils :refer :all]
            [ops_search.ops_search :refer :all]
            [ops :refer :all]
            [cgsx.tools.matcher :refer :all]
            [clojure.string :as str]
            [nl-injector :refer :all]
            [util :refer :all]
            [rules :refer :all]
            )
=          ;  (:refer clojure.math.combinatorics :rename {update, combin-update})
            )
;(ops-search mock-dyn '((on (6 16) (forklift 495))) state-ops :debug true :world (apply-all-rules rule-set mock-world))
(defn -main
  ([] (-main 2222)) ;Set a default value
  ([port]
   (def s25 (open-socket port))
   (println "Awaiting Dynamic State...")
   (def dynamic-state (sock2.socket/socket-read s25))
   (println "Awaiting World State...")
   (def world-state (sock2.socket/socket-read s25)))
  )

;(defn run
;  (ops-search
;    mock-dyn
;    '((on (7 0) (forklift 201)))
;    state-
;    :debug true
;    :world (apply-all-rules rule-set mock-world)
;    ))

