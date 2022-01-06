package com.qianxunclub.grpc2httpgateway;

import com.qianxunclub.grpc2httpgateway.utils.GrpcReflectionUtils;
import org.junit.Test;

public class GrpcReflectionUtilsTest {

    @Test
    public void testParseToMethodDefinition() {
        System.out.println(GrpcReflectionUtils.parseToMethodDefinition("io.grpc.reflection.Test.print"));
    }
}
