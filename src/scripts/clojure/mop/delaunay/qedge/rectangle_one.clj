(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.qedge.rectangle-one
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-25"}

  (:import
    [mop.java.geometry.delaunay Util]
    [mop.java.geometry.delaunay.qedge LischinskiIncrementalBuilder]
    [mop.java.geometry.euclidean VectorD2]
    [mop.java.jfx JFX JfxWorld] ))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\qedge\rectangle_one.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [builder (LischinskiIncrementalBuilder/rectangleFrame 0.0 1.0 0.0 1.0)
        exclude #{}]
    (.insertPoint builder (VectorD2. 0.5 0.5))
    (JFX/edges (Util/edgeCoordinates (.build builder) exclude)
      JFX/MESH_COLOR "rectangle-one")))
;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
