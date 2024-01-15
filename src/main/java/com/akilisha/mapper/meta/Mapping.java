package com.akilisha.mapper.meta;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Mapping extends HashMap<String, Converter<?>> {

    private Mapping() {
        // hidden
    }

    public static Mapping init() {
        return new Mapping();
    }

    public static Mapping init(Map<String, Converter<?>> map) {
        Mapping mapping = init();
        mapping.putAll(map);
        return mapping;
    }

    public Mapping map(String dest, String src) {
        this.put(dest, new Converter<>(src));
        return this;
    }

    public Mapping map(String dest, Class<?> collectionType, String src) {
        this.put(dest, new Converter<>(src, collectionType));
        return this;
    }

    public <R> Mapping map(String dest, String src, Function<Object, R> eval) {
        this.put(dest, new Converter<>(src, eval));
        return this;
    }

    public <R> Mapping map(String dest, Class<?> collectionType, String src, Function<Object, R> eval) {
        this.put(dest, new Converter<>(src, collectionType, eval));
        return this;
    }
}
