(ns sparkledriver.core)

;; TODO should add parameterization for the builder using `args`
(defn make-browser
  "Creates a new headless browser instance."
  [& args]
  (com.machinepublishers.jbrowserdriver.JBrowserDriver.
   (-> (com.machinepublishers.jbrowserdriver.Settings/builder)
       (.headless true)
       (.timezone com.machinepublishers.jbrowserdriver.Timezone/AMERICA_NEWYORK)
       (.userAgent com.machinepublishers.jbrowserdriver.UserAgent/CHROME)
       (.requestHeaders com.machinepublishers.jbrowserdriver.RequestHeaders/CHROME)
       ;; accept bad security from broken sites
       (.ssl "compatible")
       (.hostnameVerification false)
       ;; cache all files the browser encounters until exit
       (.saveMedia true)
       (.saveAttachments true)
       (.cache true)
       ;; ajax timing, super lenient for bad websites!
       (.ajaxResourceTimeout 30000) ; 30 seconds to timeout ajax
       (.ajaxWait 200)              ; 200 ms to run js
       .build)))

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

(defn find-by-id
  "Return the single element with `id` that's a child of `browser-or-elem` (which can be a browser or element)."
  [browser-or-elem id]
  (.findElementById browser-or-elem id))

(defn find-by-tag
  "Return the first element with tag name `tag` that's a child of `browser-or-elem` (which can be a browser or element)."
  [browser-or-elem tag]
  (.findElementByTagName browser-or-elem tag))

(defn find-by-tag*
  "Return all elements with tag name `tag` that are children of `browser-or-elem` (which can be a browser or element)."
  [browser-or-elem tag]
  (.findElementsByTagName browser-or-elem tag))

(defn find-by-class
  "Return the first element with class name `class` that's a child of `browser-or-elem` (which can be a browser or element)."
  [browser-or-elem class]
  (.findElementByClassName browser-or-elem class))

(defn find-by-class*
  "Return all elements with class name `class` that are children of `browser-or-elem` (which can be a browser or element)."
  [browser-or-elem class]
  (.findElementsByClassName browser-or-elem class))

(defn find-by-xpath
  "Return the first element that matches `xpath`, starting from `browser-or-elem` (which can be a browser or element)."
  [browser-or-elem xpath]
  (.findElementByXPath browser-or-elem xpath))

(defn find-by-xpath*
  "Return all elements that match `xpath`, starting from `browser-or-elem` (which can be a browser or element)."
  [browser-or-elem xpath]
  (.findElementsByXPath browser-or-elem xpath))

(defn tag
  "Return the tag name of `element`"
  [element]
  (.getTagName element))

(defn id
  "Return the value of the id attribute of `element`"
  [element]
  (.getAttribute element "id"))

(defn text
  "Return the complete textual content of `element` (including children)."
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

;; TODO where files are dropped
;; (.attachmentsDir browser)
;; (.mediaDir browser)
;; (.cacheDir browser)


