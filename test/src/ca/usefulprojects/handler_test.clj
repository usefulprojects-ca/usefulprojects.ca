(ns ca.usefulprojects.handler-test
  (:require
   [ca.usefulprojects.handler :as sut]
   [clojure.test :refer [are deftest is testing]]))

(deftest provide-component-middleware
  (let [comps        {:a :a :b :b :c :c}
        handler      (fn [req & _] req)
        mw           (partial sut/provide-component-middleware comps handler)
        handle       (fn [ks req] ((apply mw ks) req))
        handle-async (fn [ks req] ((apply mw ks) req nil nil))]
    (are [ks req expected] (= expected (handle ks req) (handle-async ks req))
      [:a]    {}      {:a :a}
      [:b]    {}      {:b :b}
      [:a :b] {}      {:a :a :b :b}
      [:a]    {:x :x} {:a :a :x :x})))

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
