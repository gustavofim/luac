package checker;

import static ast.NodeKind.ARGS_NODE;
import static ast.NodeKind.ARIT_OP_NODE;
import static ast.NodeKind.ASSIGN_NODE;
import static ast.NodeKind.BLOCK_NODE;
import static ast.NodeKind.EQ_NODE;
import static ast.NodeKind.EXP_LIST_NODE;
import static ast.NodeKind.GE_NODE;
import static ast.NodeKind.GT_NODE;
import static ast.NodeKind.LE_NODE;
import static ast.NodeKind.LT_NODE;
import static ast.NodeKind.MINUS_NODE;
import static ast.NodeKind.MOD_NODE;
import static ast.NodeKind.NEQ_NODE;
import static ast.NodeKind.NUM_NODE;
import static ast.NodeKind.OVER_NODE;
import static ast.NodeKind.PLUS_NODE;
import static ast.NodeKind.RELAT_OP_NODE;
import static ast.NodeKind.TIMES_NODE;
import static ast.NodeKind.VAL_NODE;
import static ast.NodeKind.VAR_DECL_NODE;
import static ast.NodeKind.VAR_LIST_NODE;
import static ast.NodeKind.VAR_USE_NODE;
import static ast.NodeKind.WHILE_NODE;

import ast.AST;
import ast.NodeKind;
import parser.LuaParserBaseVisitor;
import parser.LuaParser.AddSubContext;
import parser.LuaParser.ArgListContext;
import parser.LuaParser.AssignContext;
import parser.LuaParser.BlockContext;
import parser.LuaParser.ChunkContext;
import parser.LuaParser.ComparisonContext;
import parser.LuaParser.ExplistContext;
import parser.LuaParser.FunctioncallContext;
import parser.LuaParser.MultDivModContext;
import parser.LuaParser.NumberContext;
import parser.LuaParser.StringContext;
import parser.LuaParser.VarContext;
import parser.LuaParser.VarOrExpContext;
import parser.LuaParser.VarlistContext;
import parser.LuaParser.WhileContext;
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

    public SemanticChecker() {
        idt.add("print", 0);
        idt.add("read", 0);
    }

    // ----------------------------------------------------------------------------

    // Exibe o conteúdo das tabelas em stdout.
    public void printTables() {
        System.out.print("\n\n");
        System.out.print(idt);
        System.out.print("\n\n");
    }

    // Exibe a AST no formato DOT em stderr.
    public void printAST() {
    	AST.printDot(root);
    }

    // Retorna a AST construída ao final da análise.
    public AST getAST() {
    	return this.root;
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
		ctx.stat().forEach((child) -> {
            AST temp = visit(child);
			if (temp != null) node.addChild(temp);
            // node.addChild(visit(child));
		});
		return node;
	}

	@Override
	public AST visitAssign(AssignContext ctx) {
		AST node = AST.newSubtree(ASSIGN_NODE);
        node.addChild(visit(ctx.varlist()));
        node.addChild(visit(ctx.explist()));
		return node;
	}

	@Override
	public AST visitExplist(ExplistContext ctx) {
		AST node = AST.newSubtree(EXP_LIST_NODE);
		ctx.exp().forEach((child) -> {
            AST temp = visit(child);
			if (temp != null) node.addChild(temp);
            // node.addChild(visit(child));
		});
        return node;
	}

    @Override
    public AST visitVarOrExp(VarOrExpContext ctx) {
        if (ctx.LPAR() == null) {
            return visit(ctx.var());
        } else {
            return visit(ctx.exp());
        }
    }

    @Override
    public AST visitNumber(NumberContext ctx) {
        return new AST(NUM_NODE, Double.parseDouble(ctx.getChild(0).toString()));
    }

    @Override
    public AST visitString(StringContext ctx) {
        String str = ctx.getChild(0).toString();
        str = str.substring(1, str.length()-1);
        return new AST(VAL_NODE, str);
    }

    @Override
    public AST visitVarlist(VarlistContext ctx) {
		AST node = AST.newSubtree(VAR_LIST_NODE);
		ctx.var().forEach((child) -> {
            AST childNode = visit(child);
            if (!idt.lookup(childNode.data)) {
                idt.add(childNode.data, (int)Math.round(childNode.numData));
            }
			node.addChild(new AST(VAR_DECL_NODE, childNode.data, childNode.numData));
		});
		return node;
    }

    @Override
    public AST visitVar(VarContext ctx) {
        return new AST(VAR_USE_NODE, ctx.NAME().getSymbol().getText(), (double)ctx.NAME().getSymbol().getLine());
    }

    // @Override
    // public AST visitAddSub(AddSubContext ctx) {
    //     NodeKind kind;
    //     String op = ctx.operatorAddSub().getText();
    //     if (op.equals("+")) {
    //         kind = PLUS_NODE;
    //     } else {
    //         kind = MINUS_NODE;
    //     }
	// 	AST node = AST.newSubtree(kind);
	// 	ctx.exp().forEach((child) -> {
	// 		node.addChild(visit(child));
	// 	});
    //     return node;
    // }

    // @Override
    // public AST visitMultDivMod(MultDivModContext ctx) {
    //     NodeKind kind;
    //     String op = ctx.operatorMulDivMod().getText();
    //     if (op.equals("*")) {
    //         kind = TIMES_NODE;
    //     } else  if (op.equals("/")) {
    //         kind = OVER_NODE;
    //     } else {
    //         kind = MOD_NODE;
    //     }
    //     AST node = AST.newSubtree(kind);
	// 	ctx.exp().forEach((child) -> {
	// 		node.addChild(visit(child));
	// 	});
    //     return node;
    // }

    @Override
    public AST visitAddSub(AddSubContext ctx) {
        double kind;
        String op = ctx.operatorAddSub().getText();
        if (op.equals("+")) {
            kind = 1;
        } else {
            kind = 2;
        }
		AST node = new AST(ARIT_OP_NODE, op, kind);
		ctx.exp().forEach((child) -> {
			node.addChild(visit(child));
		});
        return node;
    }

    @Override
    public AST visitMultDivMod(MultDivModContext ctx) {
        double kind;
        String op = ctx.operatorMulDivMod().getText();
        if (op.equals("*")) {
            kind = 3;
        } else  if (op.equals("/")) {
            kind = 4;
        } else {
            kind = 5;
        }
		AST node = new AST(ARIT_OP_NODE, op, kind);
		ctx.exp().forEach((child) -> {
			node.addChild(visit(child));
		});
        return node;
    }

    @Override
    public AST visitFunctioncall(FunctioncallContext ctx) {
        AST name = visit(ctx.varOrExp());
        if (!idt.lookup(name.data)) {
        	System.out.printf("SEMANTIC ERROR: attempt to call a nil value at line %.0f.\n", name.numData);
            System.exit(1);
        }
        if (ctx.nameAndArgs().size() == 0) {
            return name;
        }
        AST args = new AST(ARGS_NODE, "");
		ctx.nameAndArgs().forEach((child) -> {
            AST temp = visit(child);
			if (temp != null) args.addChild(temp);
		});
        name.addChild(args);
        return name;
    }

    @Override
    public AST visitArgList(ArgListContext ctx) {
        if (ctx.explist() == null) return null;
        return visit(ctx.explist());
    }

    @Override
    public AST visitComparison(ComparisonContext ctx) {
        double kind;
        String op = ctx.operatorComparison().getText();
        if (op.equals("==")) {
            kind = 1;
        } else  if (op.equals("~=")) {
            kind = 2;
        } else  if (op.equals(">")) {
            kind = 3;
        } else  if (op.equals("<")) {
            kind = 4;
        } else  if (op.equals(">=")) {
            kind = 5;
        } else {
            kind = 6;
        }
		AST node = new AST(RELAT_OP_NODE, op, kind);
		ctx.exp().forEach((child) -> {
			node.addChild(visit(child));
		});
        return node;
    }

    @Override
    public AST visitWhile(WhileContext ctx) {
        AST node = AST.newSubtree(WHILE_NODE);
        node.addChild(visit(ctx.exp()));
        node.addChild(visit(ctx.block()));
        return node;
    }

    // ----------------------------------------------------------------------------


}