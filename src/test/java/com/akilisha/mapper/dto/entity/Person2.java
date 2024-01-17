package com.akilisha.mapper.dto.entity;

import com.akilisha.mapper.definition.MapperDest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person2 implements MapperDest {

    Long id;
    String firstName;
    String lastName;
    String phoneNum;
    Boolean isCellPhone;
}
