(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.cdsbw.triangle-uniform
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-14"}

  (:import
    [mop.java.geometry Generators]
    [mop.java.geometry.delaunay Util]
    [mop.java.geometry.delaunay.cdsbw CDSBWDelaunayBuilder]
    [mop.java.jfx JFX JfxWorld]
    [mop.java.numbers Doubles]
    [mop.java.prng PRNG]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\cdsbw\triangle_uniform.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [frame (Util/triangleFrame01)
        ;;exclude (.vertices mesh #{})
        builder (CDSBWDelaunayBuilder/make frame)
        pointGenerator (Generators/vectorD2Generator
                         (Doubles/uniformGenerator
                           (PRNG/well44497b "seeds/Well44497b-2019-01-11.txt")
                           0.0 1.0))
        nsites 64]
    (dotimes [_ nsites]
      (.insertPoint builder (.next pointGenerator)))
    (JFX/edges
      (Util/edgeCoordinates (.build builder)(.vertices (.cmplx frame)))
      JFX/MESH_COLOR "triangle uniform")))
  ;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
