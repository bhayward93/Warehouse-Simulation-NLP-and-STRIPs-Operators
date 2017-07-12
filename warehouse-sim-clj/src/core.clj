;Intructions at present:

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
            ))

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