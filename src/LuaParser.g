parser grammar LuaParser;


options {
    tokenVocab = LuaLexer;
}

@header {
    package parser;
}

chunk
    : block EOF
    ;

block
    : stat* laststat?
    ;

stat
    : SEMI #semi
    | varlist ASSIGN explist #assign
    | functioncall #functionCall
    | label #temp
    | BREAK #break
    | GOTO NAME #goto
    | DO block END #do
    | WHILE exp DO block END #while
    | REPEAT block UNTIL exp #repeat
    | IF exp THEN block (ELSEIF exp THEN block)* (ELSE block)? END #ifThenElse
    | FOR NAME ASSIGN exp COMMA exp (COMMA exp)? DO block END #for
    | FOR namelist IN explist DO block END #genFor
    | FUNCTION funcname funcbody #functionDef
    | LOCAL FUNCTION NAME funcbody #localFunctionDef
    | LOCAL attnamelist (ASSIGN explist)? #local
    ;

attnamelist
    : NAME attrib (COMMA NAME attrib)*
    ;

attrib
    : (LT NAME GT)?
    ;

laststat
    : RETURN explist? SEMI?
    ;

label
    : COLCOL NAME COLCOL
    ;

funcname
    : NAME (DOT NAME)* (COL NAME)?
    ;

varlist
    : var (COMMA var)*
    ;

namelist
    : NAME (COMMA NAME)*
    ;

explist
    : (exp COMMA)* exp
    ;

exp
    : NIL #nil | FALSE #false | TRUE #true
    | number #numConst
    | string #strConst
    | '...' #ellipsis
    | functiondef #functionDefExp
    | prefixexp #prefix
    | tableconstructor #table
    | <assoc=right> exp operatorPower exp #power
    | operatorUnary exp #unary
    | exp operatorMulDivMod exp #multDivMod
    | exp operatorAddSub exp #addSub
    | <assoc=right> exp operatorStrcat exp #strCat
    | exp operatorComparison exp #comparison
    | exp operatorAnd exp #and
    | exp operatorOr exp #or
    | exp operatorBitwise exp #bitwise
    ;

prefixexp
    : varOrExp nameAndArgs*
    ;

functioncall
    : varOrExp nameAndArgs+
    ;

varOrExp
    : var | LPAR exp RPAR
    ;

var
    : (NAME | LPAR exp RPAR varSuffix) varSuffix*
    ;

varSuffix
    : nameAndArgs* (LBRA exp RBRA | DOT NAME)
    ;

nameAndArgs
    : (COL NAME)? args
    ;

args
    : LPAR explist? RPAR #argList | tableconstructor #tableArg | string #stringConstArg
    ;

functiondef
    : FUNCTION funcbody
    ;

funcbody
    : LPAR parlist? RPAR block END
    ;

parlist
    : namelist (COMMA ELLIPSIS)? | ELLIPSIS
    ;

tableconstructor
    : LCUR fieldlist? RCUR
    ;

fieldlist
    : field (fieldsep field)* fieldsep?
    ;

field
    : LBRA exp RBRA ASSIGN exp #tableBracket | NAME ASSIGN exp #tableAssign | exp #tableExp
    ;

fieldsep
    : COMMA | SEMI
    ;

operatorOr
	: OR;

operatorAnd
	: AND;

operatorComparison
	: LT | GT | LEQ | GEQ | NOT_EQUAL | EQUAL;

operatorStrcat
	: CONCAT;

operatorAddSub
	: PLUS | MINUS;

operatorMulDivMod
	: MULT | DIV | MOD | FLOOR;

operatorBitwise
	: BIT_AND | BIT_OR | BIT_NOT | LSHIFT | RSHIFT;

operatorUnary
    : NOT | LEN | MINUS | BIT_NOT;

operatorPower
    : POW;

number
    : INT | HEX | FLOAT | HEX_FLOAT
    ;

string
    : NORMALSTRING | CHARSTRING | LONGSTRING
    ;