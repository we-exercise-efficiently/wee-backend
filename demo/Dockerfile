FROM openjdk:17-jdk

RUN apt-get update && apt-get install -y tzdata

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-jar","/app.jar"]