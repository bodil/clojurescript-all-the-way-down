(ns tbd.mongo)

(def mongodb (js/require "mongodb"))

(def ^:export Db (aget mongodb "Db"))
(def ^:export Server (aget mongodb "Server"))
(def ^:export Collection (aget mongodb "Collection"))
(def ^:export ObjectID (aget mongodb "ObjectID"))

(defn connect
  ([host port db callback]
     (let [server (Server. host port)]
       (.open (Db. db server) callback)))
  ([host db callback]
     (connect host 27017 db callback))
  ([db callback]
     (connect "localhost" db callback)))

(defn collection [db coll]
  (Collection. db coll))

(defn save! [coll doc callback]
  (let [doc (clj->js doc)]
    (.save coll doc callback)))

(defn find-all [coll query callback]
  (.find coll (clj->js query)
         (fn [err cursor]
           (.toArray cursor
                     (fn [err docs]
                       (callback (js->clj docs)))))))

(defn update-id! [coll id updater callback]
  (.find coll (clj->js {:_id (ObjectID. id)})
         (fn [err cursor]
           (.nextObject cursor
                        (fn [err doc]
                          (.log js/console "initial:" (str (js->clj doc)))
                          (let [doc (updater (js->clj doc))]
                            (.log js/console "updated:" (str doc))
                            (save! coll doc callback)))))))

(defn delete-id! [coll id callback]
  (let [_id (ObjectID. id)]
    (.log js/console "deleting" (.getTimestamp _id))
    (.remove coll (clj->js {:_id _id}) (clj->js {:safe true}) callback)))
