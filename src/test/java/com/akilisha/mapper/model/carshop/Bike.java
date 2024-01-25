package com.akilisha.mapper.model.carshop;

public class Bike extends Motorized {

    public Bike(String make, String model, String trim, Engine engine, Drivetrain trany, Price price) {
        super(make, model, trim, engine, trany, price);
    }

    public Bike() {
        super();
    }
}
