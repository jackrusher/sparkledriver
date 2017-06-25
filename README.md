# Sparkledriver

![Sparkledriver glitter tractor](https://raw.githubusercontent.com/jackrusher/sparkledriver/master/assets/SparkleDriver.jpg)

An slightly incomplete but fairly idiomatic wrapper
around
[jBrowserDriver](https://github.com/MachinePublishers/jBrowserDriver),
which is a pure-Java [Selenium]()-compatible wrapper around
the [WebKit](https://webkit.org) browser library included in recent
(>=1.8) JVMs as part of
the
[JavaFX](http://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm#JFXST784) framework.

This library is particularly useful if you have need of a
full-featured browser with support for Javascript, cookies, and the
lot, from Clojure. One can use such a thing to interact with a website
in many ways, including testing web applications, scraping content,
automating interactions with services, and so on.

## Usage

I intend to write more documentation soon, but in the meantime here's
a quick start:

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

In the meantime, there are doc strings for every function and macro,
and the tests provide many more usage examples.

## A Note on Completeness

While there are still a number of areas where the underlying APIs
could be better and more completely wrapped, the core functionality is
now largely enrobed in delicious parentheses. While some bits are
better tested than others, I *am* currently using this library in
production with no problems.

## License

Copyright © 2016, 2017 Jack Rusher

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
