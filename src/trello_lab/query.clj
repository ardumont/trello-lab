(ns trello-lab.query
  "Query the trello api (the basic authentication scheme is implemented here)"
  (:use [midje.sweet])
  (:require [clj-http.client   :as c]
            [clojure.string    :as s]))

;; your credentials in the ~/.trello/config.clj file
;; (def trello-credentials {:consumer-key "consumer-key"
;;                          :consumer-secret-key  "consumer-secret-key"
;;                          ;; https://trello.com/1/authorize?response_type=token&name=org-trello&scope=read,write&expiration=never&key=e9a870215aa36c90957d67345fe1388c
;;                          :access-token "access-token"})

;; load consumer-key, consumer-secret-key and access-token that does give access to everything forever (boum)
(load-file (str (System/getProperty "user.home") "/.trello/config.clj"))

(def URL "The needed prefix url for trello" "https://api.trello.com/1")

(defn- compute-url
  "Compute url with authentication needed."
  ([url path consumer-key]
     (format "%s%s?key=%s" url path consumer-key))
  ([url path consumer-key secret-token]
     (format "%s&token=%s" (compute-url url path consumer-key) secret-token)))

(fact
  (compute-url URL "/members/ardumont" "consumer-key")                => "https://api.trello.com/1/members/ardumont?key=consumer-key"
  (compute-url URL "/members/ardumont" "consumer-key" "secret-token") => "https://api.trello.com/1/members/ardumont?key=consumer-key&token=secret-token")

(defn- compute-url-final
  [path]
  (compute-url URL path (:consumer-key trello-credentials) (:access-token trello-credentials)))

(defn api
  "Query trello using the secret-token provided or use the one loaded from ~/.trello/config.clj"
  [method path & [req]]
  (->> {:method     method
        :url        (compute-url-final path)
        :accept     :json
        :as         :json}
       (merge req)
       c/request
       :body))

(comment ;; reading public data without tokens
  (api :get "/members/ardumont")
  (api :get "/boards/4d5ea62fd76aa1136000000c")
  (api :get "/organizations/fogcreek"))

(defn- execute-post-or-put
  [fn-post-or-put path body]
  (-> path
      compute-url-final
      (fn-post-or-put {:form-params  body
                       :content-type :json
                       :accept       :json
                       :as           :json})
      :body))

(defn post "POST" [path body] (execute-post-or-put c/post path body))

(comment
  (post "/cards/" {:name "anothertest"
                   :desc "some other desc"
                   :idList "51ccc748f7f9987320000cca"}))

(defn put "PUT" [path body] (execute-post-or-put c/put path body))

(comment
  (put (str "/cards/51ccca27a1b988f11300033c") {:desc "trying-out-the-movement"
                                                :name "renamingtestinplace"
                                                :idList "50bcfd2f033110476000e769"}))
