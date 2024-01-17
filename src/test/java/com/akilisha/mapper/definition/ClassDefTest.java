package com.akilisha.mapper.definition;

import com.akilisha.mapper.dto.entity.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import static com.akilisha.mapper.definition.Mappings.classDef;
import static org.assertj.core.api.Assertions.assertThat;

class ClassDefTest {

    @Test
    void verify_that_type_are_detected_correctly() {
        ClassDef def = new ClassDef(null);
        String floatType = "F";
        Class<?> fType = def.detectType(floatType);
        assertThat(fType.getName()).isEqualTo("float");

        String objectType = "Ljava/lang/Object;";
        Class<?> oType = def.detectType(objectType);
        assertThat(oType.getName()).isEqualTo("java.lang.Object");

        String intArrayType = "[I";
        Class<?> iaType = def.detectType(intArrayType);
        assertThat(iaType.getName()).isEqualTo("[I");

        String object3dArrayType = "[[Ljava/lang/Object;";
        Class<?> o3daType = def.detectType(object3dArrayType);
        assertThat(o3daType.getName()).isEqualTo("[[Ljava.lang.Object;");
    }

    @Test
    void verify_that_expected_fields_in_entity_class_are_mapped_to_person0() throws Throwable {
        //create entities
        PersonEntity entity = new PersonEntity(1L, "jim", "bob", "616-667-7656", "cell");
        Person0 person = new Person0();

        // create mapping
        Mapping mapping = Mapping.init()
                .map("phone", "phoneNumber")
                .map("type", "phoneType");

        //do mapping
        mapping.commit(entity, person);

        //perform assertions
        assertThat(person.getId()).isEqualTo(1L);
        assertThat(person.getFirstName()).isEqualTo("jim");
        assertThat(person.getLastName()).isEqualTo("bob");
        assertThat(person.getPhone()).isEqualTo("616-667-7656");
        assertThat(person.getType()).isEqualTo("cell");

        // create reverse mapping
        PersonEntity entityAgain = new PersonEntity();

        Mapping reversed = Mapping.init()
                .map("phoneNumber", "phone")
                .map("phoneType", "type");

        //do mapping
        reversed.commit(person, entityAgain);

        //perform assertions
        assertThat(entityAgain.getId()).isEqualTo(1L);
        assertThat(entityAgain.getFirstName()).isEqualTo("jim");
        assertThat(entityAgain.getLastName()).isEqualTo("bob");
        assertThat(entityAgain.getPhoneNumber()).isEqualTo("616-667-7656");
        assertThat(entityAgain.getPhoneType()).isEqualTo("cell");
    }

    @Test
    void verify_that_expected_fields_in_entity_class_are_mapped_to_person1() throws Throwable {
        // create mapping
        Mapping mapping = Mapping.init()
                .map("phone.number", "phoneNumber")
                .map("phone.type", "phoneType");

        // retrieve fields and their types
        ClassDef fromDef = classDef(PersonEntity.class);
        ClassDef toDef = classDef(Person1.class);

        //create entities
        PersonEntity entity = new PersonEntity(1L, "jim", "bob", "616-667-7656", "cell");
        Person1 person = new Person1();

        //do mapping
        Mappings.mapAToB(entity, fromDef, person, toDef, mapping);

        //perform assertions
        assertThat(person.getId()).isEqualTo(1L);
        assertThat(person.getFirstName()).isEqualTo("jim");
        assertThat(person.getLastName()).isEqualTo("bob");
        assertThat(person.getPhone()).isNotNull();
        assertThat(person.getPhone().getNumber()).isEqualTo("616-667-7656");
        assertThat(person.getPhone().getType()).isEqualTo("cell");

        // create reverse mapping
        PersonEntity entityAgain = new PersonEntity();

        Mapping reversed = Mapping.init()
                .map("phoneNumber", "phone.number")
                .map("phoneType", "phone.type");

        //do mapping
        reversed.commit(person, entityAgain);

        //perform assertions
        assertThat(entityAgain.getId()).isEqualTo(1L);
        assertThat(entityAgain.getFirstName()).isEqualTo("jim");
        assertThat(entityAgain.getLastName()).isEqualTo("bob");
        assertThat(entityAgain.getPhoneNumber()).isEqualTo("616-667-7656");
        assertThat(entityAgain.getPhoneType()).isEqualTo("cell");
    }

