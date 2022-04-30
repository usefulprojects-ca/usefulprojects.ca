(ns dev
  (:require
   [ca.usefulprojects :as up]))

(def system (atom nil))

(defn restart []
  (when @system
    (up/stop @system))
  (reset! system (up/start)))

(comment
  (up/stop @system)
  (restart))
