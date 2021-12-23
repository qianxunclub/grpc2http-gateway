FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY grpc-http-gateway-core/target/grpc-http-gateway-core-0.0.1-SNAPSHOT.jar grpc-http-gateway.jar
ENTRYPOINT ["java", "-jar", "grpc-http-gateway.jar"]