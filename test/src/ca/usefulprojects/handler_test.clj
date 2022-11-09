(ns ca.usefulprojects.handler-test
  (:require
   [ca.usefulprojects.handler :as sut]
   [clojure.test :refer [are deftest is testing]]
   [clojure.java.io :as io]))

(deftest get-routes
  (let [routes [:a :b :c]]
    (testing "works with functions"
      (is (= routes (sut/get-routes (constantly routes)))))
    (testing "works with vectors"
      (is (= routes (sut/get-routes routes))))
    (testing "works with vars"
      (intern 'ca.usefulprojects.handler-test 'var-test-routes routes)
      #_{:clj-kondo/ignore [:unresolved-symbol]}
      (is (= routes (sut/get-routes #'var-test-routes))))))

(deftest make-handler
  (let [handler (sut/make-handler
                 [[["/" {:get (constantly :root)}]
                    ["/test" {:get (constantly :test)}]]
                  [["/test2" {:get (constantly :test2)}]]])
        call-handler #(handler {:request-method :get :uri %})]
    (testing "expected routes work"
      (are [uri expected] (= expected (call-handler uri))
        "/" :root
        "/test" :test
        "/test2" :test2))
    (testing "404 returned on router miss"
      (is (= 404 (:status (call-handler "/does-not-exist")))))
    (testing "resources are served"
      ;; TODO: this test depends on a favicon being present.
      ;; add an assertion to verify it's there
      (let [{:keys [status headers body]} (call-handler "/favicon.ico")]
        (is (= 200 status))
        (is (= "image/x-icon" (get headers "Content-Type")))
        (is (= java.io.File (type body)))))))
