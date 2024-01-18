package com.akilisha.mapper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Actor extends Person1 {

    Gender gender;
    String homeCity;
    String homeState;
    Set<Movie> otherMovies;
    Hobby[] hobbies;

    public Actor(Long id, String firstName, String lastName, Phone0 phone, Gender gender, String homeCity, String homeState, Set<Movie> otherMovies, Hobby[] hobbies) {
        super(id, firstName, lastName, phone);
        this.gender = gender;
        this.homeCity = homeCity;
        this.homeState = homeState;
        this.otherMovies = otherMovies;
        this.hobbies = hobbies;
    }
}
