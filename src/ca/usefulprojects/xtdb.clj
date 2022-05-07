(ns ca.usefulprojects.xtdb
  (:require
   [integrant.core :as ig]
   [xtdb.api :as xt]
   [xtdb.node :as xt.node]
   [taoensso.timbre :as log]
   [clojure.pprint :as pprint])
  (:import
   (xtdb.node XtdbNode)))

(defn start [{:keys [db-spec pool-opts]}]
  (log/info "Starting XTDB node...")
  (let [node (xt/start-node
              {:xtdb.jdbc/connection-pool
               {:dialect   {:xtdb/module 'xtdb.jdbc.psql/->dialect}
                :pool-opts pool-opts
                :db-spec   db-spec}

               :xtdb/tx-log
               {:xtdb/module     'xtdb.jdbc/->tx-log
                :connection-pool :xtdb.jdbc/connection-pool}

               :xtdb/document-store
               {:xtdb/module     'xtdb.jdbc/->document-store
                :connection-pool :xtdb.jdbc/connection-pool}})
        f (future (xt/sync node))]
    (log/info "Waiting for XTDB tx sync...")
    (while (not (realized? f))
      (Thread/sleep 1000)
      (when-some [indexed (xt/latest-completed-tx node)]
        (log/info "Indexed" (pr-str indexed))))
    (log/info "XTDB startup complete.")
    node))

(defn halt! [node]
  (when (instance? java.io.Closeable node)
    (log/info "Stopping XTDB node...")
    (.close node)))

(defmethod ig/init-key ::node [_k opts] (start opts))
(defmethod ig/halt-key! ::node [_k node] (halt! node))

(defmethod print-method XtdbNode [_x ^java.io.Writer w]
  (.write w "<XtdbNode>"))

(defmethod pprint/simple-dispatch XtdbNode [_x]
  (print "<XtdbNode>"))
