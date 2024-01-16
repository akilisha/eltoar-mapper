package com.akilisha.mapper.dto.entity;

import com.akilisha.mapper.asm.MapperSrc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity implements MapperSrc {

    UUID id;
    List<ProductEntity> products;
    String randomId;    //no equivalent in Order
}
