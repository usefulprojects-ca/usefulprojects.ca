(ns ca.usefulprojects
  (:require
   [com.stuartsierra.component :as component]
   [taoensso.timbre :as log]

   [ca.usefulprojects
    [app :as app]
    [config :as config]
    [http :as http]
    [xtdb :as xtdb]]))

(defn make-system []
  (component/system-map
    :http-server (http/make-server {} #'app/app)
    :xtdb (xtdb/make-node {})))

(defn configure-system
  "Merges configuration data into a constructed (but not yet started) system."
  [system profile]
  (let [config (config/read-config profile)]
    (merge-with merge system (:components config))))

(comment
  (-> (make-system)
      (configure-system :local)
      :http-server))

(defn stop [system]
  (log/info "Stopping system...")
  (let [system (component/stop-system system)]
    (log/info "System shutdown stopped.")
    system))

(defn start [profile]
  (log/info "Starting system...")
  (let [system (-> (make-system)
                   (configure-system profile)
                   component/start-system)]
    (log/info "System startup complete.")
    system))

(defn -main [& _args]
  (start :prod))
