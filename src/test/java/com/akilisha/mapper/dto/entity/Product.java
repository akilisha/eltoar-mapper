package com.akilisha.mapper.dto.entity;

import com.akilisha.mapper.definition.MapperDest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements MapperDest {

    String itemId;
    String productName;
    BigDecimal price;
    BigInteger quantity;
}
