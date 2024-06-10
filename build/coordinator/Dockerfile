FROM maven:3.8.4-openjdk-17 as builder
WORKDIR ../../src/coordinator
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
COPY --from=builder /app/target/*.jar /app/*.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/*.jar"]