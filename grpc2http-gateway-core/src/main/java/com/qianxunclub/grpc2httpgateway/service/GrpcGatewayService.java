package com.qianxunclub.grpc2httpgateway.service;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat.TypeRegistry;
import com.qianxunclub.grpc2httpgateway.grpc.DynamicGrpcClient;
import com.qianxunclub.grpc2httpgateway.model.CallParams;
import com.qianxunclub.grpc2httpgateway.protobuf.ServiceResolver;
import com.qianxunclub.grpc2httpgateway.model.CallResults;
import com.qianxunclub.grpc2httpgateway.model.GrpcMethodDefinition;
import com.qianxunclub.grpc2httpgateway.utils.GrpcReflectionUtils;
import com.qianxunclub.grpc2httpgateway.utils.MessageWriter;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class GrpcGatewayService {

    private final DynamicGrpcClient dynamicGrpcClient;

    public CallResults invokeMethod(GrpcMethodDefinition definition,
                                    Channel channel,
                                    CallOptions callOptions,
                                    List<String> requestJsonTexts) throws ExecutionException, InterruptedException {
        FileDescriptorSet fileDescriptorSet = GrpcReflectionUtils.resolveService(channel, definition.getFullServiceName());
        if (fileDescriptorSet == null) {
            return null;
        }
        ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
        MethodDescriptor methodDescriptor = serviceResolver.resolveServiceMethod(definition);
        TypeRegistry registry = TypeRegistry.newBuilder().add(serviceResolver.listMessageTypes()).build();
        List<DynamicMessage> requestMessages = GrpcReflectionUtils.parseToMessages(registry, methodDescriptor.getInputType(),
                requestJsonTexts);
        CallResults results = new CallResults();
        StreamObserver<DynamicMessage> streamObserver = MessageWriter.newInstance(registry, results);
        CallParams callParams = CallParams.builder()
                .methodDescriptor(methodDescriptor)
                .channel(channel)
                .callOptions(callOptions)
                .requests(requestMessages)
                .responseObserver(streamObserver)
                .build();
        Objects.requireNonNull(dynamicGrpcClient.call(callParams)).get();
        return results;
    }
}
