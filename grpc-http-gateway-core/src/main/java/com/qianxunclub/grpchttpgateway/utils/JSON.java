package com.qianxunclub.grpchttpgateway.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSON {

    public static Gson getGson() {
        return getGsonBuilder().create();
    }

    /**
     * 获取 GsonBuilder
     */
    private static GsonBuilder getGsonBuilder() {
        return new GsonBuilder();
    }
}
