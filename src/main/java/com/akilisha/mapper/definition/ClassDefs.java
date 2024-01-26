package com.akilisha.mapper.definition;

import java.util.concurrent.ConcurrentHashMap;

public class ClassDefs extends ConcurrentHashMap<Class<?>, ClassDef> {

    public static ClassDefs cached = new ClassDefs();

    private ClassDefs() {
    }

    public ClassDef get(Class<?> key) {
        return computeIfAbsent(key, ClassDef::newClassDef);
    }
}
