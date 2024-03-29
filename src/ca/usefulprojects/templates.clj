(ns ca.usefulprojects.templates)

(defn page [_req body]
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
   (into [:body.m-8] body)])

(defn page-with-nav [req main]
  (page req
        [[:header.mb-4
          [:h1.font-extrabold "&gt; usefulprojects.ca"]
          [:p "Improving our world by building useful projects"]]
         [:nav
          [:ul
           [:li "Consulting services"]
           [:li "Blog"]]]
         [:main main]]))
