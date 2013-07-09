(ns trello-lab.utils.utility
  "Some utility tools")

(defn trace "Decorator to display data on the console."
  ([label e]
       (println (format "TRACE: %s: %s" label e)))
  ([e]
       (println (format "TRACE: %s" e))))
