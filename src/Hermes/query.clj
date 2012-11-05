(ns hermes.query
  (:import (com.tinkerpop.blueprints Direction))
  (:require [hermes.vertex :as v])
  (:use [hermes.core :only (*graph*)]))

(defmacro defquery [name & body]
  `(defn ~name [vertex#]
     (let [query# (if (= com.thinkaurelius.titan.graphdb.query.SimpleTitanQuery (type vertex#))
                    vertex#
                    (.query vertex#))
           results# (-> query# ~@body)]
       results#)))

(def out-flag Direction/OUT)
(def in-flag Direction/IN)
(def both-flag Direction/BOTH)

(defmacro def-directed-query [name direction]
  `(defn ~name
     ([q#]          (-> q#
                       (.labels (into-array String []))
                       (.direction ~direction)))
       ([q# & labels#] (-> q#
                    (.labels (into-array String labels#))
                    (.direction ~direction)))))

(def-directed-query --> out-flag)
(def-directed-query <-- in-flag)
(def-directed-query --- both-flag)


(defn V! [q]
  (seq (.vertexIds q)))

(defn properties! [q]
  (map v/prop-map (V! q)))



