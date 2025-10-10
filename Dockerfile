FROM bellsoft/liberica-openjdk-debian:25

COPY ./target/*.jar app.jar
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081", "--add-opens=java.base/java.util=ALL-UNNAMED", "-jar", "app.jar"]