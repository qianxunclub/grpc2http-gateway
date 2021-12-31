package com.qianxunclub.grpchttpgateway.configuration;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import springfox.documentation.oas.annotations.EnableOpenApi;


@Slf4j
@Configuration
@EnableOpenApi
public class GrpcHttpGatewayConfiguration {

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        GrpcEndpointProperties endpointProperties = ApplicationHelper.get(GrpcEndpointProperties.class);
        log.info("----------已注册服务----------");
        endpointProperties.getAllEndpoint().keySet().forEach(serverName -> log.info(serverName + "\t=\t" + endpointProperties.getAllEndpoint().get(serverName)));
        log.info("----------------------------");
    }
}
