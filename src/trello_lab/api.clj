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

(comment
  (def boards (get-boards))
  (def board1 (get-board "50bcfd2f033110476000e768")))

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

(defn get-list
  "Get a list by id"
  [list-id]
  (query/api :get (str "/lists/" list-id)))

(defn add-list
  "Add a list - the name and the board id are mandatory (so i say!)."
  [{:keys [name idBoard] :as list-data}]
  {:pre [(and name idBoard)]}
  (query/post "/lists/" list-data))

(comment
  (def list-review
    (add-list {:name "review"
               :idBoard (:id board1)}))
  (def list-todo (get-list "50bcfd2f033110476000e769")))

(defn add-card
  "Add a card to a board"
  [card-data]
  (query/post "/cards/" card-data))

(comment
  (def card1
    (add-card {:name "card test"
               :idList (:id list-review)})))

(defn list-cards
  [list-id]
  (query/api :get (str "/lists/" list-id "/cards/")))

(defn move-card
  [{:keys [id idList name] :as card-data}]
  (query/put (str "/cards/" id) {:id id
                                 :name name
                                 :idList idList}))

(comment
  (def card1 (-> card1
                 (assoc :idList (:id list-todo))
                 (assoc :name "original name")
                 move-card))
  (def card1 (-> card1
                 (assoc :idList (:id list-review))
                 (assoc :name "name card to move")
                 move-card)))

(defn add-checklist
  "Add a checklist to a card"
  [{:keys [card-id name] :as checklist-data}]
  {:pre [(and card-id name)]}
  (query/post (str "/cards/" card-id "/checklists") {:name name}))

(comment
  (def checklist
    (add-checklist {:card-id (:id card1)
                    :name "name-of-the-checklist"})))

(defn get-checklists
  [card-id]
  (query/api :get (str "/cards/" card-id "/checklists")))

(comment
  (get-checklists (:id card1)))

(defn get-checklist
  [id]
  (query/api :get (str "/checklists/" id)))

(comment
  (get-checklist (:id checklist)))

(defn add-tasks
  "Add tasks (items) to a checklist with id 'id'"
  [{:keys [checklist-id name] :as items-data}]
  {:pre [(and checklist-id name)]}
  (-> (str "/checklists/" checklist-id "/checkItems")
      (query/post {:name name})))

(defn check-or-unchecked-tasks
  "Update a task"
  [{:keys [card-id checklist-id task-id state]}]
  (-> (str "/cards/" card-id "/checklist/" checklist-id "/checkItem/" task-id)
      (query/put {:state state})))

(comment
  (def task (add-tasks {:checklist-id (:id checklist)
                        :name "name-of-the-item"}))

  (def task (check-or-unchecked-tasks {:card-id      (:id card1)
                                       :task-id      (:id task)
                                       :checklist-id (:id checklist)
                                       :state        "complete"})))
