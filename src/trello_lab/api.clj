(ns trello-lab.api
  "Wrapper around the trello api"
  (:require [trello-lab.query :as query]
            [clj-http.core :as http]))

(defn get-boards
  "Retrieve the boards of the current user."
  []
  (query/api :get "/members/me/boards"))

(defn get-board
  "Retrieve the boards of the current user."
  [id]
  (query/api :get (str "/boards/" id)))

(defn get-cards
  "cards of a board"
  [board-id]
  (query/api :get (str "/boards/" board-id "/cards")))

(defn get-card
  "Detail of a card with id card-id."
  [card-id]
  (query/api :get (str "/cards/" card-id)))

(defn add-list
  [list-data]
  (query/post "/lists/" list-data))

(comment
  (add-list {:name "review"
             :idBoard "50bcfd2f033110476000e768"}))
