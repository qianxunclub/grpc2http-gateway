package com.qianxunclub.grpc2httpgateway.model;

import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.DynamicMessage;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class CallParams {
    private MethodDescriptor methodDescriptor;
    private Channel channel;
    private CallOptions callOptions;
    private List<DynamicMessage> requests;
    private StreamObserver<DynamicMessage> responseObserver;
}
