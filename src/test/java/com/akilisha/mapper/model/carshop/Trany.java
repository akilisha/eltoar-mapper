package com.akilisha.mapper.model.carshop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trany implements Drivetrain {

    BigInteger torque;
    int gears;
    boolean awd;
}
