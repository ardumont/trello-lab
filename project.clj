(defproject trello-lab "0.1.0-SNAPSHOT"
  :description "Mess around with trello"
  :url "http://github.com/trello-lab"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure           "1.5.1"]
                 [clj-http                      "0.6.3"]
                 [ring                          "1.1.6" ]
                 ;; https://github.com/mattrepl/clj-oauth
                 [clj-oauth                     "1.4.0"]
                 [compojure                     "1.1.5"]
                 [org.clojure/data.json         "0.2.2"]
                 [midje                         "1.6.3"]]
  :plugins [[lein-ring                          "0.8.5"]
            [lein-expectations                  "0.0.7"]]
  :ring {:handler trello-lab.handler/app}
  :dev-dependencies [[ring-mock    "0.1.5"]
                     [expectations "1.4.42"]
                     [midje        "1.6.3"]])
