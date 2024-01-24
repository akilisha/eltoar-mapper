package com.akilisha.mapper.model.docs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant_E {

    Long id;
    NameInfo name;
    boolean accepted;
    short entryCode;
    Rental_E location;
}
