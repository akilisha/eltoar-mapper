package com.akilisha.mapper.model.docs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant_G {

    Long id;
    NameInfo name;
    boolean accepted;
    short entryCode;
    Set<Rental_E> locations;
}
