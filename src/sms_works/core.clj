(ns sms-works.core
  (:require [clojure.edn :as edn]
            [clojure.tools.logging :as log]
            [twilio.core :as twilio]
            [turbovote.resource-config :refer [config]]
            [langohr.consumers :as lc]
            [sms-works.queue :as queue]
            [clojure.string :as str])
  (:gen-class))

(defn select-number
  []
  (rand-nth (config :twilio :numbers)))

(defn present? [s]
  (not (or (nil? s)
           (str/blank? s))))

(defn send-sms
  "Sends an SMS message using Twilio"
  [sms-map]
  (let [sid (config :twilio :sid)
        token (config :twilio :token)
        {:keys [from to body]} sms-map
        from (if (present? from)
               from
               (select-number))
        message {:From from
                 :To to
                 :Body body}
        _ (log/debug "Sending SMS:" (pr-str message))
        result (twilio/with-auth sid token
                 (twilio/send-sms message))]
    (log/debug "Twilio result:" (pr-str @result))))

(defn message-handler
  [ch meta ^bytes payload]
  (let [message (String. payload "UTF-8")]
    (log/info "Handling message")
    (log/debug message)
    (-> message
        edn/read-string
        send-sms)))

(defn -main [& args]
  (log/info "Starting up")
  (log/info "configuration: " (config))
  (queue/initialize)
  (lc/blocking-subscribe @queue/channel
                         (config :rabbit-mq :queue)
                         message-handler
                         {:auto-ack true}))
