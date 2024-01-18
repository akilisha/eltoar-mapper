package com.akilisha.mapper.definition;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClassDefTest {

    @Test
    void verify_that_type_are_detected_correctly() {
        String floatType = "F";
        Class<?> fType = ClassDef.detectType(floatType);
        assertThat(fType.getName()).isEqualTo("float");

        String objectType = "Ljava/lang/Object;";
        Class<?> oType = ClassDef.detectType(objectType);
        assertThat(oType.getName()).isEqualTo("java.lang.Object");

        String intArrayType = "[I";
        Class<?> iaType = ClassDef.detectType(intArrayType);
        assertThat(iaType.getName()).isEqualTo("[I");

        String object3dArrayType = "[[Ljava/lang/Object;";
        Class<?> o3daType = ClassDef.detectType(object3dArrayType);
        assertThat(o3daType.getName()).isEqualTo("[[Ljava.lang.Object;");
    }
}