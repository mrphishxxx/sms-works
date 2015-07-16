(defproject sms-works "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [turbovote.resource-config "0.1.4"]
                 [twilio-api "1.0.0"]
                 [com.novemberain/langohr "3.2.0"]]
  :main ^:skip-aot sms-works.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:resource-paths ["dev-resources"]}})
