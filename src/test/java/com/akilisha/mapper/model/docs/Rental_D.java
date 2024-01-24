package com.akilisha.mapper.model.docs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rental_D {

    Long unitId;
    String unitNumber;
    String streetName;
    String city;
    String state;
    String zip;
}
