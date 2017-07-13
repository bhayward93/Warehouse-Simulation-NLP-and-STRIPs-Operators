(require '[cgsx.tools.matcher :refer :all])
(require '[clojure.set :refer :all])
(require '[clojure.pprint :refer :all])

(declare finalise-results update-state-map apply-op apply-all)


(defn ops-search
  [start goal ops & {:keys [world debug]
                     :or {debug false
                          world #{}}}]
  ; using sets for state tuples...
  (let [start {:state (set start) :path () :cmds () :txt ()}
        world (set world)
        goal? (fn [state] (mfind* [goal (into world (:state state))] state))
        ]
    (or (goal? start)
      (finalise-results
        (loop [waiting (list start)
               visited #{}
               ]
          (when debug (pprint (list 'waiting= waiting 'visited= visited)))
          (if (empty? waiting) nil
            (let [[next & waiting] waiting
                  {:keys [state path cmds]} next
                  visited? (partial contains? visited)
                  ]
              (when debug (pprint (list 'next-state= state)))
              (if (visited? state)
                (recur waiting visited)
                (let [succs (remove visited? (apply-all ops state world))
                      g (some goal? succs)
                      ]
                  (if g (update-state-map next g)
                    (recur
                      (concat waiting (map #(update-state-map next %) succs))
                      (conj visited state)))
                  )))))
        ))))


(defn update-state-map
  [old successor]
  {:state (:state successor)
   :path (cons (:state old) (:path old))
   :cmds (cons (:cmd successor) (:cmds old))
   :txt (cons (:txt successor) (:txt old))
   })


(defn finalise-results [state-map]
  "reverses relevant parts of results"
  (if-not (map? state-map)
    ;; leave it as it is
    state-map
    ;; else
    {
      :txt (reverse (:txt state-map))
      }
    ))


(defn apply-op [op state world]
  (mfor* [(:pre op) (seq (into world state))]
    {:state (union (set (mout (:add op)))
              (difference state (set (mout (:del op)))))
     :cmd (mout (:cmd op))
     :txt (mout (:txt op))
     }
    ))


(defn apply-all [ops state world]
  (reduce concat
    (map (fn [x] (apply-op (x ops) state world))
      (keys ops)
      )))



;--GROUPS CODE--

;--INITIAL IDEAS--
;Version 1.0:
;The current version allows for an agent to climb platforms aslong as they are connected, the agent can drop from platforms, it can pickup objects and drop objects.

(def ops-main
  '{climb-on-platform {
                        :pre ((agent ?agent)
                               (at ?agent ?p1)
                               (connects ?p1 ?p2)
                               )
                        :add ((at ?agent ?p2))
                        :del ((at ?agent ?p1))
                        :txt (?agent climb-on to ?p2 from ?p1)
                        :cmd [climb-on ?p2]
                        }
    climb-on-obj {
                   :pre ((agent ?agent)
                          (at ?agent ?p1)
                          (on ?obj ?p1)
                          )
                   :add ((at ?agent ?obj))
                   :del ((at ?agent ?p1))
                   :txt (?agent climb-on to ?obj from ?p1)
                   :cmd [climb-on ?obj]
                   }

    climb-off-platform {
                         :pre ((agent ?agent)
                                (at ?agent ?p2)
                                (connects ?p1 ?p2)
                                )
                         :add ((at ?agent ?p1))
                         :del ((at ?agent ?p2))
                         :txt (?agent climb-off ?p2 to ?p1)
                         :cmd [climb-off ?p2]
                         }
    climb-off-obj {
                    :pre ((agent ?agent)
                           (at ?agent ?obj)
                           (on ?obj ?p1)
                           )
                    :add ((at ?agent ?p1))
                    :del ((at ?agent ?obj))
                    :txt (?agent climb-off ?obj to ?p1)
                    :cmd [climb-off ?obj]
                    }
    pick-off {
               :pre ((agent ?agent)
                      (manipulable ?obj)
                      (at ?agent ?place)
                      (on ?obj ?place)
                      (holds ?agent nil)
                      )
               :add ((holds ?agent ?obj))
               :del ((on ?obj ?place)
                      (holds ?agent nil))
               :txt (?agent pick-off ?obj from ?place)
               :cmd [pick-off ?obj]
               }
    drop-on {
              :pre ((at ?agent ?place)
                     (holds ?agent ?obj)
                     (:guard (? obj))
                     )
              :add ((holds ?agent nil)
                     (on ?obj ?place))
              :del ((holds ?agent ?obj))
              :txt (?agent drop-on ?obj at ?place)
              :cmd [drop-on ?obj]
              }

    })

(def sow-main
  '#{(at R platform4)
     (on box platform3)
     (on box2 platform2)
     (on desk platform1)
     (holds R nil)
     (connects platform1 platform2)
     (connects platform2 platform3)
     (connects platform3 platform4)
     (manipulable box)
     (manipulable box2)
     (manipulable desk)
     (agent R)
     })

;;(ops-search sow-main '((on box2 platform1) (on desk platform4) (at R platform1)) ops-main)
;;Simple operation of moving a desk object to platform4 and a box object onto platform1. Whilst having the agent end at platform1.

;--MODIFICATIONS--
;Version 1.1:
;In this version, we edited the operators to now take into consideration heights of platforms. The changes made allow for the agent to only be able to climb onto platforms
;if that platform is within reach. If it is not, the agent will attempt to see if using an obj will make the platform within reach, if that object does make the platform
;within reach, the agent will climb onto the object, and then onto the platform. If the platform is still not within reach, nil will be returned.
;Dropping onto platforms has also been changed. The agent can not drop onto a platform if the height is too high. However, a single object (not stacked) can be used to allow
;the agent to drop down. For example, if the max drop is 2 units high, and platform1 is 1 unit high and platform 2 is 4 units high and there is a box on platform1, the agent will
;be able to drop down onto that box, and then drop down onto the platform.


(def max-climb
  1)

(def max-drop
  2)


(defn reachable?
  ([h1 h2 n]
    (let [x (- h2 h1)]
      (if (and (>= x 0) (<= x n))
        true
        false)))
  ([h1 h2 obj n]
    (let [x (- h2 h1)]
      (if (and (>= x 0) (<= x (+ n 1)))
        true
        false)))
  )


(def ops-main2
  '{climb-on-platform {
                        :pre ((agent ?agent)
                               (at ?agent ?p1)
                               (connects ?p1 ?p2)
                               (height ?p1 ?h1)
                               (height ?p2 ?h2)
                               (:guard (reachable? (? h1) (? h2) max-climb))
                               )
                        :add ((at ?agent ?p2))
                        :del ((at ?agent ?p1))
                        :txt (?agent climb-on to ?p2 from ?p1)
                        :cmd [climb-on ?p2]
                        }
    climb-on-platform-from-obj {
                                 :pre ((agent ?agent)
                                        (at ?agent ?obj)
                                        (connects ?p1 ?p2)
                                        (height ?p1 ?h1)
                                        (height ?p2 ?h2)
                                        (on ?obj ?p1)
                                        (:guard (reachable? (? h1) (? h2) (? obj) max-climb))
                                        )
                                 :add ((at ?agent ?p2))
                                 :del ((at ?agent ?obj))
                                 :txt (?agent climb-on to ?p2 from ?obj)
                                 :cmd [climb-on ?p2]
                                 }
    climb-on-obj {
                   :pre ((agent ?agent)
                          (at ?agent ?p1)
                          (on ?obj ?p1)
                          )
                   :add ((at ?agent ?obj))
                   :del ((at ?agent ?p1))
                   :txt (?agent climb-on to ?obj from ?p1)
                   :cmd [climb-on ?obj]
                   }

    climb-off-platform {
                         :pre ((agent ?agent)
                                (at ?agent ?p2)
                                (connects ?p1 ?p2)
                                (height ?p1 ?h1)
                                (height ?p2 ?h2)
                                (:guard (reachable? (? h1) (? h2) max-drop))
                                )
                         :add ((at ?agent ?p1))
                         :del ((at ?agent ?p2))
                         :txt (?agent climb-off ?p2 to ?p1)
                         :cmd [climb-off ?p2]
                         }
    climb-off-platform-on-obj {
                                :pre ((agent ?agent)
                                       (at ?agent ?p2)
                                       (connects ?p1 ?p2)
                                       (height ?p1 ?h1)
                                       (height ?p2 ?h2)
                                       (on ?obj ?p1)
                                       (:guard (reachable? (? h1) (? h2) (? obj) max-drop))
                                       )
                                :add ((at ?agent ?obj))
                                :del ((at ?agent ?p2))
                                :txt (?agent climb-off ?p2 to ?obj)
                                :cmd [climb-off ?p2]
                                }
    climb-off-obj {
                    :pre ((agent ?agent)
                           (at ?agent ?obj)
                           (on ?obj ?p1)
                           )
                    :add ((at ?agent ?p1))
                    :del ((at ?agent ?obj))
                    :txt (?agent climb-off ?obj to ?p1)
                    :cmd [climb-off ?obj]
                    }
    pick-off {
               :pre ((agent ?agent)
                      (manipulable ?obj)
                      (at ?agent ?place)
                      (on ?obj ?place)
                      (holds ?agent nil)
                      )
               :add ((holds ?agent ?obj))
               :del ((on ?obj ?place)
                      (holds ?agent nil))
               :txt (?agent pick-off ?obj from ?place)
               :cmd [pick-off ?obj]
               }
    drop-on {
              :pre ((at ?agent ?place)
                     (holds ?agent ?obj)
                     (:guard (? obj))
                     )
              :add ((holds ?agent nil)
                     (on ?obj ?place))
              :del ((holds ?agent ?obj))
              :txt (?agent drop-on ?obj at ?place)
              :cmd [drop-on ?obj]
              }

    })

(def sow-main2
  '#{(at R platform1)
     (on box platform3)
     (on box2 platform1)
     (on desk platform1)
     (holds R nil)
     (connects platform1 platform2)
     (connects platform2 platform3)
     (connects platform3 platform4)
     (manipulable box)
     (manipulable box2)
     (manipulable desk)
     (agent R)
     (height platform1 1)
     (height platform2 2)
     (height platform3 4)
     (height platform4 6)
     })

;;(ops-search sow-main2 '((on box platform2) (on box2 platform1) (on desk platform4) (at R platform1)) ops-main2)


;Version 1.2:
;Added a new operator that allows the agent to drop objects off of platforms. This was added so an agent could search the environment that is reachable for objects
;that could potentially allow the agent to gain access to other areas of the world.

(def ops-main3
  '{climb-on-platform {
                        :pre ((agent ?agent)
                               (at ?agent ?p1)
                               (connects ?p1 ?p2)
                               (height ?p1 ?h1)
                               (height ?p2 ?h2)
                               (:guard (reachable? (? h1) (? h2) max-climb))
                               )
                        :add ((at ?agent ?p2))
                        :del ((at ?agent ?p1))
                        :txt (?agent climb-on to ?p2 from ?p1)
                        :cmd [climb-on ?p2]
                        }
    climb-on-platform-from-obj {
                                 :pre ((agent ?agent)
                                        (at ?agent ?obj)
                                        (connects ?p1 ?p2)
                                        (height ?p1 ?h1)
                                        (height ?p2 ?h2)
                                        (on ?obj ?p1)
                                        (:guard (reachable? (? h1) (? h2) (? obj) max-climb))
                                        )
                                 :add ((at ?agent ?p2))
                                 :del ((at ?agent ?obj))
                                 :txt (?agent climb-on to ?p2 from ?obj)
                                 :cmd [climb-on ?p2]
                                 }
    climb-on-obj {
                   :pre ((agent ?agent)
                          (at ?agent ?p1)
                          (on ?obj ?p1)
                          )
                   :add ((at ?agent ?obj))
                   :del ((at ?agent ?p1))
                   :txt (?agent climb-on to ?obj from ?p1)
                   :cmd [climb-on ?obj]
                   }

    climb-off-platform {
                         :pre ((agent ?agent)
                                (at ?agent ?p2)
                                (connects ?p1 ?p2)
                                (height ?p1 ?h1)
                                (height ?p2 ?h2)
                                (:guard (reachable? (? h1) (? h2) max-drop))
                                )
                         :add ((at ?agent ?p1))
                         :del ((at ?agent ?p2))
                         :txt (?agent climb-off ?p2 to ?p1)
                         :cmd [climb-off ?p2]
                         }
    climb-off-platform-on-obj {
                                :pre ((agent ?agent)
                                       (at ?agent ?p2)
                                       (connects ?p1 ?p2)
                                       (height ?p1 ?h1)
                                       (height ?p2 ?h2)
                                       (on ?obj ?p1)
                                       (:guard (reachable? (? h1) (? h2) (? obj) max-drop))
                                       )
                                :add ((at ?agent ?obj))
                                :del ((at ?agent ?p2))
                                :txt (?agent climb-off ?p2 to ?obj)
                                :cmd [climb-off ?p2]
                                }
    climb-off-obj {
                    :pre ((agent ?agent)
                           (at ?agent ?obj)
                           (on ?obj ?p1)
                           )
                    :add ((at ?agent ?p1))
                    :del ((at ?agent ?obj))
                    :txt (?agent climb-off ?obj to ?p1)
                    :cmd [climb-off ?obj]
                    }
    pick-off {
               :pre ((agent ?agent)
                      (manipulable ?obj)
                      (at ?agent ?place)
                      (on ?obj ?place)
                      (holds ?agent nil)
                      )
               :add ((holds ?agent ?obj))
               :del ((on ?obj ?place)
                      (holds ?agent nil))
               :txt (?agent pick-off ?obj from ?place)
               :cmd [pick-off ?obj]
               }
    drop-on {
              :pre ((at ?agent ?place)
                     (holds ?agent ?obj)
                     (:guard (? obj))
                     )
              :add ((holds ?agent nil)
                     (on ?obj ?place))
              :del ((holds ?agent ?obj))
              :txt (?agent drop-on ?obj at ?place)
              :cmd [drop-on ?obj]
              }
    drop-off-platform {
                        :pre ((at ?agent ?p2)
                               (holds ?agent ?obj)
                               (connects ?p1 ?p2)
                               (:guard (? obj))
                               )
                        :add ((holds ?agent nil)
                               (on ?obj ?p1))
                        :del ((holds ?agent ?obj))
                        :txt (?agent drop-off ?obj from ?p2 onto ?p1)
                        :cmd [drop-on ?obj]
                        }

    })


(def sow-main3
  '#{(at R platform4)
     (on box platform5)
     (on box2 platform1)
     (holds R nil)
     (connects platform1 platform2)
     (connects platform2 platform3)
     (connects platform3 platform4)
     (connects platform4 platform5)
     (manipulable box)
     (manipulable box2)
     (agent R)
     (height platform1 1)
     (height platform2 2)
     (height platform3 4)
     (height platform4 7)
     (height platform5 8)
     })

;;(ops-search sow-main3 '((at R platform2)) ops-main3)
;;Grab the box found on platform5. Drop the box off platform4 onto platform3 so the agent can climb down. Agent climbs down to platform2.

;;(ops-search sow-main3 '((on box2 platform3) (at R platform2)) ops-main3)
;;Grabs the box found on platform5. Drop the box off platform4 onto platform3. Agent climbs down onto box. Agent climbs off of box. Agent picks up box. Agent climbs down
;;onto platform2 and drops the box. Agent drops down onto platform1 and picks up box2. Agent climbs up onto platform2 and climbs onto the box found on that platform up to platform3. Agent drops box2
;;onto platform3 and finally climbs down onto platform2.


;:Version 1.4:
;;Scalable objects can now be used to climb up to and climb down to platforms. All scalable objects will need to have a length assigned to them in order from them to work. For example, if a
;;rope object has a length of 10, an agent can use this rope to climb an additional 10 units. However, at this moment in time all these objects can only be used once. In addition to this,
;;objects on platforms are not taken into consideration when scaling up or down, an agent will only ever scale down or up to a platform.

(def ops-main4
  '{climb-on-platform {
                        :pre ((agent ?agent)
                               (at ?agent ?p1)
                               (connects ?p1 ?p2)
                               (height ?p1 ?h1)
                               (height ?p2 ?h2)
                               (:guard (reachable? (? h1) (? h2) max-climb))
                               )
                        :add ((at ?agent ?p2))
                        :del ((at ?agent ?p1))
                        :txt (?agent climb-on to ?p2 from ?p1)
                        :cmd [climb-on ?p2]
                        }
    climb-on-platform-from-obj {
                                 :pre ((agent ?agent)
                                        (at ?agent ?obj)
                                        (connects ?p1 ?p2)
                                        (height ?p1 ?h1)
                                        (height ?p2 ?h2)
                                        (on ?obj ?p1)
                                        (:guard (reachable? (? h1) (? h2) (? obj) max-climb))
                                        )
                                 :add ((at ?agent ?p2))
                                 :del ((at ?agent ?obj))
                                 :txt (?agent climb-on to ?p2 from ?obj)
                                 :cmd [climb-on ?p2]
                                 }
    climb-on-platform-with-rope {
                                  :pre ((agent ?agent)
                                         (at ?agent ?p1)
                                         (connects ?p1 ?p2)
                                         (height ?p1 ?h1)
                                         (height ?p2 ?h2)
                                         (length ?obj ?length)
                                         (obj-type ?obj scalable)
                                         (holds ?agent ?obj)
                                         (:guard (reachable? (? h1) (? h2) (? length)))
                                         )
                                  :add ((at ?agent ?p2)
                                         (holds ?agent nil))
                                  :del ((at ?agent ?p1)
                                         (holds ?agent ?obj)
                                         (manipulable ?obj)
                                         (obj-type ?obj scalable))
                                  :txt (?agent climb-on to ?p2 from ?p1 using ?obj)
                                  :cmd [climb-on ?p2]
                                  }
    climb-on-obj {
                   :pre ((agent ?agent)
                          (at ?agent ?p1)
                          (on ?obj ?p1)
                          )
                   :add ((at ?agent ?obj))
                   :del ((at ?agent ?p1))
                   :txt (?agent climb-on to ?obj from ?p1)
                   :cmd [climb-on ?obj]
                   }

    climb-off-platform {
                         :pre ((agent ?agent)
                                (at ?agent ?p2)
                                (connects ?p1 ?p2)
                                (height ?p1 ?h1)
                                (height ?p2 ?h2)
                                (obj-type ?obj scalable)
                                (holds ?agent ?obj)
                                (:guard (reachable? (? h1) (? h2) max-drop))
                                )
                         :add ((at ?agent ?p1))
                         :del ((at ?agent ?p2))
                         :txt (?agent climb-off ?p2 to ?p1)
                         :cmd [climb-off ?p2]
                         }
    climb-off-platform-with-rope {
                                   :pre ((agent ?agent)
                                          (at ?agent ?p2)
                                          (connects ?p1 ?p2)
                                          (height ?p1 ?h1)
                                          (height ?p2 ?h2)
                                          (length ?obj ?length)
                                          (obj-type ?obj scalable)
                                          (holds ?agent ?obj)
                                          (:guard (reachable? (? h1) (? h2) (? length)))
                                          )
                                   :add ((at ?agent ?p1)
                                          (holds ?agent nil))
                                   :del ((at ?agent ?p2)
                                          (holds ?agent ?obj)
                                          (manipulable ?obj)
                                          (obj-type ?obj scalable))
                                   :txt (?agent climb-off ?p2 to ?p1 using ?obj)
                                   :cmd [climb-off ?p2]
                                   }
    climb-off-platform-on-obj {
                                :pre ((agent ?agent)
                                       (at ?agent ?p2)
                                       (connects ?p1 ?p2)
                                       (height ?p1 ?h1)
                                       (height ?p2 ?h2)
                                       (on ?obj ?p1)
                                       (:guard (reachable? (? h1) (? h2) (? obj) max-drop))
                                       )
                                :add ((at ?agent ?obj))
                                :del ((at ?agent ?p2))
                                :txt (?agent climb-off ?p2 to ?obj)
                                :cmd [climb-off ?p2]
                                }
    climb-off-obj {
                    :pre ((agent ?agent)
                           (at ?agent ?obj)
                           (on ?obj ?p1)
                           )
                    :add ((at ?agent ?p1))
                    :del ((at ?agent ?obj))
                    :txt (?agent climb-off ?obj to ?p1)
                    :cmd [climb-off ?obj]
                    }
    pick-off {
               :pre ((agent ?agent)
                      (manipulable ?obj)
                      (at ?agent ?place)
                      (on ?obj ?place)
                      (holds ?agent nil)
                      )
               :add ((holds ?agent ?obj))
               :del ((on ?obj ?place)
                      (holds ?agent nil))
               :txt (?agent pick-off ?obj from ?place)
               :cmd [pick-off ?obj]
               }
    drop-on {
              :pre ((at ?agent ?place)
                     (holds ?agent ?obj)
                     (:guard (? obj))
                     )
              :add ((holds ?agent nil)
                     (on ?obj ?place))
              :del ((holds ?agent ?obj))
              :txt (?agent drop-on ?obj at ?place)
              :cmd [drop-on ?obj]
              }
    drop-off-platform {
                        :pre ((at ?agent ?p2)
                               (holds ?agent ?obj)
                               (connects ?p1 ?p2)
                               (:guard (? obj))
                               )
                        :add ((holds ?agent nil)
                               (on ?obj ?p1))
                        :del ((holds ?agent ?obj))
                        :txt (?agent drop-off ?obj from ?p2 onto ?p1)
                        :cmd [drop-on ?obj]
                        }

    })


(def sow-main4
  '#{(at R platform1)
     (on rope platform2)
     (on rope2 platform4)
     (on box platform3)
     (holds R nil)
     (connects platform1 platform2)
     (connects platform2 platform3)
     (connects platform3 platform4)
     (manipulable rope)
     (manipulable rope2)
     (manipulable box)
     (agent R)
     (obj-type rope scalable)
     (obj-type rope2 scalable)
     (length rope 5)
     (length rope2 5)
     (height platform1 1)
     (height platform2 2)
     (height platform3 6)
     (height platform4 7)
     })


;;(ops-search sow-main4 '((on box platform1) (at R platform1)) ops-main4)
;;The agent will climb onto platform2 and pickup the rope. The agent will use the rope to get to platform3. Once on platform3, the agent will pickup the box that needs to
;;be transported to platform1, this box will be dropped onto platform2. The agent is unable to drop down onto the box however as the max drop is 2 and the drop is 3 at this point.
;;The agent will go to platform4 and pick up a second rope, the agent will return to platform3 and climb down the rope. The agent will pick up the box at platform2 and drop it off
;;at platform1. NOTE: Scalable objects like ropes currently do not take into consideration objects on platforms. So if a rope is 5 long and a drop is 6 high and an object
;;can be found on that platform, the agent will not be able to scale down.



;;Version 1.5:
;;This version makes it so items need to be specified as climbable in order to be used to climb up or down platforms. Objects can now also be specified as being robust. Objects
;;need to be specified as being robust in order to be used to drop off the edge of a platform. Objects can have more than one type assosiated with them. Finally, a weight limit has
;;also been implemented when attempting to pick up objects. The agent can now only pick up objects that weigh less or equal to the max carry limit specified.
(def ops-main5
  '{climb-on-platform {
                        :pre ((agent ?agent)
                               (at ?agent ?p1)
                               (connects ?p1 ?p2)
                               (height ?p1 ?h1)
                               (height ?p2 ?h2)
                               (:guard (reachable? (? h1) (? h2) max-climb))
                               )
                        :add ((at ?agent ?p2))
                        :del ((at ?agent ?p1))
                        :txt (?agent climb-on to ?p2 from ?p1)
                        :cmd [climb-on ?p2]
                        }
    climb-on-platform-from-obj {
                                 :pre ((agent ?agent)
                                        (at ?agent ?obj)
                                        (connects ?p1 ?p2)
                                        (height ?p1 ?h1)
                                        (height ?p2 ?h2)
                                        (on ?obj ?p1)
                                        (:guard (reachable? (? h1) (? h2) (? obj) max-climb))
                                        )
                                 :add ((at ?agent ?p2))
                                 :del ((at ?agent ?obj))
                                 :txt (?agent climb-on to ?p2 from ?obj)
                                 :cmd [climb-on ?p2]
                                 }
    climb-on-platform-with-rope {
                                  :pre ((agent ?agent)
                                         (at ?agent ?p1)
                                         (connects ?p1 ?p2)
                                         (height ?p1 ?h1)
                                         (height ?p2 ?h2)
                                         (length ?obj ?length)
                                         (obj-type ?obj scalable)
                                         (holds ?agent ?obj)
                                         (:guard (reachable? (? h1) (? h2) (? length)))
                                         )
                                  :add ((at ?agent ?p2)
                                         (holds ?agent nil))
                                  :del ((at ?agent ?p1)
                                         (holds ?agent ?obj)
                                         (manipulable ?obj)
                                         (obj-type ?obj scalable))
                                  :txt (?agent climb-on to ?p2 from ?p1 using ?obj)
                                  :cmd [climb-on ?p2]
                                  }
    climb-on-obj {
                   :pre ((agent ?agent)
                          (at ?agent ?p1)
                          (on ?obj ?p1)
                          (obj-type ?obj climbable)
                          )
                   :add ((at ?agent ?obj))
                   :del ((at ?agent ?p1))
                   :txt (?agent climb-on to ?obj from ?p1)
                   :cmd [climb-on ?obj]
                   }

    climb-off-platform {
                         :pre ((agent ?agent)
                                (at ?agent ?p2)
                                (connects ?p1 ?p2)
                                (height ?p1 ?h1)
                                (height ?p2 ?h2)
                                (obj-type ?obj scalable)
                                (holds ?agent ?obj)
                                (:guard (reachable? (? h1) (? h2) max-drop))
                                )
                         :add ((at ?agent ?p1))
                         :del ((at ?agent ?p2))
                         :txt (?agent climb-off ?p2 to ?p1)
                         :cmd [climb-off ?p2]
                         }
    climb-off-platform-with-rope {
                                   :pre ((agent ?agent)
                                          (at ?agent ?p2)
                                          (connects ?p1 ?p2)
                                          (height ?p1 ?h1)
                                          (height ?p2 ?h2)
                                          (length ?obj ?length)
                                          (obj-type ?obj scalable)
                                          (holds ?agent ?obj)
                                          (:guard (reachable? (? h1) (? h2) (? length)))
                                          )
                                   :add ((at ?agent ?p1)
                                          (holds ?agent nil))
                                   :del ((at ?agent ?p2)
                                          (holds ?agent ?obj)
                                          (manipulable ?obj)
                                          (obj-type ?obj scalable))
                                   :txt (?agent climb-off ?p2 to ?p1 using ?obj)
                                   :cmd [climb-off ?p2]
                                   }
    climb-off-platform-on-obj {
                                :pre ((agent ?agent)
                                       (at ?agent ?p2)
                                       (connects ?p1 ?p2)
                                       (height ?p1 ?h1)
                                       (height ?p2 ?h2)
                                       (obj-type ?obj climbable)
                                       (on ?obj ?p1)
                                       (:guard (reachable? (? h1) (? h2) (? obj) max-drop))
                                       )
                                :add ((at ?agent ?obj))
                                :del ((at ?agent ?p2))
                                :txt (?agent climb-off ?p2 to ?obj)
                                :cmd [climb-off ?p2]
                                }
    climb-off-obj {
                    :pre ((agent ?agent)
                           (at ?agent ?obj)
                           (on ?obj ?p1)
                           )
                    :add ((at ?agent ?p1))
                    :del ((at ?agent ?obj))
                    :txt (?agent climb-off ?obj to ?p1)
                    :cmd [climb-off ?obj]
                    }
    pick-off {
               :pre ((agent ?agent)
                      (manipulable ?obj)
                      (at ?agent ?place)
                      (on ?obj ?place)
                      (holds ?agent nil)
                      (obj-weight ?obj ?w)
                      (carry-limit ?agent ?limit)
                      (:guard ((? w) (? limit)))
                      )
               :add ((holds ?agent ?obj))
               :del ((on ?obj ?place)
                      (holds ?agent nil))
               :txt (?agent pick-off ?obj from ?place)
               :cmd [pick-off ?obj]
               }
    drop-on {
              :pre ((at ?agent ?place)
                     (holds ?agent ?obj)
                     (:guard (? obj))
                     )
              :add ((holds ?agent nil)
                     (on ?obj ?place))
              :del ((holds ?agent ?obj))
              :txt (?agent drop-on ?obj at ?place)
              :cmd [drop-on ?obj]
              }
    drop-off-platform {
                        :pre ((at ?agent ?p2)
                               (holds ?agent ?obj)
                               (connects ?p1 ?p2)
                               (obj-type ?obj robust)
                               (:guard (? obj))
                               )
                        :add ((holds ?agent nil)
                               (on ?obj ?p1))
                        :del ((holds ?agent ?obj))
                        :txt (?agent drop-off ?obj from ?p2 onto ?p1)
                        :cmd [drop-on ?obj]
                        }

    })


(def sow-main5
  '#{(at R platform1)
     (on box platform1)
     (holds R nil)
     (connects platform1 platform2)
     (manipulable box)
     (agent R)
     (obj-type box climbable)
     (height platform1 1)
     (height platform2 3)
     })

;;(ops-search sow-main5 '((at R platform2)) ops-main5)
;;A simple test of an agent climbing onto an object and then onto a platform.

(def sow-main6
  '#{(at R platform1)
     (on box platform1)
     (holds R nil)
     (connects platform1 platform2)
     (manipulable box)
     (agent R)
     (height platform1 1)
     (height platform2 3)
     })

;;(ops-search sow-main6 '((at R platform2)) ops-main5)
;;The same as sow-man5, however box no longer has the type climable linked to it. This should return nil as the agent has no method of getting onto the
;;next platform as the box is not climable.


(def sow-main7
  '#{(at R platform2)
     (on vault platform2)
     (holds R nil)
     (connects platform1 platform2)
     (manipulable vault)
     (obj-type vault climbable)
     (obj-type vault robust)
     (agent R)
     (height platform1 1)
     (height platform2 4)
     })

;;(ops-search sow-main7 '((at R platform1)) ops-main5)
;;A test to show that robust objects can only be dropped down from a platform as non-robust (fragile) objects would break. The vault needs to be defined as climbable in order to still
;;be climbable as some objects can be robust and not climable at the same time, for example sphere objects, ropes etc.

(defn carryable? [weight limit]
  (if (>= limit weight)
    true
    false))

(def sow-main8
  '#{(at R platform1)
     (on vault platform1)
     (holds R nil)
     (connects platform1 platform2)
     (manipulable vault)
     (obj-type vault climbable)
     (obj-type vault robust)
     (obj-weight vault 100)
     (carry-limit R 100)
     (agent R)
     (height platform1 1)
     (height platform2 2)
     })

;;(ops-search sow-main8 '((on vault platform2)) ops-main5)
;;A simple test of an agent carrying a heavy object from one platfrom to another.

(def sow-main9
  '#{(at R platform1)
     (on vault platform1)
     (holds R nil)
     (connects platform1 platform2)
     (manipulable vault)
     (obj-type vault climbable)
     (obj-type vault robust)
     (obj-weight vault 101)
     (carry-limit R 100)
     (agent R)
     (height platform1 1)
     (height platform2 2)
     })

;;(ops-search sow-main9 '((on vault platform2)) ops-main5)
;;A simple test showing that if an object is too heavy, the agent will not be able to carry it.

;-------------------------------------------------------------------------------------------
;##Bens stuff

;-----BENS
; format:  person data: (person, platform, object?) platform data: (platform, object?)
; http://s573859921.websitehome.co.uk/pub/clj/aip/lect/L3-cafe-LMGs.pdf

;; Simons LMG
(defn traverse-lmg [state]
  (remove nil? ;remove nils: see documentation
    (for [rule apply-rule8]
      (apply-rule8 rule state)
      )))

(defn apply-rule8 [rule state]
  (mlet ['[?ante :=> ?consq] rule]
    (mif [(? ante) state]
      (mout (? consq))
      )))

(def bens-platform-rules
  '([
      ([person platform1 :nil] [platform1 object])
      :=> ([person platform1 object] [platform1 :nil])] ;pick up object from platform1.

     ([person platform2 :nil] [platform2 object])
     :=> ([person platform2 object] [platform2 :nil]) ;pick up object platform2.

     ([person platform1 object] [platform1 :nil])
     :=> ([person platform1 :nil] [platform1 object]) ;put object down on platform1.

     ([person platform2 object] [platform2 :nil])
     :=> ([person platform2 :nil] [platform2 object]) ;put object down on platform2.

     ([person platform1 object] [platform1 :nil])
     :=> ([person platform2 object] [platform2 :nil]) ;carry object from platform1 to platform2.

     ([person platform2 object] [platform2 :nil])
     :=> ([person platform1 object] [platform1 :nil]) ;carry object from platform2 to platform1.

     ([person platform1 :nil] [platform1 object])
     :=> ([person platform2 :nil] [platform2 :nil]) ;move from platform1 to platform2 with no object.

     ([person platform2 :nil] [platform2 object])
     :=> ([person platform1 :nil] [platform1 :nil]) ;move from platform2 to platform1 with no object.

     ))

(def bens-platform-rules2
  '(

     [([?platform :nil] [?platform object])
      :=> ([?platform object] [?platform :nil])] ;person picks up an object

     [([?platform object] [?platform :nil])
      :=> ([?platform :nil] [?platform object])] ;person puts an object down

     [([platform1 object] [platform1 :nil])
      :=> ([platform2 object] [platform2 :nil])] ;carry object from platform1 to platform2.

     [([platform2 object] [platform2 :nil])
      :=> ([platform1 object] [platform1 :nil])] ;carry object from platform2 to platform1.

     [([platform1 :nil] [platform1 object])
      :=> ([platform2 :nil] [platform2 :nil])] ;move from platform1 to platform2 with no object.

     [([platform2 :nil] [platform2 object])
      :=> ([platform1 :nil] [platform1 :nil])] ;move from platform2( to platform1 with no object.

     [([?platform :nil] [?platform object]) ;climb on object
      :=> ([object :nil] [?platform object])]

     [([object :nil] [?platform object]) ;climb off object
      :=> ([?platform :nil] [?platform object])]
     ))

;;(traverse-lmg '([platform1  :nil][platform1 object]))
;     R: (([platform1 object] [platform1 :nil]) ([platform2 :nil] [platform2 :nil]) ([object :nil] [platform1 object]))
;          (Pick object up)                      (move to platform2 with no object)        (stand on object) - works for platform 2 too.

;;(traverse-lmg '([platform1  object][platform1 :nil]))
;     R: (([platform1 :nil] [platform1 object]) ([platform2 object] [platform2 :nil]))
;          (Put object down) or (move to platform2) -works for platform 2 too
(defn traverse-lmg [state]
  (remove nil? ;remove nils: see documentation
    (for [rule bens-platform-rules2]
      (apply-rule rule state)
      )))

(defn apply-rule [rule state]
  (mlet ['[?ante :=> ?consq] rule]
    (mif [(? ante) state]
      (mout (? consq))
      )))

;;(traverse-lmg '([platform1  :nil][platform1 object])) <<Works
;;(traverse-lmg '([platform1  object][platform1 :nil]))

(defn traverse-lmg [state]
  (remove nil? ;remove nils: see documentation
    (for [rule bens-platform-rules2]
      (apply-rule rule state)
      )))

(defn apply-rule [rule state]
  (mlet ['[?ante :=> ?consq] rule]
    (mif [(? ante) state]
      (mout (? consq))
      )))



;(def platform-height
;  (
;    {:climable-object platform1 :height 1}
;    {:climable-object platform2 :height 2}
;    {:climable-object platform3 :height 2}
;    {:climable-object platform4 :height 3}
;    {:climable-object platform5 :height 3}
;    {:climable-object platform6 :height 3}
;    {:climable-object platform7 :height 4}
;  {:climable-object platform8 :height 6}
;   {:climable-object box       :height 1}
;   )
; )