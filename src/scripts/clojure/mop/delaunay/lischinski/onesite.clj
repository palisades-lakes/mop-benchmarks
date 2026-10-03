(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.lischinski.onesite
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-03"}

  (:import
    [javafx.scene.paint Color]
    [mop.java.geometry.delaunay.lischinski Subdivision]
    [mop.java.geometry.euclidean VectorD2]
    [mop.java.jfx JfxWorld]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\lischinski.clj
;;----------------------------------------------------------------
(defn make-world []
  (println "make-world")
  (flush)
  (let [p0 (VectorD2. -1.0 -1.0)
        p1 (VectorD2.  2.0 -1.0)
        p2 (VectorD2.  0.5, 3.0)
        mesh (Subdivision. p0 p1 p2)
        _ (println "mesh")
        _ (flush)
        p (VectorD2.  0.0  0.0)
        _ (.InsertSite mesh p)
        _ (println "inserted")
        _(flush)
        triangles (.jfxTriangles mesh (Color/web "#000088FF"))]
    (println "world" (.size (.getChildren triangles)))
    (flush)
    (.setId triangles "lischinski")
    triangles))
;;----------------------------------------------------------------
;;(println (System/getProperty "glass.win.uiScale"))
(System/setProperty "glass.win.uiScale" "1")
;;(println (System/getProperty "glass.win.uiScale"))
;;(System/setProperty "javafx.pulseLogger" "true")
;;(System/setProperty "prism.verbose" "true")
;;(System/setProperty "prism.order" "d3d")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
