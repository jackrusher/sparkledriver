(ns sparkledriver.element
  (:require [sparkledriver.retry :refer [*retry-fn*]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; finding elements

(defn find-by-id
  "Return the single element with `id` that's a child of `browser-or-elem` (which can be a browser or element). Throws an exception if there's no such element! If called with *retry-fn* bound - for example by using the `with-retry` macro - will wrap the operation in that function."
  [browser-or-elem id]
  (*retry-fn* #(.findElementById browser-or-elem id)))

(defn find-by-tag
  "Return the first element with tag name `tag` that's a child of `browser-or-elem` (which can be a browser or element). Throws an exception if there's no such element! If called with *retry-fn* bound - for example by using the `with-retry` macro - will wrap the operation in that function."
  [browser-or-elem tag]
  (*retry-fn* #(.findElementByTagName browser-or-elem tag)))

(defn find-by-tag*
  "Return all elements with tag name `tag` that are children of `browser-or-elem` (which can be a browser or element) or an empty sequence. If called with *retry-fn* bound - for example by using the `with-retry` macro - will wrap the operation in that function."
  [browser-or-elem tag]
  (*retry-fn* #(.findElementsByTagName browser-or-elem tag)))

(defn find-by-class
  "Return the first element with class name `class` that's a child of `browser-or-elem` (which can be a browser or element). Throws an exception if there's no such element! If called with *retry-fn* bound - for example by using the `with-retry` macro - will wrap the operation in that function."
  [browser-or-elem class]
  (*retry-fn* #(.findElementByClassName browser-or-elem class)))

(defn find-by-class*
  "Return all elements with class name `class` that are children of `browser-or-elem` (which can be a browser or element) or an empty sequence. If called with *retry-fn* bound - for example by using the `with-retry` macro - will wrap the operation in that function."
  [browser-or-elem class]
  (*retry-fn* #(.findElementsByClassName browser-or-elem class)))

(defn find-by-xpath
  "Return the first element that matches `xpath`, starting from `browser-or-elem` (which can be a browser or element). Throws an exception if there's no such element! If called with *retry-fn* bound - for example by using the `with-retry` macro - will wrap the operation in that function.

  For example:

```
(with-browser [browser (make-browser)]
  (-> (fetch! browser \"http://clojure.org\")
      (find-by-xpath* \"//div[@class='clj-intro-message']/p\")
      (nth 2)
      text))
;;=> \"I hope you find Clojure's combination of facilities elegant, powerful, practical and fun to use.\"
```"
  [browser-or-elem xpath]
  (*retry-fn* #(.findElementByXPath browser-or-elem xpath)))

(defn find-by-xpath*
  "Return all elements that match `xpath`, starting from `browser-or-elem` (which can be a browser or element) or an empty sequence. If called with *retry-fn* bound - for example by using the `with-retry` macro - will wrap the operation in that function."
  [browser-or-elem xpath]
  (*retry-fn* #(.findElementsByXPath browser-or-elem xpath)))

(defn find-by-css
  "Return the first element that matches `css-selector`, starting from `browser-or-elem` (which can be a browser or element). Throws an exception if there's no such element! If called with *retry-fn* bound - for example by using the `with-retry` macro - will wrap the operation in that function."
  [browser-or-elem css-selector]
  (*retry-fn* #(.findElementByCssSelector browser-or-elem css-selector)))

(defn find-by-css*
  "Return all elements that match `css-selector`, starting from `browser-or-elem` (which can be a browser or element) or an empty sequence. If called with *retry-fn* bound - for example by using the `with-retry` macro - will wrap the operation in that function."
  [browser-or-elem css-selector]
  (*retry-fn* #(.findElementsByCssSelector browser-or-elem css-selector)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; interrogating elements returned by the previous functions

(defn tag
  "Return the tag name of `element`"
  [element]
  (.getTagName element))

(defn id
  "Return the value of the id attribute of `element`"
  [element]
  (.getAttribute element "id"))

(defn text
  "Return the complete visible textual content of `element` (including any children). Text from hidden elements is not included."
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

(defn outer-html
  "Return the outerHTML of `element`"
  [element]
  (.getAttribute element "outerHTML"))

(defn screenshot
  "Takes a screenshot of `browser`'s current state and stores the image in a temporary file, then returns the file's absolute path.

(N.B. this function is in the element namespace because it should take a browser or an element, but there's presently an [upstream bug](https://github.com/jackrusher/sparkledriver/issues/12) preventing this from working.)"
  [browser]
  (-> (.getScreenshotAs browser org.openqa.selenium.OutputType/FILE)
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
  "Submit `element`, which should be an HTML form."
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
