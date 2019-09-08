(ns user
 (:require [figwheel-sidecar.repl-api :as ra]
           [clojure.java.io :as io]
           [environ.core :refer [env]]))

(import 'java.lang.Runtime)

(println "Building animate.min.js")

(let [proc (.exec (Runtime/getRuntime) "make public/animate.min.js")]
  (with-open [rdr (io/reader (.getInputStream proc))]
    (doseq [line (line-seq rdr)]
      (println line))))

(defn start-fw []
 (ra/start-figwheel!))

(defn stop-fw []
 (ra/stop-figwheel!))

(defn cljs []
 (ra/cljs-repl))
