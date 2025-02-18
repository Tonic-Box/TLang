// Generated from TLang.g4 by ANTLR 4.13.1

  package com.tonic.model.antlr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TLangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TLangVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TLangParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(TLangParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#methodDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodDecl(TLangParser.MethodDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(TLangParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(TLangParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(TLangParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#breakStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(TLangParser.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#continueStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStmt(TLangParser.ContinueStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(TLangParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#varInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarInit(TLangParser.VarInitContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#asmtOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsmtOp(TLangParser.AsmtOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(TLangParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#arrayAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayAssignment(TLangParser.ArrayAssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#assignable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignable(TLangParser.AssignableContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#assignmentStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentStmt(TLangParser.AssignmentStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#print}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrint(TLangParser.PrintContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(TLangParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#elseClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseClause(TLangParser.ElseClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(TLangParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#forStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStatement(TLangParser.ForStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#foreachStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForeachStatement(TLangParser.ForeachStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#forInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForInit(TLangParser.ForInitContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#forVarDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForVarDecl(TLangParser.ForVarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#forUpdate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForUpdate(TLangParser.ForUpdateContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(TLangParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#returnStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(TLangParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(TLangParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bitwiseComplementExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitwiseComplementExpr(TLangParser.BitwiseComplementExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code addSubExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddSubExpr(TLangParser.AddSubExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bitwiseXorExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitwiseXorExpr(TLangParser.BitwiseXorExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exponentiationExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExponentiationExpr(TLangParser.ExponentiationExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code prefixIncDecExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixIncDecExpr(TLangParser.PrefixIncDecExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code postfixIncDecExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostfixIncDecExpr(TLangParser.PostfixIncDecExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code logicalExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalExpr(TLangParser.LogicalExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparisonExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonExpr(TLangParser.ComparisonExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpr(TLangParser.ParenExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code logicalNotExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalNotExpr(TLangParser.LogicalNotExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarExpr(TLangParser.VarExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayAccessExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayAccessExpr(TLangParser.ArrayAccessExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unaryMinusExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryMinusExpr(TLangParser.UnaryMinusExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ternaryExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTernaryExpr(TLangParser.TernaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literalExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralExpr(TLangParser.LiteralExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mulDivExpr}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulDivExpr(TLangParser.MulDivExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code methodCall}
	 * labeled alternative in {@link TLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodCall(TLangParser.MethodCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#methodCallExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodCallExpr(TLangParser.MethodCallExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#argList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgList(TLangParser.ArgListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code intLiteral}
	 * labeled alternative in {@link TLangParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntLiteral(TLangParser.IntLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link TLangParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(TLangParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code boolLiteral}
	 * labeled alternative in {@link TLangParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolLiteral(TLangParser.BoolLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayLiteralExpr}
	 * labeled alternative in {@link TLangParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayLiteralExpr(TLangParser.ArrayLiteralExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#arrayLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayLiteral(TLangParser.ArrayLiteralContext ctx);
}