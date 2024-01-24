package com.akilisha.mapper.model.ecomm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    String itemId;
    String productName;
    BigDecimal price;
    BigInteger quantity;
}
