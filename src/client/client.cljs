(ns tbd.client
  (:require [reagent.core :as reagent :refer [atom]]))

(def app-state (atom []))
(def socket (.connect js/io "http://localhost"))

(defn concept-item [item]
  [:li {:class (if (item "done") "done" "open")}
    [:span {:on-click (fn [] (.emit socket "check" (item "_id"))) :class "check"} (if (item "done") "\u2611" "\u2610")]
    [:span {:on-click (fn [] (.emit socket "delete" (item "_id"))) :class "delete"} "x"]
    [:span {:class "todo"} (item "name")]])

(defn on-new-docs [docs]
  (let [docs (js->clj docs)]
    (reset! app-state docs)))

(defn add-item-input []
  [:input {:type "text"
           :on-key-up (fn [e]
                        (if (= (.-keyCode e) 13)
                          (let [this (.-target e)]
                            (.emit socket "new" (.-value this))
                            (aset this "value" ""))))}])

(defn todo-list []
  [:div nil
   [:h1
    [:img {:src "/plthulk.jpg"} "PLT Checklist"]]
   [:ul (map concept-item @app-state)]
   (add-item-input)])

(enable-console-print!)

(.on socket "docs" on-new-docs)

(reagent/render-component [todo-list]
  (.getElementById js/document "app"))
