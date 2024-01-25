package com.akilisha.mapper.model.carshop;

import com.akilisha.mapper.merge.LRMapping;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ImplicitMappingTest {

    @Test
    void copy_different_scenarios_of_seller_having_uninitialized_variables() throws Throwable {
        Seller src = new Seller("", emptySet(), null, null, null);
        Seller dest = new Seller();
        LRMapping.init().merge(src, dest);

        assertThat(dest.title).isBlank();
        assertThat(dest.bikers).isEmpty();
        assertThat(dest.trucks).isNull();
        assertThat(dest.rentals).isNull();
        assertThat(dest.cars).isNull();

        Seller src2 = new Seller(null, null, emptyList(), new Vehicle[]{}, emptyMap());
        Seller dest2 = new Seller();
        LRMapping.init().merge(src2, dest2);

        assertThat(dest2.title).isNull();
        assertThat(dest2.bikers).isNull();
        assertThat(dest2.trucks).isEmpty();
        assertThat(dest2.rentals).isEmpty();
        assertThat(dest2.cars).isEmpty();
    }

    @Test
    void implicit_mapping_from_set_to_set() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Seller src = new Seller("mr fixer", Set.of(mazda), null, null, null);
        Seller dest = new Seller();
        LRMapping.init().merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.bikers).hasSize(1);
        Vehicle v = dest.bikers.stream().findAny().get();
        assertThat(v).isNotNull();
        assertThat(v.getMake()).isEqualTo("mazda");
        assertThat(v.getModel()).isEqualTo("c60");
        assertThat(v.getTrim()).isEqualTo("sport");
        assertThat(v.getEngine()).isNull();
        assertThat(v.getTrany()).isNull();
        assertThat(dest.trucks).isNull();
        assertThat(dest.rentals).isNull();
        assertThat(dest.cars).isNull();
    }

    @Test
    void implicit_mapping_from_list_to_list() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Vehicle bmw = new Car("bmw", "x5", "terrain", null, new Trany(BigInteger.valueOf(100), 6, true), null);
        Seller src = new Seller("mr fixer", null, List.of(mazda, bmw), null, null);
        Seller dest = new Seller();
        LRMapping.init().merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.trucks).hasSize(2);
        Vehicle v0 = dest.trucks.get(0);
        assertThat(v0).isNotNull();
        assertThat(v0.getMake()).isEqualTo("mazda");
        assertThat(v0.getModel()).isEqualTo("c60");
        assertThat(v0.getTrim()).isEqualTo("sport");
        assertThat(v0.getEngine()).isNull();
        assertThat(v0.getTrany()).isNull();

        Vehicle v1 = dest.trucks.get(1);
        assertThat(v1).isNotNull();
        assertThat(v1.getMake()).isEqualTo("bmw");
        assertThat(v1.getModel()).isEqualTo("x5");
        assertThat(v1.getTrim()).isEqualTo("terrain");
        assertThat(v1.getTrany()).isNotNull();
        assertThat(v1.getTrany().getTorque()).isEqualTo(BigInteger.valueOf(100));
        assertThat(v1.getTrany().getGears()).isEqualTo(6);
        assertThat(v1.getTrany().isAwd()).isTrue();
        assertThat(v1.getEngine()).isNull();

        assertThat(dest.cars).isNull();
        assertThat(dest.rentals).isNull();
        assertThat(dest.bikers).isNull();
    }

    @Test
    void implicit_mapping_from_map_to_map() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Vehicle bmw = new Car("bmw", "x5", "terrain", null, new Trany(BigInteger.valueOf(100), 6, true), null);
        Seller src = new Seller("mr fixer", null, null, null, Map.of(1L, mazda, 2L, bmw));
        Seller dest = new Seller();
        LRMapping.init().merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.cars).hasSize(2);
        Vehicle v0 = dest.cars.get(1L);
        assertThat(v0).isNotNull();
        assertThat(v0.getMake()).isEqualTo("mazda");
        assertThat(v0.getModel()).isEqualTo("c60");
        assertThat(v0.getTrim()).isEqualTo("sport");
        assertThat(v0.getEngine()).isNull();
        assertThat(v0.getTrany()).isNull();

        Vehicle v1 = dest.cars.get(2L);
        assertThat(v1).isNotNull();
        assertThat(v1.getMake()).isEqualTo("bmw");
        assertThat(v1.getModel()).isEqualTo("x5");
        assertThat(v1.getTrim()).isEqualTo("terrain");
        assertThat(v1.getTrany()).isNotNull();
        assertThat(v1.getTrany().getTorque()).isEqualTo(BigInteger.valueOf(100));
        assertThat(v1.getTrany().getGears()).isEqualTo(6);
        assertThat(v1.getTrany().isAwd()).isTrue();
        assertThat(v1.getEngine()).isNull();

        assertThat(dest.trucks).isNull();
        assertThat(dest.rentals).isNull();
        assertThat(dest.bikers).isNull();
    }
}
