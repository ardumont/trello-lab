(ns trello-lab.http.response
  "A namespace to deal with the response to client request")

(def json-type "application/json")

(defn with-status-content-type-message
  "Answering a request with message and content-type"
  [s h m]
  {:status s
   :headers {"Content-Type" h}
   :body m})

(defn body-response "Answering request in text/plain with the message"
  [m] (with-status-content-type-message 200 "text/plain" m))

(defn json-response "Answering request in json"
  [m status]
  (with-status-content-type-message status json-type m))

(defn get-json-response "Answer a request with json mime type and the message m"
  [m] (json-response m 200))

(defn put-json-response "Answer a request with json mime type and the message m"
  [m] (json-response m 201))

(defn post-json-response "Answer a request with json mime type and the message m"
  [m] (with-status-content-type-message 201 json-type m))
