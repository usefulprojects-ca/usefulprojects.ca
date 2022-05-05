(ns dev
  (:require
   [ca.usefulprojects :as up]
   [ca.usefulprojects.config :as config]))

(defonce system (atom nil))

(defn restart []
  (when @system
    (up/stop! @system))
  (reset! system (up/start (config/read-config :local))))

(comment
  (restart)
  (up/stop! @system))
