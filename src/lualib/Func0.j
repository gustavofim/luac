.class public Func0
.super java/lang/Object
.implements luaruntime/LuaFunctionLiteral

.method public <init>()V
	aload_0

	invokenonvirtual java/lang/Object/<init>()V
	return
.end method

.method public call()Lluaruntime/LuaType;
	.limit locals 10
	.limit stack 10
	invokestatic luaruntime/Runtime/startScope()V
	
	ldc "x"
	invokestatic luaruntime/Runtime/getVar(Ljava/lang/String;)Lluaruntime/LuaType;
	
	invokestatic luaruntime/Runtime/print(Lluaruntime/LuaType;)V
	
	invokestatic luaruntime/Runtime/endScope()V
	
	invokestatic luaruntime/Runtime/nilConst()Lluaruntime/LuaType;
	areturn
.end method

