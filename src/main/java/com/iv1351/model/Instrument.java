package com.iv1351.model;

import java.math.BigDecimal;

// model for instruments
public class Instrument {
    private final int id;
    private final String brand;
    private final BigDecimal rentalPrice;

    public Instrument(int id, String brand, BigDecimal rentalPrice) {
        this.id = id;
        this.brand = brand;
        this.rentalPrice = rentalPrice;
    }

    public int getId() {
        return this.id;
    }

    public String getBrand() {
        return this.brand;
    }

    public BigDecimal getRentalPrice() {
        return this.rentalPrice;
    }
}