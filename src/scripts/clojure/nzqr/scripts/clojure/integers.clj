;; clj src/scripts/clojure/nzqr/scripts/clojure/integers.clj
(set! *warn-on-reflection* true)
(set! *unchecked-math* false)
;;----------------------------------------------------------------
(ns nzqr.scripts.clojure.integers
  
  {:doc "Check out clojure integer arithmetic."
   :author "palisades dot lakes at gmail dot com"
   :version "2021-10-25"}
  
  (:require [clojure.java.io :as io]
            [nzqr.commons.core :as ncc])
  (:import [java.math BigInteger]))
;;----------------------------------------------------------------
(defn print-value-and-type [x]
  (println (type x) ":" x))
;;----------------------------------------------------------------
(defn print-projections [^BigInteger n]
  (print-value-and-type (.byteValue n))
  (print-value-and-type (.shortValue n))
  (print-value-and-type (.intValue n))
  (print-value-and-type (.longValue n))
  ;  (print-value-and-type (.byteValueExact n))
  ;  (print-value-and-type (.shortValueExact n))
  ;  (print-value-and-type (.intValueExact n))
  ;  (print-value-and-type (.longValueExact n))
  (if (< (bit-shift-right Integer/MAX_VALUE 1) (.bitLength n))
    (println (type n) ":bitLength" (.bitLength n))
    (print-value-and-type n))
  ;;(print-value-and-type n) ;; hangs printing large BigInteger
  (println)
  )
;;----------------------------------------------------------------
(let [one BigInteger/ONE
      big (.shiftLeft one (dec Integer/MAX_VALUE))
      medium (.sqrt big)
      medium1 (- medium 1)
      ]
  #_(try  
      (.shiftLeft one Integer/MAX_VALUE)
      (catch ArithmeticException e 
        (println (.getMessage e))))
  ;  (print-projections one)
  ;  (print-projections big)
  #_(ncc/echo-types
       (+ Integer/MAX_VALUE 1)
       (- Integer/MAX_VALUE 1)
       (int (- Integer/MAX_VALUE 1))
      ;; (+ Long/MAX_VALUE 1) ;; -> ArithmeticException
       (+' Long/MAX_VALUE 1)
       (+ (bigint Long/MAX_VALUE) 1)
      (+ (biginteger Long/MAX_VALUE) 1)
       (+ (BigInteger/valueOf Long/MAX_VALUE) 1)
      (- (BigInteger/valueOf Long/MAX_VALUE) 1)
      )
  (ncc/seconds
    (.bitlength (.multiply medium medium1))
    (let [mm1 (* medium medium1)]
      (type mm1)))
  )
