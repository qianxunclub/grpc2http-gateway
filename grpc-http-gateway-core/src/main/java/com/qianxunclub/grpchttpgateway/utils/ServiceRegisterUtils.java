package com.qianxunclub.grpchttpgateway.utils;

import com.google.common.net.HostAndPort;
import com.google.protobuf.DescriptorProtos;
import com.qianxunclub.grpchttpgateway.grpc.ServiceResolver;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class ServiceRegisterUtils {

    private static final Set<String> blockServiceSet = new HashSet<>();

    static {
        blockServiceSet.add("grpc.health.v1.Health".toLowerCase());
        blockServiceSet.add("grpc.reflection.v1alpha.ServerReflection".toLowerCase());
    }

    public static List<DescriptorProtos.FileDescriptorSet> registerByIpAndPort(String hostAndPort) {
        String[] strings = hostAndPort.split(":");
        if (strings.length != 2) {
            return emptyList();
        }
        return registerByIpAndPort(strings[0], Integer.parseInt(strings[1]));
    }

    public static List<DescriptorProtos.FileDescriptorSet> registerByIpAndPort(String host, int port) {
        HostAndPort hostAndPort = HostAndPort.fromParts(host, port);
        Channel channel = ManagedChannelBuilder.forAddress(hostAndPort.getHost(), hostAndPort.getPort())
                .usePlaintext()
                .build();

        List<DescriptorProtos.FileDescriptorSet> fileDescriptorSets = GrpcReflectionUtils.resolveServices(channel);
        fileDescriptorSets.forEach(fileDescriptorSet -> {
            ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
            System.out.println(serviceResolver);
        });
        return fileDescriptorSets;
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
}
