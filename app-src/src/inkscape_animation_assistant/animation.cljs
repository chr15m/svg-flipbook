(ns inkscape-animation-assistant.animation)

(defn layer-get-delay [layer]
  (let [default 100]
    (if layer
      (let [label (.getAttribute layer "inkscape:label")
            delayparameter (.match label #"\((\d+)\)")]
        (if delayparameter (js/parseInt (aget delayparameter 1)) default))
      default)))

(defn layer-is-static [layer]
  (if layer
    (-> layer
        (.getAttribute "inkscape:label")
        (.indexOf "tatic")
        (not= -1))))

(defn layers-get-all []
  (js/Array.from (.querySelectorAll js/document "svg > g")))

(defn flip-layers [cb layers]
  (doall
    (map-indexed
      (fn [i l]
        (aset (.-style l) "display"
              (if (cb i l)
                ""
                "none")))
      layers)))

(defn animate! [fn-is-playing? frame]
  (let [fn-is-playing? (or fn-is-playing? (fn [] true))
        layers (layers-get-all)
        length (count layers)
        current-frame (mod (or frame 0) length)
        layer (aget layers (or current-frame 0))
        frame-time (layer-get-delay layer)
        static (layer-is-static layer)]
    (if (fn-is-playing?)
      (do
        (if (or (not static) (not frame))
          (flip-layers (fn [i l] (or (= i current-frame) (layer-is-static l))) layers))
        (js/setTimeout (partial animate! fn-is-playing? (+ current-frame 1)) frame-time)))))
