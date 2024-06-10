FROM maven:3.8.4-openjdk-17 as builder
COPY . /app/.
RUN mvn -f /app/pom.xml clean package -Dmaven.test.skip=true

FROM ubuntu:20.04
ENV TZ=EU \
    DEBIAN_FRONTEND=noninteractive
RUN \
    set -eux              ; \
    apt-get -qq update    ; \
    apt-get -yqq install    \
        python3 python3-pip \
        openjdk-17-jdk -y;
COPY --from=builder /app/target/*.jar /app/app.jar
COPY /src/main/resources/converter.py /app/converter.py
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]