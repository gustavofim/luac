package luaruntime;

import java.util.ArrayList;

public class LuaFunction implements LuaType {
    private int id;
    private LuaFunctionLiteral func;
    private int nPar;
    private ArrayList<String> params = new ArrayList<>();

    public LuaFunction(int id, LuaFunctionLiteral func, int nPar) {
        this.id = id;
        this.func = func;
        this.nPar = nPar;
    }

    public void setParam(String param) {
        params.add(param);
    }

    public String getParam(int id) {
        if (params.isEmpty() || params.size() < id + 1) {
            return null;
        }
        return params.get(id);
    }

    public void initArgs() {
        for (String par : params) {
            if (!Runtime.getLocalVar(par).toBoolean()) {
                Runtime.setLocalVar(par, Runtime.nilConst());
            }
        }
    }

    public LuaType call() {
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
            return String.format("Func%d (n par: %d)", this.id, this.nPar);
        }
        return String.format("Func%d (n par: %d)\n\tParams: %s",
                             this.id, this.nPar, this.params.toString());
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
