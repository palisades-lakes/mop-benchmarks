(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.hedge.rectangle-frame
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-11-05"}

  (:import
    [java.util Arrays]
    [mop.java.geometry.delaunay Util]
    [mop.java.geometry.delaunay.hedge LischinskiIncrementalBuilder]
    [mop.java.jfx JFX JfxWorld]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\hedge\rectangle_frame.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [builder (LischinskiIncrementalBuilder/rectangleFrame 0.0 1.0 0.0 1.0)
        _ (.swap (.startingEdge builder))
        edgeCoords (Util/edgeCoordinates (.build builder))]
    (println (.startingEdge builder))
    (println "edges")
    (doseq [e edgeCoords]
      (println (Arrays/toString ^objects e)))
    (JFX/edges edgeCoords JFX/MESH_COLOR "rectangle frame")))
;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
