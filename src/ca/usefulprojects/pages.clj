(ns ca.usefulprojects.pages
  (:require
   [xtdb.api :as xt]
   [ca.usefulprojects.templates :as templates]
   [integrant.core :as integrant]))

(defn home [req]
  {:status 200
   :body
   (templates/page
    req
    [[:p [:em "Some content that will eventually appear here..."]]
     [:ul
      [:li "A summary of my journey as a software craftsman"]
      [:li "An index of useful talks and books with notes on what I found valuable"]
      [:li "A distillation my approach to crafting software systems"]
      [:li "A selection of my artistic output"]]])})

(defn xtdb-demo [req]
  (let [data (xt/q (-> req :xtdb xt/db)
                   '{:find  [id name]
                     :keys  [id name]
                     :where [[x :xt/id id]
                             [x :name name]]
                     :order-by [[name :asc]]})]
    {:status 200
     :body
     (templates/page req [:ul (for [{:keys [id name]} data] [:li id " " name])])}))

(defn routes []
  [["/" {:name ::home
         :get  home
         :middleware [:hiccup]}]
   ["/xtdb-demo" {:name ::xtdb-demo
                  :get  xtdb-demo
                  :middleware [[:provide :xtdb]]}]])

(defmethod integrant/init-key ::routes [_k _v] #'routes)
