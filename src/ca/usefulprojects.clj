(ns ca.usefulprojects
  (:require
   [integrant.core :as ig]
   [taoensso.timbre :as log]
   [ca.usefulprojects
    [config :as config]
    [logging :as logging]]))

(defn start [config]
  (log/info "Starting system...")
  (let [ig-conf (config/->ig-conf config)
        _loaded (ig/load-namespaces ig-conf)
        system (ig/init ig-conf)]
    (logging/censor-secrets config)
    (log/info "System started.")
    system))

(defn stop! [system]
  (log/info "Stopping system...")
  (ig/halt! system)
  (log/info "System shutdown."))

(defn -main [& _args]
  (start (config/read-config :prod)))
