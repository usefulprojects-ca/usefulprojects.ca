(ns ca.usefulprojects.middleware-test
  (:require
   [ca.usefulprojects.middleware :as sut]
   [clojure.test :refer [deftest is testing]]))

(deftest provide-component-middleware
  (let [comps    {:a :a :b :b :c :c}
        handler  (sut/merge-to-req (fn [req & _] req) comps)
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
      (let [handler (sut/convert-hiccup (constantly response))]
        (is (= expected (handler nil)))))
    (testing "async handler"
      (let [response-atom (atom nil)
            respond-fn #(reset! response-atom %)
            handler (fn [_req respond _raise] (respond response))
            wrapped (sut/convert-hiccup handler)]
        (wrapped nil respond-fn nil)
        (is (= expected @response-atom))))))
