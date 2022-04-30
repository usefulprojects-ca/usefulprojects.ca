(ns ca.usefulprojects.http
  (:require
   [com.stuartsierra.component :as component]
   [ring.adapter.jetty :as jetty]
   [taoensso.timbre :as log]))

(defn start-jetty [_config handler]
  (log/info "Starting HTTPServer...")
  (jetty/run-jetty handler {:port 3000 :join? false}))

(defrecord HTTPServer [handler http]
  component/Lifecycle

  (start [component]
    (if (some? (:http component))
      component
      (do
        (log/info "Starting HTTPServer...")
        (assoc component :http (start-jetty {} handler)))))

  (stop [{:keys [http] :as component}]
    (when http
      (log/info "Stopping HTTPServer...")
      (.stop http))
    component))

(defn make-server [_config handler]
  (map->HTTPServer {:handler handler}))
