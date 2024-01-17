package com.akilisha.mapper.dto.entity;

import com.akilisha.mapper.definition.MapperDest;
import com.akilisha.mapper.definition.MapperSrc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person0 implements MapperDest, MapperSrc {

    Long id;
    String firstName;
    String lastName;
    String phone;
    String type;
}
