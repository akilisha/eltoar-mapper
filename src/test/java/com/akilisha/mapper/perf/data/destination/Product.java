package com.akilisha.mapper.perf.data.destination;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {

    String description;
    boolean available;
    private BigDecimal price;
    private int quantity;
    private String name;
    private RefundPolicy refundPolicy;

    public Product() {
    }

    public Product(BigDecimal price, int quantity, String name, String description, boolean available, RefundPolicy refundPolicy) {
        this.price = price;
        this.quantity = quantity;
        this.name = name;
        this.description = description;
        this.available = available;
        this.refundPolicy = refundPolicy;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public RefundPolicy getRefundPolicy() {
        return refundPolicy;
    }

    public void setRefundPolicy(RefundPolicy refundPolicy) {
        this.refundPolicy = refundPolicy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o.getClass() == Product.class) {
            Product product =
                    (Product) o;
            return quantity == product.getQuantity() &&
                    available == product.isAvailable() &&
                    Objects.equals(price, product.getPrice()) &&
                    Objects.equals(name, product.getName()) &&
                    Objects.equals(description, product.getDescription()) &&
                    Objects.equals(refundPolicy, product.getRefundPolicy());
        }
        if (o.getClass() != getClass()) return false;
        Product product = (Product) o;
        return quantity == product.quantity &&
                available == product.available &&
                Objects.equals(price, product.price) &&
                Objects.equals(name, product.name) &&
                Objects.equals(description, product.description) &&
                Objects.equals(refundPolicy, product.refundPolicy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, quantity, name, description, available, refundPolicy);
    }
}
