(ns scramblies.page
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.dom :as dom]
            [re-frame.core :as rf]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(def ^:private API-URL "http://localhost:8080/scramble")


(defn ^:private lowercase-a-z?
  [s]
  (and (string? s)
       (re-matches #"^[a-z]*$" s)))

(defn ^:private valid-string?
  [db keys]
  (lowercase-a-z? (get-in db keys "")))


;; value retrieval of the first string
(rf/reg-sub
  :scramblies/str1
  (fn [db _]
    (get-in db [:inputs :str1] "")))

;; value retrieval of the validation for the first string
(rf/reg-sub
  :scramblies/str1-valid?
  (fn [db _]
    (valid-string? db [:inputs :str1])))

;; event listener for changes to the first input
(rf/reg-event-db
  :scramblies/str1-change
  (fn [db [_ value]]
    (-> db
        (assoc-in [:inputs :str1] value)
        (dissoc :result))))


;; value retrieval of the second string
(rf/reg-sub
  :scramblies/str2
  (fn [db _]
    (get-in db [:inputs :str2] "")))

;; value retrieval of the validation for the second string
(rf/reg-sub
  :scramblies/str2-valid?
  (fn [db _]
    (valid-string? db [:inputs :str2])))

;; event listener for changes to the second input
(rf/reg-event-db
  :scramblies/str2-change
  (fn [db [_ value]]
    (-> db
        (assoc-in [:inputs :str2] value)
        (dissoc :result))))


;; event listener for pressing the "Scramble?" button
(rf/reg-event-db
  :scramblies/scramble
  (fn [db _]
    (let [str1 (get-in db [:inputs :str1] "")
          str2 (get-in db [:inputs :str2] "")]
      (when (and (lowercase-a-z? str1)
                 (lowercase-a-z? str2))
        (go (let [response (<! (http/get API-URL
                                         {:with-credentials? false
                                          :query-params      {"str1" str1
                                                              "str2" str2}}))]
              ;; API error checking could be added here
              (when (= 200 (:status response))
                (rf/dispatch [:scramblies/result-delivered (-> response :body :scramble?)])))))
      db)))

;; event listener for successful result delivery from the API
(rf/reg-event-db
  :scramblies/result-delivered
  (fn [db [_ value]]
    (assoc db :result (true? value))))

;; value retrieval for the stored API call result
(rf/reg-sub
  :scramblies/result
  (fn [db _]
    (get db :result)))


(def ^:private str1-valid-atom
  (rf/subscribe [:scramblies/str1-valid?]))

(def ^:private str2-valid-atom
  (rf/subscribe [:scramblies/str2-valid?]))

(def ^:private scramble-result-atom
  (rf/subscribe [:scramblies/result]))


(defn app-view
  []
  [:div
   [:form
    [:header [:h1 "Scramblies"]]
    [:div
     [:label {:for "str1"} "Input"]
     [:input {:type        "text"
              :id          "str1"
              :name        "str1"
              :placeholder "potential scramble"
              :value       @(rf/subscribe [:scramblies/str1])
              :on-change   #(rf/dispatch [:scramblies/str1-change (-> % .-target .-value)])}]
     (when-not @str1-valid-atom
       [:small "Input may only contain lowercase a-z!"])]
    [:div
     [:label {:for "str2"} "Target"]
     [:input {:type        "text"
              :id          "str2"
              :name        "str2"
              :placeholder "desired word"
              :value       @(rf/subscribe [:scramblies/str2])
              :on-change   #(rf/dispatch [:scramblies/str2-change (-> % .-target .-value)])}]
     (when-not @str2-valid-atom
       [:small "Target may only contain lowercase a-z!"])]
    (when (and @str1-valid-atom @str2-valid-atom)
      [:a {:href     "#"
           :on-click #(rf/dispatch [:scramblies/scramble])}
       [:b "Scramble?"]])
    (when-some [result @scramble-result-atom]
      [:hr]
      (if result
        [:h2 "💚 It's a scramble!"]
        [:h2 "🙁 Not a scramble."]))]])


(dom/render
  [app-view]
  (js/document.getElementById "app"))
