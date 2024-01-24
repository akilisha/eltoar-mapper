package com.akilisha.mapper.model.docs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rental {

    Long id;
    String unitNumber;
    String streetName;
    String city;
    String state;
    String zipCode;
}
