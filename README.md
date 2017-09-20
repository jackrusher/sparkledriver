# Sparkledriver

![Sparkledriver glitter tractor](https://raw.githubusercontent.com/jackrusher/sparkledriver/master/assets/SparkleDriver.jpg)

A mostly idiomatic wrapper
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

**N.B.** While Oracle's JVM bundles JavaFX, the OpenJDK JVM does not
come with it. If you're using OpenJDK (which is the default on
_ubuntu_, for example), you must also install the OpenJFX package on
your chosen platform in order to use this library.

## Quick Start

``` clojure
[sparkledriver "0.2.0"]
```

This example fetches the Clojure homepage, extracts some elements and
retrieves the text of one:

``` clojure
(require '[sparkledriver.browser :refer [with-browser make-browser fetch!]])
(require '[sparkledriver.element :refer [find-by-xpath* text]])

(with-browser [browser (make-browser)]
  (-> (fetch! browser "http://clojure.org")
      (find-by-xpath* "//div[@class='clj-intro-message']/p")
      (nth 2)
      text))
;;=> "I hope you find Clojure's combination of facilities elegant, powerful, practical and fun to use."
```

There are many more specific examples in the test suite. In additon,
the automatically
generated [codox](https://github.com/weavejester/codox) documentation
is [here](https://jackrusher.github.io/sparkledriver/).

## A Note on Dependencies

A case has
been [reported](https://github.com/jackrusher/sparkledriver/issues/5)
where an old version of `common-codec`, as a library dependency, was
causing the tests to fail. If you experience this issue, please add
the following dependency:

```clj
[commons-codec "1.9"]
```

to your project.

## Completeness/Robustness

While there are still a few areas where the underlying APIs could be
more completely wrapped, the core functionality has been enrobed in
delicious parentheses. Some bits are better tested than others, but I
*am* currently using this library in production.

## License

Copyright © 2016, 2017 Jack Rusher

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
