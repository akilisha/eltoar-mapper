package com.akilisha.mapper.merge;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class LRMapping extends HashMap<String, LRPathway<?>> implements LRMerge {

    public static LRMapping init() {
        return new LRMapping();
    }

    public LRMapping map(String src, String dest) {
        this.put(src, new LRPathway<>(dest));
        return this;
    }

    public <R> LRMapping map(String src, Function<Object, R> eval) {
        this.put(src, new LRPathway<>(src, eval));
        return this;
    }

    public <R> LRMapping map(String src, String dest, Function<Object, R> eval) {
        this.put(src, new LRPathway<>(dest, eval));
        return this;
    }

    public LRMapping map(String src, String dest, Class<?> collectionItemType) {
        this.put(src, new LRPathway<>(dest, null, collectionItemType, null, null, null, null));
        return this;
    }

    public <R> LRMapping map(String src, String dest, Class<?> collectionItemType, Function<Object, R> eval) {
        this.put(src, new LRPathway<>(dest, null, collectionItemType, eval, null, null, null));
        return this;
    }

    public LRMapping map(String src, Class<?> collectionType, String dest) {
        this.put(src, new LRPathway<>(dest, collectionType, null, null, null, null, null));
        return this;
    }

    public LRMapping map(String src) {
        this.put(src, new LRPathway<>(null, null, null, null, null, null, null));
        return this;
    }

    public LRMapping map(String src, String dest, String mapKey) {
        this.put(src, new LRPathway<>(dest, null, null, null, mapKey, null, null));
        return this;
    }

    public LRMapping map(String src, String dest, String mapKey, LRMapping nestedMapping) {
        this.put(src, new LRPathway<>(dest, null, null, null, mapKey, null, nestedMapping));
        return this;
    }

    public <K>LRMapping map(String src, String dest, Supplier<K> mapKey) {
        this.put(src, new LRPathway<>(dest, null, null, null, mapKey, null));
        return this;
    }

    public <K>LRMapping map(String src, String dest, Supplier<K> mapKey, LRMapping nestedMapping) {
        this.put(src, new LRPathway<>(dest, null, null, null, mapKey, nestedMapping));
        return this;
    }

    public LRMapping map(String src, LRMapping nestedMapping) {
        this.put(src, new LRPathway<>(null, null, null, null, null, null, nestedMapping));
        return this;
    }

    public LRMapping map(String src, Class<?> collectionType, LRMapping nestedMapping) {
        this.put(src, new LRPathway<>(null, null, collectionType, null, null, null, nestedMapping));
        return this;
    }

    public LRMapping map(String src, Class<?> srcCollectionType, String dest, Class<?> destCollectionType, LRMapping collectionTypeMapping) {
        this.put(src, new LRPathway<>(dest, srcCollectionType, destCollectionType, null, null, null, collectionTypeMapping));
        return this;
    }

    public LRMapping map(String src, Class<?> srcCollectionType, String dest, Class<?> destCollectionType, String keyField, LRMapping collectionTypeMapping) {
        this.put(src, new LRPathway<>(dest, srcCollectionType, destCollectionType, null, keyField, null, collectionTypeMapping));
        return this;
    }

    public LRMapping copy(String src, String fieldName, LRPathway<?> converter) {
        put(src, new LRPathway<>(fieldName,
                converter.srcCollectionType,
                converter.destCollectionType,
                converter.converter,
                converter.keyField, null, this));
        return this;
    }

    public void merge(Object src, Object dest) throws Throwable {
        this.merge(src, dest, new LRContext());
    }

    public void merge(Object src, Object dest, LRContext ctx) throws Throwable {
        merge(src, fieldValues(src), dest, fieldValues(dest), this, ctx);
    }
}
