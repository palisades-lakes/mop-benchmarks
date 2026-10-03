(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.clean.uniform
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-03"}

  (:import
    [javafx.scene.paint Color]
    [mop.java.geometry Generators]
    [mop.java.geometry.delaunay.clean Subdivision]
    [mop.java.geometry.euclidean VectorD2]
    [mop.java.jfx JfxWorld] [mop.java.numbers Doubles] [mop.java.prng PRNG]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\clean\uniform.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [p0 (VectorD2. 0.0 0.0)
        p1 (VectorD2. 2.0 0.0)
        p2 (VectorD2. 0.0, 2.0)
        mesh (Subdivision. p0 p1 p2)
        pointGenerator (Generators/vectorD2Generator
                         (Doubles/uniformGenerator
                           (PRNG/well44497b "seeds/Well44497b-2019-01-11.txt")
                           0.0 1.0))
        nsites 16]
    (dotimes [i nsites] (.insertSite mesh (.next pointGenerator)))

         (.jfxTriangles mesh (Color/web "#000088FF") "uniform 0 1")))
;;----------------------------------------------------------------
;;(println (System/getProperty "glass.win.uiScale"))
(System/setProperty "glass.win.uiScale" "1")
;;(println (System/getProperty "glass.win.uiScale"))
;;(System/setProperty "javafx.pulseLogger" "true")
;;(System/setProperty "prism.verbose" "true")
;;(System/setProperty "prism.order" "d3d")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
