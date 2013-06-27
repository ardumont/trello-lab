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

(defn lists
  "Display the lists of the board"
  [board-id]
  (query/api :get (str "/boards/" board-id "/lists")))

(comment
  (lists "50bcfd2f033110476000e768"))

(defn add-list
  "Add a list - the name and the board id are mandatory (so i say!)."
  [{:keys [name idBoard] :as list-data}]
  {:pre [(and name idBoard)]}
  (query/post "/lists/" list-data))

(comment
  (add-list {:name "review"
             :idBoard "50bcfd2f033110476000e768"})
  (add-list {:name "review"}))

(defn add-card
  "Add a card to a board"
  [card-data]
  (query/post "/cards/" card-data))

(comment
  (add-card {:name "test"
             :idList "51ccc748f7f9987320000cca"}))

(defn list-cards
  [list-id]
  (query/api :get (str "/lists/" list-id "/cards/")))

(comment
  (list-cards "51ccc748f7f9987320000cca"))

(defn move-card
  [{:keys [id] :as card-data}]
  (query/put (str "/cards/" id) card-data))

(comment
  (add-card {:id "51ccca27a1b988f11300033c"
             :name "renamingtestinplace"
             :idList "50bcfd2f033110476000e769"}))
