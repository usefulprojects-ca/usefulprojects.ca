(ns ca.usefulprojects.config
  (:require
   [aero.core :as aero]))

(defmethod aero/reader 'deep-merge
  [_opts _tag values]
  (apply merge-with (cons merge values)))

(defn read-config [profile]
  (aero/read-config "config/config.edn" {:profile profile}))

(comment
  (read-config :local)
  (read-config :ci)
  (read-config :prod))
