(ns scramblies.core
  (:gen-class))

(defn scramble?
  "returns true if a portion of str1 characters can be rearranged to match str2, otherwise returns false"
  [str1 str2]
  (loop [available-char-counts (transient (frequencies str1))
         [c & remaining] str2]
    (or (nil? c)
        (if-let [n (available-char-counts c)]
          (and (> n 0)
               (recur
                 (assoc! available-char-counts c (dec n))
                 remaining))))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
