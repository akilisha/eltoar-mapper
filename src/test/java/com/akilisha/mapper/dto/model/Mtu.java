package com.akilisha.mapper.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mtu {

    long id;
    String majina;
    BigDecimal mshahara;
    boolean pamoja;
    short siri;
}
