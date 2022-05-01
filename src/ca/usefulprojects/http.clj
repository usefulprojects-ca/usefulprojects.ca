(ns ca.usefulprojects.http
  (:require
   [com.stuartsierra.component :as component]
   [ring.adapter.jetty :as jetty]
   [taoensso.timbre :as log]))

(defn start [port handler]
  (log/info "Starting HTTPServer on port" port "...")
  (jetty/run-jetty handler {:port port :join? false}))

(defn stop [server]
  (log/info "Stopping HTTPServer...")
  (.stop server))

(defn dynamic-handler
  "Creates a ring handler that will call `make-handler` on every request.
  Intended for local development."
  [make-handler]
  (fn
    ([req] ((make-handler) req))
    ([request respond raise] ((make-handler) request respond raise))))

(defrecord HTTPServer [make-handler port handler-type http handler]
  component/Lifecycle

  (start [component]
    (if (some? (:http component))
      component
      (let [handler (if (= handler-type :dynamic)
                      (dynamic-handler make-handler)
                      (make-handler))]
        (assoc component :http (start port handler)))))

  (stop [{:keys [http] :as component}]
    (when http
      (stop http))
    component))
