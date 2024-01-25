package com.akilisha.mapper.model.carshop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Seller {

    String title;
    Set<Vehicle> bikers;
    List<Vehicle> trucks;
    Vehicle[] rentals;
    Map<Long, Vehicle> cars;
}
