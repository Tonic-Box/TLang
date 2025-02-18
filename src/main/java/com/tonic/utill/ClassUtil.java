package com.tonic.utill;

import com.google.common.reflect.ClassPath;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ClassUtil {
    /**
     * Returns a list of all top-level classes in the specified package (and its subpackages).
     *
     * @param packageName the package name to scan, e.g. "com.mycompany.mypackage"
     * @return a list of Class<?> objects for every top-level class found in the package.
     */
    @SneakyThrows(IOException.class)
    public static List<Class<?>> getClassesInPackage(String packageName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        ClassPath classPath = ClassPath.from(loader);
        return classPath.getTopLevelClassesRecursive(packageName)
                .stream()
                .map(ClassPath.ClassInfo::load)
                .collect(Collectors.toList());
    }
}
