(ns hsp.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(defonce app-state (atom {:text "“It is spiritless to think that you cannot attain to that which you have seen and heard the masters attain. The masters are men. You are also a man. If you think that you will be inferior in doing something, you will be on that road very soon.”"}))

(defn greeting []
  [:h2 (:text @app-state)])

(defn render []
  (reagent/render [greeting] (js/document.getElementById "app")))
