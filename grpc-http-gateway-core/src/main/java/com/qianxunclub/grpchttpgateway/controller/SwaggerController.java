package com.qianxunclub.grpchttpgateway.controller;

import com.qianxunclub.grpchttpgateway.service.OpenApiService;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "api", produces = MediaType.APPLICATION_JSON_VALUE)
public class SwaggerController {

    private final OpenApiService openApiService;

    @GetMapping("/v2/api-docs/{serverName}")
    public OpenAPI apiDocs(@PathVariable String serverName) throws Exception {
        return openApiService.openApi(serverName);

    }
}
