package com.akilisha.mapper.entity;

import com.akilisha.mapper.meta.Mappable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone1 implements Mappable {

    Boolean isCell;
    String number;
}
