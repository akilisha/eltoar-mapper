package com.akilisha.mapper.merge;

import com.akilisha.mapper.definition.FieldName;
import lombok.Getter;

import java.util.function.Function;
import java.util.function.Supplier;

@Getter
public class LRPathway<R> {

    final FieldName fieldName;
    final Class<?> collectionType;
    final Function<Object, R> converter;
    final String keyField;
    final Supplier<?> keySupplier;
    final LRMapping nestedMapping;

    public LRPathway(String fieldName) {
        this(FieldName.init().name(fieldName));
    }

    public LRPathway(FieldName fieldName) {
        this(fieldName, null, null, null, null, null);
    }

    public LRPathway(String fieldName, Function<Object, R> converter) {
        this(FieldName.init().name(fieldName), converter);
    }

    public LRPathway(FieldName fieldName, Function<Object, R> converter) {
        this(fieldName, null, converter, null, null, null);
    }

    public LRPathway(String fieldName, Class<?> collectionType, Function<Object, R> converter) {
        this(FieldName.init().name(fieldName), collectionType, converter);
    }

    public LRPathway(FieldName fieldName, Class<?> collectionType, Function<Object, R> converter) {
        this(fieldName, collectionType, converter, null, null, null);
    }

    public LRPathway(String fieldName, Class<?> collectionType, Function<Object, R> converter, String keyField, LRMapping nestedMapping) {
        this(FieldName.init().name(fieldName), collectionType, converter, keyField, nestedMapping);
    }

    public LRPathway(FieldName fieldName, Class<?> collectionType, Function<Object, R> converter, String keyField, LRMapping nestedMapping) {
        this(fieldName, collectionType, converter, keyField, null, nestedMapping);
    }

    public LRPathway(String fieldName, Class<?> collectionType, Function<Object, R> converter, Supplier<?> keySupplier, LRMapping nestedMapping) {
        this(FieldName.init().name(fieldName), collectionType, converter, keySupplier, nestedMapping);
    }

    public LRPathway(FieldName fieldName, Class<?> collectionType, Function<Object, R> converter, Supplier<?> keySupplier, LRMapping nestedMapping) {
        this(fieldName, collectionType, converter, null, keySupplier, nestedMapping);
    }

    public LRPathway(String fieldName, Class<?> collectionType, Function<Object, R> converter, String keyField, Supplier<?> keySupplier, LRMapping nestedMapping) {
        this(FieldName.init().name(fieldName), collectionType, converter, keyField, keySupplier, nestedMapping);
    }

    public LRPathway(FieldName fieldName, Class<?> collectionType, Function<Object, R> converter, String keyField, Supplier<?> keySupplier, LRMapping nestedMapping) {
        this.fieldName = fieldName;
        this.collectionType = collectionType;
        this.converter = converter;
        this.keyField = keyField;
        this.keySupplier = keySupplier;
        this.nestedMapping = nestedMapping;
    }
}
