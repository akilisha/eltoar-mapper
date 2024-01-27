package com.akilisha.mapper.perf.data.destination;

import java.math.BigDecimal;
import java.util.Objects;

public class Discount {
    private String startTime;
    private String endTime;
    private BigDecimal discountPrice;

    public Discount() {
    }

    public Discount(String startTime, String endTime, BigDecimal discountPrice) {

        this.startTime = startTime;
        this.endTime = endTime;
        this.discountPrice = discountPrice;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o.getClass() == Discount.class) {
            Discount discount = (Discount) o;
            return Objects.equals(startTime, discount.getStartTime()) &&
                    Objects.equals(endTime, discount.getEndTime()) &&
                    Objects.equals(discountPrice, discount.getDiscountPrice());
        }
        if (o.getClass() != getClass()) return false;
        Discount discount = (Discount) o;
        return Objects.equals(startTime, discount.startTime) &&
                Objects.equals(endTime, discount.endTime) &&
                Objects.equals(discountPrice, discount.discountPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, discountPrice);
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }
}
