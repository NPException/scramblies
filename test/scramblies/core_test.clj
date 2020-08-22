(ns scramblies.core-test
  (:require [clojure.test :refer :all]
            [scramblies.core :refer :all]))

(deftest scramblies-test
  (is (scramble? "rekqodlw" "world"))
  (is (scramble? "cedewaraaossoqqyt" "codewars"))
  (is (not (scramble? "katas" "steak")))
  (is (scramble? "multiplecharactersneeded" "arachnid"))
  (is (not (scramble? "notenoughbees" "bugbee"))))
