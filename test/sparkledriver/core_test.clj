(ns sparkledriver.core-test
  (:require [clojure.test :refer :all]
            [sparkledriver.core :refer :all]))

(deftest basic-tests
  (with-browser [browser (fetch! (make-browser) "http://clojure.org")]
    (testing "loads the page"
      (is (= 200 (status-code browser))))

    (testing "tag selectors"
      (is (= (-> (find-by-tag browser "h2") text)
             "The Clojure Programming Language"))
      (is (= (-> (find-by-tag browser "form") (attr "action"))
             "https://clojure.org/search"))
      (is (= (sort (mapv #(attr % "name") (find-by-tag* browser "meta")))
             '(nil "generator" "google-site-verification" "google-site-verification" "keywords" "viewport"))))
    
    (testing "class selectors"
      (is (= (text (find-by-class browser "clj-header-message"))
             "Clojure is a robust, practical, and fast programming language with a set of useful features that together form a simple, coherent, and powerful tool."))
      (is (= (distinct (map tag (find-by-class* browser "w-container")))
             '("div"))))
    
    (testing "css selectors"
      (is (= (mapv text (find-by-css* browser "span.clj-header-message-highlight"))
             ["robust, practical, and fast" "simple, coherent, and powerful tool"])))

    (testing "id selectors"
      (is (= (attr (find-by-id browser "wf-form-Search-Form") "name")
             "wf-form-Search-Form"))
      (is (= (css-value (find-by-id browser "wf-form-Search-Form") "color")
             "rgba(68, 68, 68, 1)")))

    (testing "xpath selectors"
      (is (= (-> (find-by-xpath browser "//h2") text)
             "The Clojure Programming Language"))
      (is (= (-> (find-by-xpath* browser "//div[@class='clj-intro-message']/p")
                 (nth 2)
                 text)
             "I hope you find Clojure's combination of facilities elegant, powerful, practical and fun to use.")))))
