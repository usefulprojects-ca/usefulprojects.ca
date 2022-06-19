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

(defn merge-middleware [handler m]
  (fn
    ([req]               (handler (merge req m)))
    ([req respond raise] (handler (merge req m) respond raise))))

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

(def middleware-registry
  {:hiccup      hiccup-response-middleware
   :log-request log-request-middleware
   :merge       merge-middleware})

(defn make-handler [{:keys [route-fns]}]
  (ring/ring-handler
   (ring/router (into [] cat ((apply juxt route-fns)))
                {:data {:middleware [:log-request]}
                 :reitit.middleware/registry middleware-registry})
   (ring/routes
    (ring/create-file-handler {:path "/" :root "resources/public/"})
    (ring/create-default-handler))))

(defmethod integrant/init-key ::make-handler [_k v]
  #(make-handler v))

(comment
  (def handler (make-handler
                {:route-fns [#(vector
                               ["/" {:get (constantly :root)}]
                               ["/test" {:get (constantly :test)}])
                             #(vector
                               ["/test2" :get (constantly :test2)])]}))

  (handler {:request-method :get :uri "/"})
  (handler {:request-method :get :uri "/favicon.ico"})
  (handler {:request-method :get :uri "/auth/login"}))
