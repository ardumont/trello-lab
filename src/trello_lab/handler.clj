(ns trello-lab.handler
  "REST Api routes"
  (:use [compojure.core :only [defroutes POST GET PUT]]
        [expectations])
  (:require [compojure
             [core                    :as comp]
             [handler                 :as handler]
             [route                   :as route]]
            [trello-lab.utils.utility :as u]
            [trello-lab.api           :as trello]
            [trello-lab.http
             [response                :as response]
             [middleware              :as middleware]]
            [clojure.data.json        :as json]
            [ring.adapter.jetty       :as ring-jetty]))

(def metadata "In :mode \"record\", every requests and responses are recorded.
In :mode :replay, every requests are replayed if they already had been recorded."
  (atom {;; possible modes:
         ;; - "record" to record every request/response
         ;; - "replay" to replay every recorded request/response
         :mode       "record"
         ;; the server uri to send requests to and register the response from for later 'faking' it.
         :server-uri ""
         ;; the registered requests
         :requests   {}}))

(defn get-data "Retrieve the metadata."
  [m]
  (select-keys m [:mode :server-uri]))

(defn change-metadata! "Update the data (server-uri, mode, etc...)."
  [{:keys [mode server-uri]} m]
  {:pre [(or (= mode "record") (= mode "replay"))]}
  (do
    ;; update data
    (if mode       (swap! m #(assoc % :mode mode)))
    (if server-uri (swap! m #(assoc % :server-uri server-uri)))
    ;; return the updated data
    (get-data @m)))

(defn read-body "Read the body from the inputed requests"
  [body]
  (-> body
      (slurp :encoding "UTF-8")
      json/read-str
      clojure.walk/keywordize-keys))

(defroutes app-routes
  ;; ######### Description part

  (GET "/" []
       (-> {:description "trello-lab - REST API to deal with the board updates of your trello - This is to be used with emacs's org-trello mode."}
            json/write-str
            response/get-json-response))

  ;; ######### Setup api part

  ;; API to deal with the registering mode or not
  (GET "/metadata/" []
       (-> @metadata
           get-data
           json/write-str
           response/get-json-response))

  (PUT "/metadata/" {body :body}
       (-> body
           read-body
           (change-metadata! metadata)
           json/write-str
           u/trace
           response/put-json-response))

  ;; ######### part, will record any api call, call the right server

  ;; main routes
  (GET "/boards/" []
       (->> (trello/get-boards)
            json/write-str
            response/get-json-response))

  (GET "/boards/:nature" [nature]
       (->> (trello/get-boards) ;; TODO use trello's natural filter...
            (map (keyword nature));; ... instead of filtering after the http connection
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

(def jetty-server
  (ring-jetty/run-jetty app {:port  3000
                             :join? false}))

(defn start   [] (.start jetty-server))
(defn stop    [] (.stop  jetty-server))
(defn restart [] (stop) (start))

(comment
  (start)
  (stop)
  (restart))
