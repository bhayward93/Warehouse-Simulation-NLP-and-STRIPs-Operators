;Intructions at present:

;run, load this file in the *ns* then enter in repl (-main) - should make this a config
;press setup in nl
;wait for the world state and dyn state to populate
;run (fwd-chain rule-set mock-world)

;(ops-search mock-dyn '((on (6 16) (forklift 495))) state-ops :debug true :world (apply-all-rules rule-set mock-world))


;Res
;https://jafingeGrhut.github.io/cheatsheet/grimoire/cheatsheet-tiptip-cdocs-summary.html
(ns core
  (:gen-class)
  (:require
    [sock2.socket_utils :refer :all]
    [ops_search.ops_search :refer :all]
    [ops :refer :all]
    [cgsx.tools.matcher :refer :all]
    [clojure.string :as str]
    [nl-injector :refer :all]
    [util :refer :all]
    [rules :refer :all]
    [nlp.lexicon :refer :all]
    [nlp.syntactical-analyser :refer :all]
    ))
  ;))
  ;
  ;(ops-search mock-dyn '((on (6 16) (forklift 495))) state-ops :debug true :world (apply-all-rules rule-set mock-world))
  (defn -main
        ([] (-main 2222)) ;Set a default value
        ([port]
         (def s25 (open-socket port))
         (println "Awaiting Dynamic State...")
         (def dynamic-state (sock2.socket/socket-read s25))
         (println "Awaiting World State...")
         (def world-state (sock2.socket/socket-read s25))))
  ; (println "reading")

  (defn goal-creator [cmds]

        (println "Attempting to create a goal state, with cmds :" cmds
                 (let [_1 (ffirst cmds)
                       _2 (first (rest (first cmds)))
                       _3 (first (rest (rest (first cmds))))]
                   (println "CMDS Split: 1: " _1 " 2:" _2 " 3:" _3)
                   (cond (and (= _1 'move) (= (or(in? all-collectables _3)) (-> _3 noun?)))
                         (sock2.socket/socket-write s25  (str "set-forklift-destination one-of-forklifts " _3))
                         (and (= _1 'pickup) = (or(in? all-collectables _3)(-> _3 noun?)))
                         (sock2.socket/socket-write s25  (str "pickup-collectable one-of forklifts " _3))
                         ))))

(defn trigger-nlp [_str]
  (let [cmds (syn-analyser _str)]
    (if (< (count cmds) 1)
      (map goal-creator cmds)
      (goal-creator cmds)
      )))

(defn read-loop []
  (let [x 0]
    (while (= x 0)
      (let [nl-read (sock2.socket/socket-read s25)]
        (when (not= (nl-read nil))
          (println "NetLogo => " nl-read "\nAnalysing Input...")
          (trigger-nlp nl-read))

        (Thread/sleep 100)))))

(defn time-returner [goal]
    (with-out-str (time ))
  )

(defn timed-ops-search [runs]

  (println "----------------------------------------------------------------")
  (dotimes [i runs]
    ;  (def single-fl-pickup )
    (print "Test "i": single-fl-move => " (time-returner '((adjacent (bay 2) (forklift 1)))))
    )
  (println "\n")
  (dotimes [i runs]
    ;  (def single-fl-pickup )
    (print "Test "i": two   -fl-move => " (time-returner '((adjacent (bay 2) (forklift 1))(adjacent (bay 1) (forklift 2)))))
    )
  (println "----------------------------------------------------------------")
  (dotimes [i runs]
          ;  (def single-fl-pickup )
           (print "Test "i": single-fl-pickup => " (time-returner '((holds (forklift 2) (object 2)))))
            )
  (println "\n")
  ;Testing the time for two forklifts to get seperate objects
  (dotimes [i runs]
           (print "Test "i": two-fl-pickup    => " (time-returner '((holds (forklift 1) (object 1)) (holds (forklift 2) (object 2)))))
    )
  (println "----------------------------------------------------------------")
  (dotimes [i runs]
     (print "Test "i": one-fl-drop-off =>" (time-returner '((holds (loading-bay 5) (object 1)))))
    )
  (println "\n")
  (dotimes [i runs]
      (print "Test "i": two-fl-drop-off =>" (time-returner '((holds (loading-bay 5) (object 1)) (holds (loading-bay 5) (object 2)))))
      )
  (println "\n")
  (dotimes [i runs]
      (print "Test "i": two-fl-4-drop-off =>"   (time-returner '((holds (loading-bay 5) (object 1)) (holds (loading-bay 5) (object 2))(holds (loading-bay 5) (object 3)) (holds (loading-bay 5) (object 4)))))
      )
  (println "----------------------------------------------------------------")
  ;Testing time for two forklifts to take two different objects to the loading bay
  ;(def two-fl-dropoff ( '((holds (loading-bay 5) (object 1)) (holds (loading-bay 5) (object 2)))))
  )




;(defn send-cmd [cmd]
  ;      (sock2.socket/socket-write s25 cmd)
  ;  G)

;(defn run
;  (ops-search
;    mock-dyn
;    '((on (7 0) (forklift 201)))
;    state-
;    :debug true
;    :world (apply-all-rules rule-set mock-world)
;    ))

