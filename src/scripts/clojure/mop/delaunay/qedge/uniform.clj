(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.qedge.uniform
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-04"}

  (:import
    [mop.java.geometry Generators]
    [mop.java.geometry.delaunay.qedge QMesh]
    [mop.java.jfx JfxWorld]
    [mop.java.numbers Doubles]
    [mop.java.prng PRNG]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\clean\uniform.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [mesh (QMesh/rectangleFrame 0.0 1.0 0.0 1.0)
        pointGenerator (Generators/vectorD2Generator
                         (Doubles/uniformGenerator
                           (PRNG/well44497b "seeds/Well44497b-2019-01-11.txt")
                           0.0 1.0))
        nsites 8]
    (dotimes [_ nsites] (.insertSite mesh (.next pointGenerator)))
    (.jfxTriangles mesh true "uniform 0 1")))
;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
