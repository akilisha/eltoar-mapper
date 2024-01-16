package com.akilisha.mapper.asm;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.ArrayList;

public interface MapperDest extends Mappable {

    default void map(String fieldName, Class<?> fieldType, Object fieldValue) throws Throwable {
        MethodType setterMethodType = MethodType.methodType(void.class, fieldType);
        String setter = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        MethodHandle setterHandler = lookup.findVirtual(getClass(), setter, setterMethodType);
        setterHandler.invoke(getThisTarget(), fieldValue);
    }

    default void add(String fieldName, Class<?> fieldType, Object listValue) throws Throwable {
        Object listFieldValue = get(fieldName, fieldType);

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
