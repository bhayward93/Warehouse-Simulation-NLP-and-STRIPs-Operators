(ns core
  (:gen-class)(:require [sock2.socket :refer :all]))
;(load-file "src/sock2/socket_utils.clj")
  ;(:gen-class
  ;  :require [sock2.socket :refer :all]))


    (defn -main
      [& args]
      (def s25 (open-socket 2222))
      (println "awaiting new world setup commands...")
    ;;  (def world (receive-state s25 '#{}))
      (println "awaiting new state setup commands...")
   ;;   (def current-state (receive-state s25 '#{}))
   ;;   (def initial-state (union world current-state))
   )

(defn hello-world []
  println ("hello, world!"))
