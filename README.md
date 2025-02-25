# TLang

TLang is a simple proof-of-concept scripting language with a compiler implementation that targets JVM bytecode. In addition to basic procedural constructs, TLang now supports a variety of new operators, control structures, and conveniences that make it more expressive and concise.

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
    - [2.2.1 Multi-dimensional Arrays](#221-multi-dimensional-arrays)
- [3. Variables](#3-variables)
  - [3.1 Declaration](#31-declaration)
  - [3.2 Assignment](#32-assignment)
  - [3.3 Array Assignment](#33-array-assignment)
- [4. Expressions](#4-expressions)
  - [4.1 Operators](#41-operators)
    - [4.1.1 Arithmetic Operators](#411-arithmetic-operators)
    - [4.1.2 Comparison Operators](#412-comparison-operators)
    - [4.1.3 Logical Operators](#413-logical-operators)
    - [4.1.4 Bitwise & Exponentiation Operators](#414-bitwise--exponentiation-operators)
    - [4.1.5 Compound Assignment Operators](#415-compound-assignment-operators)
    - [4.1.6 Increment/Decrement Operators](#416-incrementdecrement-operators)
    - [4.1.7 Ternary Operator](#417-ternary-operator)
  - [4.2 Operator Precedence](#42-operator-precedence)
  - [4.3 Array Access](#43-array-access)
  - [4.4 Method Calls](#44-method-calls)
- [5. Control Structures](#5-control-structures)
  - [5.1 if Statement](#51-if-statement)
  - [5.2 while Loop](#52-while-loop)
  - [5.3 for Loop](#53-for-loop)
  - [5.4 for-each Loop](#54-for-each-loop)
  - [5.5 Break and Continue](#55-break-and-continue)
- [6. Functions](#6-functions)
  - [6.1 Declaration](#61-declaration)
  - [6.2 Calling Functions](#62-calling-functions)
  - [6.3 Default Parameter Values](#63-default-parameter-values)
- [7. Built-in Functions & Libraries](#7-built-in-functions--libraries)
  - [7.1 print](#71-print)
  - [7.2 Library Setup & Default Args](#72-library-setup--default-args)
- [8. Examples](#8-examples)
  - [8.1 Factorial Function](#81-factorial-function)
  - [8.2 Array Manipulation](#82-array-manipulation)
- [9. Notes](#9-notes)

---

## 1. Lexical Structure

### 1.1 Comments
- **Single-line:**  
  ```tlang
  // Comment until end of line
  ```
- **Block:**  
  ```tlang
  /* Comment until closing */
  ```

### 1.2 Whitespace
- **Spaces, tabs, newlines:**  
  Ignored except as separators between tokens.

### 1.3 Keywords
```
def, if, else, while, for, print, true, false, break, continue
```

### 1.4 Identifiers
- **Rules:**  
  `[a-zA-Z_][a-zA-Z0-9_]*`  
  (e.g., `x`, `myVar1`)

### 1.5 Literals
- **Integers:**  
  `[0-9]+` (e.g., `42`). A preceding minus sign denotes a negative number (e.g., `-5`).
- **Booleans:**  
  `true` or `false`.
- **Strings:**  
  Enclosed in double quotes (`"text"`). Supports standard escape sequences including character escapes (e.g., `\n`, `\t`) and Unicode escapes (e.g., `\u1234`).
- **Arrays:**  
  A comma-separated list of expressions enclosed in square brackets.  
  Example: `[1, 12, 7 * 3]`  
  (Arrays support all types.)

---

## 2. Types

### 2.1 Primitive Types
- **int:** Integer values.
- **bool:** Boolean values.
- **string:** String values.

### 2.2 Arrays
- Arrays are defined by appending `[]` to a type.  
  **Example:** `int[] arr = [1, 2, 3];`

#### 2.2.1 Multi-dimensional Arrays
- Multi-dimensional arrays are supported by appending additional `[]` (e.g., `int[][] matrix = [[1,2], [3,4]];`).

---

## 3. Variables

### 3.1 Declaration
- **Syntax:**  
  `type ID = expr;`  
  Multiple variables can be declared in one statement, separated by commas.  
  **Example:**  
  ```tlang
  int x = 5, y = 10;
  ```

### 3.2 Assignment
- **Syntax:**  
  `ID = expr;`  
  **Example:**  
  ```tlang
  x = x + 1;
  ```

### 3.3 Array Assignment
- **Syntax:**  
  `expr[expr] = expr;`  
  **Example:**  
  ```tlang
  arr[0] = 42;
  ```

---

## 4. Expressions

### 4.1 Operators

#### 4.1.1 Arithmetic Operators
- **Operators:** `+`, `-`, `*`, `/`, `%`  
- **Notes:**  
  - The `+` operator also supports **string concatenation**.  
  - Unary minus is supported for negative numbers.

#### 4.1.2 Comparison Operators
- **Operators:** `==`, `!=`, `<`, `>`, `<=`, `>=`

#### 4.1.3 Logical Operators
- **Operators:** `&&`, `||`, `!`  
- **Usage:**  
  Logical expressions evaluate to `true` or `false`.

#### 4.1.4 Bitwise & Exponentiation Operators
- **Bitwise Operators:**  
  - `^` (typically used for bitwise XOR)  
  - `~` (bitwise NOT)
- **Exponentiation Operator:**  
  - `**` (raises the left-hand operand to the power of the right-hand operand)

#### 4.1.5 Compound Assignment Operators
- **Operators:** `+=`, `-=`, `*=`, `/=`  
- **Notes:**  
  - The `+=` operator is also supported for **strings** (for concatenation).

#### 4.1.6 Increment/Decrement Operators
- **Operators:**  
  - Pre-increment: `++var`  
  - Post-increment: `var++`  
  - Pre-decrement: `--var`  
  - Post-decrement: `var--`  
- **Usage:**  
  These operators can be applied to both variables and array elements.

#### 4.1.7 Ternary Operator
- **Syntax:**  
  `condition ? expr_if_true : expr_if_false`  
- **Example:**  
  ```tlang
  int max = (a > b) ? a : b;
  ```

### 4.2 Operator Precedence
Operators in TLang are evaluated in the following order (from highest to lowest precedence):
1. **Parentheses:** `(expr)`
2. **Unary Operators:** `-`, `!`, `++`, `--`, `~`
3. **Exponentiation:** `**`
4. **Multiplicative:** `*`, `/`, `%`
5. **Additive:** `+`, `-`
6. **Bitwise:** `^` (when used as bitwise XOR)
7. **Comparison:** `<`, `>`, `<=`, `>=`, `==`, `!=`
8. **Logical AND/OR:** `&&`, `||`
9. **Ternary:** `?:`
10. **Assignment & Compound Assignments:** `=`, `+=`, `-=`, `*=`, `/=`

### 4.3 Array Access
- **Syntax:**  
  `expr[expr]`  
- **Example:**  
  ```tlang
  int value = arr[i];
  ```

### 4.4 Method Calls
- **Syntax:**  
  `ID(arg1, arg2, ...)`  
- **Example:**  
  ```tlang
  computeSum(a, b);
  ```

---

## 5. Control Structures

### 5.1 if Statement
- **Block Form:**
  ```tlang
  if (condition) {
    // then block
  } else {
    // else block
  }
  ```
- **Single Instruction Form:**  
  If the statement is a single instruction, the curly braces are optional:
  ```tlang
  if (condition)
    print("Single instruction");
  else
    print("Single instruction else");
  ```

### 5.2 while Loop
- **Block Form:**
  ```tlang
  while (condition) {
    // loop body
  }
  ```
- **Single Instruction Form:**  
  Curly braces can be omitted if the loop body is a single instruction:
  ```tlang
  while (condition)
    print("Looping");
  ```

### 5.3 for Loop
- **Traditional for-loop Syntax:**
  ```tlang
  for (int i = 0; i < 10; i = i + 1) {
    // loop body
  }
  ```
- **Notes:**  
  - The initialization section can be a variable declaration or an assignment.
  - The condition is optional (defaults to `true` if omitted).
  - The update is optional.
  - Single instruction bodies do not require brackets.

### 5.4 for-each Loop
- **Syntax:**  
  ```tlang
  for (var element : collection) {
    // loop body using element
  }
  ```
- **Example:**  
  ```tlang
  int[] numbers = [1, 2, 3, 4];
  for (var num : numbers)
    print(num);
  ```

### 5.5 Break and Continue
- **Break:**  
  Immediately exits the closest enclosing loop.
  ```tlang
  break;
  ```
- **Continue:**  
  Skips the remainder of the current loop iteration and continues with the next iteration.
  ```tlang
  continue;
  ```

---

## 6. Functions

### 6.1 Declaration
- **Syntax:**  
  ```tlang
  def functionName(type1 param1, type2 param2) {
    // function body
  }
  ```
- **Default Parameter Values:**  
  Default values can be provided:
  ```tlang
  def myFunc(int param1 = 12, bool param2 = false) {
    // body
  }
  ```

### 6.2 Calling Functions
- **Examples:**
  ```tlang
  myFunc(5, true); // As a statement
  int result = add(12, 17); // Using the returned value
  ```

### 6.3 Default Parameter Values
- Functions—including library methods—support default arguments, making it optional to supply every parameter during a call.

---

## 7. Built-in Functions & Libraries

### 7.1 print
- **Syntax:**  
  ```tlang
  print(expr);
  ```
- **Example:**  
  ```tlang
  print("Hello world!");
  ```

### 7.2 Library Setup & Default Args
- **Library Setup:**  
  New libraries can be added by placing methods in the designated library directory (see [Builtins.java](https://github.com/Tonic-Box/TLang/blob/master/src/main/java/com/tonic/model/builtins/Builtins.java)).  
- **Default Arguments:**  
  Library methods also support default parameter values. Only `int`, `boolean`, and `string` (and `void` for return type) are permitted in library method signatures.

---

## 8. Examples

### 8.1 Factorial Function
```tlang
def factorial(n int) {
  int result = 1;
  // Using a traditional for loop with single instruction body
  for (int i = 1; i <= n; i = i + 1)
    result = result * i;
  print(result);
}

factorial(5); // Prints 120
```

### 8.2 Array Manipulation
```tlang
int[] data = [10, 20, 30];
data[1] = data[1] * 2; // data becomes [10, 40, 30]
```

---

## 9. Notes
- **Scoping:**  
  Variables are block-scoped (e.g., within `{}`).
- **Type Safety:**  
  No implicit type conversions are allowed.
- **Function Returns:**  
  Functions return `void` by default unless a `return` statement is provided.
