package ast;

import static ast.NodeKind.NUM_NODE;

import java.util.ArrayList;
import java.util.List;

// import table.IdentifierTable;

// Implementação dos nós da AST.
public class AST {

	// Todos os campos são finais para simplificar, assim não precisa de getter/setter.
	// Note que não há union em Java, então o truque de ler
	// e/ou escrever o campo com formatos diferentes não funciona aqui.
	// Os campos 'data' NÃO ficam sincronizados!
	public  final NodeKind kind;
	public  final String data;
	public  final Double numData;
	private final List<AST> children; // Privado para que a manipulação da lista seja controlável.

	public AST(NodeKind kind, String data, Double numData) {
		this.kind = kind;
		this.data = data;
		this.numData = numData;
		this.children = new ArrayList<AST>();
	}

	public AST(NodeKind kind, String data) {
		this(kind, data, 0.0);
	}

	public AST(NodeKind kind, Double data) {
		this(kind, "", data);
	}

	// Adiciona um novo filho ao nó.
	public void addChild(AST child) {
		// A lista cresce automaticamente, então nunca vai dar erro ao adicionar.
		this.children.add(child);
	}

	// Retorna o filho no índice passado.
	// Não há nenhuma verificação de erros!
	public AST getChild(int idx) {
		// Claro que um código em produção precisa testar o índice antes para
		// evitar uma exceção.
	    return this.children.get(idx);
	}

	// Cria um nó e pendura todos os filhos passados como argumento.
	public static AST newSubtree(NodeKind kind, AST... children) {
		AST node = new AST(kind, "");
	    for (AST child: children) {
	    	node.addChild(child);
	    }
	    return node;
	}

	// Retorna o número de filhos do nó.
	public int getChildCount() {
		return this.children.size();
	}

	// Variáveis internas usadas para geração da saída em DOT.
	// Estáticas porque só precisamos de uma instância.
	private static int nr;
	// private static IdentifierTable idt;

	// Imprime recursivamente a codificação em DOT da subárvore começando no nó atual.
	// Usa stderr como saída para facilitar o redirecionamento, mas isso é só um hack.
	private int printNodeDot() {
		int myNr = nr++;

	    System.err.printf("node%d[label=\"", myNr);
	    // if (this.kind == NodeKind.VAR_DECL_NODE || this.kind == NodeKind.VAR_USE_NODE) {
	    // 	System.err.printf("%s@", this.data);
	    // } else {
	    	System.err.printf("%s", this.kind.toString());
	    // }
	    if (NodeKind.hasData(this.kind)) {
			if (this.kind == NUM_NODE) {
				System.err.printf("num: %f", this.numData);
			} else if (this.kind == NodeKind.VAR_DECL_NODE
						|| this.kind == NodeKind.VAR_USE_NODE
						|| this.kind == NodeKind.PARAM_NODE
						|| this.kind == NodeKind.ARIT_OP_NODE
						|| this.kind == NodeKind.RELAT_OP_NODE
						|| this.kind == NodeKind.BOOL_OP_NODE
						|| this.kind == NodeKind.UNARY_OP_NODE) {
				System.err.printf(": %s", this.data);
			} else {
				System.err.printf("str: %s", this.data);
			}
		}
	    System.err.printf("\"];\n");

	    for (int i = 0; i < this.children.size(); i++) {
			if (this.children.get(i) == null) {
				System.err.println("aaaaaaaa");
			}
	        int childNr = this.children.get(i).printNodeDot();
	        System.err.printf("node%d -> node%d;\n", myNr, childNr);
	    }
	    return myNr;
	}

	// Imprime a árvore toda em stderr.
	public static void printDot(AST tree) {//, IdentifierTable table) {
	    nr = 0;
	    // idt = table;
	    System.err.printf("digraph {\ngraph [ordering=\"out\"];\n");
	    tree.printNodeDot();
	    System.err.printf("}\n");
	}
}
