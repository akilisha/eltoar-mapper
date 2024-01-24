package com.akilisha.mapper.model.docs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant_C {

    BigDecimal guestNumber;
    String guestName;
    boolean accepted;
    String guestCode;
    Rental location;
}
