package com.tonic.model;

import lombok.Getter;

import static org.objectweb.asm.Opcodes.*;

@Getter
public enum ConditionType {
    EQ(IFEQ, IF_ICMPEQ),      // Equal
    NE(IFNE, IF_ICMPNE),      // Not Equal
    LT(IFLT, IF_ICMPLT),      // Less Than
    LE(IFLE, IF_ICMPLE),      // Less Than or Equal
    GT(IFGT, IF_ICMPGT),      // Greater Than
    GE(IFGE, IF_ICMPGE);      // Greater Than or Equal

    private final int opcode;
    private final int icmpOpcode;

    ConditionType(int opcode, int icmpOpcode) {
        this.opcode = opcode;
        this.icmpOpcode = icmpOpcode;
    }

    public ConditionType invert() {
        switch (this) {
            case EQ:
                return NE;
            case NE:
                return EQ;
            case LT:
                return GE;
            case LE:
                return GT;
            case GT:
                return LE;
            case GE:
                return LT;
            default:
                throw new IllegalArgumentException();
        }
    }
}