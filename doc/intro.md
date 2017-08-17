# Introduction to sparkledriver

Sparkledriver is a mostly idiomatic wrapper
around
[jBrowserDriver](https://github.com/MachinePublishers/jBrowserDriver),
which is a pure-Java [Selenium](http://seleniumhq.org/)-compatible
interface to the [WebKit](https://webkit.org) browser library included
in recent JVM versions (>=1.8) as part
of
[JavaFX](http://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm#JFXST784).

This library is useful when you need a full-featured browser with
support for cookies, Javascript, and so on, with no native
dependencies. I've found it to be a nice basis for testing web
applications, scraping content from Javascript-heavy websites,
automating interactions with services, and so on.

## Quick Start

``` clojure
[sparkledriver "0.1.10"]
```

This example fetches the Clojure homepage, extracts some elements and
retrieves the text of one:

``` clojure
(require '[sparkledriver.core :refer [with-browser make-browser fetch! find-by-xpath* text]])

(with-browser [browser (make-browser)]
  (-> (fetch! browser "http://clojure.org")
      (find-by-xpath* "//div[@class='clj-intro-message']/p")
      (nth 2)
      text))
;;=> "I hope you find Clojure's combination of facilities elegant, powerful, practical and fun to use."
```
