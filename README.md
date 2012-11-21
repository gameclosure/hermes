<img src="http://upload.wikimedia.org/wikipedia/en/c/cb/FuturamaHermesConrad.png"  alt="Hermes" title="Hermes" align="right" height=/>


# Hermes
>That's a calculator. I ate it to gain its power.
>-[Hermes Conrad](https://www.youtube.com/watch?v=AkA0fYfT-vI)

A Clojure library designed to make it easy to work with embedded
[Titan](http://thinkaurelius.github.com/titan/) graphs. Hermes is very
much a work in progress. Titan is a young project and has yet to even
hit 0.2. Hermes will probably always be a work in progress until Titan
hits 1.0. So, while we have tested this and used it in production 
ourselves, in general use this in production at your own peril.

To get started, add this dependency to your `project.clj`:

``` clojure
[hermes "0.2.2"]
```

## Example
``` clojure
(ns hermes.example
  (:require [hermes.core   :as g]
            [hermes.vertex :as v]
            [hermes.edge   :as e]
            [hermes.type   :as t]))

(g/open {:storage {:backend "cassandra"
                   :hostname "127.0.0.1"}})


(g/transact! (t/create-vertex-key-once :name String {:unique true
                                                     :indexed true}))

(def Zack (g/transact! (first (v/upsert! :name
                                         {:name "Zack"
                                          :age 21
                                          :gender "Male"
                                          :occupation "INTERN"}))))

(def Brooke (g/transact! (first (v/upsert! :name
                                           {:name "Brooke"
                                            :age 19
                                            :gender "Female"
                                            :occupation "Student"}))))

(g/transact! (e/connect! (v/refresh Zack)
                         (v/refresh Brooke)
                         "siblings" {:since 1991}))

;;Oops mistake, should probably fix that
(g/transact! (e/upconnect! (first (v/find-by-kv :name "Zack"))
                           (v/refresh Brooke)
                           "siblings" {:since 1993}))

;;And well, I am really more than just an intern
(g/transact! (v/upsert! :name {:name "Zack"
                               :occupation "Software specialist and all around nice guy"}))

(defn -main [& args]
  ["Hello World!"
   (g/transact! (doall (for [vertex [Zack Brooke]]
                         (-> vertex
                             v/refresh
                             v/prop-map) )))])
;;["Hello World!" ({:id 4, :occupation "Software specialist and all around nice guy", :age 21, :name "Zack", :gender "Male"} {:id 40004, :occupation "Student", :age 19, :name "Brooke", :gender "Female"})]
```


## License

Copyright © 2012 Game Closure

Distributed under the MIT license. 

## Contact

Yell at [Zack](http://www.twitter.com/ZackMaril) if it breaks. Sometimes, we take over #hermes 
on freenode, an old IRC channel that used to have what looks to be a somewhat active physics 
discussion happening back in 2007. 

