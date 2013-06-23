(ns trello-lab.handler
  "REST Api routes"
  (:use [compojure.core :only [defroutes POST GET]])
  (:require [compojure
             [core          :as comp]
             [handler       :as handler]
             [route         :as route]]
            [trello-lab.api :as trello]
            [trello-lab.http
             [response      :as response]
             [middleware    :as middleware]]
            [clojure.data.json :as json]))

(defroutes app-routes
  ;; dummy route to explain what this api is
  (GET "/" []
       (response/body-response "trello-lab - REST API to deal with the board updates of your trello - This is to be used with emacs's org-trello mode."))

  ;; main routes
  (GET "/boards/" []
       (->> (trello/get-boards)
            :body
            json/write-str
            response/get-json-response))

  (GET "/boards/:nature" [nature]
       (->> (trello/get-boards)
            :body
            (map (keyword nature))
            json/write-str
            response/get-json-response))

  ;; create card in the board
  ;; create checklist in the card
  ;; add tasks to the checklist

  ;; any other route
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      middleware/wrap-error-handling
      handler/site))
