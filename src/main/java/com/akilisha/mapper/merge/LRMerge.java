package com.akilisha.mapper.merge;

import com.akilisha.mapper.definition.ClassDef;
import com.akilisha.mapper.definition.ClassDefs;
import com.akilisha.mapper.definition.FieldDef;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static com.akilisha.mapper.definition.ClassDef.isJavaType;
import static com.akilisha.mapper.definition.ClassDef.objectDef;

public interface LRMerge {

    MethodHandles.Lookup lookup = MethodHandles.lookup();

    static boolean isMapType(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }

    static boolean isCollectionType(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    static boolean isArrayType(Class<?> type) {
        return type.isArray();
    }

    static Object autoConvertNumeric(Object input, Class<?> inType, Class<?> outType) {
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

    static Object createDictionaryField(Class<?> fieldType) {
        if (Map.class.isAssignableFrom(fieldType)) {
            return new LinkedHashMap<>();
        }
        throw new RuntimeException("Currently supports only creating a LinkedHashMap");
    }

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

    static int getContainerSize(Object arrayOrCollection) {
        if (Collection.class.isAssignableFrom(arrayOrCollection.getClass())) {
            return ((Collection<?>) arrayOrCollection).size();
        }
        if (arrayOrCollection.getClass().isArray()) {
            return Array.getLength(arrayOrCollection);
        }
        throw new RuntimeException("Expected either a Collection or an Array type");
    }

    static LRMapping getNestedOrDefault(String key, LRMapping nestedMapping) {
        return Optional.ofNullable(nestedMapping.get(key)).filter(o -> o.nestedMapping != null).map(o -> nestedMapping.get(key).nestedMapping).orElse(LRMapping.init());
    }

    static String getOrDefault(String strValue, String defaultValue) {
        return strValue != null && !strValue.trim().isEmpty() ? strValue : defaultValue;
    }

    static Map<String, FieldDef> wrapSourceFieldDef(FieldDef entry) {
        Map<String, FieldDef> singleField = new ConcurrentHashMap<>();
        singleField.put(entry.getName(), entry);
        return singleField;
    }

    static boolean unboxedMatch(Class<?> srcType, Class<?> destType) {
        return srcType.getSimpleName().toLowerCase().startsWith(destType.getSimpleName().toLowerCase())
                || destType.getSimpleName().toLowerCase().startsWith(srcType.getSimpleName().toLowerCase());
    }

    static Object getEmbeddedValue(Object src, String srcField, Class<?> srcType) throws Throwable {
        try {
            MethodType getterMethodType = MethodType.methodType(srcType);
            String getter = "get" + Character.toUpperCase(srcField.charAt(0)) + srcField.substring(1);
            MethodHandle getterHandler = lookup.findVirtual(src.getClass(), getter, getterMethodType);
            return getterHandler.invoke(src);
        } catch (NoSuchMethodException thx) {
            // at this point, it should only be a map
            return ((Map<?, ?>) src).get(srcField);
        }
    }

    static Object getFieldValue(Object src, String srcField, Class<?> srcType) throws Throwable {
        MethodType getterMethodType = MethodType.methodType(srcType);
        Object fieldValue;
        String getter = "get" + Character.toUpperCase(srcField.charAt(0)) + srcField.substring(1);
        if (isJavaType(srcType)) {
            MethodHandle getterHandler;
            try {
                getterHandler = lookup.findVirtual(src.getClass(), getter, getterMethodType);
                fieldValue = getterHandler.invoke(src);
            } catch (NoSuchMethodException | IllegalAccessException th) {
                try {
                    getter = "is" + Character.toUpperCase(srcField.charAt(0)) + srcField.substring(1);
                    getterHandler = lookup.findVirtual(src.getClass(), getter, getterMethodType);
                    fieldValue = getterHandler.invoke(src);
                } catch (NoSuchMethodException | IllegalAccessException thx) {
                    // at this point, it should only be a map
                    fieldValue = ((Map<?, ?>) src).get(srcField);
                }
            }

            return fieldValue;
        } else {
            throw new RuntimeException("Expected a java type from the source object. Please report this usage " +
                    "carshop to the developers for consideration in future releases");
        }
    }

    static void setFieldValue(Object dest, String destField, Class<?> destType, Object destValue) throws Throwable {
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

    static void addCollectionValue(String fieldName, Class<?> fieldType, Object dest, Object listValue) throws Throwable {
        Collection<Object> collectionField = (Collection<Object>) getFieldValue(dest, fieldName, fieldType);
        if (collectionField == null) {
            // manually creating a collection object and setting as the field value since the field is currently NULL
            collectionField = (Collection<Object>) createCollectionField(fieldType);
            setFieldValue(dest, fieldName, fieldType, collectionField);
        }
        collectionField.add(listValue);
    }

    static boolean isValidMapKey(Object key) {
        Class<?> type = key.getClass();
        return type == String.class || (type == Double.class || type == Float.class || type == Long.class ||
                type == Integer.class || type == Short.class || type == Character.class ||
                type == Byte.class || type == Boolean.class);
    }

    static void setJavaTypeValue(FieldDef srcFieldDef, Object dest, FieldDef destFieldDef) throws Throwable {
        Class<?> srcType = srcFieldDef.getType();
        Class<?> destType = destFieldDef.getType();

        // if boxed/unboxed types match, all is good
        if (unboxedMatch(srcType, destType)) {
            Object srcValue = srcFieldDef.getValue();
            setFieldValue(dest, srcFieldDef.getName(), destType, srcValue);
            return;
        }

        //try another way
        Object converted = autoConvertNumeric(srcFieldDef.getValue(), srcType, destType);
        if (converted != null) {
            setFieldValue(dest, srcFieldDef.getName(), destType, converted);
            return;
        }

        // ok, last ditch effort
        Object destValue = destFieldDef.getValue();
        setFieldValue(dest, destFieldDef.getName(), destType, destValue);

    }

    static Object extractMapKey(Object srcElement, Map<String, FieldDef> srcFieldsMap, String srcFieldName, LRMapping mapping) throws Throwable {
        Object keyValue;
        String keyField = Optional.ofNullable(mapping.get(srcFieldName)).map(LRPathway::getKeyField).orElse(null);
        if (keyField != null) {
            keyValue = getFieldValue(srcElement, keyField, srcFieldsMap.get(keyField).getType());
        } else {
            Supplier<?> keySupplier = mapping.get(srcFieldName).keySupplier;
            if (keySupplier != null) {
                keyValue = keySupplier.get();
            } else {
                throw new RuntimeException("Mapping collection to a Map type, but their is not key field or key supplier function provided");
            }
        }
        return keyValue;
    }

    private static Class<?> bestEffortInferDestElementType(LRPathway<?> converter, FieldDef destFieldDef) {
        Class<?> destElementType = converter.collectionType;
        //last minute effort to infer the destination element type
        if (destElementType == null) {
            System.out.printf("The destination object type is not configured. Last minute match will try to use '%s' type\n", destFieldDef.getType());
            destElementType = destFieldDef.getType();
        }
        return destElementType;
    }

    static Class<?> getMappedElementType(String srcFieldName, LRMapping mapping, Object collectionElement) {
        Class<?> destElementType = Optional.ofNullable(mapping.get(srcFieldName)).map(o -> o.collectionType).orElse(null);
        if (destElementType == null) {
            System.out.printf("The 'destination element type' is not available. Last minute match will try to use '%s' type\n", collectionElement.getClass());
            destElementType = collectionElement.getClass();
        }
        return destElementType;
    }

    default Map<String, FieldDef> fieldValues(Object src) throws Throwable {
        if (Map.class.isAssignableFrom(src.getClass())) {
            return fieldValues(src, objectDef(src));
        } else {
            return fieldValues(src, ClassDefs.cached.get(src.getClass()));
        }
    }

    default Map<String, FieldDef> fieldValues(Object src, ClassDef srcDef) throws Throwable {
        Map<String, FieldDef> fields = new ConcurrentHashMap<>();
        for (Map.Entry<String, FieldDef> entry : srcDef.getFields().entrySet()) {
            String key = entry.getKey();
            FieldDef def = entry.getValue();
            Class<?> type = def.getType();
            if (!ClassDef.class.isAssignableFrom(type)) {
                if (isJavaType(type)) {
                    fields.put(key, FieldDef.define(def.getName(), def.getType(), getFieldValue(src, key, type)));
                } else {
                    fields.put(key, FieldDef.define(def.getName(), def.getType(), getEmbeddedValue(src, key, type)));
                }
                continue;
            }
            fields.put(key, FieldDef.define(def.getName(), def.getType(), def.getValue()));
        }
        return fields;
    }

    default void merge(Object src, Map<String, FieldDef> srcDef, Object dest, Map<String, FieldDef> destDef, LRMapping mapping, LRContext context) throws Throwable {
        //iterate over each field in the source (left) and map to the corresponding field in the destination (right)
        for (Iterator<Map.Entry<String, FieldDef>> iter = srcDef.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, FieldDef> entry = iter.next();
            String srcField = entry.getKey();
            FieldDef srcFieldDef = entry.getValue();
            iter.remove();

            if (srcFieldDef.getValue() != null) {
                Object srcValue = srcFieldDef.getValue();

                // perform some housekeeping to detect circular dependency
                context.trace(src, dest, Optional.ofNullable(destDef.get(srcField)).map(FieldDef::getName).orElse(srcField));

                // Start with the low-hanging fruits - where there is no explicit mapping
                // All valid mapping is done relative to the source field, and therefore if an entry is missing, then
                // there is definitely no valid mapping exists
                LRPathway<?> pathway = mapping.get(srcField);
                if (pathway == null) {
                    //using implicit mapping at this point
                    if (destDef.containsKey(srcField)) {
                        FieldDef destFieldDef = destDef.get(srcField);

                        if (destFieldDef != null) {

                            if (ClassDef.class.isAssignableFrom(destFieldDef.getType())) {
                                implicitCopyLhsEmbeddedToRhsEmbedded(src, srcFieldDef, dest, destFieldDef, srcField, context);
                                continue;
                            }

                            if (destFieldDef.getType().isArray()) {
                                Class<?> componentType = destFieldDef.getType().componentType();

                                int containerSize = getContainerSize(srcValue);
                                Object destArray = Array.newInstance(componentType, containerSize);

                                //source could either be an array of collection
                                if (srcFieldDef.getType().isArray()) {
                                    implicitCopyLhsArrayToRhsArray(srcFieldDef, componentType, destArray, containerSize, context);
                                }

                                if (Collection.class.isAssignableFrom(srcFieldDef.getType())) {
                                    implicitCopyLhsCollectionToRhsArray(srcFieldDef, componentType, destArray, context);
                                }

                                setFieldValue(dest, destFieldDef.getName(), destFieldDef.getType(), destArray);
                                continue;
                            }

                            if (Collection.class.isAssignableFrom(destFieldDef.getType())) {
                                implicitCopyLhsCollectionToRhsCollection(srcFieldDef, dest, destFieldDef, context);
                                continue;
                            }

                            if (Map.class.isAssignableFrom(destFieldDef.getType())) {
                                implicitCopyLhsMapToRhsMap(src, srcFieldDef, dest, destFieldDef, context);
                                continue;
                            }

                            // for implicit mapping, interface values need their own special treatment
                            if (destFieldDef.getType().isInterface()) {
                                Object concreteSourceObject = srcFieldDef.getValue();
                                Class<?> concreteSourceType = concreteSourceObject.getClass();
                                Object concreteDestObject = concreteSourceType.getConstructor().newInstance();
                                merge(concreteSourceObject, fieldValues(concreteSourceObject), concreteDestObject, fieldValues(concreteDestObject), LRMapping.init(), context);
                                // set created value into dest object
                                setFieldValue(dest, destFieldDef.getName(), destFieldDef.getType(), concreteDestObject);
                                continue;
                            }

                            // enums deserve their own special treatment
                            if (destFieldDef.getType().isEnum()) {
                                setFieldValue(dest, destFieldDef.getName(), destFieldDef.getType(), srcValue);
                                continue;
                            }

                            // this needs to come at the end of this section
                            if (isJavaType(destFieldDef.getType())) {
                                setJavaTypeValue(srcFieldDef, dest, destFieldDef);
                                continue;
                            }

                            throw new RuntimeException("This scenario is definitely not anticipated. Please report the usage that " +
                                    "produced this error to the developers for further review.");
                        } else {
                            throw new RuntimeException("It is very unlikely to end up in this scenario unless the mapping " +
                                    "defined was intentionally botched or sabotaged. Please report the setup which produced " +
                                    "this outcome to the developers for additional review.");
                        }
                    } else {
                        System.out.printf("Ignoring property '%s' in '%s'. With no explicit mapping created for this " +
                                        "property, it's expected that the field '%s' would be available in '%s' but it does " +
                                        "not exist there either\n",
                                srcField, src.getClass().getSimpleName(), srcField, dest.getClass().getSimpleName());
                    }

                    continue;
                }

                //you are now inevitably dealing with explicit mapping
                String destFieldName = getOrDefault(pathway.fieldName, srcField);

                //Scenario where destination (LHS) fieldName contains a '.' character
                if (destFieldName.contains(".")) {
                    // this happens when you have a relationship with a _1x1_ multiplicity through one of the following
                    //1. embedded element dependency, either LHS or RHS
                    //2. single-element array, either LHS or RHS

                    String parent = destFieldName.substring(0, destFieldName.indexOf("."));
                    String nested = destFieldName.substring(parent.length() + 1);
                    LRMapping nestedMapping = LRMapping.init().copy(srcField, nested, mapping.get(srcField));

                    // when RHS contains an embedded entity
                    FieldDef destFieldDef = destDef.get(parent);

                    if (ClassDef.class.isAssignableFrom(destFieldDef.getType())) {
                        explicitCopyLhsParentToRhsEmbedded(src, entry.getValue(), dest, destFieldDef, parent, nestedMapping, context);
                        continue;
                    }

                    // when RHS contains a single-element array
                    if (destFieldDef.getType().isArray()) {
                        explicitCopyLhsParentToRhsSingleElementArray(src, srcFieldDef, dest, destFieldDef, parent, nestedMapping, context);
                        continue;
                    }

                    // when RHS contains a single-element collection
                    if (Collection.class.isAssignableFrom(destFieldDef.getType())) {
                        Class<?> collectionElementType = pathway.collectionType;
                        explicitCopyLhsParentToRhsSingleElementCollection(src, srcFieldDef, dest, destFieldDef, parent, collectionElementType, nestedMapping, context);
                        continue;
                    }

                    if (Map.class.isAssignableFrom(destFieldDef.getType())) {
                        throw new RuntimeException("This scenario is definitely not anticipated. Please report the usage that " +
                                "produced this error to the developers for further review.");
                    }
                }

                // At this point, LHS field name cannot possibly contain a '.' character
                FieldDef destFieldDef = destDef.get(destFieldName);

                if (destFieldDef != null) {

                    // destination (rhs) is an embedded object
                    if (ClassDef.class.isAssignableFrom(destFieldDef.getType())) {
                        ClassDef nestedDef = (ClassDef) srcValue;
                        Class<?> srcNestedClassType = nestedDef.getType();

                        if (isMapType(srcNestedClassType)) {
                            Class<?> destNestedClassType = mapping.get(destFieldName).collectionType;
                            if (destNestedClassType == null) {
                                throw new RuntimeException(String.format(
                                        "Expected a collection type mapping for field'%s' in object '%s'", destFieldName, src.getClass().getSimpleName()));
                            }

                            Object nestedDestValue = destNestedClassType.getConstructor().newInstance();
                            Object nestedSrcValue = getEmbeddedValue(src, srcField, srcNestedClassType);
                            merge(nestedSrcValue, fieldValues(nestedSrcValue), nestedDestValue, fieldValues(nestedDestValue),
                                    mapping.get(srcField).nestedMapping, context);
                            setFieldValue(dest, destFieldName, destNestedClassType, nestedDestValue);

                            continue;
                        }

                        throw new RuntimeException("This scenario is definitely not anticipated. Please report the usage that " +
                                "produced this error to the developers for further review.");
                    }

                    // destination (rhs) is a collection
                    if (Collection.class.isAssignableFrom(destFieldDef.getType())) {
                        Class<?> destElementType = bestEffortInferDestElementType(pathway, destFieldDef);

                        // source (lhs) is an array
                        if (srcFieldDef.getType().isArray()) {
                            explicitCopyLhsArrayToRhsCollection(srcFieldDef, srcField, dest, destFieldDef, destFieldName, destElementType, context, mapping);
                            continue;
                        }

                        // source (lhs) is a collection
                        if (Collection.class.isAssignableFrom(srcFieldDef.getType())) {
                            explicitCopyLhsCollectionToRhsCollection(srcFieldDef, srcField, dest, destFieldDef, destFieldName, mapping, context);
                            continue;
                        }

                        // source (lhs) is a map
                        if (Map.class.isAssignableFrom(srcFieldDef.getType())) {
                            explicitCopyLhsMapValuesToRhsCollection(srcFieldDef, srcField, dest, destFieldDef, destFieldName, context, mapping);
                            continue;
                        }

                        throw new RuntimeException("This scenario is definitely not anticipated. Please report the usage that " +
                                "produced this error to the developers for further review.");
                    }

                    // destination (rhs) is an array
                    if (destFieldDef.getType().isArray()) {
                        Class<?> destElementType = bestEffortInferDestElementType(pathway, destFieldDef);

                        // source (lhs) is an array
                        if (srcFieldDef.getType().isArray()) {
                            explicitCopyLhsArrayToRhsArray(srcFieldDef, srcField, dest, destFieldDef, destFieldName, mapping, context);
                            continue;
                        }

                        // source (lhs) is a collection
                        if (Collection.class.isAssignableFrom(srcFieldDef.getType())) {
                            explicitCopyLhsCollectionToRhsArray(srcFieldDef, srcField, dest, destFieldDef, destFieldName, mapping, context);
                            continue;
                        }

                        // source (lhs) is a map
                        if (Map.class.isAssignableFrom(srcFieldDef.getType())) {
                            explicitCopyLhsMapValuesToRhsArray(srcFieldDef, dest, destFieldName, destFieldDef.getType(), mapping, context);
                            continue;
                        }

                        throw new RuntimeException("This scenario is definitely not anticipated. Please report the usage that " +
                                "produced this error to the developers for further review.");
                    }

                    // destination (rhs) is a map
                    if (Map.class.isAssignableFrom(destFieldDef.getType())) {
                        Class<?> srcFieldType = srcFieldDef.getType();

                        // source (lhs) is an array
                        if (srcFieldType.isArray()) {
                            explicitCopyLhsArrayToRhsMap(srcFieldDef, srcField, dest, destFieldDef, destFieldName, mapping, context);
                            continue;
                        }

                        // source (lhs) is a collection
                        if (Collection.class.isAssignableFrom(srcFieldType)) {
                            explicitCopyLhsCollectionToRhsMap(srcFieldDef, srcField, dest, destFieldDef, destFieldName, mapping, context);
                            continue;
                        }

                        // source (lhs) is a map
                        if (Map.class.isAssignableFrom(srcFieldType)) {
                            explicitCopyLhsMapEntriesToRhsMapEntries(srcFieldDef, dest, destFieldName, srcFieldType, context, mapping);
                            continue;
                        }

                        throw new RuntimeException("This scenario is definitely not anticipated. Please report the usage that " +
                                "produced this error to the developers for further review.");
                    }

                    // enums deserve their own special treatment
                    if (destFieldDef.getType().isEnum()) {
                        setFieldValue(dest, destFieldDef.getName(), destFieldDef.getType(), srcValue);
                        continue;
                    }

                    // this needs to come at the end of this section
                    if (isJavaType(destFieldDef.getType())) {
                        Object destValue = pathway.getConverter() != null
                                ? pathway.getConverter().apply(srcValue)
                                : srcValue;
                        setFieldValue(dest, destFieldDef.getName(), destFieldDef.getType(), destValue);
                        continue;
                    }
                } else {
                    // this special case happens when the LHS is mapped to a nested object or single-collection-element on the RHS
                    if (ClassDef.class.isAssignableFrom(srcFieldDef.getType())) {
                        explicitFlattenLhsEmbeddedIntoRhsDestination(src, srcFieldDef, srcField, dest, destDef, destFieldName, mapping, context);
                        continue;
                    }

                    if (Collection.class.isAssignableFrom(srcFieldDef.getType())) {
                        explicitFlattenLhsSingleElementCollectionIntoRhsDestination(srcFieldDef, srcField, dest, destDef, destFieldName, mapping, context);
                        continue;
                    }

                    if (srcFieldDef.getType().isArray()) {
                        explicitFlattenLhsSingleElementArrayIntoRhsDestination(srcFieldDef, dest, destDef, destFieldName, mapping, context);
                        continue;
                    }

                    throw new RuntimeException(String.format("At this point, you have most likely mapped a field '%s' " +
                            "in src object '%s' to an non-existent field '%s' in dest object '%s'.", srcField, src.getClass().getName(), destFieldName, dest.getClass().getName()));
                }
                continue;
            }

            System.out.printf("Skipping field '%s' in class '%s' since it is a null value\n", srcField, src.getClass().getName());
        }
    }

    default void explicitCopyLhsArrayToRhsCollection(FieldDef srcFieldDef, String srcFieldName, Object dest, FieldDef destFieldDef, String destFieldName, Class<?> destElementType, LRContext context, LRMapping mapping) throws Throwable {
        Object sourceArray = srcFieldDef.getValue();

        if (sourceArray != null) {
            Class<?> destCollectionType = destFieldDef.getType();
            Object destCollection = createCollectionField(destCollectionType);

            int size = Array.getLength(sourceArray);
            for (int i = 0; i < size; i++) {
                Object sourceElement = Array.get(sourceArray, i);
                // last minute effort to infer a suitable class type for 'destination element'
                if (destElementType == null || destElementType.isInterface()) {
                    System.out.printf("The 'destination element type' is not available. Last minute match will try to use '%s' type\n", sourceElement.getClass());
                    destElementType = sourceElement.getClass();
                }

                //proceed with merging
                Object destElement = destElementType.getConstructor().newInstance();
                merge(sourceElement, fieldValues(sourceElement), destElement, fieldValues(destElement), LRMerge.getNestedOrDefault(srcFieldName, mapping), context);
                ((Collection<Object>) destCollection).add(destElement);
            }
            setFieldValue(dest, destFieldName, destFieldDef.getType(), destCollection);
        }
    }

    private void implicitCopyLhsMapToRhsMap(Object src, FieldDef srcFieldDef, Object dest, FieldDef destFieldDef, LRContext context) throws Throwable {
        Class<?> mapType = destFieldDef.getType();

        Object srcMap = getEmbeddedValue(src, srcFieldDef.getName(), mapType);
        if (srcMap != null) {

            Object destMap = getEmbeddedValue(dest, destFieldDef.getName(), mapType);
            if (destMap == null) {
                destMap = createDictionaryField(mapType);
                setFieldValue(dest, destFieldDef.getName(), mapType, destMap);
            }

            for (Map.Entry<?, ?> mapEntry : ((Map<?, ?>) srcMap).entrySet()) {
                Object srcElement = mapEntry.getValue();
                Class<?> destElementType = srcElement.getClass();
                Object destElement = destElementType.getConstructor().newInstance();
                merge(srcElement, fieldValues(srcElement), destElement, fieldValues(destElement), LRMapping.init(), context);

                //add the created element to the dest collection
                ((Map<Object, Object>) destMap).put(mapEntry.getKey(), destElement);
            }
        }
    }

    default void implicitCopyLhsCollectionToRhsCollection(FieldDef srcFieldDef, Object dest, FieldDef destFieldDef, LRContext context) throws Throwable {
        Class<?> collectionElementType = destFieldDef.getType();

        Object srcCollection = srcFieldDef.getValue();
        if (srcCollection != null) {

            Object destCollection = getEmbeddedValue(dest, destFieldDef.getName(), collectionElementType);
            if (destCollection == null) {
                destCollection = createCollectionField(collectionElementType);
                setFieldValue(dest, destFieldDef.getName(), collectionElementType, destCollection);
            }

            for (Object srcCollectionElement : ((Collection<?>) srcCollection)) {
                Class<?> destElementType = srcCollectionElement.getClass();
                Object destCollectionElement = destElementType.getConstructor().newInstance();
                merge(srcCollectionElement, fieldValues(srcCollectionElement), destCollectionElement, fieldValues(destCollectionElement), LRMapping.init(), context);

                //add the created element to the dest collection
                ((Collection<Object>) destCollection).add(destCollectionElement);
            }
        }
    }

    default void explicitCopyLhsMapValuesToRhsArray(FieldDef srcFieldDef, Object dest, String destFieldName, Class<?> destFieldType, LRMapping mapping, LRContext context) throws Throwable {
        Map<?, ?> srcDictionary = (Map<?, ?>) srcFieldDef.getValue();
        if (srcDictionary != null) {
            Class<?> arrayElementType = destFieldType.componentType();
            Object destArray = getEmbeddedValue(dest, destFieldName, destFieldType);
            if (destArray == null) {
                destArray = Array.newInstance(arrayElementType, srcDictionary.size());
                setFieldValue(dest, destFieldName, destFieldType, destArray);
            }

            int i = 0;
            for (Object sourceElement : srcDictionary.values()) {
                Class<?> destElementType = arrayElementType.isInterface() ? sourceElement.getClass() : arrayElementType;
                Object destElement = destElementType.getConstructor().newInstance();
                merge(sourceElement, fieldValues(sourceElement), destElement, fieldValues(destElement), LRMerge.getNestedOrDefault(destFieldName, mapping), context);

                // add created element to the destination array
                Array.set(destArray, i++, destElement);
            }
        }
    }

    default void explicitCopyLhsMapValuesToRhsCollection(FieldDef srcFieldDef, String srcFieldName, Object dest, FieldDef destFieldDef, String destFieldName, LRContext context, LRMapping mapping) throws Throwable {
        Map<?, ?> srcDictionary = (Map<?, ?>) srcFieldDef.getValue();
        if (srcDictionary != null) {

            Class<?> destCollectionType = destFieldDef.getType();
            Object destCollection = destFieldDef.getValue();
            if (destCollection == null) {
                destCollection = createCollectionField(destCollectionType);
                setFieldValue(dest, destFieldName, destCollectionType, destCollection);
            }

            for (Object collectionElement : srcDictionary.values()) {
                Class<?> destElementType = getMappedElementType(srcFieldName, mapping, collectionElement);

                Object destElement = destElementType.getConstructor().newInstance();
                merge(collectionElement, fieldValues(collectionElement), destElement, fieldValues(destElement), LRMerge.getNestedOrDefault(destFieldName, mapping), context);

                // add created element to the destination collection
                ((Collection) destCollection).add(destElement);
            }
        }
    }

    default void explicitCopyLhsMapEntriesToRhsMapEntries(FieldDef srcFieldDef, Object dest, String destFieldName, Class<?> destFieldType, LRContext context, LRMapping mapping) throws Throwable {
        Map<Object, Object> destDictionary = (Map) getEmbeddedValue(dest, destFieldName, destFieldType);
        if (destDictionary == null) {
            destDictionary = (Map) createDictionaryField(destFieldType);
            setFieldValue(dest, destFieldName, destFieldType, destDictionary);
        }

        Map<?, ?> srcDictionary = (Map<?, ?>) srcFieldDef.getValue();
        Class<?> destElementType = Optional.ofNullable(mapping.get(destFieldName)).map(o -> o.collectionType).orElse(null);
        for (Map.Entry<?, ?> srcEntry : srcDictionary.entrySet()) {
            Object key = srcEntry.getKey();
            if (!isValidMapKey(key)) {
                throw new RuntimeException("Invalid map key type. Expecting only String or Boxed Primitive types");
            }

            Object sourceElement = srcEntry.getValue();
            Class<?> destValueType = (destElementType == null || destElementType.isInterface()) ? sourceElement.getClass() : destElementType;
            Object destElement = destValueType.getConstructor().newInstance();
            merge(sourceElement, fieldValues(sourceElement), destElement, fieldValues(destElement), LRMerge.getNestedOrDefault(destFieldName, mapping), context);

            // add created element to the destination map
            destDictionary.put(key, destElement);
        }
    }

    default void explicitCopyLhsArrayToRhsMap(FieldDef srcFieldDef, String srcFieldName, Object dest, FieldDef destFieldDef, String destFieldName, LRMapping mapping, LRContext context) throws Throwable {
        Class<?> dictionaryType = destFieldDef.getType();

        // start mapping from the source (lhs)
        Object arrayValue = srcFieldDef.getValue();
        if (arrayValue != null) {

            // create dest map
            Map<Object, Object> destDictionary = (Map<Object, Object>) getEmbeddedValue(dest, destFieldName, dictionaryType);
            if (destDictionary == null) {
                destDictionary = (Map<Object, Object>) createDictionaryField(dictionaryType);
                setFieldValue(dest, destFieldName, dictionaryType, destDictionary);
            }

            // loop over source elements
            int length = Array.getLength(arrayValue);
            for (int i = 0; i < length; i++) {
                Object srcElement = Array.get(arrayValue, i);
                // infer a suitable class type for 'destination element'
                Class<?> destElementType = getMappedElementType(srcFieldName, mapping, srcElement);

                // proceed with mapping
                Object destElement = destElementType.getConstructor().newInstance();
                Map<String, FieldDef> srcFieldsMap = fieldValues(srcElement);

                // get dest map key
                Object keyValue = extractMapKey(srcElement, srcFieldsMap, srcFieldName, mapping);

                // merge into map element
                merge(srcElement, srcFieldsMap, destElement, fieldValues(destElement), getNestedOrDefault(destFieldName, mapping), context);

                //add dest element to dest map
                destDictionary.put(keyValue, destElement);
            }
        }
    }

    default void explicitCopyLhsCollectionToRhsMap(FieldDef srcFieldDef, String srcFieldName, Object dest, FieldDef destFieldDef, String destFieldName, LRMapping mapping, LRContext context) throws Throwable {
        Class<?> dictionaryType = destFieldDef.getType();

        // start mapping from the source (lhs)
        Collection<?> srcCollectionValue = (Collection<?>) srcFieldDef.getValue();
        if (srcCollectionValue != null) {
            // create dest map
            Map<Object, Object> destDictionary = (Map) destFieldDef.getValue();
            if (destDictionary == null) {
                destDictionary = (Map<Object, Object>) createDictionaryField(dictionaryType);
                setFieldValue(dest, destFieldName, dictionaryType, destDictionary);
            }

            // loop over source elements
            for (Object srcElement : srcCollectionValue) {
                // last minute effort to infer a suitable class type for 'destination element'
                Class<?> destElementType = getMappedElementType(srcFieldName, mapping, srcElement);

                // proceed with mapping
                Object destElement = destElementType.getConstructor().newInstance();
                Map<String, FieldDef> srcFieldsMap = fieldValues(srcElement);

                // get dest map key
                Object keyValue = extractMapKey(srcElement, srcFieldsMap, srcFieldName, mapping);

                // merge into map element
                merge(srcElement, srcFieldsMap, destElement, fieldValues(destElement), getNestedOrDefault(destFieldName, mapping), context);

                //add dest element to dest map
                destDictionary.put(keyValue, destElement);
            }
        }
    }

    default void explicitCopyLhsArrayToRhsArray(FieldDef srcFieldDef, String srcFieldName, Object dest, FieldDef destFieldDef, String destFieldName, LRMapping mapping, LRContext context) throws Throwable {
        Object srcArrayValue = srcFieldDef.getValue();

        if (srcArrayValue != null) {
            Class<?> destArrayType = destFieldDef.getType();
            Object destArrayValue = getEmbeddedValue(dest, destFieldName, destArrayType);

            int arrayLength = Array.getLength(srcArrayValue);
            if (destArrayValue == null) {
                destArrayValue = Array.newInstance(destFieldDef.getType().componentType(), arrayLength);
                setFieldValue(dest, destFieldName, destArrayType, destArrayValue);
            }

            for (int i = 0; i < arrayLength; i++) {
                Object collectionElement = Array.get(srcArrayValue, i);
                // last minute effort to infer a suitable class type for 'destination element'
                Class<?> destElementType = destArrayType.componentType();
                if (destElementType.isInterface()) {
                    System.out.printf("The 'destination element type' is not a concrete type. Last minute match will try to use '%s' type\n", collectionElement.getClass());
                    destElementType = collectionElement.getClass();
                }

                // proceed with merging
                Object arrayElement = destElementType.getConstructor().newInstance();
                merge(collectionElement, fieldValues(collectionElement), arrayElement, fieldValues(arrayElement),
                        LRMerge.getNestedOrDefault(srcFieldName, mapping), context);
                Array.set(destArrayValue, i, arrayElement);
            }
        }
    }

    default void explicitCopyLhsCollectionToRhsArray(FieldDef srcFieldDef, String srcFieldName, Object dest, FieldDef destFieldDef, String destFieldName, LRMapping mapping, LRContext context) throws Throwable {
        Collection<?> srcCollectionValue = (Collection<?>) srcFieldDef.getValue();

        if (srcCollectionValue != null) {
            Class<?> destArrayType = destFieldDef.getType();
            Object destArrayValue = getEmbeddedValue(dest, destFieldName, destArrayType);

            if (destArrayValue == null) {
                destArrayValue = Array.newInstance(destFieldDef.getType().componentType(), srcCollectionValue.size());
                setFieldValue(dest, destFieldName, destArrayType, destArrayValue);
            }

            int i = 0;
            for (Object collectionElement : srcCollectionValue) {
                // last minute effort to infer a suitable class type for 'destination element'
                Class<?> destElementType = getMappedElementType(srcFieldName, mapping, collectionElement);

                // proceed with merging
                Object arrayElement = destElementType.getConstructor().newInstance();
                merge(collectionElement, fieldValues(collectionElement), arrayElement, fieldValues(arrayElement),
                        LRMerge.getNestedOrDefault(srcFieldName, mapping), context);
                Array.set(destArrayValue, i++, arrayElement);
            }
        }
    }

    default void explicitCopyLhsCollectionToRhsCollection(FieldDef srcFieldDef, String srcFieldName, Object dest, FieldDef destFieldDef, String destFieldName, LRMapping mapping, LRContext context) throws Throwable {
        Collection<?> sourceCollection = (Collection<?>) srcFieldDef.getValue();

        Class<?> destCollectionType = destFieldDef.getType();
        Object destCollection = createCollectionField(destCollectionType);

        for (Object sourceElement : sourceCollection) {
            // last minute effort to infer a suitable class type for 'destination element'
            Class<?> destElementType = getMappedElementType(srcFieldName, mapping, sourceElement);

            //proceed with merging
            Object destElement = destElementType.getConstructor().newInstance();
            merge(sourceElement, fieldValues(sourceElement), destElement, fieldValues(destElement), LRMerge.getNestedOrDefault(srcFieldName, mapping), context);
            ((Collection<Object>) destCollection).add(destElement);
        }
        setFieldValue(dest, destFieldName, destFieldDef.getType(), destCollection);
    }

    default void explicitFlattenLhsSingleElementArrayIntoRhsDestination(FieldDef srcFieldDef, Object dest, Map<String, FieldDef> destDef, String destFieldName, LRMapping mapping, LRContext context) throws Throwable {
        LRMapping nestedMapping = getNestedOrDefault(destFieldName, mapping);
        Object srcArrayValue = srcFieldDef.getValue();

        Object arrayElement = Array.get(srcArrayValue, 0);
        merge(arrayElement, fieldValues(arrayElement), dest, destDef, nestedMapping, context);
    }

    default void explicitFlattenLhsSingleElementCollectionIntoRhsDestination(FieldDef srcFieldDef, String srcFieldName, Object dest, Map<String, FieldDef> destDef, String destFieldName, LRMapping mapping, LRContext context) throws Throwable {
        LRMapping nestedMapping = getNestedOrDefault(destFieldName, mapping);
        Class<?> srcNestedClassType = mapping.get(destFieldName).collectionType;
        Collection<?> nestedCollectionValue = (Collection<?>) srcFieldDef.getValue();

        Object srcCollectionElement = null;
        if (!nestedCollectionValue.isEmpty()) {
            for (Object o : nestedCollectionValue) {
                srcCollectionElement = o;
                break;
            }
        } else {
            srcCollectionElement = srcNestedClassType.getConstructor().newInstance();
            addCollectionValue(srcFieldName, Collection.class, dest, srcCollectionElement);
        }

        merge(srcCollectionElement, fieldValues(srcCollectionElement), dest, destDef, nestedMapping, context);
    }

    default void explicitFlattenLhsEmbeddedIntoRhsDestination(Object src, FieldDef srcFieldDef, String srcFieldName, Object dest, Map<String, FieldDef> destDef, String destFieldName, LRMapping mapping, LRContext context) throws Throwable {
        LRMapping nestedMapping = getNestedOrDefault(destFieldName, mapping);
        ClassDef nestedDef = (ClassDef) srcFieldDef.getValue();
        Class<?> srcNestedClassType = nestedDef.getType();

        Object nestedSrcValue = getEmbeddedValue(src, srcFieldName, srcNestedClassType);
        merge(nestedSrcValue, fieldValues(nestedSrcValue), dest, destDef, nestedMapping, context);
    }

    default void explicitCopyLhsParentToRhsSingleElementCollection(Object src, FieldDef srcFieldDef, Object dest, FieldDef destFieldDef, String destFieldName, Class<?> destElementType, LRMapping nestedMapping, LRContext context) throws Throwable {
        Class<?> collectionType = destFieldDef.getType();
        Collection<?> destCollection = (Collection<?>) getEmbeddedValue(dest, destFieldName, collectionType);

        if (destCollection == null) {
            destCollection = (Collection<?>) createCollectionField(collectionType);
            setFieldValue(dest, destFieldName, collectionType, destCollection);
        }

        Object collectionElement = null;
        if (!destCollection.isEmpty()) {
            for (Object o : destCollection) {
                collectionElement = o;
                break;
            }
        } else {
            collectionElement = destElementType.getConstructor().newInstance();
            addCollectionValue(destFieldName, collectionType, dest, collectionElement);
        }

        Map<String, FieldDef> subSet = wrapSourceFieldDef(srcFieldDef);
        merge(src, subSet, collectionElement, fieldValues(collectionElement), nestedMapping, context);
    }

    default void explicitCopyLhsParentToRhsSingleElementArray(Object src, FieldDef srcFieldDef, Object dest, FieldDef destFieldDef, String destFieldName, LRMapping nestedMapping, LRContext context) throws Throwable {
        Class<?> destArrayType = destFieldDef.getType();
        Object destArrayValue = getEmbeddedValue(dest, destFieldName, destArrayType);

        if (destArrayValue == null) {
            destArrayValue = Array.newInstance(destFieldDef.getType().componentType(), 1);
            setFieldValue(dest, destFieldName, destArrayType, destArrayValue);
            //Add single array element
            Object singleElement = destArrayType.componentType().getConstructor().newInstance();
            Array.set(destArrayValue, 0, singleElement);
        }

        Object arrayElement = Array.get(destArrayValue, 0);

        Map<String, FieldDef> subFieldDef = wrapSourceFieldDef(srcFieldDef);
        merge(src, subFieldDef, arrayElement, fieldValues(arrayElement), nestedMapping, context);
    }

    default void explicitCopyLhsParentToRhsEmbedded(Object src, FieldDef srcFieldDef, Object dest, FieldDef destFieldDef, String destFieldName, LRMapping nestedMapping, LRContext context) throws Throwable {
        ClassDef embeddedDestDef = (ClassDef) destFieldDef.getValue();
        Class<?> embeddedDestType = embeddedDestDef.getType();
        Object embeddedDestValue = getEmbeddedValue(dest, destFieldName, embeddedDestType);

        if (embeddedDestValue == null) {
            embeddedDestValue = embeddedDestType.getConstructor().newInstance();
            setFieldValue(dest, destFieldName, embeddedDestType, embeddedDestValue);
        }

        Map<String, FieldDef> singleFieldDef = wrapSourceFieldDef(srcFieldDef);
        merge(src, singleFieldDef, embeddedDestValue, embeddedDestDef.getFields(), nestedMapping, context);
    }

    default void implicitCopyLhsArrayToRhsArray(FieldDef srcFieldDef, Class<?> componentType, Object destArray, int containerSize, LRContext context) throws Throwable {
        for (int i = 0; i < containerSize; i++) {
            Object nestedSrcValue = Array.get(srcFieldDef.getValue(), i);
            if (nestedSrcValue != null) {
                Class<?> destElementType = componentType.isInterface() ? nestedSrcValue.getClass() : componentType;
                Object nestedDestValue = destElementType.getConstructor().newInstance();
                merge(nestedSrcValue, fieldValues(nestedSrcValue), nestedDestValue, fieldValues(nestedDestValue),
                        LRMapping.init(), context);
                Array.set(destArray, i, nestedDestValue);
            }
        }
    }

    default void implicitCopyLhsCollectionToRhsArray(FieldDef srcFieldDef, Class<?> componentType, Object destArray, LRContext context) throws Throwable {
        int i = 0;
        for (Object nestedSrcValue : (Collection<?>) srcFieldDef.getValue()) {
            Object nestedDestValue = componentType.getConstructor().newInstance();
            if (nestedSrcValue != null) {
                merge(nestedSrcValue, fieldValues(nestedSrcValue), nestedDestValue, fieldValues(nestedDestValue),
                        LRMapping.init(), context);
                Array.set(destArray, i++, nestedDestValue);
            }
        }
    }

    default void implicitCopyLhsEmbeddedToRhsEmbedded(Object src, FieldDef srcFieldDef, Object dest, FieldDef destFieldDef, String srcField, LRContext context) throws Throwable {
        ClassDef srcEmbeddedDef = (ClassDef) srcFieldDef.getValue();
        Class<?> srcEmbeddedType = srcEmbeddedDef.getType();
        Object embeddedSrcValue = getEmbeddedValue(src, srcField, srcEmbeddedType);

        ClassDef destEmbeddedDef = (ClassDef) destFieldDef.getValue();
        Class<?> destEmbeddedType = Objects.requireNonNullElse(destEmbeddedDef.getType(), srcEmbeddedType);
        Object embeddedDestValue = destEmbeddedType.getConstructor().newInstance();
        if (embeddedSrcValue != null) {
            merge(embeddedSrcValue, fieldValues(embeddedSrcValue), embeddedDestValue, destEmbeddedDef.getFields(), LRMapping.init(), context);
            setFieldValue(dest, destFieldDef.getName(), destEmbeddedType, embeddedDestValue);
        }
    }
}
