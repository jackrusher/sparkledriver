(ns sparkledriver.retry
  (:require [clojure.math.numeric-tower :refer [expt]]))

(defonce ^:dynamic *retry-fn* (fn [f] (f)))

(defn retry-backoff
  "Execute `try-fn`, catching any exception and then retrying with an exponential backoff until a retry pause of `max-wait` seconds is reached. If `recover-fn` is supplied, it will be called before each retry with the most recent exception as its only argument. Either returns the result of a successful invocation of `try-fn` or raises an exception.

  If try-fn throws an `:unhandled-fatal` exception the retry loop will terminate.

  Simple example:
  
```
  (retry-backoff
    ;; retry at 2,4 and 8 seconds
    8
    ;; return the value of element w/ id \"the-id\" if successful
    #(find-by-id browser \"the-id\"))
```

  Add a `recovery-fn` to the retry, which in this case just logs the exception:

```
  (retry-backoff
    ;; retry at 2,4,8 and 16 seconds
    16
    ;; \"recover-fn\" that logs failures between retries
    #(info (str \"Trying again after a \" (.getMessage %) \" exception...\"))
    ;; return the value of element w/ id \"the-id\" if successful
    #(find-by-id browser \"the-id\"))
```
  "
  ([max-wait try-fn] (retry-backoff max-wait nil try-fn))
  ([max-wait recover-fn try-fn]
   (loop [retry 0]
     (let [[success result ex] (try
                                 [true (try-fn) nil]
                                 (catch Exception e [false nil e]))]
       (if success
         result
         (let [x-info (ex-data ex)
               wait (+ (expt 2 retry) 2)]        ; 2s min wait
           (when (= (:cause x-info) :unhandled-fatal) ; bubble up :unhandled-fatal to escape retries
             (throw ex))
           (if (> wait max-wait)
             (throw (Exception. (str (.getMessage ex) " - timeout exceeded, too many retries!")))
             (do
               (when recover-fn
                 (recover-fn ex))
               (Thread/sleep (* 1000 wait))
               (recur (inc retry))))))))))

(defmacro with-retry
  "Evaluate `body` with `retry-fn` bound to the dynamic variable `*retry-fn*`. Many Sparkledriver functions automatically wrap themselves in `*retry-fn*` when it is bound, which allows one to specify a retry policy for those functions within the scope of this macro.

  Example:

```
  (with-browser [browser (make-browser)]
    (fetch! browser \"http://clojure.org\")  
    (with-retry (partial retry-backoff 16)
      (-> browser
          (find-by-xpath* \"//div[@class='clj-intro-message']/p\")
          (nth 2)
          text)))
;;=> \"I hope you find Clojure's combination of facilities elegant, powerful, practical and fun to use.\"      
```"
  [retry-fn & body]
  `(binding [*retry-fn* ~retry-fn]
     ~@body))

