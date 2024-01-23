FROM openjdk:17-jdk-alpine

RUN apk add --no-cache tzdata

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-jar","/app.jar"]
