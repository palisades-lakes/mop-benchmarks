(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.qedge.triangle-nonconvex
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-04"}

  (:import
    [mop.java.geometry.delaunay.qedge LischinskiIncremental QMesh]
    [mop.java.geometry.euclidean VectorD2]
    [mop.java.jfx JFX JfxWorld]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\qedge\triangle_nonconvex.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [p0 (VectorD2. 0.0 0.0)
        p1 (VectorD2. 2.0 0.0)
        p2 (VectorD2. 0.0 2.0)
        mesh (QMesh/triangleFrame p0 p1 p2)
        exclude (.vertices mesh #{})
        sites [(VectorD2. 0.5 0.25)
               (VectorD2. 0.5 0.75)
               (VectorD2. 0.25 0.5)
               (VectorD2. 0.75 0.5)
               (VectorD2. 0.25 0.2)
               (VectorD2. 0.75 0.2)]]
    (doseq [site sites] (LischinskiIncremental/insertSite mesh site))
    (JFX/edges (.edges mesh exclude) JFX/MESH_COLOR "triangle frame")))
    ;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
