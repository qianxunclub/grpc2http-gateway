package com.qianxunclub.grpc2httpgateway.model;

import java.util.ArrayList;
import java.util.List;

public class CallResults {

    private final List<String> results = new ArrayList<>();

    public void add(String jsonText) {
        results.add(jsonText);
    }

    public List<String> asList() {
        return results;
    }

    public Object getResults() {
        if (results.size() == 1) {
            return results.get(0);
        }
        return results;
    }
}
