package ast;

// Enumeração dos tipos de nós de uma AST.
// Adaptado da versão original em C.
// Algumas pessoas podem preferir criar uma hierarquia de herança para os
// nós para deixar o código "mais OO". Particularmente eu não sou muito
// fã, acho que só complica mais as coisas. Imagine uma classe abstrata AST
// com mais de 20 classes herdando dela, uma classe para cada tipo de nó...
public enum NodeKind {
    ARGS_NODE,
    ARIT_OP_NODE,
    ASSIGN_NODE,
    BLOCK_NODE,
    BOOL_OP_NODE,
    EQ_NODE,
    EXP_LIST_NODE,
    FALSE_NODE,
    FOR_NODE,
    FUNC_DEF_NODE,
    GE_NODE,
    GT_NODE,
    IF_NODE,
    LE_NODE,
    LOCAL_NODE,
    LT_NODE,
    MINUS_NODE,
    MOD_NODE,
    NEQ_NODE,
    NIL_NODE,
    NUM_NODE,
    OVER_NODE,
    PLUS_NODE,
    // PROGRAM_NODE,
    // READ_NODE,
    REPEAT_NODE,
    // STAT_NODE,
    RELAT_OP_NODE,
    TRUE_NODE,
    TIMES_NODE,
    UNARY_OP_NODE,
    VAL_NODE,
    VAR_DECL_NODE,
    VAR_LIST_NODE,
    VAR_USE_NODE,
    // WRITE_NODE;
    WHILE_NODE;

	public String toString() {
		switch(this) {
            case ARGS_NODE:   return "args";
            case ARIT_OP_NODE:   return "arit_op";
            case ASSIGN_NODE:   return "=";
            case BOOL_OP_NODE:    return "bool_op";
            case BLOCK_NODE:    return "block";
            case EQ_NODE:       return "==";
            case EXP_LIST_NODE: return "exp_list";
            case FALSE_NODE: return "FALSE";
            case FOR_NODE: return "for";
            case FUNC_DEF_NODE: return "def";
            case GE_NODE:       return ">=";
            case GT_NODE:       return ">";
            case IF_NODE:       return "if";
            case LE_NODE:       return "<=";
            case LOCAL_NODE:       return "local";
            case LT_NODE:       return "<";
            case MINUS_NODE:    return "-";
            case MOD_NODE:    return "%";
            case NEQ_NODE:    return "~=";
            case NIL_NODE:      return "NIL";
            case NUM_NODE:      return "";
            case OVER_NODE:     return "/";
            case PLUS_NODE:     return "+";
            // case PROGRAM_NODE:  return "program";
            // case READ_NODE:     return "read";
            case REPEAT_NODE:   return "repeat";
            // case STAT_NODE:     return "stat";
            case RELAT_OP_NODE:    return "relat_op";
            case TIMES_NODE:    return "*";
            case TRUE_NODE:    return "TRUE";
            case UNARY_OP_NODE:    return "unary_op";
            case VAL_NODE:      return "";
            case VAR_DECL_NODE: return "var_decl";
            case VAR_LIST_NODE: return "var_list";
            case VAR_USE_NODE:  return "var_use";
            // case WRITE_NODE:    return "write";
            case WHILE_NODE:    return "while";
			default:
				System.err.println("ERROR: Fall through in NodeKind enumeration!");
				System.exit(1);
				return ""; // Never reached.
		}
	}

	public static boolean hasData(NodeKind kind) {
		switch(kind) {
	        case ARIT_OP_NODE:
	        case UNARY_OP_NODE:
	        case RELAT_OP_NODE:
	        case BOOL_OP_NODE:
	        case VAL_NODE:
	        case NUM_NODE:
	        case VAR_DECL_NODE:
	        case VAR_USE_NODE:
	            return true;
	        default:
	            return false;
		}
	}
}
