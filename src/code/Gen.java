package code;

import static ast.NodeKind.ARGS_NODE;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import ast.AST;
import ast.ASTBaseVisitor;

public class Gen extends ASTBaseVisitor<Void> {
    private int ident = 0;
    private boolean isMain = true;
    private boolean isAssign = true;
    private ArrayList<String> mainCode = new ArrayList<String>();
    private HashMap<String, ArrayList<String>> funcCode = new HashMap<String, ArrayList<String>>();

    @Override
    public void execute(AST root) {
        emit(".class public Program");
        emit(".super java/lang/Object", true);
        emit(".method public static main([Ljava/lang/String;)V");
        ident++;
        emit(".limit locals 10");
        emit(".limit stack 10", true);

        emit("invokestatic luaruntime/Runtime/startScope()V", true);

        // print and read functions where defined in lualib (Func0 and Func1)
        emit("ldc \"print\"");
        emit("ldc 0");
        emit("new Func0");
        emit("dup");
        emit("invokespecial Func0/<init>()V");
        emit("ldc 1");
        emit("invokestatic luaruntime/Runtime/wrapConst(ILluaruntime/LuaFunctionLiteral;I)Lluaruntime/LuaType;", true);
        emit("ldc \"x\"");
        emit("invokestatic luaruntime/Runtime/setParam(Lluaruntime/LuaType;Ljava/lang/String;)Lluaruntime/LuaType;", true);
        emit("invokestatic luaruntime/Runtime/setGlobalVar(Ljava/lang/String;Lluaruntime/LuaType;)V", true);

        emit("ldc \"read\"");
        emit("ldc 1");
        emit("new Func1");
        emit("dup");
        emit("invokespecial Func1/<init>()V");
        emit("ldc 0");
        emit("invokestatic luaruntime/Runtime/wrapConst(ILluaruntime/LuaFunctionLiteral;I)Lluaruntime/LuaType;", true);
        emit("invokestatic luaruntime/Runtime/setGlobalVar(Ljava/lang/String;Lluaruntime/LuaType;)V", true);

        visit(root);
        emit("invokestatic luaruntime/Runtime/endScope()V", true);
        emit("return");
        ident--;
        emit(".end method");
        dump();
    }

    private void dump() {
        mainCode.forEach(line -> System.out.printf(line));
        // funcCode.forEach(line -> System.out.printf(line));
        funcCode.forEach((key, value) -> {
            try (FileWriter fileWriter = new FileWriter(String.format("./out/%s.j", key))) {
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.println(String.format(".class public %s", key));
                printWriter.println(".super java/lang/Object");
                printWriter.println(".implements luaruntime/LuaFunctionLiteral\n");
                printWriter.println(".method public <init>()V");
                printWriter.println("\taload_0\n");
                printWriter.println("\tinvokenonvirtual java/lang/Object/<init>()V");
                printWriter.println("\treturn");
                printWriter.println(".end method\n");
                printWriter.println(".method public call()Lluaruntime/LuaType;");
                printWriter.println("\t.limit locals 10");
                printWriter.println("\t.limit stack 10");
                value.forEach(line -> printWriter.printf(line));
                printWriter.println(".end method\n");
                printWriter.close();
            } catch (IOException e) {
                // Auto-generated catch block
                e.printStackTrace();
            }

            // System.out.println(key);
            // value.forEach(line -> System.out.printf(line));
        });
    }

    private void emit(String line) {
        if (isMain) {
            mainCode.add(String.format("%s%s\n", "\t".repeat(ident), line));
            return;
        }
        String func = String.format("Func%d", funcCount);
        if (!funcCode.containsKey(func)) {
            funcCode.put(func, new ArrayList<String>());
        }
        funcCode.get(func).add(String.format("%s%s\n", "\t".repeat(ident), line));
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
        isAssign = true;
        visit(node.getChild(0).getChild(0));
        visit(node.getChild(1).getChild(0));
        if (node.getChild(0).getChild(0).getChildCount() == 0) {
            if (isLocal) {
                emit("invokestatic luaruntime/Runtime/setLocalVar(Ljava/lang/String;Lluaruntime/LuaType;)V", true);
            } else {
                emit("invokestatic luaruntime/Runtime/setGlobalVar(Ljava/lang/String;Lluaruntime/LuaType;)V", true);
            }
        }
        isAssign = false;
        return null;
    }

    @Override
    protected Void visitVarUse(AST node) {
        int count = node.getChildCount();
        if (count == 0) {
            // No args -> evaluate var
            emit(String.format("ldc \"%s\"", node.data));
            emit("invokestatic luaruntime/Runtime/getVar(Ljava/lang/String;)Lluaruntime/LuaType;", true);
        } else {
            if (node.getChild(count - 1).kind == ARGS_NODE) {
                emit("invokestatic luaruntime/Runtime/startScope()V", true);
                emit(String.format("ldc \"%s\"", node.data));
                emit("invokestatic luaruntime/Runtime/getVar(Ljava/lang/String;)Lluaruntime/LuaType;", true);
                visit(node.getChild(0));
                emit("invokestatic luaruntime/Runtime/initArgs(Lluaruntime/LuaType;)Lluaruntime/LuaType;");
                emit("invokestatic luaruntime/Runtime/call(Lluaruntime/LuaType;)Lluaruntime/LuaType;", true);
                emit("invokestatic luaruntime/Runtime/endScope()V", true);
                if (!isAssign) emit("pop", true);
            } else {
                emit(String.format("ldc \"%s\"", node.data));
                emit("invokestatic luaruntime/Runtime/getVar(Ljava/lang/String;)Lluaruntime/LuaType;", true);
                for (int i = 0; i < node.getChildCount(); i++) {
                    visit(node.getChild(i));
                }
            }
        }
        return null;
    }

