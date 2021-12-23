package com.qianxunclub.grpchttpgateway;

import com.google.protobuf.DescriptorProtos;
import com.qianxunclub.grpchttpgateway.utils.ServiceRegisterUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class RegisterController {

    @RequestMapping("/register")
    public void registerServices() {

        List<DescriptorProtos.FileDescriptorSet> fileDescriptorSets = ServiceRegisterUtils.registerByIpAndPort("dev.coding.oa.com", 9903);
        List<String> serviceNames = ServiceRegisterUtils.getServiceNames(fileDescriptorSets);
    }

}
