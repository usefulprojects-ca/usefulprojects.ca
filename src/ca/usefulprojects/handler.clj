(ns ca.usefulprojects.handler
  (:require
   [ca.usefulprojects.middleware :as middleware]
   [integrant.core :as integrant]
   [reitit.ring :as ring]))

(defprotocol RouteProvider
  (get-routes [x] "Given a value, return a vector of routes"))

(extend-protocol RouteProvider
  clojure.lang.Fn
  (get-routes [x] (x))

  clojure.lang.Var
  (get-routes [x] (deref x))

  clojure.lang.IPersistentVector
  (get-routes [x] x))

(comment
  (get-routes [:a :b :c])
  (get-routes #(vector :a :b :c))
  (def my-routes [:a :b :c])
  (get-routes #'my-routes))

;; TODO: Think of a better name for this function
(defn- make-handler-rebuilder
  "Creates a ring handler that will call `make-handler` on every request,
  then calls the handler for the request. Intended for local development."
  [make-handler]
  (fn
    ([req] ((make-handler) req))
    ([request respond raise] ((make-handler) request respond raise))))

(defn make-handler
  "Creates a ring handler out of a given collection of `route-providers`, which
  should be values that satisfy the `RouteProvider` protocol."
  [route-providers]
  (ring/ring-handler
   (ring/router (into [] (map get-routes) route-providers)
                {:data {:middleware [middleware/log-request]}})
   (ring/routes
    (ring/create-file-handler {:path "/" :root "resources/public/"})
    (ring/create-default-handler))))

(defmethod integrant/init-key ::handler
  [_k {:keys [rebuild-on-request route-providers]}]
  (if rebuild-on-request
    (make-handler-rebuilder #(#'make-handler route-providers))
    (make-handler route-providers)))

(comment
  (def handler
    (make-handler
     [(constantly
       [["/" {:get (constantly :root)}]
        ["/test" {:get (constantly :test)}]])
      [["/test2" {:get (constantly :test2)}]]]))

  (handler {:request-method :get :uri "/"})
  (handler {:request-method :get :uri "/test"})
  (handler {:request-method :get :uri "/test2"})
  (handler {:request-method :get :uri "/favicon.ico"})
  (handler {:request-method :get :uri "/doesnt-exist"}))
