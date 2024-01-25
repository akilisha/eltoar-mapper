package com.akilisha.mapper.model.carshop;

public class Truck extends Motorized {

    public Truck(String make, String model, String trim, Engine engine, Drivetrain trany, Price price) {
        super(make, model, trim, engine, trany, price);
    }

    public Truck() {
        super();
    }
}
