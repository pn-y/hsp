(ns hsp.routes
  (:require [clojure.java.io :as io]
            [compojure.core :refer [GET PATCH POST routes context]]
            [compojure.route :refer [not-found resources]]
            [ring.util.response :refer [response]]
            [hsp.controllers.patients :as patients]))

(defn home-routes [{db :db}]
  (routes
   (GET "/" _
     (-> "public/index.html"
         io/resource
         io/input-stream
         response
         (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))
   (context "/patients" []
     (GET "/" [] (patients/index db))
     (GET "/:id" [id] (patients/show db id))
     (POST "/" {body :body} (patients/create db body))
     (PATCH "/:id" [id :as {body :body}] (patients/update-patient db id body)))
   (resources "/")
   (not-found {:status 404 :body {}})))
