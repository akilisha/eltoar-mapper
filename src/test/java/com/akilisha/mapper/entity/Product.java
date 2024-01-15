package com.akilisha.mapper.entity;

import com.akilisha.mapper.meta.Mappable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Mappable {

    String itemId;
    String productName;
    BigDecimal price;
    BigInteger quantity;
}
