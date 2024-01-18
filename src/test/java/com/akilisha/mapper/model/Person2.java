package com.akilisha.mapper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person2 {

    Long id;
    String firstName;
    String lastName;
    String phoneNum;
    Boolean isCellPhone;
}
