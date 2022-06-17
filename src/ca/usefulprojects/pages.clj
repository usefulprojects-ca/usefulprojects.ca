(ns ca.usefulprojects.pages
  (:require
   [hiccup.core :refer [html]]
   [xtdb.api :as xt]))

(defn page [_req & content]
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
      [:link {:rel "stylesheet" :href "/main.css"}]]
     [:body
      [:header
       [:h1.font-extrabold "&gt; usefulprojects.ca"]
       [:aside [:em "The humble online home of Robert Frederick Warner"]]]
      content]]))

(defn home [req]
  {:status 200
   :body
   (page req
         [:p [:em "Some content that will eventually appear here..."]]
         [:ul
          [:li "A summary of my journey as a software craftsman"]
          [:li "An index of useful talks and books with notes on what I found valuable"]
          [:li "A distillation my approach to crafting software systems"]
          [:li "A selection of my artistic output"]])})

(defn xtdb-demo [req]
  (let [data (xt/q (-> req :xtdb xt/db)
                   '{:find  [id name]
                     :keys  [id name]
                     :where [[x :xt/id id]
                             [x :name name]]
                     :order-by [[name :asc]]})]
    {:status 200
     :body
     (page req [:ul (for [{:keys [id name]} data] [:li id " " name])])}))

(defn routes []
  [["/" {:name ::home
         :get  home}]
   ["/xtdb-demo" {:name ::xtdb-demo
                  :get  xtdb-demo
                  :middleware [[:provide :xtdb]]}]])
