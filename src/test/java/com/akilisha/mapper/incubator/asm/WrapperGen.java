package com.akilisha.mapper.incubator.asm;

import org.objectweb.asm.*;

import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class WrapperGen extends ClassVisitor {

    static final Map<String, Integer> primitives = Map.of(
            boolean.class.getSimpleName(), IRETURN,
            char.class.getSimpleName(), IRETURN,
            byte.class.getSimpleName(), IRETURN,
            short.class.getSimpleName(), IRETURN,
            int.class.getSimpleName(), IRETURN,
            long.class.getSimpleName(), LRETURN,
            float.class.getSimpleName(), FRETURN,
            double.class.getSimpleName(), DRETURN);
    final ClassWriter cw;
    final Class<?> genericClass;
    final String genericClassName;
    final String genericClassDescriptor;
    final String targetClassName;
    final String targetClassDescriptor;
    final WrapperLoader wrapperLoader;
    final String dataWrapperName;

    int lineNum = 13;

    public WrapperGen(ClassWriter cw, Class<?> genericClass, WrapperLoader wrapperLoader) {
        super(ASM9);
        this.cw = cw;
        this.genericClass = genericClass;
        this.genericClassName = genericClass.getName().replace(".", "/");
        this.genericClassDescriptor = String.format("L%s;", this.genericClassName);
        this.targetClassName = String.format("%s__Wrapper", genericClass.getName().replace(".", "/"));
        this.targetClassDescriptor = String.format("L%s;", this.targetClassName);
        this.wrapperLoader = wrapperLoader;
        this.dataWrapperName = null; //TODO this needs to be revisited
    }

    public static int returnSymbol(String desc) {
        String clsName = Type.getType(desc).getReturnType().getClassName();
        return primitives.getOrDefault(clsName, ARETURN);
    }

    public static int loadSymbol(String desc) {
        String clsName = Type.getType(desc).getArgumentTypes()[0].getClassName();
        return primitives.getOrDefault(clsName, ALOAD);
    }

    public static int getterMax(String desc) {
        String clsName = Type.getType(desc).getReturnType().getClassName();
        return List.of("double", "long", "java.lang.Double", "java.lang.Long").contains(clsName) ? 2 : 1;
    }

    public static int setterMax(String desc) {
        String clsName = Type.getType(desc).getArgumentTypes()[0].getClassName();
        return List.of("double", "long", "java.lang.Double", "java.lang.Long").contains(clsName) ? 3 : 2;
    }

    @Override
    public void visitSource(String source, String debug) {
        super.visitSource(String.format("%s__Wrapper.java", genericClass.getSimpleName()), debug);

        //create constructor
        MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC, "<init>", String.format("(%s)V", this.genericClassDescriptor), null, null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(9, label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, this.dataWrapperName, "<init>", "(Ljava/lang/Object;)V", false);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLineNumber(10, label1);
        methodVisitor.visitInsn(RETURN);
        Label label2 = new Label();
        methodVisitor.visitLabel(label2);
        methodVisitor.visitLocalVariable("this", this.targetClassDescriptor, null, label0, label2, 0);
        methodVisitor.visitLocalVariable("target", this.genericClassDescriptor, null, label0, label2, 1);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        cw.visit(version,
                access,
                this.targetClassName,
                String.format("L%s<L%s;>;", this.dataWrapperName, name),
                this.dataWrapperName,
                interfaces);
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (access == ACC_PUBLIC) {
            if (name.matches("(get|is).*")) {
                MethodVisitor mv = cw.visitMethod(access, name, desc, signature, exceptions);
                mv.visitCode();
                //delegate call to the target object 'getThisTarget().get*'
                addGetter(mv, name, desc, signature);
                mv.visitEnd();
                return mv;
            }
            if (name.matches("(set).*")) {
                MethodVisitor mv = cw.visitMethod(access, name, desc, signature, exceptions);
                mv.visitCode();
                //delegate call to the target object 'getThisTarget().set*'
                addSetter(mv, name, desc, signature);
                mv.visitEnd();
                return mv;
            }
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    public void addGetter(MethodVisitor methodVisitor, String name, String desc, String signature) {
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(lineNum, label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, "getThisTarget", "()Ljava/lang/Object;", false);
        methodVisitor.visitTypeInsn(CHECKCAST, genericClassName);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, genericClassName, name, desc, false);
        methodVisitor.visitInsn(returnSymbol(desc));
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("this", targetClassDescriptor, signature, label0, label1, 0);
        methodVisitor.visitMaxs(getterMax(desc), 1);
        lineNum += 4;
    }

    public void addSetter(MethodVisitor methodVisitor, String name, String desc, String signature) {
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(lineNum, label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, "getThisTarget", "()Ljava/lang/Object;", false);
        methodVisitor.visitTypeInsn(CHECKCAST, genericClassName);
        methodVisitor.visitVarInsn(loadSymbol(desc), 1);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, genericClassName, name, desc, false);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        lineNum += 1;
        methodVisitor.visitLineNumber(lineNum, label1);
        methodVisitor.visitInsn(RETURN);
        Label label2 = new Label();
        methodVisitor.visitLabel(label2);
        methodVisitor.visitLocalVariable("this", targetClassDescriptor, null, label0, label2, 0);
        methodVisitor.visitLocalVariable(name.substring("set".length()).toLowerCase(), Type.getType(desc).getArgumentTypes()[0].getDescriptor(), signature, label0, label2, 1);
        methodVisitor.visitMaxs(setterMax(desc), setterMax(desc));
        lineNum += 3;
    }

    public void visitEnd() {
        cw.visitEnd();
    }
}
