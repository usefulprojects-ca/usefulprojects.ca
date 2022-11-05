(ns ca.usefulprojects.config
  (:require
   [aero.core :as aero]
   [clojure.pprint :as pprint]
   [clojure.walk :as walk]
   [integrant.core :as ig]))

(defmethod aero/reader 'ig/ref [_opts _tag value]
  (ig/ref value))

;; a record is used to make it easier to override print/pprint
(defrecord Config [])

(defn read-config [profile]
  (with-meta
    (map->Config (aero/read-config "config/config.edn" {:profile profile}))
    {::profile profile}))

(defn ->ig-conf
  "Returns configuration suitable for feeding to integrant."
  [config]
  (into {} (dissoc config :secrets :environment)))

(defn- leaves [t]
  (remove coll? (tree-seq coll? #(if (map? %) (vals %) %) t)))

(defn secrets
  "Returns a set containing any secrets found in `config`"
  [config]
  (-> config :secrets leaves set))

(defn censor
  "Masks any secrets in config to make it suitable for printing."
  [secrets value]
  (walk/postwalk #(if (contains? secrets %) "*****" %) value))

(defmethod print-method Config [x ^java.io.Writer w]
  (.write w (str (into {} (censor (secrets x) x)))))

(defmethod pprint/simple-dispatch Config [x]
  (pprint/pprint (into {} (censor (secrets x) x))))

;; TODO: for truly comprehensive print/pprint censorship, we'd need to check
;; *everything* being printed for secrets.

(comment
  (read-config :local)
  (->ig-conf (read-config :local))
  (with-out-str (print (read-config :local)))
  (with-out-str (pprint/pprint (read-config :local))))
