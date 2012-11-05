(ns hermes.example
  (:require [hermes.core   :as g]
            [hermes.vertex :as v]
            [hermes.edge   :as e])
  (:use hermes.query))

(g/open)
(v/index-on "name")

(def Zack (v/create {:name "Zack"
                     :age "21"
                     :gender "Male"
                     :occupation "INTERN"}))

(def Brooke (v/create {:name "Brooke"
                       :age "19"
                       :gender "Female"
                       :occupation "Student"}))

(def Cindy (v/create {:name "Cindy"
                      :occupation "Saleswoman"}))

(def Steve (v/create {:name "Steve"
                      :occupation "Salesmen"}))

;;Direction? 
(e/create Zack Brooke "siblings")

(e/create Steve Cindy  "married")

(e/create Zack Cindy  "child")
(e/create Zack Steve  "child")

(e/create Brooke Cindy  "child")
(e/create Brooke Steve  "child")

(defn a-test-query [vertex]
  (.. vertex
      query
      (labels (into-array String ["siblings"]))
      (labels (into-array String ["child"]))
      vertexIds
      ))

(defquery siblings-with
  (<-- "siblings"))

(defquery child-of
  (--> "child"))

(defquery find-parents-of-siblings
  siblings-with
  child-of
  properties!)