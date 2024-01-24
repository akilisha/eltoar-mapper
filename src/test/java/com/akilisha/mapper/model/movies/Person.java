package com.akilisha.mapper.model.movies;

import lombok.*;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Person extends Bio {

    Gender gender;
    String homeCity;
    String homeState;

    public Person(Long id, String first, String last, Set<Phone> phones, Gender gender, String homeCity, String homeState, Map<Integer, Tour> roadTrip) {
        super(id, first, last, phones, roadTrip);
        this.gender = gender;
        this.homeCity = homeCity;
        this.homeState = homeState;
    }
}
