(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.gg4.triangle-nonconvex
  {:doc     "Use JavaFX to display a delaunay triangulation."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-04"}

  (:import
    [javafx.scene.paint Color]
    [mop.java.geometry.delaunay.gg4 Subdivision]
    [mop.java.geometry.euclidean VectorD2]
    [mop.java.jfx JfxWorld]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\lischinski\triangle_nonconvex.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [p0 (VectorD2. 0.0 0.0)
        p1 (VectorD2. 2.0 0.0)
        p2 (VectorD2. 0.0 2.0)
        mesh (Subdivision. p0 p1 p2)
        _(.InsertSite mesh (VectorD2. 0.5 0.25))
        _(.InsertSite mesh (VectorD2. 0.5 0.75))
        _(.InsertSite mesh (VectorD2. 0.25 0.5))
        _(.InsertSite mesh (VectorD2. 0.75 0.5))
        _(.InsertSite mesh (VectorD2. 0.25 0.2))
        _(.InsertSite mesh (VectorD2. 0.75 0.2))
        triangles (.jfxTriangles mesh (Color/web "#000088FF"))]
    (println "world" (.size (.getChildren triangles)))
    (flush)
    (.setId triangles "lischinski")
    triangles))
;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
