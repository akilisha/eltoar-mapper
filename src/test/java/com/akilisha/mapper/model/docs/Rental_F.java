package com.akilisha.mapper.model.docs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rental_F {

    String id;
    String unitNumber;
    String streetName;
    CityInfo cityInfo;
}
