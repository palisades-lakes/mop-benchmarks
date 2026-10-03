(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.clean.one
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-03"}

  (:import
    [javafx.scene.paint Color]
    [mop.java.geometry.delaunay.clean Subdivision]
    [mop.java.geometry.euclidean VectorD2]
    [mop.java.jfx JfxWorld]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\clean\one.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [p0 (VectorD2. -1.0 -1.0)
        p1 (VectorD2.  2.0 -1.0)
        p2 (VectorD2.  0.5, 3.0)
        mesh (Subdivision. p0 p1 p2)]
    (.insertSite mesh (VectorD2.  0.0  0.0))
    (.jfxTriangles mesh (Color/web "#000088FF") "one")))
;;----------------------------------------------------------------
;;(println (System/getProperty "glass.win.uiScale"))
(System/setProperty "glass.win.uiScale" "1")
;;(println (System/getProperty "glass.win.uiScale"))
;;(System/setProperty "javafx.pulseLogger" "true")
;;(System/setProperty "prism.verbose" "true")
;;(System/setProperty "prism.order" "d3d")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
