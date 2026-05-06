;; clj src/scripts/clojure/nzqr/scripts/doc/codox.clj
(set! *warn-on-reflection* false)
(set! *unchecked-math* false)
;;----------------------------------------------------------------
(ns nzqr.scripts.doc.codox
  
  {:doc "Generate codox for nzqr.

   <b>TODO: codox font choices?"
   :author "palisades dot lakes at gmail dot com"
   :version "2021-04-23"}
  
  (:require [clojure.java.io :as io]
            [codox.main :as codox]))
;;----------------------------------------------------------------
(let [version "0.0.0"
      project-name "nzqr"
      description "Supporting code for working thru SICP, SICM, etc"
      options {:name project-name
               :version version 
               :description description
               :language :clojure
               :root-path (io/file "./")
               :output-path "docs/codox"
               :source-paths ["src/main/clojure"]
               :source-uri (str "https://github.com/palisades-lakes/"
                                project-name
                                "/tree/"
                                project-name
                                "-{version}/{filepath}#L{line}")
               :namespaces :all
               :doc-paths ["docs"]
               :doc-files ["README.md"]
               :html {:namespace-list :flat}
               :metadata {:doc "TODO: write docs"
                          :doc/format :markdown}
               :themes [:hyperlegible #_:default]}]
  (codox/generate-docs options))
;;----------------------------------------------------------------

