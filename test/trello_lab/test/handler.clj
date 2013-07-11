(ns trello-lab.test.handler
  (:use [trello-lab.handler]
        [ring.mock.request]
        [expectations])
  (:require [clojure.data.json :as json])
  (:import [java.io InputStream]))

(expect {:status 200
         :headers {"Content-Type" "application/json"}
         :body (json/write-str
                {:description "trello-lab - REST API to deal with the board updates of your trello - This is to be used with emacs's org-trello mode."})}
        (app-admin-routes (request :get "/")))

(expect {:status 404
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body "Not Found"}
  (app-admin-routes (request :get "/not-found" app-admin-routes)))

(expect {:mode "a" :server-uri "b"}
        (get-data {:mode       "a"
                   :server-uri "b"
                   :some       "come"
                   :get        "some"}))

(expect {:mode "replay"
         :server-uri "some-new-uri"}
        (change-metadata! {:mode "replay" :server-uri "some-new-uri"}
                          (atom {:mode "some-other-record-mode" :server-uri ""})))

(expect {:mode "some-other-record-mode"
         :server-uri ""
         :requests :some-requests}
        (change-metadata! {:requests :some-requests}
                          (atom {:mode "some-other-record-mode" :server-uri ""})))

(expect {:mode "recode"
         :server-uri "some-server"}
 (-> {"mode" "recode"
      "server-uri" "some-server"}
     json/write-str
     (.getBytes "UTF-8")
     (java.io.ByteArrayInputStream.)
     read-body))
