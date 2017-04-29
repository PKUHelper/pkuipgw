(ns pkuipgw.ipgw-client
  (:require [pkuipgw.instruction :as instruction])
  (:require [clojure.string :as string])
  (:require [org.httpkit.client :as http-client]))

(def ^:private reserved-argument-map {:user-id  #"\{user_id\}"
                                      :password #"\{password\}"
                                      :ip       #"\{ip\}"})

(defn- create-argument-bundle [{:keys [user-id password ip]}]
  {:user-id (str user-id), :password (str password), :ip (str ip)})

(defn- replace-string-with-reserved-arguments [s bundle]
  (reduce (fn [cur-str [k match]] (string/replace cur-str match (bundle k)))
          s
          reserved-argument-map))

(defn- replace-map-with-reserved-arguments [m bundle]
  (into {} (map (fn [[k v]]
                  (condp #(%1 %2) v
                    keyword? {k v}
                    string? {k (replace-string-with-reserved-arguments v bundle)}
                    map? {k (replace-map-with-reserved-arguments v bundle)}))
                m)))

(defn- create-request
  ([{:keys [url method headers parameters]}]
   (http-client/request {:url         url
                         :method      method
                         :headers     headers
                         :form-params parameters}))
  ([ins bundle]
   (create-request (replace-map-with-reserved-arguments ins bundle))))

(defn send-request [ins-name args]
  (let [bundle (create-argument-bundle args)
        ins (instruction/create-instruction ins-name)
        request (create-request ins bundle)
        response @request]
    (instruction/parse-response ins-name response)))
