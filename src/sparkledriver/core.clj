(ns sparkledriver.core)

(def browser-options
  "The possible options for building a browser instance. The format is {:option [default setter-fn]}."
  {;; how load to wait for resources loaded by ajax
   :ajax-load-timeout [30000 #(.ajaxResourceTimeout %1 %2)]
   ;; how long to wait for JS to run after page load
   :ajax-wait         [200 #(.ajaxWait %1 %2)]
   ;; use a local browser cache
   :cache             [true #(.cache %1 %2)]
   ;; if false, open a window so you can watch it work
   :headless          [true #(.headless %1 %2)]
   ;; increased logging
   :log-trace         [false #(.logTrace %1 %2)]
   :log-wire          [false #(.logWire %1 %2)]
   :request-headers   [com.machinepublishers.jbrowserdriver.RequestHeaders/CHROME #(.requestHeaders %1 %2)]
   ;; store copies of media and attachments in a temporary folder
   :save-attachments  [true #(.saveAttachments %1 %2)]
   :save-media?       [true #(.saveMedia %1 %2)]
   ;; set browser screen dimentions - 1366x768 by default (we're a laptop)
   :screen-size       [[1366 768] (fn [builder [w h]]
                                    (.screen builder (org.openqa.selenium.Dimension. w h)))]
   ;; be accepting of weird SSL certs
   :ssl-policy        ["compatible" #(.ssl %1 %2)]
   :timezone          [com.machinepublishers.jbrowserdriver.Timezone/AMERICA_NEWYORK #(.timezone %1 %2)]
   :user-agent        [com.machinepublishers.jbrowserdriver.UserAgent/CHROME #(.userAgent %1 %2)]
   ;; SSL certificate verification, off by default because the internet is broken
   :verify-hostname?  [false #(.hostnameVerification %1 %2)]})

(defn make-browser
  "Creates a new headless browser instance."
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
  ;; jBrowserDriver has a bug where closing the browser always throws an exception!
  ;; https://github.com/MachinePublishers/jBrowserDriver/issues/189
  (try
    (.close browser)
    (catch Exception e nil)))

(defmacro with-browser
  "Evaluate `body` in a try block within which `binding` is bound, finally calling close-browser on binding. This is just a version of with-open that traps the exception that closing a jBrowserDriver currently throws. :("
  [binding & body]
  ;; assert-args is private to clojure.core :(
  ;; (assert-args
  ;;    (vector? binding) "a vector for its binding"
  ;;    (= 1 (count binding)) "a single name is expected"
  ;;    (symbol? (binding 0)) "the name should be a symbol")
  `(let ~(subvec binding 0 2)
     (try
       ~@body
       (finally (close-browser! ~(binding 0))))))

(defn fetch!
  "Fetch 'url' using 'browser', return browser after loading is complete."
  [browser url]
  (.get browser url)
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
;; finding elements

(defn find-by-id
  "Return the single element with `id` that's a child of `browser-or-elem` (which can be a browser or element). Throws an exception if there's no such element!"
  [browser-or-elem id]
  (.findElementById browser-or-elem id))

(defn find-by-tag
  "Return the first element with tag name `tag` that's a child of `browser-or-elem` (which can be a browser or element). Throws an exception if there's no such element!"
  [browser-or-elem tag]
  (.findElementByTagName browser-or-elem tag))

(defn find-by-tag*
  "Return all elements with tag name `tag` that are children of `browser-or-elem` (which can be a browser or element) or an empty sequence."
  [browser-or-elem tag]
  (.findElementsByTagName browser-or-elem tag))

(defn find-by-class
  "Return the first element with class name `class` that's a child of `browser-or-elem` (which can be a browser or element). Throws an exception if there's no such element!"
  [browser-or-elem class]
  (.findElementByClassName browser-or-elem class))

(defn find-by-class*
  "Return all elements with class name `class` that are children of `browser-or-elem` (which can be a browser or element) or an empty sequence."
  [browser-or-elem class]
  (.findElementsByClassName browser-or-elem class))

(defn find-by-xpath
  "Return the first element that matches `xpath`, starting from `browser-or-elem` (which can be a browser or element). Throws an exception if there's no such element!"
  [browser-or-elem xpath]
  (.findElementByXPath browser-or-elem xpath))

(defn find-by-xpath*
  "Return all elements that match `xpath`, starting from `browser-or-elem` (which can be a browser or element) or an empty sequence."
  [browser-or-elem xpath]
  (.findElementsByXPath browser-or-elem xpath))

(defn find-by-css
  "Return the first element that matches `xpath`, starting from `browser-or-elem` (which can be a browser or element). Throws an exception if there's no such element!"
  [browser-or-elem css-selector]
  (.findElementByCssSelector browser-or-elem css-selector))

(defn find-by-css*
  "Return all elements that match `xpath`, starting from `browser-or-elem` (which can be a browser or element) or an empty sequence."
  [browser-or-elem css-selector]
  (.findElementsByCssSelector browser-or-elem css-selector))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Interrogating elements

(defn tag
  "Return the tag name of `element`"
  [element]
  (.getTagName element))

(defn id
  "Return the value of the id attribute of `element`"
  [element]
  (.getAttribute element "id"))

(defn text
  "Return the complete textual content of `element` (including any children)."
  [element]
  (.getText element))

(defn attr
  "Return the value of `attribute` for `element`"
  [element attribute]
  (.getAttribute element attribute))

(defn css-value
  "Return the value of CSS `property` for `element`"
  [element property]
  (.getCssValue element property))

(defn inner-html
  "Return the innerHTML of `element`"
  [element]
  (.getAttribute element "innerHTML"))

(defn screenshot
  "Takes a screenshot of `browser-or-element`'s current state and stores the image in a temporary file, then returns the file's absolute path."
  [browser-or-element]
  (-> (.getScreenshotAs browser-or-element org.openqa.selenium.OutputType/FILE)
      (.getAbsolutePath)))

(defn click!
  "Send a browser click event to `element`."
  [element]
  (.click element))

(defn click-all!
  "Click every element in the sequence `elements`. Returns the number of elements clicked."
  [elements]
  (count (map click! elements)))

(defn send-text!
  "Send the string `text` to `element`, as if the user typed it."
  [element text]
  (.sendKeys element (into-array [text])))

(defn clear-text!
  "Clear the current text of text-entry `element`. Used with send-text! to manipulate form fields."
  [element]
  (.clear element))

(defn submit-form!
  "Submit `element`, which should be an HTML <form>."
  [element]
  (.submit element))

(defn displayed?
  "Is this element displayed or not? This method avoids the problem of having to parse an element's CSS."
  [element]
  (.isDisplayed element))

(defn enabled?
  "Is the element currently enabled or not? This will generally return true for everything but disabled input elements."
  [element]
  (.isEnabled element))

(defn selected?
  "Return whether or not this element is selected "
  [element]
  (.isSelected element))

(defn location
  "Location on the page of the top left-hand corner of the rendered element."
  [element]
  (.getLocation element))

(defn size
  "What is the width and height of the rendered element?"
  [element]
  (.getSize element))

(defn rectangle
  "The location and size of the rendered element."
  [element]
  (.getRect element))

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

(defn accept-alert
  "Accept an alert."
  [alert]
  (.accept alert))

(defn dismiss-alert
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
  "Return `browser`'s logs of whatever `kind`, or all if not specified. See http://machinepublishers.github.io/jBrowserDriver/org/openqa/selenium/logging/LogEntry.html for details of the returned items."
  [browser & kind]
  (iterator-seq (.iterator (.get (.logs (.manage browser)) (or kind "all")))))

;; z.B.
;;(map #(.getMessage %) (logs b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn page-wait
  "Blocks until the browser's current page quiesces."
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

(defn browser-cookies->map
  "Convert `browser`'s current cookies into the map format used by clj-http."
  [browser]
  (reduce
   #(assoc %1 (keyword (.getName %2))
           {:domain (.getDomain %2)
            :path   (.getPath %2)
            :value  (.getValue %2)})
   {}
   (.getCookies (.manage browser))))
