(ns ca.usefulprojects.config
  (:require
   [aero.core :as aero]))

(defn read-config [profile]
  (aero/read-config "resources/config.edn" {:profile profile}))

(comment
  (read-config :local)
  (read-config :ci)
  (read-config :prod))
