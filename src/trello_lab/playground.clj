(ns trello-lab.playground
  "Wrapper around the trello api"
  (:use [trello-lab.api])
  (:require [trello-lab.query :as query]
            [clj-http.core :as http]))

(comment
  (def boards (-> (get-boards)
                  query/execute))
  (def board1 (->> boards
                   (filter (fn [b] (and (= (:name b) "api test board") (not (:closed b)))))
                   first))

  (def list-todo
    (-> {:name "Todo"
         :idBoard (:id board1)}
        add-list
        query/execute))

  (def list-todo (->> board1
                      :id
                      lists
                      query/execute
                      (filter #(= (:name %) "Todo"))
                      first))

  (def list-doing (->> board1
                       :id
                       lists
                       query/execute
                       (filter #(= (:name %) "Doing"))
                       first))

  ;; (:id list-todo)  ;; "51cf0075e3c810c452000d29"
  ;; (:id list-doing) ;; "50bcfd2f033110476000e76a"

  (def card1
    (-> {:name "card test"
         :idList (:id list-todo)}
        add-card
        query/execute))

  ;; (card1 :id);; "51cf011238239ebc3a000626"

  (def card1 (-> card1
                 (assoc :idList (:id list-todo))
                 (assoc :name "some original name")
                 move-card
                 query/execute))

  (def card1 (-> card1
                 (assoc :idList (:id list-doing))
                 (assoc :name "card moved and renamed")
                 move-card
                 query/execute))

  (def checklist
    (-> {:card-id (:id card1)
         :name "my first checklist"}
        add-checklist
        query/execute))

  (def checklists1
    (-> (:id card1)
        get-checklists
        query/execute))

  (def task (-> {:checklist-id (:id checklist)
                 :name "my first task (item in trello)"}
                add-tasks
                query/execute))

  (def task (-> {:card-id      (:id card1)
                 :task-id      (:id task)
                 :checklist-id (:id checklist)
                 :state        "incomplete"}
                check-or-unchecked-tasks
                query/execute)))
