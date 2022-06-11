(ns ca.usefulprojects.router
  (:require
   [integrant.core :as ig]
   [reitit.ring :as ring]
   [taoensso.timbre :as log]
   [ca.usefulprojects.pages :as pages]))

(defn request-logging-middleware [handler]
  (fn
    ([req]
     (log/debug (select-keys req [:request-method :uri]))
     (handler req))
    ([req respond raise]
     (log/debug (select-keys req [:request-method :uri]))
     (handler req respond raise))))

(defn provide-middleware [system handler & ks]
  (let [provided (select-keys system ks)
        provide  (fn [req] (merge req provided))]
    (fn
      ([req]               (handler (provide req)))
      ([req respond raise] (handler (provide req) respond raise)))))

(defn make-router [provided-components]
  (ring/ring-handler
   (ring/router
    (pages/routes)
    {:data {:middleware [request-logging-middleware]}
     :reitit.middleware/registry
     {:provide (partial provide-middleware provided-components)}})
   (ring/routes
    (ring/create-file-handler {:path "/" :root "resources/public/"})
    (ring/create-default-handler))))

(defmethod ig/init-key ::router [_k v] (partial make-router (:provides v)))

(comment
  ((make-router nil) {:request-method :get :uri "/"})
  ((make-router nil) {:request-method :get :uri "/favicon.ico"})
  ((make-router nil) {:request-method :get :uri "/xtdb-demo"}))
