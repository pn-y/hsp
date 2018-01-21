(ns user
  (:require [hsp.application]
            [environ.core :refer [env]]
            [com.stuartsierra.component :as component]
            [figwheel-sidecar.config :as fw-config]
            [figwheel-sidecar.system :as fw-sys]
            [clojure.tools.namespace.repl :refer [set-refresh-dirs]]
            [reloaded.repl :refer [system init]]
            [ring.middleware.reload :refer [wrap-reload]]
            [figwheel-sidecar.repl-api :as figwheel]
            [garden-watcher.core :refer [new-garden-watcher]]
            [hsp.config :refer [config]]
            [hsp.test-helpers :refer [test-config]]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]))

(defn current-env-config []
  (if (= (env :clj-env) "test")
    (test-config)
    (config)))

(defn dev-system []
  (assoc (hsp.application/app-system current-env-config)
         :figwheel-system (fw-sys/figwheel-system (fw-config/fetch-config))
         :css-watcher (fw-sys/css-watcher {:watch-paths ["resources/public/css"]})
         :garden-watcher (new-garden-watcher ['hsp.styles])))

(set-refresh-dirs "src" "dev")
(reloaded.repl/set-init! #(dev-system))

(defn cljs-repl []
  (fw-sys/cljs-repl (:figwheel-system system)))

;; Set up aliases so they don't accidentally
;; get scrubbed from the namespace declaration
(def start reloaded.repl/start)
(def stop reloaded.repl/stop)
(def go reloaded.repl/go)
(def reset reloaded.repl/reset)
(def reset-all reloaded.repl/reset-all)

(defn load-config []
  {:datastore  (jdbc/sql-database (env :database-url))
   :migrations (jdbc/load-resources (get-in (current-env-config) [:ragtime :resource-path]))})

(defn migrate []
  (repl/migrate (load-config)))

(defn rollback []
  (repl/rollback (load-config)))
