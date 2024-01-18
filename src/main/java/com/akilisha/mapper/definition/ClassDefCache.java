package com.akilisha.mapper.definition;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ClassDefCache {

    public static SoftReference<Map<Class<?>, ClassDef>> cache = new SoftReference<>(new HashMap<>());

    private ClassDefCache() {
    }

    public static void cache(Class<?> target, ClassDef def) {
        Objects.requireNonNull(cache.get()).put(target, def);
    }

    public static Optional<ClassDef> get(Class<?> target) {
        return Optional.ofNullable(Objects.requireNonNull(cache.get()).get(target));
    }

    public static ClassDef createAndCacheClassDef(Class<?> superClass) {
        return ClassDefCache.get(superClass).orElseGet(() -> {
            try {
                ClassDef cv1 = new ClassDef(superClass);
                ClassReader cr = new ClassReader(superClass.getName());
                ClassDefCache.cache(superClass, cv1);
                cr.accept(cv1, 0);
                return cv1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
