package com.akilisha.mapper.definition;

import java.util.HashMap;

public class ClassDefs extends HashMap<Class<?>, ClassDef> {

    public static ClassDefs cached = new ClassDefs();

    private ClassDefs() {
    }

    public ClassDef get(Class<?> key) {
        if (!super.containsKey(key)) {
            super.put(key, ClassDef.newClassDef(key));
        }
        return super.get(key);
    }
}
