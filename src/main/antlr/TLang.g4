grammar TLang;

options {
  language = Java;
}

@header {
  package com.tonic.model.antlr;
}

COMMENT       : '//' .*? '\n' -> skip;
BLOCK_COMMENT : '/*' .*? '*/' -> skip;
WS            : [ \t\r\n]+ -> skip;

DEF     : 'def';
RETURN  : 'return';
BREAK    : 'break';
CONTINUE : 'continue';
INT     : [0-9]+;
BOOL    : 'true' | 'false';
STRING  : '"' (ESC | ~["\\\r\n])* '"';
ELSE    : 'else';
ID      : [a-zA-Z_][a-zA-Z0-9_]*;

fragment ESC
  : '\\u' [0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F]
  | '\\' [\\'"nrtbf]
  ;

// -----------------
// Program Structure
// -----------------

program
    : (methodDecl)* statement+
    ;

methodDecl
    : DEF type ID '(' paramList? ')' block
    ;

paramList
    : param (',' param)*
    ;

param
    : type ID ( '=' expression )?
    ;

statement
    : varDecl
    | assignmentStmt
    | print
    | ifStatement
    | whileStatement
    | forStatement
    | foreachStatement
    | returnStmt
    | expression ';'
    | breakStmt
    | continueStmt
    ;

breakStmt
    : BREAK ';'
    ;

continueStmt
    : CONTINUE ';'
    ;

// --------------------------
// Multi-variable Declaration
// --------------------------

varDecl
    : type varInit (',' varInit)* ';'
    ;

varInit
    : ID '=' expression
    ;

// ----------------------
// Assignment Definitions
// ----------------------

// Operator for assignment.
asmtOp
    : '=' | '+=' | '-=' | '*=' | '/='
    ;

// Simple assignment for variables.
assignment
    : ID asmtOp expression
    ;

// Array assignment (note: no trailing semicolon).
arrayAssignment
    : expression '[' expression ']' asmtOp expression
    ;

// "Assignable" covers both kinds of assignments.
assignable
    : assignment
    | arrayAssignment
    ;

// Multiple assignments can be comma–separated.
assignmentStmt
    : assignable (',' assignable)* ';'
    ;

// ----------------
// Other Statements
// ----------------

print
    : 'print' '(' expression ')' ';'
    ;

ifStatement
    : 'if' '(' expression ')' block (elseClause)?
    ;

elseClause
    : ELSE (ifStatement | block)
    ;

whileStatement
    : 'while' '(' expression ')' block
    ;

forStatement
    : 'for' '(' (init=forInit)? ';' cond=expression? ';' (update=forUpdate)? ')' block
    ;

foreachStatement
    : 'for' '(' type ID ':' expression ')' block
    ;

forInit
    : forVarDecl
    | assignment
    ;

forVarDecl
    : type ID '=' expression
    ;

forUpdate
    : assignment
    | expression
    ;

block
    : '{' statement* '}'
    | statement
    ;

returnStmt
    : RETURN expression? ';'
    ;

// --------------
// Type & Expression
// --------------

type
    : ('int' | 'bool' | 'string' | 'void') ('[' ']')*
    ;

expression
    : '!' expression                           # logicalNotExpr
    | '~' expression                           # bitwiseComplementExpr
    | '-' expression                           # unaryMinusExpr
    | ('++' | '--') ID                         # prefixIncDecExpr
    | ID ('++' | '--')                         # postfixIncDecExpr
    | methodCallExpr                           # methodCall
    | '(' expression ')'                       # parenExpr
    | literal                                  # literalExpr
    | ID                                       # varExpr
    | expression '[' expression ']'            # arrayAccessExpr
    | expression op=('*'|'/'|'%') expression     # mulDivExpr
    | expression op=('+'|'-') expression         # addSubExpr
    | expression '^' expression                  # bitwiseXorExpr
    | expression '**' expression                 # exponentiationExpr
    | expression op=('<'|'>'|'=='|'!='|'>='|'<=') expression  # comparisonExpr
    | expression op=('&&'|'||') expression       # logicalExpr
    | expression '?' expression ':' expression   # ternaryExpr
    ;

methodCallExpr
    : ID '(' argList? ')'
    ;

argList
    : expression (',' expression)*
    ;

literal
    : INT              # intLiteral
    | STRING           # stringLiteral
    | BOOL             # boolLiteral
    | arrayLiteral     # arrayLiteralExpr
    ;

arrayLiteral
    : '[' (expression (',' expression)*)? ']'
    ;
