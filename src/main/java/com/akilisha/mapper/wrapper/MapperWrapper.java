package com.akilisha.mapper.wrapper;

import com.akilisha.mapper.asm.MapperDest;
import com.akilisha.mapper.asm.MapperSrc;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapperWrapper<T> implements MapperDest, MapperSrc {

    final T target;

    @Override
    public T getThisTarget() {
        return this.target;
    }
}
