(ns ca.usefulprojects.config
  (:require
   [aero.core :as aero]
   [clojure.walk :as walk]))

(defmethod aero/reader 'deep-merge
  [_opts _tag values]
  (apply merge-with (cons merge values)))

(defn read-config [profile]
  ;; setting the :type meta allows for overriding print behaviour
  (with-meta (aero/read-config "config/config.edn" {:profile profile})
    {:type ::config-map}))

(comment
  (read-config :local)
  (type (read-config :local)))

(defn- tree-leaves [t]
  (let [branch? #(or (sequential? %) (map? %))]
    (remove branch? (tree-seq branch? #(if (map? %) (vals %) %) t))))

(comment
  (tree-leaves (read-config :local)))

(defn sanitize
  "Censors any secrets in config to make it suitable for printing."
  [config]
  (let [secrets (-> (:secrets config) tree-leaves set)]
    (walk/postwalk #(if (contains? secrets %) "*****" %) config)))

(comment
  (sanitize (read-config :local)))

(defmethod print-method ::config-map [x ^java.io.Writer w]
  (.write w (print-str (-> x sanitize (vary-meta dissoc :type)))))

(comment
  ;; TODO: figure out how to extend pprint to also have sanitize behaviour
  (require '[clojure.pprint :as pprint])
  (with-out-str (pprint/pprint (read-config :local))))
