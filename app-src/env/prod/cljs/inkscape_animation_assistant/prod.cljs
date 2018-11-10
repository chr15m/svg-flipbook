(ns inkscape-animation-assistant.prod
  (:require
    [inkscape-animation-assistant.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
