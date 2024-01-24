package com.akilisha.mapper.merge;

import java.util.HashMap;
import java.util.function.Function;

public class LRMapping extends HashMap<String, LRConverter<?>> implements LRMerge {

    public static LRMapping init() {
        return new LRMapping();
    }

    public LRMapping map(String src, String dest) {
        this.put(src, new LRConverter<>(dest));
        return this;
    }

    public <R> LRMapping map(String src, Function<Object, R> eval) {
        this.put(src, new LRConverter<>(src, eval));
        return this;
    }

    public <R> LRMapping map(String src, String dest, Function<Object, R> eval) {
        this.put(src, new LRConverter<>(dest, eval));
        return this;
    }

    public LRMapping map(String src, String dest, Class<?> collectionItemType) {
        this.put(src, new LRConverter<>(dest, null, collectionItemType, null, null, null));
        return this;
    }

    public <R> LRMapping map(String src, String dest, Class<?> collectionItemType, Function<Object, R> eval) {
        this.put(src, new LRConverter<>(dest, null, collectionItemType, eval, null, null));
        return this;
    }

    public LRMapping map(String src, Class<?> collectionType, String dest) {
        this.put(src, new LRConverter<>(dest, collectionType, null, null, null, null));
        return this;
    }

    public LRMapping map(String src) {
        this.put(src, new LRConverter<>(null, null, null, null, null, null));
        return this;
    }

    public LRMapping map(String src, LRMapping nestedMapping) {
        this.put(src, new LRConverter<>(null, null, null, null, null, nestedMapping));
        return this;
    }

    public LRMapping map(String src, Class<?> collectionType, LRMapping nestedMapping) {
        this.put(src, new LRConverter<>(null, null, collectionType, null, null, nestedMapping));
        return this;
    }

    public LRMapping map(String src, Class<?> srcCollectionType, String dest, Class<?> destCollectionType, LRMapping collectionTypeMapping) {
        this.put(src, new LRConverter<>(dest, srcCollectionType, destCollectionType, null, null, collectionTypeMapping));
        return this;
    }

    public LRMapping map(String src, Class<?> srcCollectionType, String dest, Class<?> destCollectionType, String keyField, LRMapping collectionTypeMapping) {
        this.put(src, new LRConverter<>(dest, srcCollectionType, destCollectionType, null, keyField, collectionTypeMapping));
        return this;
    }

    public LRMapping copy(String src, String fieldName, LRConverter<?> converter) {
        put(src, new LRConverter<>(fieldName,
                converter.srcCollectionType,
                converter.destCollectionType,
                converter.eval,
                converter.keyField, this));
        return this;
    }

    public void merge(Object src, Object dest) throws Throwable {
        this.merge(src, dest, new LRContext());
    }

    public void merge(Object src, Object dest, LRContext ctx) throws Throwable {
        merge(src, fieldValues(src), dest, fieldValues(dest), this, ctx);
    }
}
