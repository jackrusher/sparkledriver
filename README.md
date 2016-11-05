# Sparkledriver

This is an incomplete but idiomatic-as-possible wrapper around
[jBrowserDriver](https://github.com/MachinePublishers/jBrowserDriver),
which is a pure-Java [Selenium]()-compatible wrapper around the
[WebKit](https://webkit.org) browser library included in recent
(>=1.8) JVMs as part of the
[JavaFX](http://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm#JFXST784)
framework.

This library is particularly useful if you have need of a full
featured browser from within your Clojure code. One can use such a
thing to interact with a website in many ways, including testing web
applications, scraping content, and so on.

## Completeness

The bad news is that -- in the grand tradition of Clojure wrapper
libraries -- I've only built out the parts I've needed for my own
purposes. The good news is that I use this library everyday for
production work, so what's included is very well tested.

## Usage

I intend to write more documentation soon, but in the meantime here's
a quick start:

``` clojure
(require '[sparkledriver.core :refer [with-browser make-browser fetch! find-by-xpath* text]])

(with-browser [browser (make-browser)]
  (-> (fetch! browser "http://clojure.org")
      (find-by-xpath* "//div[@class='clj-intro-message']/p")
      (nth 2)
      text))
;;=> "I hope you find Clojure's combination of facilities elegant, powerful, practical and fun to use."
```

In the meantime, there are doc strings for every function and macro,
and the tests provide several more usage examples.

## License

Copyright © 2016 Jack Rusher

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
