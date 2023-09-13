FROM amazoncorretto:17-alpine-jdk
MAINTAINER uditsharma1632@gmail.com
RUN mkdir /app
WORKDIR /app
COPY target/SpringBootReactiveCRUD-0.0.1-SNAPSHOT.jar /app/crudApp-1.0.0.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "crudApp-1.0.0.jar"]