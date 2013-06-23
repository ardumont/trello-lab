(ns trello.api-oauth
  "Connection to trello through oauth"
  (:require [clj-http.client :as c]
            [clojure.string  :as s]
            [oauth.client    :as oauth]
            [clojure.pprint  :as pprint]))

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

(def URL "https://api.trello.com/1")

(defn compute-url "Compute url with authentication needed." [url path] (format "%s%s" url path))

(defn credentials
  [method url]
  (oauth/credentials consumer
                     (:oauth_token access-token-response)
                     (:oauth_token_secret access-token-response)
                     method
                     (compute-url URL url)))

(defn api-query
  ([method path & [req]]
     (let [creds (credentials method path)]
       (->> {:method       method
             :url          (compute-url URL path)
             :accept       :json
             :as           :json
             :query-params creds}
            (merge req)
            c/request))))

;; api part

(defn get-boards
  "Retrieve the boards of the current user."
  []
  (api-query :get "/members/me/boards"))

(comment
  (->> (get-boards)
       :body
       (map #(select-keys % #{:name :url :id})))

  ;; '({:id "some-id0"
  ;;    :url "https://trello.com/board/some-name0/some-id0"
  ;;    :name "some-name0-with-spaces"}
  ;;   {:id "some-id1"
  ;;    :url "https://trello.com/board/some-name1/some-id1"
  ;;    :name "some-name1-with-spaces"})
  )

(defn get-board
  "Retrieve the boards of the current user."
  [id]
  (api-query :get (str "/boards/" id)))

(comment
  (get-board "some-board-id")

  ;; {:trace-redirects ["https://api.trello.com/1/boards/some-board-id"]
  ;;  :request-time 995
  ;;  :status 200
  ;;  :headers {"x-server-time" "1371833899621"
  ;;            "access-control-allow-origin" "*"
  ;;            "content-type" "application/json"
  ;;            "date" "Fri 21 Jun 2013 16:58:19 GMT"
  ;;            "cache-control" "max-age=0 must-revalidate no-cache no-store"
  ;;            "expires" "Thu 01 Jan 1970 00:00:00"
  ;;            "access-control-allow-methods" "GET PUT POST DELETE"
  ;;            "x-powered-by" "Express"
  ;;            "content-length" "484"
  ;;            "connection" "close"}
  ;;  :body {:desc ""
  ;;         :name "some-name"
  ;;         :labelNames {:yellow ""
  ;;                      :red "administration"
  ;;                      :purple ""
  ;;                      :orange ""
  ;;                      :green ""
  ;;                      :blue ""}
  ;;         :prefs {:selfJoin false
  ;;                 :invitations "members"
  ;;                 :canInvite true
  ;;                 :canBeOrg true
  ;;                 :canBePrivate true
  ;;                 :cardCovers true
  ;;                 :comments "members"
  ;;                 :permissionLevel "private"
  ;;                 :canBePublic true
  ;;                 :voting "members"}
  ;;         :pinned true
  ;;         :closed true
  ;;         :idOrganization nil
  ;;         :url "https://trello.com/board/web2print/some-board-id"
  ;;         :id "some-board-id"}}
  )

(defn get-cards
  "cards of a board"
  [board-id]
  (api-query :get (str "/boards/" board-id "/cards")))

(defn get-card
  "Detail of a card with id card-id."
  [card-id]
  (api-query :get (str "/cards/" card-id)))
