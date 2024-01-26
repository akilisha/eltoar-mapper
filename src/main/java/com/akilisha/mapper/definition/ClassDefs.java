package com.akilisha.mapper.definition;

import java.util.HashMap;

public class ClassDefs extends HashMap<Class<?>, ClassDef> {

    public static ClassDefs cached = new ClassDefs();

    private ClassDefs() {
    }

    public ClassDef get(Class<?> key) {
        return computeIfAbsent(key, ClassDef::newClassDef);
    }
}
