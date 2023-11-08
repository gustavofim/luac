# Lua Compiler

[Manual](https://www.lua.org/manual/5.4/manual.html) and
[grammar](https://github.com/antlr/grammars-v4/tree/master/lua) references.

# Lexer and Parser
Inconsistencies with the reference were removed from the grammar, those were:

- Some aditional escapes for WoW Lua;
- Implementation of `continue`;

# Semantic analysis

- Typing and coersion:
    - Runtime, thats the point

- Variable declaration:
    - Undeclared variables evaluate to Nil -> Runtime
        - Trying to call or access index of Nil -> Compile (single table for identifiers?)

- Function declaration and parameters:
    - Extra parameters are ignored
    - Non specified parameters evaluate to Nil -> Runtime

- Tables
    - Fields evaluate to Nil when not initialized

# Coersions
Strings may be coerced to number for arithmetic operations (not for boolean).

## Notes
- Assignment: varlist -> var -> identifier
- Evaluation: varOrExp -> var -> identifier

### Jasmin stuff
- Compile: `java -jar jasmin.jar program.j`
- Run with runtime: `java -cp "LuaRuntime.jar:." Program`
- Small example:

```java
.class public Program
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit locals 100
	.limit stack 100

	ldc "011"
	invokestatic LuaRuntime/Runtime/wrapConst(Ljava/lang/String;)LLuaRuntime/LuaType;

	ldc2_w 9.5 ; Pushing double
	invokestatic LuaRuntime/Runtime/wrapConst(D)LLuaRuntime/LuaType;

	ldc 1
	invokestatic LuaRuntime/Runtime/aritOp(LLuaRuntime/LuaType;LLuaRuntime/LuaType;I)LLuaRuntime/LuaType;

	getstatic java/lang/System/out Ljava/io/PrintStream;
	swap									; Pass obj "toString"
	invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V

	return
.end method
```
