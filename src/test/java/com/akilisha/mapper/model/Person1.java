package com.akilisha.mapper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person1 {

    Long id;
    String firstName;
    String lastName;
    Phone0 phone;
}
