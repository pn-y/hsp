(defproject hsp "0.1.0-SNAPSHOT"
  :description "Patient database"
  :url "https://infinite-temple-14905.herokuapp.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [ring "1.6.3"]
                 [ring/ring-defaults "0.3.1"]
                 [bk/ring-gzip "0.2.1"]
                 [ring/ring-json "0.4.0"]
                 [radicalzephyr/ring.middleware.logger "0.6.0"]
                 [compojure "1.6.0"]
                 [environ "1.1.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [org.danielsz/system "0.4.1"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [reagent "0.7.0"]
                 [lambdaisland/garden-watcher "0.3.2"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [honeysql "0.9.1"]
                 [org.postgresql/postgresql "42.1.4"]
                 [cheshire "5.8.0"]
                 [ragtime "0.7.2"]
                 [org.clojure/core.match "0.3.0-alpha5"]
                 [clj-http "3.7.0"]
                 [org.slf4j/slf4j-simple "1.7.9"]
                 [hikari-cp "2.0.1"]]

  :plugins [[lein-ancient "0.6.15"]
            [lein-cljsbuild "1.1.6"]
            [lein-environ "1.1.0"]
            [lein-ring "0.12.3"]]

  :min-lein-version "2.6.1"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :test-paths ["test/clj" "test/cljc"]

  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js"]

  :uberjar-name "hsp.jar"

  ;; Use `lein run` if you just want to start a HTTP server, without figwheel
  :main hsp.application

  ;; nREPL by default starts in the :main namespace, we want to start in `user`
  ;; because that's where our development helper functions like (go) and
  ;; (browser-repl) live.
  :repl-options {:init-ns user}

  :cljsbuild {:builds
              [{:id "app"
                :source-paths ["src/cljs" "src/cljc" "dev"]

                :figwheel {:on-jsload "hsp.system/reset"}

                :compiler {:main cljs.user
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/hsp.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}

               {:id "test"
                :source-paths ["src/cljs" "test/cljs" "src/cljc" "test/cljc"]
                :compiler {:output-to "resources/public/js/compiled/testable.js"
                           :main hsp.test-runner
                           :optimizations :none}}

               {:id "min"
                :source-paths ["src/cljs" "src/cljc"]
                :jar true
                :compiler {:main hsp.system
                           :output-to "resources/public/js/compiled/hsp.js"
                           :output-dir "target"
                           :source-map-timestamp true
                           :optimizations :advanced
                           :pretty-print false}}]}

  ;; When running figwheel from nREPL, figwheel will read this configuration
  ;; stanza, but it will read it without passing through leiningen's profile
  ;; merging. So don't put a :figwheel section under the :dev profile, it will
  ;; not be picked up, instead configure figwheel here on the top level.

  :figwheel {;; :http-server-root "public"       ;; serve static assets from resources/public/
             ;; :server-port 3449                ;; default
             ;; :server-ip "127.0.0.1"           ;; default
             :css-dirs ["resources/public/css"]  ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process. We
             ;; don't do this, instead we do the opposite, running figwheel from
             ;; an nREPL process, see
             ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
             ;; :nrepl-port 7888

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             :server-logfile "log/figwheel.log"}

  :doo {:build "test"}

  :profiles {:dev
             {:dependencies [[figwheel "0.5.14"]
                             [figwheel-sidecar "0.5.14"]
                             [com.cemerick/piggieback "0.2.2"]
                             [org.clojure/tools.nrepl "0.2.13"]
                             [lein-doo "0.1.8"]
                             [reloaded.repl "0.2.4"]]

              :plugins [[lein-figwheel "0.5.11"]
                        [lein-doo "0.1.7"]]

              :source-paths ["dev"]
              :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}

             :uberjar
             {:source-paths ^:replace ["src/clj" "src/cljc"]
              :prep-tasks ["compile"
                           ["cljsbuild" "once" "min"]
                           ["run" "-m" "garden-watcher.main" "hsp.styles"]]
              :hooks []
              :omit-source true
              :aot :all}}

  :aliases {"migrate"  ["run" "-m" "user/migrate"]
            "rollback" ["run" "-m" "user/rollback"]})
