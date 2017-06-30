;Quick Copy
;(ns warehouse-sim-clj-v2.core)


(ns warehouse-sim-clj-v2.core
  (:gen-class)
  (:require))
            ;[clojure.set :refer :all]
            ;[ops-search.ops-search :refer :all])

    (defn main
      (def s25 (open-socket 2222))
      (println "Awaiting new world setup commands...")
      (def world (receive-state s25 '#{}))
      (println "Awaiting new state setup commands...")
      (def current-state (receive-state s25 '#{}))
      (def initial-state (union world current-state)))

