# grpc2http-gateway
[![License](https://github.com/qianxunclub/grpc2http-gateway/workflows/maven_build/badge.svg?branch=master)](https://github.com/qianxunclub/grpc2http-gateway/actions/workflows/maven_build.yml)
[![codecov](https://codecov.io/gh/qianxunclub/grpc2http-gateway/branch/master/graph/badge.svg?token=ZEWH3G98RL)](https://codecov.io/gh/qianxunclub/grpc2http-gateway)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/qianxunclub/grpc2http-gateway/blob/main/LICENSE)
## 简介
该服务是基于 [Grpc 反射](https://github.com/grpc/grpc/blob/master/doc/server-reflection.md) 开发的一款 Grpc 换为 Http 请求的工具。  
因为是基于反射开发，所以使用时无需修改 proto 及相关的代码实现，只需在启动服务时开启反射功能。

## 使用方法

### 服务端
因为是基于反射开发，需要服务端开启反射功能，开启方式：
1. 使用 `grpc-spring-boot-starter`
```
grpc.server.reflection-service-enabled=true
```
2. 原生开启
```
Server server = ServerBuilder.forPort(SERVER_PORT)
    .addService(new HelloServiceImpl())
    // 这里开启反射
    .addService(ProtoReflectionService.newInstance())
    .build()
    .start();
```

### grpc2http-gateway 服务添加环境变量配置

#### 添加 GRPC 服务端应用
```
grpc.endpoint.服务名称=dev.coding.oa.com:9902
```

### 启动 grpc2http-gateway 服务

示例 proto
```
syntax = "proto3";

package com.qianxunclub.proto;
option java_outer_classname = "HelloProto";

message HelloRequest {
    string fieldName = 1;
}

message HelloResponse {
}

service HelloService {
    rpc HelloWorld (HelloRequest) returns (HelloResponse);
}
```


#### 获取服务端 Grpc 接口列表
`GET` 请求 `http://localhost:8080/api/服务名称/`

#### 调用 Grpc 接口
```
curl \
--location \
--request POST 'http://localhost:8080/api/服务名称/Grpc方法名称[com.qianxunclub.proto.HelloService.HelloWorld]' \
--header 'Content-Type: application/json' \
--data-raw '{
    "fieldName":"value"
}'
```

### swagger 使用

#### 配置 grpc2http-gateway 的 swagger 服务地址
```
# 这个配置是所部署的服务器地址的 IP 或者 域名，为了 swagger 执行调用
swagger.serverUrl=http://localhost:8080
```

访问：http://localhost:8080/swagger-ui/index.html  

OPENAPI 地址填写：`http://localhost:8080/api/v3/api-docs/服务名称`


## 开发构建
### docker 构建镜像

```
./docker-build.sh
```
构建完镜像名称为：`grpc2http-gateway:latest`


## 部署
### docker-compose 部署

1. 编辑 [docker-compose.yml](docker-compose/docker-compose.yml) ，配置环境变量
2. 启动
```
cd docker-compose
docker-compose up -d
```

## 开发者讨论

欢迎提交 PR 升级，有什么好建议或者想法的，可以添加QQ群一起讨论：852214454
