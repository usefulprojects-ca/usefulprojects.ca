(ns ca.usefulprojects.config
  (:require
   [aero.core :as aero]
   [clojure.pprint :as pprint]
   [clojure.walk :as walk]))

(defmethod aero/reader 'deep-merge
  [_opts _tag values]
  (apply merge-with (cons merge values)))

;; a record is used primarily for simple overriding of print/pprint behaviour
(defrecord Config [secrets overrides])

(defn read-config [profile]
  (map->Config (aero/read-config "config/config.edn" {:profile profile})))

(comment
  (read-config :local)
  (type (read-config :local))
  (class (read-config :local)))

(defn- tree-leaves [t]
  (let [branch? #(or (sequential? %) (map? %))]
    (remove branch? (tree-seq branch? #(if (map? %) (vals %) %) t))))

(comment
  (tree-leaves (read-config :local)))

(defn sanitize
  "Censors any secrets in config to make it suitable for printing.
  Returns a Map instead of a Config record."
  [config]
  (let [secrets (-> (:secrets config) tree-leaves set)]
    (walk/postwalk #(if (contains? secrets %) "*****" %) (into {} config))))

(comment
  (sanitize (read-config :local)))

(defmethod print-method Config [x ^java.io.Writer w]
  (.write w (print-str (sanitize x))))

(defmethod pprint/simple-dispatch Config [x]
  (pprint/pprint (sanitize x)))

(comment
  (with-out-str (print (read-config :local)))
  (with-out-str (pprint/pprint (read-config :local))))
