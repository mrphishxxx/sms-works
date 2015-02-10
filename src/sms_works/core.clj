(ns sms-works.core
  (:require [clojure.edn :as edn]
            [clojure.tools.logging :as log]
            [twilio.core :as twilio]
            [turbovote.resource-config :refer [config]]
            [langohr.consumers :as lc]
            [sms-works.queue :as queue])
  (:gen-class))

(defn select-number
  []
  (rand-nth (config :twilio :numbers)))

(defn send-sms
  "Sends an SMS message using Twilio"
  [sms-map]
  (let [sid (config :twilio :sid)
        token (config :twilio :token)
        {:keys [from to body]} sms-map
        from (or from (select-number))]
    (twilio/with-auth sid token
      (twilio/send-sms {:From from
                        :To to
                        :Body body}))))

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
