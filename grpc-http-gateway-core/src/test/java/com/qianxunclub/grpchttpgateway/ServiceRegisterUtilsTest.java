package com.qianxunclub.grpchttpgateway;


import com.google.protobuf.DescriptorProtos;
import com.qianxunclub.grpchttpgateway.utils.ServiceRegisterUtils;
import org.junit.Test;

import java.util.List;

public class ServiceRegisterUtilsTest {

    @Test
    public void registerByIpAndPort() {
        List<DescriptorProtos.FileDescriptorSet> fileDescriptorSets = ServiceRegisterUtils.registerByIpAndPort("dev.coding.oa.com", 9903);
        List<String> serviceNames = ServiceRegisterUtils.getServiceNames(fileDescriptorSets);
        System.out.println(serviceNames);
    }
}
