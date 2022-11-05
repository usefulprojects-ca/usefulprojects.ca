(ns ca.usefulprojects.handler
  (:require
   [ca.usefulprojects.middleware :as middleware]
   [integrant.core :as integrant]
   [reitit.ring :as ring]))

(defn make-handler [{:keys [route-fns]}]
  (ring/ring-handler
    (ring/router (into [] cat ((apply juxt route-fns)))
                {:data {:middleware [:log-request]}
                 :reitit.middleware/registry middleware/registry})
   (ring/routes
    (ring/create-file-handler {:path "/" :root "resources/public/"})
    (ring/create-default-handler))))

(defmethod integrant/init-key ::make-handler [_k v]
  #(make-handler v))

(comment
  (def handler
    (make-handler
     {:route-fns [(constantly
                   [["/" {:get (constantly :root)}]
                    ["/test" {:get (constantly :test)}]])
                  (constantly [["/test2" {:get (constantly :test2)}]])]}))

  (handler {:request-method :get :uri "/"})
  (handler {:request-method :get :uri "/test"})
  (handler {:request-method :get :uri "/test2"})
  (handler {:request-method :get :uri "/favicon.ico"}))
