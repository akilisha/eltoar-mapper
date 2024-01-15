package com.akilisha.mapper.meta;

import com.akilisha.mapper.asm.ClassDef;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public interface MVisitable {

    MethodHandles.Lookup lookup = MethodHandles.lookup();

    default void accept(String fromField, Class<?> fromType, String fieldName, Class<?> fieldType, Mappable target, Mapping mapping) throws Throwable {
        MethodType getterMethodType = MethodType.methodType(fromType);
        String getter = "get" + Character.toUpperCase(fromField.charAt(0)) + fromField.substring(1);
        if (ClassDef.isJavaType(fieldType)) {
            MethodHandle getterHandler = lookup.findVirtual(getClass(), getter, getterMethodType);
            Object fieldValue = getterHandler.invoke(this);
            if (fromType.equals(fieldType)) {
                //map value now
                target.map(fieldName, fieldType, fieldValue);
            } else {
                Converter<?> converter = mapping.get(fieldName);
                Object convertedValue = converter.getEval().apply(fieldValue);
                target.map(fieldName, fieldType, convertedValue);
            }

        } else {
            throw new RuntimeException("Not yet handled");
        }
    }
}
