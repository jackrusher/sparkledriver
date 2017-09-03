(ns sparkledriver.cookies
  (:require [sparkledriver.browser :as brwsr]))

(defn browser-cookies->map
  "Convert `browser`'s current cookies into the map format used by clj-http. This returns cookies from all domains, but cookie names that are set on multiple domains will only be returned once."
  [browser]
  (reduce
   #(assoc %1 (keyword (.getName %2))
           {:domain (.getDomain %2)
            :path   (.getPath %2)
            :value  (.getValue %2)})
   {}
   (.getCookies (.manage browser))))

(defn delete-all-cookies!
  "Clear all cookies from all domains."
  [browser]
  (.deleteAllCookies (.manage browser))
  browser)

(defn delete-cookie!
  "Delete the named cookie from the given domain. When omitted `domain` defaults to the `browser`'s current domain."
  ([browser name]
   (delete-cookie! browser name (.getHost (java.net.URL. (brwsr/current-url browser)))))
  ([browser name domain]
   (let [manager (.manage browser)
         cookies (.getCookies manager)]
     (when-let [cookie (some (fn [c]
                               (and (= (.getName c) name)
                                    (= (.getDomain c) domain)
                                    c))
                             cookies)]
       (.deleteCookie manager cookie))
     browser)))

(defn- build-selenium-cookie
  "Build an instance of org.openqa.selenium.Cookie with `name`, `value` and `options`."
  [name value options]
  (.build
   (reduce
    (fn [builder [k v]]
      (case k
        :domain (.domain builder v)
        :expires-on (.expiresOn builder v)
        :http-only (.isHttpOnly builder v)
        :secure (.isSecure builder v)
        :path (.path builder v)
        builder))
    (org.openqa.selenium.Cookie$Builder. name value)
    options)))

(defn set-cookie!
  "Set a cookie with given `name` and `value`. If a cookie with the same name and `domain` is already present, it will be replaced. The following keyword arguments are supported.

  - `:domain` (String) The cookie domain, defaults to the current domain of the browser.
  - `:expires-on` (java.util.Date) Expiration date. Must be in the future or the cookie won't be stored.
  - `:http-only` (Boolean) Cookie's HttpOnly flag, disallow access from JavaScript
  - `:secure` (Boolean) Cookie's Secure flag, only serve over HTTPS.
  - `:path` (String) URL path of the cookie."
  [browser name value & {domain :domain :as options}]
  (if domain
    (delete-cookie! browser name domain)
    (delete-cookie! browser name))
  (.addCookie (.manage browser) (build-selenium-cookie name value options))
  browser)
