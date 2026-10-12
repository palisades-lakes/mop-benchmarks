(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.cdsbw.rectangle-one
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-11"}

  (:import
    [mop.java.geometry.delaunay.qedge LischinskiIncremental QMesh]
    [mop.java.geometry.euclidean VectorD2]
    [mop.java.jfx JFX JfxWorld]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\cdsbw\rectangle_one.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [mesh (QMesh/rectangleFrame 0.0 1.0 0.0 1.0)]
    (LischinskiIncremental/insertSite mesh (VectorD2. 0.5 0.5))
    (JFX/edges (.edges mesh #{}) JFX/MESH_COLOR "rectangle frame")))
;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
