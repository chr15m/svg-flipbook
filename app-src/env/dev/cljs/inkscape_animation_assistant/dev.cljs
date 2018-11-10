(ns ^:figwheel-no-load inkscape-animation-assistant.dev
  (:require
    [inkscape-animation-assistant.core :as core]
    [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(core/init!)
