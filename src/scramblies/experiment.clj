(ns scramblies.experiment
  (:import [java.util HashMap Map BitSet]
           [java.util.function BiFunction]))

;; ORIGINAL VERSION ;;

(defn scramble?
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


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def ^:private safe-dec
  (reify
    BiFunction
    (apply [_ k v]
      (dec (or v 0)))))


(def ^:private safe-inc
  (reify
    BiFunction
    (apply [_ k v]
      (inc (or v 0)))))


(defn ^:private native-frequencies
  "Just like `frequencies`, but returns a regular java.util.Map instead."
  ^Map [col]
  (reduce
    (fn [^Map freqs element]
      (.compute freqs element safe-inc)
      freqs)
    (HashMap.)
    col))

;; JAVA HASHMAP VERSION ;;

(defn scramble?-hashmap
  "returns true if a portion of str1 characters can be rearranged to match str2, otherwise returns false"
  [str1 str2]
  (boolean
    (let [available-char-counts (native-frequencies str1)]
      (loop [[c & remaining] str2]
        (or (nil? c)
            (and (<= 0 (.compute available-char-counts c safe-dec))
                 (recur remaining)))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn ^:private next-unused-char-index
  "Returns the next index for the character in the String
  which has not yet been used according to the used-chars BitSet."
  [^String s
   ^BitSet used-chars
   character
   ^long start-index]
  (let [i (.indexOf s (int character) start-index)]
    (when (> i -1)
      (if-not (.get used-chars i)
        i
        (recur s used-chars character (inc i))))))

;; BITSET VERSION ;;

(defn scramble?-bitset
  "returns true if a portion of str1 characters can be rearranged to match str2, otherwise returns false"
  [str1 str2]
  (boolean
    (let [used-chars (BitSet. (count str1))]
      (loop [[c & remaining] str2]
        (or (nil? c)
            (and (when-let [i (next-unused-char-index str1 used-chars c 0)]
                   (.set used-chars i)
                   true)
                 (recur remaining)))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; benchmarking snippets
(comment
  (use 'criterium.core)

  (bench
    (scramble? "multiplecharactersneeded" "arachnid"))
  ; Evaluation count : 9743400 in 60 samples of 162390 calls.
  ;             Execution time mean : 6,163140 µs
  ;    Execution time std-deviation : 10,749977 ns
  ;   Execution time lower quantile : 6,150668 µs ( 2,5%)
  ;   Execution time upper quantile : 6,191385 µs (97,5%)
  ;                   Overhead used : 6,772466 ns

  (bench
    (scramble? "multiplecharactersneeded" "multiplecharactersneeded"))
  ; Evaluation count : 8064660 in 60 samples of 134411 calls.
  ;             Execution time mean : 7,454470 µs
  ;    Execution time std-deviation : 18,371791 ns
  ;   Execution time lower quantile : 7,438324 µs ( 2,5%)
  ;   Execution time upper quantile : 7,493557 µs (97,5%)
  ;                   Overhead used : 6,772466 ns

  (bench
    (scramble?-hashmap "multiplecharactersneeded" "arachnid"))
  ; Evaluation count : 59692500 in 60 samples of 994875 calls.
  ;             Execution time mean : 1,001869 µs
  ;    Execution time std-deviation : 7,001678 ns
  ;   Execution time lower quantile : 996,781147 ns ( 2,5%)
  ;   Execution time upper quantile : 1,013112 µs (97,5%)
  ;                   Overhead used : 6,772466 ns

  (bench
    (scramble?-hashmap "multiplecharactersneeded" "multiplecharactersneeded"))
  ; Evaluation count : 41779620 in 60 samples of 696327 calls.
  ;             Execution time mean : 1,435406 µs
  ;    Execution time std-deviation : 8,367757 ns
  ;   Execution time lower quantile : 1,427676 µs ( 2,5%)
  ;   Execution time upper quantile : 1,458829 µs (97,5%)
  ;                   Overhead used : 6,772466 ns

  (bench
    (scramble?-bitset "multiplecharactersneeded" "arachnid"))
  ; Evaluation count : 168131640 in 60 samples of 2802194 calls.
  ;             Execution time mean : 349,116209 ns
  ;    Execution time std-deviation : 2,110576 ns
  ;   Execution time lower quantile : 346,329688 ns ( 2,5%)
  ;   Execution time upper quantile : 353,372776 ns (97,5%)
  ;                   Overhead used : 6,787013 ns

  (bench
    (scramble?-bitset "multiplecharactersneeded" "multiplecharactersneeded"))
  ; Evaluation count : 69266040 in 60 samples of 1154434 calls.
  ;             Execution time mean : 906,237926 ns
  ;    Execution time std-deviation : 5,364701 ns
  ;   Execution time lower quantile : 896,430496 ns ( 2,5%)
  ;   Execution time upper quantile : 918,252260 ns (97,5%)
  ;                   Overhead used : 6,787013 ns

  )