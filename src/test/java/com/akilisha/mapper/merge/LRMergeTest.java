package com.akilisha.mapper.merge;


import com.akilisha.mapper.dto.entity.*;
import com.akilisha.mapper.dto.model.Mtu;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LRMergeTest {

    @Test
    public void basic_test_to_dest_between_two_identical_objects() throws Throwable {
        Mtu src = new Mtu(123L, "jimna rishi", BigDecimal.valueOf(123456L), true, (short) 123);
        Mtu dest = new Mtu();

        //dest values
        LRMapping.init().merge(src, dest);

        //assert values
        assertThat(dest.getId()).isEqualTo(src.getId());
        assertThat(dest.getMajina()).isEqualTo(src.getMajina());
        assertThat(dest.getMshahara()).isEqualTo(src.getMshahara());
        assertThat(dest.isPamoja()).isEqualTo(src.isPamoja());
        assertThat(dest.getSiri()).isEqualTo(src.getSiri());
    }

    @Test
    public void basic_test_to_dest_between_two_non_identical_objects() throws Throwable {
        Map<String, Object> src = new HashMap<>();
        src.put("number", "123");
        src.put("majina", "jimna rishi");
        src.put("mshahara", "123456");
        src.put("pamoja", true);
        src.put("passcode", (short) 123);
        Mtu dest = new Mtu();

        //dest values
        LRMapping.init()
                .map("number", "id", str -> Long.valueOf((String) str))
                .map("mshahara", str -> new BigDecimal(String.valueOf(str)))
                .map("passcode", "siri")
                .merge(src, dest);

        //assert values
        assertThat(dest.getId()).isEqualTo(123L);
        assertThat(dest.getMajina()).isEqualTo(src.get("majina"));
        assertThat(dest.getMshahara()).isEqualTo(new BigDecimal("123456"));
        assertThat(dest.isPamoja()).isEqualTo(src.get("pamoja"));
        assertThat(dest.getSiri()).isEqualTo(src.get("passcode"));
    }

    @Test
    void verify_dest_scenario_simplest_case() throws Throwable {
        //create entities
        PersonEntity src = new PersonEntity(1L, "jim", "bob", "616-667-7656", "cell");
        Person0 person = new Person0();

        // create mapping
        LRMapping.init()
                .map("phoneNumber", "phone")
                .map("phoneType", "type")
                .merge(src, person);

        //perform assertions
        assertThat(person.getId()).isEqualTo(1L);
        assertThat(person.getFirstName()).isEqualTo("jim");
        assertThat(person.getLastName()).isEqualTo("bob");
        assertThat(person.getPhone()).isEqualTo("616-667-7656");
        assertThat(person.getType()).isEqualTo("cell");

        // create reverse mapping
        PersonEntity srcAgain = new PersonEntity();

        LRMapping.init()
                .map("phone", "phoneNumber")
                .map("type", "phoneType")
                .merge(person, srcAgain);

        //perform assertions
        assertThat(srcAgain.getId()).isEqualTo(1L);
        assertThat(srcAgain.getFirstName()).isEqualTo("jim");
        assertThat(srcAgain.getLastName()).isEqualTo("bob");
        assertThat(srcAgain.getPhoneNumber()).isEqualTo("616-667-7656");
        assertThat(srcAgain.getPhoneType()).isEqualTo("cell");
    }

    @Test
    void verify_dest_scenario_simple_case_using_converter_function() throws Throwable {
        //create entities
        PersonEntity src = new PersonEntity(1L, "jim", "bob", "616-667-7656", "cell");
        Person2 person = new Person2();

        // create mapping
        LRMapping.init()
                .map("phoneNumber", "phoneNum")
                .map("phoneType", "isCellPhone", (value) -> value.equals("cell"))
                .merge(src, person);

        //perform assertions
        assertThat(person.getId()).isEqualTo(1L);
        assertThat(person.getFirstName()).isEqualTo("jim");
        assertThat(person.getLastName()).isEqualTo("bob");
        assertThat(person.getPhoneNum()).isEqualTo("616-667-7656");
        assertThat(person.getIsCellPhone()).isTrue();
    }

    @Test
    void verify_that_expected_fields_in_src_class_are_mapped_to_person1() throws Throwable {
        //create entities
        PersonEntity src = new PersonEntity(1L, "jim", "bob", "616-667-7656", "cell");
        Person1 person = new Person1();

        //do mapping
        LRMapping.init()
                .map("phoneNumber", "phone.number")
                .map("phoneType", "phone.type")
                .merge(src, person);

        //perform assertions
        assertThat(person.getId()).isEqualTo(1L);
        assertThat(person.getFirstName()).isEqualTo("jim");
        assertThat(person.getLastName()).isEqualTo("bob");
        assertThat(person.getPhone()).isNotNull();
        assertThat(person.getPhone().getNumber()).isEqualTo("616-667-7656");
        assertThat(person.getPhone().getType()).isEqualTo("cell");

        // create reverse mapping
        PersonEntity srcAgain = new PersonEntity();

        LRMapping.init()
                .map("phone", LRMapping.init()
                        .map("number", "phoneNumber")
                        .map("type", "phoneType"))
                .merge(person, srcAgain);

        //perform assertions
        assertThat(srcAgain.getId()).isEqualTo(1L);
        assertThat(srcAgain.getFirstName()).isEqualTo("jim");
        assertThat(srcAgain.getLastName()).isEqualTo("bob");
        assertThat(srcAgain.getPhoneNumber()).isEqualTo("616-667-7656");
        assertThat(srcAgain.getPhoneType()).isEqualTo("cell");
    }

    @Test
    void verify_dest_scenario_when_either_side_contains_single_list_element() throws Throwable {
        //create entities
        PersonEntity src = new PersonEntity(1L, "jim", "bob", "616-667-7656", "cell");
        Person3 person = new Person3();

        // create mapping
        LRMapping.init()
                .map("phoneNumber", "phones.number", Phone0.class)
                .map("phoneType", "phones.type", Phone0.class)
                .merge(src, person);

        //perform assertions
        assertThat(person.getId()).isEqualTo(1L);
        assertThat(person.getFirstName()).isEqualTo("jim");
        assertThat(person.getLastName()).isEqualTo("bob");
        assertThat(person.getPhones()).isNotEmpty();
        assertThat(person.getPhones().get(0).getNumber()).isEqualTo("616-667-7656");
        assertThat(person.getPhones().get(0).getType()).isEqualTo("cell");

        //create reverse mapping
        PersonEntity srcAgain = new PersonEntity();

        // create mapping
        LRMapping.init()
                .map("phones", Phone0.class, LRMapping.init()
                        .map("number", "phoneNumber")
                        .map("type", "phoneType"))
                .merge(person, srcAgain);

        //perform assertions
        assertThat(srcAgain.getId()).isEqualTo(1L);
        assertThat(srcAgain.getFirstName()).isEqualTo("jim");
        assertThat(srcAgain.getLastName()).isEqualTo("bob");
        assertThat(srcAgain.getPhoneNumber()).isEqualTo("616-667-7656");
        assertThat(srcAgain.getPhoneType()).isEqualTo("cell");
    }

    @Test
    void verify_that_expected_fields_in_entity_class_are_mapped_to_person4() throws Throwable {
        //create entities
        PersonEntity entity = new PersonEntity(1L, "jim", "bob", "616-667-7656", "cell");
        Person4 person = new Person4();

        // create mapping
        LRMapping.init()
                .map("firstName", "first")
                .map("lastName", "last")
                .map("phoneNumber", "phones.number", Phone1.class)
                .map("phoneType", "phones.isCell", Phone1.class, (value) -> value.equals("cell"))
                .merge(entity, person);

        //perform assertions
        assertThat(person.getId()).isEqualTo(1L);
        assertThat(person.getFirst()).isEqualTo("jim");
        assertThat(person.getLast()).isEqualTo("bob");
        assertThat(person.getPhones()).isNotEmpty();
        assertThat(person.getPhones().get(0).getNumber()).isEqualTo("616-667-7656");
        assertThat(person.getPhones().get(0).getIsCell()).isTrue();
    }

    @Test
    void verify_that_expected_fields_in_product_entity_are_mapped_to_product() throws Throwable {
        //create entities
        UUID uuid = UUID.randomUUID();
        ProductEntity entity = new ProductEntity(uuid, "kimbo", 10.20, 100);
        Product product = new Product();

        // create mapping
        LRMapping.init()
                .map("id", "itemId", Object::toString)
                .map("product_name", "productName")
                .map("price", "price", (value) -> BigDecimal.valueOf((double) value))
                .map("quantity", "quantity", (value) -> BigInteger.valueOf((int) value))
                .merge(entity, product);

        //perform assertions
        assertThat(product.getItemId()).isEqualTo(uuid.toString());
        assertThat(product.getProductName()).isEqualTo("kimbo");
        assertThat(product.getPrice()).isEqualTo(BigDecimal.valueOf(10.20));
        assertThat(product.getQuantity()).isEqualTo(BigInteger.valueOf(100));
    }

    @Test
    void verify_that_list_of_product_entity_items_in_order_entity_is_mapped_to_order() throws Throwable {
        //create entities
        UUID kimboId = UUID.randomUUID();
        UUID kasukuId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        OrderEntity entity = new OrderEntity(orderId, List.of(
                new ProductEntity(kimboId, "kimbo", 10.20, 100),
                new ProductEntity(kasukuId, "kasuku", 12.70, 88)), "random string value"
        );
        Order order = new Order();

        // create mapping
        LRMapping.init()
                .map("id", "orderId", Object::toString)
                .map("products", ProductEntity.class, "orderItems", Product.class, LRMapping.init()
                        .map("id", "itemId", Object::toString)
                        .map("product_name", "productName")
                        .map("price", "price", (value) -> BigDecimal.valueOf((double) value))
                        .map("quantity", "quantity", (value) -> BigInteger.valueOf((int) value)))
                .merge(entity, order);

        //perform assertions
        assertThat(order.getOrderId()).isEqualTo(orderId.toString());
        assertThat(order.getOrderItems()).isNotEmpty();

        //product 1
        assertThat(order.getOrderItems().get(0).getItemId()).isEqualTo(kimboId.toString());
        assertThat(order.getOrderItems().get(0).getProductName()).isEqualTo("kimbo");
        assertThat(order.getOrderItems().get(0).getPrice()).isEqualTo(BigDecimal.valueOf(10.20));
        assertThat(order.getOrderItems().get(0).getQuantity()).isEqualTo(BigInteger.valueOf(100));

        //product 2
        assertThat(order.getOrderItems().get(1).getItemId()).isEqualTo(kasukuId.toString());
        assertThat(order.getOrderItems().get(1).getProductName()).isEqualTo("kasuku");
        assertThat(order.getOrderItems().get(1).getPrice()).isEqualTo(BigDecimal.valueOf(12.70));
        assertThat(order.getOrderItems().get(1).getQuantity()).isEqualTo(BigInteger.valueOf(88));
    }
}