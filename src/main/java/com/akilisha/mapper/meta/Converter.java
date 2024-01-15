package com.akilisha.mapper.meta;

import lombok.Getter;

import java.util.function.Function;

@Getter
public class Converter<R> {

    final String fieldName;
    final Class<?> collectionType;
    final Function<Object, R> eval;

    public Converter(String fieldName) {
        this(fieldName, null, null);
    }

    public Converter(String fieldName, Function<Object, R> eval) {
        this(fieldName, null, eval);
    }

    public Converter(String fieldName, Class<?> collectionType) {
        this(fieldName, collectionType, null);
    }

    public Converter(String fieldName, Class<?> collectionType, Function<Object, R> eval) {
        this.fieldName = fieldName;
        this.collectionType = collectionType;
        this.eval = eval;
    }
}
