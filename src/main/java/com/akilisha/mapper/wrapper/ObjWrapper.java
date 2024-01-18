package com.akilisha.mapper.wrapper;

import com.akilisha.mapper.definition.MapperDest;
import com.akilisha.mapper.definition.MapperSrc;

public class ObjWrapper<T> implements MapperDest, MapperSrc {

    final T target;

    public ObjWrapper(T target) {
        this.target = target;
    }

    @Override
    public T getThisTarget() {
        return this.target;
    }
}
