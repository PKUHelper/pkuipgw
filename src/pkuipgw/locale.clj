(ns pkuipgw.locale
  (:require [clojure.java.io :as io])
  (:require [cheshire.core :as json]))

(def ^:private home-path (System/getProperty "user.home"))

(def ^:private config-path (str home-path "/.pkuipgwconfig"))

(defn- config-exist? [] (.exists (io/file config-path)))

(defn load-config []
  (if (config-exist?)
    (json/parse-string (slurp config-path) true)
    {}))

(defn store-config [arg]
  (let [config (into (load-config) (select-keys arg [:user-id :password]))
        config-string (json/generate-string config)]
    (spit config-path config-string)))
