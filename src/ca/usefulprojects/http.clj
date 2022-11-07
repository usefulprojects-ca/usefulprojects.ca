(ns ca.usefulprojects.http
  (:require
   [clojure.spec.alpha :as s]
   [integrant.core :as ig]
   [ring.adapter.jetty :as jetty]
   [taoensso.timbre :as log]))

(defn start [port handler]
  (log/info "Starting HTTPServer on port" port "...")
  (jetty/run-jetty handler {:port port :join? false}))

(defn stop [server]
  (when (= (class server) org.eclipse.jetty.server.Server)
    (log/info "Stopping HTTPServer...")
    (.stop server)))

(s/def ::port pos-int?)
(s/def ::handler ifn?)
(s/def ::jetty (s/keys :req-un [::port ::handler]))

(defmethod ig/pre-init-spec ::jetty [_k] ::jetty)

(defmethod ig/init-key ::jetty
  [_k {:keys [port handler]}]
  (start port handler))

(defmethod ig/halt-key! ::jetty
  [_k server]
  (stop server))
