(ns ca.usefulprojects.handler
  (:require
   [hiccup.core :as hiccup]
   [integrant.core :as integrant]
   [reitit.ring :as ring]
   [ring.util.response :as response]
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

(defn hiccup-response-middleware [handler]
  (letfn [(convert-hiccup-body [{:keys [body] :as resp}]
            (-> resp
                (assoc :body (hiccup/html body))
                (response/content-type "text/html; charset=UTF-8")))]
    (fn
      ([req]
       (convert-hiccup-body (handler req)))
      ([req respond raise]
       (handler req (comp respond convert-hiccup-body) raise)))))

(defn make-handler [{:keys [route-fns provides]}]
  (ring/ring-handler
    (ring/router (into [] cat ((apply juxt route-fns)))
    {:data {:middleware [:log-request]}
     :reitit.middleware/registry
     {:provide (partial provide-component-middleware provides)
      :log-request log-request-middleware
      :hiccup hiccup-response-middleware}})
   (ring/routes
    (ring/create-file-handler {:path "/" :root "resources/public/"})
    (ring/create-default-handler))))

(defmethod integrant/init-key ::make-handler [_k v]
  #(make-handler v))

(comment
  ((make-handler nil) {:request-method :get :uri "/"})
  ((make-handler nil) {:request-method :get :uri "/favicon.ico"})
  ((make-handler nil) {:request-method :get :uri "/xtdb-demo"})
  ((make-handler nil) {:request-method :get :uri "/auth/login"}))
