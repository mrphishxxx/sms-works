(ns sms-works.queue
  (:require [clojure.tools.logging :as log]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.exchange :as le]
            [langohr.queue :as lq]
            [turbovote.resource-config :refer [config]]))

(def connection (atom nil))
(def channel (atom nil))

(defn initialize []
  (let [max-retries 5]
    (loop [attempt 1]
      (try
        (reset! connection
                (rmq/connect (or (config :rabbit-mq :connection)
                                 {})))
        (log/info "RabbitMQ connected.")
        (catch Throwable t
          (log/warn "RabbitMQ not available:" (.getMessage t) "attempt:" attempt)))
      (when (nil? @connection)
        (if (< attempt max-retries)
          (do (Thread/sleep (* attempt 1000))
              (recur (inc attempt)))
          (do (log/error "Connecting to RabbitMQ failed. Bailing.")
              (throw (ex-info "Connecting to RabbitMQ failed" {:attempts attempt})))))))
  (reset! channel
          (let [ch (lch/open @connection)]
            (lq/declare ch (config :rabbit-mq :queue) {:exclusive false :auto-delete false :durable true})
            (log/info "RabbitMQ channel declared.")
                        ch)))
