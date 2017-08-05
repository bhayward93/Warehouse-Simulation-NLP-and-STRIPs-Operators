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
            [rules :refer :all]))
            ;[nlp.lexicon :refer :all]
            ;[nlp.syntactical-analyser :refer :all]
            [nlp.#core :refer :all]


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


(defn read-loop []
  (let [x 0]
           (while (= x 0)
             (let [nl-read (sock2.socket/socket-read s25)]
             (when (not= (nl-read nil))
               (println "NetLogo => "nl-read "\nAnalysing Input...")

               (nlp.#core/trigger-nlp nl-read))

             (Thread/sleep 100)))))


(defn send-cmd [cmd]
  (sock2.socket/socket-write s25 cmd))

;(defn run
;  (ops-search
;    mock-dyn
;    '((on (7 0) (forklift 201)))
;    state-
;    :debug true
;    :world (apply-all-rules rule-set mock-world)
;    ))

