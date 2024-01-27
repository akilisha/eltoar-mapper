package com.akilisha.mapper.perf.data.destination;

import com.akilisha.mapper.perf.data.source.SourceOrder;

import java.util.List;
import java.util.Objects;

public class Order {

    private User orderingUser;
    private List<Product> orderedProducts;
    private OrderStatus orderStatus;
    private String orderDate;
    private String orderFinishDate;
    private PaymentType paymentType;
    private Discount discount;
    private int orderId;
    private DeliveryData deliveryData;
    private Shop offeringShop;

    public Order() {
    }

    public Order(User orderingUser, List<Product> orderedProducts, OrderStatus orderStatus, String orderDate, String orderFinishDate, PaymentType paymentType, Discount discount, int orderId, DeliveryData deliveryData, Shop offeringShop) {

        this.orderingUser = orderingUser;
        this.orderedProducts = orderedProducts;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.orderFinishDate = orderFinishDate;
        this.paymentType = paymentType;
        this.discount = discount;
        this.orderId = orderId;
        this.deliveryData = deliveryData;
        this.offeringShop = offeringShop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o.getClass() == SourceOrder.class) {
            SourceOrder order =
                    (SourceOrder) o;
            return Objects.equals(orderingUser, order.getOrderingUser()) &&
                    Objects.equals(orderedProducts, order.getOrderedProducts()) &&
                    orderStatus.ordinal() == order.getStatus().ordinal() &&
                    Objects.equals(orderDate, order.getOrderDate()) &&
                    Objects.equals(orderFinishDate, order.getOrderFinishDate()) &&
                    paymentType.ordinal() == order.getPaymentType().ordinal() &&
                    Objects.equals(discount, order.getDiscount()) &&
                    Objects.equals(deliveryData, order.getDeliveryData());
        }
        if (o.getClass() != getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderingUser, order.orderingUser) &&
                Objects.equals(orderedProducts, order.orderedProducts) &&
                orderStatus == order.orderStatus &&
                Objects.equals(orderDate, order.orderDate) &&
                Objects.equals(orderFinishDate, order.orderFinishDate) &&
                paymentType == order.paymentType &&
                Objects.equals(discount, order.discount) &&
                Objects.equals(deliveryData, order.deliveryData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderingUser, orderedProducts, orderStatus, orderDate, orderFinishDate, paymentType, discount, deliveryData);
    }

    public User getOrderingUser() {
        return orderingUser;
    }

    public void setOrderingUser(User orderingUser) {
        this.orderingUser = orderingUser;
    }

    public List<Product> getOrderedProducts() {
        return orderedProducts;
    }

    public void setOrderedProducts(List<Product> orderedProducts) {
        this.orderedProducts = orderedProducts;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus status) {
        this.orderStatus = status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderFinishDate() {
        return orderFinishDate;
    }

    public void setOrderFinishDate(String orderFinishDate) {
        this.orderFinishDate = orderFinishDate;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public DeliveryData getDeliveryData() {
        return deliveryData;
    }

    public void setDeliveryData(DeliveryData deliveryData) {
        this.deliveryData = deliveryData;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Shop getOfferingShop() {
        return offeringShop;
    }

    public void setOfferingShop(Shop offeringShop) {
        this.offeringShop = offeringShop;
    }

    public OrderStatus conversion(OrderStatus status) {
        OrderStatus orderStatus = null;
        switch (status) {
            case CREATED:
                orderStatus = OrderStatus.CREATED;
                break;
            case FINISHED:
                orderStatus = OrderStatus.FINISHED;
                break;

            case CONFIRMED:
                orderStatus = OrderStatus.CONFIRMED;
                break;

            case COLLECTING:
                orderStatus = OrderStatus.COLLECTING;
                break;

            case IN_TRANSPORT:
                orderStatus = OrderStatus.IN_TRANSPORT;
                break;
        }
        return orderStatus;
    }

    public PaymentType conversion(PaymentType type) {
        PaymentType paymentType = null;
        switch (type) {
            case CARD:
                paymentType = PaymentType.CARD;
                break;

            case CASH:
                paymentType = PaymentType.CASH;
                break;

            case TRANSFER:
                paymentType = PaymentType.TRANSFER;
                break;
        }
        return paymentType;
    }
}
