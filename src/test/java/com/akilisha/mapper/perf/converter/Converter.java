package com.akilisha.mapper.perf.converter;

import com.akilisha.mapper.perf.data.destination.DestinationCode;
import com.akilisha.mapper.perf.data.destination.Order;
import com.akilisha.mapper.perf.data.source.SourceCode;
import com.akilisha.mapper.perf.data.source.SourceOrder;

public interface Converter {
    Order convert(SourceOrder sourceOrder) throws Throwable;

    DestinationCode convert(SourceCode sourceCode) throws Throwable;

}
