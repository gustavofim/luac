lexer grammar LuaLexer;

@header {
    package parser;
}

// Implicit, originally

BREAK : 'break' ;
GOTO : 'goto' ;
RETURN : 'return' ;
DO : 'do' ;
WHILE : 'while' ;
REPEAT : 'repeat' ;
UNTIL : 'until' ;
IF : 'if' ;
THEN : 'then' ;
ELSEIF : 'elseif' ;
ELSE : 'else' ;
FOR : 'for' ;
IN : 'in' ;
FUNCTION : 'function' ;
LOCAL : 'local' ;
NIL : 'nil' ;
FALSE : 'false' ;
TRUE : 'true' ;
AND : 'and' ;
OR : 'or' ;
NOT : 'not' ;
END : 'end' ;

PLUS : '+' ;
MINUS : '-' ;
MULT : '*' ;
DIV : '/' ;
MOD : '%' ;
POW : '^' ;
LEN : '#' ;
BIT_AND : '&' ;
BIT_NOT : '~' ;
BIT_OR : '|' ;
LSHIFT : '<<' ;
RSHIFT : '>>' ;
FLOOR : '//' ;
EQUAL : '==' ;
NOT_EQUAL : '~=' ;
LEQ : '<=' ;
GEQ : '>=' ;
LT : '<' ;
GT : '>' ;
ASSIGN : '=' ;
LPAR : '(' ;
RPAR : ')' ;
LCUR : '{' ;
RCUR : '}' ;
LBRA : '[' ;
RBRA : ']' ;
COLCOL : '::' ;
SEMI : ';' ;
COL : ':' ;
COMMA : ',' ;
DOT : '.' ;
CONCAT : '..' ;
ELLIPSIS : '...' ;

// Explicit from original grammar

NAME
    : [a-zA-Z_][a-zA-Z_0-9]*
    ;

NORMALSTRING
    : '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;

CHARSTRING
    : '\'' ( EscapeSequence | ~('\''|'\\') )* '\''
    ;

LONGSTRING
    : '[' NESTED_STR ']'
    ;

fragment
NESTED_STR
    : '=' NESTED_STR '='
    | '[' .*? ']'
    ;

INT
    : Digit+
    ;

HEX
    : '0' [xX] HexDigit+
    ;

FLOAT
    : Digit+ '.' Digit* ExponentPart?
    | '.' Digit+ ExponentPart?
    | Digit+ ExponentPart
    ;

HEX_FLOAT
    : '0' [xX] HexDigit+ '.' HexDigit* HexExponentPart?
    | '0' [xX] '.' HexDigit+ HexExponentPart?
    | '0' [xX] HexDigit+ HexExponentPart
    ;

fragment
ExponentPart
    : [eE] [+-]? Digit+
    ;

fragment
HexExponentPart
    : [pP] [+-]? Digit+
    ;

fragment
EscapeSequence
    : '\\' [abfnrtvz"'\\]
    | '\\' '\r'? '\n'
    | DecimalEscape
    | HexEscape
    | UtfEscape
    ;

fragment
DecimalEscape
    : '\\' Digit
    | '\\' Digit Digit
    | '\\' [0-2] Digit Digit
    ;

fragment
HexEscape
    : '\\' 'x' HexDigit HexDigit
    ;

fragment
UtfEscape
    : '\\' 'u{' HexDigit+ '}'
    ;

fragment
Digit
    : [0-9]
    ;

fragment
HexDigit
    : [0-9a-fA-F]
    ;

fragment
SingleLineInputCharacter
    : ~[\r\n\u0085\u2028\u2029]
    ;

COMMENT
    : '--[' NESTED_STR ']' -> channel(HIDDEN)
    ;

LINE_COMMENT
    : '--' SingleLineInputCharacter* -> channel(HIDDEN)
    ;

WS
    : [ \t\u000C\r\n]+ -> skip
    ;

SHEBANG
    : '#' '!' SingleLineInputCharacter* -> channel(HIDDEN)
    ;