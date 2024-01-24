package com.akilisha.mapper.model.docs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant_B {

    BigDecimal id;
    String firstName;
    String lastName;
    boolean accepted;
    String entryCode;
    Rental location;
}
