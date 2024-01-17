package com.akilisha.mapper.dto.entity;

import com.akilisha.mapper.definition.MapperDest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements MapperDest {

    String orderId;
    List<Product> orderItems;
    Date randomDate;    //no equivalent in OrderEntity
}
