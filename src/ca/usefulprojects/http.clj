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

(defn wrap-handler-rebuild-middleware
  "Creates a ring handler that will call `make-handler-fn` on every request.
  Intended for local development."
  [make-handler-fn]
  (fn
    ([req] ((make-handler-fn) req))
    ([request respond raise] ((make-handler-fn) request respond raise))))

(s/def ::port pos-int?)
(s/def ::make-handler-fn ifn?)
(s/def ::rebuild-handler-on-request (s/nilable boolean?))

(s/def ::jetty (s/keys :req-un [::port ::make-handler-fn]
                       :opt-un [::rebuild-handler-on-request]))

(defmethod ig/pre-init-spec ::jetty [_k] ::jetty)

(defmethod ig/init-key ::jetty
  [_k {:keys [port make-handler-fn rebuild-handler-on-request]}]
  (start port (if rebuild-handler-on-request
                (wrap-handler-rebuild-middleware make-handler-fn)
                (make-handler-fn))))

(defmethod ig/halt-key! ::jetty
  [_k server]
  (stop server))
