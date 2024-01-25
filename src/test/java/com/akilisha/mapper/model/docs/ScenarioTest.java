package com.akilisha.mapper.model.docs;

import com.akilisha.mapper.merge.LRMapping;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ScenarioTest {

    @Test
    void One_to_one_mapping_with_no_mismatch() throws Throwable {
        Tenant src = new Tenant(123L, "first", "customer", true, (short) 123, new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789"));
        Tenant dest = new Tenant();
        LRMapping.init().merge(src, dest);

        assertThat(dest.id).isEqualTo(123L);
        assertThat(dest.firstName).isEqualTo("first");
        assertThat(dest.lastName).isEqualTo("customer");
        assertThat(dest.accepted).isEqualTo(true);
        assertThat(dest.entryCode).isEqualTo((short) 123);
        assertThat(dest.location.id).isEqualTo(234L);
        assertThat(dest.location.unitNumber).isEqualTo("1001");
        assertThat(dest.location.streetName).isEqualTo("michigan ave");
        assertThat(dest.location.city).isEqualTo("cheboygan");
        assertThat(dest.location.state).isEqualTo("MI");
        assertThat(dest.location.zipCode).isEqualTo("56789");
    }

    @Test
    void Property_name_mismatch() throws Throwable {
        Tenant src = new Tenant(123L, "first", "customer", true, (short) 123, new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789"));
        Tenant_A dest = new Tenant_A();
        LRMapping.init()
                .map("firstName", "first_name")
                .map("lastName", "last_name")
                .map("entryCode", "entry_code")
                .merge(src, dest);

        assertThat(dest.id).isEqualTo(123L);
        assertThat(dest.first_name).isEqualTo("first");
        assertThat(dest.last_name).isEqualTo("customer");
        assertThat(dest.accepted).isEqualTo(true);
        assertThat(dest.entry_code).isEqualTo((short) 123);
        assertThat(dest.location.id).isEqualTo(234L);
        assertThat(dest.location.unitNumber).isEqualTo("1001");
        assertThat(dest.location.streetName).isEqualTo("michigan ave");
        assertThat(dest.location.city).isEqualTo("cheboygan");
        assertThat(dest.location.state).isEqualTo("MI");
        assertThat(dest.location.zipCode).isEqualTo("56789");
    }

    @Test
    void Property_type_mismatch() throws Throwable {
        Tenant src = new Tenant(123L, "first", "customer", true, (short) 123, new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789"));
        Tenant_B dest = new Tenant_B();
        LRMapping.init()
                .map("id", (id) -> BigDecimal.valueOf((long) id))
                .map("entryCode", (code) -> ((Short) code).toString())
                .merge(src, dest);

        assertThat(dest.id).isEqualTo(BigDecimal.valueOf(123L));
        assertThat(dest.firstName).isEqualTo("first");
        assertThat(dest.lastName).isEqualTo("customer");
        assertThat(dest.accepted).isEqualTo(true);
        assertThat(dest.entryCode).isEqualTo("123");
        assertThat(dest.location.id).isEqualTo(234L);
        assertThat(dest.location.unitNumber).isEqualTo("1001");
        assertThat(dest.location.streetName).isEqualTo("michigan ave");
        assertThat(dest.location.city).isEqualTo("cheboygan");
        assertThat(dest.location.state).isEqualTo("MI");
        assertThat(dest.location.zipCode).isEqualTo("56789");
    }

    @Test
    void Mismatch_on_both_property_name_and_type() throws Throwable {
        Tenant src = new Tenant(123L, "first", "customer", true, (short) 123, new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789"));
        Tenant_C dest = new Tenant_C();
        LRMapping.init()
                .map("id", "guestNumber", (id) -> BigDecimal.valueOf((long) id))
                .map("firstName", "guestName")
                .map("entryCode", "guestCode", (code) -> ((Short) code).toString())
                .merge(src, dest);

        assertThat(dest.guestNumber).isEqualTo(BigDecimal.valueOf(123L));
        assertThat(dest.guestName).isEqualTo("first");
        assertThat(dest.accepted).isEqualTo(true);
        assertThat(dest.guestCode).isEqualTo("123");
        assertThat(dest.location.id).isEqualTo(234L);
        assertThat(dest.location.unitNumber).isEqualTo("1001");
        assertThat(dest.location.streetName).isEqualTo("michigan ave");
        assertThat(dest.location.city).isEqualTo("cheboygan");
        assertThat(dest.location.state).isEqualTo("MI");
        assertThat(dest.location.zipCode).isEqualTo("56789");
    }

    @Test
    void Property_multiplicity_mismatch_with_embedded_lhs() throws Throwable {
        Tenant_D src = new Tenant_D(123L, "first", "customer", true, "123", 234L, "1001", "michigan ave", "cheboygan", "MI", "56789");
        Tenant dest = new Tenant();
        LRMapping.init()
                .map("code", "entryCode", str -> Short.parseShort((String) str))
                .map("unitId", "location.id")
                .map("unitNumber", "location.")
                .map("streetName", "location.")
                .map("city", "location.")
                .map("state", "location.")
                .map("zip", "location.zipCode")
                .merge(src, dest);

        assertThat(dest.id).isEqualTo(123L);
        assertThat(dest.firstName).isEqualTo("first");
        assertThat(dest.lastName).isEqualTo("customer");
        assertThat(dest.accepted).isEqualTo(true);
        assertThat(dest.entryCode).isEqualTo((short) 123);
        assertThat(dest.location.id).isEqualTo(234L);
        assertThat(dest.location.unitNumber).isEqualTo("1001");
        assertThat(dest.location.streetName).isEqualTo("michigan ave");
        assertThat(dest.location.city).isEqualTo("cheboygan");
        assertThat(dest.location.state).isEqualTo("MI");
        assertThat(dest.location.zipCode).isEqualTo("56789");
    }

    @Test
    void Property_multiplicity_mismatch_with_embedded_lhs_with_nested_objects() throws Throwable {
        Tenant_D src = new Tenant_D(123L, "first", "customer", true, "123", 234L, "1001", "michigan ave", "cheboygan", "MI", "56789");
        Tenant_E dest = new Tenant_E();
        LRMapping.init()
                .map("firstName", "name.")
                .map("lastName", "name.")
                .map("code", "entryCode", str -> Short.parseShort((String) str))
                .map("unitId", "location.id")
                .map("unitNumber", "location.")
                .map("streetName", "location.")
                .map("city", "location.cityInfo.")
                .map("state", "location.cityInfo.")
                .map("zip", "location.cityInfo.zipCode")
                .merge(src, dest);

        assertThat(dest.id).isEqualTo(123L);
        assertThat(dest.name.firstName).isEqualTo("first");
        assertThat(dest.name.lastName).isEqualTo("customer");
        assertThat(dest.accepted).isEqualTo(true);
        assertThat(dest.entryCode).isEqualTo((short) 123);
        assertThat(dest.location.id).isEqualTo(234L);
        assertThat(dest.location.unitNumber).isEqualTo("1001");
        assertThat(dest.location.streetName).isEqualTo("michigan ave");
        assertThat(dest.location.cityInfo.city).isEqualTo("cheboygan");
        assertThat(dest.location.cityInfo.state).isEqualTo("MI");
        assertThat(dest.location.cityInfo.zipCode).isEqualTo("56789");
    }

    @Test
    void Property_multiplicity_mismatch_lhs_with_single_collection_object() throws Throwable {
        Tenant_D src = new Tenant_D(123L, "first", "customer", true, "123", 234L, "1001", "michigan ave", "cheboygan", "MI", "56789");
        Tenant_F dest = new Tenant_F();
        LRMapping.init()
                .map("firstName", "name.")
                .map("lastName", "name.")
                .map("code", "entryCode", str -> Short.parseShort((String) str))
                .map("unitId", "locations.id", Rental.class)
                .map("unitNumber", "locations.", Rental.class)
                .map("streetName", "locations.", Rental.class)
                .map("city", "locations.", Rental.class)
                .map("state", "locations.", Rental.class)
                .map("zip", "locations.zipCode", Rental.class)
                .merge(src, dest);

        assertThat(dest.id).isEqualTo(123L);
        assertThat(dest.name.firstName).isEqualTo("first");
        assertThat(dest.name.lastName).isEqualTo("customer");
        assertThat(dest.accepted).isEqualTo(true);
        assertThat(dest.entryCode).isEqualTo((short) 123);
        List<Rental> locations = new ArrayList<>(dest.locations);
        assertThat(locations).hasSize(1);
        assertThat(locations.get(0).id).isEqualTo(234L);
        assertThat(locations.get(0).unitNumber).isEqualTo("1001");
        assertThat(locations.get(0).streetName).isEqualTo("michigan ave");
        assertThat(locations.get(0).city).isEqualTo("cheboygan");
        assertThat(locations.get(0).state).isEqualTo("MI");
        assertThat(locations.get(0).zipCode).isEqualTo("56789");
    }

    @Test
    void Property_multiplicity_mismatch_lhs_with_single_array_object() throws Throwable {
        Tenant_D src = new Tenant_D(123L, "first", "customer", true, "123", 234L, "1001", "michigan ave", "cheboygan", "MI", "56789");
        Tenant_K dest = new Tenant_K();
        LRMapping.init()
                .map("firstName", "name.")
                .map("lastName", "name.")
                .map("code", "entryCode", str -> Short.parseShort((String) str))
                .map("unitId", "locations.id", Rental.class)
                .map("unitNumber", "locations.", Rental.class)
                .map("streetName", "locations.", Rental.class)
                .map("city", "locations.", Rental.class)
                .map("state", "locations.", Rental.class)
                .map("zip", "locations.zipCode", Rental.class)
                .merge(src, dest);

        assertThat(dest.id).isEqualTo(123L);
        assertThat(dest.name.firstName).isEqualTo("first");
        assertThat(dest.name.lastName).isEqualTo("customer");
        assertThat(dest.accepted).isEqualTo(true);
        assertThat(dest.entryCode).isEqualTo((short) 123);
        Rental[] locations = dest.locations;
        assertThat(locations).hasSize(1);
        assertThat(locations[0].id).isEqualTo(234L);
        assertThat(locations[0].unitNumber).isEqualTo("1001");
        assertThat(locations[0].streetName).isEqualTo("michigan ave");
        assertThat(locations[0].city).isEqualTo("cheboygan");
        assertThat(locations[0].state).isEqualTo("MI");
        assertThat(locations[0].zipCode).isEqualTo("56789");
    }

    @Test
    void Property_multiplicity_mismatch_lhs_with_single_collection_object_having_nested_entities() throws Throwable {
        Tenant_D src = new Tenant_D(123L, "first", "customer", true, "123", 234L, "1001", "michigan ave", "cheboygan", "MI", "56789");
        Tenant_G dest = new Tenant_G();
        LRMapping.init()
                .map("firstName", "name.")
                .map("lastName", "name.")
                .map("code", "entryCode", str -> Short.parseShort((String) str))
                .map("unitId", "locations.id", Rental_E.class)
                .map("unitNumber", "locations.", Rental_E.class)
                .map("streetName", "locations.", Rental_E.class)
                .map("city", "locations.cityInfo.", Rental_E.class)
                .map("state", "locations.cityInfo.", Rental_E.class)
                .map("zip", "locations.cityInfo.zipCode", Rental_E.class)
                .merge(src, dest);

        assertThat(dest.id).isEqualTo(123L);
        assertThat(dest.name.firstName).isEqualTo("first");
        assertThat(dest.name.lastName).isEqualTo("customer");
        assertThat(dest.accepted).isEqualTo(true);
        assertThat(dest.entryCode).isEqualTo((short) 123);
        List<Rental_E> locations = new ArrayList<>(dest.locations);
        assertThat(locations).hasSize(1);
        assertThat(locations.get(0).id).isEqualTo(234L);
        assertThat(locations.get(0).unitNumber).isEqualTo("1001");
        assertThat(locations.get(0).streetName).isEqualTo("michigan ave");
        assertThat(locations.get(0).cityInfo.city).isEqualTo("cheboygan");
        assertThat(locations.get(0).cityInfo.state).isEqualTo("MI");
        assertThat(locations.get(0).cityInfo.zipCode).isEqualTo("56789");
    }

    @Test
    void Property_multiplicity_mismatch_lhs_with_single_collection_object_having_nested_entities_and_converter_function() throws Throwable {
        Tenant_D src = new Tenant_D(123L, "first", "customer", true, "123", 234L, "1001", "michigan ave", "cheboygan", "MI", "56789");
        Tenant_H dest = new Tenant_H();
        LRMapping.init()
                .map("firstName", "name.")
                .map("lastName", "name.")
                .map("code", "entryCode", str -> Short.parseShort((String) str))
                .map("unitId", "locations.id", Rental_F.class, String::valueOf)
                .map("unitNumber", "locations.", Rental_F.class)
                .map("streetName", "locations.", Rental_F.class)
                .map("city", "locations.cityInfo.", Rental_F.class)
                .map("state", "locations.cityInfo.", Rental_F.class)
                .map("zip", "locations.cityInfo.zipCode", Rental_F.class)
                .merge(src, dest);

        assertThat(dest.id).isEqualTo(123L);
        assertThat(dest.name.firstName).isEqualTo("first");
        assertThat(dest.name.lastName).isEqualTo("customer");
        assertThat(dest.accepted).isEqualTo(true);
        assertThat(dest.entryCode).isEqualTo((short) 123);
        List<Rental_F> locations = new ArrayList<>(dest.locations);
        assertThat(locations).hasSize(1);
        assertThat(locations.get(0).id).isEqualTo("234");
        assertThat(locations.get(0).unitNumber).isEqualTo("1001");
        assertThat(locations.get(0).streetName).isEqualTo("michigan ave");
        assertThat(locations.get(0).cityInfo.city).isEqualTo("cheboygan");
        assertThat(locations.get(0).cityInfo.state).isEqualTo("MI");
        assertThat(locations.get(0).cityInfo.zipCode).isEqualTo("56789");
    }

    @Test
    void Property_multiplicity_mismatch_with_embedded_rhs() throws Throwable {
        Tenant_I src = new Tenant_I(123L, "first", "customer", true, (short) 123, new Rental_D(234L, "1001", "michigan ave", "cheboygan", "MI", "56789"));
        Tenant_D dest = new Tenant_D();
        LRMapping.init()
                .map("entryCode", "code", sh -> Short.toString((Short) sh))
                .map("location")
                .merge(src, dest);

        assertThat(dest.id).isEqualTo(123L);
        assertThat(dest.firstName).isEqualTo("first");
        assertThat(dest.lastName).isEqualTo("customer");
        assertThat(dest.accepted).isEqualTo(true);
        assertThat(dest.code).isEqualTo("123");
        assertThat(dest.unitId).isEqualTo(234L);
        assertThat(dest.unitNumber).isEqualTo("1001");
        assertThat(dest.streetName).isEqualTo("michigan ave");
        assertThat(dest.city).isEqualTo("cheboygan");
        assertThat(dest.state).isEqualTo("MI");
        assertThat(dest.zip).isEqualTo("56789");
    }

    @Test
    void Property_multiplicity_mismatch_with_embedded_rhs_with_custom_mapping() throws Throwable {
        Tenant src = new Tenant(123L, "first", "customer", true, (short) 123, new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789"));
        Tenant_D dest = new Tenant_D();
        LRMapping.init()
                .map("entryCode", "code", sh -> Short.toString((Short) sh))
                .map("location", LRMapping.init()
                        .map("id", "unitId")
                        .map("zipCode", "zip"))
                .merge(src, dest);

        assertThat(dest.id).isEqualTo(123L);
        assertThat(dest.firstName).isEqualTo("first");
        assertThat(dest.lastName).isEqualTo("customer");
        assertThat(dest.accepted).isEqualTo(true);
        assertThat(dest.code).isEqualTo("123");
        assertThat(dest.unitId).isEqualTo(234L);
        assertThat(dest.unitNumber).isEqualTo("1001");
        assertThat(dest.streetName).isEqualTo("michigan ave");
        assertThat(dest.city).isEqualTo("cheboygan");
        assertThat(dest.state).isEqualTo("MI");
        assertThat(dest.zip).isEqualTo("56789");
    }

    @Test
    void Property_multiplicity_mismatch__single_collection_element_on_lhs() throws Throwable {
        Tenant_J src = new Tenant_J(123L, new NameInfo("first", "customer"), true, (short) 123, List.of(new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789")));
        Tenant_D dest = new Tenant_D();
        LRMapping.init()
                .map("name", LRMapping.init()
                        .map("first", "firstName")
                        .map("last", "lastName"))
                .map("entryCode", "code", sh -> Short.toString((Short) sh))
                .map("locations", Rental.class, LRMapping.init()
                        .map("id", "unitId")
                        .map("zipCode", "zip"))
                .merge(src, dest);

        assertThat(dest.id).isEqualTo(123L);
        assertThat(dest.firstName).isEqualTo("first");
        assertThat(dest.lastName).isEqualTo("customer");
        assertThat(dest.accepted).isEqualTo(true);
        assertThat(dest.code).isEqualTo("123");
        assertThat(dest.unitId).isEqualTo(234L);
        assertThat(dest.unitNumber).isEqualTo("1001");
        assertThat(dest.streetName).isEqualTo("michigan ave");
        assertThat(dest.city).isEqualTo("cheboygan");
        assertThat(dest.state).isEqualTo("MI");
        assertThat(dest.zip).isEqualTo("56789");
    }

    @Test
    void Property_multiplicity_mismatch__single_array_element_on_lhs() throws Throwable {
        Tenant_K src = new Tenant_K(123L, new NameInfo("first", "customer"), true, (short) 123, new Rental[]{new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789")});
        Tenant_D dest = new Tenant_D();
        LRMapping.init()
                .map("name", LRMapping.init()
                        .map("first", "firstName")
                        .map("last", "lastName"))
                .map("entryCode", "code", sh -> Short.toString((Short) sh))
                .map("locations", Rental.class, LRMapping.init()
                        .map("id", "unitId")
                        .map("zipCode", "zip"))
                .merge(src, dest);

        assertThat(dest.id).isEqualTo(123L);
        assertThat(dest.firstName).isEqualTo("first");
        assertThat(dest.lastName).isEqualTo("customer");
        assertThat(dest.accepted).isEqualTo(true);
        assertThat(dest.code).isEqualTo("123");
        assertThat(dest.unitId).isEqualTo(234L);
        assertThat(dest.unitNumber).isEqualTo("1001");
        assertThat(dest.streetName).isEqualTo("michigan ave");
        assertThat(dest.city).isEqualTo("cheboygan");
        assertThat(dest.state).isEqualTo("MI");
        assertThat(dest.zip).isEqualTo("56789");
    }

    @Test
    void Property_multiplicity_mismatch__collection_elements_on_both_lhs_and_rhs() throws Throwable {
        Tenant_K k1 = new Tenant_K(123L, new NameInfo("muchina", "gachanja"), true, (short) 123, new Rental[]{new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789")});
        Tenant_K k2 = new Tenant_K(456L, new NameInfo("ondieki", "mong'are"), false, (short) 987, new Rental[]{new Rental(567L, "2002", "superior ave", "dearborn", "MI", "56778")});
        RecordsA src = new RecordsA("records from server A", Set.of(k1, k2));
        RecordsB dest = new RecordsB();
        LRMapping.init()
                .map("name", "title")
                .map("tenants", Tenant_K.class, "tenants", Tenant_D.class,
                        LRMapping.init()
                                .map("name", LRMapping.init()
                                        .map("first", "firstName")
                                        .map("last", "lastName"))
                                .map("entryCode", "code", sh -> Short.toString((Short) sh))
                                .map("locations", Rental.class, LRMapping.init()
                                        .map("id", "unitId")
                                        .map("zipCode", "zip")))
                .merge(src, dest);

        assertThat(dest.title).isEqualTo(src.name);
        Tenant_D tenant_0 = Arrays.stream(dest.tenants).filter(d -> d.id == 123L).findAny().orElse(null);
        assertThat(tenant_0).isNotNull();
        assertThat(tenant_0.id).isEqualTo(123L);
        assertThat(tenant_0.firstName).isEqualTo("muchina");
        assertThat(tenant_0.lastName).isEqualTo("gachanja");
        assertThat(tenant_0.accepted).isEqualTo(true);
        assertThat(tenant_0.code).isEqualTo("123");
        assertThat(tenant_0.unitId).isEqualTo(234L);
        assertThat(tenant_0.unitNumber).isEqualTo("1001");
        assertThat(tenant_0.streetName).isEqualTo("michigan ave");
        assertThat(tenant_0.city).isEqualTo("cheboygan");
        assertThat(tenant_0.state).isEqualTo("MI");
        assertThat(tenant_0.zip).isEqualTo("56789");
    }

    @Test
    void Property_multiplicity_mismatch__collection_elements_on_lhs_and_map_on_rhs() throws Throwable {
        Tenant_K k1 = new Tenant_K(123L, new NameInfo("muchina", "gachanja"), true, (short) 123, new Rental[]{new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789")});
        Tenant_K k2 = new Tenant_K(456L, new NameInfo("ondieki", "mong'are"), false, (short) 987, new Rental[]{new Rental(567L, "2002", "superior ave", "dearborn", "MI", "56778")});
        RecordsA src = new RecordsA("records from server A", Set.of(k1, k2));
        RecordsC dest = new RecordsC();
        LRMapping.init()
                .map("name", "title")
                .map("tenants", Tenant_K.class, "tenants", Tenant_D.class, "id",
                        LRMapping.init()
                                .map("name", LRMapping.init()
                                        .map("first", "firstName")
                                        .map("last", "lastName"))
                                .map("entryCode", "code", sh -> Short.toString((Short) sh))
                                .map("locations", Rental.class, LRMapping.init()
                                        .map("id", "unitId")
                                        .map("zipCode", "zip")))
                .merge(src, dest);

        assertThat(dest.title).isEqualTo(src.name);
        Tenant_D tenant_d0 = dest.tenants.get(123L);
        assertThat(tenant_d0).isNotNull();
        assertThat(tenant_d0.id).isEqualTo(123L);
        assertThat(tenant_d0.firstName).isEqualTo("muchina");
        assertThat(tenant_d0.lastName).isEqualTo("gachanja");
        assertThat(tenant_d0.accepted).isEqualTo(true);
        assertThat(tenant_d0.code).isEqualTo("123");
        assertThat(tenant_d0.unitId).isEqualTo(234L);
        assertThat(tenant_d0.unitNumber).isEqualTo("1001");
        assertThat(tenant_d0.streetName).isEqualTo("michigan ave");
        assertThat(tenant_d0.city).isEqualTo("cheboygan");
        assertThat(tenant_d0.state).isEqualTo("MI");
        assertThat(tenant_d0.zip).isEqualTo("56789");
        Tenant_D tenant_d1 = dest.tenants.get(456L);
        assertThat(tenant_d1).isNotNull();
        assertThat(tenant_d1.id).isEqualTo(456L);
        assertThat(tenant_d1.firstName).isEqualTo("ondieki");
        assertThat(tenant_d1.lastName).isEqualTo("mong'are");
        assertThat(tenant_d1.accepted).isEqualTo(false);
        assertThat(tenant_d1.code).isEqualTo("987");
        assertThat(tenant_d1.unitId).isEqualTo(567L);
        assertThat(tenant_d1.unitNumber).isEqualTo("2002");
        assertThat(tenant_d1.streetName).isEqualTo("superior ave");
        assertThat(tenant_d1.city).isEqualTo("dearborn");
        assertThat(tenant_d1.state).isEqualTo("MI");
        assertThat(tenant_d1.zip).isEqualTo("56778");

        // Now reversing the mapping
        RecordsA newDest = new RecordsA();

        LRMapping.init()
                .map("title", "name")
                .map("tenants", Tenant_D.class, "tenants", Tenant_K.class, LRMapping.init()
                        .map("firstName", "name.firstName")
                        .map("lastName", "name.lastName")
                        .map("code", "entryCode", str -> Short.parseShort((String) str))
                        .map("unitId", "locations.id", Rental.class)
                        .map("unitNumber", "locations.", Rental.class)
                        .map("streetName", "locations.", Rental.class)
                        .map("city", "locations.", Rental.class)
                        .map("state", "locations.", Rental.class)
                        .map("zip", "locations.zipCode", Rental.class))

                .merge(dest, newDest);

        assertThat(newDest.name).isEqualTo(src.name);
        Tenant_K tenant_k0 = newDest.tenants.stream().filter(f -> f.id == 123L).findAny().orElse(null);
        assertThat(tenant_k0).isNotNull();
        assertThat(tenant_k0.id).isEqualTo(123L);
        assertThat(tenant_k0.name.firstName).isEqualTo("muchina");
        assertThat(tenant_k0.name.lastName).isEqualTo("gachanja");
        assertThat(tenant_k0.accepted).isEqualTo(true);
        assertThat(tenant_k0.entryCode).isEqualTo((short) 123);
        assertThat(tenant_k0.locations[0].id).isEqualTo(234L);
        assertThat(tenant_k0.locations[0].unitNumber).isEqualTo("1001");
        assertThat(tenant_k0.locations[0].streetName).isEqualTo("michigan ave");
        assertThat(tenant_k0.locations[0].city).isEqualTo("cheboygan");
        assertThat(tenant_k0.locations[0].state).isEqualTo("MI");
        assertThat(tenant_k0.locations[0].zipCode).isEqualTo("56789");
        Tenant_K tenant_k1 = newDest.tenants.stream().filter(f -> f.id == 456L).findAny().orElse(null);
        assertThat(tenant_k1).isNotNull();
        assertThat(tenant_k1.id).isEqualTo(456L);
        assertThat(tenant_k1.name.firstName).isEqualTo("ondieki");
        assertThat(tenant_k1.name.lastName).isEqualTo("mong'are");
        assertThat(tenant_k1.accepted).isEqualTo(false);
        assertThat(tenant_k1.entryCode).isEqualTo((short) 987);
        assertThat(tenant_k1.locations[0].id).isEqualTo(567L);
        assertThat(tenant_k1.locations[0].unitNumber).isEqualTo("2002");
        assertThat(tenant_k1.locations[0].streetName).isEqualTo("superior ave");
        assertThat(tenant_k1.locations[0].city).isEqualTo("dearborn");
        assertThat(tenant_k1.locations[0].state).isEqualTo("MI");
        assertThat(tenant_k1.locations[0].zipCode).isEqualTo("56778");

        // Now reversing the mapping again, (but to an array type instead of a collection type)
        RecordsB newDestArr = new RecordsB();

        LRMapping.init()
                .map("tenants", "tenants", Tenant_D.class)
                .merge(dest, newDestArr);

        assertThat(newDest.name).isEqualTo(src.name);
        Tenant_D tenant_d00 = dest.tenants.get(123L);
        assertThat(tenant_d00).isNotNull();
        assertThat(tenant_d00.id).isEqualTo(123L);
        assertThat(tenant_d00.firstName).isEqualTo("muchina");
        assertThat(tenant_d00.lastName).isEqualTo("gachanja");
        assertThat(tenant_d00.accepted).isEqualTo(true);
        assertThat(tenant_d00.code).isEqualTo("123");
        assertThat(tenant_d00.unitId).isEqualTo(234L);
        assertThat(tenant_d00.unitNumber).isEqualTo("1001");
        assertThat(tenant_d00.streetName).isEqualTo("michigan ave");
        assertThat(tenant_d00.city).isEqualTo("cheboygan");
        assertThat(tenant_d00.state).isEqualTo("MI");
        assertThat(tenant_d00.zip).isEqualTo("56789");
        Tenant_D tenant_d01 = dest.tenants.get(456L);
        assertThat(tenant_d01).isNotNull();
        assertThat(tenant_d01.id).isEqualTo(456L);
        assertThat(tenant_d01.firstName).isEqualTo("ondieki");
        assertThat(tenant_d01.lastName).isEqualTo("mong'are");
        assertThat(tenant_d01.accepted).isEqualTo(false);
        assertThat(tenant_d01.code).isEqualTo("987");
        assertThat(tenant_d01.unitId).isEqualTo(567L);
        assertThat(tenant_d01.unitNumber).isEqualTo("2002");
        assertThat(tenant_d01.streetName).isEqualTo("superior ave");
        assertThat(tenant_d01.city).isEqualTo("dearborn");
        assertThat(tenant_d01.state).isEqualTo("MI");
        assertThat(tenant_d01.zip).isEqualTo("56778");
    }
}
