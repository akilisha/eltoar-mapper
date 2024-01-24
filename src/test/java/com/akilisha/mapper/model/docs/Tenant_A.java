package com.akilisha.mapper.model.docs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant_A {

    Long id;
    String first_name;
    String last_name;
    boolean accepted;
    short entry_code;
    Rental location;
}
