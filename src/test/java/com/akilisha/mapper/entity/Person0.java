package com.akilisha.mapper.entity;

import com.akilisha.mapper.meta.Mappable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person0 implements Mappable {

    Long id;
    String firstName;
    String lastName;
    String phone;
    String type;
}
