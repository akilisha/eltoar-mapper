package com.akilisha.mapper.merge;

import lombok.Getter;

import java.util.function.Function;

@Getter
public class LRConverter<R> {

    final String fieldName;
    final Class<?> srcCollectionType;
    final Class<?> destCollectionType;
    final Function<Object, R> eval;
    final String keyField;
    final LRMapping nestedMapping;

    public LRConverter(String fieldName) {
        this(fieldName, null, null, null, null, null);
    }

    public LRConverter(String fieldName, Function<Object, R> eval) {
        this(fieldName, null, null, eval);
    }

    public LRConverter(String fieldName, Class<?> srcCollectionType, Class<?> destCollectionType, Function<Object, R> eval) {
        this(fieldName, srcCollectionType, destCollectionType, eval, null, null);
    }

    public LRConverter(String fieldName, Class<?> srcCollectionType, Class<?> destCollectionType, Function<Object, R> eval, String keyField, LRMapping nestedMapping) {
        this.fieldName = fieldName;
        this.destCollectionType = destCollectionType;
        this.srcCollectionType = srcCollectionType;
        this.eval = eval;
        this.keyField = keyField;
        this.nestedMapping = nestedMapping;
    }
}
