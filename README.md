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
