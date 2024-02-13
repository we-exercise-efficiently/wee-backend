FROM openjdk:17-jdk-alpine

RUN apk add --no-cache tzdata

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENV WEE_DB_URL=jdbc:mariadb://43.200.2.121:3306/wee_db
ENV WEE_DB_USERNAME=root
ENV WEE_DB_PASSWORD=1234

EXPOSE 8080

ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-jar","/app.jar"]
