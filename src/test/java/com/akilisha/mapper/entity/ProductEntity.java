package com.akilisha.mapper.entity;

import com.akilisha.mapper.meta.MVisitable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity implements MVisitable {

    UUID id;
    String product_name;
    Double price;
    int quantity;
}
