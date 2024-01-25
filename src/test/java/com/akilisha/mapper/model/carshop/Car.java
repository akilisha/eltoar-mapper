package com.akilisha.mapper.model.carshop;

public class Car extends Motorized {

    public Car(String make, String model, String trim, Engine engine, Drivetrain trany, Price price) {
        super(make, model, trim, engine, trany, price);
    }

    public Car() {
        super();
    }
}
