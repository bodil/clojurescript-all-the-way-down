(ns tbd.server
  (:require [tbd.mongo :as mongo]))

(defn log [& args] (apply (.-log js/console) (map str args)))

(def http (js/require "http"))
(def express (js/require "express"))
(def socket-io (js/require "socket.io"))

(def coll (atom nil))

(defn send-docs [socket]
  (mongo/find-all @coll {}
                  (fn [docs]
                    (.emit socket "docs" (clj->js docs)))))

(defn on-new-doc [socket data]
  (mongo/save! @coll {:name data :done false}
               #(send-docs socket)))

(defn on-delete-doc [socket data]
  (mongo/delete-id! @coll data #(send-docs socket)))

(defn on-check-doc [socket data]
  (mongo/update-id! @coll data
                    (fn [doc] (assoc doc "done" (not (doc "done"))))
                    #(send-docs socket)))

(defn server [db]
  (reset! coll (mongo/collection db "concepts"))
  (let [app (express)
        server (.createServer http app)
        io (.listen socket-io server)]
    (-> app
        (.use ((aget express "static") "static")))
    (.on (.-sockets io) "connection"
         (fn [socket]
           (.on socket "new" (partial on-new-doc socket))
           (.on socket "delete" (partial on-delete-doc socket))
           (.on socket "check" (partial on-check-doc socket))
           (send-docs socket)))
    (.listen server 1337))
  (log "u so listening on http://localhost:1337/"))

(defn main [& args]
  (mongo/connect "plt" (fn [err db] (server db))))

(set! *main-cli-fn* main)
