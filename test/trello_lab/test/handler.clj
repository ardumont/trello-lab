(ns trello-lab.test.handler
  (:use [midje.sweet]
        [trello-lab.handler]
        [ring.mock.request])
  (:require [clojure.data.json :as json]))

(fact :slash
  (app-routes (request :get "/")) => {:status 200
                                      :headers {"Content-Type" "application/json"}
                                      :body (json/write-str
                                             {:description "trello-lab - REST API to deal with the board updates of your trello - This is to be used with emacs's org-trello mode."})})

(fact :not-found
  (app-routes (request :get "/not-found" app-routes)) => {:status 404
                                                          :headers {"Content-Type" "text/html; charset=utf-8"}
                                                          :body "Not Found"})
