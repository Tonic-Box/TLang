package com.tonic.utill;

import org.objectweb.asm.*;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;

public class BytecodeInspector {

    /**
     * Pretty prints the bytecode of the main method found in the class byte array.
     *
     * @param classBytes the byte array representing the class
     * @return a String containing the pretty printed bytecode of the main method;
     *         if no main method is found, returns an empty string.
     */
    public static String prettyPrint(byte[] classBytes) {
        // A StringWriter to capture the output of the Textifier.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        // Create a ClassReader from the provided byte array.
        ClassReader classReader = new ClassReader(classBytes);

        // Use a flag to indicate if the main method was found.
        final boolean[] mainMethodFound = { false };

        // Create a ClassVisitor that intercepts the main method.
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    String[] exceptions) {
                // Get the default MethodVisitor.
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

                // Check if this is the main method.
                // The main method should be "public static void main(String[] args)",
                // which corresponds to the name "main" and the descriptor "([Ljava/lang/String;)V".
                if ("main".equals(name) && "([Ljava/lang/String;)V".equals(descriptor)) {
                    mainMethodFound[0] = true;

                    // Create a Textifier to convert bytecode into a human-readable form.
                    Textifier textifier = new Textifier();

                    // Wrap the Textifier with a TraceMethodVisitor.
                    TraceMethodVisitor tmv = new TraceMethodVisitor(textifier);

                    // Return a MethodVisitor that prints the bytecode when the method ends.
                    return new MethodVisitor(Opcodes.ASM9, tmv) {
                        @Override
                        public void visitEnd() {
                            // Complete the method visitation.
                            super.visitEnd();

                            // Print the textified bytecode into our PrintWriter.
                            textifier.print(pw);
                        }
                    };
                }
                return mv; // For other methods, simply delegate.
            }
        };

        // Accept the visitor to process the class.
        classReader.accept(cv, 0);
        pw.flush();

        // Return the pretty printed bytecode if the main method was found.
        return mainMethodFound[0] ? sw.toString() : "";
    }
}
