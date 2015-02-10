FROM clojure:lein-2.5.1

RUN mkdir -p /usr/src/sms-works
WORKDIR /usr/src/sms-works

COPY project.clj /usr/src/sms-works/
RUN lein deps

COPY . /usr/src/sms-works

RUN lein test
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" sms-works-standalone.jar

CMD ["java", "-jar", "sms-works-standalone.jar"]
