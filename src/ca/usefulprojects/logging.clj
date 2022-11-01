(ns ca.usefulprojects.logging
  (:require [integrant.core :as ig]
            [taoensso.timbre :as log]
            [ca.usefulprojects.config :as config]))

(defmethod ig/init-key ::timbre
  [_k config]
  (log/merge-config! config))

(defn censor-secrets
  "Adds middleware to Timbre to prevent leaking secrets via logs.
  Maintains one middleware per config profile, determined from config meta."
  [conf]
  (let [secrets (config/secrets conf)
        profile   (-> conf meta ::profile)
        censor-fn (with-meta
                    #(config/censor secrets %)
                    {::profile profile})
        match-fn  #(= profile (-> % meta ::profile))
        update-fn #(conj (remove match-fn %) censor-fn)]

    (alter-var-root #'log/*config* update :middleware update-fn)))

(comment
  (log/debug "consider_me_compromised"))
