(defproject trello-lab "0.1.0-SNAPSHOT"
  :description "Mess around with trello"
  :url "http://github.com/trello-lab"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http            "0.6.3"]
                 ;; https://github.com/mattrepl/clj-oauth
                 [clj-oauth           "1.4.0"]
                 [compojure           "1.1.5"]
                 [org.clojure/data.json   "0.2.2"]]
  :plugins [[lein-ring "0.8.5"]]
  :ring {:handler trello-lab.handler/app}
  :profiles {:dev {:dependencies [[ring-mock "0.1.5"]]}})
