(ns ca.usefulprojects.handler
  (:require
   [reitit.ring :as ring]
   [taoensso.timbre :as log]
   [integrant.core :as ig]))

(defn request-logging-middleware [handler]
  (fn [request]
    (log/debug (select-keys request [:request-method :uri]))
    (handler request)))

(defn ping [_request]
  {:status 200 :body "pong"})

(defn make-handler []
  (ring/ring-handler
    (ring/router ["/ping" ping]
                 {:data {:middleware [request-logging-middleware]}})
    (ring/create-default-handler)))

(defmethod ig/init-key ::make-handler-fn [_k _v] make-handler)

(comment
  (def handler (make-handler))
  (handler {:request-method :get :uri "/ping"}))
