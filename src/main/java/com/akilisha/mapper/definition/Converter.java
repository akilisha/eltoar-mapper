package com.akilisha.mapper.definition;

import lombok.Getter;

import java.util.function.Function;

@Getter
public class Converter<R> {

    final String fieldName;
    final Class<?> srcCollectionType;
    final Class<?> destCollectionType;
    final Function<Object, R> eval;
    final Mapping nestedMapping;

    public Converter(String fieldName) {
        this(fieldName, null, null, null, null);
    }

    public Converter(String fieldName, Function<Object, R> eval) {
        this(fieldName, null, null, eval);
    }

    public Converter(String fieldName, Class<?> srcCollectionType, Class<?> destCollectionType, Function<Object, R> eval) {
        this(fieldName, srcCollectionType, destCollectionType, eval, null);
    }

    public Converter(String fieldName, Class<?> srcCollectionType, Class<?> destCollectionType, Function<Object, R> eval, Mapping nestedMapping) {
        this.fieldName = fieldName;
        this.destCollectionType = destCollectionType;
        this.srcCollectionType = srcCollectionType;
        this.eval = eval;
        this.nestedMapping = nestedMapping;
    }
}
