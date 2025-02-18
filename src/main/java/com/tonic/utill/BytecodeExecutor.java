package com.tonic.utill;

import java.lang.reflect.Method;

public class BytecodeExecutor {
    public static void execute(String className, byte[] bytecode) throws Exception {
        // Create custom class loader with the bytecode
        TLangClassLoader loader = new TLangClassLoader(className, bytecode);

        // Load the class (assuming class name is "Main")
        Class<?> loadedClass = loader.loadClass(className);

        // Get main method using reflection
        Method mainMethod = loadedClass.getDeclaredMethod("main", String[].class);

        // Invoke main method
        mainMethod.invoke(null, (Object) new String[0]);
    }

    private static class TLangClassLoader extends ClassLoader {
        private final byte[] bytecode;
        private final String className;

        public TLangClassLoader(String className, byte[] bytecode) {
            this.bytecode = bytecode;
            this.className = className; // Change if your class has different name
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.equals(className)) {
                return defineClass(className, bytecode, 0, bytecode.length);
            }
            return super.findClass(name);
        }
    }
}