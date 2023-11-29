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

    @Override
    public int hashCode() {
        return String.format("bool-%b", this.data).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LuaBoolean)) {
            return false;
        }
        LuaBoolean oBoolean = (LuaBoolean)o;
        return this.data == oBoolean.data;
    }
}
