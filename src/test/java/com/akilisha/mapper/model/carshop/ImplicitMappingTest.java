package com.akilisha.mapper.model.carshop;

import com.akilisha.mapper.merge.LRMapping;
import com.akilisha.mapper.model.docs.Rental;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

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
    void implicit_mapping_from_array_to_array() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Seller src = new Seller("mr fixer", null, null, new Vehicle[]{mazda}, null);
        Seller dest = new Seller();
        LRMapping.init().merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.rentals).hasSize(1);
        Vehicle v = dest.rentals[0];
        assertThat(v).isNotNull();
        assertThat(v.getMake()).isEqualTo("mazda");
        assertThat(v.getModel()).isEqualTo("c60");
        assertThat(v.getTrim()).isEqualTo("sport");
        assertThat(v.getEngine()).isNull();
        assertThat(v.getTrany()).isNull();
        assertThat(dest.trucks).isNull();
        assertThat(dest.bikers).isNull();
        assertThat(dest.cars).isNull();

        // make partially explicit
        Seller dest2 = new Seller();
        LRMapping.init().map("rentals").merge(src, dest2);

        assertThat(dest2.title).isEqualTo("mr fixer");
        assertThat(dest2.rentals).hasSize(1);
        Vehicle v2 = dest2.rentals[0];
        assertThat(v2).isNotNull();
        assertThat(v2.getMake()).isEqualTo("mazda");
        assertThat(v2.getModel()).isEqualTo("c60");
        assertThat(v2.getTrim()).isEqualTo("sport");
        assertThat(v2.getEngine()).isNull();
        assertThat(v2.getTrany()).isNull();
        assertThat(dest2.trucks).isNull();
        assertThat(dest2.bikers).isNull();
        assertThat(dest2.cars).isNull();
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

        //make partially explicit
        Seller dest2 = new Seller();
        LRMapping.init().map("bikers").merge(src, dest2);

        assertThat(dest2.title).isEqualTo("mr fixer");
        assertThat(dest2.bikers).hasSize(1);
        Vehicle v2 = dest2.bikers.stream().findAny().get();
        assertThat(v2).isNotNull();
        assertThat(v2.getMake()).isEqualTo("mazda");
        assertThat(v2.getModel()).isEqualTo("c60");
        assertThat(v2.getTrim()).isEqualTo("sport");
        assertThat(v2.getEngine()).isNull();
        assertThat(v2.getTrany()).isNull();
        assertThat(dest2.trucks).isNull();
        assertThat(dest2.rentals).isNull();
        assertThat(dest2.cars).isNull();
    }

    @Test
    void implicit_mapping_from_list_to_list() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Vehicle bmw = new Car("bmw", "x5", "all terrain", null, new Trany(BigInteger.valueOf(100), 6, true), null);
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
        assertThat(v1.getTrim()).isEqualTo("all terrain");
        assertThat(v1.getTrany()).isNotNull();
        assertThat(v1.getTrany().getTorque()).isEqualTo(BigInteger.valueOf(100));
        assertThat(v1.getTrany().getGears()).isEqualTo(6);
        assertThat(v1.getTrany().isAwd()).isTrue();
        assertThat(v1.getEngine()).isNull();

        assertThat(dest.cars).isNull();
        assertThat(dest.rentals).isNull();
        assertThat(dest.bikers).isNull();

        //make partially explicit
        Seller dest3 = new Seller();
        LRMapping.init().map("trucks").merge(src, dest3);

        assertThat(dest3.title).isEqualTo("mr fixer");
        assertThat(dest3.trucks).hasSize(2);
        Vehicle v2 = dest3.trucks.get(0);
        assertThat(v2).isNotNull();
        assertThat(v2.getMake()).isEqualTo("mazda");
        assertThat(v2.getModel()).isEqualTo("c60");
        assertThat(v2.getTrim()).isEqualTo("sport");
        assertThat(v2.getEngine()).isNull();
        assertThat(v2.getTrany()).isNull();
    }

    @Test
    void implicit_mapping_from_map_to_map() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Vehicle bmw = new Car("bmw", "x5", "all terrain", null, new Trany(BigInteger.valueOf(100), 6, true), null);
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
        assertThat(v1.getTrim()).isEqualTo("all terrain");
        assertThat(v1.getTrany()).isNotNull();
        assertThat(v1.getTrany().getTorque()).isEqualTo(BigInteger.valueOf(100));
        assertThat(v1.getTrany().getGears()).isEqualTo(6);
        assertThat(v1.getTrany().isAwd()).isTrue();
        assertThat(v1.getEngine()).isNull();

        assertThat(dest.trucks).isNull();
        assertThat(dest.rentals).isNull();
        assertThat(dest.bikers).isNull();

        //make partially explicit
        Seller dest2 = new Seller();
        LRMapping.init().map("cars").merge(src, dest2);

        assertThat(dest2.title).isEqualTo("mr fixer");
        assertThat(dest2.cars).hasSize(2);
        Vehicle v2 = dest2.cars.get(1L);
        assertThat(v2).isNotNull();
        assertThat(v2.getMake()).isEqualTo("mazda");
        assertThat(v2.getModel()).isEqualTo("c60");
        assertThat(v2.getTrim()).isEqualTo("sport");
        assertThat(v2.getEngine()).isNull();
        assertThat(v2.getTrany()).isNull();
    }

    @Test
    void implicit_mapping_from_set_to_list() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Seller src = new Seller("mr fixer", Set.of(mazda), null, null, null);
        Seller dest = new Seller();
        LRMapping.init().map("bikers", "trucks").merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.trucks).hasSize(1);
        Vehicle v = dest.trucks.get(0);
        assertThat(v).isNotNull();
        assertThat(v.getMake()).isEqualTo("mazda");
        assertThat(v.getModel()).isEqualTo("c60");
        assertThat(v.getTrim()).isEqualTo("sport");
        assertThat(v.getEngine()).isNull();
        assertThat(v.getTrany()).isNull();
        assertThat(dest.bikers).isNull();
        assertThat(dest.rentals).isNull();
        assertThat(dest.cars).isNull();
    }

    @Test
    void implicit_mapping_from_set_to_array() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Seller src = new Seller("mr fixer", Set.of(mazda), null, null, null);
        Seller dest = new Seller();
        LRMapping.init().map("bikers", "rentals").merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.rentals).hasSize(1);
        Vehicle v = dest.rentals[0];
        assertThat(v).isNotNull();
        assertThat(v.getMake()).isEqualTo("mazda");
        assertThat(v.getModel()).isEqualTo("c60");
        assertThat(v.getTrim()).isEqualTo("sport");
        assertThat(v.getEngine()).isNull();
        assertThat(v.getTrany()).isNull();
        assertThat(dest.bikers).isNull();
        assertThat(dest.trucks).isNull();
        assertThat(dest.cars).isNull();
    }

    @Test
    void implicit_mapping_from_set_to_map() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Seller src = new Seller("mr fixer", Set.of(mazda), null, null, null);
        Seller dest = new Seller();
        AtomicLong mapKey = new AtomicLong(0);
        LRMapping.init().map("bikers", "cars", mapKey::incrementAndGet).merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.cars).hasSize(1);
        Vehicle v = dest.cars.get(1L);
        assertThat(v).isNotNull();
        assertThat(v.getMake()).isEqualTo("mazda");
        assertThat(v.getModel()).isEqualTo("c60");
        assertThat(v.getTrim()).isEqualTo("sport");
        assertThat(v.getEngine()).isNull();
        assertThat(v.getTrany()).isNull();
        assertThat(dest.bikers).isNull();
        assertThat(dest.trucks).isNull();
        assertThat(dest.rentals).isNull();
    }

    @Test
    void implicit_mapping_from_list_to_set() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Seller src = new Seller("mr fixer", null, List.of(mazda), null, null);
        Seller dest = new Seller();
        LRMapping.init().map("trucks", "bikers" ).merge(src, dest);

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
    void implicit_mapping_from_list_to_array() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Seller src = new Seller("mr fixer", null, List.of(mazda), null, null);
        Seller dest = new Seller();
        LRMapping.init().map("trucks", "rentals" ).merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.rentals).hasSize(1);
        Vehicle v = dest.rentals[0];
        assertThat(v).isNotNull();
        assertThat(v.getMake()).isEqualTo("mazda");
        assertThat(v.getModel()).isEqualTo("c60");
        assertThat(v.getTrim()).isEqualTo("sport");
        assertThat(v.getEngine()).isNull();
        assertThat(v.getTrany()).isNull();
        assertThat(dest.trucks).isNull();
        assertThat(dest.bikers).isNull();
        assertThat(dest.cars).isNull();
    }

    @Test
    void implicit_mapping_from_list_to_map() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Seller src = new Seller("mr fixer", null, List.of(mazda), null, null);
        Seller dest = new Seller();
        AtomicLong mapKey = new AtomicLong(10);
        LRMapping.init().map("trucks", "cars", mapKey::incrementAndGet).merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.cars).hasSize(1);
        Vehicle v = dest.cars.get(11L);
        assertThat(v).isNotNull();
        assertThat(v.getMake()).isEqualTo("mazda");
        assertThat(v.getModel()).isEqualTo("c60");
        assertThat(v.getTrim()).isEqualTo("sport");
        assertThat(v.getEngine()).isNull();
        assertThat(v.getTrany()).isNull();
        assertThat(dest.bikers).isNull();
        assertThat(dest.trucks).isNull();
        assertThat(dest.rentals).isNull();
    }

    @Test
    void implicit_mapping_from_array_to_list() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Seller src = new Seller("mr fixer", null, null, new Vehicle[]{mazda}, null);
        Seller dest = new Seller();
        LRMapping.init().map("rentals", "trucks" ).merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.trucks).hasSize(1);
        Vehicle v = dest.trucks.get(0);
        assertThat(v).isNotNull();
        assertThat(v.getMake()).isEqualTo("mazda");
        assertThat(v.getModel()).isEqualTo("c60");
        assertThat(v.getTrim()).isEqualTo("sport");
        assertThat(v.getEngine()).isNull();
        assertThat(v.getTrany()).isNull();
        assertThat(dest.rentals).isNull();
        assertThat(dest.bikers).isNull();
        assertThat(dest.cars).isNull();
    }

    @Test
    void implicit_mapping_from_array_to_set() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Seller src = new Seller("mr fixer", null, null, new Vehicle[]{mazda}, null);
        Seller dest = new Seller();
        LRMapping.init().map("rentals", "bikers" ).merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.bikers).hasSize(1);
        Vehicle v = dest.bikers.stream().findAny().get();
        assertThat(v).isNotNull();
        assertThat(v.getMake()).isEqualTo("mazda");
        assertThat(v.getModel()).isEqualTo("c60");
        assertThat(v.getTrim()).isEqualTo("sport");
        assertThat(v.getEngine()).isNull();
        assertThat(v.getTrany()).isNull();
        assertThat(dest.rentals).isNull();
        assertThat(dest.trucks).isNull();
        assertThat(dest.cars).isNull();
    }

    @Test
    void implicit_mapping_from_array_to_map() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Seller src = new Seller("mr fixer", null, null, new Vehicle[]{mazda}, null);
        Seller dest = new Seller();
        AtomicLong mapKey = new AtomicLong(10);
        LRMapping.init().map("rentals", "cars", mapKey::incrementAndGet).merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.cars).hasSize(1);
        Vehicle v = dest.cars.get(11L);
        assertThat(v).isNotNull();
        assertThat(v.getMake()).isEqualTo("mazda");
        assertThat(v.getModel()).isEqualTo("c60");
        assertThat(v.getTrim()).isEqualTo("sport");
        assertThat(v.getEngine()).isNull();
        assertThat(v.getTrany()).isNull();
        assertThat(dest.bikers).isNull();
        assertThat(dest.trucks).isNull();
        assertThat(dest.rentals).isNull();
    }

    @Test
    void implicit_mapping_from_map_to_array() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Vehicle bmw = new Car("bmw", "x5", "all terrain", null, new Trany(BigInteger.valueOf(100), 6, true), null);
        Seller src = new Seller("mr fixer", null, null, null, Map.of(1L, mazda, 2L, bmw));
        Seller dest = new Seller();
        LRMapping.init().map("cars", "rentals").merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.rentals).hasSize(2);
        Vehicle v0 = Arrays.stream(dest.rentals).filter(f -> f.getMake().equals("mazda")).findAny().orElse(null);
        assertThat(v0).isNotNull();
        assertThat(v0.getMake()).isEqualTo("mazda");
        assertThat(v0.getModel()).isEqualTo("c60");
        assertThat(v0.getTrim()).isEqualTo("sport");
        assertThat(v0.getEngine()).isNull();
        assertThat(v0.getTrany()).isNull();

        Vehicle v1 = Arrays.stream(dest.rentals).filter(f -> f.getMake().equals("bmw")).findAny().orElse(null);
        assertThat(v1).isNotNull();
        assertThat(v1.getMake()).isEqualTo("bmw");
        assertThat(v1.getModel()).isEqualTo("x5");
        assertThat(v1.getTrim()).isEqualTo("all terrain");
        assertThat(v1.getTrany()).isNotNull();
        assertThat(v1.getTrany().getTorque()).isEqualTo(BigInteger.valueOf(100));
        assertThat(v1.getTrany().getGears()).isEqualTo(6);
        assertThat(v1.getTrany().isAwd()).isTrue();
        assertThat(v1.getEngine()).isNull();

        assertThat(dest.cars).isNull();
        assertThat(dest.trucks).isNull();
        assertThat(dest.bikers).isNull();
    }

    @Test
    void implicit_mapping_from_map_to_list() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Vehicle bmw = new Car("bmw", "x5", "all terrain", null, new Trany(BigInteger.valueOf(100), 6, true), null);
        Seller src = new Seller("mr fixer", null, null, null, Map.of(1L, mazda, 2L, bmw));
        Seller dest = new Seller();
        LRMapping.init().map("cars", "trucks").merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.trucks).hasSize(2);
        Vehicle v0 = dest.trucks.stream().filter(f -> f.getMake().equals("mazda")).findAny().orElse(null);
        assertThat(v0).isNotNull();
        assertThat(v0.getMake()).isEqualTo("mazda");
        assertThat(v0.getModel()).isEqualTo("c60");
        assertThat(v0.getTrim()).isEqualTo("sport");
        assertThat(v0.getEngine()).isNull();
        assertThat(v0.getTrany()).isNull();

        Vehicle v1 = dest.trucks.stream().filter(f -> f.getMake().equals("bmw")).findAny().orElse(null);
        assertThat(v1).isNotNull();
        assertThat(v1.getMake()).isEqualTo("bmw");
        assertThat(v1.getModel()).isEqualTo("x5");
        assertThat(v1.getTrim()).isEqualTo("all terrain");
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
    void implicit_mapping_from_map_to_set() throws Throwable {
        Vehicle mazda = new Car("mazda", "c60", "sport", null, null, null);
        Vehicle bmw = new Car("bmw", "x5", "all terrain", null, new Trany(BigInteger.valueOf(100), 6, true), null);
        Seller src = new Seller("mr fixer", null, null, null, Map.of(1L, mazda, 2L, bmw));
        Seller dest = new Seller();
        LRMapping.init().map("cars", "bikers").merge(src, dest);

        assertThat(dest.title).isEqualTo("mr fixer");
        assertThat(dest.bikers).hasSize(2);
        Vehicle v0 = dest.bikers.stream().filter(f -> f.getMake().equals("mazda")).findAny().orElse(null);
        assertThat(v0).isNotNull();
        assertThat(v0.getMake()).isEqualTo("mazda");
        assertThat(v0.getModel()).isEqualTo("c60");
        assertThat(v0.getTrim()).isEqualTo("sport");
        assertThat(v0.getEngine()).isNull();
        assertThat(v0.getTrany()).isNull();

        Vehicle v1 = dest.bikers.stream().filter(f -> f.getMake().equals("bmw")).findAny().orElse(null);
        assertThat(v1).isNotNull();
        assertThat(v1.getMake()).isEqualTo("bmw");
        assertThat(v1.getModel()).isEqualTo("x5");
        assertThat(v1.getTrim()).isEqualTo("all terrain");
        assertThat(v1.getTrany()).isNotNull();
        assertThat(v1.getTrany().getTorque()).isEqualTo(BigInteger.valueOf(100));
        assertThat(v1.getTrany().getGears()).isEqualTo(6);
        assertThat(v1.getTrany().isAwd()).isTrue();
        assertThat(v1.getEngine()).isNull();

        assertThat(dest.cars).isNull();
        assertThat(dest.rentals).isNull();
        assertThat(dest.trucks).isNull();
    }
}
