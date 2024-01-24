package com.akilisha.mapper.model.company;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    private int id;
    private String name;
    private boolean perm;
    private Address address;
    private Phone[] phones;
    private String role;
}
