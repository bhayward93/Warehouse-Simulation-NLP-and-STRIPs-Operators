(ns sock2.socket_utils
  (require [sock2.socket :refer :all]))

(defn open-socket
  ([] open-socket 2222)
  ([port] (startup-server port)))

(defn receive-state
  [socket state-list]
  (let [new-state (socket-read socket)]
    (println new-state)
    (if (= new-state -1)
      state-list
      (receive-state socket (conj state-list new-state))
      )
    )
  )
