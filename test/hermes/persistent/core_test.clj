(ns hermes.persistent.core-test
  (:use clojure.test)
  (:require [hermes.core :as g]
            [hermes.type :as t]
            [hermes.vertex :as v])
  (:use [hermes.persistent.conf :only (conf)])
  (:import  (com.thinkaurelius.titan.graphdb.blueprints TitanInMemoryBlueprintsGraph)
            (com.thinkaurelius.titan.graphdb.database   StandardTitanGraph)
            (com.thinkaurelius.titan.graphdb.vertices   PersistStandardTitanVertex)))

(deftest test-opening-a-graph-with-conf
  (testing "Stored graph"
    (println "Make sure hbase is up and running locally.")
    (println "Be careful with types! They don't get removed or rewritten ever. ")
    (println "When you are doing the backed-by-hbase tests, always be on the look out.")
    (g/open conf)
    (is (= (type g/*graph*)
           StandardTitanGraph))))

(deftest test-simple-transaction
  (testing "Stored graph"
    (g/open conf)
    (let [vertex (g/transact! (.addVertex g/*graph*))]      
      (is (= PersistStandardTitanVertex (type vertex))))))

(deftest test-transaction-ensuring
  (testing "Stored graph"
    (g/open conf)
    (is (thrown? Throwable #"transact!" (v/create!)))))

(deftest test-dueling-transactions
  (testing "Without retries"
    (g/open conf)
    (g/transact!
      (t/create-vertex-key-once :vertex-id Long {:indexed true
                                                 :unique true}))
    (let [random-long (long (rand-int 100000))
          f1 (future (g/transact! (v/upsert! :vertex-id {:vertex-id random-long})))
          f2 (future (g/transact! (v/upsert! :vertex-id {:vertex-id random-long})))]

      (is (thrown? java.util.concurrent.ExecutionException
        (do @f1 @f2)) "The futures throw errors.")))

  (testing "With retries"
    (g/open conf)
    (g/transact!
      (t/create-vertex-key-once :vertex-id Long {:indexed true
                                                 :unique true}))
    (let [random-long (long (rand-int 100000))
          f1 (future (g/retry-transact! 3 100 (v/upsert! :vertex-id {:vertex-id random-long})))
          f2 (future (g/retry-transact! 3 100 (v/upsert! :vertex-id {:vertex-id random-long})))]

      (is (= random-long
             (g/transact!
               (v/get-property (v/refresh (first @f1)) :vertex-id))
             (g/transact!
               (v/get-property (v/refresh (first @f2)) :vertex-id))) "The futures have the correct values.")

      (is (= 1 (count
        (g/transact! (v/find-by-kv :vertex-id random-long))))
        "*graph* has only one vertex with the specified vertex-id"))))
