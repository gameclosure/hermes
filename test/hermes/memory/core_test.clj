(ns hermes.memory.core-test
  (:use clojure.test)
  (:require [hermes.core :as g])
  (:import  (com.thinkaurelius.titan.graphdb.blueprints TitanInMemoryBlueprintsGraph)
            (com.thinkaurelius.titan.graphdb.database   StandardTitanGraph)
            (com.thinkaurelius.titan.graphdb.vertices   PersistStandardTitanVertex)
            (com.thinkaurelius.titan.core               TitanFactory)))

(deftest test-opening-a-graph-in-memory
  (testing "Graph in memory"
    (g/open)
    (is (= (type g/*graph*)
           TitanInMemoryBlueprintsGraph))))

(deftest test-with-graph
  (testing "with-graph macro"
    ; Open the usual *graph*
    (g/open)
    ; Open a real graph the hard wary
    (let [graph (TitanFactory/openInMemoryGraph)]
      (g/with-graph graph
        (.addVertex graph))
      (is (= 1 (count (seq (.getVertices graph)))) "graph has the new vertex")
      (is (= 0 (count (seq (.getVertices g/*graph*)))) "the usual *graph* is still empty"))))