    @Test
    void verify_that_expected_fields_in_entity_class_are_mapped_to_person2() throws Throwable {
        // create mapping
        Mapping mapping = Mapping.init()
                .map("phoneNum", "phoneNumber")
                .map("isCellPhone", "phoneType", (value) -> value.equals("cell"));

        // retrieve fields and their types
        ClassDef fromDef = classDef(PersonEntity.class);
        ClassDef toDef = classDef(Person2.class);

        //create entities
        PersonEntity entity = new PersonEntity(1L, "jim", "bob", "616-667-7656", "cell");
        Person2 person = new Person2();

        //do mapping
        Mappings.mapAToB(entity, fromDef, person, toDef, mapping);

        //perform assertions
        assertThat(person.getId()).isEqualTo(1L);
        assertThat(person.getFirstName()).isEqualTo("jim");
        assertThat(person.getLastName()).isEqualTo("bob");
        assertThat(person.getPhoneNum()).isEqualTo("616-667-7656");
        assertThat(person.getIsCellPhone()).isTrue();
    }

    @Test
    void verify_that_expected_fields_in_entity_class_are_mapped_to_person3() throws Throwable {
        // create mapping
        Mapping mapping = Mapping.init()
                .map("phones.number", Phone0.class, "phoneNumber")
                .map("phones.type", Phone0.class, "phoneType");

        // retrieve fields and their types
        ClassDef fromDef = classDef(PersonEntity.class);
        ClassDef toDef = classDef(Person3.class);

        //create entities
        PersonEntity entity = new PersonEntity(1L, "jim", "bob", "616-667-7656", "cell");
        Person3 person = new Person3();

        //do mapping
        Mappings.mapAToB(entity, fromDef, person, toDef, mapping);

        //perform assertions
        assertThat(person.getId()).isEqualTo(1L);
        assertThat(person.getFirstName()).isEqualTo("jim");
        assertThat(person.getLastName()).isEqualTo("bob");
        assertThat(person.getPhones()).isNotEmpty();
        assertThat(person.getPhones().get(0).getNumber()).isEqualTo("616-667-7656");
        assertThat(person.getPhones().get(0).getType()).isEqualTo("cell");
    }

    @Test
    void verify_that_expected_fields_in_entity_class_are_mapped_to_person4() throws Throwable {
        //create entities
        PersonEntity entity = new PersonEntity(1L, "jim", "bob", "616-667-7656", "cell");
        Person4 person = new Person4();

        // create mapping
        Mapping mapping = Mapping.init()
                .map("first", "firstName")
                .map("last", "lastName")
                .map("phones.number", Phone1.class, "phoneNumber")
                .map("phones.isCell", Phone1.class, "phoneType", (value) -> value.equals("cell"));

        //do mapping
        mapping.commit(entity, person);

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
        // create mapping
        Mapping mapping = Mapping.init()
                .map("itemId", "id", Object::toString)
                .map("productName", "product_name")
                .map("price", "price", (value) -> BigDecimal.valueOf((double) value))
                .map("quantity", "quantity", (value) -> BigInteger.valueOf((int) value));

        // retrieve fields and their types
        ClassDef fromDef = classDef(ProductEntity.class);
        ClassDef toDef = classDef(Product.class);

        //create entities
        UUID uuid = UUID.randomUUID();
        ProductEntity entity = new ProductEntity(uuid, "kimbo", 10.20, 100);
        Product product = new Product();

        //do mapping
        Mappings.mapAToB(entity, fromDef, product, toDef, mapping);

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
        Mapping mapping = Mapping.init()
                .map("orderId", "id", Object::toString)
                .map("orderItems", ProductEntity.class, "products", Product.class, Mapping.init()
                        .map("itemId", "id", Object::toString)
                        .map("productName", "product_name")
                        .map("price", "price", (value) -> BigDecimal.valueOf((double) value))
                        .map("quantity", "quantity", (value) -> BigInteger.valueOf((int) value)));

        //do mapping
        mapping.commit(entity, order);

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