package com.akilisha.mapper.model.docs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant_J {

    Long id;
    NameInfo name;
    boolean accepted;
    short entryCode;
    List<Rental> locations;
}
