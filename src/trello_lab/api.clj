(ns trello-lab.api
  "Wrapper around the trello api"
  (:require [trello-lab.query :as query]))

(defn get-me "Retrieve one's informations."
  []
  {:method :get
   :uri "/members/me"})

(defn get-boards
  "Retrieve the boards of the current user."
  []
  {:method :get
   :uri    "/members/me/boards"})

(defn get-board
  "Retrieve the boards of the current user."
  [id]
  {:method :get
   :uri    (format "/boards/%s?memberships=active&memberships_member=true" id)})

(defn get-cards
  "cards of a board"
  [board-id]
  {:method :get
   :uri    (str "/boards/" board-id "/cards")})

(defn get-card
  "Detail of a card with id card-id."
  [card-id]
  {:method :get
   :uri    (format "/cards/%s?actions=commentCard" card-id)})

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

(defn add-card
  "Add a card to a board"
  [card-data]
  {:method :post
   :uri "/cards/"
   :params card-data})

(defn list-cards
  [list-id]
  {:method :get
   :uri (str "/lists/" list-id "/cards/")})

(defn update-card
  [{:keys [id idList name due labels] :as card-data}]
  {:method :put
   :uri     (str "/cards/" id)
   :params {:name name
            :idList idList
            :due due
            :labels labels}})

(defn update-card-labels
  [{:keys [id labels]}]
  {:method :put
   :uri     (format "/cards/%s/labels" id)
   :params {:value labels}})

(defn delete-card-label
  [{:keys [id label]}]
  {:method :delete
   :uri     (format "/cards/%s/labels/%s" id label)})

(defn add-checklist
  "Add a checklist to a card"
  [{:keys [card-id name] :as checklist-data}]
  {:pre [(and card-id name)]}
  {:method :post
   :uri    (str "/cards/" card-id "/checklists")
   :params {:name name}})

(defn get-checklists
  [card-id]
  {:method :get
   :uri (str "/cards/" card-id "/checklists")})

(defn get-checklist
  [id]
  {:method :get
   :uri    (str "/checklists/" id)})

(defn get-items
  [checklist-id]
  {:method :get
   :uri    (str "/checklists/" checklist-id "/checkItems/")})

(defn get-item
  [checklist-id check-item-id]
  {:method :get
   :uri    (str "/checklists/" checklist-id "/checkItems/" check-item-id)})

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

(defn get-user "Retrieve the user."
  [user-id]
  {:method :get
   :uri (str "/members/" user-id)})
