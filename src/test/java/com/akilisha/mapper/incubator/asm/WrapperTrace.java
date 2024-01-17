package com.akilisha.mapper.incubator.asm;

import com.akilisha.mapper.dto.model.Mtu;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;

public class WrapperTrace {

    public static void main(String[] args) throws IOException {
        generateSample();
    }

    public static void generateSample() throws IOException {
        ClassWriter cw = new ClassWriter(0);
        TraceClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
        ClassReader cr = new ClassReader(Mtu.class.getName());
        cr.accept(cv, 0);
        byte[] b = cw.toByteArray();
        System.out.println(b.length);
    }
}
