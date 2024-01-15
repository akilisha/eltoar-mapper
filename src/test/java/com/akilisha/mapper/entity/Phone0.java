package com.akilisha.mapper.entity;

import com.akilisha.mapper.meta.Mappable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone0 implements Mappable {

    String type;
    String number;
}
