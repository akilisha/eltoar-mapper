package com.akilisha.mapper.meta;

import com.akilisha.mapper.asm.ClassDef;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Mappings {

    public static MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();

    public static void mapAToB(MVisitable visitable, ClassDef fromDef, Mappable mappable, ClassDef toDef, Mapping mapping) throws Throwable {
        if (visitable != null && mappable != null) {
            for (String name : toDef.getFields().keySet()) {
                Object targetType = toDef.getFields().get(name);

                if (targetType.getClass() == Class.class) {
                    Class<?> toClass = (Class<?>) targetType;
                    Converter<?> fromField = mapping.getOrDefault(name, null);
                    Class<?> fromClass = fromField != null ? (Class<?>) fromDef.getFields().get(fromField.fieldName) : toClass;
                    try {
                        visitable.accept(fromField != null ? fromField.fieldName : name, fromClass, name, toClass, mappable, mapping);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Map<String, Converter<?>> mapValues = mapping.entrySet().stream()
                            .filter(e -> e.getKey().startsWith(name))
                            .collect(Collectors.toMap(
                                    e -> e.getKey().substring(e.getKey().indexOf(".") + 1),
                                    Map.Entry::getValue));
                    Mapping nestedMapping = Mapping.init(mapValues);
                    ClassDef nestedDef = (ClassDef) targetType;
                    if (nestedDef.getType().isAssignableFrom(List.class)) {
                        Converter<?> converter = nestedMapping.values().toArray(Converter[]::new)[0]; // any entry from nestedMapping
                        Mappable collectionTypeValue = newInstance(converter.collectionType);
                        ClassDef collectionTypeDef = classDef(converter.collectionType);
                        mapAToB(visitable, fromDef, collectionTypeValue, collectionTypeDef, nestedMapping);
                        mappable.add(name, nestedDef.getType(), collectionTypeValue);
                    } else {
                        Mappable nestedValue = newInstance(nestedDef.getType());
                        mapAToB(visitable, fromDef, nestedValue, nestedDef, nestedMapping);
                        mappable.map(name, nestedDef.getType(), nestedValue);
                    }
                }
            }
        }
    }

    public static Mappable newInstance(Class<?> type) throws Throwable {
        MethodType mt = MethodType.methodType(void.class);
        MethodHandle newType = publicLookup.findConstructor(type, mt);
        return (Mappable) newType.invoke();
    }

    public static ClassDef classDef(Class<?> type) throws IOException {
        ClassDef def = new ClassDef(type);
        ClassReader cr = new ClassReader(type.getName());
        cr.accept(def, 0);
        return def;
    }
}
