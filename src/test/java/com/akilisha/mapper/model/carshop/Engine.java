package com.akilisha.mapper.model.carshop;

import java.math.BigDecimal;

public interface Engine {

    float getCapacity();

    void setCapacity(float capacity);

    int getCylinders();

    void setCylinders(int cylinders);

    BigDecimal getHorsepower();

    void setHorsepower(BigDecimal hp);

}
