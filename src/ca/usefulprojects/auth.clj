(ns ca.usefulprojects.auth
  (:require
   [ca.usefulprojects.middleware :as middleware]
   [ca.usefulprojects.templates :as templates]
   [integrant.core :as integrant]
   [ring.util.response :as response]))

(defn login-form [req]
  {:status 200
   :body (templates/page-with-nav req [[:p "Login form"]])})

(defn process-logout [_req]
  (response/redirect "/" :see-other))

(defn routes []
  [["/auth"
    ["/login" {:name ::login :get login-form
               :middleware [middleware/convert-hiccup]}]
    ["/logout" {:name ::logout :post process-logout}]]])

(defmethod integrant/init-key ::routes [_k _v] #'routes)
