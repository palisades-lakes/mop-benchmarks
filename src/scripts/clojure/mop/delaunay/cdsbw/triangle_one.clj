(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.cdsbw.triangle-one
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-12"}

  (:import
    [mop.java.geometry.delaunay Util]
    [mop.java.geometry.delaunay.cdsbw CDSBWDelaunayBuilder]
    [mop.java.geometry.euclidean VectorD2]
    [mop.java.jfx JFX JfxWorld]))
;;----------------------------------------------------------------
;; mvn -q install & cljfx src\scripts\clojure\mop\delaunay\cdsbw\triangle_one.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [frame (Util/triangleFrame01)
        builder (CDSBWDelaunayBuilder/make frame)]
    (.insertPoint builder (VectorD2. 0.5 0.5))
    (JFX/edges
      (Util/edgeCoordinates (.build builder))
      JFX/FRAME_COLOR "one")))
;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
