package com.akilisha.mapper.dto.entity;

import com.akilisha.mapper.asm.MapperSrc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity implements MapperSrc {

    UUID id;
    String product_name;
    Double price;
    int quantity;
}
