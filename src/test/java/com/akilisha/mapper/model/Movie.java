package com.akilisha.mapper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    String title;
    LocalDate date;
    Set<Actor> actors;
    float rating;
    Person5[] directors;
}
