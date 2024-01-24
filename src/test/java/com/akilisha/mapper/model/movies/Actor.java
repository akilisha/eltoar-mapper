package com.akilisha.mapper.model.movies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Actor extends Person {

    Set<Movie> otherMovies;
    Hobby[] hobbies;

    public Actor(Long id, String firstName, String lastName, Set<Phone> phones, Gender gender, String homeCity, String homeState, Map<Integer, Tour> roadTrip, Set<Movie> otherMovies, Hobby[] hobbies) {
        super(id, firstName, lastName, phones, gender, homeCity, homeState, roadTrip);
        this.otherMovies = otherMovies;
        this.hobbies = hobbies;
    }
}
