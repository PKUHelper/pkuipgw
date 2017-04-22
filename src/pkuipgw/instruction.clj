(ns pkuipgw.instruction
  (:require [pkuipgw.config :as config])
  (:require [clojure.string :as string])
  (:require [cheshire.core :as json]))

(def ^:private api-code {:success           0
                         :connection-error  1000
                         :http-status-error 1001
                         :common-error      2000
                         :unknown-format    2001})

(def ^:private method-set #{:get :post :put :delete :head})

(def ^:private instruction-map {:connect         "connect"
                                :disconnect      "disconnect"
                                :disconnect-all  "disconnect_all"
                                :disconnect-ip   "disconnect_ip"
                                :get-connections "get_connections"})

(def ^:private basic-instruction {:url     (config/config :its-url)
                                  :headers {"Accept"     "text/html"
                                            "User-Agent" "IPGWiOS1.2_"}})

(defn- bind-method [ins method]
  {:pre [(method-set method)]}
  (into ins {:method method}))

(defn- bind-post-method [ins] (bind-method ins :post))

(defn- bind-parameters [ins params] (into ins {:parameters params}))

(defn- same-instruction-name? [test-expr expr]
  (if (string? expr)
    (= (instruction-map test-expr) expr)
    (= test-expr expr)))

(defn- valid-instruction-name? [ins-name]
  (or ((set (vals instruction-map)) ins-name) (instruction-map ins-name)))

(defn create-instruction [ins-name]
  {:pre [(valid-instruction-name? ins-name)]}
  (condp same-instruction-name? ins-name
    :connect
    (bind-parameters (bind-post-method basic-instruction)
                     {"app"      "IPGWiOS1.2"
                      "cmd"      "open"
                      "iprange"  "free"
                      "username" "{user_id}"
                      "password" "{password}"})
    :disconnect
    (bind-parameters (bind-post-method basic-instruction)
                     {"cmd" "close"})
    :disconnect-all
    (bind-parameters (bind-post-method basic-instruction)
                     {"cmd"      "closeall"
                      "username" "{user_id}"
                      "password" "{password}"})
    :disconnect-ip
    (bind-parameters (bind-post-method basic-instruction)
                     {"cmd"      "disconnect"
                      "ip"       "{ip}"
                      "username" "{user_id}"
                      "password" "{password}"})
    :get-connections
    (bind-parameters (bind-post-method basic-instruction)
                     {"cmd"      "getconnections"
                      "username" "{user_id}"
                      "password" "{password}"})))

(defn get-instruction-json [ins-name] (json/generate-string (create-instruction ins-name)))

(defn- create-error-api-response [key msg] {:code (api-code key), :message msg})

(defn- create-success-api-response
  ([] (create-success-api-response nil))
  ([payload] {:code (api-code :success), :message "success", :payload payload}))

;; i.e. {"succ":"162.105.75.223;收费;电子学系及周边;2017-04-15 16:02:53;
;;               10.2.212.212;收费;45甲4-6层;2017-04-15 17:54:01"}
(defn- parse-raw-connections [raw]
  (let [first-seq (string/split raw #";")
        second-seq (rest first-seq)
        third-seq (rest second-seq)
        forth-seq (rest third-seq)]
    (map (fn [[ip _ pos t]] {"ip" ip, "position" pos, "connected_at" t})
         (filter #(re-find #"^[0-9]+.[0-9]+.[0-9]+.[0-9]+$" (first %))
                 (map vector first-seq second-seq third-seq forth-seq)))))

(defn- parse-success-response [ins-name payload]
  (condp same-instruction-name? ins-name
    :connect
    (create-success-api-response {"connection_count" (payload "CONNECTIONS")
                                  "balance"          (payload "BALANCE_EN")
                                  "ip"               (payload "IP")})
    :disconnect
    (create-success-api-response)
    :disconnect-all
    (create-success-api-response)
    :disconnect-ip
    (create-success-api-response)
    :get-connections
    (create-success-api-response {"connections" (parse-raw-connections (payload "succ"))})))

(defn- parse-response-body [ins-name resp-body]
  {:pre [(valid-instruction-name? ins-name)]}
  (let [payload (json/parse-string resp-body)
        error-msg (payload "error")
        success? (payload "succ")]
    (cond
      error-msg (create-error-api-response :common-error error-msg)
      success? (parse-success-response ins-name payload)
      :else (create-error-api-response :unknown-format
                                       (str "unknown response format: " resp-body)))))

(defn parse-response [ins-name {:keys [status body error]}]
  (cond
    error (create-error-api-response :connection-error error)
    (not= 200 status) (create-error-api-response :http-status-error (str "HTTP " status))
    :else (parse-response-body ins-name body)))

(defn get-api-response-json [ins-name resp-body]
  (json/generate-string (parse-response-body ins-name resp-body)))
