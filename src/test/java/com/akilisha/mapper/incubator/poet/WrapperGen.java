package com.akilisha.mapper.incubator.poet;

import com.akilisha.mapper.wrapper.ObjWrapper;
import com.squareup.javapoet.*;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.akilisha.mapper.definition.ClassDef.detectType;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ASM4;

public class WrapperGen extends ClassVisitor {

    final Path destPath;
    final Boolean writeToDisk;
    final Class<?> genericClassType;
    JavaFile javaFile;
    MethodSpec constructor;
    Map<String, MethodSpec> accessors = new HashMap<>();
    String genericClassName;
    String wrapperClassName;
    String packageName;

    public WrapperGen(Class<?> genericClassType, String writePath, Boolean writeToDisk) {
        super(ASM4);
        this.genericClassType = genericClassType;
        this.destPath = Path.of(System.getProperty("user.dir"), writePath);
        this.writeToDisk = writeToDisk;
    }

    public static Type returnType(String desc) {
        String pattern = desc.replaceAll("\\(.*?\\)(.+)$", "$1");
        return detectType(pattern);
    }

    public static Type paramType(String desc) {
        String pattern = desc.replaceAll("\\((.*)?\\).+$", "$1");
        return detectType(pattern);
    }

    @Override
    public void visitSource(String source, String debug) {
        super.visitSource(source, debug);
        //create constructor
        this.constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(this.genericClassType, "target")
                .addStatement("super(target)")
                .build();
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        //define class file
        this.genericClassName = name.replace("/", ".");
        this.wrapperClassName = String.format("%s__Wrapper", this.genericClassType.getSimpleName());
        this.packageName = this.genericClassName.substring(0, this.genericClassName.lastIndexOf("."));
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (access == ACC_PUBLIC) {
            if (name.matches("(get|is).*")) {
                addGetter(name, desc);
            }
            if (name.matches("(set).*")) {
                addSetter(name, desc);
            }
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    public void addGetter(String name, String desc) {
        Type type = returnType(desc);
        MethodSpec getter = MethodSpec.methodBuilder(name)
                .returns(type)
                .addStatement("return this.getThisTarget()." + name + "()")
                .build();
        this.accessors.put(name, getter);
    }

    public void addSetter(String name, String desc) {
        String paramName = name.substring("set".length()).toLowerCase();
        ParameterSpec setterParam = ParameterSpec.builder(paramType(desc), paramName).build();
        MethodSpec setter = MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(setterParam)
                .addStatement("this.getThisTarget()." + name + "(" + paramName + ")")
                .build();
        this.accessors.put(name, setter);
    }

    public void visitEnd() {
        super.visitEnd();

        ParameterizedTypeName generic = ParameterizedTypeName.get(ClassName.get(ObjWrapper.class),
                ClassName.get(this.genericClassType));

        TypeSpec classBody = TypeSpec.classBuilder(this.wrapperClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(this.constructor)
                .addMethods(this.accessors.values())
                .superclass(generic)
                .build();

        this.javaFile = JavaFile.builder(this.packageName, classBody)
                .build();

        //output the generated content
        try {
            if (this.writeToDisk) {
                javaFile.writeTo(destPath);
            } else {
                javaFile.writeTo(System.out);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
