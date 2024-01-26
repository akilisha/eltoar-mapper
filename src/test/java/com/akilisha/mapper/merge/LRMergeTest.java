package com.akilisha.mapper.merge;


import com.akilisha.mapper.definition.FieldDef;
import com.akilisha.mapper.model.*;
import com.akilisha.mapper.model.company.Address;
import com.akilisha.mapper.model.company.Employee;
import com.akilisha.mapper.model.cycle.Child;
import com.akilisha.mapper.model.cycle.Parent;
import com.akilisha.mapper.model.ecomm.Order;
import com.akilisha.mapper.model.ecomm.OrderEntity;
import com.akilisha.mapper.model.ecomm.Product;
import com.akilisha.mapper.model.ecomm.ProductEntity;
import com.akilisha.mapper.model.movies.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
    public void basic_test_to_dest_between_map_and_model_object() throws Throwable {
        Map<String, Object> src = new LinkedHashMap<>();
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
    public void basic_test_to_dest_between_map_with_nested_entities_and_a_model_object() throws Throwable {
        Map<String, Object> src = new LinkedHashMap<>();
        src.put("id", "123");
        src.put("name", "jimna rishi");
        src.put("perm", true);
        src.put("address", Map.of("street", "El chapo Ave", "city", "Dearborn", "state", "MI", "zip", "66578"));
        src.put("phones", List.of(Map.of("type", "cell", "number", "323-454-6767"), Map.of("type", "office", "number", "323-343-4646")));
        src.put("role", "Developer");
        Employee dest = new Employee();

        //dest values
        LRMapping.init()
                .map("id", str -> Integer.parseInt((String) str))
                .map("address", null, Address.class, LRMapping.init().map("zip", "zipcode", str -> Integer.parseInt((String) str)))
                .merge(src, dest);

        //assert values
        assertThat(dest.getId()).isEqualTo(123L);
        assertThat(dest.getName()).isEqualTo(src.get("name"));
        assertThat(dest.getRole()).isEqualTo("Developer");
        assertThat(dest.getAddress()).isNotNull();
        assertThat(dest.getAddress().getStreet()).isEqualTo("El chapo Ave");
        assertThat(dest.getAddress().getCity()).isEqualTo("Dearborn");
        assertThat(dest.getAddress().getState()).isEqualTo("MI");
        assertThat(dest.getAddress().getZipcode()).isEqualTo(66578);
        assertThat(dest.getPhones()).isNotEmpty();
        assertThat(dest.getPhones()[0].getNumber()).isEqualTo("323-454-6767");
        assertThat(dest.getPhones()[0].getType()).isEqualTo("cell");
        assertThat(dest.getPhones()[1].getNumber()).isEqualTo("323-343-4646");
        assertThat(dest.getPhones()[1].getType()).isEqualTo("office");
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
        Phone1[] phones = person.getPhones().toArray(Phone1[]::new);
        assertThat(phones[0].getNumber()).isEqualTo("616-667-7656");
        assertThat(phones[0].getIsCell()).isTrue();
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
                .map("products", "orderItems", Product.class, LRMapping.init()
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

    @Test
    void verify_mapping_item_having_array_field() throws Throwable {
        Hobby iceFishing = new Hobby("ice fishing", 5, 5.5f);
        Hobby windSurfing = new Hobby("wind surfing", 8, 4.5f);
        Person5 src = new Person5(123L, "Frank", "Darabont", Set.of(new Phone1(true, "616-337-3398")), new Hobby[]{iceFishing, windSurfing}, "LA", "CA",
                Map.of(1, new Tour("Chicago", "IL"), 2, new Tour("Madison", "WI"), 3, new Tour("Minneapolis", "MN")));

        Person5 dest = new Person5();
        LRMapping.init().merge(src, dest);

        assertThat(dest.getId()).isEqualTo(123L);
        assertThat(dest.getFirst()).isEqualTo("Frank");
        assertThat(dest.getHobbies()).isNotEmpty();
        assertThat(dest.getHobbies()).hasSize(2);
        assertThat(dest.getHobbies()[0].getActivity()).isEqualTo("ice fishing");
        assertThat(dest.getHobbies()[1].getActivity()).isEqualTo("wind surfing");
        assertThat(dest.getPhones()).isNotEmpty();
        assertThat(dest.getPhones()).hasSize(1);
        assertThat(dest.getPhones().toArray(Phone1[]::new)[0].getNumber()).isEqualTo("616-337-3398");
        assertThat(dest.getRoadTrip()).isNotEmpty();
        assertThat(dest.getRoadTrip()).hasSize(3);
        assertThat(dest.getRoadTrip().get(1).getCity()).isEqualTo("Chicago");
        assertThat(dest.getRoadTrip().get(2).getCity()).isEqualTo("Madison");
        assertThat(dest.getRoadTrip().get(3).getCity()).isEqualTo("Minneapolis");
    }

    @Test
    void verify_mapping_very_simple_cyclic_relations() throws Throwable {
        //parent created with no children
        Parent parent = new Parent("awesome", null);
        //children created and assigned parent
        Child child1 = new Child(1, parent);
        Child child2 = new Child(2, parent);
        //parent accepts children
        parent.setChildren(new Child[]{child1, child2});

        //make a copy of the parent
        Parent copy = new Parent();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> LRMapping.init().merge(parent, copy));
    }

    @Test
    void verify_same_mapping_very_simple_but_removing_the_cyclic_relations() throws Throwable {
        //parent created with no children
        Parent parent = new Parent("awesome", null);
        //children created and assigned parent
        Child child1 = new Child(1, null);
        Child child2 = new Child(2, null);
        //parent accepts children
        parent.setChildren(new Child[]{child1, child2});

        //make a copy of the parent
        Parent copy = new Parent();
        LRMapping.init().merge(parent, copy);

        //perform assertions
        assertThat(copy.getChildren()).isNotEmpty();
        assertThat(copy.getChildren()).hasSize(2);
        assertThat(copy.getChildren()[0].getId()).isEqualTo(1);
        assertThat(copy.getChildren()[1].getId()).isEqualTo(2);
        assertThat(copy.getName()).isEqualTo("awesome");
    }

    @Test
    void verify_mapping_item_having_fields_with_cyclic_relations() throws Throwable {
        //create entities
        Hobby iceFishing = new Hobby("ice fishing", 5, 5.5f);
        Hobby windSurfing = new Hobby("wind surfing", 8, 4.5f);
        Hobby sailBoating = new Hobby("ocean surfing", 8, 7.5f);
        Hobby rollerSkating = new Hobby("roller skating", 8, 7.5f);
        Hobby storyWriting = new Hobby("story writing", 8, 6.6f);
        Hobby horseRiding = new Hobby("horse riding", 8, 8.4f);
        Person5 frankD = new Person5(123L, "Frank", "Darabont", Set.of(new Phone1(true, "616-337-3398")), new Hobby[]{}, "LA", "CA",
                Map.of(1, new Tour("Chicago", "IL"), 2, new Tour("Madison", "WI"), 3, new Tour("Minneapolis", "MN")));
        Person5 bruceB = new Person5(123L, "Bruce", "Beresford", Set.of(new Phone1(true, "565-543-5467")), new Hobby[]{rollerSkating}, "Athens", "OH",
                Map.of(1, new Tour("Dallas", "TX"), 2, new Tour("Denver", "CO"), 3, new Tour("Cheyenne", "WY")));
        Person5 johnS = new Person5(124L, "John", "Singleton", Set.of(new Phone1(true, "459-343-5657")), new Hobby[]{rollerSkating}, "Hossier", "IN",
                Map.of(1, new Tour("Athens", "OH"), 2, new Tour("Georgetown", "KY"), 3, new Tour("Little Rock", "AK")));
        Actor timR = new Actor(221L, "Tim", "Robbins", Set.of(new Phone("cell", "344-554-5444")), Gender.M, "Fishport", "AL", new LinkedHashMap<>(), new HashSet<>(), new Hobby[]{iceFishing});
        Actor morganF = new Actor(222L, "Morgan", "Freeman", Set.of(new Phone("cell", "455-289-2755")), Gender.M, "Gatorville", "MS", new LinkedHashMap<>(), new HashSet<>(), new Hobby[]{sailBoating});
        Actor bobG = new Actor(223L, "Bob", "Gunton", Set.of(new Phone("home", "582-594-4567")), Gender.M, "Atlanta", "GA", new LinkedHashMap<>(), new HashSet<>(), new Hobby[]{windSurfing});
        Actor clancyB = new Actor(224L, "Clancy", "Brown", Set.of(new Phone("work", "899-523-2231")), Gender.M, "Athens", "OH", new LinkedHashMap<>(), new HashSet<>(), new Hobby[]{rollerSkating});
        Actor jessicaT = new Actor(226L, "Jessica", "Tandy", Set.of(new Phone("work", "123-453-9854")), Gender.F, "Miami", "FL", new LinkedHashMap<>(), new HashSet<>(), new Hobby[]{storyWriting});
        Actor pattiL = new Actor(227L, "Patti", "LuPone", Set.of(new Phone("work", "467-236-9854")), Gender.F, "Jeezville", "MN", new LinkedHashMap<>(), new HashSet<>(), new Hobby[]{sailBoating});
        Actor estherR = new Actor(228L, "Esther", "Rolle", Set.of(new Phone("cell", "592-398-2983")), Gender.F, "horseville", "MT", new LinkedHashMap<>(), new HashSet<>(), new Hobby[]{horseRiding});
        Actor donC = new Actor(229L, "Don", "Cheadle", Set.of(new Phone("cell", "983-876-3422")), Gender.M, "cornville", "IA", new LinkedHashMap<>(), new HashSet<>(), new Hobby[]{horseRiding});
        Movie shawShank = new Movie("The Shawshank Redemption", LocalDate.ofYearDay(1998, 1), Set.of(timR, morganF, bobG, clancyB, estherR), 4.9f, new Person5[]{frankD});
        Movie missDaisy = new Movie("Driving Miss Daisy", LocalDate.ofYearDay(1999, 1), Set.of(morganF, jessicaT, pattiL, estherR), 7.9f, new Person5[]{bruceB});
        Movie rosewood = new Movie("Rosewood", LocalDate.ofYearDay(1999, 1), Set.of(estherR, donC), 7.9f, new Person5[]{johnS});

        //add cyclic relation
        morganF.getOtherMovies().add(missDaisy);
        estherR.getOtherMovies().add(rosewood);
//        donC.getOtherMovies().add(shawShank); // not true, but ok, so what? It's just testing :-)

        // create mapping
        Actor donCopy = new Actor();
//        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> LRMapping.init().merge(donC, donCopy));
        LRMapping.init().merge(donC, donCopy);

        //perform assertions
        assertThat(donCopy.getHobbies()).isNotEmpty();
        assertThat(donCopy.getHobbies()[0].getActivity()).isEqualTo("horse riding");
        assertThat(donCopy.getGender()).isEqualTo(Gender.M);
        assertThat(donCopy.getOtherMovies()).isEmpty();
    }

    @Test
    void test_getting_field_values_from_basic_class_def() throws Throwable {
        Mtu src = new Mtu(123L, "jimna rishi", BigDecimal.valueOf(123456L), true, (short) 123);
        LRMerge merge = new LRMerge() {
        };
        Map<String, FieldDef> fields = merge.fieldValues(src);
        assertThat(fields).hasSize(5);
        assertThat(fields.get("id").getValue()).isEqualTo((long) 123);
        assertThat(fields.get("majina").getValue()).isEqualTo("jimna rishi");
        assertThat(fields.get("mshahara").getValue()).isEqualTo(new BigDecimal("123456"));
        assertThat(fields.get("pamoja").getValue()).isEqualTo(true);
        assertThat(fields.get("siri").getValue()).isEqualTo((short) 123);
    }

    @Test
    void test_getting_field_values_from_basic_map() throws Throwable {
        Map<String, Object> src = new LinkedHashMap<>();
        src.put("number", "123");
        src.put("majina", "jimna rishi");
        src.put("mshahara", "123456");
        src.put("pamoja", true);
        src.put("passcode", (short) 123);

        LRMerge merge = new LRMerge() {
        };

        Map<String, FieldDef> fields = merge.fieldValues(src);
        assertThat(fields).hasSize(5);
        assertThat(fields.get("number").getValue()).isEqualTo("123");
        assertThat(fields.get("majina").getValue()).isEqualTo("jimna rishi");
        assertThat(fields.get("mshahara").getValue()).isEqualTo("123456");
        assertThat(fields.get("pamoja").getValue()).isEqualTo(true);
        assertThat(fields.get("passcode").getValue()).isEqualTo((short) 123);
    }
}