    @Override
    protected Void visitIndex(AST node) {
        visit(node.getChild(0));
        emit("invokestatic luaruntime/Runtime/getFromTable(Lluaruntime/LuaType;Lluaruntime/LuaType;)Lluaruntime/LuaType;", true);
        return null;
    }

    @Override
    protected Void visitVarDecl(AST node) {
        // if (node.getChildCount() == 0) {
            emit(String.format("ldc \"%s\"", node.data), true);
        // } else {
        //     emit(String.format("ldc \"%s\"", node.data), true);
        //     emit(String.format("ldc \"%s\"", node.data), true);
        // }
        return null;
    }

    @Override
    protected Void visitNum(AST node) {
        emit(String.format("ldc2_w %f", node.numData));
        emit("invokestatic luaruntime/Runtime/wrapConst(D)Lluaruntime/LuaType;", true);
        return null;
    }

    @Override
    protected Void visitNil(AST node) {
        emit("invokestatic luaruntime/Runtime/nilConst()Lluaruntime/LuaType;", true);
        return null;
    }

    @Override
    protected Void visitTrue(AST node) {
        emit("invokestatic luaruntime/Runtime/trueConst()Lluaruntime/LuaType;", true);
        return null;
    }

    @Override
    protected Void visitFalse(AST node) {
        emit("invokestatic luaruntime/Runtime/falseConst()Lluaruntime/LuaType;", true);
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

    @Override
    protected Void visitBoolOp(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        emit(String.format("ldc %.0f", node.numData));
        emit("invokestatic luaruntime/Runtime/boolOp(Lluaruntime/LuaType;Lluaruntime/LuaType;I)Lluaruntime/LuaType;", true);
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

    private boolean inTable = false;
    @Override
    protected Void visitTable(AST node) {
        inTable = true;
        emit("invokestatic luaruntime/Runtime/tableConst()Lluaruntime/LuaType;", true);
        for (int i = 0; i < node.getChildCount(); i++) {
            visit(node.getChild(i));
        }
        inTable = false;
        return null;
    }
    
    @Override
    protected Void visitTableField(AST node) {
        if (node.getChildCount() == 0) {
            return null;
        }
        visit(node.getChild(0));
        if (node.getChildCount() == 2) {
            visit(node.getChild(1));
            emit("invokestatic luaruntime/Runtime/constructTable(Lluaruntime/LuaType;Lluaruntime/LuaType;Lluaruntime/LuaType;)Lluaruntime/LuaType;", true);
            if (!inTable) {
                emit("pop");
            }
        } else {
            emit("invokestatic luaruntime/Runtime/constructTable(Lluaruntime/LuaType;Lluaruntime/LuaType;)Lluaruntime/LuaType;", true);
        }
        return null;
    }

    @Override
    protected Void visitArgs(AST node) {
        if (node.getChildCount() == 0) {
            return null;
        }
        AST child = node.getChild(0);
        for (int i = 0; i < child.getChildCount(); i++) {
            visit(child.getChild(i));
            emit(String.format("ldc %d", i));
            emit("invokestatic luaruntime/Runtime/setArg(Lluaruntime/LuaType;Lluaruntime/LuaType;I)Lluaruntime/LuaType;", true);
        }
        return null;
    }

    @Override
    protected Void visitParams(AST node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            visit(node.getChild(i));
            emit("invokestatic luaruntime/Runtime/setParam(Lluaruntime/LuaType;Ljava/lang/String;)Lluaruntime/LuaType;", true);
        }
        return null;
    }

    @Override
    protected Void visitParam(AST node) {
        emit(String.format("ldc \"%s\"", node.data));
        return null;
    }
    
    // The 10 first functions where reserved to lualib
    private int funcCount = 10;

    @Override
    protected Void visitFuncDef(AST node) {
        int nPar = 0;
        int blockIdx = 0;
        if (node.getChildCount() > 1) {
            nPar = node.getChild(0).getChildCount();
            blockIdx = 1;
        }

        // Dump the literal here....
        isMain = false;
        visit(node.getChild(blockIdx));
        if (!returned) {
            emit("invokestatic luaruntime/Runtime/nilConst()Lluaruntime/LuaType;");
            emit("areturn");
        }
        returned = false;
        isMain = true;

        emit(String.format("ldc %d", funcCount));
        // emit("new Func0");
        emit(String.format("new Func%d", funcCount));
        emit("dup");
        // emit("invokespecial Func0/<init>()V");
        emit(String.format("invokespecial Func%d/<init>()V", funcCount));
        emit(String.format("ldc %d", nPar));
        emit("invokestatic luaruntime/Runtime/wrapConst(ILluaruntime/LuaFunctionLiteral;I)Lluaruntime/LuaType;", true);

        if (node.getChildCount() > 1) {
            visit(node.getChild(0));
        }

        funcCount += 1;
        return null;
    }

    private boolean returned = false;

    @Override
    protected Void visitReturn(AST node) {
        returned = true;
        if (node.getChildCount() > 0) {
            visit(node.getChild(0));
        } else {
            emit("invokestatic luaruntime/Runtime/nilConst()Lluaruntime/LuaType;", true);
        }
        emit("areturn");
        return null;
    }

}