package com.tonic.utill;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class InsnUtil
{
    public static void insertStringCasts(InsnList insnList) {
        for (AbstractInsnNode node : insnList) {
            if (node instanceof LdcInsnNode) {
                LdcInsnNode ldc = (LdcInsnNode) node;
                if (!(ldc.cst instanceof String)) {
                    MethodInsnNode toStringCall = new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "java/lang/String",
                            "valueOf",
                            "(Ljava/lang/Object;)Ljava/lang/String;",
                            false
                    );
                    insnList.insert(ldc, toStringCall);
                }
            } else if (node instanceof IntInsnNode || node instanceof InsnNode) {
                int opcode = node.getOpcode();
                if ((opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5) ||
                        opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH ||
                        opcode == Opcodes.LDC) {
                    MethodInsnNode toStringCall = new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "java/lang/Integer",
                            "toString",
                            "(I)Ljava/lang/String;",
                            false
                    );
                    insnList.insert(node, toStringCall);
                }
            } else if (node instanceof VarInsnNode) {
                VarInsnNode varInsn = (VarInsnNode) node;
                if (varInsn.getOpcode() == Opcodes.ILOAD ||
                        varInsn.getOpcode() == Opcodes.LLOAD ||
                        varInsn.getOpcode() == Opcodes.FLOAD ||
                        varInsn.getOpcode() == Opcodes.DLOAD ||
                        varInsn.getOpcode() == Opcodes.ALOAD) {
                    MethodInsnNode toStringCall = generateToStringCast(varInsn);

                    insnList.insert(varInsn, toStringCall);
                }
            }
        }
    }

    private static MethodInsnNode generateToStringCast(VarInsnNode varInsn) {
        String owner, method, descriptor;
        switch (varInsn.getOpcode()) {
            case Opcodes.ILOAD:
                owner = "java/lang/Integer";
                method = "toString";
                descriptor = "(I)Ljava/lang/String;";
                break;
            case Opcodes.LLOAD:
                owner = "java/lang/Long";
                method = "toString";
                descriptor = "(J)Ljava/lang/String;";
                break;
            case Opcodes.FLOAD:
                owner = "java/lang/Float";
                method = "toString";
                descriptor = "(F)Ljava/lang/String;";
                break;
            case Opcodes.DLOAD:
                owner = "java/lang/Double";
                method = "toString";
                descriptor = "(D)Ljava/lang/String;";
                break;
            case Opcodes.ALOAD:
            default:
                owner = "java/lang/String";
                method = "valueOf";
                descriptor = "(Ljava/lang/Object;)Ljava/lang/String;";
                break;
        }

        return new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                owner,
                method,
                descriptor,
                false
        );
    }

    public static boolean isReturn(InsnNode node)
    {
        return node.getOpcode() >= Opcodes.IRETURN && node.getOpcode() <= Opcodes.RETURN;
    }
}
