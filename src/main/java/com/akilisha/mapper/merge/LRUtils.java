package com.akilisha.mapper.merge;

import com.akilisha.mapper.definition.ClassDef;
import com.akilisha.mapper.definition.ClassDefCache;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LRUtils {

    public static final Map<Class<?>, Class<?>> WRAPPER_TYPE_MAP;
    public static MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();

    static {
        WRAPPER_TYPE_MAP = new HashMap<>(16);
        WRAPPER_TYPE_MAP.put(Integer.class, int.class);
        WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
        WRAPPER_TYPE_MAP.put(Character.class, char.class);
        WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
        WRAPPER_TYPE_MAP.put(Double.class, double.class);
        WRAPPER_TYPE_MAP.put(Float.class, float.class);
        WRAPPER_TYPE_MAP.put(Long.class, long.class);
        WRAPPER_TYPE_MAP.put(Short.class, short.class);
        WRAPPER_TYPE_MAP.put(Void.class, void.class);
    }

    public static boolean isMapType(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }

    public static boolean isCollectionType(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    public static boolean isArrayType(Class<?> type) {
        return type.isArray();
    }

    public static ClassDef objectDef(Object target) {
        Class<?> targetType = target.getClass();
        if (isMapType(targetType)) {
            ClassDef def = ClassDefCache.createAndCacheClassDef(targetType);
            Map<?, ?> map = (Map<?, ?>) target;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object value = entry.getValue();
                Class<?> valueType = value.getClass();
                def.getFields().put((String) entry.getKey(), valueType);
            }
            return def;
        } else if (isArrayType(targetType) || isCollectionType(targetType)) {
            throw new RuntimeException("Cannot inspect a list of array for fields. Try to inspect the collection or " +
                    "array elements individually instead");
        } else {
            return ClassDefCache.createAndCacheClassDef(targetType);
        }
    }

    public static Object newInstance(Class<?> type) throws Throwable {
        MethodType mt = MethodType.methodType(void.class);
        MethodHandle newType = publicLookup.findConstructor(type, mt);
        return newType.invoke();
    }
}
