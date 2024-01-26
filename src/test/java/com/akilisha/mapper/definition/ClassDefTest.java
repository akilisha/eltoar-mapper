package com.akilisha.mapper.definition;

import com.akilisha.mapper.model.Person5;
import com.akilisha.mapper.model.movies.Actor;
import com.akilisha.mapper.model.movies.Movie;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;

import java.io.IOException;

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

    @Test
    void verify_can_handle_objects_that_extends_from_another_object() throws IOException {
        ClassDef def = new ClassDef(Person5.class);
        ClassReader cr = new ClassReader(Person5.class.getName());
        cr.accept(def, 0);

        assertThat(def.fields).hasSize(8);
    }

    @Test
    void verify_can_handle_objects_having_recursive_references() throws IOException {
        ClassDef def = ClassDefs.cached.get(Movie.class);

        assertThat(def.fields).hasSize(5);

        ClassDef def2 = new ClassDef(Actor.class);
        ClassReader cr2 = new ClassReader(Actor.class.getName());
        cr2.accept(def2, 0);

        assertThat(def2.fields).hasSize(10);
    }
}