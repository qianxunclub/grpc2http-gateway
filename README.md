# grpc-http-gateway
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

### 启动 grpc-http-gateway 服务

示例 proto
```
syntax = "proto3";

package com.qianxunclub.proto;
option java_outer_classname = "HelloProto";

message HelloRequest {
}

message HelloResponse {
}

service HelloService {
    rpc HelloWorld (HelloRequest) returns (HelloResponse);
}
```

#### 获取服务端 Grpc 接口列表
`GET` 请求 `http://localhost:8080/提供Grpc服务IP/提供Grpc服务端口/`

#### 调用 Grpc 接口
```
curl \
--location \
--request POST 'http://localhost:8080/提供Grpc服务IP/提供Grpc服务端口/Grpc方法名称[com.qianxunclub.proto.HelloService.HelloWorld]' \
--header 'Content-Type: application/json' \
--data-raw '{
    "fieldName":"value"
}'
```

## 开发部署

### docker 构建镜像

```
 && 
```