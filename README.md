# Sparkledriver

![Sparkledriver glitter tractor](https://raw.githubusercontent.com/jackrusher/sparkledriver/master/assets/SparkleDriver.jpg)

A slightly incomplete but fairly idiomatic wrapper
around
[jBrowserDriver](https://github.com/MachinePublishers/jBrowserDriver),
which is a pure-Java [Selenium]()-compatible interface to
the [WebKit](https://webkit.org) browser library included in recent
JVM versions (>=1.8) as part
of
[JavaFX](http://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm#JFXST784).

This library is useful when you need a full-featured browser with
support for cookies, Javascript, and so on, with no native
dependencies. I've found it to be a nice basis for testing web
applications, scraping content from Javascript-heavy websites,
automating interactions with services, and so on.

## Usage

Here's a quick start:

``` clojure
[sparkledriver "0.1.8"]
```

``` clojure
(require '[sparkledriver.core :refer [with-browser make-browser fetch! find-by-xpath* text]])

(with-browser [browser (make-browser)]
  (-> (fetch! browser "http://clojure.org")
      (find-by-xpath* "//div[@class='clj-intro-message']/p")
      (nth 2)
      text))
;;=> "I hope you find Clojure's combination of facilities elegant, powerful, practical and fun to use."
```

While I intend to write more documentation at some point, for the time
being there are doc strings for every function and macro, and the
tests provide usage examples for many common tasks.

## A Note on Completeness

While there are still a few areas where the underlying APIs could be
more completely wrapped, the core functionality has been enrobed in
delicious parentheses. While some bits are better tested than others,
but I *am* currently using this library in production.

## License

Copyright © 2016, 2017 Jack Rusher

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
