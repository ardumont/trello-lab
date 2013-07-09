(ns trello-lab.test.handler
  (:use [trello-lab.handler]
        [ring.mock.request]
        [expectations])
  (:require [clojure.data.json :as json]))

(expect {:status 200
         :headers {"Content-Type" "application/json"}
         :body (json/write-str
                {:description "trello-lab - REST API to deal with the board updates of your trello - This is to be used with emacs's org-trello mode."})}
        (app-routes (request :get "/")))

(expect {:status 404
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body "Not Found"}
  (app-routes (request :get "/not-found" app-routes)))

(expect {:mode "a" :server-uri "b"}
        (get-data {:mode       "a"
                   :server-uri "b"
                   :some       "come"
                   :get        "some"}))

(expect {:mode "replay"
         :server-uri "some-new-uri"}
        (change-metadata! {:mode "replay" :server-uri "some-new-uri"}
                          (atom {:mode "some-other-record-mode" :server-uri ""})))
