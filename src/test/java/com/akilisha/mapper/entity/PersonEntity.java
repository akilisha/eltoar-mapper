package com.akilisha.mapper.entity;

import com.akilisha.mapper.meta.MVisitable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonEntity implements MVisitable {

    Long id;
    String firstName;
    String lastName;
    String phoneNumber;
    String phoneType;
}
