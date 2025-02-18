# TLang
TLang is a simple POC showcasing a custom scripting language with a compiler implimentation that targets JVM bytecode.

## Table of Contents
- [1. Lexical Structure](#1-lexical-structure)
  - [1.1 Comments](#11-comments)
  - [1.2 Whitespace](#12-whitespace)
  - [1.3 Keywords](#13-keywords)
  - [1.4 Identifiers](#14-identifiers)
  - [1.5 Literals](#15-literals)
- [2. Types](#2-types)
  - [2.1 Primitive Types](#21-primitive-types)
  - [2.2 Arrays](#22-arrays)
- [3. Variables](#3-variables)
  - [3.1 Declaration](#31-declaration)
  - [3.2 Assignment](#32-assignment)
  - [3.3 Array Assignment](#33-array-assignment)
- [4. Expressions](#4-expressions)
  - [4.1 Operators](#41-operators)
  - [4.2 Operator Precedence](#42-operator-precedence)
  - [4.3 Array Access](#43-array-access)
  - [4.4 Method Calls](#44-method-calls)
- [5. Control Structures](#5-control-structures)
  - [5.1 if Statement](#51-if-statement)
  - [5.2 while Loop](#52-while-loop)
  - [5.3 for Loop](#53-for-loop)
- [6. Functions](#6-functions)
  - [6.1 Declaration](#61-declaration)
  - [6.2 Calling Functions](#62-calling-functions)
- [7. Built-in Functions](#7-built-in-functions)
  - [7.1 print](#71-print)
  - [7.2 Implementing New Built-in Functions](#72-implimenting-new-built-in-functions)
- [8. Examples](#8-examples)
  - [8.1 Factorial Function](#81-factorial-function)
  - [8.2 Array Manipulation](#82-array-manipulation)
- [9. Notes](#9-notes)

---


# Example Compilation & Execution
### **Example Script:** [demo.tlang](http://github.com/Tonic-Box/TLang/blob/master/src/main/resources/com/tonic/demo/demo.tlang)
### **Example Compilation & Execution:** [Compile & Run](https://github.com/Tonic-Box/TLang/blob/master/src/main/java/com/tonic/demo/Main.java)

---

# The TLang Language Documentation

TLang is a procedural programming language with support for functions, variables, control structures, and basic data types. This documentation describes the syntax and semantics of TLang as defined by the provided ANTLR grammar.

## 1. Lexical Structure

### 1.1 Comments
- **Single-line:** `// Comment until end of line`
- **Block:** `/* Comment until closing */`

### 1.2 Whitespace
- **Spaces, tabs, newlines:** Ignored except as separators.

### 1.3 Keywords
```
def, var, if, else, while, for, print, true, false
```

### 1.4 Identifiers
- **Rules:** `[a-zA-Z_][a-zA-Z0-9_]*` (e.g., `x`, `myVar1`).

### 1.5 Literals
- **Integers:** `[0-9]+` (e.g., `42`).
- **Booleans:** `true` or `false`.
- **Strings:** `"text"` (supports escape sequences if implemented).
- **Arrays:** `[expr1, expr2, ...]` (e.g., `[1, 12, 7 * 3]`, supports all types).

## 2. Types

### 2.1 Primitive Types
- **int:** Integer values.
- **bool:** Boolean values (`true`/`false`).
- **string:** String values.

### 2.2 Arrays
- Defined by appending [] to a type (e.g., int[]).
- **Example:** `var int[] arr = [1, 2, 3];`

## 3. Variables

### 3.1 Declaration
- **Syntax:** `var type ID = expr;`
- **Example:** `var int x = 5;`

### 3.2 Assignment
- **Syntax:** `ID = expr;`
- **Example:** `x = x + 1;`

### 3.3 Array Assignment
- **Syntax:** `expr[expr] = expr;`
- **Example:** `arr[0] = 42;`

## 4. Expressions

### 4.1 Operators
- **Arithmetic:** `+`, `-`, `*`, `/`
- **Comparison:** `==`, `!=`, `<`, `>`
- **Parentheses:** `(expr)`

### 4.2 Operator Precedence
1. `*`, `/`
2. `+`, `-`
3. `<`, `>`, `==`, `!=`

### 4.3 Array Access
- **Syntax:** `expr[expr]`
- **Example:** `arr[i]`

### 4.4 Method Calls
- **Syntax:** `ID(arg1, arg2, ...)`
- **Example:** `computeSum(a, b)`

## 5. Control Structures

### 5.1 if Statement
```c
if (condition) {
  // then block
} else {
  // else block
}
```
- The else clause can chain to another if.

### 5.2 while Loop
```c
while (condition) {
  // body
}
```

### 5.3 for Loop
```c
for (var int i = 0; i < 10; i = i + 1) {
  // body
}
```
- **Init:** variable declaration or assignment.
- **Cond:** Optional (defaults to `true`).
- **Update:** Optional assignment.

## 6. Functions

### 6.1 Declaration
```c
def myFunc(param1 int, param2 bool) {
  // body
}
```
- **Parameters:** Comma-separated type ID pairs.
- No return value (functions are void).

### 6.2 Calling Functions
```c
myFunc(5, true); // As a statement
var int x = max(12, 17); //with a return
```

## 7. Built-in Functions

### 7.1 print
- **Syntax:** `print(expr);`
- **Example:** print("Hello world!");

### 7.2 Implimenting New Built-in Functions
- Simply add new methods to: [Builtins.java](https://github.com/Tonic-Box/TLang/blob/master/src/main/java/com/tonic/model/builtins/Builtins.java)
- **Note:** only `int`, `boolean`, and `String` may be used in these for parameters or returns (with the exception of `void` also allowed for returns.

## 8. Examples

### 8.1 Factorial Function
```c
def factorial(n int) {
  var int result = 1;
  for (var int i = 1; i < n + 1; i = i + 1) {
    result = result * i;
  }
  print(result);
}

factorial(5); // Prints 120
```

### 8.2 Array Manipulation
```c
var int[] data = [10, 20, 30];
data[1] = data[1] * 2; // data becomes [10, 40, 30]
```

# 9. Notes
- **Scoping:** Variables are block-scoped (e.g., inside {}).
- **Type Safety:** No implicit type conversions;
- **Function Returns:** Functions do not return values (no return keyword).
