(ns trello-lab.http.middleware
  "Middlewares"
  (:require [trello-lab.http.response :as response]
            [clojure.data.json        :as json]))

(defn wrap-error-handling "A middleware to deal with basic error"
  [handler]
  (fn [req]
    (try
      (or (handler req)
          (response/json-response (json/write-str {:error "resource not found!"}) 404))
      (catch AssertionError e
        (response/json-response (json/write-str {:error "bad request"}) 400)))))
