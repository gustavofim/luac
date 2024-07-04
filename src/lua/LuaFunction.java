package lua;

import java.util.ArrayList;

public class LuaFunction implements LuaObj {
    private int id;
    private LuaFunctionLiteral func;
    private int numPar = 0;
    private int numArgs = 0;
    private ArrayList<String> params = new ArrayList<>();

    public LuaFunction(int id, LuaFunctionLiteral func) {
        this.id = id;
        this.func = func;
    }

    public void setParam(String param) {
        ++numPar;
        params.add(param);
    }

    public void setArg(int n, LuaObj arg) {
        if (params.isEmpty() || params.size() < n + 1) {
            return;
        }
        String argName = params.get(n);
        Runtime.setLocalVar(argName, arg);
        ++numArgs;
    }

    public void setNilArgs() {
        for (int i = numArgs; i < numPar; ++i) {
            Runtime.setLocalVar(params.get(i), Runtime.nilConst());
        }
    }

    public LuaObj call() {
        setNilArgs();
        numArgs = 0;
        return func.call();
    }

    @Override
    public boolean toBoolean() {
        return true;
    }

    @Override
    public Double toDouble() {
        return null;
    }

    @Override
    public String toString() {
        if (this.params == null) {
            return String.format("Func%d (n par: %d)", this.id, this.numPar);
        }
        return String.format("Func%d (n par: %d)\n\tParams: %s",
                             this.id, this.numPar, this.params.toString());
    }

    @Override
    public int hashCode() {
        return String.format("Func%s", this.toString()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LuaFunction)) {
            return false;
        }
        LuaFunction oFunction = (LuaFunction)o;
        return this.id == oFunction.id;
    }
}
