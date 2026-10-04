(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.qedge.one
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-04"}

  (:import
    [mop.java.geometry.delaunay.qedge LischinskiIncremental QMesh]
    [mop.java.geometry.euclidean VectorD2]
    [mop.java.jfx JFX JfxWorld]))
;;----------------------------------------------------------------
;; mvn -q install & cljfx src\scripts\clojure\mop\delaunay\qedge\one.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [p0 (VectorD2. -1.0 -1.0)
        p1 (VectorD2.  2.0 -1.0)
        p2 (VectorD2.  0.5, 3.0)
        mesh (QMesh/triangleFrame p0 p1 p2)]
    (LischinskiIncremental/insertSite mesh (VectorD2.  0.0  0.0))
    (JFX/edges (.edges mesh #{}) JFX/FRAME_COLOR "one")))
;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
