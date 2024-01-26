package com.akilisha.mapper.model.docs;

import com.akilisha.mapper.merge.LRMapping;
import com.akilisha.mapper.model.Person4;
import com.akilisha.mapper.model.PersonEntity;
import com.akilisha.mapper.model.Phone1;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonEntityTest {

    Gson gson = new Gson();

    @Test
    void One_to_one_mapping_with_no_mismatch() throws Throwable {
        Tenant src = new Tenant(123L, "first", "customer", true, (short) 123, new Rental(234L, "1001", "michigan ave", "cheboygan", "MI", "56789"));

        String jsonStr = gson.toJson(src);
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> jsonMap = gson.fromJson(jsonStr, mapType);

        Tenant dest = new Tenant();
        LRMapping.init().merge(jsonMap, dest);

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
    void verify_that_expected_fields_in_entity_class_are_mapped_to_person4() throws Throwable {
        //create entities
        PersonEntity entity = new PersonEntity(1L, "jim", "bob", "616-667-7656", "cell");

        String jsonStr = gson.toJson(entity);
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> jsonMap = gson.fromJson(jsonStr, mapType);
        Person4 person = new Person4();

        // create mapping
        LRMapping.init()
                .map("firstName", "first")
                .map("lastName", "last")
                .map("phoneNumber", "phones.number", Phone1.class)
                .map("phoneType", "phones.isCell", Phone1.class, (value) -> value.equals("cell"))
                .merge(jsonMap, person);

        //perform assertions
        assertThat(person.getId()).isEqualTo(1L);
        assertThat(person.getFirst()).isEqualTo("jim");
        assertThat(person.getLast()).isEqualTo("bob");
        assertThat(person.getPhones()).isNotEmpty();
        Phone1[] phones = person.getPhones().toArray(Phone1[]::new);
        assertThat(phones[0].getNumber()).isEqualTo("616-667-7656");
        assertThat(phones[0].getIsCell()).isTrue();
    }
}
