package com.akilisha.mapper.merge;

import com.akilisha.mapper.definition.ClassDef;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

import static com.akilisha.mapper.merge.LRUtils.*;

public interface LRMerge {

    MethodHandles.Lookup lookup = MethodHandles.lookup();

    static Object createCollectionField(Class<?> fieldType) {
        Object listFieldValue;
        if (List.class.isAssignableFrom(fieldType)) {
            listFieldValue = new ArrayList<>();
        } else if (Set.class.isAssignableFrom(fieldType)) {
            listFieldValue = new HashSet<>();
        } else {
            throw new RuntimeException("Currently supports only collections of type List or Set");
        }
        return listFieldValue;
    }

    default void merge(Object src, ClassDef srcDef, Object dest, ClassDef destDef, LRMapping mapping, LRContext context) throws Throwable {
        //for each field in the srcDef, discover whether there's a matching field on the destDef
        for (String srcField : srcDef.getFields().keySet()) {
            // Scenario 1 - source fieldName matches dest fieldName exactly
            if (destDef.getFields().containsKey(srcField)) {
                // 1.1 find matching types
                Object srcType = srcDef.getFields().get(srcField);
                if (destDef.getFields().get(srcField) == srcType) {
                    // this
                    if (ClassDef.class.isAssignableFrom(srcType.getClass())) {
                        Class<?> embeddedElementTYpe = ((ClassDef) srcType).getType();
                        Object innerSrcValue = getNestedValue(src, srcField, embeddedElementTYpe);
                        if (innerSrcValue != null) {
                            try {
                                Object embeddedEDestValue = embeddedElementTYpe.getConstructor().newInstance();
                                LRMapping.init().merge(innerSrcValue, embeddedEDestValue, context.trace(src, innerSrcValue, srcField));
                                setFieldValue(dest, srcField, embeddedElementTYpe, embeddedEDestValue);
                            } catch (Throwable e) {
                                throw new RuntimeException(e);
                            }
                        }
                        continue;
                    }

                    // each of these scenarios deserves special treatment
                    Class<?> srcClass = (Class<?>) srcType;
                    if (isArrayType(srcClass)) {
                        Object arrValue = getNestedValue(src, srcField, srcClass);
                        if (arrValue != null) {
                            try {
                                int length = Array.getLength(arrValue);
                                Class<?> componentType = srcClass.componentType();
                                for (int i = 0; i < length; i++) {
                                    Object innerSrcValue = Array.get(arrValue, i);
                                    Object innerDestValue = componentType.getConstructor().newInstance();
                                    LRMapping.init().merge(innerSrcValue, innerDestValue, context.trace(src, innerSrcValue, srcField));
                                    addArrayValue(srcField, srcClass, dest, innerDestValue, length, i);
                                }
                            } catch (Throwable e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else if (isCollectionType(srcClass)) {
                        Object nested = getNestedValue(src, srcField, srcClass);
                        if (nested != null) {
                            try {
                                Collection<?> collectionValue = (Collection<?>) nested;
                                for (Object innerSrcValue : collectionValue) {
                                    Class<?> innerSrcType = innerSrcValue.getClass();
                                    Object innerDestValue = innerSrcType.getConstructor().newInstance();
                                    LRMapping.init().merge(innerSrcValue, innerDestValue, context.trace(src, innerSrcValue, srcField));
                                    addListValue(srcField, srcClass, dest, innerDestValue);
                                }
                            } catch (Throwable e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else if (isMapType(srcClass)) {
                        Object nested = getNestedValue(src, srcField, srcClass);
                        if (nested != null) {
                            try {
                                Map<?, ?> mapValue = (Map<?, ?>) nested;
                                for (Map.Entry<?, ?> innerSrcEntry : mapValue.entrySet()) {
                                    Object innerDestKey = innerSrcEntry.getKey();
                                    if (isOkMapKeyType(innerDestKey.getClass())) {
                                        Class<?> innerSrcType = innerSrcEntry.getValue().getClass();
                                        Object innerSrcValue = innerSrcEntry.getValue();
                                        Object innerDestValue = innerSrcType.getConstructor().newInstance();
                                        LRMapping.init().merge(innerSrcValue, innerDestValue, context.trace(src, innerSrcValue, srcField));
                                        addMapValue(srcField, srcClass, dest, innerDestKey, innerDestValue);
                                    } else {
                                        System.out.printf("Skipping map key %s of type %s since it is not an acceptable type", innerDestKey, innerDestKey.getClass());
                                    }
                                }
                            } catch (Throwable e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else if (srcClass.isEnum()) {
                        Object srcValue = getNestedValue(src, srcField, srcClass);
                        if (srcValue != null) {
                            try {
                                setFieldValue(dest, srcField, srcClass, srcValue);
                            } catch (Throwable e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        Object srcValue = getFieldValue(src, srcField, srcClass);
                        if (srcValue != null) {
                            setFieldValue(dest, srcField, srcClass, srcValue);
                        }
                    }
                } else {
                    Class<?> destType = (Class<?>) destDef.getFields().get(srcField);
                    // types could be similar but only when unboxed
                    if (unboxedMatch((Class<?>) srcType, destType)) {
                        Object srcValue = getFieldValue(src, srcField, (Class<?>) srcType);
                        setFieldValue(dest, srcField, destType, srcValue);
                    } else {
                        //fields having different types require a converter function
                        Function<Object, ?> converter = mapping.get(srcField).getEval();
                        Object srcValue = getFieldValue(src, srcField, (Class<?>) srcType);
                        Object destValue = converter.apply(srcValue);
                        setFieldValue(dest, srcField, destType, destValue);
                    }
                }
                continue;
            }

            // Scenario 2 - source fieldName does not match dest fieldName exactly (one-to-one mapping)
            LRConverter<?> converter = mapping.get(srcField);
            if (converter != null) {
                String destField = converter.getFieldName();
                if (destField != null) {
                    //2.1 find matching types
                    // note that srcType can either be a value type (Class<?>) or a type definition (ClassCef)
                    Object srcType = srcDef.getFields().get(srcField);
                    if (destDef.getFields().get(destField) == null || srcType == destDef.getFields().get(destField)) {
                        // a missing 'defType' in an explicit mapping means that it matches the 'srcType'
                        Object srcValue = getFieldValue(src, srcField, (Class<?>) srcType);
                        //2.1.1 - dest should not contain a '.' character. This indicates a nested object in the destination
                        if (destField.contains(".")) {
                            mapToNestedElement(src, dest, destDef, mapping, destField, context);
                        } else if (converter.srcCollectionType != null && converter.destCollectionType != null) {
                            LRMapping nestedMapping = converter.nestedMapping;
                            List<?> srcListValues = (List<?>) getFieldValue(src, srcField, (Class<?>) srcType);
                            if (srcListValues != null) {
                                Class<?> destCollectionCls = (Class<?>) destDef.getFields().get(destField);
                                for (Object innerSrc : srcListValues) {
                                    Object innerDest = newInstance(converter.destCollectionType);
                                    nestedMapping.merge(innerSrc, innerDest, context);
                                    addListValue(destField, destCollectionCls, dest, innerDest);
                                }
                            }
                        } else {
                            setFieldValue(dest, destField, (Class<?>) srcType, srcValue);
                        }
                    } else {
                        Class<?> destType = (Class<?>) destDef.getFields().get(destField);
                        // types could be similar but only when unboxed
                        if (unboxedMatch((Class<?>) srcType, destType)) {
                            Object srcValue = getFieldValue(src, srcField, (Class<?>) srcType);
                            setFieldValue(dest, destField, destType, srcValue);
                        } else {
                            //fields having different types require a converter function
                            Function<Object, ?> function = converter.getEval();
                            Object srcValue = getFieldValue(src, srcField, (Class<?>) srcType);
                            Object destValue = function.apply(srcValue);
                            setFieldValue(dest, destField, destType, destValue);
                        }
                    }
                } else {
                    mapFromNestedElement(src, srcDef, dest, mapping, srcField, context);
                }
            } else {
                System.out.printf("Ignoring '%s' from '%s' object since it has no mapped field in the '%s' object\n",
                        srcField,
                        srcDef.getType().getSimpleName(),
                        destDef.getType().getSimpleName());
            }
        }
    }

    private void mapFromNestedElement(Object src, ClassDef srcDef, Object dest, LRMapping mapping, String srcField, LRContext context) throws Throwable {
        //1. Strip off the top-level name in the nested mapping to create another mapping
        Map<String, LRConverter<?>> nestedMapping = mapping.get(srcField).nestedMapping;
        //2. retrieve element from lhs collection
        Object innerDestTypeHint = srcDef.getFields().get(srcField);
        if (innerDestTypeHint.getClass() == ClassDef.class) {
            Class<?> innerDestType = ((ClassDef) innerDestTypeHint).getType();
            Object embeddedElement = getNestedValue(src, srcField, innerDestType);
            //3. merge 'inner' left with 'right'
            try {
                LRMapping.init(nestedMapping).merge(embeddedElement, dest, context);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        } else {
            Object listElement = getListValue(srcField, (Class<?>) srcDef.getFields().get(srcField), src, 0);
            //3. merge 'inner' left with 'right'
            LRMapping.init(nestedMapping).merge(listElement, dest, context);
        }
    }

    private void mapToNestedElement(Object src, Object dest, ClassDef destDef, LRMapping mapping, String destField, LRContext context) throws Throwable {
        String innerDestField = destField.substring(0, destField.indexOf("."));
        //1. Strip off the top-level name in the nested mapping to create another mapping
        Map<String, LRConverter<?>> nestedMapping = new HashMap<>();
        for (Iterator<Map.Entry<String, LRConverter<?>>> iterator = mapping.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, LRConverter<?>> e = iterator.next();
            if (e.getValue().getFieldName().startsWith(innerDestField)) {
                nestedMapping.put(e.getKey(), new LRConverter<>(
                        e.getValue().getFieldName().substring(innerDestField.length() + 1),
                        e.getValue().getSrcCollectionType(),
                        e.getValue().getDestCollectionType(),
                        e.getValue().getEval(),
                        e.getValue().getNestedMapping()
                ));
                iterator.remove();
            }
        }

        //2. create an instance of nested destination - this could either be a collection element of an embedded element
        Object innerDestTypeHint = destDef.getFields().get(innerDestField);
        if (innerDestTypeHint.getClass() == ClassDef.class) {
            Class<?> innerDestType = ((ClassDef) innerDestTypeHint).getType();
            Object innerDestValue = innerDestType.getConstructor().newInstance();

            //3. Merge 'left' with inner 'right;
            LRMapping.init(nestedMapping).merge(src, innerDestValue, context);

            //4. add inner dest value in the collection on the 'dest'
            setFieldValue(dest, innerDestField, innerDestType, innerDestValue);
        } else {
            Class<?> innerDestType = nestedMapping.values().toArray(LRConverter[]::new)[0].getDestCollectionType();
            Object innerDestValue = innerDestType.getConstructor().newInstance();

            //3. Merge 'left' with inner 'right;
            LRMapping.init(nestedMapping).merge(src, innerDestValue, context);

            //4. add inner dest value in the collection on the 'dest'
            Class<?> collectionType = (Class<?>) destDef.getFields().get(innerDestField);
            addListValue(innerDestField, collectionType, dest, innerDestValue);
        }
    }

    default boolean unboxedMatch(Class<?> srcType, Class<?> destType) {
        return srcType.getSimpleName().toLowerCase().startsWith(destType.getSimpleName().toLowerCase())
                || destType.getSimpleName().toLowerCase().startsWith(srcType.getSimpleName().toLowerCase());
    }

    default boolean isOkMapKeyType(Class<?> type) {
        return type.isPrimitive() || WRAPPER_TYPE_MAP.containsKey(type);
    }

    default Object getNestedValue(Object src, String srcField, Class<?> srcType) throws Throwable {
        MethodType getterMethodType = MethodType.methodType(srcType);
        String getter = "get" + Character.toUpperCase(srcField.charAt(0)) + srcField.substring(1);
        MethodHandle getterHandler = lookup.findVirtual(src.getClass(), getter, getterMethodType);
        return getterHandler.invoke(src);
    }

    default Object getFieldValue(Object src, String srcField, Class<?> srcType) throws Throwable {
        MethodType getterMethodType = MethodType.methodType(srcType);
        Object fieldValue;
        String getter = "get" + Character.toUpperCase(srcField.charAt(0)) + srcField.substring(1);
        if (ClassDef.isJavaType(srcType)) {
            MethodHandle getterHandler;
            try {
                getterHandler = lookup.findVirtual(src.getClass(), getter, getterMethodType);
                fieldValue = getterHandler.invoke(src);
            } catch (NoSuchMethodException th) {
                try {
                    getter = "is" + Character.toUpperCase(srcField.charAt(0)) + srcField.substring(1);
                    getterHandler = lookup.findVirtual(src.getClass(), getter, getterMethodType);
                    fieldValue = getterHandler.invoke(src);
                } catch (NoSuchMethodException thx) {
                    // at this point, it should only be a map
                    fieldValue = ((Map<?, ?>) src).get(srcField);
                }
            }

            return fieldValue;
        } else {
            throw new RuntimeException("Expected a java type from the source object. Please report this usage " +
                    "scenario to the developers for consideration in future releases");
        }
    }

    default void setFieldValue(Object dest, String destField, Class<?> destType, Object destValue) throws Throwable {
        MethodType setterMethodType = MethodType.methodType(void.class, destType);
        String setter = "set" + Character.toUpperCase(destField.charAt(0)) + destField.substring(1);
        try {
            MethodHandle setterHandler = lookup.findVirtual(dest.getClass(), setter, setterMethodType);
            setterHandler.invoke(dest, destValue);
        } catch (NoSuchMethodException th) {
            // at this point, it should only be a map
            setterMethodType = MethodType.methodType(Object.class, Object.class, Object.class);
            MethodHandle setterHandler = lookup.findVirtual(dest.getClass(), "put", setterMethodType);
            setterHandler.invoke(dest, destField, destValue);
        }
    }

    default void addListValue(String fieldName, Class<?> fieldType, Object dest, Object listValue) throws Throwable {
        Object listFieldValue = getFieldValue(dest, fieldName, fieldType);

        if (listFieldValue == null) {
            // manually creating a collection object and setting as the field value since the field is currently NULL
            listFieldValue = createCollectionField(fieldType);
            setFieldValue(dest, fieldName, fieldType, listFieldValue);
        }

        MethodType addMethodType = MethodType.methodType(boolean.class, Object.class);
        MethodHandle addHandler = lookup.findVirtual(listFieldValue.getClass(), "add", addMethodType);
        addHandler.invoke(listFieldValue, listValue);
    }

    default Object getListValue(String fieldName, Class<?> fieldType, Object dest, int index) throws Throwable {
        Object listFieldValue = getFieldValue(dest, fieldName, fieldType);

        MethodType getMethodType = MethodType.methodType(Object.class, int.class);
        MethodHandle addHandler = lookup.findVirtual(listFieldValue.getClass(), "get", getMethodType);
        return addHandler.invoke(listFieldValue, index);
    }

    default void addArrayValue(String fieldName, Class<?> fieldType, Object dest, Object arrayValue, int capacity, int index) throws Throwable {
        Object arrayFieldValue = getNestedValue(dest, fieldName, fieldType);

        if (arrayFieldValue == null) {
            // manually creating an array object and setting as the field value since the field is currently NULL
            Class<?> componentType = fieldType.componentType();
            arrayFieldValue = Array.newInstance(componentType, capacity);
            setFieldValue(dest, fieldName, fieldType, arrayFieldValue);
        }

        Array.set(arrayFieldValue, index, arrayValue);
    }

    default void addMapValue(String fieldName, Class<?> fieldType, Object dest, Object keyValue, Object mapValue) throws Throwable {
        Object mapFieldValue = getFieldValue(dest, fieldName, fieldType);

        if (mapFieldValue == null) {
            // manually creating a map object and setting as the field value since the field is currently NULL
            mapFieldValue = new HashMap<>();
            setFieldValue(dest, fieldName, fieldType, mapFieldValue);
        }

        MethodType addMethodType = MethodType.methodType(Object.class, Object.class, Object.class);
        MethodHandle addHandler = lookup.findVirtual(mapFieldValue.getClass(), "put", addMethodType);
        addHandler.invoke(mapFieldValue, keyValue, mapValue);
    }
}
