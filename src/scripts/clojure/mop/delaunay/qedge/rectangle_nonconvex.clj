(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.qedge.rectangle-nonconvex
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-04"}

  (:import
    [mop.java.geometry.delaunay.qedge QMesh]
    [mop.java.geometry.euclidean VectorD2]
    [mop.java.jfx JfxWorld]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\qedge\rectangle_nonconvex.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [mesh (QMesh/rectangleFrame 0.0 1.0 0.0 1.0)]
    (.insertSite mesh (VectorD2. 0.5 0.25))
    (.insertSite mesh (VectorD2. 0.5 0.75))
    (.insertSite mesh (VectorD2. 0.25 0.5))
    (.insertSite mesh (VectorD2. 0.75 0.5))
    (.insertSite mesh (VectorD2. 0.25 0.2))
    (.insertSite mesh (VectorD2. 0.75 0.2))
    (.jfxTriangles mesh false "rectangle 2")))
;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
