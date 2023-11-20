package luaruntime;

public class LuaFunction implements LuaType {
    private int nArgs;
    // private String data;

    public LuaFunction(int nArgs) {
        // this.data = name;
        this.nArgs = nArgs;
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
        return String.format("function: nArgs=%d", this.nArgs);
    }
}
