package com.parse.demo;

public class Endpoint {
    public String method;
    public String type;
    public String path;

    public Endpoint(String method, String type, String path) {
        this.method = method;
        this.type = type;
        this.path = path;
    }

    @Override
    public String toString() {
        return String.format("Endpoint{method='%s', type='%s', path='%s'}", method, type, path);
    }
} 