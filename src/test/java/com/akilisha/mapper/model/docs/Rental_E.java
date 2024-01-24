package com.akilisha.mapper.model.docs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rental_E {

    Long id;
    String unitNumber;
    String streetName;
    CityInfo cityInfo;
}
