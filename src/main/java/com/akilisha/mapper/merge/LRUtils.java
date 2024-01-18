package com.akilisha.mapper.merge;

import com.akilisha.mapper.definition.ClassDef;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collection;
import java.util.Map;

public class LRUtils {

    public static MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();

    public static boolean isMapType(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }

    public static boolean isCollectionType(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    public static boolean isArrayType(Class<?> type) {
        return type.isArray();
    }

    public static ClassDef objectDef(Object target) throws IOException {
        Class<?> targetType = target.getClass();
        if (isMapType(targetType)) {
            ClassDef def = new ClassDef(targetType);
            Map<String, Object> map = (Map<String, Object>) target;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object value = entry.getValue();
                Class<?> valueType = value.getClass();
                def.getFields().put(entry.getKey(), valueType);
            }
            return def;
        } else if (isArrayType(targetType) || isCollectionType(targetType)) {
            throw new RuntimeException("Cannot inspect a list of array for fields. Try to inspect the collection or " +
                    "array elements individually instead");
        } else {
            ClassDef def = new ClassDef(targetType);
            ClassReader cr = new ClassReader(targetType.getName());
            cr.accept(def, 0);
            return def;
        }
    }

    public static Object newInstance(Class<?> type) throws Throwable {
        MethodType mt = MethodType.methodType(void.class);
        MethodHandle newType = publicLookup.findConstructor(type, mt);
        return newType.invoke();
    }
}
