package com.akilisha.mapper.dto.entity;

import com.akilisha.mapper.asm.MapperDest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone1 implements MapperDest {

    Boolean isCell;
    String number;
}
