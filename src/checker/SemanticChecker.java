package checker;

import static ast.NodeKind.ARGS_NODE;
import static ast.NodeKind.ARIT_OP_NODE;
import static ast.NodeKind.ASSIGN_NODE;
import static ast.NodeKind.BLOCK_NODE;
import static ast.NodeKind.BOOL_OP_NODE;
import static ast.NodeKind.DOUBLE_NODE;
import static ast.NodeKind.EXP_LIST_NODE;
import static ast.NodeKind.FALSE_NODE;
import static ast.NodeKind.FUNC_DEF_NODE;
import static ast.NodeKind.FUNC_STAT_NODE;
import static ast.NodeKind.IF_NODE;
import static ast.NodeKind.INDEX_NODE;
import static ast.NodeKind.INT_NODE;
import static ast.NodeKind.LAST_INDEX_NODE;
import static ast.NodeKind.LOCAL_NODE;
import static ast.NodeKind.NIL_NODE;
import static ast.NodeKind.PARAM_LIST_NODE;
import static ast.NodeKind.PARAM_NODE;
import static ast.NodeKind.RELAT_OP_NODE;
import static ast.NodeKind.REPEAT_NODE;
import static ast.NodeKind.RETURN_NODE;
import static ast.NodeKind.TABLE_FIELD_NODE;
import static ast.NodeKind.TABLE_NODE;
import static ast.NodeKind.TRUE_NODE;
import static ast.NodeKind.UNARY_OP_NODE;
import static ast.NodeKind.STR_NODE;
import static ast.NodeKind.VAR_DECL_NODE;
import static ast.NodeKind.VAR_LIST_NODE;
import static ast.NodeKind.VAR_USE_NODE;
import static ast.NodeKind.WHILE_NODE;

