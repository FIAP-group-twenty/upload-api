# -----------------------------------
# CONFIGURATIONS ONLY FOR DEPLOY
# JAR MUST BE BUILT BEFORE DEPLOYING
# -----------------------------------

#FROM openjdk:17-ea-10-alpine3.13
#WORKDIR /app-docker
#COPY build/libs/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]

# -----------------------------------
# CONFIGURATIONS FOR BUILD AND DEPLOY
# -----------------------------------

FROM gradle:latest AS builder

WORKDIR /app

RUN apt-get update && apt-get install -y openjdk-17-jdk

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

RUN gradle build --no-daemon

FROM openjdk:17-jdk-slim

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]