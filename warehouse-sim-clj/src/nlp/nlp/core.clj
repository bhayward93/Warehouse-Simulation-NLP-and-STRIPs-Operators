(ns nlp.#core
  (:gen-class)
  (:require
    [nlp.syntactical-analyser :refer :all]
    [nlp.lexicon :refer :all]
    [sock2.socket :refer :all]
    [sock2.socket_utils :refer :all]
    [core :as s25]))

(defn in? ;https://stackoverflow.com/questions/3249334/test-whether-a-list-contains-a-specific-value-in-clojure
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

(defn goal-creator [cmds]
  (println "Attempting to create a goal state, with cmds :" cmds
    (let[_1 (ffirst cmds)
         _2 (first(rest (first cmds)))
         _3 (first (rest (rest (first cmds))))]
      (println "CMDS Split: 1: " _1" 2:"_2" 3:"_3)
      (cond (and(= _1 'move)(= (in? all-collectables _3)))
            (sock2.socket/socket-write s25 (str "set-forklift-destination one-of-forklifts "_3))
            (and(= _1 'pickup)= (in? all-collectables _3))
            (sock2.socket/socket-write s25 (str "pickup-collectable one-of forklifts "_3))))))




(defn trigger-nlp [_str]
  (let [cmds (syn-analyser _str)]
    (if (< (count cmds) 1)
      (map goal-creator cmds)
      (goal-creator cmds))))

