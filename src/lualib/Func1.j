.class public Func1
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
	
	invokestatic luaruntime/Runtime/read()Lluaruntime/LuaType;
	
	invokestatic luaruntime/Runtime/endScope()V
	
	areturn
.end method

