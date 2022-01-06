FROM java:8
VOLUME /tmp
COPY grpc2http-gateway-core/target/grpc2http-gateway-core-0.0.1-SNAPSHOT.jar grpc2http-gateway.jar
ENTRYPOINT ["java", "-jar", "grpc2http-gateway.jar"]