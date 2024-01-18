package com.akilisha.mapper.merge;

import com.akilisha.mapper.definition.ClassDef;
import com.akilisha.mapper.wrapper.ObjWrapper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;
import java.util.function.Function;

import static com.akilisha.mapper.merge.LRUtils.newInstance;

public interface LRMerge {

    MethodHandles.Lookup lookup = MethodHandles.lookup();

    default void merge(ObjWrapper<?> src, ClassDef srcDef, ObjWrapper<?> dest, ClassDef destDef, LRMapping mapping) throws Throwable {
        //for each field in the srcDef, discover whether there's a matching field on the destDef
        for (String srcField : srcDef.getFields().keySet()) {
            // Scenario 1 - source fieldName matches dest fieldName exactly
            if (destDef.getFields().containsKey(srcField)) {
                // 1.1 find matching types
                Object srcType = srcDef.getFields().get(srcField);
                if (destDef.getFields().get(srcField) == srcType) {
                    Object srcValue = getFieldValue(src, srcField, (Class<?>) srcType);
                    setFieldValue(dest, srcField, (Class<?>) srcType, srcValue);
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
                            mapToNestedElement(src, dest, destDef, mapping, destField);
                        } else if (converter.srcCollectionType != null && converter.destCollectionType != null) {
                            LRMapping nestedMapping = converter.nestedMapping;
                            List<Object> srcListValues = (List<Object>) getFieldValue(src, srcField, (Class<?>) srcType);
                            if (srcListValues != null) {
                                Class<?> destCollectionCls = (Class<?>) destDef.getFields().get(destField);
                                for (Object innerSrc : srcListValues) {
                                    Object innerDest = newInstance(converter.destCollectionType);
                                    nestedMapping.merge(innerSrc, innerDest);
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
                    mapFromNestedElement(src, srcDef, dest, mapping, srcField);
                }
            } else {
                System.out.printf("Ignoring '%s' from '%s' object since it has no mapped field in the '%s' object\n",
                        srcField,
                        srcDef.getType().getSimpleName(),
                        destDef.getType().getSimpleName());
            }
        }
    }

    private void mapFromNestedElement(ObjWrapper<?> src, ClassDef srcDef, ObjWrapper<?> dest, LRMapping mapping, String srcField) throws Throwable {
        //1. Strip off the top-level name in the nested mapping to create another mapping
        Map<String, LRConverter<?>> nestedMapping = mapping.get(srcField).nestedMapping;
        //2. retrieve element from lhs collection
        Object innerDestTypeHint = srcDef.getFields().get(srcField);
        if (innerDestTypeHint.getClass() == ClassDef.class) {
            Class<?> innerDestType = ((ClassDef) innerDestTypeHint).getType();
            Object embeddedElement = getNestedValue(src, srcField, innerDestType);
            //3. merge 'inner' left with 'right'
            LRMapping.init(nestedMapping).merge(embeddedElement, dest.getThisTarget());
        } else {
            Object listElement = getListValue(srcField, (Class<?>) srcDef.getFields().get(srcField), src, 0);
            //3. merge 'inner' left with 'right'
            LRMapping.init(nestedMapping).merge(listElement, dest.getThisTarget());
        }
    }

    private void mapToNestedElement(ObjWrapper<?> src, ObjWrapper<?> dest, ClassDef destDef, LRMapping mapping, String destField) throws Throwable {
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
            LRMapping.init(nestedMapping).merge(src.getThisTarget(), innerDestValue);

            //4. add inner dest value in the collection on the 'dest'
            setFieldValue(dest, innerDestField, innerDestType, innerDestValue);
        } else {
            Class<?> innerDestType = nestedMapping.values().toArray(LRConverter[]::new)[0].getDestCollectionType();
            Object innerDestValue = innerDestType.getConstructor().newInstance();

            //3. Merge 'left' with inner 'right;
            LRMapping.init(nestedMapping).merge(src.getThisTarget(), innerDestValue);

            //4. add inner dest value in the collection on the 'dest'
            Class<?> collectionType = (Class<?>) destDef.getFields().get(innerDestField);
            addListValue(innerDestField, collectionType, dest, innerDestValue);
        }
    }

    default boolean unboxedMatch(Class<?> srcType, Class<?> destType) {
        return srcType.getSimpleName().toLowerCase().startsWith(destType.getSimpleName().toLowerCase())
                || destType.getSimpleName().toLowerCase().startsWith(srcType.getSimpleName().toLowerCase());
    }

    default Object getNestedValue(ObjWrapper<?> src, String srcField, Class<?> srcType) throws Throwable {
        MethodType getterMethodType = MethodType.methodType(srcType);
        String getter = "get" + Character.toUpperCase(srcField.charAt(0)) + srcField.substring(1);
        MethodHandle getterHandler = lookup.findVirtual(src.getThisTarget().getClass(), getter, getterMethodType);
        return getterHandler.invoke(src.getThisTarget());
    }

    default Object getFieldValue(ObjWrapper<?> src, String srcField, Class<?> srcType) throws Throwable {
        MethodType getterMethodType = MethodType.methodType(srcType);
        Object fieldValue;
        String getter = "get" + Character.toUpperCase(srcField.charAt(0)) + srcField.substring(1);
        if (ClassDef.isJavaType(srcType)) {
            MethodHandle getterHandler;
            try {
                getterHandler = lookup.findVirtual(src.getThisTarget().getClass(), getter, getterMethodType);
                fieldValue = getterHandler.invoke(src.getThisTarget());
            } catch (NoSuchMethodException th) {
                try {
                    getter = "is" + Character.toUpperCase(srcField.charAt(0)) + srcField.substring(1);
                    getterHandler = lookup.findVirtual(src.getThisTarget().getClass(), getter, getterMethodType);
                    fieldValue = getterHandler.invoke(src.getThisTarget());
                } catch (NoSuchMethodException thx) {
                    // at this point, it should only be a map
                    fieldValue = ((Map<?, ?>) src.getThisTarget()).get(srcField);
                }
            }

            return fieldValue;
        } else {
            throw new RuntimeException("Expected a java type from the source object. Please report this usage" +
                    "scenario to the developers for consideration in future releases");
        }
    }

    default void setFieldValue(ObjWrapper<?> dest, String destField, Class<?> destType, Object destValue) throws Throwable {
        MethodType setterMethodType = MethodType.methodType(void.class, destType);
        String setter = "set" + Character.toUpperCase(destField.charAt(0)) + destField.substring(1);
        try {
            MethodHandle setterHandler = lookup.findVirtual(dest.getThisTarget().getClass(), setter, setterMethodType);
            setterHandler.invoke(dest.getThisTarget(), destValue);
        } catch (NoSuchMethodException th) {
            // at this point, it should only be a map
            setterMethodType = MethodType.methodType(Object.class, Object.class, Object.class);
            MethodHandle setterHandler = lookup.findVirtual(dest.getThisTarget().getClass(), "put", setterMethodType);
            setterHandler.invoke(dest.getThisTarget(), destField, destValue);
        }
    }

    default void addListValue(String fieldName, Class<?> fieldType, ObjWrapper<?> dest, Object listValue) throws Throwable {
        Object listFieldValue = getFieldValue(dest, fieldName, fieldType);

        if (listFieldValue == null) {
            // manually creating ArrayList and setting as the field value since the field is currently NULL
            if (List.class.isAssignableFrom(fieldType)) {
                listFieldValue = new ArrayList<>();
            } else if (Set.class.isAssignableFrom(fieldType)) {
                listFieldValue = new HashSet<>();
            } else {
                throw new RuntimeException("Currently supports only collections of type List or Set");
            }
            setFieldValue(dest, fieldName, fieldType, listFieldValue);
        }

        MethodType addMethodType = MethodType.methodType(boolean.class, Object.class);
        MethodHandle addHandler = lookup.findVirtual(listFieldValue.getClass(), "add", addMethodType);
        addHandler.invoke(listFieldValue, listValue);
    }

    default Object getListValue(String fieldName, Class<?> fieldType, ObjWrapper<?> dest, int index) throws Throwable {
        Object listFieldValue = getFieldValue(dest, fieldName, fieldType);

        MethodType getMethodType = MethodType.methodType(Object.class, int.class);
        MethodHandle addHandler = lookup.findVirtual(listFieldValue.getClass(), "get", getMethodType);
        return addHandler.invoke(listFieldValue, index);
    }
}
