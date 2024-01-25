package com.akilisha.mapper.model.carshop;

public interface Vehicle {

    String getMake();

    void setMake(String make);

    String getModel();

    void setModel(String model);

    String getTrim();

    void setTrim(String trim);

    Engine getEngine();

    void setEngine(Engine engine);

    Drivetrain getTrany();

    void setTrany(Drivetrain trany);

    Price getPrice();

    void setPrice(Price price);
}
