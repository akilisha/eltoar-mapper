package com.akilisha.mapper.perf;

import com.akilisha.mapper.perf.converter.Converter;
import com.akilisha.mapper.perf.converter.lib.EltoarConverter;
import com.akilisha.mapper.perf.data.destination.DestinationCode;
import com.akilisha.mapper.perf.data.destination.Order;
import com.akilisha.mapper.perf.data.source.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderTest {

    private SourceOrder sourceOrder = null;
    private SourceCode sourceCode = null;

    @BeforeEach
    void setUp() {
        User user = new User("John", "John@doe.com", AccountStatus.ACTIVE);
        RefundPolicy refundPolicy = new RefundPolicy(true, 30, Collections
                .singletonList("Refundable only if not used!"));

        Product product = new Product(BigDecimal.valueOf(10.99),
                100,
                "Sample Product",
                "Sample Product to be sold",
                true,
                refundPolicy
        );

        Discount discount = new Discount(Instant.now().toString(), Instant.now().toString(), BigDecimal.valueOf(5.99));
        Address deliveryAddress = new Address("Washington Street 5", "New York", "55045", "USA");
        DeliveryData deliveryData = new DeliveryData(deliveryAddress, true, "", 10);
        Address shopAddress = new Address("Roosvelt Street 9", "Boston", "55042", "USA");
        User reviewingUser = new User("John", "Johhny@John.com", AccountStatus.ACTIVE);
        User negativeReviewingUser = new User("Carl", "Carl@Coral.com", AccountStatus.ACTIVE);
        Review review = new Review(5, 5, 5, reviewingUser, "The best shop I've ever bought things in");

        Review negativeReview = new Review(1, 1, 1, negativeReviewingUser, "I will never buy anything again here!");

        List<Review> reviewList = new ArrayList<>();
        reviewList.add(review);
        reviewList.add(negativeReview);
        Shop shop = new Shop("Super Shop", shopAddress, "www.super-shop.com", reviewList);

        sourceOrder = new SourceOrder(OrderStatus.CONFIRMED,
                Instant.now().toString(),
                Instant.MAX.toString(),
                PaymentType.TRANSFER,
                discount,
                deliveryData,
                user,
                Collections.singletonList(product),
                shop,
                1
        );

        sourceCode = new SourceCode("This is source code!");
    }

    @Test
    void test_code_against_mapping_example_online() throws Throwable {
        Converter converter = new EltoarConverter();
        DestinationCode order = converter.convert(sourceCode);

        assertThat(order).isNotNull();
    }

    @Test
    @Disabled
    void test_order_against_mapping_example_online() throws Throwable {
        Converter converter = new EltoarConverter();
        Order order = converter.convert(sourceOrder);

        assertThat(order).isNotNull();
    }
}
