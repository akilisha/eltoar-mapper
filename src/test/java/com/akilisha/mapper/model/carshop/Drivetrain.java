package com.akilisha.mapper.model.carshop;

import java.math.BigInteger;

public interface Drivetrain {

    BigInteger getTorque();

    void setTorque(BigInteger torque);

    int getGears();

    void setGears(int gears);

    boolean isAwd();

    void setAwd(boolean awd);
}
