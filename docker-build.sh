commit_id=$(git rev-parse HEAD)
mvn clean compile package -B -DskipTests -pl grpc2http-gateway-core -am
docker build -t grpc2http-gateway:"${commit_id}" .

docker tag grpc2http-gateway:"${commit_id}" grpc2http-gateway:latest