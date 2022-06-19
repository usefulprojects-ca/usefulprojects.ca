(ns ca.usefulprojects.handler-test
  (:require
   [ca.usefulprojects.handler :as sut]
   [clojure.test :refer [deftest is testing]]))

(deftest provide-component-middleware
  (let [comps    {:a :a :b :b :c :c}
        handler  (sut/merge-middleware (fn [req & _] req) comps)
        req {:x :x}
        expected (merge req comps)]
    (testing "synchronous" (is (= expected (handler req))))
    (testing "asynchronous" (is (= expected (handler req nil nil))))))

(deftest hiccup-response-middleware
  (let [response {:status 200 :body [:p "Test"]}
        expected {:headers {"Content-Type" "text/html; charset=UTF-8"}
                  :body    "<p>Test</p>"
                  :status  200}]
    (testing "synchronous handler"
      (let [handler (sut/hiccup-response-middleware (constantly response))]
        (is (= expected (handler nil)))))
    (testing "async handler"
      (let [response-atom (atom nil)
            respond-fn #(reset! response-atom %)
            handler (fn [_req respond _raise] (respond response))
            wrapped (sut/hiccup-response-middleware handler)]
        (wrapped nil respond-fn nil)
        (is (= expected @response-atom))))))
