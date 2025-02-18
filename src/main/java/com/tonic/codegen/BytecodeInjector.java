package com.tonic.codegen;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class BytecodeInjector implements Opcodes {

    /**
     * Copy all public static methods from `libraryClass` into the ClassWriter `cw`.
     */
    public static void injectStaticMethodsFrom(ClassWriter cw, Class<?> libraryClass, Set<String> used) throws IOException {
        // Convert the class name -> resource path, e.g. "com/tonic/builtin/MyBuiltins.class"
        String resourceName = libraryClass.getName().replace('.', '/') + ".class";

        try (InputStream is = libraryClass.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new IOException("Could not find " + resourceName);
            }
            ClassReader cr = new ClassReader(is);
            ClassNode libraryNode = new ClassNode();
            cr.accept(libraryNode, 0);

            // For each method in the library class...
            for (MethodNode mn : libraryNode.methods) {
                // 1) Skip constructors, <clinit>, or non-static
                if (mn.name.equals("<init>") || mn.name.equals("<clinit>") || !used.contains(mn.name + mn.desc)) {
                    continue;
                }
                if ((mn.access & ACC_STATIC) == 0) {
                    // skip non-static
                    continue;
                }

                // 2) Create the corresponding method in the target
                //    We can keep the same name & descriptor, or rename if we want
                int newAccess = (mn.access & ~(ACC_PRIVATE | ACC_PROTECTED)) | ACC_PUBLIC | ACC_STATIC;
                MethodVisitor mv = cw.visitMethod(
                        newAccess,
                        mn.name,
                        mn.desc,
                        mn.signature,
                        (mn.exceptions == null) ? null : mn.exceptions.toArray(new String[0])
                );
                mv.visitCode();

                // 3) Let the MethodNode accept this new MethodVisitor, copying instructions
                mn.accept(mv);

                mv.visitEnd();
            }
        }
    }
}
