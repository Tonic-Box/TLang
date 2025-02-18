package com.tonic.model;

import org.objectweb.asm.tree.InsnList;

import java.util.List;

/**
 * Stores info for a single method (user-defined or built-in).
 */
public class MethodInfo {

    public final String name;
    public final List<Type> paramTypes;
    public final Type returnType;
    public final InsnList instructions;
    public final String descriptor;
    public final List<ParameterInfo> params;  // Full parameter info, including default expressions

    public MethodInfo(
            String name,
            List<Type> paramTypes,
            Type returnType,
            InsnList instructions,
            String descriptor,
            List<ParameterInfo> params
    ) {
        this.name = name;
        this.paramTypes = paramTypes;
        this.returnType = returnType;
        this.instructions = instructions;
        this.descriptor = descriptor;
        this.params = params;
    }
}
