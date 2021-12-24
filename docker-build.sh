commit_id=$(git rev-parse HEAD)
mvn clean compile package -B -DskipTests -pl grpc-http-gateway-core -am
docker build -t grpc-http-gateway:"${commit_id}" .

docker tag grpc-http-gateway:"${commit_id}" grpc-http-gateway:latest