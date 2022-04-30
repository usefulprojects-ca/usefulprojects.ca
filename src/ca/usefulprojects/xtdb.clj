(ns ca.usefulprojects.xtdb
  (:require
   [com.stuartsierra.component :as component]))

(defn start-node [_config]
  )

(defn stop-node [_node]
  )

(defrecord XTDB [node]
  component/Lifecycle

  (start [{:keys [node] :as component}]
    (if (some? node)
      component
      (assoc component :node (start-node {}))))

  (stop [{:keys [node] :as component}]
    (when (some? node)
      (stop-node node))
    component))

(defn make-node [_config]
  (map->XTDB {}))
