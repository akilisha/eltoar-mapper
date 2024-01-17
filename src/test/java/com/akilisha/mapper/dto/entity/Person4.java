package com.akilisha.mapper.dto.entity;

import com.akilisha.mapper.definition.MapperDest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person4 implements MapperDest {

    Long id;
    String first;
    String last;
    List<Phone1> phones;
}
