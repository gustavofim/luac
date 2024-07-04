.class public Func1
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
	
	invokestatic lua/Runtime/read()Llua/LuaObj;
	
	invokestatic lua/Runtime/endScope()V
	
	areturn
.end method

