package com.akilisha.mapper.definition;

import com.akilisha.mapper.wrapper.ObjWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.akilisha.mapper.definition.Mappings.classDef;
import static com.akilisha.mapper.definition.Mappings.objectDef;

public class Mapping extends HashMap<String, Converter<?>> {

    private Mapping() {
        //hidden
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

    public <R> Mapping map(String dest, String src, Function<Object, R> eval) {
        this.put(dest, new Converter<>(src, eval));
        return this;
    }

    public Mapping map(String dest, String src, Class<?> collectionType) {
        this.put(dest, new Converter<>(src, collectionType, null, null, null));
        return this;
    }

    public <R> Mapping map(String dest, String src, Class<?> collectionType, Function<Object, R> eval) {
        this.put(dest, new Converter<>(src, collectionType, null, eval, null));
        return this;
    }

    public Mapping map(String dest, Class<?> collectionType, String src) {
        this.put(dest, new Converter<>(src, null, collectionType, null, null));
        return this;
    }

    public Mapping map(String dest, Class<?> collectionType, Mapping nestedMapping) {
        this.put(dest, new Converter<>(null, collectionType, null, null, nestedMapping));
        return this;
    }

    public <R> Mapping map(String dest, Class<?> collectionType, String src, Function<Object, R> eval) {
        this.put(dest, new Converter<>(src, null, collectionType, eval, null));
        return this;
    }

    public Mapping map(String dest, Class<?> destCollectionType, String src, Class<?> srcCollectionType, Mapping collectionTypeMapping) {
        this.put(dest, new Converter<>(src, destCollectionType, srcCollectionType, null, collectionTypeMapping));
        return this;
    }

    public void commit(MapperSrc from, MapperDest to) throws Throwable {
        ClassDef fromDef = classDef(from.getClass());
        ClassDef toDef = classDef(to.getClass());
        Mappings.mapAToB(from, fromDef, to, toDef, this);
    }

    public void commit(ObjWrapper<?> from, ObjWrapper<?> to) throws Throwable {
        ClassDef fromDef = objectDef(from);
        ClassDef toDef = objectDef(to);
        Mappings.mapAToB(from, fromDef, to, toDef, this);
    }
}
