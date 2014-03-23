(ns trello-lab.playground
  "Wrapper around the trello api"
  (:use [trello-lab.api])
  (:require [trello-lab.query :as query]
            [clj-http.core :as http]))

(comment
  (def boards (-> (get-boards)
                  query/execute))

  (def user-me (-> (get-me)
                   query/execute))

  (let [member-id (:id user-me)]
    (->> boards
         ;; filter on board closed
         (filter (fn [board] (= true (get-in board [:closed]))))
         ;; now retrieve the boards id
         (map (fn [board] (try ((comp #(query/execute %) #(delete-member-board % member-id) #(get-in % [:id])) board) (catch java.lang.Exception e (println (.getMessage e))))))))

  (def board1 (->> boards
                   (filter (fn [b] (and (= (:name b) "api test board") (not (:closed b)))))
                   first))

  (def board1-full (-> board1
                       :id
                       get-board
                       query/execute))

  ;; (def list-todo
  ;;   (-> {:name "Todo"
  ;;        :idBoard (:id board1)}
  ;;       add-list
  ;;       query/execute))

  (def list-todo (->> board1
                      :id
                      lists
                      query/execute
                      (filter #(= (:name %) "TODO"))
                      first))

  (def list-doing (->> board1
                       :id
                       lists
                       query/execute
                       (filter #(= (:name %) "IN-PROGRESS"))
                       first))

  ;; (:id list-todo)  ;; "51cf0075e3c810c452000d29"
  ;; (:id list-doing) ;; "50bcfd2f033110476000e76a"

  (def card1
    (-> {:name "some card"
         :idList (:id list-todo)
         :due "2013-07-29T08:00:00.000Z"
         :desc "update description from playground"}
        add-card
        query/execute))

  ;; (card1 :id);; "51cf011238239ebc3a000626"

  (def cards (-> board1
                 :id
                 get-cards
                 query/execute))

  (def card-joy (->> cards
                     (filter #(= (:name %) "Joy of FUN(ctional) LANGUAGES"))
                     first))

  (def card1 (-> card-joy
                 (assoc :idList (:id list-doing))
                 ;; (assoc :name "some original name")
                 (assoc :due "2013-07-28T10:00:00.000Z")
                 ;; add label green
                 (update-in [:labels] conj "green")
                 update-card
                 query/execute))

  (-> {:id     (:id card1)
       :labels "red,green,yellow"}
      update-card-labels
      query/execute)

  (-> {:id     (:id card1)
       :label  "green"}
      delete-card-label
      query/execute)

  (def card1 (-> card-joy
                 (assoc :idList (:id list-todo))
                 ;; (assoc :name "some original name")
                 (assoc :due "2013-07-28T10:00:00.000Z")
                 (update-in [:labels] (fn [_] []))
                 update-card
                 query/execute))


  (def card1 (-> card-joy
                 (assoc :idList (:id list-doing))
                 (assoc :name "card moved and renamed")
                 (assoc :labels "")
                 update-card
                 query/execute))

  (def comment1 (-> card-joy
                    (assoc :comment "some comment to be put")
                    add-card-comment
                    query/execute))

  (def card1 (-> card-joy
                 (assoc :comment-id (:id comment1))
                 (assoc :comment "overwrite some comment to be put")
                 update-card-comment
                 query/execute))

  (def card-joy-from-get (-> card-joy
                             :id
                             get-card
                             query/execute))

  (def id-to-user {"some-user-id" "ardumont"})

  (map (fn [[id comment]] [(id-to-user id) comment])
       '(["some-user-id" "some comment"] ["some-user-id" "some other comment"]))

  (map
   (fn [e] [(:idMemberCreator e) (get-in e [:data :text])])
   (:actions card-with-comment))

  (def card-with-comment {:labels [{:color "green", :name "green label with & char"}]
                          :manualCoverAttachment false
                          :desc ""
                          :dateLastActivity "2014-03-20T17:46:14.417Z"
                          :idBoard "51d99bbc1e1d8988390047f2"
                          :name "Joy of FUN(ctional) LANGUAGES"
                          :idChecklists []
                          :descData nil
                          :badges {:checkItems 0
                                   :fogbugz ""
                                   :viewingMemberVoted false
                                   :attachments 0
                                   :subscribed false
                                   :checkItemsChecked 0
                                   :comments 1
                                   :votes 0
                                   :due nil
                                   :description false}
                          :idList "51d99bbc1e1d8988390047f3"
                          :closed false
                          :pos 16384
                          :url "https://trello.com/c/SvkeABjD/2729-joy-of-fun-ctional-languages"
                          :checkItemStates []
                          :shortUrl "https://trello.com/c/SvkeABjD"
                          :idAttachmentCover nil
                          :idShort 2729
                          :due nil
                          :id "532b2947a89432e147637511"
                          :actions [{:id "532b29662890a6a74786e112"
                                     :idMemberCreator "some-user-id"
                                     :data {:board {:shortLink "6JSsg3aG"
                                                    :name "api test board"
                                                    :id "51d99bbc1e1d8988390047f2"}
                                            :card {:shortLink "SvkeABjD"
                                                   :idShort 2729
                                                   :name "Joy of FUN(ctional) LANGUAGES"
                                                   :id "532b2947a89432e147637511"}
                                            :text "some comment"}
                                     :type "commentCard"
                                     :date "2014-03-20T17:46:14.420Z"
                                     :memberCreator {:id "some-user-id"
                                                     :avatarHash "ff242a6fbf51ccf70e4760b23e194bca"
                                                     :fullName "Antoine R. Dumont"
                                                     :initials "AD"
                                                     :username "ardumont"}}
                                    {:id "532b29662890a6a74786e112"
                                     :idMemberCreator "some-user-id"
                                     :data {:board {:shortLink "6JSsg3aG"
                                                    :name "api test board"
                                                    :id "51d99bbc1e1d8988390047f2"}
                                            :card {:shortLink "SvkeABjD"
                                                   :idShort 2729
                                                   :name "Joy of FUN(ctional) LANGUAGES"
                                                   :id "532b2947a89432e147637511"}
                                            :text "some other comment"}
                                     :type "commentCard"
                                     :date "2014-03-20T17:46:14.420Z"
                                     :memberCreator {:id "some-user-id"
                                                     :avatarHash "ff242a6fbf51ccf70e4760b23e194bca"
                                                     :fullName "Antoine R. Dumont"
                                                     :initials "AD"
                                                     :username "ardumont"}}]
                          :idMembers []})

  (def card1 (-> cards
                 first))

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
                query/execute))

  (def tasks (-> checklists1
                 first
                 :id
                 get-items
                 query/execute))

  (def me (-> (get-me)
              query/execute))

  (def user (-> me
                :id
                get-user
                query/execute)))
