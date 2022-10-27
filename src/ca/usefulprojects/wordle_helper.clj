(ns ca.usefulprojects.wordle-helper
  "Helps show possible combinations of wordle clues, without being too cheaty."
  (:require
   [clojure.math.combinatorics :as combo]
   [clojure.set :as set]))

(defn sensible?
  "Checks if a guess is sensible.
  Currently just checks if `x` has letters that repeat more than twice in a row."
  [x]
  (->> x (remove #{\_}) (partition-by identity) (map count) (apply max) (> 2)))

(defn possibilities
  "Shows possible combinations given some wordle guess data.
  Guess data is a vector of information:
  - a char for known squares
  - a vector of chars for a square with yellows guesses
  - nil for nothing known"
  [guesses]
  (let [found (->> guesses (filter char?) set)

        letter-pool
        (as-> guesses $
          (filter set? $)
          (apply set/union $)
          (set/difference $ found))

        slot-pools
        (mapv (fn [x]
                (cond
                  (set? x)  (conj (set/difference letter-pool x) \_)
                  (nil? x)  (conj letter-pool \_)
                  (char? x) [x]))
              guesses)

        combos
        (apply combo/cartesian-product
               slot-pools)]
    (into [] (comp (filter #(set/superset? (set %) letter-pool))
                   (filter sensible?)
                   (map #(apply str %)))
          combos)))

;; TODO: need to be able to communicate a slot going from yellow to green
(possibilities [#{\T \R} \E nil #{\T} \T])
