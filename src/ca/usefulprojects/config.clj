(ns ca.usefulprojects.config
  (:require
   [aero.core :as aero]
   [clojure.pprint :as pprint]
   [clojure.walk :as walk]
   [integrant.core :as ig]))

(defmethod aero/reader 'ig/ref [_opts _tag value]
  (ig/ref value))

;; a record is used to enable simple overriding of print/pprint behaviour
(defrecord Config [])

(defn read-config [profile]
  (map->Config (aero/read-config "config/config.edn" {:profile profile})))

(comment
  (read-config :local))

(defn ->ig-conf
  "Returns configuration suitable for feeding to integrant."
  [config]
  (into {} (dissoc config :secrets :overrides)))

(comment
  (->ig-conf (read-config :local)))

(defn- config-values [t]
  (let [branch? #(or (sequential? %) (map? %))]
    (remove branch? (tree-seq branch? #(if (map? %) (vals %) %) t))))

(comment
  (config-values (read-config :local)))

(defn sanitize
  "Censors any secrets in config to make it suitable for printing.
  Returns a Map instead of a Config record."
  [config]
  (let [secrets (-> (:secrets config) config-values set)]
    (walk/postwalk #(if (contains? secrets %) "*****" %) (into {} config))))

(comment
  (sanitize (read-config :local)))

(defmethod print-method Config [x ^java.io.Writer w]
  (.write w (print-str (sanitize x))))

(defmethod pprint/simple-dispatch Config [x]
  (pprint/pprint (sanitize x)))

;; TODO: print sanitizing works if we're logging a config directly, but further
;; work is needed at the logging level to check for secret leaks

(comment
  (with-out-str (print (read-config :local)))
  (with-out-str (pprint/pprint (read-config :local))))
