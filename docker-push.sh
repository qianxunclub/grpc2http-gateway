commit_id=$(git rev-parse HEAD)

docker tag grpc-http-gateway:latest 960339491/grpc-http-gateway:latest
docker push 960339491/grpc-http-gateway:latest

docker tag grpc-http-gateway:latest 960339491/grpc-http-gateway:"${commit_id}"
docker push 960339491/grpc-http-gateway:"${commit_id}"

