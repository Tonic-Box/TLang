package com.tonic.codegen;

import com.tonic.annotations.DefaultArg;
import com.tonic.model.ParameterInfo;
import com.tonic.model.antlr.TLangLexer;
import com.tonic.model.antlr.TLangParser;
import com.tonic.model.MethodInfo;
import com.tonic.model.Type;
import com.tonic.utill.ClassUtil;
import org.antlr.v4.runtime.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class TLangCompiler {
    private final List<MethodInfo> methods = new ArrayList<>();

    public TLangCompiler() {
        // Register built-in methods at construction.
        registerLibraryClasses();
    }

    public byte[] compile(String className, String source) {
        CharStream input = CharStreams.fromString(source);
        TLangLexer lexer = new TLangLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TLangParser parser = new TLangParser(tokens);

        BytecodeVisitor visitor = new BytecodeVisitor(methods, className);
        parser.program().accept(visitor);

        visitor.getBytecodeBuilder().returnVoid();

        Set<String> used = removeUnusedMethods(className, visitor.getBytecodeBuilder());

        return generateClass(visitor.getBytecodeBuilder(), className, used);
    }

    private byte[] generateClass(BytecodeBuilder mainBB, String className, Set<String> used) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", null);

        // Create main method.
        MethodVisitor mv = cw.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null,
                null
        );
        mv.visitCode();
        mainBB.build().accept(mv);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // Add all user-defined methods that are reachable.
        for (MethodInfo mi : methods) {
            if (mi.instructions.size() == 0 || !used.contains(mi.name + mi.descriptor)) {
                continue;
            }
            MethodVisitor mvm = cw.visitMethod(
                    Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                    mi.name,
                    mi.descriptor,
                    null,
                    null
            );
            mvm.visitCode();
            mi.instructions.accept(mvm);
            mvm.visitMaxs(0, 0);
            mvm.visitEnd();
        }

        List<Class<?>> libraries = ClassUtil.getClassesInPackage("com.tonic.lib");
        for(Class<?> clazz : libraries)
        {
            try {
                BytecodeInjector.injectStaticMethodsFrom(cw, clazz, used);
            } catch (IOException e) {
                throw new RuntimeException("Failed to inject built-in methods", e);
            }
        }

        cw.visitEnd();
        return cw.toByteArray();
    }

    private void registerLibraryClasses()
    {
        List<Class<?>> libraryClasses = ClassUtil.getClassesInPackage("com.tonic.lib");
        for(Class<?> library : libraryClasses)
        {
            registerBuiltInMethods(library);
        }
    }

    private void registerBuiltInMethods(Class<?> library) {
        Method[] all = library.getDeclaredMethods();
        for (Method m : all) {
            int mods = m.getModifiers();
            if (Modifier.isStatic(mods) && Modifier.isPublic(mods)) {
                String methodName = m.getName();
                Class<?>[] paramClasses = m.getParameterTypes();

                // Prepare a list of ParameterInfo (with default expressions if annotated)
                List<ParameterInfo> paramInfos = new ArrayList<>();
                Annotation[][] paramAnnotations = m.getParameterAnnotations();
                // Use Java reflection to get parameter names (if available) or use a placeholder.
                java.lang.reflect.Parameter[] parameters = m.getParameters();

                for (int i = 0; i < paramClasses.length; i++) {
                    // Convert the Java type to our TLang type.
                    Type t = toTLangType(paramClasses[i]);
                    // Use the reflection name (or a default if not available).
                    String paramName = parameters[i].getName();

                    // Check for a DefaultArg annotation.
                    TLangParser.ExpressionContext defaultExpr = null;
                    for (Annotation ann : paramAnnotations[i]) {
                        if (ann instanceof DefaultArg) {
                            String defaultSnippet = ((DefaultArg) ann).value();
                            // Parse the snippet into a TLang expression.
                            CharStream input = CharStreams.fromString(defaultSnippet);
                            TLangLexer lexer = new TLangLexer(input);
                            CommonTokenStream tokens = new CommonTokenStream(lexer);
                            TLangParser parser = new TLangParser(tokens);
                            defaultExpr = parser.expression();
                            break;  // assume only one default per parameter
                        }
                    }
                    paramInfos.add(new ParameterInfo(t, paramName, defaultExpr));
                }

                // Build our list of parameter types from the ParameterInfo objects.
                List<Type> paramTypes = new ArrayList<>();
                for (ParameterInfo pi : paramInfos) {
                    paramTypes.add(pi.type);
                }

                Class<?> retClass = m.getReturnType();
                Type returnType = toTLangType(retClass);
                String descriptor = computeDescriptor(paramTypes, returnType);
                methods.add(new MethodInfo(
                        methodName,
                        paramTypes,
                        returnType,
                        new InsnList(),
                        descriptor,
                        paramInfos   // now with default expression info!
                ));
            }
        }
    }


    /**
     * Converts a Java class to our TLang Type.
     * This method also handles arrays by counting dimensions.
     */
    private Type toTLangType(Class<?> c) {
        int dims = 0;
        while (c.isArray()) {
            dims++;
            c = c.getComponentType();
        }
        if (c == void.class) {
            if (dims != 0)
                throw new RuntimeException("void cannot be an array type.");
            return Type.VOID;
        } else if (c == int.class) {
            return new Type(Type.BaseType.INT, dims);
        } else if (c == boolean.class) {
            return new Type(Type.BaseType.BOOL, dims);
        } else if (c == String.class) {
            return new Type(Type.BaseType.STRING, dims);
        }
        throw new RuntimeException("Unsupported type: " + c);
    }

    /**
     * Computes a JVM descriptor string for the given parameter types and return type.
     * For example, if paramTypes = [INT, STRING] and returnType = INT,
     * the result is: "(ILjava/lang/String;)I"
     */
    private String computeDescriptor(List<Type> paramTypes, Type returnType) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Type t : paramTypes) {
            sb.append(toJVMType(t));
        }
        sb.append(")");
        if (returnType == null || returnType.equals(Type.VOID)) {
            sb.append("V");
        } else {
            sb.append(toJVMType(returnType));
        }
        return sb.toString();
    }

    /**
     * Converts a single Type to its corresponding JVM descriptor snippet.
     * For example, an int becomes "I", a string becomes "Ljava/lang/String;",
     * and arrays are built based on the number of dimensions.
     */
    private String toJVMType(Type t) {
        StringBuilder sb = new StringBuilder();
        // For each array dimension, prepend a '['
        for (int i = 0; i < t.dimensions; i++) {
            sb.append("[");
        }
        // Append the base type descriptor.
        switch (t.base) {
            case INT:
                sb.append("I");
                break;
            case BOOL:
                sb.append("Z");
                break;
            case STRING:
                sb.append("Ljava/lang/String;");
                break;
            case VOID:
                sb.append("V");
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + t);
        }
        return sb.toString();
    }

    /**
     * Performs reachability analysis starting from main.
     * Scans both the main method's bytecode and all methods, keeping only those
     * that are called (directly or indirectly) from main.
     */
    private Set<String> removeUnusedMethods(String className, BytecodeBuilder mainBB) {
        Set<String> used = new HashSet<>();
        Queue<MethodInfo> toScan = new ArrayDeque<>();

        // Scan the main method's instructions for method calls.
        for (AbstractInsnNode insn : mainBB.build().toArray()) {
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode min = (MethodInsnNode) insn;
                if (className.equals(min.owner)) {
                    MethodInfo mi = methods.stream()
                            .filter(m -> m.name.equals(min.name) && m.descriptor.equals(min.desc))
                            .findFirst()
                            .orElse(null);
                    if (mi != null) {
                        String key = mi.name + mi.descriptor;
                        if (used.add(key)) {
                            toScan.add(mi);
                        }
                    }
                }
            }
        }

        while (!toScan.isEmpty()) {
            MethodInfo target = toScan.poll();
            if (target == null) break;
            for (AbstractInsnNode insn : target.instructions.toArray()) {
                if (insn instanceof MethodInsnNode) {
                    MethodInsnNode min = (MethodInsnNode) insn;
                    if (className.equals(min.owner)) {
                        MethodInfo mi = methods.stream()
                                .filter(m -> m.name.equals(min.name) && m.descriptor.equals(min.desc))
                                .findFirst()
                                .orElse(null);
                        if (mi != null) {
                            String key = mi.name + mi.descriptor;
                            if (used.add(key)) {
                                toScan.add(mi);
                            }
                        }
                    }
                }
            }
        }

        return used;
    }
}
