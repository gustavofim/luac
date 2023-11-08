package code;

import ast.AST;
import ast.ASTBaseVisitor;

public class Gen extends ASTBaseVisitor<Integer> {
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
        System.out.println("\tinvokestatic LuaRuntime/Runtime/setVar(Ljava/lang/String;LLuaRuntime/LuaType;)V");
        return 0;
    }

    @Override
    protected Integer visitVarUse(AST node) {
        if (node.getChildCount() == 0) {
            System.out.printf("\tldc \"%s\"\n", node.data);
            System.out.println("\tinvokestatic LuaRuntime/Runtime/getVar(Ljava/lang/String;)LLuaRuntime/LuaType;");
        } else {
            // Args
            visit(node.getChild(0));
            // System.out.printf("\n%s\n", node.data);
            if (node.data.equals("print")) {
                // System.out.println("\tgetstatic java/lang/System/out Ljava/io/PrintStream;");
                // System.out.println("\tswap									; Pass obj toString");
                // System.out.println("\tinvokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V");
                System.out.println("\tinvokestatic LuaRuntime/Runtime/print(LLuaRuntime/LuaType;)V");
            }
        }
        return 0;
    }

    @Override
    protected Integer visitVarDecl(AST node) {
        System.out.printf("\tldc \"%s\"\n", node.data);
        return 0;
    }

    @Override
    protected Integer visitNum(AST node) {
        System.out.printf("\tldc2_w %f\n", node.numData);
        System.out.println("\tinvokestatic LuaRuntime/Runtime/wrapConst(D)LLuaRuntime/LuaType;");
        return 0;
    }

    @Override
    protected Integer visitVal(AST node) {
        System.out.printf("\tldc \"%s\"\n", node.data);
        System.out.println("\tinvokestatic LuaRuntime/Runtime/wrapConst(Ljava/lang/String;)LLuaRuntime/LuaType;");
        return 0;
    }

    @Override
    protected Integer visitMinus(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        System.out.println("\tldc 2");
        System.out.println("\tinvokestatic LuaRuntime/Runtime/aritOp(LLuaRuntime/LuaType;LLuaRuntime/LuaType;I)LLuaRuntime/LuaType;");
        return 0;
    }

    @Override
    protected Integer visitPlus(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        System.out.println("\tldc 1");
        System.out.println("\tinvokestatic LuaRuntime/Runtime/aritOp(LLuaRuntime/LuaType;LLuaRuntime/LuaType;I)LLuaRuntime/LuaType;");
        return 0;
    }
}
