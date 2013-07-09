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
         ;; the registered pair request/response
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

  ;; any other route
  (route/not-found "Not Found"))

(defn- cleanup-response-or-request "Takes a request or response map and clean it up according to the app config."
  [request]
  (let [ignore-seq     [:ssl-client-cert :remote-addr :server-name :server-port]
        ignore-headers ["host"]]
    (-> ignore-seq
        (->> (cons request))
        (->> (apply dissoc))
        (update-in [:headers] #(apply dissoc (cons % ignore-headers))))))

(def do-action nil)
;; The actual action that takes place (either record or replay requests)
(defmulti do-action (fn [metadata _ _] (:mode @metadata)))

(defmethod do-action "record"
  [metadata handler request]
  ;; change the uri from this proxy to the server-uri entry
  (let [server-uri (get-in @metadata [:server-uri])
        resp (-> request handler cleanup-response-or-request)
        requ (cleanup-response-or-request request)]
    (swap! metadata #(assoc-in % [:requests requ] resp))
    resp))

(defmethod do-action "replay"
  [metadata _ request]
  ;; retrieve the request
  ;; send the recorded response to such request
  (get-in @metadata [:requests (cleanup-response-or-request request)]))

(defn wrap-action "A middleware that records the http request / response into an atom"
  [handler metadata]
  (fn [request]
    (do-action metadata handler request)))

(def app
  (-> app-routes
      middleware/wrap-error-handling
      (wrap-action metadata)
      handler/site))

;; ######### Running the server from the repl

(declare jetty-server
         stop)

(if (bound? #'jetty-server) (stop))

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
