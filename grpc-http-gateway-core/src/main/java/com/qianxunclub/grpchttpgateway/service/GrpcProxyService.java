package com.qianxunclub.grpchttpgateway.service;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat.TypeRegistry;
import com.qianxunclub.grpchttpgateway.grpc.ServiceResolver;
import com.qianxunclub.grpchttpgateway.model.CallParams;
import com.qianxunclub.grpchttpgateway.model.CallResults;
import com.qianxunclub.grpchttpgateway.model.GrpcMethodDefinition;
import com.qianxunclub.grpchttpgateway.utils.GrpcReflectionUtils;
import com.qianxunclub.grpchttpgateway.utils.MessageWriter;
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
public class GrpcProxyService {

    private final GrpcClientService grpcClientService;

    public CallResults invokeMethod(GrpcMethodDefinition definition,
                                    Channel channel,
                                    CallOptions callOptions,
                                    List<String> requestJsonTexts) {
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
        try {
            Objects.requireNonNull(grpcClientService.call(callParams)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Caught exception while waiting for rpc", e);
        }
        return results;
    }
}
