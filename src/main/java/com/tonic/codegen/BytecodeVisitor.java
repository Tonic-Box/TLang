package com.tonic.codegen;

import com.tonic.model.MethodInfo;
import com.tonic.model.ParameterInfo;
import com.tonic.model.antlr.TLangBaseVisitor;
import com.tonic.model.antlr.TLangParser;
import com.tonic.model.ConditionType;
import com.tonic.model.SymbolTable;
import com.tonic.model.Type;
import com.tonic.model.VariableInfo;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@SuppressWarnings({"ConstantConditions","ExtractMethod"})
public class BytecodeVisitor extends TLangBaseVisitor<Void> {
    private final Deque<LabelNode> breakLabels = new ArrayDeque<>();
    private final Deque<LabelNode> continueLabels = new ArrayDeque<>();
    private final BytecodeBuilder bb;
    private final SymbolTable symbolTable;
    private final Deque<Type> typeStack;
    private final List<MethodInfo> methods;
    private int nextLocalIndex;
    private Type expectedArrayType = null;
    private Type expectedReturnType = null;
    private final String className;

    // === Constructor for the main program ===
    public BytecodeVisitor(final List<MethodInfo> methods, String className) {
        this.bb = BytecodeBuilder.create();
        this.symbolTable = new SymbolTable();
        this.typeStack = new ArrayDeque<>();
        this.methods = methods;
        this.nextLocalIndex = 1;  // local index starts at 1
        this.className = className;
    }

    // === Constructor for method bodies ===
    // Pass in a custom BytecodeBuilder, plus a fresh SymbolTable, etc.
    public BytecodeVisitor(final List<MethodInfo> methods,
                           BytecodeBuilder bb,
                           SymbolTable symbolTable,
                           Deque<Type> typeStack,
                           int nextLocalIndex,
                           Type expectedReturnType,
                           String className
    ) {
        this.bb = bb;
        this.symbolTable = symbolTable;
        this.typeStack = typeStack;
        this.nextLocalIndex = nextLocalIndex;
        this.methods = methods;
        this.expectedReturnType = expectedReturnType;
        this.className = className;
    }

    public BytecodeBuilder getBytecodeBuilder() {
        return bb;
    }

    private Type toType(String typeStr) {
        // Remove all occurrences of "[]"
        String baseStr = typeStr.replaceAll("\\[\\]", "");
        // Each "[]" is 2 characters
        int dims = (typeStr.length() - baseStr.length()) / 2;
        switch (baseStr.toLowerCase()) {
            case "int":    return new Type(Type.BaseType.INT, dims);
            case "bool":   return new Type(Type.BaseType.BOOL, dims);
            case "string": return new Type(Type.BaseType.STRING, dims);
            case "void":
                if (dims > 0)
                    throw new RuntimeException("void cannot be an array type.");
                return Type.VOID;
            default:
                throw new IllegalArgumentException("Unknown type: " + baseStr);
        }
    }


    // ===== methods =====

    @Override
    public Void visitMethodDecl(TLangParser.MethodDeclContext ctx) {
        // Read the declared return type from the method signature.
        Type declaredReturnType = toType(ctx.type().getText());
        String methodName = ctx.ID().getText();

        // Parse parameters with potential default expressions.
        List<ParameterInfo> params = new ArrayList<>();
        TLangParser.ParamListContext pl = ctx.paramList();
        if (pl != null) {
            for (TLangParser.ParamContext pctx : pl.param()) {
                Type pt = toType(pctx.type().getText());
                String pname = pctx.ID().getText();
                TLangParser.ExpressionContext defaultExpr = null;
                // Check if a default value is provided.
                // The parse tree for a parameter with a default looks like:
                //  [type, ID, '=', expression]
                if (pctx.getChildCount() >= 4) {
                    defaultExpr = pctx.expression();
                }
                params.add(new ParameterInfo(pt, pname, defaultExpr));
            }
        }

        // Build a list of parameter types from the ParameterInfo objects.
        List<Type> paramTypes = new ArrayList<>();
        for (ParameterInfo pi : params) {
            paramTypes.add(pi.type);
        }

        // Create a separate BytecodeBuilder for this method.
        BytecodeBuilder methodBB = BytecodeBuilder.create();
        SymbolTable methodSym = new SymbolTable();
        Deque<Type> methodStack = new ArrayDeque<>();

        // Register parameters in the method's symbol table with local indices starting at 0.
        for (int i = 0; i < params.size(); i++) {
            ParameterInfo pi = params.get(i);
            methodSym.put(pi.name, new VariableInfo(pi.type.name().toLowerCase(), i));
        }
        int bodyLocalIndex = params.size();

        // Create a new BytecodeVisitor for the method body, passing the declared return type.
        BytecodeVisitor methodVisitor = new BytecodeVisitor(
                methods,
                methodBB,
                methodSym,
                methodStack,
                bodyLocalIndex,
                declaredReturnType,
                className
        );

        // Compile the method body (the block).
        methodVisitor.visit(ctx.block());

        // If the method is void and no explicit return was encountered, add a return.
        if (declaredReturnType == Type.VOID) {
            methodBB.returnVoid();
        }
        // For non-void methods, it is expected that an explicit return appears in the body.

        // Compute the method descriptor using paramTypes and the declared return type.
        String descriptor = toJVMDescriptor(paramTypes, declaredReturnType);

        // Create a MethodInfo instance that now also carries full parameter info.
        MethodInfo mi = new MethodInfo(
                methodName,
                paramTypes,
                declaredReturnType,
                methodBB.build(),
                descriptor,
                params
        );
        methods.add(mi);
        return null;
    }


    @Override
    public Void visitReturnStmt(TLangParser.ReturnStmtContext ctx) {
        // If there is a return expression, evaluate it.
        if (ctx.expression() != null) {
            ctx.expression().accept(this);
            Type exprType = typeStack.pop();
            if (!exprType.equals(expectedReturnType)) {
                throw new RuntimeException("Return type mismatch: expected " +
                        expectedReturnType + " but got " + exprType);
            }
            // Emit a return instruction based on the type.
            if (expectedReturnType.equals(Type.INT) || expectedReturnType.equals(Type.BOOL)) {
                bb.appendInsn(new InsnNode(Opcodes.IRETURN));
            } else {
                // For object types (string, arrays, etc.) and for void (if using return with no expr)
                bb.appendInsn(new InsnNode(Opcodes.ARETURN));
            }
        } else {
            // No expression provided; the declared return type must be void.
            if (!expectedReturnType.equals(Type.VOID)) {
                throw new RuntimeException("Missing return value in non-void method");
            }
            bb.returnVoid();
        }
        return null;
    }

