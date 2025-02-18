package com.tonic.model;

public class Type {
    public enum BaseType { INT, BOOL, STRING, VOID }

    public final BaseType base;
    public final int dimensions;

    public Type(BaseType base, int dimensions) {
        this.base = base;
        this.dimensions = dimensions;
    }

    public static final Type INT    = new Type(BaseType.INT, 0);
    public static final Type BOOL   = new Type(BaseType.BOOL, 0);
    public static final Type STRING = new Type(BaseType.STRING, 0);
    public static final Type VOID   = new Type(BaseType.VOID, 0);

    /**
     * Returns a new Type representing an array of the given type
     * with the specified additional dimensions.
     */
    public static Type arrayOf(Type baseType, int additionalDims) {
        return new Type(baseType.base, baseType.dimensions + additionalDims);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Type)) return false;
        Type other = (Type) o;
        return this.base == other.base && this.dimensions == other.dimensions;
    }

    @Override
    public int hashCode() {
        return base.hashCode() * 31 + dimensions;
    }

    public String name()
    {
        return toString();
    }

    public Type getElementType() {
        if (dimensions == 0) {
            throw new RuntimeException("Not an array type.");
        }
        return new Type(base, dimensions - 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch(base) {
            case INT:    sb.append("int"); break;
            case BOOL:   sb.append("bool"); break;
            case STRING: sb.append("string"); break;
            case VOID:   sb.append("void"); break;
        }
        for (int i = 0; i < dimensions; i++) {
            sb.append("[]");
        }
        return sb.toString();
    }
}
