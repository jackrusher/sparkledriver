(ns sparkledriver.browser
  (:require [sparkledriver.element :as elem]))

(def browser-options
  "The possible options for building a browser instance in {:option [default setter-fn]} format."
  {;; how long to wait for resources loaded by ajax, in milliseconds (default is quite long)
   :ajax-load-timeout [30000 #(.ajaxResourceTimeout %1 %2)]
   ;; how long to wait for JS to run after page load, in milliseconds
   :ajax-wait         [200 #(.ajaxWait %1 %2)]
   ;; whether requests to ad/spam servers should be blocked
   :block-ads         [false #(.blockAds %1 %2)]
   ;; use a local browser cache
   :cache             [true #(.cache %1 %2)]
   ;; if false, open a window so you can watch it work
   :headless          [true #(.headless %1 %2)]
   ;; increased logging
   :log-trace         [false #(.logTrace %1 %2)]
   :log-wire          [false #(.logWire %1 %2)]
   ;; we pretend to be Chrome
   :request-headers   [com.machinepublishers.jbrowserdriver.RequestHeaders/CHROME #(.requestHeaders %1 %2)]
   ;; store copies of media and attachments in a temporary folder
   :save-attachments  [true #(.saveAttachments %1 %2)]
   :save-media?       [true #(.saveMedia %1 %2)]
   ;; set browser screen dimentions - 1366x768 by default (we're a laptop)
   :screen-size       [[1366 768] (fn [builder [w h]]
                                    (.screen builder (org.openqa.selenium.Dimension. w h)))]
   ;; be accepting of weird SSL certs
   :ssl-policy        ["compatible" #(.ssl %1 %2)]
   ;; We're in New York, no matter where we are
   :timezone          [com.machinepublishers.jbrowserdriver.Timezone/AMERICA_NEWYORK #(.timezone %1 %2)]
   ;; no, really, we're Chrome
   :user-agent        [com.machinepublishers.jbrowserdriver.UserAgent/CHROME #(.userAgent %1 %2)]
   ;; SSL certificate verification, off by default because the internet is broken
   :verify-hostname?  [false #(.hostnameVerification %1 %2)]})

(defn make-browser
  "Creates a new headless browser instance parameterized by `options`. Examples:

```clojure
  (make-browser :log-wire true)  ; log network traffic

  (make-browser :ajax-wait 1000) ; wait 1000ms for JS to run after page load

  (make-browser :headless false) ; pop up a browser window to watch it work
```
  "
  [& options]
  (assert (or (= nil options) (even? (count options)))
          "The options to make-browser must be an even number of key-value pairs.")
  (let [merged-opts (->> (partition 2 options)
                         (reduce (fn [a [k v]]
                                   (if-let [[default setter-fn] (get browser-options k)]
                                     (assoc a k [v setter-fn])
                                     (throw (IllegalArgumentException. "Invalid browser option."))))
                                 browser-options)
                         vals)]
    (com.machinepublishers.jbrowserdriver.JBrowserDriver.
     (.build (reduce (fn [builder [v f]]
                       (f builder v))
                     (com.machinepublishers.jbrowserdriver.Settings/builder)
                     merged-opts)))))

(defn close-browser!
  "Close a browser instance, killing the underlying subprocess and freeing all resources."
  [browser]
  ;; upstream bug causes crash when not on the first window when there
  ;; are multiple open windows
  (let [handles (.getWindowHandles browser)]
    (when (> (count handles) 1)
      (.window (.switchTo browser) (first handles))))
  (.quit browser))

(defmacro with-browser
  "Evaluate `body` in a try block within which `binding` is bound, finally calling close-browser on binding. This is just a version of with-open that traps the exception that closing a jBrowserDriver instance currently throws."
  [binding & body]
  ;; assert-args is private to clojure.core 😿
  ;; (assert-args
  ;;    (vector? binding) "a vector for its binding"
  ;;    (= 1 (count binding)) "a single name is expected"
  ;;    (symbol? (binding 0)) "the name should be a symbol")
  `(let ~(subvec binding 0 2)
     (try
       ~@body
       (finally (close-browser! ~(binding 0))))))

(defn fetch!
  "Fetch 'url' using 'browser' and return browser after loading is complete.

```clojure
  (fetch! browser \"http://www.clojure.com/\")
  ;;=> returns browser after navigating to the clojure site
```"
  [browser url]
  (.get browser (str url))
  browser)

(defn status-code
  "Return the current HTTP status code of `browser`."
  [browser]
  (.getStatusCode browser))

(defn current-url
  "Return the currently visited url of `browser`."
  [browser]
  (.getCurrentUrl browser))

(defn page-source
  "Return the HTML source of the currently visited page in `browser`."
  [browser]
  (.getPageSource browser))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; browser windows

(defn current-window
  "Return a handle for the currently active `browser` window."
  [browser]
  (.getWindowHandle browser))

(defn all-windows
  "Return a set of handles for all `browser` windows."
  [browser]
  (.getWindowHandles browser))

(defn switch-to-window
  "Switch `browser` to `window`, which should be a handle returned by all-windows or current-window."
  [browser window]
  (.window (.switchTo browser) window))

(defn maximize-window
  "Maximize `browser`'s currently focused window."
  [browser]
  (.maximize (.window (.manage browser))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; alert handling

(defn switch-to-alert
  "Switch `browser`'s focus to the current alert popup. Returns the alert's handle."
  [browser]
  (.alert (.switchTo browser)))

(defn accept-alert!
  "Accept an alert."
  [alert]
  (.accept alert))

(defn dismiss-alert!
  "Dismisses an alert."
  [alert]
  (.dismiss alert))

;; XXX currently locks up and never comes back! Upstream problem in
;; jBrowserDriver?
;;
;; (defn alert-text
;;   "Returns the text of an alert."
;;   [alert]
;;   (.getText alert))

;; TODO add typing into alerts...
;;void	sendKeys(String keysToSend) 

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; exec arbitrary javascript

(defn execute-script
  "Executes JavaScript `script` in the context of the currently selected frame or window of `browser` with `arguments`."
  [browser script & arguments]
  (.executeScript browser script (to-array arguments)))

(defn execute-script-async
  "Asynchronously execute JavaScript `script` in the context of the currently selected frame or window of `browser` with `arguments`."
  [browser script & arguments]
  (.executeAsyncScript browser script (to-array arguments)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; interrogate logs

(defn available-log-types
  "Return a set of strings representing the types of logs available for `browser`. N.B. `browser` must have been initialized with some kind of logging enabled, for isntance :log-trace or :log-wire."
  [browser]
  (.getAvailableLogTypes (.logs (.manage browser))))

(defn logs
  "Return `browser`'s logs of whatever `kind`, or all if not specified. See http://machinepublishers.github.io/jBrowserDriver/org/openqa/selenium/logging/LogEntry.html for details of the returned items. Example:

```
  (map #(.getMessage %) (logs browser))
  ;;=> events that have occurred since the last time you did this
```"
  [browser & kind]
  (iterator-seq (.iterator (.get (.logs (.manage browser)) (or kind "all")))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn page-wait
  "Blocks until the browser's current page quiesces. N.B. This is the default for many operations, so this function is likely only useful when things are misbehaving."
  [browser]
  (.pageWait browser))

(defn attachments-dir
  "Return the path to the directory where the browser is storing attachment files."
  [browser]
  (.attachmentsDir browser))

(defn media-dir
  "Return the path to the directory where the browser is storing media files."
  [browser]
  (.mediaDir browser))

(defn cache-dir
  "Return the path to the directory where the browser is storing cached files."
  [browser]
  (.cacheDir browser))

(defn page-text
  "Return the complete visible textual content of the current page in the focused window of `browser`. Text from hidden elements is not included."
  [browser]
  (elem/text (elem/find-by-tag browser "html")))

(defn title
  "Return the title of the current page in the focused window of `browser`."
  [browser]
  (elem/inner-html (elem/find-by-css browser "head title")))
