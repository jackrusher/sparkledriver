(ns sparkledriver.core-test
  (:require [clojure.test :refer :all]
            [sparkledriver.core :refer :all]))

(deftest basic-tests
  (with-browser [browser (fetch! (make-browser) "http://clojure.org")]
    (testing "loaded?"
      (is (= 200 (status-code browser))))

    (testing "exercise the selectors"
      (is (= (-> (find-by-xpath browser "//h2") text)
             "The Clojure Programming Language"))
      (is (= (text (find-by-class browser "clj-header-message"))
             "Clojure is a robust, practical, and fast programming language with a set of useful features that together form a simple, coherent, and powerful tool."))
      (is (= (distinct (map tag (find-by-class* browser "w-container")))
             '("div")))
      (is (= (->> (find-by-tag browser "form") (attr "action"))
             "http://clojure.org/search"))
      (is (= (sort (mapv (partial attr "name") (find-by-tag* browser "meta")))
             '(nil "generator" "google-site-verification" "google-site-verification" "viewport")))
      (is (= (attr "name" (find-by-id browser "wf-form-Search-Form"))
             "wf-form-Search-Form"))
      (is (= (css-value "color" (find-by-id browser "wf-form-Search-Form"))
             "rgba(68, 68, 68, 1)")))

    (testing "Lastly, a few words from Rich Hickey."
      (is (= (-> (find-by-xpath* browser "//div[@class='clj-intro-message']/p")
                 (nth 2)
                 text)
             "I hope you find Clojure's combination of facilities elegant, powerful, practical and fun to use.")))))


