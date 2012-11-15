(ns tbd.dom)

(defn domready [handler]
  (.addEventListener js/window "DOMContentLoaded" handler))

(defn target [event] (.-target event))

(defn- el-in-nodelist [l el]
  (some #(= el %) (map #(.item l %) (range (.-length l)))))

(defn watch [event selector handler]
  (let [filter-on-selector
        (fn [e]
          (let [target (target e)
                candidates (.querySelectorAll js/document selector)]
            (when (el-in-nodelist candidates target)
              (handler e))))]
    (.addEventListener js/document event filter-on-selector)))

(defn data [el key]
  (.getAttribute el (str "data-" key)))
