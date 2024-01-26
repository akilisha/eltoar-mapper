package com.akilisha.mapper.merge;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LRUtils {

    public static final Map<Class<?>, Class<?>> WRAPPER_TYPE_MAP;

    static {
        WRAPPER_TYPE_MAP = new LinkedHashMap<>(16);
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

    public static Map<String, Object> unfoldProperties(Map<String, Object> src) {
        for (Iterator<Map.Entry<String, Object>> iter = src.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, Object> e = iter.next();
            String key = e.getKey();
            if (key.contains(".")) {
                String pre = key.substring(0, key.indexOf("."));
                String post = key.substring(pre.length() + 1);
                if (!src.containsKey(pre)) {
                    Map<String, Object> dest = new ConcurrentHashMap<>();
                    dest.put(post, src.get(key));
                    src.put(pre, unfoldProperties(dest));
                } else {
                    Map<String, Object> dest = (Map) src.get(pre);
                    dest.put(post, src.get(key));
                    src.put(pre, unfoldProperties(dest));
                }
                iter.remove();
            }
        }
        return src;
    }

    public static Object autoConvertNumeric(Object input, Class<?> inType, Class<?> outType) {
        if (Number.class.isAssignableFrom(inType)) {
            Number value = (Number) input;
            if (outType.equals(short.class) || outType.equals(Short.class)) return value.shortValue();
            if (outType.equals(int.class) || outType.equals(Integer.class)) return value.intValue();
            if (outType.equals(float.class) || outType.equals(Float.class)) return value.floatValue();
            if (outType.equals(long.class) || outType.equals(Long.class)) return value.longValue();
            if (outType.equals(BigDecimal.class)) return new BigDecimal(value.toString());
        }
        return input;
    }
}
