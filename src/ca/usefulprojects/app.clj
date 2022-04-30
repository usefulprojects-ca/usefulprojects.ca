(ns ca.usefulprojects.app
  (:require
   [reitit.ring :as ring]
   [taoensso.timbre :as log]))

(defn request-logging-middleware [handler]
  (fn [request]
    (log/debug (select-keys request [:request-method :uri]))
    (handler request)))

(defn ping [_request]
  {:status 200 :body "pong!"})

(def app
  (ring/ring-handler
    (ring/router ["/ping" ping]
                 {:data {:middleware [request-logging-middleware]}})
    (ring/create-default-handler)))

(comment
  (app {:request-method :get :uri "/ping"}))
