package com.akilisha.mapper.merge;

import lombok.Getter;

import java.util.function.Function;
import java.util.function.Supplier;

@Getter
public class LRPathway<R> {

    final String fieldName;
    final Class<?> collectionType;
    final Function<Object, R> converter;
    final String keyField;
    final Supplier<?> keySupplier;
    final LRMapping nestedMapping;

    public LRPathway(String fieldName) {
        this(fieldName, null, null, null, null, null);
    }

    public LRPathway(String fieldName, Function<Object, R> converter) {
        this(fieldName, null, converter);
    }

    public LRPathway(String fieldName, Class<?> collectionType, Function<Object, R> converter) {
        this(fieldName, collectionType, converter, null, null, null);
    }

    public LRPathway(String fieldName, Class<?> collectionType, Function<Object, R> converter, String keyField, LRMapping nestedMapping) {
        this(fieldName, collectionType, converter, keyField, null, nestedMapping);
    }

    public LRPathway(String fieldName, Class<?> collectionType, Function<Object, R> converter, Supplier<?> keySupplier, LRMapping nestedMapping) {
        this(fieldName, collectionType, converter, null, keySupplier, nestedMapping);
    }

    public LRPathway(String fieldName, Class<?> collectionType, Function<Object, R> converter, String keyField, Supplier<?> keySupplier, LRMapping nestedMapping) {
        this.fieldName = fieldName;
        this.collectionType = collectionType;
        this.converter = converter;
        this.keyField = keyField;
        this.keySupplier = keySupplier;
        this.nestedMapping = nestedMapping;
    }
}
