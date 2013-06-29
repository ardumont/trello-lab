(ns trello-lab.api
  "Wrapper around the trello api"
  (:require [trello-lab.query :as query]
            [clj-http.core :as http]))

(defn get-boards
  "Retrieve the boards of the current user."
  []
  {:method :get
   :uri    "/members/me/boards"})

(defn get-board
  "Retrieve the boards of the current user."
  [id]
  {:method :get
   :uri    (str "/boards/" id)})

(comment
  (def boards (-> (get-boards)
                  query/execute))
  (def board1 (-> boards
                  :id
                  get-board
                  query/execute)))

(defn get-cards
  "cards of a board"
  [board-id]
  {:method :get
   :uri    (str "/boards/" board-id "/cards")})

(defn get-card
  "Detail of a card with id card-id."
  [card-id]
  {:method :get
   :uri    (str "/cards/" card-id)})

(defn lists
  "Display the lists of the board"
  [board-id]
  {:method :get
   :uri    (str "/boards/" board-id "/lists")})

(defn get-list
  "Get a list by id"
  [list-id]
  {:method :get
   :uri    (str "/lists/" list-id)})

(defn add-list
  "Add a list - the name and the board id are mandatory (so i say!)."
  [{:keys [name idBoard] :as list-data}]
  {:pre [(and name idBoard)]}
  {:method :post
   :uri    "/lists/"
   :params list-data})

(comment
  (def list-review
    (-> {:name "review"
         :idBoard (:id board1)}
        add-list
        query/execute))

  (def list-todo (-> "50bcfd2f033110476000e769"
                     get-list
                     query/execute)))

(defn add-card
  "Add a card to a board"
  [card-data]
  {:method :post
   :uri "/cards/"
   :params card-data})

(comment
  (def card1
    (-> {:name "card test"
         :idList (:id list-review)}
        add-card
        query/execute)))

(defn list-cards
  [list-id]
  {:method :get
   :uri (str "/lists/" list-id "/cards/")})

(defn move-card
  [{:keys [id idList name] :as card-data}]
  {:method :put
   :uri     (str "/cards/" id)
   :params {:id id
            :name name
            :idList idList}})

(comment
  (def card1 (-> card1
                 (assoc :idList (:id list-todo))
                 (assoc :name "original name")
                 move-card
                 query/execute))
  (def card1 (-> card1
                 (assoc :idList (:id list-review))
                 (assoc :name "name card to move")
                 move-card
                 query/execute)))

(defn add-checklist
  "Add a checklist to a card"
  [{:keys [card-id name] :as checklist-data}]
  {:pre [(and card-id name)]}
  {:method :post
   :uri    (str "/cards/" card-id "/checklists")
   :params {:name name}})

(comment
  (def checklist
    (-> {:card-id (:id card1)
         :name "name-of-the-checklist"}
        add-checklist
        query/execute)))

(defn get-checklists
  [card-id]
  {:method :get
   :uri (str "/cards/" card-id "/checklists")})

(comment
  (-> (:id card1)
      get-checklists
      query/execute))

(defn get-checklist
  [id]
  {:method :get
   :uri    (str "/checklists/" id)})

(comment
  (-> (:id checklist)
      get-checklist
      query/execute))

(defn add-tasks
  "Add tasks (items) to a checklist with id 'id'"
  [{:keys [checklist-id name] :as items-data}]
  {:pre [(and checklist-id name)]}
  {:method :post
   :uri    (str "/checklists/" checklist-id "/checkItems")
   :params {:name name}})

(defn check-or-unchecked-tasks
  "Update a task"
  [{:keys [card-id checklist-id task-id state]}]
  {:method :put
   :uri (str "/cards/" card-id "/checklist/" checklist-id "/checkItem/" task-id)
   :params {:state state}})

(comment
  (def task (-> {:checklist-id (:id checklist)
                 :name "name-of-the-item"}
                add-tasks
                query/execute))

  (def task (-> {:card-id      (:id card1)
                 :task-id      (:id task)
                 :checklist-id (:id checklist)
                 :state        "complete"}
                check-or-unchecked-tasks
                query/execute)))
