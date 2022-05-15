(ns ca.usefulprojects.pages
  (:require
   [garden.core :refer [css]]
   [hiccup.core :refer [html]]))

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
       [:h1 "&gt; usefulprojects.ca"]
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
          [:li "An attempt to distill my approach to crafting software systems"]
          [:li "A selection of my artistic output"]])})

(defn routes []
  [["/" {:name ::home
         :get home}]])
