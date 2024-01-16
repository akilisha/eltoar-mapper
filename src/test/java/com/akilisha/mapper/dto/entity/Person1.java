package com.akilisha.mapper.dto.entity;

import com.akilisha.mapper.asm.MapperDest;
import com.akilisha.mapper.asm.MapperSrc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person1 implements MapperDest, MapperSrc {

    Long id;
    String firstName;
    String lastName;
    Phone0 phone;
}
