package com.akilisha.mapper.model.ecomm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    String orderId;
    List<Product> orderItems;
    Date randomDate;    //no equivalent in OrderEntity
}
