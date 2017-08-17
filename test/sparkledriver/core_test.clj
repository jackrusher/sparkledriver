(ns sparkledriver.core-test
  (:require [clojure.test :refer :all]
            [sparkledriver.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.core :refer [html]])
  (:import java.net.URI))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; setup a local webserver and test page

(def port 9999)
(declare start-server stop-server)

(use-fixtures :once (fn [tests]
                      (start-server port)
                      (tests)
                      (stop-server)))

(defonce server-handle (atom nil))

(def test-page
  [:html
   [:head
    [:title "The Second Coming"]
    [:meta {:name "turning and turning" :content "in the widening gyre"}]
    [:meta {:name "the falcon" :content "cannot hear the falconer"}]
    [:meta {:name "things fall apart" :content "the centre cannot hold"}]]
   [:body
    [:div
     [:h1#heroic "Sparkledriver, driven"]
     [:div.emptiness]
     [:p [:span {:data-direction "slouching towards bethlehem"}
          "And what rough beast, its hour come round at last"]]
     [:div.emptiness]
     [:p.vast-image "Spiritus Mundi"]
     [:div.emptiness]]
    [:div
     [:p#loosed-upon-the-world.some-revelation {:style "color:red;"}
      [:span.mere-anarchy "The blood-dimmed tide is loosed, and everywhere"]
      [:span.mere-anarchy "The ceremony of innocence is drowned"]]]]])

(defroutes taxi-server
  (GET "/" [] (html test-page))
  (route/not-found "<h1>Page not found</h1>"))

(defn stop-server []
  (when @server-handle (@server-handle)))

(defn start-server [port]
  (swap! server-handle
         (fn [_]
           (stop-server) ;; kill the old server first, if needed
           (run-server taxi-server {:port port}))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; tests against the local test page

(deftest basic-tests
  (with-browser [browser (fetch! (make-browser) (str "http://0.0.0.0:" port))]
    (testing "loads the page"
      (is (= 200 (status-code browser))))

    (testing "tag selectors"
      (is (= (-> (find-by-tag browser "h1") text)
             "Sparkledriver, driven"))
      (is (= (-> (find-by-tag browser "span") (attr "data-direction"))
             "slouching towards bethlehem"))
      (is (= (sort (mapv #(attr % "name") (find-by-tag* browser "meta")))
             '("the falcon" "things fall apart" "turning and turning"))))
    
    (testing "class selectors"
      (is (= (text (find-by-class browser "vast-image"))
             "Spiritus Mundi"))
      (is (= (distinct (map tag (find-by-class* browser "emptiness")))
             '("div"))))
    
    (testing "css selectors"
      (is (= (mapv text (find-by-css* browser "span.mere-anarchy"))
             ["The blood-dimmed tide is loosed, and everywhere"
              "The ceremony of innocence is drowned"])))

    (testing "id selectors"
      (is (= (text (find-by-id browser "heroic"))
             "Sparkledriver, driven"))
      (is (= (css-value (find-by-id browser "loosed-upon-the-world") "color")
             "rgba(255, 0, 0, 1)")))

    (testing "xpath selectors"
      (is (= (-> (find-by-xpath browser "//h1") text)
             "Sparkledriver, driven"))
      (is (= (-> (find-by-xpath* browser "//p[@class='some-revelation']/span")
                 (second)
                 text)
             "The ceremony of innocence is drowned")))

    (testing "inner/outer html"
      (is (= (-> (find-by-xpath browser "//h1") outer-html)
             "<h1 id=\"heroic\">Sparkledriver, driven</h1>"))
      (is (= (-> (find-by-xpath browser "//h1") inner-html)
             "Sparkledriver, driven")))
    
    (testing "javascript execution"
      ;; use a script to set an attribute
      (execute-script browser "document.getElementById(\"heroic\")[\"data-villainous\"] = \"frenemy\";")
      (is (= (attr (find-by-id browser "heroic") "data-villainous")
             "frenemy"))
      ;; pass an element as an argument to javascript, receive it in return, get an attr from it
      (is (= (attr (execute-script browser "return arguments[0];", (find-by-id browser "heroic")) "data-villainous")
             "frenemy")))

    (testing "page helpers"
      (is (= (page-text browser) "Sparkledriver, driven\n\nAnd what rough beast, its hour come round at last\n\nSpiritus Mundi\n\nThe blood-dimmed tide is loosed, and everywhereThe ceremony of innocence is drowned"))
      (is (= (title browser) "The Second Coming")))

    (testing "cookies"
      (is (= (-> browser
                 (set-cookie! "no-domain" "uses current domain")
                 (set-cookie! "no-domain" "can be changed")
                 (set-cookie! "with-domain" "SparkleDriver" :domain "example.org" :path "/home")
                 browser-cookies->map)
             {:no-domain   {:domain "0.0.0.0", :path "/", :value "can be changed"},
              :with-domain {:domain "example.org", :path "/home", :value "SparkleDriver"}}))

      (is (= (-> browser
                 delete-all-cookies!
                 browser-cookies->map) {}))

      (is (= (-> browser
                 (set-cookie! "a-cookie" "no domain" :path "/foo")
                 (set-cookie! "a-cookie" "no domain" :path "/bar")
                 (set-cookie! "a-cookie" "some domain" :domain "example.org")
                 (set-cookie! "a-cookie" "some domain" :domain "example.org" :http-only true, :secure true, :path "/foo")
                 (set-cookie! "a-cookie" "some other domain" :domain "clojure.org")
                 (.. manage getCookies)
                 (->> (map str))
                 sort)
             ["a-cookie=no domain; path=/bar; domain=0.0.0.0"
              "a-cookie=some domain; path=/foo; domain=example.org;secure;"
              "a-cookie=some other domain; path=/; domain=clojure.org"])))))

(deftest fetch-test
  (with-browser [browser (make-browser)]
    (testing "it coerces the URL to string"
      (let [url (str "http://0.0.0.0:" port "/the/path")]
        (is (= (current-url (fetch! browser (URI. url))) url))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; for quick interactive tests

;;(def browser (make-browser))

;; (page-source browser)

;; (start-server port)
;; (with-browser [browser (fetch! (make-browser :screen-size [1900 1000]) (str "http://0.0.0.0:" port))]
;;   (-> (find-by-tag browser "h1") inner-html)
;;   )
