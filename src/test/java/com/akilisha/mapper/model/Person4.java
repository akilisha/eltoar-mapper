package com.akilisha.mapper.model;

import com.akilisha.mapper.model.movies.Tour;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person4 {

    Long id;
    String first;
    String last;
    Set<Phone1> phones;
    Map<Integer, Tour> roadTrip;
}
