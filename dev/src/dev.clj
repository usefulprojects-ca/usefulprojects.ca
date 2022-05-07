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
  ;; evaluate this on startup if you are using reveal
  (do
    (require '[vlaaad.reveal :as r]
             'xtdb.node)
    (import '(xtdb.node XtdbNode))

    (r/defstream XtdbNode [_]
      (r/raw-string "<XtdbNode>"))))

(comment
  (restart)
  (up/stop! @system))
