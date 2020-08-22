(ns scramblies.core
  (:gen-class))

(defn ^:private enough-chars?
  [required-char-count [char n]]
  (>= n (required-char-count char)))

(defn scramble?
  "returns true if a portion of str1 characters can be rearranged to match str2, otherwise returns false"
  [str1 str2]
  (let [required-char-counts (frequencies str2)
        available-char-counts (select-keys
                                (frequencies str1)
                                (keys required-char-counts))]
    (and
      (= (count required-char-counts)
         (count available-char-counts))
      (every?
        #(enough-chars? required-char-counts %)
        available-char-counts))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
