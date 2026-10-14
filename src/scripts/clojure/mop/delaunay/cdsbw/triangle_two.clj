(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.cdsbw.triangle-two
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-14"}

  (:import
    [mop.java.geometry.delaunay Util]
    [mop.java.geometry.delaunay.cdsbw CDSBWDelaunayBuilder]
    [mop.java.geometry.euclidean VectorD2]
    [mop.java.jfx JFX JfxWorld]))
;;----------------------------------------------------------------
;; mvn -q install & cljfx src\scripts\clojure\mop\delaunay\cdsbw\triangle_two.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [frame (Util/triangleFrame01)
        builder (CDSBWDelaunayBuilder/make frame)]
    (println (str "0th\n" (.toString builder)))
    ;;(println (str "mesh\n" (.toString (.build builder))))
    (println)
    (.insertPoint builder (VectorD2. 0.25 0.25))
    (println (str "1st\n" (.toString builder)))
    ;;(println (str "mesh\n" (.toString (.build builder))))
    (println)
    (.insertPoint builder (VectorD2. 0.75 0.75))
    (println (str "2nd\n" (.toString builder)))
    ;;(println (str "mesh\n" (.toString (.build builder))))
  (JFX/edges (Util/edgeCoordinates (.build builder))
      JFX/FRAME_COLOR "two")))
;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
