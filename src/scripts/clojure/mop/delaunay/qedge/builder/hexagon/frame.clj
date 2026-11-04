(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.qedge.builder.hexagon.frame
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-11-03"}

  (:import
    [java.util Arrays]
    [mop.java.geometry.delaunay Util]
    [mop.java.geometry.delaunay.qedge LischinskiIncrementalBuilder]
    [mop.java.jfx JFX JfxWorld]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\qedge\builder\hexagon\frame.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [frame (Util/hexagonFrame 0.0 1.0 0.0 1.0)
        builder (LischinskiIncrementalBuilder/make frame)
        edgeCoords (Util/edgeCoordinates (.build builder))]
    (println "edges")
    (doseq [e edgeCoords]
      (println (Arrays/toString ^objects e)))
    (JFX/edges edgeCoords JFX/MESH_COLOR "hexagon frame")))
;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
