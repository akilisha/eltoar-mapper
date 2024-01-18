package com.akilisha.mapper.definition;

import lombok.Getter;
import lombok.Setter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.akilisha.mapper.definition.ClassDefCache.createAndCacheClassDef;
import static org.objectweb.asm.Opcodes.ASM9;


@Getter
@Setter
public class ClassDef extends ClassVisitor {

    final Class<?> type;
    final Map<String, Object> fields = new HashMap<>();

    public ClassDef(Class<?> type) {
        super(ASM9);
        this.type = type;
    }

    public static boolean isJavaType(Class<?> type) {
        return (type.isPrimitive() && type != void.class) ||
                Collection.class.isAssignableFrom(type) || //case where a class may be extending one of the collection interfaces
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

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (superName != null && Set.of("java/", "javax/", "sun/", "com/sun/").stream().noneMatch(superName::startsWith)) {
            try {
                Class<?> superClass = Class.forName(superName.replace("/", "."));
                ClassDef def = createAndCacheClassDef(superClass);
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
        this.fields.put(name, fieldType);
        if (!(isJavaType(fieldType) || fieldType.isArray() || fieldType.isEnum() || fieldType.isInterface() || fieldType.isHidden())) {
            ClassDef def = createAndCacheClassDef(fieldType);
            this.fields.put(name, def);
        }

        return super.visitField(access, name, descriptor, signature, value);
    }
}
