package com.akilisha.mapper.model.ecomm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    UUID id;
    List<ProductEntity> products;
    String randomId;    //no equivalent in Order
}
