package com.akilisha.mapper.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldDef {

    String name;
    Class<?> type;
    Object value;

    public static FieldDef define(String name, Class<?> type) {
        return define(name, type, null);
    }

    public static FieldDef define(String name, Class<?> type, Object value) {
        return new FieldDef(name, type, value);
    }
}
