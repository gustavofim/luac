# Lua Compiler

[Manual](https://www.lua.org/manual/5.4/manual.html) and
[grammar](https://github.com/antlr/grammars-v4/tree/master/lua) references.

# Lexer and Parser
Inconsistencies with the reference were removed from the grammar, those were:

- Some aditional escapes for WoW Lua;
- Implementation of `continue`;

# Semantic analisys

## Runtime

- Types?

## Compile-time

- Undeclared functions?