    @Override
    public Void visitMethodCall(TLangParser.MethodCallContext ctx) {
        String methodName = ctx.methodCallExpr().ID().getText();
        TLangParser.ArgListContext al = ctx.methodCallExpr().argList();

        // 1) Evaluate argument expressions that are provided.
        List<Type> argTypes = new ArrayList<>();
        if (al != null) {
            for (TLangParser.ExpressionContext exprCtx : al.expression()) {
                exprCtx.accept(this);
                Type t = typeStack.pop();
                argTypes.add(t);
            }
        }

        // 2) Find the MethodInfo (which now also includes default parameters).
        MethodInfo mi = findMethod(methodName, argTypes);
        if (mi == null) {
            throw new RuntimeException("No matching method " + methodName +
                    " with param types " + argTypes);
        }

        // 3) If not enough arguments were provided, compile default expressions for missing parameters.
        int providedCount = (al != null) ? al.expression().size() : 0;
        int totalCount = mi.paramTypes.size(); // or mi.params.size() if you stored ParameterInfo list
        // Assume here that mi has a field: List<ParameterInfo> params.
        for (int i = providedCount; i < totalCount; i++) {
            ParameterInfo pi = mi.params.get(i);
            if (pi.defaultExpr == null) {
                throw new RuntimeException("Missing argument for parameter " + pi.name + " and no default provided");
            }
            // Evaluate the default expression.
            pi.defaultExpr.accept(this);
            Type defaultType = typeStack.pop();
            if (!defaultType.equals(pi.type)) {
                throw new RuntimeException("Default expression for parameter " + pi.name +
                        " does not match declared type. Got " + defaultType +
                        " but expected " + pi.type);
            }
            argTypes.add(pi.type);
        }

        // 4) Emit invokeStatic to call "Main.methodName" with mi.descriptor.
        bb.invokeStatic(className, methodName, mi.descriptor);

        // 5) If the method is non-void, push its return type.
        if (mi.returnType != null && !mi.returnType.equals(Type.VOID)) {
            typeStack.push(mi.returnType);
        }
        return null;
    }

    @Override
    public Void visitBitwiseXorExpr(TLangParser.BitwiseXorExprContext ctx) {
        // Evaluate the left operand.
        ctx.expression(0).accept(this);
        // Evaluate the right operand.
        ctx.expression(1).accept(this);

        // Pop operands from the stack (right then left).
        Type rightType = typeStack.pop();
        Type leftType  = typeStack.pop();

        // Both operands must be integers.
        if (!leftType.equals(Type.INT) || !rightType.equals(Type.INT)) {
            throw new RuntimeException("Bitwise XOR operator '^' only supports integers, got "
                    + leftType + " and " + rightType);
        }

        // Emit the XOR instruction.
        bb.xor();  // This method emits the IXOR opcode.

        // Push the result (an integer) onto the stack.
        typeStack.push(Type.INT);
        return null;
    }

    @Override
    public Void visitExponentiationExpr(TLangParser.ExponentiationExprContext ctx) {
        // Evaluate left and right operands.
        ctx.expression(0).accept(this);
        ctx.expression(1).accept(this);
        Type rightType = typeStack.pop();
        Type leftType = typeStack.pop();
        if (!leftType.equals(Type.INT) || !rightType.equals(Type.INT)) {
            throw new RuntimeException("Exponentiation operator '**' only supports integers.");
        }
        // Store operands into locals.
        int rightLocal = nextLocalIndex++;
        bb.storeLocal(rightLocal, Opcodes.ISTORE);
        int leftLocal = nextLocalIndex++;
        bb.storeLocal(leftLocal, Opcodes.ISTORE);

        // Load left operand, convert to double.
        bb.loadLocal(leftLocal, Opcodes.ILOAD);
        bb.intToDouble();

        // Load right operand, convert to double.
        bb.loadLocal(rightLocal, Opcodes.ILOAD);
        bb.intToDouble();

        // Call Math.pow(double, double), which returns a double.
        bb.invokeStatic("java/lang/Math", "pow", "(DD)D");

        // Convert the double result back to int.
        bb.doubleToInt();
        typeStack.push(Type.INT);
        return null;
    }

    @Override
    public Void visitBitwiseComplementExpr(TLangParser.BitwiseComplementExprContext ctx) {
        // Evaluate the operand.
        ctx.expression().accept(this);
        Type t = typeStack.pop();
        if (!t.equals(Type.INT)) {
            throw new RuntimeException("Bitwise complement operator '~' only supports integers, got " + t);
        }
        // Compute the bitwise complement by XORing with -1.
        bb.pushInt(-1);
        bb.xor();  // Emits IXOR.
        typeStack.push(Type.INT);
        return null;
    }

    // ===== Statements =====

    @Override
    public Void visitVarDecl(TLangParser.VarDeclContext ctx) {
        // Convert the declared type from the type node.
        Type declaredType = toType(ctx.type().getText());

        // For an array variable, set expectedArrayType so that the initializer is checked properly.
        if (declaredType.dimensions > 0) {
            expectedArrayType = declaredType;
        }

        // Iterate over each variable initializer in the declaration.
        for (TLangParser.VarInitContext initCtx : ctx.varInit()) {
            // For array variables, ensure the expected type is set per initializer.
            if (declaredType.dimensions > 0) {
                expectedArrayType = declaredType;
            }

            // Evaluate the initializer expression.
            initCtx.expression().accept(this);
            expectedArrayType = null;  // Clear the expected type after evaluating the initializer.

            // Get the type that resulted from evaluating the expression.
            Type exprType = typeStack.pop();
            if (!exprType.equals(declaredType)) {
                throw new RuntimeException("Type mismatch in variable declaration for '" +
                        initCtx.ID().getText() + "'. Declared: " + declaredType + ", actual: " + exprType);
            }

            // Allocate a new local variable index and register the variable in the symbol table.
            int idx = nextLocalIndex++;
            symbolTable.put(initCtx.ID().getText(), new VariableInfo(ctx.type().getText(), idx));

            // Store the value from the stack into the local variable.
            bb.storeLocal(idx, getStoreOpcode(ctx.type().getText()));
        }

        return null;
    }

    @Override
    public Void visitAssignmentStmt(TLangParser.AssignmentStmtContext ctx) {
        // Loop over each assignable (either a simple or array assignment).
        for (TLangParser.AssignableContext actx : ctx.assignable()) {
            actx.accept(this);
        }
        return null;
    }

    // Optional: if ANTLR doesn’t generate one automatically, delegate based on the alternative.
    @Override
    public Void visitAssignable(TLangParser.AssignableContext ctx) {
        if (ctx.assignment() != null) {
            return visitAssignment(ctx.assignment());
        } else if (ctx.arrayAssignment() != null) {
            return visitArrayAssignment(ctx.arrayAssignment());
        }
        return null;
    }

