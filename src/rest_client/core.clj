(ns rest-client.core
  (:require [seesaw.core :as ss])
  (:require [clj-http.client :as client]))

(defn request
  [s]
  (try
    (client/request
      (read-string (str "{" s "}")))
    (catch Exception e {:status (.getMessage e)})))

(defn status
  [response]
  (str (:status response)))

(defn header
  [response]
  (apply str (map (fn [[k v]] (str k ": " v \newline))
                  (:headers response))))

(defn body
  [response]
  (str (:body response)))

(defn click-handler
  [event]
  (let [response (request (ss/text (ss/select (ss/to-frame event) [:#txt])))]
    (ss/text! (ss/select (ss/to-frame event) [:#status]) (status response))
    (ss/text! (ss/select (ss/to-frame event) [:#header]) (header response))
    (ss/text! (ss/select (ss/to-frame event) [:#body])   (body   response))))

(defn create-frame
  []
  (ss/frame
    :title "rest-client"
    :content "rest-client"
    :size [420 :by 420]
    :on-close :exit
    :content (ss/vertical-panel
               :items [
                       (ss/grid-panel
                         :columns 2
                         :items [(ss/text :id :txt
                                          :text
                                          (str
                                            ":url \"http://junk.clan.vc:80/form_test.php?a=b&c=d\"" \newline
                                            ":method :post" \newline
                                            ":headers {\"X-Api-Version\" \"2\"}" \newline
                                            ":form-params {:foo \"bar\"}" \newline
                                            ":content-type \"application/x-www-form-urlencoded\"")
                                          :multi-line? true
                                          :rows 3)
                                 (ss/vertical-panel
                                   :items [(ss/grid-panel
                                             :border "status"
                                             :items [(ss/text :id :status
                                                              :text ""
                                                              :multi-line? true
                                                              :rows 1)])
                                           (ss/grid-panel
                                             :border "header"
                                             :items [(ss/text :id :header
                                                              :text ""
                                                              :multi-line? true
                                                              :rows 5)])
                                           (ss/grid-panel
                                             :border "body"
                                             :items [(ss/scrollable (ss/text :id :body
                                                                          :text ""
                                                                          :multi-line? true
                                                                          :rows 30))])
                                           ])])
                       (ss/flow-panel
                         :align :center
                         :items [(ss/button :text "send" :listen [:action click-handler])])
                       ])))

(defn show
  [frame]
  (ss/invoke-later (ss/show! frame)))

(defn -main
  []
  (show (create-frame)))
