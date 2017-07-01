;intructions at present

;run, load this file in the *ns* then enter in repl (-main) - should make this a config
;press setup in nl

(ns core
  (:gen-class)
  (:require [sock2.socket_utils :refer :all]
            [ops_search.ops_search :refer :all]
            [ops :refer :all]
            [cgsx.tools.matcher :refer :all]))
; ([sock2.socket :refer :all]))

    (defn -main
      [& args]
      (def s25 (open-socket 2222))
      (println "awaiting new world setup commands...")
      (def world-state (receive-state s25 '#{}))
      (print world-state)
    ;nnm  (println "awaiting new state setup commands...")
    ;  (  def current-state (receive-state s25 '#{}))
    ;  (def initial-state (union world current-state))
    )

(defn hello-world []
  println ("hello, world!"))

;DESIGN THE OPS NEXT THEN STRUCTURE NL OUTPUT. < NOTE TO SELF