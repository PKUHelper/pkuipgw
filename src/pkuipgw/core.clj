(ns pkuipgw.core
  (:require [pkuipgw.ipgw-client :as client])
  (:require [clojure.pprint :as pp])
  (:require [clojure.string :as string])
  (:require [clojure.tools.cli :as cli])
  (:gen-class))

;; Lein passes in the project version as a system prop at compile time.
;; We use this macro to save it as a static value so we can use it at run time.
(defmacro get-version [] (System/getProperty "pkuipgw.version"))

(def version-message (str "pkuipgw version " (get-version)))

(defn format-lines
  [lens tables]
  (let [fmt (case (count lens)
              2 "~{  ~vA  ~vA~}"
              3 "~{  ~vA  ~vA  ~vA~}")]
    (map #(string/trimr (pp/cl-format nil fmt (interleave lens %))) tables)))

(defn table-string
  [tables]
  (if (empty? tables)
    ""
    (let [lens (apply map (fn [& cols] (apply max (map count cols))) tables)
          lines (format-lines lens tables)]
      (string/join \newline lines))))

;; The valid actions.
(def action-map [[:connect ["c" "connect"] "Connect to PKU IP Gateway"]
                 [:disconnect ["d" "disconnect"] "Disconnect from PKU IP Gateway"]
                 [:list ["l" "list"] "Show the list of current connections"]])

;; i.e.
;;   c, connect     Connect to PKU IP Gateway
;;   d, disconnect  Disconnect from PKU IP Gateway
;;   l, list        Show the list of current connections
(def action-summary
  (let [tables (map (fn [[_ coll desc]] [(string/join ", " coll) desc]) action-map)]
    (table-string tables)))

;; The options shown in short usage.
;; i.e. Usage: pkuipgw [--help] [--version] <action> [<options>]
(def cli-options-prefix
  [[nil "--help" "Show help information"]
   [nil "--version" "Show version information"]])

(def cli-options-disconnect
  [["-a" "--all" "(Must be used with 'disconnect' action) Disconnect all connections"]
   [nil "--ip IP" "(Must be used with 'disconnect' action) Disconnect the connection with the specific IP"]])

(def cli-options-root
  (concat cli-options-prefix
          [["-u" "--user-id USER_ID" "Your IAAA account"]
           ["-p" "--password PASSWORD" "Your IAAA password"]]
          cli-options-disconnect))

;; i.e. Usage: pkuipgw [--help] [--version] <action> [<options>]
(def short-usage (str "Usage: pkuipgw"
                      (reduce (fn [cur-str [short-op long-op & _]]
                                (str cur-str " [" (string/join " | " (filter some? [short-op long-op])) "]"))
                              ""
                              cli-options-prefix)
                      " <action> [<options>]"))

(defn usage [options-summary]
  (->> [""
        ">>>> pkuipgw: PKU IP Gateway connection handler. https://github.com/PKUHelper/pkuipgw <<<<"
        ""
        short-usage
        ""
        "Actions:"
        action-summary
        ""
        "Options:"
        options-summary
        ""
        "Please refer to the manual page for more information."]
       (string/join \newline)))

(defn create-error-msg [errors]
  (str (string/join \newline errors) "\n" short-usage))

(defn parse-arguments [args]
  (let [arg-count (count args)
        arg (first args)
        action (reduce #(or %1 %2) (map (fn [[action coll]] (if ((set coll) arg) action)) action-map))]
    (cond
      (not= 1 arg-count) {:arg-error (str "Wrong action count: " arg-count)}
      action {:action action}
      :else {:arg-error (str "Unknown action: " arg)})))

(defn validate-args
  [args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args (concat cli-options-root))
        {:keys [action arg-error]} (parse-arguments arguments)]
    (cond
      ;; show help info
      (options :help) {:exit-message (usage summary), :ok? true}
      ;; show version info
      (options :version) {:exit-message version-message, :ok? true}
      ;; alert cli errors
      errors {:exit-message (create-error-msg errors)}
      ;; alert argument errors i.e. [Wrong action count] [Unknown action]
      arg-error {:exit-message (create-error-msg [arg-error])}
      action {:action action, :options options}
      :else {:exit-message short-usage})))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn drop-error-api-response [{:keys [code message payload]}]
  (if (zero? code)
    payload
    (exit 1 (str "Error: " message))))

(defn handle-connect [options]
  (let [resp (client/send-request :connect options)
        payload (drop-error-api-response resp)
        msg (->> ["Connection success!"
                  "Status:"
                  (table-string payload)]
                 (string/join \newline))]
    (exit 0 msg)))

(defn handle-list [options]
  (let [resp (client/send-request :get-connections options)
        {:strs [connections]} (drop-error-api-response resp)
        raw-connections (map vals connections)
        total (str "total " (count connections))
        msg (str total \newline (table-string raw-connections))]
    (exit 0 msg)))

(defn handle-disconnect-current [options]
  (let [resp (client/send-request :disconnect options)
        _ (drop-error-api-response resp)
        msg "Disconection success!"]
    (exit 0 msg)))

(defn handle-disconnect-all [options]
  (let [resp (client/send-request :disconnect-all options)
        _ (drop-error-api-response resp)
        msg "Disconection success! All connections are break."]
    (exit 0 msg)))

(defn handle-disconnect-ip [options]
  (let [resp (client/send-request :disconnect-ip options)
        _ (drop-error-api-response resp)
        msg (str "Disconection success! Connection at " (options :ip) " is break.")]
    (exit 0 msg)))

(defn handle-disconnect [options]
  (cond
    (options :all) (handle-disconnect-all options)
    (options :ip) (handle-disconnect-ip options)
    :else (handle-disconnect-current options)))

(defn -main [& args]
  (let [{:keys [action options exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (case action
        :connect (handle-connect options)
        :list (handle-list options)
        :disconnect (handle-disconnect options)))))
