package com.akilisha.mapper.merge;

import com.akilisha.mapper.definition.ClassDef;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.akilisha.mapper.merge.LRUtils.objectDef;

public class LRMapping extends HashMap<String, LRConverter<?>> {

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
        this.merge(src, dest, new LRContext());
    }

    public void merge(Object src, Object dest, LRContext ctx) throws Throwable {
        LRMerge rlMerge = new LRMerge() {
        };

        int srcHash = System.identityHashCode(src);
        if (!ctx.containsKey(srcHash)) {
            ClassDef srcDef = objectDef(src);
            ClassDef destDef = objectDef(dest);
            rlMerge.merge(src, srcDef, dest, destDef, this, ctx);
        } else {
            String error = ctx.get(srcHash).getFields().stream().filter(f -> f.getParent() == srcHash)
                    .findFirst().map(f -> String.format("Detected cycle from parent '%s' to child '%s' on the field '%s'",
                            src.getClass().getSimpleName(), f.getClassName(), f.getFieldName())).orElse(
                            String.format("Detected cycle - trying to map %s again before the original mapping ic completed",
                                    src.getClass().getSimpleName()));
            throw new RuntimeException(error);
        }
    }
}