    @Override
    public Void visitAssignment(TLangParser.AssignmentContext ctx) {
        VariableInfo var = symbolTable.get(ctx.ID().getText());
        Type varType = toType(var.getType());
        String opText = ctx.asmtOp().getText();

        if ("=".equals(opText)) {
            if (varType.dimensions > 0) {
                expectedArrayType = varType;
            }
            ctx.expression().accept(this);
            expectedArrayType = null;
            Type exprType = typeStack.pop();
            if (!exprType.equals(varType)) {
                throw new RuntimeException("Type mismatch in assignment for variable '" +
                        ctx.ID().getText() + "'. Declared: " + varType + ", actual: " + exprType);
            }
            bb.storeLocal(var.getIndex(), getStoreOpcodeForType(varType));
        }
        else {
            // Support augmented assignment for strings with "+="
            if (opText.equals("+=") && varType.equals(Type.STRING)) {
                // Load current string value.
                bb.loadLocal(var.getIndex(), Opcodes.ALOAD);
                // Evaluate the RHS expression.
                ctx.expression().accept(this);
                Type exprType = typeStack.pop();
                // If the right-hand value isn't a string, coerce it.
                if (!exprType.equals(Type.STRING)) {
                    coerceToString(exprType);
                }
                // Concatenate: invoke String.concat(String) to combine them.
                bb.invokeVirtual("java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");
                // Store the resulting string back.
                bb.storeLocal(var.getIndex(), Opcodes.ASTORE);
            }
            else {
                // --- Augmented assignment for integers ---
                if (!varType.equals(Type.INT)) {
                    throw new RuntimeException("Augmented assignment operator " + opText +
                            " only supports int variables (or string for '+='), got " + varType);
                }
                bb.loadLocal(var.getIndex(), Opcodes.ILOAD);
                ctx.expression().accept(this);
                Type exprType = typeStack.pop();
                if (!exprType.equals(Type.INT)) {
                    throw new RuntimeException("Augmented assignment operator " + opText +
                            " requires an int expression, got " + exprType);
                }
                switch (opText) {
                    case "+=":
                        bb.add();
                        break;
                    case "-=":
                        bb.sub();
                        break;
                    case "*=":
                        bb.mul();
                        break;
                    case "/=":
                        bb.div();
                        break;
                    default:
                        throw new RuntimeException("Unsupported augmented assignment operator: " + opText);
                }
                bb.storeLocal(var.getIndex(), Opcodes.ISTORE);
            }
        }
        return null;
    }

    @Override
    public Void visitArrayAssignment(TLangParser.ArrayAssignmentContext ctx) {
        // Get the assignment operator text.
        String op = ctx.asmtOp().getText();

        if ("=".equals(op)) {
            // --- Regular array assignment ---
            // Evaluate the array expression.
            ctx.expression(0).accept(this);
            Type arrayType = typeStack.pop();
            if (arrayType.dimensions == 0) {
                throw new RuntimeException("Left-hand side is not an array.");
            }
            // Evaluate the index.
            ctx.expression(1).accept(this);
            Type indexType = typeStack.pop();
            if (!indexType.equals(Type.INT)) {
                throw new RuntimeException("Array index must be an integer.");
            }
            // Expected element type is the array type with one less dimension.
            Type expectedElemType = new Type(arrayType.base, arrayType.dimensions - 1);
            // Evaluate the right-hand side expression.
            ctx.expression(2).accept(this);
            Type valueType = typeStack.pop();
            if (!valueType.equals(expectedElemType)) {
                throw new RuntimeException("Type mismatch in array assignment: expected " +
                        expectedElemType + ", got " + valueType);
            }
            // Emit the proper store instruction:
            // For a primitive element (dimensions==0 and base int/bool) use IASTORE; otherwise use AASTORE.
            if (expectedElemType.dimensions == 0 &&
                    (expectedElemType.base == Type.BaseType.INT || expectedElemType.base == Type.BaseType.BOOL)) {
                bb.appendInsn(new InsnNode(Opcodes.IASTORE));
            } else {
                bb.appendInsn(new InsnNode(Opcodes.AASTORE));
            }
        } else {
            // --- Augmented array assignment (e.g. +=, -=, *=, /=) ---
            // For augmented array assignment we assume that the element type is int.
            // We will use a DUP2/IALOAD sequence to retrieve the current element value without losing
            // the array reference and index.
            //
            // The sequence we want is:
            //    1. Evaluate array expression -> stack: [arrayRef]  (type: [I)
            //    2. Evaluate index expression -> stack: [arrayRef, index]
            //    3. DUP2 -> stack: [arrayRef, index, arrayRef, index]
            //    4. IALOAD -> pops the top arrayRef and index, pushes currentValue -> stack: [arrayRef, index, currentValue]
            //    5. Evaluate RHS -> stack: [arrayRef, index, currentValue, rhs]
            //    6. Perform arithmetic (e.g. add) -> stack: [arrayRef, index, result]
            //    7. IASTORE -> store result into array.
            //
            // Evaluate the array expression.
            ctx.expression(0).accept(this); // pushes arrayRef (should be of type [I)
            // Evaluate the index.
            ctx.expression(1).accept(this); // pushes index (int)
            // Duplicate the top two items (arrayRef and index) so they can be used later.
            bb.appendInsn(new InsnNode(Opcodes.DUP2));
            // Use IALOAD to load the current element value.
            bb.appendInsn(new InsnNode(Opcodes.IALOAD));
            // Now the stack is: [arrayRef, index, currentValue (int)]
            // Evaluate the RHS expression.
            ctx.expression(2).accept(this); // pushes rhs (int)
            // Now the stack is: [arrayRef, index, currentValue, rhs]
            // Perform the augmented operation.
            switch (op) {
                case "+=":
                    bb.add();  // performs (currentValue + rhs)
                    break;
                case "-=":
                    bb.sub();  // performs (currentValue - rhs)
                    break;
                case "*=":
                    bb.mul();  // performs (currentValue * rhs)
                    break;
                case "/=":
                    bb.div();  // performs (currentValue / rhs)
                    break;
                default:
                    throw new RuntimeException("Unsupported augmented array assignment operator: " + op);
            }
            // Now the stack is: [arrayRef, index, result (int)]
            // Store the result back into the array.
            bb.appendInsn(new InsnNode(Opcodes.IASTORE));
        }
        return null;
    }

