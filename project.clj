(defproject sparkledriver "0.2.2"
  :description "A clojure wrapper for jBrowserDriver, which is a Selenium-compatible wrapper around JFX embedded WebKit."
  :url "https://github.com/jackrusher/sparkledriver"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.machinepublishers/jbrowserdriver "1.0.0-RC1"]
                 [org.clojure/math.numeric-tower "0.0.4"]]
  :profiles {:dev {:dependencies [[org.slf4j/slf4j-simple "1.7.25"]
                                  [http-kit "2.3.0-alpha2"]
                                  [compojure "1.6.0"]
                                  [hiccup "1.0.5"]]}
             :codox {:dependencies [[codox-theme-rdash "0.1.2"]]
                     :plugins [[lein-codox "0.10.3"]]
                     :codox {:project {:name "sparkledriver"}
                             :metadata {:doc/format :markdown}
                             :themes [:rdash]
                             :output-path "gh-pages"}}}
  :aliases {"codox" ["with-profile" "codox,dev" "codox"]}
  :deploy-repositories [["releases" :clojars]])
