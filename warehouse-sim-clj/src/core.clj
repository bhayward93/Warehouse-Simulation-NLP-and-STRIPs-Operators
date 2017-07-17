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
            [clojure.math.combinatorics])
            (:refer clojure.math.combinatorics :rename {update, combin-update})
            )
;(ops-search mock-dyn '((on (6 16) (forklift 495))) state-ops :debug true :world (apply-all-rules rule-set mock-world))
(defn -main [port]
      (def s25 (open-socket port))
      (println "Awaiting World State")
      (def world-state (receive-state s25 '#{}))
      (println "Awaiting Dynamic State...")
      (def dynamic-state (receive-state s25 '#{}))
      (println world-state)
      (println "-------------------------")
      (println dynamic-state)
      )

(defn run []
  (ops-search
    mock-dyn
    '((on (5 0) forklift ?))
    state-ops
    :debug true
    :world (apply-all-rules rule-set mock-world))
  )

;(defn run []
;  (ops-search
;    (conj mock-dyn (apply-all-rules rule-set mock-world))
;    '((on (5 0) forklift ?))
;    state-ops
;    :debug true
;    :world (apply-all-rules rule-set mock-world)
;    ;(ops-search mock-dyn '((on (6 16) (forklift 495)))
;    ;state-ops :debug true :world (apply-all-rules rule-set mock-world))
;    )
;  )
;Notes-
;13/7/17 current issues could be a case of

