package com.akilisha.mapper.dto.entity;

import com.akilisha.mapper.asm.MapperDest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person3 implements MapperDest {

    Long id;
    String firstName;
    String lastName;
    List<Phone0> phones;
}
