package ast;

/*
 * Classe abstrata que define a interface do visitor para a AST.
 * Implementa o despacho do método 'visit' conforme o 'kind' do nó.
 * Com isso, basta herdar desta classe para criar um interpretador
 * ou gerador de código.
 */
public abstract class ASTBaseVisitor<T> {

	// Único método público. Começa a visita a partir do nó raiz
	// passado. Precisa ter outro nome porque tem a mesma assinatura
	// que o método "genérico" 'visit'.
	public void execute(AST root) {
		visit(root);
	}
	
	// Método "genérico" que despacha a visitação para os métodos
	// especializados conforme o 'kind' do nó atual. Igual ao código
	// em C. Novamente fica o argumento sobre usar OO ou não aqui.
	// Se tivéssemos trocentas classes especializando o nó da AST
	// esse despacho seria feito pela JVM. Aqui precisa fazer na mão.
	// Por outro lado, assim não precisa de trocentas classes com o
	// código todo espalhado entre elas...
	protected T visit(AST node) {
		switch(node.kind) {
	        case ARGS_NODE:     return visitArgs(node);
	        case ARIT_OP_NODE:   return visitAritOp(node);
	        case ASSIGN_NODE:   return visitAssign(node);
	        case BOOL_OP_NODE:    return visitBoolOp(node);
	        case BLOCK_NODE:    return visitBlock(node);
	        case DOUBLE_NODE:    return visitDouble(node);
	        case FALSE_NODE:    return visitFalse(node);
	        case FUNC_DEF_NODE:    return visitFuncDef(node);
	        case FUNC_STAT_NODE:    return visitFuncStat(node);
	        case EXP_LIST_NODE:    return visitExpList(node);
	        case IF_NODE:       return visitIf(node);
	        case INDEX_NODE:       return visitIndex(node);
	        case INT_NODE:       return visitInt(node);
	        case LAST_INDEX_NODE:  return null;//visitLocal(node);
	        case LOCAL_NODE:  return visitLocal(node);
	        case NIL_NODE:      return visitNil(node);
	        case NUM_NODE:      return visitNum(node);
	        case PARAMS_NODE:  return visitParams(node);
	        case PARAM_NODE:  return visitParam(node);
	        case REPEAT_NODE:   return visitRepeat(node);
	        case RETURN_NODE:   return visitReturn(node);
	        case RELAT_OP_NODE:    return visitRelatOp(node);
	        case TABLE_NODE:    return visitTable(node);
	        case TABLE_FIELD_NODE:    return visitTableField(node);
	        case TRUE_NODE:    return visitTrue(node);
	        case UNARY_OP_NODE:    return visitUnaryOp(node);
	        case VAL_NODE: return visitVal(node);
	        case VAR_DECL_NODE: return visitVarDecl(node);
	        case VAR_USE_NODE:  return visitVarUse(node);
	        case WHILE_NODE:    return visitWhile(node);
	
	        default:
	            System.err.printf("Invalid kind: %s!\n", node.kind.toString());
	            System.exit(1);
	            return null;
		}
	}
	
	// Métodos especializados para visitar um nó com um certo 'kind'.

	protected abstract T visitArgs(AST node);

	protected abstract T visitAritOp(AST node);

	protected abstract T visitAssign(AST node);

	protected abstract T visitBoolOp(AST node);

	protected abstract T visitBlock(AST node);

	protected abstract T visitDouble(AST node);

	protected abstract T visitFalse(AST node);

	protected abstract T visitFuncDef(AST node);

	protected abstract T visitFuncStat(AST node);

	protected abstract T visitExpList(AST node);

	protected abstract T visitIf(AST node);

	protected abstract T visitIndex(AST node);

	protected abstract T visitInt(AST node);

	protected abstract T visitLocal(AST node);

	protected abstract T visitNil(AST node);
	
	protected abstract T visitNum(AST node);

	protected abstract T visitParams(AST node);

	protected abstract T visitParam(AST node);

	protected abstract T visitRepeat(AST node);

	protected abstract T visitReturn(AST node);

	protected abstract T visitRelatOp(AST node);

	protected abstract T visitTable(AST node);

	protected abstract T visitTableField(AST node);

	protected abstract T visitTrue(AST node);

	protected abstract T visitUnaryOp(AST node);

	protected abstract T visitVal(AST node);

	protected abstract T visitVarDecl(AST node);

	protected abstract T visitVarUse(AST node);

	protected abstract T visitWhile(AST node);
}