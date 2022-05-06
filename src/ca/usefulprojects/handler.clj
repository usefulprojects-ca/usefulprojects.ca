(ns ca.usefulprojects.handler
  (:require
   [garden.core :refer [css]]
   [hiccup.core :refer [html]]
   [integrant.core :as ig]
   [reitit.ring :as ring]
   [taoensso.timbre :as log]))

(defn page [content]
  (html
    [:html
     [:head
      [:meta {:charset "utf-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
      [:title "usefulprojects.ca"]
      [:link {:rel "apple-touch-icon" :sizes "180x180" :href "/apple-touch-icon.png"}]
      [:link {:rel "icon" :type "image/png" :sizes "32x32" :href "/favicon-32x32.png"}]
      [:link {:rel "icon" :type "image/png" :sizes "16x16" :href "/favicon-16x16.png"}]
      [:link {:rel "manifest" :href "/site.webmanifest"}]
      [:style {:type "text/css"}
       ;; copied shamelessly from http://bettermotherfuckingwebsite.com/
       (css [:body {:margin "40px auto"
                    :max-width "650px"
                    :line-height 1.6
                    :font-size "18px"
                    :color "#444"
                    :background-color "#EEE"
                    :padding "0 10px"}]
            [:h1 :h2 :h3 {:line-height 1.2}])]]
     [:body
      [:header [:h1 "usefulprojects.ca"]]
      content]]))

(defn request-logging-middleware [handler]
  (fn [request]
    (log/debug (select-keys request [:request-method :uri]))
    (handler request)))

(defn home [_request]
  {:status 200
   :body
   (page [:p "hallo there"])})

(defn ping [_request]
  {:status 200 :body (page "pong")})

(defn make-handler []
  (ring/ring-handler
    (ring/router [["/" home]
                  ["/ping" ping]]
                 {:data {:middleware [request-logging-middleware]}})
    (ring/routes
      (ring/create-file-handler {:path "/"
                                 :root "resources/public/"})
      (ring/create-default-handler))))

(defmethod ig/init-key ::make-handler-fn [_k _v] #'make-handler)

(comment
  (def handler (make-handler))
  (handler {:request-method :get :uri "/ping"}))
