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
public class GrpcEndpointProperties {

    private Map<String, String> endpoint = new HashMap<>();

    /**
     * 忽略大小写
     */
    public Map<String, String> getAllEndpoint() {
        Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        map.putAll(endpoint);
        return map;
    }

    public Endpoint getEndpoint(String serverName) throws Exception {
        if (endpoint.get(serverName) == null) {
            throw new Exception("缺少【" + serverName + "】endpoint 配置");
        }
        return new Endpoint(
                endpoint.get(serverName).split(":")[0],
                Integer.parseInt(endpoint.get(serverName).split(":")[1])
        );
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


        public Endpoint(String channelHost, Integer channelPort) {
            this.channelHost = channelHost;
            this.channelPort = channelPort;
        }

    }
}
