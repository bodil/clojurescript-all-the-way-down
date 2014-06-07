(ns tbd.client
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(def app-state (atom []))
(def socket (.connect js/io "http://localhost"))

(defn concept-item [item]
  (dom/li #js {:className (if (item "done") "done" "open")}
    (dom/span #js {:onClick (fn [] (.emit socket "check" (item "_id"))) :className "check"} (if (item "done") "\u2611" "\u2610"))
    (dom/span #js {:onClick (fn [] (.emit socket "delete" (item "_id"))) :className "delete"} "x")
    (dom/span #js {:className "todo"} (item "name"))))

(defn on-new-docs [docs]
  (let [docs (js->clj docs)]
    (reset! app-state docs)))

(defn todo-list [state owner]
  (reify
    om/IWillMount
    (will-mount [this]
      (.on socket "docs" on-new-docs))
    om/IRender
    (render [this]
      (dom/div nil
        (dom/h1 nil
          (dom/img #js {:src "/plthulk.jpg"} nil) "PLT Checklist")
        (apply dom/ul nil (map concept-item @app-state))
        (dom/input #js {:onKeyUp (fn [e]
                                   (if (= (.-keyCode e) 13)
                                       (let [this (.-target e)]
                                         (.emit socket "new" (.-value this))
                                         (aset this "value" ""))))
                                   :type "text"})))))

(enable-console-print!)

(om/root todo-list app-state
  {:target (.getElementById js/document "app")})
