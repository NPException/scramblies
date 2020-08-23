(ns scramblies.core
  (:require [reitit.ring :as ring]
            [reitit.coercion.spec :as rspec]
            [reitit.ring.coercion :as coercion]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.adapter.jetty :as jetty])
  (:gen-class))

(defn ^:private scramble?
  "returns true if a portion of str1 characters can be rearranged to match str2, otherwise returns false"
  [str1 str2]
  (boolean
    (loop [available-char-counts (transient (frequencies str1))
           [c & remaining] str2]
      (or (nil? c)
          (if-let [n (available-char-counts c)]
            (and (> n 0)
                 (recur
                   (assoc! available-char-counts c (dec n))
                   remaining)))))))


(defn ^:private lowercase-a-z?
  [s]
  (and (string? s)
       (re-matches #"^[a-z]*$" s)))


(def ^:private router
   (ring/ring-handler
      (ring/router
        ["/scramble" {:get {:parameters {:query {:str1 lowercase-a-z?
                                                 :str2 lowercase-a-z?}}
                            :responses  {200 {:body {:scramble? boolean?}}}
                            :handler    (fn [{{{:keys [str1 str2]} :query} :parameters}]
                                          {:status  200
                                           :headers {"Access-Control-Allow-Origin" "*"}
                                           :body    {:scramble? (scramble? str1 str2)}})}}]
        {:data {:coercion   rspec/coercion
                :middleware [coercion/coerce-exceptions-middleware
                             coercion/coerce-request-middleware
                             coercion/coerce-response-middleware]}})))


(defn ^:private wrap-empty-response
  [handler]
  (fn
    ([request]
     (or (handler request)
         {:status 400 :body "bad request"}))
    ([request respond raise]
     (handler request
              (fn [response]
                (respond response {:status 400 :body "bad request"}))
              raise))))


(defn -main
  "starts the server on a given :port (default 8080)"
  [& {:keys [port]
      :or {port 8080}}]
  (jetty/run-jetty
    (-> #'router
        wrap-json-response
        wrap-empty-response
        wrap-params)
    {:port port
     :join? false}))

;; development REPL snippets
(comment
  (def server (-main))
  (.stop server)
  )