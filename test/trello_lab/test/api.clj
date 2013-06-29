(ns trello-lab.test.api
  (:use [midje.sweet]
        [trello-lab.api]))

(fact
  (get-boards) => {:method :get
                   :uri    "/members/me/boards"}

  (get-board :id) => {:method :get
                      :uri    "/boards/:id"}

  (get-cards :id) => {:method :get
                      :uri    "/boards/:id/cards"}

  (get-card :id) => {:method :get
                     :uri    "/cards/:id"}

  (lists :id) => {:method :get
                  :uri    "/boards/:id/lists"}

  (get-list :id) => {:method :get
                     :uri    "/lists/:id"}

  (add-list {:name    :n
             :idBoard :i}) => {:method :post
                               :uri    "/lists/"
                               :params {:name    :n
                                        :idBoard :i}}

  (add-card :card-data) => {:method :post
                            :uri    "/cards/"
                            :params :card-data}

  (list-cards :id) => {:method :get
                       :uri    "/lists/:id/cards/"}

  (move-card {:id :id
              :name :n
              :idList :i}) => {:method :put
                               :uri     "/cards/:id"
                               :params {:name :n
                                        :idList :i}})
