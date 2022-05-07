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
      [:style {:type "text/css"}
       ;; copied shamelessly from http://bettermotherfuckingwebsite.com/
       (css [:body {:margin "40px auto"
                    :max-width "650px"
                    :line-height 1.6
                    :font-size "18px"
                    :color "#444"
                    :background-color "#ecegef"
                    :padding "0 10px"}]
            [:header {:border-bottom "1px dashed #444"}]
            [:h1 :h2 :h3 {:line-height 1.2}])]]
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
          [:li "A summary of my journey as a solver of problems using software"]
          [:li "Some of my paintings and artwork"]
          [:li "Talks and books I've learned from"]
          [:li "Cat tax"]])})

(defn routes []
  [["/" {:name ::home
         :get home}]])
