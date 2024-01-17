package com.akilisha.mapper.dto.entity;

import com.akilisha.mapper.definition.MapperDest;
import com.akilisha.mapper.definition.MapperSrc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonEntity implements MapperSrc, MapperDest {

    Long id;
    String firstName;
    String lastName;
    String phoneNumber;
    String phoneType;
}
