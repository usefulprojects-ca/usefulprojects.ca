(ns ca.usefulprojects.handler
  (:require
   [ca.usefulprojects
    [auth :as auth]
    [pages :as pages]]
   [integrant.core :as integrant]
   [reitit.ring :as ring]
   [taoensso.timbre :as log]))

(defn log-request-middleware [handler]
  (fn
    ([req]
     (log/debug (select-keys req [:request-method :uri]))
     (handler req))
    ([req respond raise]
     (log/debug (select-keys req [:request-method :uri]))
     (handler req respond raise))))

(defn provide-component-middleware [system handler & ks]
  (let [provided (select-keys system ks)
        provide  (fn [req] (merge req provided))]
    (fn
      ([req]               (handler (provide req)))
      ([req respond raise] (handler (provide req) respond raise)))))

(defn make-handler [components]
  (ring/ring-handler
   (ring/router (into [] cat [(pages/routes)
                              (auth/routes)])
    {:data {:middleware [:log-request]}
     :reitit.middleware/registry
     {:provide (partial provide-component-middleware components)
      :log-request log-request-middleware}})
   (ring/routes
    (ring/create-file-handler {:path "/" :root "resources/public/"})
    (ring/create-default-handler))))

(defmethod integrant/init-key ::make-handler [_k v]
  (partial make-handler (:provides v)))

(comment
  ((make-handler nil) {:request-method :get :uri "/"})
  ((make-handler nil) {:request-method :get :uri "/favicon.ico"})
  ((make-handler nil) {:request-method :get :uri "/xtdb-demo"})
  ((make-handler nil) {:request-method :get :uri "/auth/login"}))
