(ns hsp.application
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.middleware :refer [new-middleware]]
            [system.components.jetty :refer [new-jetty new-web-server]]
            [system.components.postgres :refer [new-postgres-database]]
            [system.components.hikari :refer [new-hikari-cp]]
            [hsp.lib.components.ragtime-component :refer [ragtime migrate rollback]]
            [system.repl :refer [set-init! system start]]
            [hsp.config :refer [config]]
            [hsp.routes :refer [home-routes]]))

(defn app-system
  ([] (app-system config))
  ([config]
   (let [config (config)]
     (component/system-map
      :db         (new-hikari-cp (:db config))
      :ragtime    (-> (ragtime (:ragtime config))
                      (component/using [:db]))
      :routes     (-> (new-endpoint home-routes)
                      (component/using [:db]))
      :middleware (new-middleware {:middleware (:middleware config)})
      :handler    (-> (new-handler)
                      (component/using [:routes :middleware]))
      :http       (-> (new-web-server (:http-port config))
                      (component/using [:handler]))))))

(defn -main [& _]
  (set-init! #'app-system)
  (start)
  (println "Started hsp on" (str "http://localhost:" (:http-port (config)))))
