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
    String getter;
    String setter;
    Class<?> type;
    Object value;

    public static FieldDef define(String name, Class<?> type) {
        return define(name, type, null);
    }

    public static FieldDef define(String name, Class<?> type, Object value) {
        String method = String.format("%s%s", Character.toUpperCase(name.charAt(0)), name.substring(1));
        String getter = boolean.class == type ? "is" + method : "get" + method;
        String setter = "set" + method;
        return define(name, getter, setter, type, value);
    }

    public static FieldDef define(FieldName field, Class<?> type, Object value) {
        return define(field.name, field.getter, field.setter, type, value);
    }

    public static FieldDef define(String name, String getter, String setter, Class<?> type, Object value) {
        return new FieldDef(name, getter, setter, type, value);
    }
}
