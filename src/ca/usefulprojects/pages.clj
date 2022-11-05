(ns ca.usefulprojects.pages
  (:require
   [ca.usefulprojects.templates :as templates]
   [integrant.core :as integrant]
   #_[xtdb.api :as xt]))

(defn home [req]
  {:status 200
   :body
   (templates/page-with-nav
    req
    ["test"]

    )})

#_(defn xtdb-demo [req]
    (let [data (xt/q (-> req :xtdb xt/db)
                     '{:find  [id name]
                       :keys  [id name]
                       :where [[x :xt/id id]
                               [x :name name]]
                       :order-by [[name :asc]]})]
      {:status 200
       :body
       (templates/page req [[:ul (for [{:keys [id name]} data] [:li id " " name])]])}))

(defn routes [deps]
  [["/" {:name ::home
         :get  home
         :middleware [:hiccup]}]
   #_["/xtdb-demo" {:name ::xtdb-demo
                    :get  xtdb-demo
                    :middleware [[:hiccup] [:merge deps]]}]])

(defmethod integrant/init-key ::routes [_k v]
  #(routes v))
