package com.qianxunclub.grpc2httpgateway.grpc;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.DynamicMessage;
import com.qianxunclub.grpc2httpgateway.protobuf.DynamicMessageMarshaller;
import com.qianxunclub.grpc2httpgateway.model.CallParams;
import com.qianxunclub.grpc2httpgateway.utils.GrpcReflectionUtils;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor.MethodType;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;

@Slf4j
@Service
public class DynamicGrpcClient {

    @Nullable
    public ListenableFuture<Void> call(CallParams callParams) {
        checkParams(callParams);
        MethodType methodType = GrpcReflectionUtils.fetchMethodType(callParams.getMethodDescriptor());
        List<DynamicMessage> requests = callParams.getRequests();
        StreamObserver<DynamicMessage> responseObserver = callParams.getResponseObserver();
        DoneObserver<DynamicMessage> doneObserver = new DoneObserver<>();
        StreamObserver<DynamicMessage> compositeObserver = CompositeStreamObserver.of(responseObserver, doneObserver);
        StreamObserver<DynamicMessage> requestObserver;
        switch (methodType) {
            case UNARY:
                asyncUnaryCall(createCall(callParams), requests.get(0), compositeObserver);
                return doneObserver.getCompletionFuture();
            case SERVER_STREAMING:
                asyncServerStreamingCall(createCall(callParams), requests.get(0), compositeObserver);
                return doneObserver.getCompletionFuture();
            case CLIENT_STREAMING:
                requestObserver = asyncClientStreamingCall(createCall(callParams), compositeObserver);
                requests.forEach(responseObserver::onNext);
                requestObserver.onCompleted();
                return doneObserver.getCompletionFuture();
            case BIDI_STREAMING:
                requestObserver = asyncBidiStreamingCall(createCall(callParams), compositeObserver);
                requests.forEach(responseObserver::onNext);
                requestObserver.onCompleted();
                return doneObserver.getCompletionFuture();
            default:
                log.info("Unknown methodType:{}", methodType);
                return null;
        }
    }

    private void checkParams(CallParams callParams) {
        checkNotNull(callParams);
        checkNotNull(callParams.getMethodDescriptor());
        checkNotNull(callParams.getChannel());
        checkNotNull(callParams.getCallOptions());
        checkArgument(!CollectionUtils.isEmpty(callParams.getRequests()));
        checkNotNull(callParams.getResponseObserver());
    }

    private ClientCall<DynamicMessage, DynamicMessage> createCall(CallParams callParams) {
        return callParams.getChannel().newCall(createGrpcMethodDescriptor(callParams.getMethodDescriptor()),
                callParams.getCallOptions());
    }

    private io.grpc.MethodDescriptor<DynamicMessage, DynamicMessage> createGrpcMethodDescriptor(MethodDescriptor descriptor) {
        return io.grpc.MethodDescriptor.<DynamicMessage, DynamicMessage>newBuilder()
                .setType(GrpcReflectionUtils.fetchMethodType(descriptor))
                .setFullMethodName(GrpcReflectionUtils.fetchFullMethodName(descriptor))
                .setRequestMarshaller(new DynamicMessageMarshaller(descriptor.getInputType()))
                .setResponseMarshaller(new DynamicMessageMarshaller(descriptor.getOutputType()))
                .build();
    }
}
