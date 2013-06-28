(ns trello-lab.test.api
  (:use [midje.sweet]
        [trello-lab.api]))

(fact
  (get-boards) => {:method :get
                   :uri    "/members/me/boards"})

(fact
  (get-board :id) => {:method :get
                      :uri    "/boards/:id"})

(fact
  (get-cards :id) => {:method :get
                      :uri    "/boards/:id/cards"})

(fact
  (get-card :id) => {:method :get
                     :uri    "/cards/:id"})

(fact
  (lists :id) => {:method :get
                  :uri    "/boards/:id/lists"})

(fact
  (get-list :id) => {:method :get
                     :uri    "/lists/:id"})

(fact
  (add-list {:name    :n
             :idBoard :i}) => {:method :post
                               :uri    "/lists/"
                               :params {:name    :n
                                        :idBoard :i}})

(fact
  (add-card :card-data) => {:method :post
                            :uri    "/cards/"
                            :params :card-data})

(fact
  (list-cards :id) => {:method :get
                       :uri    "/lists/:id/cards/"})

(fact
  (move-card {:id :id
              :name :n
              :idList :i}) => {:method :put
                               :uri     "/cards/:id"
                               :params {:id :id
                                        :name :n
                                        :idList :i}})
