(ns scratch.trello.api-oauth
  (:require [clj-http.client :as c]
            [clojure.string  :as s]
            [oauth.client    :as oauth]))

;; your credentials in the ~/.trello/config.clj file
;; (def trello-credentials {:consumer-key "consumer-key"
;;                          :consumer-secret-key  "consumer-secret-key"})

;; load consumer-key and consumer-secret
(load-file (str (System/getProperty "user.home") "/.trello/config.clj"))

;; Create a Consumer, in this case one to access Twitter.
;; Register an application at the service you want to use (e.g. Twitter - http://api.twitter.com/oauth_clients/new, trello, etc...)
;; to obtain a Consumer token and token secret.
(def consumer (oauth/make-consumer (:consumer-key trello-credentials)
                                   (:consumer-secret-key trello-credentials)
                                   "https://trello.com/1/OAuthGetRequestToken"
                                   "https://trello.com/1/OAuthGetAccessToken"
                                   "https://trello.com/1/OAuthAuthorizeToken"
                                   :hmac-sha1))

;; Fetch a request token that a OAuth User may authorize
;;
;; If you are using OAuth with a desktop application, a callback URI
;; is not required.
(def callback-uri nil)
(def request-token (oauth/request-token consumer callback-uri))

;; Send the User to this URI for authorization, they will be able
;; to choose the level of access to grant the application and will
;; then be redirected to the callback URI provided with the
;; request-token.
(oauth/user-approval-uri consumer (:oauth_token request-token))
;;"https://trello.com/1/OAuthAuthorizeToken?oauth_token=XXYYYYZZZ"

;; Assuming the User has approved the request token, trade it for an access token.
;; The access token will then be used when accessing protected resources for the User.
;;
;; If the OAuth Service Provider provides a verifier, it should be included in the
;; request for the access token.  See [Section 6.2.3](http://oauth.net/core/1.0a#rfc.section.6.2.3) of the OAuth specification
;; for more information.
(def verifier nil)
(def access-token-response (oauth/access-token consumer
                                               request-token
                                               verifier))

;; Each request to a protected resource must be signed individually. The
;; credentials are returned as a map of all OAuth parameters that must be
;; included with the request as either query parameters or in an
;; Authorization HTTP header.
(def credentials (oauth/credentials consumer
                                    (:oauth_token access-token-response)
                                    (:oauth_token_secret access-token-response)
                                    :GET
                                    "https://api.trello.com/1/members/me/boards"))

(def URL "The needed prefix url for trello" "https://api.trello.com/1")

(defn compute-url "Compute url with authentication needed." [url path] (format "%s%s" url path))

(defn api-query
  ([method path & [req]]
     (->> {:method     method
           :url        (compute-url URL path)
           :accept     :json
           :as         :json}
          (merge req)
          c/request)))

(comment
  (api-query :get "/members/me/boards"
             {:query-params credentials})

  (->> (api-query :get "/members/me/boards"
                  {:query-params credentials})
       :body
       (map :name)
       clojure.pprint/pprint))
