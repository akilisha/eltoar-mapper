package com.akilisha.mapper.model.docs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant_I {

    Long id;
    String firstName;
    String lastName;
    boolean accepted;
    short entryCode;
    Rental_D location;
}
