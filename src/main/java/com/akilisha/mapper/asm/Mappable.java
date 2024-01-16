package com.akilisha.mapper.asm;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public interface Mappable {

    MethodHandles.Lookup lookup = MethodHandles.lookup();

    default Object getThisTarget() {
        return this;
    }

    default Object get(String fieldName, Class<?> fieldType) throws Throwable {
        MethodType getterMethodType = MethodType.methodType(fieldType);
        String getter = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        MethodHandle getterHandler = lookup.findVirtual(getClass(), getter, getterMethodType);
        return getterHandler.invoke(getThisTarget());
    }
}
