(ns inkscape-animation-assistant.core
    (:require
      [reagent.core :as r]))

(defn read-file [file cb]
  (let [reader (js/FileReader.)]
    (aset reader "onload" #(cb (.. % -target -result)))
    (.readAsText reader file "utf-8")))

(defn get-file-time [file]
  (.getTime (.-lastModifiedDate file)))

(defn filewatcher [state]
  (let [file (@state :file)
        last-mod (@state :last)]
    (when (and file (not= (.getTime (.-lastModifiedDate file)) (.getTime last-mod)))
      (read-file file (fn [content]
                        (swap! state assoc :svg content))))))

(defn file-selected [state ev]
  (let [input (.. ev -target)
        file (and (.-files input) (aget (.-files input) 0))]
    (read-file file (fn [content]
                      (swap! state assoc :file file :last (.-lastModifiedDate file) :svg content)))))

;; -------------------------
;; Views

(defn component-app [state]
  (if (@state :file)
    [:div
     [:div#animation {:dangerouslySetInnerHTML {:__html (@state :svg)}}]
     [:div#interface]]
    [:div#choosefile
     [:div
      [:label 
       [:input#fileinput {:type "file" :accept ".svg" :on-change (partial file-selected state)}]
       [:svg {:viewBox "0 -256 1950 1950" :width 256 :height 256}
        [:g {:transform "matrix(1,0,0,-1,30.372881,1443.4237)"}
         [:path {:fill "#888" :d "m1781,605q0,35-53,35h-1088q-40,0-85.5-21.5t-71.5-52.5l-294-363q-18-24-18-40,0-35,53-35h1088q40,0,86,22t71,53l294,363q18,22,18,39zm-1141,163h768v160q0,40-28,68t-68,28h-576q-40,0-68,28t-28,68v64q0,40-28,68t-68,28h-320q-40,0-68-28t-28-68v-853l256,315q44,53,116,87.5t140,34.5zm1269-163q0-62-46-120l-295-363q-43-53-116-87.5t-140-34.5h-1088q-92,0-158,66t-66,158v960q0,92,66,158t158,66h320q92,0,158-66t66-158v-32h544q92,0,158-66t66-158v-160h192q54,0,99-24.5t67-70.5q15-32,15-68z"}]]]
       [:p "Choose SVG"]]]]))

;; -------------------------
;; Initialize app

(defonce state (r/atom {}))

(defn mount-root []
  (r/render [component-app state] (.getElementById js/document "app")))

(defn init! []
  (js/setInterval (partial #'filewatcher state) 250)
  (mount-root))
