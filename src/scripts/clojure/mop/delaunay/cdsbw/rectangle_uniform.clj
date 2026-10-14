(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.cdsbw.rectangle-uniform
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-14"}

  (:require
    [mop.commons.time :as mct])
  (:import
    [mop.java.geometry Generators]
    [mop.java.geometry.delaunay Util]
    [mop.java.geometry.delaunay.cdsbw CDSBWDelaunayBuilder]
    [mop.java.jfx JFX JfxWorld]
    [mop.java.numbers Doubles]
    [mop.java.prng PRNG]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\cdsbw\rectangle_uniform.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [frame (Util/rectangleFrame 0.0 1.0 0.0 1.0)
        builder (CDSBWDelaunayBuilder/make frame)
        pointGenerator (Generators/vectorD2Generator
                         (Doubles/uniformGenerator
                           (PRNG/well44497b "seeds/Well44497b-2019-01-11.txt")
                           0.0 1.0))
        nsites 64 #_(* 64 1024)]
    (mct/seconds
      (str "insert: " nsites)
      (dotimes [_ nsites] (.insertPoint builder (.next pointGenerator))))
    (mct/seconds "jfx"
      (JFX/edges
        (Util/edgeCoordinates (.build builder) (.vertices (.cmplx frame)))
        JFX/MESH_COLOR "rectangle uniform"))))
  ;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
