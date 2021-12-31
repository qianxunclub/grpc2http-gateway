package com.qianxunclub.grpchttpgateway.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.protobuf.GeneratedMessageV3.Builder;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;

/**
 * PROTO 转换工具类
 */
@Slf4j
public class ProtoUtil {

    public static String toJson(MessageOrBuilder message) {
        StringBuilder sb = new StringBuilder();
        try {
            if (!StringUtils.hasLength(message.toString())) {
                return null;
            }
            JsonFormat.printer().appendTo(message, sb);
            return sb.toString();
        } catch (IOException e) {
            log.error("proto 转换 json 字符串失败", e);
        }
        return null;
    }

    /**
     * model 转 proto
     *
     * @param model
     * @param builder
     * @param <T>
     * @param <B>
     * @return
     */
    public static <T, B extends Builder> B toProto(T model, B builder) {
        Gson gson = new GsonBuilder().serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter())
                .create();
        String json = gson.toJson(model);
        try {
            JsonFormat.parser().ignoringUnknownFields().merge(json, builder);
            return builder;
        } catch (InvalidProtocolBufferException e) {
            log.error("model 转换 proto 失败", e);
        }
        return null;
    }


    /**
     * proto 转 model
     *
     * @param message
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toModel(MessageOrBuilder message, Class<T> clazz) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter())
                .create();
        StringBuilder sb = new StringBuilder();
        try {
            if (!StringUtils.hasLength(message.toString())) {
                return null;
            }
            JsonFormat.printer().appendTo(message, sb);
            return gson.fromJson(sb.toString(), clazz);
        } catch (IOException e) {
            log.error("proto 转换 model 失败", e);
        }
        return null;
    }

    /**
     * json 转 proto
     *
     * @param json
     * @param builder
     * @param <T>
     * @param <B>
     * @return
     */
    public static <T, B extends Builder> B toProto(String json, B builder) {
        try {
            JsonFormat.parser().ignoringUnknownFields().merge(json, builder);
            return builder;
        } catch (InvalidProtocolBufferException e) {
            log.error("json 转换 proto 失败", e);
        }
        return null;
    }


    /**
     * 日期转换处理
     */
    public static class TimestampTypeAdapter implements JsonSerializer<Timestamp>,
            JsonDeserializer<Timestamp> {

        @Override
        public Timestamp deserialize(
                JsonElement json,
                Type typeOfT,
                JsonDeserializationContext context
        ) throws JsonParseException {
            return new Timestamp(json.getAsLong());
        }

        @Override
        public JsonElement serialize(
                Timestamp timestamp,
                Type typeOfSrc,
                JsonSerializationContext context
        ) {
            return new JsonPrimitive(timestamp.getTime());
        }
    }


}
