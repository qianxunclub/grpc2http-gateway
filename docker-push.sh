commit_id=$(git rev-parse HEAD)

docker tag grpc2http-gateway:latest qianxunclub/grpc2http-gateway:latest
docker push qianxunclub/grpc2http-gateway:latest

docker tag grpc2http-gateway:latest qianxunclub/grpc2http-gateway:"${commit_id}"
docker push qianxunclub/grpc2http-gateway:"${commit_id}"

