(defproject sparkledriver "0.1.10"
  :description "A clojure wrapper for jBrowserDriver, which is a Selenium-compatible wrapper around JFX embedded WebKit."
  :url "https://github.com/jackrusher/sparkledriver"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.machinepublishers/jbrowserdriver "0.17.9"]]
  :plugins [[lein-codox "0.10.3"]]
  :profiles {:dev {:dependencies [[org.slf4j/slf4j-simple "1.7.25"]
                                  [http-kit "2.3.0-alpha2"]
                                  [compojure "1.6.0"]
                                  [hiccup "1.0.5"]]}}
  :codox {:output-path "gh-pages"}
  :deploy-repositories [["releases" :clojars]])
