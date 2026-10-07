(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.qedge.rectangle-uniform
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-06"}

  (:require
    [mop.commons.time :as mct])
  (:import
    [mop.java.geometry Generators]
    [mop.java.geometry.delaunay.qedge LischinskiIncremental QMesh]
    [mop.java.jfx JFX JfxWorld]
    [mop.java.numbers Doubles]
    [mop.java.prng PRNG]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\qedge\rectangle_uniform.clj
;;----------------------------------------------------------------
(defn make-world []
  (mct/seconds
    "total"
    (let [mesh (QMesh/rectangleFrame 0.0 1.0 0.0 1.0)
          exclude (.vertices mesh #{})
          pointGenerator (Generators/vectorD2Generator
                           (Doubles/uniformGenerator
                             (PRNG/well44497b "seeds/Well44497b-2019-01-11.txt")
                             0.0 1.0))
          nsites (* 64 1024)]
      (mct/seconds
        (str "insert: " nsites)
        (dotimes [_ nsites]
          (LischinskiIncremental/insertSite mesh (.next pointGenerator))))
      (mct/seconds "jfx"
        (JFX/edges (.edges mesh exclude) JFX/MESH_COLOR "rectangle uniform")))))
  ;;----------------------------------------------------------------
  (System/setProperty "glass.win.uiScale" "1")
  (JfxWorld/setWorldBuilder make-world)
  (JfxWorld/launch JfxWorld (make-array String 0))
