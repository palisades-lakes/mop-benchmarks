(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.cdsbw.rectangle-one
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-12"}

  (:import
    [mop.java.geometry.delaunay Util]
    [mop.java.geometry.delaunay.cdsbw CDSBWDelaunayBuilder]
    [mop.java.geometry.euclidean VectorD2]
    [mop.java.jfx JFX JfxWorld]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\cdsbw\rectangle_one.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [frame (Util/rectangleFrame 0.0 1.0 0.0 1.0)
        builder (CDSBWDelaunayBuilder/make frame)]
    (.insert builder (VectorD2. 0.5 0.5))
    (JFX/edges
      (Util/edgeCoordinates (.build builder))
      JFX/FRAME_COLOR "one")))
;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
