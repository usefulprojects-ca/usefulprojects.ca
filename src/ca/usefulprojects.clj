(ns ca.usefulprojects
  (:require
   [com.stuartsierra.component :as component]
   [taoensso.timbre :as log]

   [ca.usefulprojects
    [app :as app]
    [http :as http]
    [xtdb :as xtdb]]))

(defn make-system []
  (component/system-map
    :http-server (http/make-server {} #'app/app)
    :xtdb (xtdb/make-node {})))

(defn stop [system]
  (log/info "Stopping system...")
  (component/stop-system system)
  (log/info "System stopped."))

(defn start []
  (log/info "Starting system...")
  (let [system (component/start-system (make-system))]
    (log/info "System started.")
    system))

(defn -main [& _args]
  (start))
