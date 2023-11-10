package luaruntime;

public class LuaBoolean implements LuaType {
    private boolean data;
    public LuaBoolean(boolean data) {
        this.data = data;
    }

    @Override
    public Double toDouble() {
        return null;
    }

    @Override
    public boolean toBoolean() {
        return this.data;
    }

    @Override
    public String toString() {
        if (this.data) {
            return "true";
        } else {
            return "false";
        }
    }
}
