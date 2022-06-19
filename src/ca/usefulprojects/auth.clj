(ns ca.usefulprojects.auth
  (:require
   [ca.usefulprojects.templates :refer [page]]
   [ring.util.response :as response]
   #_[xtdb.api :as xt]
   [integrant.core :as integrant]))

(defn login-form [req]
  {:status 200
   :body (page req [[:p "Login form"]])})

(defn process-logout [_req]
  (response/redirect "/" :see-other))

(defn routes []
  [["/auth"
    ["/login" {:name ::login :get login-form
               :middleware [:hiccup]}]
    ["/logout" {:name ::logout :post process-logout}]]])

(defmethod integrant/init-key ::routes [_k _v] #'routes)
