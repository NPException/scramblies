(ns scramblies.core-test
  (:require [clojure.test :refer :all]
            [scramblies.core :as scramblies]))

(deftest scramblies-test
  (is (#'scramblies/scramble? "rekqodlw" "world"))
  (is (#'scramblies/scramble? "cedewaraaossoqqyt" "codewars"))
  (is (not (#'scramblies/scramble? "katas" "steak")))
  (is (#'scramblies/scramble? "multiplecharactersneeded" "arachnid"))
  (is (not (#'scramblies/scramble? "notenoughbees" "bugbee"))))


(deftest route-test
  (testing "valid requests"
    (is (= (#'scramblies/router {:request-method :get
                                 :uri            "/scramble"
                                 :query-params   {:str1 "multiplecharactersneeded"
                                                  :str2 "arachnid"}})
           {:status 200
            :body   {:scramble? true}}))
    (is (= (#'scramblies/router {:request-method :get
                                 :uri            "/scramble"
                                 :query-params   {:str1 "notenoughbees"
                                                  :str2 "bugbee"}})
           {:status 200
            :body   {:scramble? false}})))

  (testing "invalid parameters"
    (is (= 400 (:status (#'scramblies/router {:request-method :get
                                              :uri            "/scramble"
                                              :query-params   {:str1 "includes invalid chars!"
                                                               :str2 "darci"}}))))
    (is (= 400 (:status (#'scramblies/router {:request-method :get
                                              :uri            "/scramble"
                                              :query-params   {:str1 "arachnid"
                                                               :str2 123}})))))

  (testing "invalid route"
    (is (nil? (#'scramblies/router {:request-method :get
                                    :uri            "/poodle"
                                    :query-params   {:str1 "notenoughbees"
                                                     :str2 "bugbee"}}))))

  (testing "invalid request method"
    (is (nil? (#'scramblies/router {:request-method :post
                                    :uri            "/scramble"
                                    :query-params   {:str1 "notenoughbees"
                                                     :str2 "bugbee"}})))))

(deftest server-test
  (let [^org.eclipse.jetty.server.Server server (scramblies/-main :port 3000)
        true-response (slurp "http://localhost:3000/scramble?str1=multiplecharactersneeded&str2=arachnid")
        false-response (slurp "http://localhost:3000/scramble?str1=abc&str2=world")]
    (is (= true-response "{\"scramble?\":true}"))
    (is (= false-response "{\"scramble?\":false}"))
    (.stop server)))
