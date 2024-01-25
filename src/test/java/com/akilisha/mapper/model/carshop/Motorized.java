package com.akilisha.mapper.model.carshop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Motorized implements Vehicle {

    String make;
    String model;
    String trim;
    Engine engine;
    Drivetrain trany;
    Price price;
}
