(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns mop.delaunay.qedge.rectangle
  {:doc     "Experiment with QEdge."
   :author  "palisades dot lakes at gmail dot com"
   :version "2026-10-23"}

  (:import
    [mop.java.geometry.delaunay.qedge QEdge]
    [mop.java.geometry.euclidean VectorD2]
    [mop.java.jfx JFX JfxWorld]))
;;----------------------------------------------------------------
;; mvn -q -DskipTests=true install & cljfx src\scripts\clojure\mop\delaunay\qedge\rectangle.clj
;;----------------------------------------------------------------
(defn make-world []
  (let [p0 (VectorD2. 0.0 0.0)
        p1 (VectorD2. 1.0 0.0)
        p2 (VectorD2. 1.0 1.0)
        p3 (VectorD2. 0.0 1.0)
        e01 (QEdge/make "e01" p0 p1)
        e12 (QEdge/make "e12" p1 p2)
        e20 (QEdge/make "e20" p2 p0)
        e23 (QEdge/make "e23" p2 p3)
        ]
    (.splice e12 (.reverse e01))
    (.splice e20 (.reverse e12))
    ;;(.splice (.reverse e20) (.reverse e23))
    (.printMesh e01)
    (JFX/edges (.edges e01 #{}) JFX/MESH_COLOR "rectangle frame")))
;;----------------------------------------------------------------
(System/setProperty "glass.win.uiScale" "1")
(JfxWorld/setWorldBuilder make-world)
(JfxWorld/launch JfxWorld (make-array String 0))
