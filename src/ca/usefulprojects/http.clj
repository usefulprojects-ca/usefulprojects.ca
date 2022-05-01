(ns ca.usefulprojects.http
  (:require
   [com.stuartsierra.component :as component]
   [ring.adapter.jetty :as jetty]
   [taoensso.timbre :as log]))

(defn start-jetty [port handler]
  (log/info "Starting HTTPServer...")
  (jetty/run-jetty handler {:port port :join? false}))

(defrecord HTTPServer [handler port http]
  component/Lifecycle

  (start [component]
    (if (some? (:http component))
      component
      (do
        (log/info "Starting HTTPServer on port" port "...")
        (assoc component :http (start-jetty port handler)))))

  (stop [{:keys [http] :as component}]
    (when http
      (log/info "Stopping HTTPServer...")
      (.stop http))
    component))

(defn make-server [_config handler]
  (map->HTTPServer {:handler handler}))
