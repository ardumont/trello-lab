(ns trello.query
  "Query the trello api (the basic authentication scheme is implemented here)"
  (:require [clj-http.client   :as c]
            [clojure.string    :as s]))

;; your credentials in the ~/.trello/config.clj file
;; (def trello-credentials {:consumer-key "consumer-key"
;;                          :consumer-secret-key  "consumer-secret-key"})

;; load consumer-key and consumer-secret and secret-token that does give access to everything forever (boum)
(load-file (str (System/getProperty "user.home") "/.trello/config.clj"))

(def URL "The needed prefix url for trello" "https://api.trello.com/1")

(defn- compute-url
  "Compute url with authentication needed."
  ([url path]
     (format "%s%s?key=%s"
             url
             path
             (:consumer-key trello-credentials)))
  ([url path secret-token]
     (format "%s&token=%s"
             (compute-url url path)
             secret-token)))

(comment
  (compute-url URL "/members/ardumont")
  (compute-url URL "/members/ardumont" "some-secret-token"))

(defn api
  "Query trello using the secret-token provided or use the one loaded from ~/.trello/config.clj"
  ([method path]
     (api method path org-trello-token-forever))
  ([method path secret-token]
     (c/request
      {:method     method
       :url        (compute-url URL path secret-token)
       :accept     :json
       :as         :json})))

(comment ;; reading public data without tokens
  (api :get "/members/ardumont")
  (api :get "/boards/4d5ea62fd76aa1136000000c")
  (api :get "/organizations/fogcreek"))

(comment ;; for private data, we need to ask for a token

  ;; execute this code, then let do the stuff the browser asks you to
  (clojure.java.browse/browse-url (get-token-from-url))

  ;; Retrieve the secret token the browser gives you
  (def secret-token org-trello-token-forever)

  ;; now we can read/write to private data
  (api :get "/members/me/boards" secret-token)

  ;; list the cards of the board 50bcfd2f033110476000e768
  (api :get "/board/50bcfd2f033110476000e768/lists" secret-token))
