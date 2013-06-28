(ns trello-lab.test.query
  (:use [midje.sweet]
        [trello-lab.query]))

(fact
  (compute-url URL "/members/ardumont" "consumer-key")                => "https://api.trello.com/1/members/ardumont?key=consumer-key"
  (compute-url URL "/members/ardumont" "consumer-key" "secret-token") => "https://api.trello.com/1/members/ardumont?key=consumer-key&token=secret-token")

(fact
  (execute {:method :get
           :uri    :some-uri
           :params :some-params}) => :res
  (provided
    (api :get :some-uri :some-params) => :res))

(fact
  (execute {:method :post
           :uri    :some-uri
           :params :some-params}) => :res
  (provided
    (post :some-uri :some-params) => :res))

(fact
  (execute {:method :put
           :uri    :some-uri
           :params :some-params}) => :res
  (provided
    (put :some-uri :some-params) => :res))
