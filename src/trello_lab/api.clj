(ns scratch.trello.api
  (:require [clj-http.client   :as c]
            [clojure.string    :as s]))

;; your credentials in the ~/.trello/config.clj file
;; (def trello-credentials {:developer-api-key "developer-api-key"
;;                          :secret-oauth-key  "secret-oauth-key"})

(load-file (str (System/getProperty "user.home") "/.trello/config.clj"))

(defn url
  "The needed prefix url for trello"
  []
  "https://api.trello.com/1")

(defn compute-url
  "Compute url with authentication needed."
  ([url path]
     (format "%s%s?key=%s"
             url
             path
             (:developer-api-key trello-credentials)))
  ([url path secret-token]
     (format "%s&token=%s"
             (compute-url url path)
             secret-token)))

(comment
  (compute-url (url) "/members/ardumont")
  (compute-url (url) "/members/ardumont" "some-secret-token"))

(defn api-query
  ([method path]
     (c/request
      {:method     method
       :url        (compute-url (url) path)
       :accept     :json
       :as         :json}))
  ([method path secret-token]
     (c/request
      {:method     method
       :url        (compute-url (url) path secret-token)
       :accept     :json
       :as         :json})))

(comment ;; reading public data without tokens
  (api-query :get "/members/ardumont")
  (api-query :get "/boards/4d5ea62fd76aa1136000000c")
  (api-query :get "/organizations/fogcreek"))


(defn get-token-from-url
  "Generates the trello url to retrieve a token."
  []
  (format "https://trello.com/1/connect?key=%s&expiration=1day&response_type=token&scope=read,write" (:developer-api-key trello-credentials)))

(comment ;; for private data, we need to ask for a token

  ;; execute this code, then let do the stuff the browser asks you to
  (clojure.java.browse/browse-url (get-token-from-url))

  ;; Retrieve the secret token the browser gives you
  (def secret-token "secret-token")

  ;; now we can read/write to private data
  (api-query :get "/members/me/boards" secret-token)

  ;; list the cards of the board 50bcfd2f033110476000e768
  (api-query :get "/board/50bcfd2f033110476000e768/lists" secret-token))
