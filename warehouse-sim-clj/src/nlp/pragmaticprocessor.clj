(ns nlp.pragmaticprocessor
  (:require [
             cgsx.tools.matcher :refer :all
             core :as world-state
             core :as dynamic-state]))


; taking the context of the world into account, and ultimately provide a set of instructions
;that are translatable into Clojure.
;could use globals such as atoms here, to keep track of things like "last box"

;Atoms are used here to store states for the nlp to use as a kind of history.
(def last-box (atom 0))
(def last-crate (atom 0))

(def specify-crate? [ctype]
  "Attempts to return which crate is being referred to given world context"
  (mfind [
          '(at (?object ?o)(forklift ?f))
          dynamic-state]))

