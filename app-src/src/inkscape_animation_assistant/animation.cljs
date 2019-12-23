(ns inkscape-animation-assistant.animation)

(def inkscape-label-re #"\((\d+)\)")
(def illustrator-label-re #"_x28_(\d+)_x29_")

(defn layer-get-delay [layer]
  (let [default 100]
    (if layer
      (let [label (or (.getAttribute layer "inkscape:label") (.getAttribute layer "id"))
            delayparameter-inkscape (if label (.match label inkscape-label-re))
            delayparameter-illustrator (if label (.match label illustrator-label-re))
            delayparameter (or delayparameter-inkscape delayparameter-illustrator)]
        (if delayparameter (js/parseInt (aget delayparameter 1)) default))
      default)))

(defn layer-is-static [layer]
  (if layer
    (-> (or
          (.getAttribute layer "inkscape:label")
          (.getAttribute layer "id"))
        (or "")
        (.indexOf "tatic")
        (not= -1))))

(defn layers-get-all [container]
  (js/Array.from (.querySelectorAll js/document container)))

(defn flip-layers [cb layers]
  (doall
    (map-indexed
      (fn [i l]
        (aset (.-style l) "display"
              (if (cb i l)
                "inline"
                "none")))
      layers)))

(defn animate! [fn-is-playing? frame container]
  (let [fn-is-playing? (or fn-is-playing? (fn [] true))
        layers (layers-get-all (or container "svg > g"))
        length (count layers)
        current-frame (mod (or frame 0) length)
        layer (aget layers (or current-frame 0))
        frame-time (layer-get-delay layer)
        static (layer-is-static layer)]
    (if (fn-is-playing?)
      (do
        (if (or (not static) (not frame))
          (flip-layers (fn [i l] (or (= i current-frame) (layer-is-static l))) layers))
        (js/setTimeout (partial animate! fn-is-playing? (+ current-frame 1) container) frame-time)))))
