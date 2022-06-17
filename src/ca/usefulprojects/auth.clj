(ns ca.usefulprojects.auth
  (:require
   [ca.usefulprojects.pages :refer [page]]
   [ring.util.response :as response]
   #_[xtdb.api :as xt]))

(defn login-form [req]
  {:status 200
   :body (page req [:p "Login form"])})

(defn process-logout [_req]
  (response/redirect "/" :see-other))

(defn routes []
  [["/auth"
    ["/login" {:name ::login :get login-form}]
    ["/logout" {:name ::logout :post process-logout}]]])
