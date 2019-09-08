(ns inkscape-animation-assistant.core
  (:require
    [reagent.core :as r]
    [inkscape-animation-assistant.animation :refer [animate! flip-layers layers-get-all]])
  (:import goog.crypt.Sha256))

(def initial-state
  {:playing false
   :svg nil
   :last nil
   :file nil})

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
  (let [h (goog.crypt.Sha256.)]
    (.update h t)
    (.digest h)))

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
                                (swap! state assoc :svg content :last updated))))
        ; chrome - can re-read the modified file without errors
        (let [updated (get-file-time file)]
          (when (not= updated last-mod)
            (read-file file (fn [content]
                              (swap! state assoc :svg content :last updated)))))))))

(defn file-selected! [state ev]
  (let [input (.. ev -target)
        file (and (.-files input) (aget (.-files input) 0))]
    (js/console.log "Loading:" file)
    (swap! state assoc :file file)))

(defn play! [state ev]
  (let [timer (js/setTimeout (partial animate! #(@state :playing) 0) 1)]
    (swap! state #(-> %
                      (assoc :timer timer)
                      (update-in [:playing] not)))))

(defn pause! [state ev]
  (swap! state update-in [:playing] not))

(defn make-export-url [state animation-script]
  (let [svg-text (@state :svg)
        export-text (if svg-text (.replace svg-text "</svg>" (str "<script>/*svgflipbook*/" (.trim animation-script "\n") "</script>\n</svg>")))]
    (if export-text
      (str "data:image/svg;charset=utf-8," (js/encodeURIComponent export-text))
      "#loading")))

;; -------------------------
;; Views

(defn component-folder-icon []
  [:svg {:viewBox "0 -256 1950 1950" :width 256 :height 256}
   [:g {:transform "matrix(1,0,0,-1,30.372881,1443.4237)"}
    [:path {:fill "#888" :d "m1781,605q0,35-53,35h-1088q-40,0-85.5-21.5t-71.5-52.5l-294-363q-18-24-18-40,0-35,53-35h1088q40,0,86,22t71,53l294,363q18,22,18,39zm-1141,163h768v160q0,40-28,68t-68,28h-576q-40,0-68,28t-28,68v64q0,40-28,68t-68,28h-320q-40,0-68-28t-28-68v-853l256,315q44,53,116,87.5t140,34.5zm1269-163q0-62-46-120l-295-363q-43-53-116-87.5t-140-34.5h-1088q-92,0-158,66t-66,158v960q0,92,66,158t158,66h320q92,0,158-66t66-158v-32h544q92,0,158-66t66-158v-160h192q54,0,99-24.5t67-70.5q15-32,15-68z"}]]])

(defn component-play-pause [state]
  [:svg#play-pause.icon {:viewBox "0 0 1792 1792"
                         :on-click (partial (if (@state :playing) pause! play!) state)}
   [:path {:d
           (if (@state :playing)
             "M1216 1184v-576q0-14-9-23t-23-9h-576q-14 0-23 9t-9 23v576q0 14 9 23t23 9h576q14 0 23-9t9-23zm448-288q0 209-103 385.5t-279.5 279.5-385.5 103-385.5-103-279.5-279.5-103-385.5 103-385.5 279.5-279.5 385.5-103 385.5 103 279.5 279.5 103 385.5z"
             "M896 128q209 0 385.5 103t279.5 279.5 103 385.5-103 385.5-279.5 279.5-385.5 103-385.5-103-279.5-279.5-103-385.5 103-385.5 279.5-279.5 385.5-103zm384 823q32-18 32-55t-32-55l-544-320q-31-19-64-1-32 19-32 56v640q0 37 32 56 16 8 32 8 17 0 32-9z")}]])

(defn component-export [state animation-script]
  [:a {:href (make-export-url state animation-script) :download (.replace (.-name (@state :file)) ".svg" "-animated.svg")}
   [:svg#export.icon {:viewBox "0 0 1792 1792"}
    [:path {:d "M1344 1344q0-26-19-45t-45-19-45 19-19 45 19 45 45 19 45-19 19-45zm256 0q0-26-19-45t-45-19-45 19-19 45 19 45 45 19 45-19 19-45zm128-224v320q0 40-28 68t-68 28h-1472q-40 0-68-28t-28-68v-320q0-40 28-68t68-28h465l135 136q58 56 136 56t136-56l136-136h464q40 0 68 28t28 68zm-325-569q17 41-14 70l-448 448q-18 19-45 19t-45-19l-448-448q-31-29-14-70 17-39 59-39h256v-448q0-26 19-45t45-19h256q26 0 45 19t19 45v448h256q42 0 59 39z"}]]])

(defn component-close [state]
  [:svg#close.icon {:viewBox "0 0 1792 1792"
                    :on-click #(reset! state initial-state)}
   [:path {:d "M1277 1122q0-26-19-45l-181-181 181-181q19-19 19-45 0-27-19-46l-90-90q-19-19-46-19-26 0-45 19l-181 181-181-181q-19-19-45-19-27 0-46 19l-90 90q-19 19-19 46 0 26 19 45l181 181-181 181q-19 19-19 45 0 27 19 46l90 90q19 19 46 19 26 0 45-19l181-181 181 181q19 19 45 19 27 0 46-19l90-90q19-19 19-46zm387-226q0 209-103 385.5t-279.5 279.5-385.5 103-385.5-103-279.5-279.5-103-385.5 103-385.5 279.5-279.5 385.5-103 385.5 103 279.5 279.5 103 385.5z"}]])

(defn component-app [state animation-script]
  (if (@state :file)
    [:div#container
     [:div#animation {:dangerouslySetInnerHTML {:__html (@state :svg)}
                      :ref #(when % (flip-layers (fn [i l] (or (= i 0))) (layers-get-all)))}]
     [:div#interface
      [component-close state]
      [component-export state animation-script]
      [component-play-pause state]]]
    [:div#choosefile
     (js/console.log "updated file chooser")
     [:div
      [:label 
       [:input#fileinput {:type "file"
                          :accept ".svg"
                          :on-change (partial file-selected! state)}]
       [component-folder-icon]
       [:p "Choose SVG"]]]]))

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
