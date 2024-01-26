package com.akilisha.mapper.definition;

import lombok.Getter;
import lombok.Setter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.akilisha.mapper.merge.LRMerge.*;
import static org.objectweb.asm.Opcodes.ASM9;


@Getter
@Setter
public class ClassDef extends ClassVisitor {

    final Class<?> type;
    final Map<String, FieldDef> fields = new LinkedHashMap<>();

    public ClassDef(Class<?> type) {
        super(ASM9);
        this.type = type;
    }

    public static boolean isJavaType(Class<?> type) {
        return (type.isPrimitive() && type != void.class) ||
                Collection.class.isAssignableFrom(type) || // case where a type is a subclass of the collection interface
                Map.class.isAssignableFrom(type) || // case where a type is a subclass of the map interface
                Stream.of("java.", "javax.", "sun.", "com.sun.")
                        .anyMatch(t -> type.getName().startsWith(t));
    }

    public static Class<?> detectType(String descriptor) {
        if (descriptor.contains(".")) {
            try {
                return Class.forName(descriptor);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        if (descriptor.startsWith("L")) {
            return detectType(descriptor.substring(1, descriptor.length() - 1).replace("/", "."));
        }

        if (descriptor.startsWith("[")) {
            Class<?> type = detectType(descriptor.substring(1));
            return type.arrayType();
        }

        return switch (descriptor) {
            case "Z" -> boolean.class;
            case "C" -> char.class;
            case "B" -> byte.class;
            case "S" -> short.class;
            case "I" -> int.class;
            case "F" -> float.class;
            case "J" -> long.class;
            case "D" -> double.class;
            default ->
                    throw new RuntimeException(String.format("Cannot determine class type from signature - %s", descriptor));
        };
    }

    public static ClassDef newClassDef(Class<?> target) {
        try {
            System.out.printf("creating new class def for - %s\n", target.getName());
            ClassDef cv1 = new ClassDef(target);
            ClassReader cr = new ClassReader(target.getName());
            cr.accept(cv1, 0);
            return cv1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ClassDef objectDef(Object target) {
        Class<?> targetType = target.getClass();
        if (isMapType(targetType)) {
            ClassDef def = new ClassDef(targetType);
            Map<?, ?> map = (Map<?, ?>) target;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                Class<?> fieldType = value.getClass();
                if (Map.class.isAssignableFrom(fieldType)) {
                    FieldDef field = FieldDef.define(key, ClassDef.class, objectDef(value));
                    def.getFields().put(key, field);
                } else {
                    FieldDef field = FieldDef.define(key, fieldType, value);
                    def.getFields().put(key, field);
                }
            }
            return def;
        } else if (isArrayType(targetType) || isCollectionType(targetType)) {
            throw new RuntimeException("Cannot inspect a list of array for fields. Try to inspect the collection or " +
                    "array elements individually instead");
        } else {
            return ClassDefs.cached.get(targetType);
        }
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (superName != null && Set.of("java/", "javax/", "sun/", "com/sun/").stream().noneMatch(superName::startsWith)) {
            try {
                Class<?> superClass = Class.forName(superName.replace("/", "."));
                ClassDef def = ClassDefs.cached.get(superClass);
                this.fields.putAll(def.fields);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        Class<?> fieldType = detectType(descriptor);

        System.out.printf("access: %d, name: %s, descriptor: %s, signature: %s, type: %s\n", access, name, descriptor, signature, fieldType);
        this.fields.put(name, FieldDef.define(name, fieldType));
        if (!(isJavaType(fieldType) || fieldType.isArray() || fieldType.isEnum() || fieldType.isInterface() || fieldType.isHidden())) {
            ClassDef def = ClassDefs.cached.get(fieldType);
            this.fields.put(name, FieldDef.define(name, ClassDef.class, def));
        }

        return super.visitField(access, name, descriptor, signature, value);
    }
}
