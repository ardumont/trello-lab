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
         :server-uri "https://api.trello.com/1"
         ;; the registered pair request/response
         :requests   {}}))

(defn get-data "Retrieve the metadata."
  [m]
  (select-keys m [:mode :server-uri :requests]))

(defn change-metadata! "Update the data (server-uri, requests - loading requests, mode - only 'record' or 'replay', etc...)."
  [{:keys [mode server-uri requests]} m]
  (do
    ;; update data
    (if mode       (swap! m #(assoc % :mode mode)))
    (if server-uri (swap! m #(assoc % :server-uri server-uri)))
    (if requests   (swap! m #(assoc % :requests requests)))
    ;; return the updated data
    (get-data @m)))

(defn read-body "Read the body from the requests"
  [body]
  (-> body
      (slurp :encoding "UTF-8")
      json/read-str
      clojure.walk/keywordize-keys))

(defroutes app-admin-routes
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

  (PUT "/metadata/save/" []
       (do
         ;; store any metadata on disk (do not check anything)
         (spit ".metadata/metadata.clj" @metadata :encoding "UTF-8")
         ;; send a description of what has been done
         (-> {:description "saving metadata on '.metadata/metadata.clj' done!"}
             json/write-str
             response/put-json-response)))

  (GET "/metadata/load/" []
       (-> ".metadata/metadata.clj"
           (slurp :encoding "UTF-8")
           read-string
           (change-metadata! metadata)
           json/write-str
           response/get-json-response))

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
  (let [resp (-> request
                 handler
                 cleanup-response-or-request)
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

(defn wrap-proxy "Do the request to the real server."
  [handler metadata]
  (fn [request]
    (let [server-uri (get-in @metadata [:server-uri])
          new-request (-> request
                          (assoc     :url       (str server-uri (:uri request)))
                          (update-in [:headers] dissoc "content-length"))]
      (handler (u/trace :new-request new-request)))))

(def app-admin
  (-> app-admin-routes
      middleware/wrap-error-handling
      handler/site))

(defn app-proxy-routes "Basic routes. The intelligence for such routes is null. We need this to record or replay requests."
  [request]
  (-> {:description "Proxy in charge or recording/replaying requests."}
      json/write-str
      response/get-json-response))

(def app-proxy "App to record or replay requests"
  (-> app-proxy-routes
      ;; deal with basic errors
      middleware/wrap-error-handling
      ;; compute the request for the real server
      (wrap-proxy metadata)
      ;; record or replay the request
      (wrap-action metadata)))

(comment ;; ######### Running the admin (server permitting the setup of the proxy) and the proxy (in charge of recording/replaying the requests)

  (declare jetty-admin
           jetty-proxy
           stop-admin
           stop-proxy)

  (if (bound? #'jetty-admin) (stop-admin))
  (if (bound? #'jetty-proxy) (stop-proxy))

  (def jetty-admin
    (ring-jetty/run-jetty app-admin {:port  3000
                                     :join? false}))
  (def jetty-proxy
    (ring-jetty/run-jetty app-proxy {:port  3001
                                     :join? false}))

  (defn start-admin   [] (.start jetty-admin))
  (defn stop-admin    [] (.stop  jetty-admin))
  (defn restart-admin [] (stop-admin) (start-admin))

  (defn start-proxy   [] (.start jetty-proxy))
  (defn stop-proxy    [] (.stop  jetty-proxy))
  (defn restart-proxy [] (stop-proxy) (start-proxy))

  (start-admin)
  (stop-admin)
  (restart-admin)

  (start-proxy)
  (stop-proxy)
  (restart-proxy))
