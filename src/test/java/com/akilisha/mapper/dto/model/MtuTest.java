package com.akilisha.mapper.dto.model;

import com.akilisha.mapper.definition.Mapping;
import com.akilisha.mapper.incubator.WrapperLoader;
import com.akilisha.mapper.wrapper.DataWrapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class MtuTest {

    @Test
    public void basic_test_to_copy_between_two_similar_objects_without_generated_wrapper() throws Throwable {
        Mtu entity = new Mtu(123L, "jimna rishi", BigDecimal.valueOf(123456L), true, (short) 123);
        Mtu copy = new Mtu();

        //using static wrapper
        DataWrapper<Mtu> src = new MtuWrap(entity);
        DataWrapper<Mtu> dest = new MtuWrap(copy);

        //copy values
        Mapping.init().commitWrapped(src, dest);

        //assert values
        assertThat(dest.getThisTarget().getId()).isEqualTo(src.getThisTarget().getId());
        assertThat(dest.getThisTarget().getMajina()).isEqualTo(src.getThisTarget().getMajina());
        assertThat(dest.getThisTarget().getMshahara()).isEqualTo(src.getThisTarget().getMshahara());
        assertThat(dest.getThisTarget().isPamoja()).isEqualTo(src.getThisTarget().isPamoja());
        assertThat(dest.getThisTarget().getSiri()).isEqualTo(src.getThisTarget().getSiri());
    }

    @Test
    public void basic_test_to_copy_between_two_similar_objects_using_generated_wrapper() throws Throwable {
        Mtu entity = new Mtu(123L, "jimna rishi", BigDecimal.valueOf(123456L), true, (short) 123);
        Mtu copy = new Mtu();

        //using generate wrapper
        Class<?> wrapper = WrapperLoader.generate(Mtu.class);
//        DataWrapper<Mtu> src = (DataWrapper) wrapper.getConstructor(Mtu.class).newInstance(entity);
//        DataWrapper<Mtu> dest = (DataWrapper) wrapper.getConstructor(Mtu.class).newInstance(copy);

        //copy values
//        Mapping.init().commitWrapped(src, dest);

        //assert values
//        assertThat(dest.getThisTarget().getId()).isEqualTo(src.getThisTarget().getId());
    }
}
