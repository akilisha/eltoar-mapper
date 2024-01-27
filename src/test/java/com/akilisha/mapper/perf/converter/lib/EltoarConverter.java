package com.akilisha.mapper.perf.converter.lib;

import com.akilisha.mapper.merge.LRMapping;
import com.akilisha.mapper.perf.converter.Converter;
import com.akilisha.mapper.perf.data.destination.DestinationCode;
import com.akilisha.mapper.perf.data.destination.Order;
import com.akilisha.mapper.perf.data.source.SourceCode;
import com.akilisha.mapper.perf.data.source.SourceOrder;

public class EltoarConverter implements Converter {

    @Override
    public Order convert(SourceOrder sourceOrder) throws Throwable {
        Order dest = new Order();
        LRMapping.init()
                .map("status", "orderStatus")
                .map("deliveryData.isPrePaid", "deliveryData.prePaid")
                .merge(sourceOrder, dest);
        return dest;
    }

    @Override
    public DestinationCode convert(SourceCode sourceCode) throws Throwable {
        DestinationCode dest = new DestinationCode();
        LRMapping.init().merge(sourceCode, dest);
        return dest;
    }
}
