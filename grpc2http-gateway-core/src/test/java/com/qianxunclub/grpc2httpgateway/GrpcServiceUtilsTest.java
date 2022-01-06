package com.qianxunclub.grpc2httpgateway;


import com.google.protobuf.DescriptorProtos;
import com.qianxunclub.grpc2httpgateway.utils.GrpcServiceUtils;
import org.junit.Test;

import java.util.List;

public class GrpcServiceUtilsTest {

    @Test
    public void registerByIpAndPort() {
        List<DescriptorProtos.FileDescriptorSet> fileDescriptorSets = GrpcServiceUtils.getFileDescriptorSetList("dev.coding.oa.com", 9903);
        List<String> serviceNames = GrpcServiceUtils.getServiceNames(fileDescriptorSets);
        System.out.println(serviceNames);
    }
}
