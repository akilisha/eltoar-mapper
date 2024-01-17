package com.akilisha.mapper.wrapper;

import com.akilisha.mapper.definition.MapperDest;
import com.akilisha.mapper.definition.MapperSrc;

public class DataWrapper<T> implements MapperDest, MapperSrc {

    final T target;

    public DataWrapper(T target) {
        this.target = target;
    }

    @Override
    public T getThisTarget() {
        return this.target;
    }
}