import ast.AST;
import ast.NodeKind;
import parser.LuaParserBaseVisitor;
import parser.LuaParser.AddSubContext;
import parser.LuaParser.AndContext;
import parser.LuaParser.ArgListContext;
import parser.LuaParser.AssignContext;
import parser.LuaParser.AttnamelistContext;
import parser.LuaParser.BlockContext;
import parser.LuaParser.ChunkContext;
import parser.LuaParser.ComparisonContext;
import parser.LuaParser.ExplistContext;
import parser.LuaParser.FalseContext;
import parser.LuaParser.FieldlistContext;
import parser.LuaParser.FuncbodyContext;
import parser.LuaParser.FunctioncallContext;
import parser.LuaParser.IfThenElseContext;
import parser.LuaParser.LaststatContext;
import parser.LuaParser.LocalContext;
import parser.LuaParser.MultDivModContext;
import parser.LuaParser.NameAndArgsContext;
import parser.LuaParser.NamelistContext;
import parser.LuaParser.NilContext;
import parser.LuaParser.NumberContext;
import parser.LuaParser.OrContext;
import parser.LuaParser.PrefixexpContext;
import parser.LuaParser.RepeatContext;
import parser.LuaParser.StringContext;
import parser.LuaParser.TableAssignContext;
import parser.LuaParser.TableBracketContext;
import parser.LuaParser.TableContext;
import parser.LuaParser.TableExpContext;
import parser.LuaParser.TableconstructorContext;
import parser.LuaParser.TrueContext;
import parser.LuaParser.UnaryContext;
import parser.LuaParser.VarContext;
import parser.LuaParser.VarOrExpContext;
import parser.LuaParser.VarSuffixContext;
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
        // idt.add("pront", 0);
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
		});
        if (ctx.laststat() != null) {
            node.addChild(visit(ctx.laststat()));
        }
		return node;
	}

	@Override
	public AST visitAssign(AssignContext ctx) {
		AST node = AST.newSubtree(ASSIGN_NODE);

        AST varlist = visit(ctx.varlist());
        AST explist = visit(ctx.explist()); 

        if (varlist.getChildCount() > 1 || explist.getChildCount() > 1) {
        	System.out.printf("NOT IMPLEMENTED ERROR: no multiple assignments");
            System.exit(1);
        }

        node.addChild(varlist);
        AST var = varlist.getChild(0);
        int count = var.getChildCount();


        if (count > 0) {
            NodeKind kind = var.getChild(0).kind;
            if (kind == INDEX_NODE || kind == LAST_INDEX_NODE) {
                AST child = var.getChild(count-1).getChild(0);
                node.addChild(AST.newSubtree(EXP_LIST_NODE, AST.newSubtree(TABLE_FIELD_NODE, child, explist.getChild(0))));
            }
        } else {
            node.addChild(explist);
        }
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
        try {
            return new AST(INT_NODE, (double)Integer.parseInt(ctx.getChild(0).toString()));
        } catch (NumberFormatException e) {
            return new AST(DOUBLE_NODE, Double.parseDouble(ctx.getChild(0).toString()));
        }
    }

    @Override
    public AST visitNil(NilContext ctx) {
        return new AST(NIL_NODE, "", 0.0);
    }

    @Override
    public AST visitTrue(TrueContext ctx) {
        return new AST(TRUE_NODE, "", 0.0);
    }

    @Override
    public AST visitFalse(FalseContext ctx) {
        return new AST(FALSE_NODE, "", 0.0);
    }

    @Override
    public AST visitString(StringContext ctx) {
        String str = ctx.getChild(0).toString();
        str = str.substring(1, str.length()-1);
        return new AST(STR_NODE, str);
    }

    @Override
    public AST visitVarlist(VarlistContext ctx) {
		AST node = AST.newSubtree(VAR_LIST_NODE);
		ctx.var().forEach((child) -> {
            AST childNode = visit(child);
            if (!idt.lookup(childNode.data)) {
                idt.add(childNode.data, (int)Math.round(childNode.numData));
            }
            int childCount = childNode.getChildCount();
            if (childCount > 0) {
                AST idx = new AST(VAR_USE_NODE, childNode.data, childNode.numData);
                for (int i = 0; i < childCount - 1; i++) {
                    idx.addChild(childNode.getChild(i));
                }
                AST lastIdx = childNode.getChild(childCount-1).getChild(0);
                idx.addChild(AST.newSubtree(LAST_INDEX_NODE, new AST(lastIdx.kind, lastIdx.data, lastIdx.numData)));
                node.addChild(idx);
            } else {
                node.addChild(new AST(VAR_DECL_NODE, childNode.data, childNode.numData));
            }
		});
		return node;
    }

    @Override
    public AST visitVar(VarContext ctx) {
        AST node = new AST(VAR_USE_NODE, ctx.NAME().getSymbol().getText(), (double)ctx.NAME().getSymbol().getLine());
        ctx.varSuffix().forEach((child) -> {
            if (!idt.lookup(ctx.NAME().getSymbol().getText())) {
                System.out.printf("SEMANTIC ERROR: attempt to index a nil value at line %d.\n", ctx.NAME().getSymbol().getLine());
                System.exit(1);
            }
            node.addChild(visit(child));
        });
        return node;
    }

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
    public AST visitAnd(AndContext ctx) {
		AST node = new AST(BOOL_OP_NODE, ctx.operatorAnd().getText(), (double)1);
		ctx.exp().forEach((child) -> {
			node.addChild(visit(child));
		});
        return node;
    }

    @Override
    public AST visitOr(OrContext ctx) {
		AST node = new AST(BOOL_OP_NODE, ctx.operatorOr().getText(), (double)2);
		ctx.exp().forEach((child) -> {
			node.addChild(visit(child));
		});
        return node;
    }

    @Override
    public AST visitFunctioncall(FunctioncallContext ctx) {
		AST func = new AST(FUNC_STAT_NODE, "");
        AST name = visit(ctx.varOrExp());
        if (!idt.lookup(name.data)) {
        	System.out.printf("SEMANTIC ERROR: attempt to call a nil value at line %.0f.\n", name.numData);
            System.exit(1);
        }
        name.addChild(visit(ctx.nameAndArgs().get(0)));
        func.addChild(name);
        return func;
    }

    @Override
    public AST visitPrefixexp(PrefixexpContext ctx) {
        AST name = visit(ctx.varOrExp());
        if (ctx.nameAndArgs().size() > 0) {
            if (!idt.lookup(name.data)) {
                System.out.printf("SEMANTIC ERROR: attempt to call a nil value at line %.0f.\n", name.numData);
                System.exit(1);
            }
            name.addChild(visit(ctx.nameAndArgs().get(0)));
        }
        return name;
    }

    @Override
    public AST visitNameAndArgs(NameAndArgsContext ctx) {
        AST args = new AST(ARGS_NODE, "");
		ctx.children.forEach((child) -> {
            AST temp = visit(child);
			if (temp != null) args.addChild(temp);
		});
        return args;
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

    @Override
    public AST visitRepeat(RepeatContext ctx) {
        AST node = AST.newSubtree(REPEAT_NODE);
        node.addChild(visit(ctx.block()));
        node.addChild(visit(ctx.exp()));
        return node;
    }

    // @Override
    // public AST visitFunctionDef(FunctionDefContext ctx) {
    //     AST node = AST.newSubtree(ASSIGN_NODE);
    //     node.addChild(visit(ctx.funcname()));
    //     node.addChild(AST.newSubtree(EXP_LIST_NODE, visit(ctx.funcbody())));
    //     return node;
    // }

    // @Override
    // public AST visitFuncname(FuncnameContext ctx) {
    //     AST node = AST.newSubtree(VAR_LIST_NODE);
    //     AST child = new AST(VAR_DECL_NODE, ctx.NAME().get(0).getSymbol().getText(), (double)ctx.NAME().get(0).getSymbol().getLine());
    //     if (!idt.lookup(child.data)) {
    //         idt.add(child.data, (int)Math.round(child.numData));
    //     }
    //     node.addChild(child);
    //     if (ctx.NAME().size() > 1) {
    //         AST use = new AST(VAR_USE_NODE, ctx.NAME().get(1).getSymbol().getText(), (double)ctx.NAME().get(1).getSymbol().getLine());
    //         AST idx = AST.newSubtree(LAST_INDEX_NODE, use);
    //         child.addChild(idx);
    //     }
    //     return node;
    // }

    @Override
    public AST visitFuncbody(FuncbodyContext ctx) {
        AST node = AST.newSubtree(FUNC_DEF_NODE);
        if (ctx.parlist() != null) {
            node.addChild(visit(ctx.parlist()));
        }
        node.addChild(visit(ctx.block()));
        node = AST.newSubtree(EXP_LIST_NODE, node);
        return node;
    }

    @Override
    public AST visitNamelist(NamelistContext ctx) {
        // Considering theres no FORs, implemented for parameters only
        AST node = AST.newSubtree(PARAM_LIST_NODE);
		ctx.NAME().forEach((child) -> {
			node.addChild(new AST(PARAM_NODE, child.getSymbol().getText(), (double)child.getSymbol().getLine()));
            if (!idt.lookup(child.getSymbol().getText())) {
                idt.add(child.getSymbol().getText(), (int)Math.round(child.getSymbol().getLine()));
            }
		});
        return node;
    }


    @Override
    public AST visitLaststat(LaststatContext ctx) {
        AST node = AST.newSubtree(RETURN_NODE);
        if (ctx.children.size() > 1) {
            node.addChild(visit(ctx.getChild(1)));
        }
        return node;
    }

    @Override
    public AST visitIfThenElse(IfThenElseContext ctx) {
        AST node = AST.newSubtree(IF_NODE, visit(ctx.exp(0)));
        AST tail = node;
        node.addChild(visit(ctx.block(0)));
        for (int i = 0; i < ctx.ELSEIF().size(); i++) {
            AST elif = AST.newSubtree(IF_NODE, visit(ctx.exp(i + 1)));
            elif.addChild(visit(ctx.block(i + 1)));
            tail.addChild(elif);
            tail = elif;
        }
        if (ctx.ELSE() != null) {
            tail.addChild(visit(ctx.block(ctx.block().size() - 1)));
        }

        return node;
    }

    @Override
    public AST visitUnary(UnaryContext ctx) {
        double kind;
        String op = ctx.operatorUnary().getText();
        if (op.equals("-")) {
            kind = 1;
        } else if (op.equals("not")) {
            kind = 2;
        } else {
            kind = 3;
            // System.exit(1);
        }
        AST node = new AST(UNARY_OP_NODE, op, kind);
        node.addChild(visit(ctx.exp()));
        return node;
    }

    @Override
    public AST visitLocal(LocalContext ctx) {
        AST node = AST.newSubtree(ASSIGN_NODE);
        node.addChild(visit(ctx.attnamelist()));
        node.addChild(visit(ctx.explist()));
    
        return AST.newSubtree(LOCAL_NODE, node);
    }

    @Override
    public AST visitAttnamelist(AttnamelistContext ctx) {
        String name = ctx.NAME().get(0).getSymbol().getText();
        int line = ctx.NAME().get(0).getSymbol().getLine();
        if (!idt.lookup(name)) {
            idt.add(name, line);
        }
        return AST.newSubtree(VAR_LIST_NODE, new AST(VAR_DECL_NODE, name, (double)line));
    }

    @Override
    public AST visitTable(TableContext ctx) {
        return visit(ctx.tableconstructor());
    }
    
    @Override
    public AST visitTableconstructor(TableconstructorContext ctx) {
        if (ctx.fieldlist() != null) {
            return visit(ctx.fieldlist());
        } else {
            return AST.newSubtree(TABLE_NODE);
        }
    }
    
    @Override
    public AST visitFieldlist(FieldlistContext ctx) {
        AST node = AST.newSubtree(TABLE_NODE);
        ctx.field().forEach((child) -> {
            node.addChild(visit(child));
        });
        return node;
    }

    @Override
    public AST visitTableExp(TableExpContext ctx) {
        return AST.newSubtree(TABLE_FIELD_NODE, visit(ctx.exp()));
    }

    @Override
    public AST visitTableBracket(TableBracketContext ctx) {
        AST node = AST.newSubtree(TABLE_FIELD_NODE);
        ctx.exp().forEach((child) -> {
            node.addChild(visit(child));
        });
        return node;
    }

    @Override
    public AST visitTableAssign(TableAssignContext ctx) {
        AST node = AST.newSubtree(TABLE_FIELD_NODE);
        node.addChild(new AST(STR_NODE, ctx.NAME().getSymbol().getText()));
        node.addChild(visit(ctx.exp()));
        return node;
    }

    @Override
    public AST visitVarSuffix(VarSuffixContext ctx) {
        AST node = AST.newSubtree(INDEX_NODE);
        if (ctx.LBRA() != null) {
            // Table indexed with brackets
            node.addChild(visit(ctx.exp()));
        } else {
            // Syntax sugar: table.key
            node.addChild(new AST(STR_NODE, ctx.NAME().getSymbol().getText()));
        }
        return node;
    }

    // @Override
    // public AST visitFunctionDefExp(FunctionDefExpContext ctx) {
    //     return visit(ctx.getChild(0));
    // }

    // ----------------------------------------------------------------------------


}