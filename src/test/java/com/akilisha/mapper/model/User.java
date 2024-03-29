package com.akilisha.mapper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    Long id;
    String names;
    BigDecimal salary;
    boolean accepted;
    short code;
}
