(ns inkscape-animation-assistant.core
  (:require
    [reagent.core :as r]
    [inkscape-animation-assistant.animation :refer [animate! flip-layers layers-get-all]]
    [goog.crypt :refer [byteArrayToHex]]
    [oops.core :refer [oget]])
  (:import goog.crypt.Sha256))

(def initial-state
  {:playing false
   :svg nil
   :last nil
   :menu nil
   :file nil
   :modal false})

(def animation-layers-selector "#animation svg > g")

(defn read-file [file cb]
  (let [reader (js/FileReader.)]
    (aset reader "onload" #(cb (.. % -target -result)))
    (aset reader "onerror" #(js/console.log "FileReader error" %))
    (.readAsText reader file "utf-8")))

(defn get-file-time [file]
  (if file
    (-> file .-lastModified js/Date. .getTime)
    0))

(defn sha256 [t]
  (let [h (Sha256.)]
    (.update h t)
    (->
      (.digest h)
      (byteArrayToHex)
      (.substr 8))))

(defn filewatcher [state]
  (let [file (@state :file)
        last-mod (@state :last)]
    ; TODO: strip out <script> tags here as they will mess with the animation
    (when file
      (if (aget file "text")
        ; firefox (lastModified does not update)
        ; and FileReader produces errors on changed file
        (.then (.text file) (fn [content]
                              (let [updated (sha256 content)]
                                (when (not= updated last-mod)
                                  (swap! state assoc :svg content :last updated)))))
        ; chrome - can re-read the modified file without errors
        (let [updated (get-file-time file)]
          (when (not= updated last-mod)
            (read-file file (fn [content]
                              (swap! state assoc :svg content :last updated)))))))))

(defn file-selected! [state ev]
  (let [input (.. ev -target)
        file (and (.-files input) (aget (.-files input) 0))]
    (js/console.log "Loading:" file)
    (swap! state assoc :file file :modal true)))

(defn play! [state _ev]
  (let [timer (js/setTimeout (partial animate! #(@state :playing) 0 animation-layers-selector) 1)]
    (swap! state #(-> %
                      (assoc :timer timer)
                      (update-in [:playing] not)))))

(defn pause! [state _ev]
  (swap! state update-in [:playing] not))

(defn make-export-url [state animation-script]
  (let [svg-text (@state :svg)
        export-text (when svg-text (.replace svg-text "</svg>" (str "<script>/*svgflipbook*/" (.trim animation-script "\n") "</script>\n</svg>")))]
    (if export-text
      (str "data:image/svg;charset=utf-8," (js/encodeURIComponent export-text))
      "#loading")))

(defn hide-menu [state ev]
  (swap! state assoc :help true :menu false)
  (.preventDefault ev))

(defn fit-width-height [div]
  (js/setTimeout (fn []
                   (let [svg (.querySelector div "svg")
                         width (and svg (.getAttribute svg "width"))
                         height (and svg (.getAttribute svg "height"))
                         viewBox (when svg (oget svg "viewBox" "baseVal"))]
                     (js/console.log "fit width height:" svg width height viewBox)
                     (when (and viewBox (or (nil? width) (> (.indexOf width "%") 0)) (or (nil? height) (> (.indexOf height "%") 0)))
                       (.setAttribute svg "width" (- (aget viewBox "width") (aget viewBox "x")))
                       (.setAttribute svg "height" (- (aget viewBox "height") (aget viewBox "y"))))
                     (when (and height width
                                (not (> (.indexOf height "%") 0))
                                (not (> (.indexOf width "%") 0))
                                (or (nil? viewBox)
                                                 (= 0
                                                    (aget viewBox "x")
                                                    (aget viewBox "x")
                                                    (aget viewBox "width")
                                                    (aget viewBox "height"))))
                       (.setAttribute svg "viewBox" (str "0 0 " width " " height)))))
                 1))

;; -------------------------
;; Views

(defn component-play-pause [state]
  [:span#pp.button {:on-click (partial (if (@state :playing) pause! play!) state)}
   [:svg#play-pause.icon {:viewBox "0 0 1792 1792"}
    [:path {:fill "#fff"
            :stroke "#fff"
            :stroke-linejoin "round"
            :stroke-width "200"
            :d
            (if (@state :playing)
              "M1664 192v1408q0 26-19 45t-45 19h-1408q-26 0-45-19t-19-45v-1408q0-26 19-45t45-19h1408q26 0 45 19t19 45z"
              "M1576 927l-1328 738q-23 13-39.5 3t-16.5-36v-1472q0-26 16.5-36t39.5 3l1328 738q23 13 23 31t-23 31z")}]]])

(defn component-choose-file [state]
  [:label#choosefile
   [:input#fileinput {:type "file"
                      :accept ".svg"
                      :on-change (partial file-selected! state)}]
   [:a.button "Open SVG"]])

(defn component-menu [state animation-script]
  [:nav#menu
   [:span#buttons
    [:span [component-choose-file state]]
    [:span [component-play-pause state]]]
   [:span#filename [:span [:img#logo {:src "icon.png"}] (when (@state :file) (.-name (@state :file)))]]
   [:span#actions
    (when (@state :file)
      [:span
       [:a#export {:href (make-export-url state animation-script) :download (.replace (.-name (@state :file)) ".svg" "-animated.svg") :id "exportbtn"} "Export"]])
    [:span.menu
     [:a#help {:href "https://github.com/chr15m/svg-animation-assistant"
               :target "_BLANK"}
      "Source code"]
     [:a#help {:href "#"
               :on-click (partial hide-menu state)}
      "Help"]]]])

(defn component-close [_state close-fn]
  [:svg#close.icon {:viewBox "0 0 1792 1792"
                    :on-click close-fn}
   [:path {:d "M1277 1122q0-26-19-45l-181-181 181-181q19-19 19-45 0-27-19-46l-90-90q-19-19-46-19-26 0-45 19l-181 181-181-181q-19-19-45-19-27 0-46 19l-90 90q-19 19-19 46 0 26 19 45l181 181-181 181q-19 19-19 45 0 27 19 46l90 90q19 19 46 19 26 0 45-19l181-181 181 181q19 19 45 19 27 0 46-19l90-90q19-19 19-46zm387-226q0 209-103 385.5t-279.5 279.5-385.5 103-385.5-103-279.5-279.5-103-385.5 103-385.5 279.5-279.5 385.5-103 385.5 103 279.5 279.5 103 385.5z"}]])

(defn component-modal [state]
  [:div#modal
   [:div
    [:p "Now open " [:strong (when (@state :file) (.-name (@state :file)))] " in your vector graphics editor."]
    [:p "When you make changes and save your work, the animation will update here."]
    [:button {:on-click #(swap! state assoc :modal false)} "Ok"]]])

(defn component-help [state]
  [:div#help-page
   [:div
    [component-close state #(swap! state assoc :help nil)]
    [:h1 "Help"]
    [:p "You can customize frame timing and behaviour by editing the layer name in your SVG editor."]
    [:img {:src "layers.png"}]
    [:p "Add frame commands to the layer name in brackets like '(300)' and '(static)'. See below for frame command details."]
    [:p "Once you have edited the layer name save your SVG and the changes will appear in the SVG Flipbook app immediately."]
    [:h3 "Frame duration"]
    [:p "Set the frame duration by entering the number of milliseconds in brackets in the layer name, like `(100)` for a pause of 100ms, or 1/10th of a second."]
    [:h3 "Static background"]
    [:p "Set a layer as a static background which will always be visible, by adding the word `(static)` to the layer name."]
    [:h3 "Embed code"]
    [:p "Once you have exported your animated SVG you can embed it in a web page with this code:"]
    [:pre "<object data='FILENAME.svg' type='image/svg+xml'>\n\t<img src='FILENAME.svg' />\n</object>"]
    [:h3 "Feedback"]
    [:p "Got questions or feedback? " [:a#feedback {:href "mailto:chris@svgflipbook.com?subject=SVGFlipbook%20feedback"} "Send me an email"] "."]
    [:button {:on-click #(swap! state assoc :help nil)} "Ok"]]])

(defn component-app [state animation-script]
  [:div#container
   (if (:svg @state)
     [:div#animation {:dangerouslySetInnerHTML {:__html (@state :svg)}
                      :ref #(when %
                              (fit-width-height %)
                              (flip-layers (fn [i _l] (= i 0)) (layers-get-all animation-layers-selector)))}]
     [:div#intro
      [:div
       [:h3 "SVG Flipbook"]
       [:p "SVG Flipbook is an online app for creating flipbook style frame-by-frame animated SVGs."]
       [:ul
        [:li "Start by opening the SVG you want to animate in your favourite editor, such as Inkscape."]
        [:li "Open the same SVG in this app using the 'Open SVG' button to the top left."]
        [:li "Add layers to your SVG. When you hit save, the animation will update in this window."]]]])
   [:div#interface
    (when (or (not (@state :file)) (@state :help))
      {:style {:opacity 1}})
    (when (@state :help)
      [component-help state])
    [component-menu state animation-script]
    (when (@state :modal)
      [component-modal state])]])

;; -------------------------
;; Initialize app

(defonce state (r/atom initial-state))

(defn mount-root []
  (->
    (js/fetch "animate.min.js")
    (.then #(.text %))
    (.then (fn [animation-script]
             (r/render [component-app state animation-script] (.getElementById js/document "app"))))))

(defn init! []
  (js/setInterval (partial #'filewatcher state) 500)
  (mount-root))
