package com.qianxunclub.grpchttpgateway.model;


import com.qianxunclub.grpchttpgateway.utils.JSON;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class CallResults {
    private List<String> results;

    public CallResults() {
        this.results = new ArrayList<>();
    }

    public void add(String jsonText) {
        results.add(jsonText);
    }

    public List<String> asList() {
        return results;
    }

    public Object asJSON() {
        if (results.size() == 1) {
            return JSON.getGson().fromJson(results.get(0), Object.class);
        }
        return results.stream().map(str -> JSON.getGson().fromJson(str, Object.class)).collect(toList());
    }
}
