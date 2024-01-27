package com.akilisha.mapper.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldName {

    String name;
    String setter;
    String getter;

    public static FieldName init() {
        return new FieldName();
    }

    public FieldName name(String name) {
        this.name = name;
        return this;
    }

    public FieldName getter(String getter) {
        this.getter = getter;
        return this;
    }

    public FieldName setter(String setter) {
        this.setter = setter;
        return this;
    }
}
