package com.qianxunclub.grpchttpgateway.configuration;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Data
@Configuration
@ConfigurationProperties(prefix = "grpc")
public class GrpcConfiguration {

    private Map<String, Endpoint> endpoint = new HashMap<>();

    /**
     * 忽略大小写
     *
     * @return
     */
    public Map<String, Endpoint> getAllEndpoint() {
        Map<String, Endpoint> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        map.putAll(endpoint);
        return map;
    }

    public Endpoint getEndpoint(String serverName) throws Exception {
        if (endpoint.get(serverName) == null) {
            throw new Exception("缺少【" + serverName + "】endpoint 配置");
        }
        return endpoint.get(serverName);
    }

    @Data
    public static class Endpoint {

        /**
         * IP
         */
        private String channelHost;
        /**
         * 端口
         */
        private Integer channelPort;
    }
}
