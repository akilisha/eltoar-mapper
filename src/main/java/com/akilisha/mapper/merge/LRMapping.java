package com.akilisha.mapper.merge;

import com.akilisha.mapper.definition.ClassDef;
import com.akilisha.mapper.wrapper.ObjWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.akilisha.mapper.merge.LRUtils.objectDef;

public class LRMapping extends HashMap<String, LRConverter<?>> {

    private LRMapping() {
        //hidden
    }

    public static LRMapping init() {
        return new LRMapping();
    }

    public static LRMapping init(Map<String, LRConverter<?>> map) {
        LRMapping mapping = init();
        mapping.putAll(map);
        return mapping;
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

    public LRMapping map(String src, String dest, Class<?> collectionType) {
        this.put(src, new LRConverter<>(dest, null, collectionType, null, null));
        return this;
    }

    public <R> LRMapping map(String src, String dest, Class<?> collectionType, Function<Object, R> eval) {
        this.put(src, new LRConverter<>(dest, null, collectionType, eval, null));
        return this;
    }

    public LRMapping map(String src, Class<?> collectionType, String dest) {
        this.put(src, new LRConverter<>(dest, collectionType, null, null, null));
        return this;
    }

    public LRMapping map(String src, LRMapping nestedMapping) {
        this.put(src, new LRConverter<>(null, null, null, null, nestedMapping));
        return this;
    }

    public LRMapping map(String src, Class<?> collectionType, LRMapping nestedMapping) {
        this.put(src, new LRConverter<>(null, collectionType, null, null, nestedMapping));
        return this;
    }

    public <R> LRMapping map(String src, Class<?> collectionType, String dest, Function<Object, R> eval) {
        this.put(src, new LRConverter<>(dest, null, collectionType, eval, null));
        return this;
    }

    public LRMapping map(String src, Class<?> srcCollectionType, String dest, Class<?> destCollectionType, LRMapping collectionTypeMapping) {
        this.put(src, new LRConverter<>(dest, srcCollectionType, destCollectionType, null, collectionTypeMapping));
        return this;
    }

    public void merge(Object src, Object dest) throws Throwable {
        LRMerge rlMerge = new LRMerge() {
        };

        ClassDef srcDef = objectDef(src);
        ClassDef destDef = objectDef(dest);
        rlMerge.merge(new ObjWrapper<>(src), srcDef, new ObjWrapper<>(dest), destDef, this);
    }
}
