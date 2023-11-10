package code;

import ast.AST;
import ast.ASTBaseVisitor;

public class Gen extends ASTBaseVisitor<Integer> {
    private int ident = 1;
    @Override
    public void execute(AST root) {
        System.out.println(".class public Program");
        System.out.println(".super java/lang/Object\n");
        System.out.println(".method public static main([Ljava/lang/String;)V");
        System.out.println("\t.limit locals 100");
        System.out.println("\t.limit stack 100\n");
        visit(root);
        System.out.println("\treturn");
        System.out.println(".end method");
    }

    @Override
    protected Integer visitBlock(AST node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            visit(node.getChild(i));
        }
        return 0;
    }

    @Override
    protected Integer visitArgs(AST node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            visit(node.getChild(i));
        }
        return 0;
    }

    @Override
    protected Integer visitExpList(AST node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            visit(node.getChild(i));
        }
        return 0;
    }

    @Override
    protected Integer visitAssign(AST node) {
        visit(node.getChild(0).getChild(0));
        visit(node.getChild(1).getChild(0));
        System.out.printf("\t".repeat(ident));
        System.out.println("invokestatic luaruntime/Runtime/setVar(Ljava/lang/String;Lluaruntime/LuaType;)V");
        System.out.println();
        return 0;
    }

    @Override
    protected Integer visitVarUse(AST node) {
        if (node.getChildCount() == 0) {
            System.out.printf("\t".repeat(ident));
            System.out.printf("ldc \"%s\"\n", node.data);
            System.out.printf("\t".repeat(ident));
            System.out.println("invokestatic luaruntime/Runtime/getVar(Ljava/lang/String;)Lluaruntime/LuaType;");
        } else {
            // Args
            visit(node.getChild(0));
            // System.out.printf("\n%s\n", node.data);
            if (node.data.equals("print")) {
                // System.out.println("\tgetstatic java/lang/System/out Ljava/io/PrintStream;");
                // System.out.println("\tswap									; Pass obj toString");
                // System.out.println("\tinvokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V");
                System.out.printf("\t".repeat(ident));
                System.out.println("invokestatic luaruntime/Runtime/print(Lluaruntime/LuaType;)V");
            }
        }
        System.out.println();
        return 0;
    }

    @Override
    protected Integer visitVarDecl(AST node) {
        System.out.printf("\t".repeat(ident));
        System.out.printf("ldc \"%s\"\n", node.data);
        System.out.println();
        return 0;
    }

    @Override
    protected Integer visitNum(AST node) {
        System.out.printf("\t".repeat(ident));
        System.out.printf("ldc2_w %f\n", node.numData);
        System.out.printf("\t".repeat(ident));
        System.out.println("invokestatic luaruntime/Runtime/wrapConst(D)Lluaruntime/LuaType;");
        System.out.println();
        return 0;
    }

    @Override
    protected Integer visitVal(AST node) {
        System.out.printf("\t".repeat(ident));
        System.out.printf("ldc \"%s\"\n", node.data);
        System.out.printf("\t".repeat(ident));
        System.out.println("invokestatic luaruntime/Runtime/wrapConst(Ljava/lang/String;)Lluaruntime/LuaType;");
        System.out.println();
        return 0;
    }

    @Override
    protected Integer visitAritOp(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        System.out.printf("\t".repeat(ident));
        System.out.printf("ldc %.0f\n", node.numData);
        System.out.printf("\t".repeat(ident));
        System.out.println("invokestatic luaruntime/Runtime/aritOp(Lluaruntime/LuaType;Lluaruntime/LuaType;I)Lluaruntime/LuaType;");
        System.out.println();
        return 0;
    }

    @Override
    protected Integer visitRelatOp(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        System.out.printf("\t".repeat(ident));
        System.out.printf("ldc %.0f\n", node.numData);
        System.out.printf("\t".repeat(ident));
        System.out.println("invokestatic luaruntime/Runtime/relatOp(Lluaruntime/LuaType;Lluaruntime/LuaType;I)Lluaruntime/LuaType;");
        System.out.println();
        return 0;
    }

    private int whileCount = 0;

    @Override
    protected Integer visitWhile(AST node) {
        System.out.printf("\t".repeat(ident));
        System.out.printf("while%d:\n", whileCount);
        ident++;
        visit(node.getChild(0));
        System.out.printf("\t".repeat(ident));
        System.out.printf("invokeinterface luaruntime/LuaType/toBoolean()Z 1\n");
        ident--;
        System.out.printf("\t".repeat(ident));
        System.out.printf("ifeq whileEnd%d\n", whileCount);
        ident++;
        visit(node.getChild(1));
        ident--;
        System.out.printf("\t".repeat(ident));
        System.out.printf("goto while%d\n", whileCount);
        System.out.printf("\t".repeat(ident));
        System.out.printf("whileEnd%d:\n", whileCount);
        System.out.println();
        whileCount++;
        return 0;
    }

    private int repeatCount = 0;

    @Override
    protected Integer visitRepeat(AST node) {
        System.out.printf("\t".repeat(ident));
        System.out.printf("repeat%d:\n", repeatCount);
        ident++;
        visit(node.getChild(0));
        visit(node.getChild(1));
        System.out.printf("\t".repeat(ident));
        System.out.printf("invokeinterface luaruntime/LuaType/toBoolean()Z 1\n");
        System.out.printf("\t".repeat(ident));
        System.out.printf("ifeq repeat%d\n", repeatCount);
        ident--;
        System.out.println();

        repeatCount++;
        return 0;
    }

}
