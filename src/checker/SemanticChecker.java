package checker;

import static ast.NodeKind.ASSIGN_NODE;
import static ast.NodeKind.BLOCK_NODE;
import static ast.NodeKind.EQ_NODE;
import static ast.NodeKind.IF_NODE;
import static ast.NodeKind.LT_NODE;
import static ast.NodeKind.MINUS_NODE;
import static ast.NodeKind.OVER_NODE;
import static ast.NodeKind.PLUS_NODE;
import static ast.NodeKind.PROGRAM_NODE;
import static ast.NodeKind.READ_NODE;
import static ast.NodeKind.REPEAT_NODE;
import static ast.NodeKind.VAL_NODE;
import static ast.NodeKind.TIMES_NODE;
import static ast.NodeKind.VAR_DECL_NODE;
import static ast.NodeKind.VAR_LIST_NODE;
import static ast.NodeKind.VAR_USE_NODE;
import static ast.NodeKind.WRITE_NODE;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import ast.AST;
import parser.LuaParser;
import parser.LuaParserBaseVisitor;
import parser.LuaParser.BlockContext;
import parser.LuaParser.ChunkContext;
import parser.LuaParser.StatContext;
import table.IdentifierTable;

/*
 * Analisador semântico de LuaLang implementado como um visitor
 * da ParseTree do ANTLR. A classe LuaParserBaseVisitor é gerada
 * automaticamente e já possui métodos padrão aonde o comportamento
 * é só visitar todos os filhos. Por conta disto, basta sobreescrever
 * os métodos que a gente quer alterar. Neste caso, todos foram sobreescritos.
 *
 * No laboratório anterior, foi usado Type no tipo genérico do
 * LuaParserBaseVisitor porque a gente só estava fazendo uma verificação
 * simples dos tipos primitivos. Agora o tipo declarado é AST, pois o
 * analisador semântico também realiza a construção da AST na mesma passada.
 * Assim, se a análise semântica (uso de variáveis e tipos) terminar sem erros,
 * então temos no final uma AST que representa o programa de entrada.
 * Em linguagens mais complexas é provável que sejam necessárias mais passadas,
 * por exemplo, uma para análise semântica e outra para a construção da AST.
 * Neste caso, talvez você tenha de implementar dois visitadores diferentes.
 *
 * Lembre que o caminhamento pela Parse Tree é top-down. Assim, é preciso sempre
 * visitar os filhos de um nó primeiro para construir as subárvores dos filhos.
 * No Bison isso já acontecia automaticamente porque o parsing lá é bottom-up e
 * as ações semânticas do parser já faziam a construção da AST junto com a análise
 * sintática. Aqui, é o inverso, por isso temos que visitar os filhos primeiro.
 */
public class SemanticChecker extends LuaParserBaseVisitor<AST> {

	private IdentifierTable idt = new IdentifierTable();

    AST root; // Nó raiz da AST sendo construída.

    // Testa se o dado token foi declarado antes.
    // Se sim, cria e retorna um nó de 'var use'.
    // AST checkVar(Token token) {
    // 	String text = token.getText();
    // 	int line = token.getLine();
   	// 	int idx = vt.lookupVar(text);
    // 	if (idx == -1) {
    // 		System.err.printf("SEMANTIC ERROR (%d): variable '%s' was not declared.\n", line, text);
    // 		System.exit(1);
    //         return null; // Never reached.
    //     }
    // 	return new AST(VAR_USE_NODE, idx, vt.getType(idx));
    // }

    // Cria uma nova variável a partir do dado token.
    // Retorna um nó do tipo 'var declaration'.
    // AST newVar(Token token) {
    // 	String text = token.getText();
    // 	int line = token.getLine();
   	// 	int idx = vt.lookupVar(text);
    //     if (idx != -1) {
    //     	System.err.printf("SEMANTIC ERROR (%d): variable '%s' already declared at line %d.\n", line, text, vt.getLine(idx));
    //     	System.exit(1);
    //         return null; // Never reached.
    //     }
    //     idx = vt.addVar(text, line, lastDeclType);
    //     return new AST(VAR_DECL_NODE, idx, lastDeclType);
    // }

    // ----------------------------------------------------------------------------

    // Exibe o conteúdo das tabelas em stdout.
    public void printTables() {
        System.out.print("\n\n");
        System.out.print(idt);
        System.out.print("\n\n");
    }

    // Exibe a AST no formato DOT em stderr.
    public void printAST() {
    	AST.printDot(root, idt);
    }

    // ----------------------------------------------------------------------------

    // Visitadores.

	@Override
	public AST visitChunk(ChunkContext ctx) {
		this.root = visit(ctx.block());
		return this.root;
	}

	@Override
	public AST visitBlock(BlockContext ctx) {
		AST node = AST.newSubtree(BLOCK_NODE);
		ctx.stat().forEach((stat) -> {
			AST child = visit(stat);
			node.addChild(child);
		});
		return node;

	}

	@Override
	public AST visitStat(StatContext ctx) {
		return new AST(VAL_NODE, "HEEy");
	}

    // ----------------------------------------------------------------------------


}
