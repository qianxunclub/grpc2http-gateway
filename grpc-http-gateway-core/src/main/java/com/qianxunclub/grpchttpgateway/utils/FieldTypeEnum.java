package com.qianxunclub.grpchttpgateway.utils;

import com.google.protobuf.Descriptors;
import lombok.Getter;

@Getter
public enum FieldTypeEnum {

    DOUBLE(Descriptors.FieldDescriptor.JavaType.DOUBLE, Double.class.getSimpleName()),
    FLOAT(Descriptors.FieldDescriptor.JavaType.FLOAT, Float.class.getSimpleName()),
    INT64(Descriptors.FieldDescriptor.JavaType.LONG, Long.class.getSimpleName()),
    UINT64(Descriptors.FieldDescriptor.JavaType.LONG, Long.class.getSimpleName()),
    INT32(Descriptors.FieldDescriptor.JavaType.INT, Integer.class.getSimpleName()),
    FIXED64(Descriptors.FieldDescriptor.JavaType.LONG, Long.class.getSimpleName()),
    FIXED32(Descriptors.FieldDescriptor.JavaType.INT, Integer.class.getSimpleName()),
    BOOL(Descriptors.FieldDescriptor.JavaType.BOOLEAN, Boolean.class.getSimpleName()),
    STRING(Descriptors.FieldDescriptor.JavaType.STRING, String.class.getSimpleName()),
    GROUP(Descriptors.FieldDescriptor.JavaType.MESSAGE, Object.class.getSimpleName()),
    MESSAGE(Descriptors.FieldDescriptor.JavaType.MESSAGE, Object.class.getSimpleName()),
    BYTES(Descriptors.FieldDescriptor.JavaType.BYTE_STRING, String.class.getSimpleName()),
    UINT32(Descriptors.FieldDescriptor.JavaType.INT, Integer.class.getSimpleName()),
    ENUM(Descriptors.FieldDescriptor.JavaType.ENUM, Enum.class.getSimpleName()),
    SFIXED32(Descriptors.FieldDescriptor.JavaType.INT, Integer.class.getSimpleName()),
    SFIXED64(Descriptors.FieldDescriptor.JavaType.LONG, Long.class.getSimpleName()),
    SINT32(Descriptors.FieldDescriptor.JavaType.INT, Integer.class.getSimpleName()),
    SINT64(Descriptors.FieldDescriptor.JavaType.LONG, Long.class.getSimpleName());

    private Descriptors.FieldDescriptor.JavaType javaType;
    private String javaSimpleType;

    FieldTypeEnum(Descriptors.FieldDescriptor.JavaType javaType, String javaSimpleType) {
        this.javaType = javaType;
        this.javaSimpleType = javaSimpleType;
    }

    public static String getJavaSimpleType(String type) {
        return FieldTypeEnum.valueOf(type).getJavaSimpleType().toLowerCase();
    }
}