    @Override
    public Void visitForeachStatement(TLangParser.ForeachStatementContext ctx) {
        ctx.expression().accept(this);
        Type arrayType = typeStack.pop();
        if (arrayType.dimensions == 0) {
            throw new RuntimeException("Foreach loop expects an array type but got: " + arrayType);
        }
        Type elementType = new Type(arrayType.base, arrayType.dimensions - 1);
        Type declaredType = toType(ctx.type().getText());
        if (!declaredType.equals(elementType)) {
            throw new RuntimeException("Foreach loop variable type mismatch: expected " +
                    elementType + ", got " + declaredType);
        }
        String loopVarName = ctx.ID().getText();
        int arrayLocal = nextLocalIndex++;
        bb.storeLocal(arrayLocal, Opcodes.ASTORE);

        bb.loadLocal(arrayLocal, Opcodes.ALOAD);
        bb.appendInsn(new InsnNode(Opcodes.ARRAYLENGTH));
        int lengthLocal = nextLocalIndex++;
        bb.storeLocal(lengthLocal, Opcodes.ISTORE);

        int indexLocal = nextLocalIndex++;
        bb.pushInt(0);
        bb.storeLocal(indexLocal, Opcodes.ISTORE);

        int loopVarLocal = nextLocalIndex++;
        symbolTable.put(loopVarName, new VariableInfo(declaredType.toString(), loopVarLocal));

        LabelNode loopStart     = new LabelNode();
        LabelNode continueLabel = new LabelNode();
        LabelNode loopExit      = new LabelNode();

        bb.placeLabel(loopStart);
        bb.loadLocal(indexLocal, Opcodes.ILOAD);
        bb.loadLocal(lengthLocal, Opcodes.ILOAD);
        bb.jumpIfICmp(ConditionType.GE, loopExit);

        bb.loadLocal(arrayLocal, Opcodes.ALOAD);
        bb.loadLocal(indexLocal, Opcodes.ILOAD);
        if (elementType.dimensions == 0 &&
                (elementType.base == Type.BaseType.INT || elementType.base == Type.BaseType.BOOL)) {
            bb.appendInsn(new InsnNode(Opcodes.IALOAD));
            typeStack.push(new Type(elementType.base, 0));
        } else {
            bb.appendInsn(new InsnNode(Opcodes.AALOAD));
            typeStack.push(new Type(elementType.base, elementType.dimensions));
        }
        bb.storeLocal(loopVarLocal, getStoreOpcodeForType(declaredType));

        // Push break/continue targets.
        breakLabels.push(loopExit);
        continueLabels.push(continueLabel);

        ctx.block().accept(this);

        breakLabels.pop();
        continueLabels.pop();

        bb.placeLabel(continueLabel);
        bb.loadLocal(indexLocal, Opcodes.ILOAD);
        bb.pushInt(1);
        bb.add();
        bb.storeLocal(indexLocal, Opcodes.ISTORE);
        bb.gotoLabel(loopStart);
        bb.placeLabel(loopExit);
        return null;
    }

    @Override
    public Void visitBreakStmt(TLangParser.BreakStmtContext ctx) {
        if (breakLabels.isEmpty()) {
            throw new RuntimeException("Cannot use break outside of a loop.");
        }
        bb.gotoLabel(breakLabels.peek());
        return null;
    }

    @Override
    public Void visitContinueStmt(TLangParser.ContinueStmtContext ctx) {
        if (continueLabels.isEmpty()) {
            throw new RuntimeException("Cannot use continue outside of a loop.");
        }
        bb.gotoLabel(continueLabels.peek());
        return null;
    }

    @Override
    public Void visitLogicalNotExpr(TLangParser.LogicalNotExprContext ctx) {
        // Evaluate the operand.
        ctx.expression().accept(this);
        Type operandType = typeStack.pop();
        if (!operandType.equals(Type.BOOL)) {
            throw new RuntimeException("Logical not (!) operator expects a boolean, got: " + operandType);
        }
        // For booleans represented as 0/1, pushing 1 and XORing inverts the value.
        bb.pushInt(1);
        bb.xor(); // Assumes your BytecodeBuilder.xor() method exists.
        // The result is a boolean.
        typeStack.push(Type.BOOL);
        return null;
    }

    // ===== Print Statement =====

