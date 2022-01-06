package com.qianxunclub.grpc2httpgateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrpcMethodDefinition {
    private String packageName;
    private String serviceName;
    private String methodName;

    public String getFullServiceName() {
        if (StringUtils.hasLength(packageName)) {
            return packageName + "." + serviceName;
        }
        return serviceName;
    }

    public String getFullMethodName() {
        if (StringUtils.hasLength(packageName)) {
            return packageName + "." + serviceName + "/" + methodName;
        }
        return serviceName + "/" + methodName;
    }
}
