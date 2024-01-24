package com.akilisha.mapper.model.movies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bio {

    Long id;
    String first;
    String last;
    Set<Phone> phones;
    Map<Integer, Tour> roadTrip;
}
