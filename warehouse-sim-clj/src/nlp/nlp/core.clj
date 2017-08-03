(ns nlp.core
  (:gen-class)
  (:require
    [nlp.syntactical-analyser :refer :all]
    [nlp.lexicon :refer :all]))

(defn trigger-nlp [_str]
  (let [cmds (syn-analyser _str)]
  (if (< (count cmds) 1)
    (map cmds)
    )
  ))

(defn goal-creator [cmds]
  (when (> (count cmds) 1)
    (println "hello and world")
    (let[_1 (ffirst cmds)
         _2 (ffirst(rest cmds))
         _3 (ffirst(rest (rest cmds)))]

      (cond (and(= _1 'move)(= _3 'crate))
            (println "Send a command to Netlogo")
            )
      ))

  )