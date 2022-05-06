(ns ca.usefulprojects.router
  (:require
   [integrant.core :as ig]
   [reitit.ring :as ring]
   [taoensso.timbre :as log]
   [ca.usefulprojects.pages :as pages]))

(defn request-logging-middleware [handler]
  (fn [req]
    (log/debug (select-keys req [:request-method :uri]))
    (handler req)))

(defn make-router []
  (ring/ring-handler
    (ring/router (pages/routes)
                 {:data {:middleware [request-logging-middleware]}})
    (ring/routes
      (ring/create-file-handler {:path "/" :root "resources/public/"})
      (ring/create-default-handler))))

(defmethod ig/init-key ::router [_k _v] #'make-router)

(comment
  ((make-router) {:request-method :get :uri "/"})
  ((make-router) {:request-method :get :uri "/favicon.ico"}))
