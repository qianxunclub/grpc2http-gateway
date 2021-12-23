package com.qianxunclub.grpchttpgateway.utils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Printer;
import com.qianxunclub.grpchttpgateway.model.CallResults;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import static com.google.protobuf.util.JsonFormat.TypeRegistry;

@Slf4j
public class MessageWriter<T extends Message> implements StreamObserver<T> {

    private final Printer printer;
    private final CallResults results;

    private MessageWriter(Printer printer, CallResults results) {
        this.printer = printer;
        this.results = results;
    }

    public static <T extends Message> MessageWriter<T> newInstance(TypeRegistry registry, CallResults results) {
        return new MessageWriter<>(
                JsonFormat.printer().usingTypeRegistry(registry).includingDefaultValueFields(),
                results);
    }

    @Override
    public void onNext(T value) {
        try {
            results.add(printer.print(value));
        } catch (InvalidProtocolBufferException e) {
            log.error("Skipping invalid response message", e);
        }
    }

    @Override
    public void onError(Throwable t) {
        log.error("Messages write occur errors", t);
    }

    @Override
    public void onCompleted() {
        log.info("Messages write complete");
    }
}
