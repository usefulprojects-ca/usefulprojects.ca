(ns dev
  (:require
   [ca.usefulprojects :as up]))

(def system (atom nil))

(defn restart []
  (when @system
    (up/stop @system))
  (reset! system (up/start :local)))

(comment
  (restart)
  (up/stop @system))