    @Override
    public Void visitPrint(TLangParser.PrintContext ctx) {
        ctx.expression().accept(this);
        Type exprType = typeStack.pop();
        bb.getStaticField("java/lang/System", "out", "Ljava/io/PrintStream;");
        bb.swap();
        if (exprType.equals(Type.INT)) {
            bb.invokeVirtual("java/io/PrintStream", "println", "(I)V");
        } else if (exprType.equals(Type.BOOL)) {
            bb.invokeVirtual("java/io/PrintStream", "println", "(Z)V");
        } else if (exprType.equals(Type.STRING)) {
            bb.invokeVirtual("java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        } else {
            bb.invokeVirtual("java/io/PrintStream", "println", "(Ljava/lang/Object;)V");
        }
        return null;
    }

    // ===== While Statement =====

    @Override
    public Void visitWhileStatement(TLangParser.WhileStatementContext ctx) {
        LabelNode startLabel = new LabelNode();
        LabelNode exitLabel  = new LabelNode();

        // For a while loop, the continue target is the start of the loop.
        breakLabels.push(exitLabel);
        continueLabels.push(startLabel);

        bb.placeLabel(startLabel);
        ctx.expression().accept(this);
        Type condType = typeStack.pop();
        if (!condType.equals(Type.BOOL)) {
            throw new RuntimeException("While condition must be boolean.");
        }
        bb.jumpIf(ConditionType.EQ, exitLabel);

        for (TLangParser.StatementContext stmt : ctx.block().statement()) {
            stmt.accept(this);
        }
        bb.gotoLabel(startLabel);
        bb.placeLabel(exitLabel);

        breakLabels.pop();
        continueLabels.pop();
        return null;
    }

    //===== For Statement =====

    @Override
    public Void visitForStatement(TLangParser.ForStatementContext ctx) {
        // Process the initialization, if any.
        if (ctx.init != null) {
            ctx.init.accept(this);
        }
        LabelNode startLabel    = new LabelNode();
        LabelNode continueLabel = new LabelNode();
        LabelNode exitLabel     = new LabelNode();

        // Start of the loop.
        bb.placeLabel(startLabel);
        if (ctx.cond != null) {
            ctx.cond.accept(this);
            Type condType = typeStack.pop();
            if (!condType.equals(Type.BOOL)) {
                throw new RuntimeException("For-loop condition must be boolean.");
            }
            bb.jumpIf(ConditionType.EQ, exitLabel);
        }

        // Push the current loop’s break and continue targets.
        breakLabels.push(exitLabel);
        continueLabels.push(continueLabel);

        // Process the loop body.
        for (TLangParser.StatementContext stmt : ctx.block().statement()) {
            stmt.accept(this);
        }

        breakLabels.pop();
        continueLabels.pop();

        // Process the update clause.
        bb.placeLabel(continueLabel);
        if (ctx.update != null) {
            // For the "old way": an update in the form of an assignment.
            if (ctx.update.assignment() != null) {
                ctx.update.assignment().accept(this);
            }
            // For an update that's a general expression (e.g. i++ or i--).
            else if (ctx.update.expression() != null) {
                ctx.update.expression().accept(this);
                // Emit a POP to remove the value left by the update expression from the JVM stack.
                bb.appendInsn(new InsnNode(Opcodes.POP));
                // Also remove it from our semantic type stack.
                typeStack.pop();
            }
        }
        bb.gotoLabel(startLabel);
        bb.placeLabel(exitLabel);
        return null;
    }

    @Override
    public Void visitUnaryMinusExpr(TLangParser.UnaryMinusExprContext ctx) {
        // Evaluate the inner expression.
        ctx.expression().accept(this);
        // The type must be an integer.
        Type t = typeStack.pop();
        if (!t.equals(Type.INT)) {
            throw new RuntimeException("Unary minus only supports integers.");
        }
        // Multiply by -1: push -1 then multiply.
        bb.pushInt(-1);
        bb.mul(); // Assumes bb.mul() emits IIMUL.
        typeStack.push(Type.INT);
        return null;
    }

    @Override
    public Void visitForVarDecl(TLangParser.ForVarDeclContext ctx) {
        // Process the variable declaration (like a normal varDecl but no semicolon).
        ctx.expression().accept(this);
        Type exprType = typeStack.pop();
        Type declaredType = toType(ctx.type().getText());
        if (!exprType.equals(declaredType)) {
            throw new RuntimeException("Type mismatch in for-loop declaration for variable '"
                    + ctx.ID().getText() + "': expected " + declaredType + ", got " + exprType);
        }
        int index = nextLocalIndex++;
        symbolTable.put(ctx.ID().getText(), new VariableInfo(ctx.type().getText(), index));
        bb.storeLocal(index, getStoreOpcode(ctx.type().getText()));
        return null;
    }

    // ===== If Statement =====

    @Override
    public Void visitIfStatement(TLangParser.IfStatementContext ctx) {
        // Evaluate the condition.
        ctx.expression().accept(this);
        Type condType = typeStack.pop();
        if (!condType.equals(Type.BOOL)) {
            throw new RuntimeException("If condition must be boolean.");
        }

        // Create labels for the else branch and for the end of the if/else chain.
        LabelNode elseLabel = new LabelNode();
        LabelNode endLabel = new LabelNode();

        // If the condition is false, jump to the else branch.
        bb.jumpIf(ConditionType.EQ, elseLabel);

        // Process the "then" part.
        if (ctx.block() != null) {
            for (TLangParser.StatementContext stmt : ctx.block().statement()) {
                stmt.accept(this);
            }
        }

        // After the then part, jump unconditionally to the end.
        bb.gotoLabel(endLabel);

        // Process the else clause, if present.
        bb.placeLabel(elseLabel);
        if (ctx.elseClause() != null) {
            TLangParser.ElseClauseContext elseCtx = ctx.elseClause();
            if (elseCtx.ifStatement() != null) {
                elseCtx.ifStatement().accept(this);
            } else if (elseCtx.block() != null) {
                for (TLangParser.StatementContext stmt : elseCtx.block().statement()) {
                    stmt.accept(this);
                }
            }
        }
        bb.placeLabel(endLabel);
        return null;
    }

    // ===== Expressions =====

    @Override
    public Void visitPrefixIncDecExpr(TLangParser.PrefixIncDecExprContext ctx) {
        // Get the variable name.
        String varName = ctx.ID().getText();
        VariableInfo var = symbolTable.get(varName);
        Type varType = toType(var.getType());
        if (!varType.equals(Type.INT)) {
            throw new RuntimeException("++/-- can only be applied to int variables, got " + varType);
        }

        // Load the current value of the variable.
        bb.loadLocal(var.getIndex(), Opcodes.ILOAD);

        // Push 1 and perform addition or subtraction.
        bb.pushInt(1);
        if (ctx.getChild(0).getText().equals("++")) {
            bb.add();  // value + 1
        } else {
            bb.sub();  // value - 1
        }

        // Store the updated value back.
        bb.storeLocal(var.getIndex(), Opcodes.ISTORE);

        // For a prefix operation, push the updated value onto the stack.
        bb.loadLocal(var.getIndex(), Opcodes.ILOAD);
        typeStack.push(Type.INT);

        return null;
    }

    @Override
    public Void visitPostfixIncDecExpr(TLangParser.PostfixIncDecExprContext ctx) {
        // Get the variable name.
        String varName = ctx.ID().getText();
        VariableInfo var = symbolTable.get(varName);
        Type varType = toType(var.getType());
        if (!varType.equals(Type.INT)) {
            throw new RuntimeException("++/-- can only be applied to int variables, got " + varType);
        }

        // For postfix, first load the original value onto the stack.
        bb.loadLocal(var.getIndex(), Opcodes.ILOAD);
        typeStack.push(Type.INT);

        // Then load the variable again to compute the updated value.
        bb.loadLocal(var.getIndex(), Opcodes.ILOAD);
        bb.pushInt(1);
        if (ctx.getChild(1).getText().equals("++")) {
            bb.add();  // value + 1
        } else {
            bb.sub();  // value - 1
        }

        // Store the updated value back into the variable.
        bb.storeLocal(var.getIndex(), Opcodes.ISTORE);

        // The original value remains on the stack.
        return null;
    }

    @Override
    public Void visitParenExpr(TLangParser.ParenExprContext ctx) {
        return ctx.expression().accept(this);
    }

    @Override
    public Void visitIntLiteral(TLangParser.IntLiteralContext ctx) {
        bb.pushInt(Integer.parseInt(ctx.INT().getText()));
        typeStack.push(Type.INT);
        return null;
    }

    @Override
    public Void visitStringLiteral(TLangParser.StringLiteralContext ctx) {
        String raw = ctx.STRING().getText();
        // raw includes the leading & trailing quotes, e.g. "\"Hello\\n\""
        // So first strip off the quotes and then unescape:
        String unescaped = unescapeStringLiteral(raw);

        bb.pushString(unescaped);
        typeStack.push(Type.STRING);
        return null;
    }


    @Override
    public Void visitBoolLiteral(TLangParser.BoolLiteralContext ctx) {
        bb.pushInt(ctx.BOOL().getText().equals("true") ? 1 : 0);
        typeStack.push(Type.BOOL);
        return null;
    }

    @Override
    public Void visitVarExpr(TLangParser.VarExprContext ctx) {
        VariableInfo var = symbolTable.get(ctx.ID().getText());
        bb.loadLocal(var.getIndex(), getLoadOpcode(var.getType()));
        typeStack.push(toType(var.getType()));
        return null;
    }

    @Override
    public Void visitAddSubExpr(TLangParser.AddSubExprContext ctx) {
        // 1) Evaluate left expression
        ctx.expression(0).accept(this);
        // 2) Evaluate right expression
        ctx.expression(1).accept(this);

        // The top of our 'typeStack' now has right then left (LIFO).
        // Pop them in that order:
        Type rightType = typeStack.pop();
        Type leftType  = typeStack.pop();

        // The operator: '+' or '-'
        String op = ctx.op.getText();

        if (op.equals("+")) {
            int rIndex = nextLocalIndex++;
            if (leftType.equals(Type.INT) && rightType.equals(Type.INT)) {
                // -----------------------------
                // Case A: Both are integer => numeric addition
                // -----------------------------
                // Right now, the actual integer values are on the JVM stack in the order [left, right].
                // We already popped them off our semantic stack, so let's store/reload for the IADD.
                bb.storeLocal(rIndex, Opcodes.ISTORE);
                int lIndex = nextLocalIndex++;
                bb.storeLocal(lIndex, Opcodes.ISTORE);

                // Reload in correct order for iadd:
                bb.loadLocal(lIndex, Opcodes.ILOAD);
                bb.loadLocal(rIndex, Opcodes.ILOAD);

                bb.add();  // iadd
                typeStack.push(Type.INT);
            }
            else {
                // -----------------------------
                // Case B: At least one is String (or something else) => String concat
                // -----------------------------
                // We'll store them in locals, then build a StringBuilder.

                // Store right
                bb.storeLocal(rIndex, getStoreOpcode(rightType.name().toLowerCase()));
                // Store left
                int lIndex = nextLocalIndex++;
                bb.storeLocal(lIndex, getStoreOpcode(leftType.name().toLowerCase()));

                // Create StringBuilder
                bb.newInstance("java/lang/StringBuilder");
                bb.dup();
                bb.invokeSpecial("java/lang/StringBuilder", "<init>", "()V");

                // Append left
                bb.loadLocal(lIndex, getLoadOpcode(leftType.name().toLowerCase()));
                coerceToString(leftType);
                bb.invokeVirtual(
                        "java/lang/StringBuilder",
                        "append",
                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;"
                );

                // Append right
                bb.loadLocal(rIndex, getLoadOpcode(rightType.name().toLowerCase()));
                coerceToString(rightType);
                bb.invokeVirtual(
                        "java/lang/StringBuilder",
                        "append",
                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;"
                );

                // Finish => toString()
                bb.invokeVirtual("java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
                typeStack.push(Type.STRING);
            }
        }
        else if (op.equals("-")) {
            // -----------------------------
            // Subtraction => purely numeric
            // -----------------------------
            if (!leftType.equals(Type.INT) || !rightType.equals(Type.INT)) {
                throw new RuntimeException(
                        "Operator '-' only supports integers, got " + leftType + " and " + rightType
                );
            }
            // Repush them onto the JVM stack in correct order
            int rIndex = nextLocalIndex++;
            bb.storeLocal(rIndex, Opcodes.ISTORE);
            int lIndex = nextLocalIndex++;
            bb.storeLocal(lIndex, Opcodes.ISTORE);

            bb.loadLocal(lIndex, Opcodes.ILOAD);
            bb.loadLocal(rIndex, Opcodes.ILOAD);

            bb.sub();  // isub
            typeStack.push(Type.INT);
        }
        else {
            // Optionally handle other operators '*', '/' etc.,
            // or defer to your existing handleBinaryOp(...) if it applies.
            handleBinaryOp(ctx.expression(0), ctx.expression(1), op);
        }

        return null;
    }

    @Override
    public Void visitMulDivExpr(TLangParser.MulDivExprContext ctx) {
        return handleBinaryOp(ctx.expression(0), ctx.expression(1), ctx.op.getText());
    }

    @Override
    public Void visitComparisonExpr(TLangParser.ComparisonExprContext ctx) {
        // Evaluate left and right expressions.
        ctx.expression(0).accept(this);
        ctx.expression(1).accept(this);

        Type rightType = typeStack.pop();
        Type leftType = typeStack.pop();
        String op = ctx.op.getText();

        // For relational operators (>, <, >=, <=) both sides must be int.
        if (op.equals(">") || op.equals("<") || op.equals(">=") || op.equals("<=")) {
            if (!leftType.equals(Type.INT) || !rightType.equals(Type.INT)) {
                throw new RuntimeException("Operator '" + op + "' only supports integers.");
            }
        }
        // For equality checks, both sides must be the same type.
        else if (op.equals("==") || op.equals("!=")) {
            if (!leftType.equals(rightType)) {
                throw new RuntimeException("Operator '" + op + "' requires both operands to be of the same type.");
            }
        }
        else {
            throw new IllegalArgumentException("Unknown comparison operator: " + op);
        }

        // Subtract right from left.
        bb.sub();

        // Create labels for true and end.
        LabelNode trueLabel = new LabelNode();
        LabelNode endLabel = new LabelNode();

        // Depending on the operator, jump if the comparison is true.
        switch (op) {
            case ">":
                bb.jumpIf(ConditionType.GT, trueLabel);
                break;
            case "<":
                bb.jumpIf(ConditionType.LT, trueLabel);
                break;
            case ">=":
                bb.jumpIf(ConditionType.GE, trueLabel);
                break;
            case "<=":
                bb.jumpIf(ConditionType.LE, trueLabel);
                break;
            case "==":
                bb.jumpIf(ConditionType.EQ, trueLabel);
                break;
            case "!=":
                bb.jumpIf(ConditionType.NE, trueLabel);
                break;
        }

        // If the condition fails, push 0 (false) and jump to end.
        bb.pushInt(0);
        bb.gotoLabel(endLabel);

        // On success, push 1 (true).
        bb.placeLabel(trueLabel);
        bb.pushInt(1);

        // Final label.
        bb.placeLabel(endLabel);

        // The result is a boolean.
        typeStack.push(Type.BOOL);
        return null;
    }

    /**
     * Visits a ternary expression of the form:
     *    condition ? thenExpr : elseExpr
     *
     * The condition must evaluate to a boolean (0/1).
     * Depending on its value, the then-expression or the else-expression is evaluated,
     * and its value is left on the stack.
     */
    @Override
    public Void visitTernaryExpr(TLangParser.TernaryExprContext ctx) {
        // Evaluate the condition.
        ctx.expression(0).accept(this);
        Type condType = typeStack.pop();
        if (!condType.equals(Type.BOOL)) {
            throw new RuntimeException("Ternary condition must be boolean, got " + condType);
        }

        // Create labels for the else branch and for merging the branches.
        LabelNode elseLabel = new LabelNode();
        LabelNode mergeLabel = new LabelNode();

        // If condition == false (i.e. equals 0), jump to else branch.
        bb.jumpIf(ConditionType.EQ, elseLabel);

        // Then branch.
        ctx.expression(1).accept(this);
        Type thenType = typeStack.pop();

        // After then branch, jump unconditionally to merge.
        bb.gotoLabel(mergeLabel);

        // Else branch.
        bb.placeLabel(elseLabel);
        ctx.expression(2).accept(this);
        Type elseType = typeStack.pop();

        // Ensure both branches return the same type.
        if (!thenType.equals(elseType)) {
            throw new RuntimeException("Ternary expression branches must have the same type: then branch is " +
                    thenType + ", else branch is " + elseType);
        }

        // Merge the branches.
        bb.placeLabel(mergeLabel);
        typeStack.push(thenType);
        return null;
    }

    // New: Array Access expression.
    // Syntax: <array-expression> '[' <index-expression> ']'
    @Override
    public Void visitArrayAccessExpr(TLangParser.ArrayAccessExprContext ctx) {
        ctx.expression(0).accept(this);
        Type arrayType = typeStack.pop();
        if (arrayType.dimensions == 0) {
            throw new RuntimeException("Type error: trying to index a non-array type: " + arrayType);
        }
        ctx.expression(1).accept(this);
        Type indexType = typeStack.pop();
        if (!indexType.equals(Type.INT)) {
            throw new RuntimeException("Array index must be an integer.");
        }
        // The element type is the array’s base type with one less dimension.
        Type elementType = new Type(arrayType.base, arrayType.dimensions - 1);
        // Emit the proper load instruction based on the element type.
        if (elementType.dimensions == 0) {
            if (elementType.base == Type.BaseType.INT || elementType.base == Type.BaseType.BOOL) {
                bb.appendInsn(new InsnNode(Opcodes.IALOAD));
            } else if (elementType.base == Type.BaseType.STRING) {
                bb.appendInsn(new InsnNode(Opcodes.AALOAD));
            }
        } else {
            // For an element that is itself an array (always an object)
            bb.appendInsn(new InsnNode(Opcodes.AALOAD));
        }
        typeStack.push(elementType);
        return null;
    }

    @Override
    public Void visitArrayLiteralExpr(TLangParser.ArrayLiteralExprContext ctx) {
        // Save any outer expected type (for example, from a varDecl) and clear it.
        Type outerExpected = expectedArrayType;
        expectedArrayType = null;

        // Get the inner literal context (the bracketed list)
        TLangParser.ArrayLiteralContext arrCtx = ctx.getRuleContext(TLangParser.ArrayLiteralContext.class, 0);
        int count = arrCtx.expression().size();

        if (count == 0) {
            if (outerExpected == null) {
                throw new RuntimeException("Empty array literal type cannot be inferred.");
            }
        }

        // --- First pass: Evaluate each element exactly once and store its value in a local.
        List<Integer> elementLocals = new ArrayList<>();
        Type inferredElementType = null;
        for (int i = 0; i < count; i++) {
            // If an outer expected type is provided, set the expected type for each element.
            Type savedExpected = expectedArrayType;
            if (outerExpected != null) {
                expectedArrayType = outerExpected.getElementType();
            }
            // Evaluate the i-th inner literal.
            arrCtx.expression(i).accept(this);
            Type t = typeStack.pop();
            if (i == 0) {
                inferredElementType = t;
            } else {
                if (!t.equals(inferredElementType)) {
                    throw new RuntimeException("All elements in an array literal must have the same type.");
                }
            }
            // Store the computed element value into a new local.
            int localIndex = nextLocalIndex++;
            bb.storeLocal(localIndex, getStoreOpcodeForType(inferredElementType));
            elementLocals.add(localIndex);
            expectedArrayType = savedExpected;
        }

        // --- Determine the overall type of this array literal.
        Type overallType;
        if (outerExpected != null) {
            if (!outerExpected.getElementType().equals(inferredElementType)) {
                throw new RuntimeException("Array literal element type " + inferredElementType +
                        " does not match expected element type " + outerExpected.getElementType());
            }
            overallType = outerExpected;
        } else {
            overallType = Type.arrayOf(inferredElementType, 1);
        }

        // --- Allocate the outer array.
        bb.pushInt(count);
        // The outer array's element type is overallType.getElementType()
        Type elementTypeForAllocation = overallType.getElementType();
        if (elementTypeForAllocation.dimensions == 0) {
            // For primitive element types.
            if (elementTypeForAllocation.base == Type.BaseType.INT) {
                bb.appendInsn(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_INT));
            } else if (elementTypeForAllocation.base == Type.BaseType.BOOL) {
                bb.appendInsn(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_BOOLEAN));
            } else if (elementTypeForAllocation.base == Type.BaseType.STRING) {
                bb.appendInsn(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/String"));
            } else {
                throw new RuntimeException("Unsupported base type in array literal: " + elementTypeForAllocation.base);
            }
        } else {
            // For elements that are themselves arrays (i.e. reference types), use ANEWARRAY.
            String elementInternalName = toJVMType(elementTypeForAllocation);
            bb.appendInsn(new TypeInsnNode(Opcodes.ANEWARRAY, elementInternalName));
        }

        // --- Fill the outer array with the values from our locals.
        for (int i = 0; i < count; i++) {
            bb.dup();
            bb.pushInt(i);
            bb.loadLocal(elementLocals.get(i), getLoadOpcodeForType(inferredElementType));
            if (inferredElementType.dimensions == 0) {
                if (inferredElementType.base == Type.BaseType.INT) {
                    bb.appendInsn(new InsnNode(Opcodes.IASTORE));
                } else if (inferredElementType.base == Type.BaseType.BOOL) {
                    bb.appendInsn(new InsnNode(Opcodes.BASTORE));
                } else {
                    bb.appendInsn(new InsnNode(Opcodes.AASTORE));
                }
            } else {
                bb.appendInsn(new InsnNode(Opcodes.AASTORE));
            }
        }

        typeStack.push(overallType);
        return null;
    }

    @Override
    public Void visitLogicalExpr(TLangParser.LogicalExprContext ctx) {
        String op = ctx.op.getText();
        LabelNode branchLabel = new LabelNode();
        LabelNode endLabel = new LabelNode();

        if (op.equals("&&")) {
            // Evaluate the left operand.
            ctx.expression(0).accept(this);
            // If left == 0, jump to branchLabel (which will push false).
            bb.jumpIf(ConditionType.EQ, branchLabel);
            // Otherwise, left was true; now evaluate the right operand.
            ctx.expression(1).accept(this);
            bb.gotoLabel(endLabel);
            // Branch: left was false.
            bb.placeLabel(branchLabel);
            bb.pushInt(0);
            bb.placeLabel(endLabel);
        } else if (op.equals("||")) {
            // Evaluate the left operand.
            ctx.expression(0).accept(this);
            // If left != 0, jump to branchLabel (which will push true).
            bb.jumpIf(ConditionType.NE, branchLabel);
            // Otherwise, left was false; evaluate the right operand.
            ctx.expression(1).accept(this);
            bb.gotoLabel(endLabel);
            // Branch: left was true.
            bb.placeLabel(branchLabel);
            bb.pushInt(1);
            bb.placeLabel(endLabel);
        } else {
            throw new RuntimeException("Unknown logical operator: " + op);
        }

        // The result (0 or 1) remains on the stack.
        typeStack.push(Type.BOOL);
        return null;
    }


    // Helper for binary arithmetic operations.
    private Void handleBinaryOp(TLangParser.ExpressionContext left,
                                TLangParser.ExpressionContext right,
                                String op) {
        left.accept(this);
        right.accept(this);
        Type rightType = typeStack.pop();
        Type leftType = typeStack.pop();
        if (!leftType.equals(Type.INT) || !rightType.equals(Type.INT)) {
            throw new RuntimeException("Arithmetic operators are only supported on integers.");
        }
        switch (op) {
            case "+":
                bb.add();
                break;
            case "-":
                bb.sub();
                break;
            case "*":
                bb.mul();
                break;
            case "/":
                bb.div();
                break;
            case "%":
                bb.appendInsn(new InsnNode(Opcodes.IREM));  // IREM computes the remainder.
                break;
            default:
                throw new IllegalArgumentException("Unknown binary operator: " + op);
        }
        typeStack.push(Type.INT);
        return null;
    }

    private int getStoreOpcode(String type) {
        if (type.endsWith("[]")) {
            // Array variables are stored as object references.
            return Opcodes.ASTORE;
        }
        switch (type.toLowerCase()) {
            case "int":
            case "bool":
                return Opcodes.ISTORE;
            case "string":
                return Opcodes.ASTORE;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    private int getLoadOpcode(String type) {
        if (type.endsWith("[]")) {
            return Opcodes.ALOAD;
        }
        switch (type.toLowerCase()) {
            case "int":
            case "bool":
                return Opcodes.ILOAD;
            case "string":
                return Opcodes.ALOAD;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    private String toJVMDescriptor(List<Type> paramTypes, Type returnType) {
        // Build param part
        StringBuilder sb = new StringBuilder("(");
        for (Type t : paramTypes) {
            sb.append(toJVMType(t));
        }
        sb.append(")");

        // If null or Type.VOID => 'V'
        if (returnType == null || returnType.equals(Type.VOID)) {
            sb.append("V");
        } else {
            sb.append(toJVMType(returnType));
        }
        return sb.toString();
    }

    private String toJVMType(Type t) {
        StringBuilder sb = new StringBuilder();
        // Each dimension adds a '['
        sb.append("[".repeat(Math.max(0, t.dimensions)));
        if (t.dimensions > 0) {
            // In an array, the element type is encoded as:
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
                default:
                    throw new IllegalArgumentException("Unsupported array base type: " + t.base);
            }
        } else {
            // Not an array
            switch (t.base) {
                case INT:
                case BOOL:
                    sb.append("I");
                    break;
                case STRING:
                    sb.append("Ljava/lang/String;");
                    break;
                case VOID:
                    sb.append("V");
                    break;
            }
        }
        return sb.toString();
    }


    private MethodInfo findMethod(String methodName, List<Type> providedArgTypes) {
        // First pass: Look for an exact match.
        for (MethodInfo mi : methods) {
            if (mi.name.equals(methodName)) {
                if (providedArgTypes.size() == mi.paramTypes.size()) {
                    boolean exactMatch = true;
                    for (int i = 0; i < providedArgTypes.size(); i++) {
                        if (!providedArgTypes.get(i).equals(mi.paramTypes.get(i))) {
                            exactMatch = false;
                            break;
                        }
                    }
                    if (exactMatch) {
                        return mi;
                    }
                }
            }
        }

        // Second pass: Look for a match with default parameters.
        for (MethodInfo mi : methods) {
            if (mi.name.equals(methodName)) {
                int total = mi.paramTypes.size();
                int provided = providedArgTypes.size();
                // Only consider if fewer arguments were provided than declared.
                if (provided < total) {
                    boolean match = true;
                    // Check that the provided arguments match the corresponding declared types.
                    for (int i = 0; i < provided; i++) {
                        if (!providedArgTypes.get(i).equals(mi.paramTypes.get(i))) {
                            match = false;
                            break;
                        }
                    }
                    // Check that each missing parameter has a default expression.
                    for (int i = provided; i < total && match; i++) {
                        ParameterInfo pi = mi.params.get(i);
                        if (pi.defaultExpr == null) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        return mi;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Convert top-of-stack to a String if necessary.
     */
    private void coerceToString(Type t) {
        if (t.equals(Type.INT)) {
            bb.invokeStatic("java/lang/String", "valueOf", "(I)Ljava/lang/String;");
        } else if (t.equals(Type.BOOL)) {
            bb.invokeStatic("java/lang/String", "valueOf", "(Z)Ljava/lang/String;");
        } else if (t.equals(Type.STRING)) {
        } else {
            bb.invokeStatic("java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
        }
    }

    private String unescapeStringLiteral(String raw) {
        // 1) Remove outer quotes:
        //    raw is something like "\"Hello\\n\""
        //    So skip the first and last char if they are quotes.
        if (raw.length() >= 2 && raw.charAt(0) == '"' && raw.charAt(raw.length() - 1) == '"') {
            raw = raw.substring(1, raw.length() - 1);
        }

        // 2) Parse escapes. We'll build a StringBuilder and walk the characters.
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '\\' && i < raw.length() - 1) {
                // Look at the next char to see which escape
                char next = raw.charAt(++i);
                switch (next) {
                    case 'n':  sb.append('\n'); break;
                    case 't':  sb.append('\t'); break;
                    case 'r':  sb.append('\r'); break;
                    case 'b':  sb.append('\b'); break;
                    case 'f':  sb.append('\f'); break;
                    case '\\': sb.append('\\'); break;
                    case '"':  sb.append('"');  break;
                    case '\'': sb.append('\''); break;

                    case 'u':
                        // Unicode escape \\uXXXX
                        if (i + 4 >= raw.length()) {
                            throw new RuntimeException("Invalid Unicode escape (too few hex digits) in string literal.");
                        }
                        String hex = raw.substring(i + 1, i + 5); // next 4 hex digits
                        i += 4; // skip them
                        int codePoint;
                        try {
                            codePoint = Integer.parseInt(hex, 16);
                        } catch (NumberFormatException ex) {
                            throw new RuntimeException("Invalid Unicode escape '\\u" + hex + "' in string literal.");
                        }
                        sb.append((char) codePoint);
                        break;

                    default:
                        // Possibly an unknown escape like \z or something
                        // You can decide to handle or error out.
                        // For example:
                        sb.append('\\').append(next);
                        break;
                }
            } else {
                // normal character
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private int getStoreOpcodeForType(Type t) {
        // For primitives (non-array) int and bool, use ISTORE;
        // for everything else (string or any array), use ASTORE.
        if (t.dimensions == 0) {
            switch(t.base) {
                case INT:
                case BOOL:
                    return Opcodes.ISTORE;
                case STRING:
                    return Opcodes.ASTORE;
                default:
                    throw new IllegalArgumentException("Unknown base type: " + t.base);
            }
        } else {
            return Opcodes.ASTORE;
        }
    }

    private int getLoadOpcodeForType(Type t) {
        if (t.dimensions > 0) {
            return Opcodes.ALOAD;
        }
        switch(t.base) {
            case INT:
            case BOOL:   return Opcodes.ILOAD;
            case STRING: return Opcodes.ALOAD;
            default:
                throw new IllegalArgumentException("Unknown base type: " + t.base);
        }
    }
}