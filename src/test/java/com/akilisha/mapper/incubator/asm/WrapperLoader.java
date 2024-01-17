package com.akilisha.mapper.incubator.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WrapperLoader extends ClassLoader {

    final String destPath = "build/classes/java/test";
    ClassWriter cw;
    boolean writeOut;

    public WrapperLoader(ClassWriter cw, boolean writeOut) {
        super();
        this.cw = cw;
        this.writeOut = writeOut;
    }

    public static Class<?> generate(Class<?> target) throws IOException, ClassNotFoundException {
        ClassWriter cw = new ClassWriter(0);
        WrapperLoader loader = new WrapperLoader(cw, true);
        WrapperGen wg = new WrapperGen(cw, target, loader);
        TraceClassVisitor tcv = new TraceClassVisitor(wg, new PrintWriter(System.out));
        CheckClassAdapter cv = new CheckClassAdapter(tcv);
        ClassReader cr = new ClassReader(target.getName());
        cr.accept(cv, 0);

        //return generated class
        String targetClassName = String.format("%s__Wrapper", target.getName());
        return loader.findClass(targetClassName);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String classPath = name.replace(".", "/");
        try {
            if (writeOut) {
                Path path = Paths.get(System.getProperty("user.dir"), destPath, classPath + ".class");
                File file = path.toFile();
                boolean madeDirs = file.getParentFile().mkdirs();
                System.out.println("created dirs? " + madeDirs);
                boolean createdFile = file.createNewFile();
                System.out.println("created file? " + createdFile);
                try (FileOutputStream out = new FileOutputStream(file)) {
                    out.write(cw.toByteArray());
                }
            }
            byte[] b = cw.toByteArray();
            return defineClass(name, b, 0, b.length);
        } catch (IOException e) {
            return super.findClass(name);
        }
    }
}
