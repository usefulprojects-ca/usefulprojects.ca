(ns ca.usefulprojects.handler-test
  (:require
   [ca.usefulprojects.handler :as sut]
   [clojure.test :refer [deftest are]]))

(deftest provide-component-middleware-test
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
