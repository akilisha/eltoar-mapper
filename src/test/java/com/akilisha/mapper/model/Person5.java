package com.akilisha.mapper.model;

import com.akilisha.mapper.model.movies.Hobby;
import com.akilisha.mapper.model.movies.Tour;
import lombok.*;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Person5 extends Person4 {

    Hobby[] hobbies;
    String homeCity;
    String homeState;

    public Person5(Long id, String first, String last, Set<Phone1> phones, Hobby[] hobbies, String homeCity, String homeState, Map<Integer, Tour> roadTrip) {
        super(id, first, last, phones, roadTrip);
        this.hobbies = hobbies;
        this.homeCity = homeCity;
        this.homeState = homeState;
    }
}
