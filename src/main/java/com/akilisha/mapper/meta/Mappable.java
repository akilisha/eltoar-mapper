package com.akilisha.mapper.meta;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;

public interface Mappable {

    MethodHandles.Lookup lookup = MethodHandles.lookup();

    default void map(String fieldName, Class<?> fieldType, Object fieldValue) throws Throwable {
        MethodType setterMethodType = MethodType.methodType(void.class, fieldType);
        String setter = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        MethodHandle setterHandler = lookup.findVirtual(getClass(), setter, setterMethodType);
        setterHandler.invoke(this, fieldValue);
    }

    default void add(String fieldName, Class<?> fieldType, Object listValue) throws Throwable {
        MethodType getterMethodType = MethodType.methodType(fieldType);
        String getter = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        MethodHandle getterHandler = lookup.findVirtual(getClass(), getter, getterMethodType);
        Object listFieldValue = getterHandler.invoke(this);

        if (listFieldValue == null) {
            // manually creating ArrayList and setting as the field value since the field is currently NULL
            listFieldValue = new ArrayList<>();
            map(fieldName, fieldType, listFieldValue);
        }

        MethodType listMethodType = MethodType.methodType(boolean.class, Object.class);
        MethodHandle addHandler = lookup.findVirtual(listFieldValue.getClass(), "add", listMethodType);
        addHandler.invoke(listFieldValue, listValue);
    }
}
