package com.qianxunclub.grpc2httpgateway.utils;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.qianxunclub.grpc2httpgateway.configuration.GrpcEndpointProperties;
import com.qianxunclub.grpc2httpgateway.protobuf.ServiceResolver;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GrpcServiceUtils {

    private static final Set<String> blockServiceSet = new HashSet<>();

    static {
        blockServiceSet.add("grpc.health.v1.Health".toLowerCase());
        blockServiceSet.add("grpc.reflection.v1alpha.ServerReflection".toLowerCase());
    }

    public static List<DescriptorProtos.FileDescriptorSet> getFileDescriptorSetList(
            GrpcEndpointProperties.Endpoint endpoint
    ) {
        return GrpcServiceUtils.getFileDescriptorSetList(endpoint.getChannelHost(), endpoint.getChannelPort());
    }


    public static List<DescriptorProtos.FileDescriptorSet> getFileDescriptorSetList(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        List<DescriptorProtos.FileDescriptorSet> fileDescriptorSetList = GrpcReflectionUtils.resolveServices(channel);
        channel.shutdown();
        return fileDescriptorSetList;
    }

    public static List<String> getServiceNames(List<DescriptorProtos.FileDescriptorSet> fileDescriptorSets) {
        List<String> serviceNames = new ArrayList<>();
        fileDescriptorSets.forEach(fileDescriptorSet -> {
            ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
            serviceResolver.listServices().forEach(serviceDescriptor -> {
                String serviceName = serviceDescriptor.getFullName();
                if (blockServiceSet.contains(serviceName.toLowerCase())) {
                    return;
                }
                serviceNames.add(serviceName);
            });
        });
        return serviceNames.stream().distinct().sorted().collect(Collectors.toList());
    }


    public static List<String> getMethodNames(List<DescriptorProtos.FileDescriptorSet> fileDescriptorSets) {
        List<String> methodNames = new ArrayList<>();
        fileDescriptorSets.forEach(fileDescriptorSet -> {
            ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
            serviceResolver.listServices().forEach(serviceDescriptor -> {
                List<Descriptors.MethodDescriptor> methodDescriptorList = serviceDescriptor.getMethods();
                methodDescriptorList.forEach(methodDescriptor -> methodNames.add(methodDescriptor.getFullName()));

            });
        });
        return methodNames;
    }
}
