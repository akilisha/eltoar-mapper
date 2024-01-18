package com.akilisha.mapper.incubator.poet;

import com.akilisha.mapper.model.User;
import org.objectweb.asm.ClassReader;

import java.io.IOException;

public class WrapperLoader {

    final String destPath = "generated/main/java";
    boolean writeOut;

    public WrapperLoader(boolean writeOut) {
        super();
        this.writeOut = writeOut;
    }

    public static void main(String[] args) throws IOException {
        Class<?> target = User.class;
        WrapperLoader loader = new WrapperLoader(true);
        WrapperGen gen = new WrapperGen(target, loader.destPath, true);
        ClassReader cr = new ClassReader(target.getName());
        cr.accept(gen, 0);
    }
}
