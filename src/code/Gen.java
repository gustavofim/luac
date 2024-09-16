package code;

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

        emit("invokestatic lua/Runtime/startScope()V", true);

        // print and read functions where defined in lualib (Func0 and Func1)
        emit("; Hardcoded lualib functions ====================================================", true);
        emit("ldc \"print\"");
        emit("ldc 0");
        emit("new Func0");
        emit("dup");
        emit("invokespecial Func0/<init>()V");
        emit("invokestatic lua/Runtime/wrapConst(ILlua/LuaFunctionLiteral;)Llua/LuaObj;", true);
        emit("ldc \"x\"");
        emit("invokestatic lua/Runtime/setParam(Llua/LuaObj;Ljava/lang/String;)Llua/LuaObj;", true);
        emit("invokestatic lua/Runtime/setGlobalVar(Ljava/lang/String;Llua/LuaObj;)V", true);

        emit("ldc \"read\"");
        emit("ldc 1");
        emit("new Func1");
        emit("dup");
        emit("invokespecial Func1/<init>()V");
        emit("invokestatic lua/Runtime/wrapConst(ILlua/LuaFunctionLiteral;)Llua/LuaObj;", true);
        emit("invokestatic lua/Runtime/setGlobalVar(Ljava/lang/String;Llua/LuaObj;)V", true);

        emit("; ===============================================================================", true);

        visit(root);
        emit("invokestatic lua/Runtime/endScope()V", true);
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
                printWriter.println(".implements lua/LuaFunctionLiteral\n");
                printWriter.println(".method public <init>()V");
                printWriter.println("\taload_0\n");
                printWriter.println("\tinvokenonvirtual java/lang/Object/<init>()V");
                printWriter.println("\treturn");
                printWriter.println(".end method\n");
                printWriter.println(".method public call()Llua/LuaObj;");
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
        emit("invokestatic lua/Runtime/startScope()V", true);
        for (int i = 0; i < node.getChildCount(); i++) {
            visit(node.getChild(i));
        }
        emit("invokestatic lua/Runtime/endScope()V", true);
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
                emit("invokestatic lua/Runtime/setLocalVar(Ljava/lang/String;Llua/LuaObj;)V", true);
            } else {
                emit("invokestatic lua/Runtime/setGlobalVar(Ljava/lang/String;Llua/LuaObj;)V", true);
            }
        }
        isAssign = false;
        return null;
    }

    @Override
    protected Void visitVarUse(AST node) {
        emit(String.format("ldc \"%s\"", node.data));
        emit("invokestatic lua/Runtime/getVar(Ljava/lang/String;)Llua/LuaObj;", true);
        for (int i = 0; i < node.getChildCount(); ++i) visit(node.getChild(i));
        return null;
    }

    @Override
    protected Void visitIndex(AST node) {
        visit(node.getChild(0));
        emit("invokestatic lua/Runtime/getFromTable(Llua/LuaObj;Llua/LuaObj;)Llua/LuaObj;", true);
        return null;
    }

    @Override
    protected Void visitVarDecl(AST node) {
        emit(String.format("ldc \"%s\"", node.data), true);
        return null;
    }

    @Override
    protected Void visitDouble(AST node) {
        emit(String.format("ldc2_w %f", node.numData));
        emit("invokestatic lua/Runtime/wrapConst(D)Llua/LuaObj;", true);
        return null;
    }

    @Override
    protected Void visitInt(AST node) {
        emit(String.format("ldc %d", node.numData.intValue()));
        emit("invokestatic lua/Runtime/wrapConst(I)Llua/LuaObj;", true);
        return null;
    }

    @Override
    protected Void visitNil(AST node) {
        emit("invokestatic lua/Runtime/nilConst()Llua/LuaObj;", true);
        return null;
    }

    @Override
    protected Void visitTrue(AST node) {
        emit("invokestatic lua/Runtime/trueConst()Llua/LuaObj;", true);
        return null;
    }

    @Override
    protected Void visitFalse(AST node) {
        emit("invokestatic lua/Runtime/falseConst()Llua/LuaObj;", true);
        return null;
    }

    @Override
    protected Void visitVal(AST node) {
        emit(String.format("ldc \"%s\"", node.data));
        emit("invokestatic lua/Runtime/wrapConst(Ljava/lang/String;)Llua/LuaObj;", true);
        return null;
    }

    @Override
    protected Void visitAritOp(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        emit(String.format("ldc %.0f", node.numData));
        emit("invokestatic lua/Runtime/aritOp(Llua/LuaObj;Llua/LuaObj;I)Llua/LuaObj;", true);
        return null;
    }

    @Override
    protected Void visitRelatOp(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        emit(String.format("ldc %.0f", node.numData));
        emit("invokestatic lua/Runtime/relatOp(Llua/LuaObj;Llua/LuaObj;I)Llua/LuaObj;", true);
        return null;
    }

    @Override
    protected Void visitBoolOp(AST node) {
        visit(node.getChild(0));
        visit(node.getChild(1));
        emit(String.format("ldc %.0f", node.numData));
        emit("invokestatic lua/Runtime/boolOp(Llua/LuaObj;Llua/LuaObj;I)Llua/LuaObj;", true);
        return null;
    }

    private int whileCount = 0;

    @Override
    protected Void visitWhile(AST node) {
        emit(String.format("while%d:", whileCount));
        ident++;
        visit(node.getChild(0));
        emit("invokeinterface lua/LuaObj/toBoolean()Z 1\n");
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
        emit("invokeinterface lua/LuaObj/toBoolean()Z 1");
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
        emit("invokeinterface lua/LuaObj/toBoolean()Z 1");
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
        emit("invokestatic lua/Runtime/unaryOp(Llua/LuaObj;I)Llua/LuaObj;", true);
        return null;
    }

    private boolean inTable = false;
    @Override
    protected Void visitTable(AST node) {
        inTable = true;
        emit("invokestatic lua/Runtime/tableConst()Llua/LuaObj;", true);
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
            emit("invokestatic lua/Runtime/constructTable(Llua/LuaObj;Llua/LuaObj;Llua/LuaObj;)Llua/LuaObj;", true);
            if (!inTable) {
                emit("pop");
            }
        } else {
            emit("invokestatic lua/Runtime/constructTable(Llua/LuaObj;Llua/LuaObj;)Llua/LuaObj;", true);
        }
        return null;
    }

    @Override
    protected Void visitArgs(AST node) {
        emit("invokestatic lua/Runtime/startScope()V", true);
        if (node.getChildCount() > 0) {
            AST explist = node.getChild(0);
            for (int i = 0; i < explist.getChildCount(); i++) {
                visit(explist.getChild(i));
                // emit(String.format("ldc %d", i));
                emit("invokestatic lua/Runtime/setArg(Llua/LuaObj;Llua/LuaObj;)Llua/LuaObj;", true);
            }
        }
        emit("invokestatic lua/Runtime/call(Llua/LuaObj;)Llua/LuaObj;", true);
        emit("invokestatic lua/Runtime/endScope()V", true);
        return null;
    }

    @Override
    protected Void visitParamList(AST node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            visit(node.getChild(i));
            emit("invokestatic lua/Runtime/setParam(Llua/LuaObj;Ljava/lang/String;)Llua/LuaObj;", true);
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
        int blockIdx = 0;
        if (node.getChildCount() > 1) {
            // nPar = node.getChild(0).getChildCount();
            blockIdx = 1;
        }

        // Dump the literal here....
        isMain = false;
        visit(node.getChild(blockIdx));
        // if (!returned) {
            emit("invokestatic lua/Runtime/nilConst()Llua/LuaObj;");
            emit("areturn");
        // }
        returned = false;
        isMain = true;

        emit(String.format("ldc %d", funcCount));
        emit(String.format("new Func%d", funcCount));
        emit("dup");
        emit(String.format("invokespecial Func%d/<init>()V", funcCount));
        emit("invokestatic lua/Runtime/wrapConst(ILlua/LuaFunctionLiteral;)Llua/LuaObj;", true);

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
            emit("invokestatic lua/Runtime/nilConst()Llua/LuaObj;", true);
        }
        emit("areturn");
        return null;
    }

    @Override
    protected Void visitFuncStat(AST node) {
        // If the whole statement is just a function call,
        // pop the returned value to avoid stack height inconsistences
        visit(node.getChild(0));
        emit("pop", true);
        return null;
    }

}