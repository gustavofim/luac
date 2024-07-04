.class public Func0
.super java/lang/Object
.implements lua/LuaFunctionLiteral

.method public <init>()V
	aload_0

	invokenonvirtual java/lang/Object/<init>()V
	return
.end method

.method public call()Llua/LuaObj;
	.limit locals 10
	.limit stack 10
	invokestatic lua/Runtime/startScope()V
	
	ldc "x"
	invokestatic lua/Runtime/getVar(Ljava/lang/String;)Llua/LuaObj;
	
	invokestatic lua/Runtime/print(Llua/LuaObj;)V
	
	invokestatic lua/Runtime/endScope()V
	
	invokestatic lua/Runtime/nilConst()Llua/LuaObj;
	areturn
.end method

