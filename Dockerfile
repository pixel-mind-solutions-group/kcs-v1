FROM openjdk:21-jdk

ARG WAR_FILE=target/pdev-kcs-service.war

COPY ${WAR_FILE} pdev-kcs-service.war

ENTRYPOINT ["java", "-jar", "/pdev-kcs-service.war"]

EXPOSE 8201