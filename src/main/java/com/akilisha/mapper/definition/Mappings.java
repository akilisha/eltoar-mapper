package com.akilisha.mapper.definition;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Mappings {

    public static MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();

    public static void mapAToB(MapperSrc source, ClassDef srcDef, MapperDest destination, ClassDef destDef, Mapping mapping) throws Throwable {
        if (source != null && destination != null) {
            //iterate over dest fields
            for (String destField : destDef.getFields().keySet()) {
                Object destType = destDef.getFields().get(destField);
                //identify source field
                String srcField = Optional.ofNullable(mapping.get(destField)).map(f -> f.fieldName).orElse(destField);
                Object srcType = srcDef.getFields().get(srcField);

                if (ClassDef.class != destType.getClass() && (List.class.isAssignableFrom((Class<?>) destType) || Set.class.isAssignableFrom((Class<?>) destType))) {
                    if (srcType != null) {
                        assert List.class.isAssignableFrom((Class<?>) srcType); // this line MUST be true as well
                        Mapping nestedMapping = mapping.get(destField).nestedMapping;
                        List<MapperSrc> srcListValues = (List) source.get(srcField, (Class<?>) srcType);
                        if (srcListValues != null) {
                            for (MapperSrc src : srcListValues) {
                                MapperDest dest = newInstance(mapping.get(destField).destCollectionType);
                                nestedMapping.commit(src, dest);
                                destination.add(destField, (Class<?>) destType, dest);
                            }
                        }
                    } else {
                        Mapping nestedMapping = extractNestedDestMapping(mapping, destField);
                        mapValuesToListItem(source, srcDef, destination, destField, (Class<?>) destType, nestedMapping);
                    }
                    continue;
                }

                if (destType.equals(srcType)) {
                    assert srcType instanceof Class<?>;
                    if (destField.equals(srcField)) {
                        mapSrcToDestField((Class<?>) srcType, srcField, source, destField, destination, mapping);
                    } else {
                        assert destType instanceof Class<?>;
                        mapSrcToDestField((Class<?>) srcType, srcField, source, (Class<?>) destType, destField, destination, mapping);
                    }
                    continue;
                }

                if (destType.getClass() == ClassDef.class) {
                    ClassDef nestedDef = (ClassDef) destType;
                    if (List.class.isAssignableFrom(nestedDef.getType()) || Set.class.isAssignableFrom(nestedDef.getType())) {
                        Mapping nestedMapping = extractNestedDestMapping(mapping, destField);
                        mapValuesToListItem(source, srcDef, destination, destField, nestedDef.getType(), nestedMapping);
                    } else {
                        Mapping nestedMapping = extractNestedDestMapping(mapping, destField);
                        MapperDest nestedValue = newInstance(nestedDef.getType());
                        mapAToB(source, srcDef, nestedValue, nestedDef, nestedMapping);
                        destination.map(destField, nestedDef.getType(), nestedValue);
                    }
                    continue;
                }

                if (srcType == null && srcField.contains(".")) {
                    String mappedSrcField = mapping.get(destField).fieldName;
                    if (mappedSrcField.contains(".")) {
                        String innerSourceField = mappedSrcField.substring(0, mappedSrcField.indexOf("."));
                        Class<?> innerSourceType = ((ClassDef) srcDef.getFields().get(innerSourceField)).getType();
                        MapperSrc innerSource = (MapperSrc) source.get(innerSourceField, innerSourceType);
                        Mapping innerSourceMapping = extractNestedSrcMapping(mapping, innerSourceField);
                        mapSrcToDestField(innerSource, classDef(innerSourceType), destination, innerSourceMapping, destField, (Class<?>) destDef.getFields().get(destField));
                    }
                    continue;
                }

                // last effort if nothing else matches
                Class<?> toClass = (Class<?>) destType;
                mapSrcToDestField(source, srcDef, destination, mapping, destField, toClass);
            }
        }
    }

    public static void mapSrcToDestField(Class<?> fieldType, String fromField, MapperSrc source, String destField, MapperDest destination, Mapping mapping) {
        mapSrcToDestField(fieldType, fromField, source, fieldType, destField, destination, mapping);
    }

    public static void mapSrcToDestField(Class<?> srcType, String fromField, MapperSrc source, Class<?> destType, String destField, MapperDest destination, Mapping mapping) {
        try {
            source.accept(fromField, srcType, destField, destType, destination, mapping);
        } catch (Throwable e) {
            // throwing an exception here is not necessary
            // since some fields may have been intentionally skipped from the mapping
            System.err.printf("Ignoring exception. %s\n", e.getMessage());
        }
    }

    public static void mapSrcToDestField(MapperSrc source, ClassDef fromDef, MapperDest destination, Mapping mapping, String destField, Class<?> toClass) {
        Converter<?> fromField = mapping.getOrDefault(destField, null);
        Class<?> fromClass = fromField != null ? (Class<?>) fromDef.getFields().get(fromField.fieldName) : toClass;
        try {
            source.accept(fromField != null ? fromField.fieldName : destField, fromClass, destField, toClass, destination, mapping);
        } catch (Throwable e) {
            // throwing an exception here is not necessary
            // since some fields may have been intentionally skipped from the mapping
            System.err.printf("Ignoring exception. %s\n", e.getMessage());
        }
    }

    public static void mapValuesToListItem(MapperSrc source, ClassDef fromDef, MapperDest destination, String name, Class<?> toClass, Mapping nestedMapping) throws Throwable {
        Converter<?> converter = nestedMapping.values().toArray(Converter[]::new)[0]; // any entry from nestedMapping
        MapperDest collectionTypeValue = newInstance(converter.destCollectionType);
        ClassDef collectionTypeDef = classDef(converter.destCollectionType);
        mapAToB(source, fromDef, collectionTypeValue, collectionTypeDef, nestedMapping);
        destination.add(name, toClass, collectionTypeValue);
    }

    public static Mapping extractNestedDestMapping(Mapping mapping, String name) {
        Map<String, Converter<?>> mapValues = mapping.entrySet().stream()
                .filter(e -> e.getKey().startsWith(name))
                .collect(Collectors.toMap(
                        e -> e.getKey().substring(e.getKey().indexOf(".") + 1),
                        Map.Entry::getValue));
        return Mapping.init(mapValues);
    }

    public static Mapping extractNestedSrcMapping(Mapping mapping, String name) {
        Map<String, Converter<?>> mapValues = mapping.entrySet().stream()
                .filter(e -> e.getValue().fieldName.startsWith(name))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new Converter<>(e.getValue().fieldName.substring(e.getValue().fieldName.indexOf(".") + 1),
                                e.getValue().srcCollectionType,
                                e.getValue().destCollectionType,
                                e.getValue().eval,
                                e.getValue().nestedMapping
                        )));
        return Mapping.init(mapValues);
    }

    public static MapperDest newInstance(Class<?> type) throws Throwable {
        MethodType mt = MethodType.methodType(void.class);
        MethodHandle newType = publicLookup.findConstructor(type, mt);
        return (MapperDest) newType.invoke();
    }

    public static ClassDef classDef(Class<?> type) throws IOException {
        ClassDef def = new ClassDef(type);
        ClassReader cr = new ClassReader(type.getName());
        cr.accept(def, 0);
        return def;
    }
}
