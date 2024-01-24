package com.akilisha.mapper.model.docs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant_D {

    Long id;
    String firstName;
    String lastName;
    boolean accepted;
    String code;
    Long unitId;
    String unitNumber;
    String streetName;
    String city;
    String state;
    String zip;
}
