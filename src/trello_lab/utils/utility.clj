(ns trello-lab.utils.utility
  "Some utility tools"
  (:use [expectations]))

(defn trace "Decorator to display data on the console."
  [e]
  (println (format "TRACE: %s" e)))

(expect (interaction (println "TRACE: test"))
        (trace "test"))
