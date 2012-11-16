(ns tbd.client
  (:use [tbd.yunoincore :only [clj->js]]
        [tbd.dom :only [domready watch data target q]]
        [webfui.dom :only [defdom]]))

(def my-dom (atom nil))
(def state (atom []))
(def socket (.connect js/io "http://localhost"))

(defdom my-dom)

(defn concept-item [item]
  [:li {:class (if (item "done") "done" "open")}
   [:span.check {:data-id (item "_id")} (if (item "done") "\u2611" "\u2610")]
   [:span.delete {:data-id (item "_id")} "x"]
   [:span.todo (item "name")]])

(defn render-all [old-dom]
  [:div
   [:h1
    [:img {:src "/plthulk.jpg"}] "PLT Checklist"]
   [:ul (map concept-item @state)]
   [:form
    [:input {:type "text"}]]])

(defn update-dom []
  (swap! my-dom render-all))

(defn on-new-docs [docs]
  (let [docs (js->clj docs)]
    (reset! state docs)
    (update-dom)))

(defn on-submit [event]
  (.preventDefault event)
  (let [input (q "input")
        value (.-value input)]
    (aset input "value" "")
    (.emit socket "new" value)))

(defn on-check [event]
  (let [id (data (target event) "id")]
    (.emit socket "check" id)))

(defn on-delete [event]
  (let [id (data (target event) "id")]
    (.emit socket "delete" id)))

(domready
 (fn []
   (update-dom)
   (watch "submit" "form" on-submit)
   (watch "click" "span.check" on-check)
   (watch "click" "span.delete" on-delete)
   (.on socket "docs" on-new-docs)))
