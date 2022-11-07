(ns ca.usefulprojects.handler
  (:require
   [ca.usefulprojects.middleware :as middleware]
   [integrant.core :as integrant]
   [reitit.ring :as ring]))

(defn- wrap-rebuild-handler-middleware
  "Creates a ring handler that will call `make-handler` on every request.
  Intended for local development."
  [make-handler]
  (fn
    ([req] ((make-handler) req))
    ([request respond raise] ((make-handler) request respond raise))))

(defn make-handler [route-fns]
  (ring/ring-handler
   (ring/router (reduce #(into %1 (%2)) [] route-fns)
                {:data {:middleware [middleware/log-request]}})
   (ring/routes
    (ring/create-file-handler {:path "/" :root "resources/public/"})
    (ring/create-default-handler))))

(defmethod integrant/init-key ::handler
  [_k {:keys [rebuild-on-request route-fns]}]
  (if rebuild-on-request
    (wrap-rebuild-handler-middleware #(#'make-handler route-fns))
    (make-handler route-fns)))

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
