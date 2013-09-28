(ns trello-lab.play)

(defn fib [x [a b & _ :as l]]
  (if (nil? l)
    (fib x '(1 1))
    (if (= (count l) x)
      (reverse l)
      (cons x (fib (+ a b) l) ))))

(fact (fib 3) => '(1 1 2))
(fact (fib 6) => '(1 1 2 3 5 8))
(fact (fib 8) => '(1 1 2 3 5 8 13 21))

(fn f
  ([n] (f 1 1 n))
  ([x y n]
     (if (= 0 n)
       ()
       (cons x (f y (+ x y) (- n 1))))))
