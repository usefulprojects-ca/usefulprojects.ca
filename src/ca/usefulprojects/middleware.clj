(ns ca.usefulprojects.middleware
  (:require
   [taoensso.timbre :as log]
   [hiccup.core :as hiccup]
   [ring.util.response :as response]))

(defn log-request [handler]
  (fn
    ([req]
     (log/debug (select-keys req [:request-method :uri]))
     (handler req))
    ([req respond raise]
     (log/debug (select-keys req [:request-method :uri]))
     (handler req respond raise))))

(defn merge-to-req [handler m]
  (fn
    ([req]               (handler (merge req m)))
    ([req respond raise] (handler (merge req m) respond raise))))

(defn convert-hiccup [handler]
  (letfn [(convert-hiccup-body [{:keys [body] :as resp}]
            (-> resp
                (assoc :body (hiccup/html body))
                (response/content-type "text/html; charset=UTF-8")))]
    (fn
      ([req]
       (convert-hiccup-body (handler req)))
      ([req respond raise]
       (handler req (comp respond convert-hiccup-body) raise)))))

(def registry
  {:hiccup      convert-hiccup
   :log-request log-request
   :merge       merge-to-req})
