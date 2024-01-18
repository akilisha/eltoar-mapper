package com.akilisha.mapper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonEntity {

    Long id;
    String firstName;
    String lastName;
    String phoneNumber;
    String phoneType;
}
