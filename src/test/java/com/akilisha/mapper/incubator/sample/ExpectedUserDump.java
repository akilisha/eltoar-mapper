package com.akilisha.mapper.incubator.sample;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ExpectedUserDump implements Opcodes {

    public static byte[] dump() {

        ClassWriter classWriter = new ClassWriter(0);
        MethodVisitor methodVisitor;

        classWriter.visit(V21, ACC_PUBLIC | ACC_SUPER, "com/akilisha/mapper/incubator/sample/WrappedUser", "Lcom/akilisha/mapper/wrapper/DataWrapper<Lcom/akilisha/mapper/sample/User;>;", "com/akilisha/mapper/wrapper/DataWrapper", null);

        classWriter.visitSource("WrappedUser.java", null);

        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(Lcom/akilisha/mapper/sample/User;)V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(10, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/akilisha/mapper/wrapper/DataWrapper", "<init>", "(Ljava/lang/Object;)V", false);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(11, label1);
            methodVisitor.visitInsn(RETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", "Lcom/akilisha/mapper/sample/WrappedUser;", null, label0, label2, 0);
            methodVisitor.visitLocalVariable("target", "Lcom/akilisha/mapper/sample/User;", null, label0, label2, 1);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getId", "()Ljava/lang/Long;", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(14, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/WrappedUser", "getThisTarget", "()Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "com/akilisha/mapper/incubator/sample/User");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/User", "getId", "()Ljava/lang/Long;", false);
            methodVisitor.visitInsn(ARETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "Lcom/akilisha/mapper/sample/WrappedUser;", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setId", "(Ljava/lang/Long;)V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(18, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/WrappedUser", "getThisTarget", "()Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "com/akilisha/mapper/incubator/sample/User");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/User", "setId", "(Ljava/lang/Long;)V", false);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(19, label1);
            methodVisitor.visitInsn(RETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", "Lcom/akilisha/mapper/sample/WrappedUser;", null, label0, label2, 0);
            methodVisitor.visitLocalVariable("id", "Ljava/lang/Long;", null, label0, label2, 1);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getFirstName", "()Ljava/lang/String;", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(22, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/WrappedUser", "getThisTarget", "()Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "com/akilisha/mapper/incubator/sample/User");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/User", "getFirstName", "()Ljava/lang/String;", false);
            methodVisitor.visitInsn(ARETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "Lcom/akilisha/mapper/sample/WrappedUser;", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setFirstName", "(Ljava/lang/String;)V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(26, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/WrappedUser", "getThisTarget", "()Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "com/akilisha/mapper/incubator/sample/User");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/User", "setFirstName", "(Ljava/lang/String;)V", false);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(27, label1);
            methodVisitor.visitInsn(RETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", "Lcom/akilisha/mapper/sample/WrappedUser;", null, label0, label2, 0);
            methodVisitor.visitLocalVariable("firstName", "Ljava/lang/String;", null, label0, label2, 1);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getLastName", "()Ljava/lang/String;", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(30, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/WrappedUser", "getThisTarget", "()Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "com/akilisha/mapper/incubator/sample/User");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/User", "getLastName", "()Ljava/lang/String;", false);
            methodVisitor.visitInsn(ARETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "Lcom/akilisha/mapper/sample/WrappedUser;", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setLastName", "(Ljava/lang/String;)V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(34, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/WrappedUser", "getThisTarget", "()Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "com/akilisha/mapper/incubator/sample/User");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/User", "setLastName", "(Ljava/lang/String;)V", false);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(35, label1);
            methodVisitor.visitInsn(RETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", "Lcom/akilisha/mapper/sample/WrappedUser;", null, label0, label2, 0);
            methodVisitor.visitLocalVariable("lastName", "Ljava/lang/String;", null, label0, label2, 1);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getLuckyNumber", "()Ljava/math/BigDecimal;", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(38, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/WrappedUser", "getThisTarget", "()Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "com/akilisha/mapper/incubator/sample/User");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/User", "getLuckyNumber", "()Ljava/math/BigDecimal;", false);
            methodVisitor.visitInsn(ARETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "Lcom/akilisha/mapper/sample/WrappedUser;", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setLuckyNumber", "(Ljava/math/BigDecimal;)V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(42, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/WrappedUser", "getThisTarget", "()Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "com/akilisha/mapper/incubator/sample/User");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/akilisha/mapper/incubator/sample/User", "setLuckyNumber", "(Ljava/math/BigDecimal;)V", false);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(43, label1);
            methodVisitor.visitInsn(RETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", "Lcom/akilisha/mapper/sample/WrappedUser;", null, label0, label2, 0);
            methodVisitor.visitLocalVariable("luckyNumber", "Ljava/math/BigDecimal;", null, label0, label2, 1);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
