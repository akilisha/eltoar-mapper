package com.akilisha.mapper.model.ecomm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    UUID id;
    String product_name;
    Double price;
    int quantity;
}
