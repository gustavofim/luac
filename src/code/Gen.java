package code;

import java.util.ArrayList;

import ast.AST;
import ast.ASTBaseVisitor;

public class Gen extends ASTBaseVisitor<Void> {
    private int ident = 0;
    private boolean isMain = true;
    private ArrayList<String> mainCode = new ArrayList<String>();
    private ArrayList<String> funcCode = new ArrayList<String>();

    @Override
    public void execute(AST root) {
        emit(".class public Program");
        emit(".super java/lang/Object", true);
        emit(".method public static main([Ljava/lang/String;)V");
        ident++;
        emit(".limit locals 10");
        emit(".limit stack 10", true);
        visit(root);
        emit("return");
        ident--;
        emit(".end method");
        dump();
    }

    private void dump() {
        mainCode.forEach(line -> System.out.printf(line));
        funcCode.forEach(line -> System.out.printf(line));
    }

    private void emit(String line) {
        if (isMain) {
            mainCode.add(String.format("%s%s\n", "\t".repeat(ident), line));
            return;
        }
        funcCode.add(String.format("%s%s\n", "\t".repeat(ident), line));
    }

    private void emit(String line, boolean newLine) {
        emit(line);
        if (newLine) {
            emit("");
        }
    }

    @Override
    protected Void visitBlock(AST node) {
        emit("invokestatic luaruntime/Runtime/startScope()V", true);
        for (int i = 0; i < node.getChildCount(); i++) {
            visit(node.getChild(i));
        }
        emit("invokestatic luaruntime/Runtime/endScope()V", true);
        return null;
    }

    @Override
    protected Void visitArgs(AST node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            visit(node.getChild(i));
        }
        return null;
    }

    @Override
    protected Void visitExpList(AST node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            visit(node.getChild(i));
        }
        return null;
    }

    private boolean isLocal = false;

    @Override
    protected Void visitLocal(AST node) {
        isLocal = true;
        visit(node.getChild(0));
        isLocal = false;
        return null;
    }

    @Override
    protected Void visitAssign(AST node) {
        visit(node.getChild(0).getChild(0));
        visit(node.getChild(1).getChild(0));
        if (isLocal) {
            emit("invokestatic luaruntime/Runtime/setLocalVar(Ljava/lang/String;Lluaruntime/LuaType;)V", true);
        } else {
            emit("invokestatic luaruntime/Runtime/setGlobalVar(Ljava/lang/String;Lluaruntime/LuaType;)V", true);
        }
        return null;
    }

    @Override
    protected Void visitVarUse(AST node) {
        if (node.getChildCount() == 0) {
            emit(String.format("ldc \"%s\"", node.data));
            emit("invokestatic luaruntime/Runtime/getVar(Ljava/lang/String;)Lluaruntime/LuaType;", true);
        } else {
            // Args
            visit(node.getChild(0));
            if (node.data.equals("print")) {
                emit("invokestatic luaruntime/Runtime/print(Lluaruntime/LuaType;)V", true);
            } else {
                emit("pops");
            }
        }
        return null;
    }

    @Override
    protected Void visitVarDecl(AST node) {
        if (node.getChildCount() == 0) {
            emit(String.format("ldc \"%s\"", node.data), true);
        }
        return null;
    }

    @Override
    protected Void visitNum(AST node) {
        emit(String.format("ldc2_w %f", node.numData));
        emit("invokestatic luaruntime/Runtime/wrapConst(D)Lluaruntime/LuaType;", true);
        return null;
    }

    @Override
    protected Void visitVal(AST node) {
        emit(String.format("ldc \"%s\"", node.data));
        emit("invokestatic luaruntime/Runtime/wrapConst(Ljava/lang/String;)Lluaruntime/LuaType;", true);
        return null;
    }

    @Override
    protected Void visitAritOp(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        emit(String.format("ldc %.0f", node.numData));
        emit("invokestatic luaruntime/Runtime/aritOp(Lluaruntime/LuaType;Lluaruntime/LuaType;I)Lluaruntime/LuaType;", true);
        return null;
    }

    @Override
    protected Void visitRelatOp(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        emit(String.format("ldc %.0f", node.numData));
        emit("invokestatic luaruntime/Runtime/relatOp(Lluaruntime/LuaType;Lluaruntime/LuaType;I)Lluaruntime/LuaType;", true);
        return null;
    }

    private int whileCount = 0;

    @Override
    protected Void visitWhile(AST node) {
        emit(String.format("while%d:", whileCount));
        ident++;
        visit(node.getChild(0));
        emit("invokeinterface luaruntime/LuaType/toBoolean()Z 1\n");
        ident--;
        emit(String.format("ifeq whileEnd%d", whileCount));
        ident++;
        visit(node.getChild(1));
        ident--;
        emit(String.format("goto while%d", whileCount));
        emit(String.format("whileEnd%d:", whileCount), true);
        whileCount++;
        return null;
    }

    private int repeatCount = 0;

    @Override
    protected Void visitRepeat(AST node) {
        emit(String.format("repeat%d:", repeatCount));
        ident++;
        visit(node.getChild(0));
        visit(node.getChild(1));
        emit("invokeinterface luaruntime/LuaType/toBoolean()Z 1");
        emit(String.format("ifeq repeat%d", repeatCount), true);
        ident--;

        repeatCount++;
        return null;
    }

    @Override
    protected Void visitFuncDef(AST node) {
        int nArgs = node.getChild(0).getChildCount();
        emit(String.format("ldc %d", nArgs));
        emit("invokestatic luaruntime/Runtime/wrapConst(I)Lluaruntime/LuaType;", true);
        ident--;
        isMain = false;
        emit(String.format(".method public static func([Ljava/lang/String;)V"));
        ident++;
        emit(".limit locals 10");
        emit(".limit stack 10", true);
        visit(node.getChild(0));
        emit("return");
        ident--;
        emit(".end method", true);
        isMain = true;
        ident++;
        return null;
    }

    private int ifCount = 0;

    @Override
    protected Void visitIf(AST node) {
        int count = ifCount;
        ifCount++;
        visit(node.getChild(0));
        emit("invokeinterface luaruntime/LuaType/toBoolean()Z 1");
        emit(String.format("ifeq if%d", count));
        ident++;
        visit(node.getChild(1));
        emit(String.format("goto ifEnd%d", count));
        ident--;
        emit(String.format("if%d:", count));
        if (node.getChildCount() > 2) {
            ident++;
            visit(node.getChild(2));
            ident--;
        }
        emit(String.format("ifEnd%d:", count), true);
        return null;
    }

    @Override
    protected Void visitUnaryOp(AST node) {
        visit(node.getChild(0));
        emit(String.format("ldc %.0f", node.numData));
        emit("invokestatic luaruntime/Runtime/unaryOp(Lluaruntime/LuaType;I)Lluaruntime/LuaType;", true);
        return null;
    }

    // private int forCount = 0;

    @Override
    protected Void visitFor(AST node) {
        // int count = forCount;
        // forCount++;
        // AST ctrlVar = new AST(
        //     VAR_USE_NODE,
        //     node.getChild(0) // assign
        //                         .getChild(0) // var_list
        //                         .getChild(0) // var_decl
        //                         .data
        // );
        // visit(node.getChild(0));
        // emit(String.format("for%d:", count));
        
        return null;
    }

}
